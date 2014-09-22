/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package connection;

import apcontroller.Main;
import data.JAPInfo;
import database.JDataManagement;
import java.io.File;
import log.JLogger;
import org.apache.log4j.Logger;
import util.JMail;

/**
 * Esta classe é responsável pela conexão entre o controlador e os pontos de acesso, execução de comandos e cópia de arquivo via SCP.
 * @author Felipe Rolim
 * updated by schara
 */

public class JRouterConnection
{
    public static final String USERNAME = "root";
    public static final int TIMEOUT = 5;
    public static final int NUMBER_OF_RETRIES = 1;
    public static final int RETRY_INTERVAL = 5;

   /**
    * Método que copia um arquivo de um determinado host para o controlador.
    *
    @param apInfo  Objeto que representa o ponto de acesso.
    *
    @param strHostFile Localização do arquivo que será copiado no host.
     * 
    @param fileLocal Aqruivo local onde o dados serão gravados.
    * 
   @return Retorna true se a operação foi realizada com sucesso ou false, caso contrário.
    */         
    public static boolean scpFrom(JAPInfo apInfo, String strHostFile, File fileLocal)
    {
        return scpFrom(apInfo, strHostFile, fileLocal, 1);
    }

   /**
    * Método que copia um arquivo de um determinado host para o controlador, considerando o número de tentativas de cópia do arquivo realizadas.
    * Se houve erro no SCP, outra tentativa é realizada, até que o número de tentativas realizadas alcance o valor de NUMBER_OF_RETRIES.
    * 
    @param apInfo  Objeto que representa o ponto de acesso.
    *
    @param strHostFile Localização do arquivo que será copiado no host.
     * 
     @param fileLocal Aqruivo local onde o dados serão gravados.
    *
    @param nCurrentTry Número da tentativa atual de copia do arquivo.
    * 
   @return Retorna true se a operação foi realizada com sucesso ou false, caso contrário.
    */     
    protected static boolean scpFrom(JAPInfo apInfo, String strHostFile, File fileLocal, int nCurrentTry)
    {
        String strHost = apInfo.getIP();
        InetAddress addressAp = InetAddress.getByName(strHost);
        // se o número da tentativa atual é maior do que o máximo de tentativas permitido,
        // e o ap está respondendo ping -- schara
        if((nCurrentTry > NUMBER_OF_RETRIES) && (addressAp.isReachable(1000)))
        {
            // Insere no banco de dados que o AP está sem conexão (Reachable = 0)
            JDataManagement.addReachableInfoToDB(apInfo.getMAC(), 0);
            return false;
        }
        // caso contrário,
        else
        {
            // inicia uma thread para a execução do SCP.
            JSCPThread threadExec  = new JSCPThread(strHost, strHostFile, fileLocal);

            threadExec.start();
            // aguardar para que o SCP seja executado no tempo determinado por TIMEOUT*1000 [ms]
            try
            {
                threadExec.join(TIMEOUT*1000);
            }
            catch (InterruptedException ex)
            {
                Logger.getLogger(Main.JAVA_LOG).error(JLogger.getDateTime() + " " + JLogger.getTime() + " " + ex);
            }
            // Se o SCP não terminou no tempo esperado,
            if(!threadExec.isFinished())
            {
                // interromper a execução
                threadExec.interrupt();
                // limpar a thread
                threadExec.cleanUp();
                // guardar no Log de conexão que houve erro ao tentar copiar o arquivo.
            }
            //  Se o resultado do SCP é inválido (se houve erro durante a execução)
            if(!threadExec.getResult())
            {
                // Aguarda RETRY_INTERVAL*1000 [ms],
                try
                {
                    Thread.sleep(RETRY_INTERVAL * 1000);
                }
                catch (InterruptedException ex)
                {
                    Logger.getLogger(Main.JAVA_LOG).error(JLogger.getDateTime() + " " + JLogger.getTime() + " " + ex);
                }
                // e executa o SCP novamente com o número da tentativa atual incrementado.
                return scpFrom(apInfo, strHostFile, fileLocal, ++nCurrentTry);
            }
            // se o resultado do SCP é válido (não ocorreu erro durante a transação)
            else
            {
                // insere no banco de dados a informação de que o controlador consegue se conectar ao AP (Reachable = 1)
                JDataManagement.addReachableInfoToDB(apInfo.getMAC(), 1);
                return threadExec.getResult();
            }
        }
    }

   /**
    * Método que executa um comando em um determinado host.
    *
    @param strCommand Comando a ser executado.
    * 
    @param apInfo  Objeto que representa o ponto de acesso.
    *    
   @return Retorna true se a operação foi realizada com sucesso ou false, caso contrário.
    */    
    public static boolean execCommand(String strCommand, JAPInfo apInfo)
    {
        return execCommand(strCommand, apInfo, 1);
    }

   /**
    * Método que executa um comando em um determinado host, considerando o número de tentativas.
    * Se houve erro na execução, outra tentativa é realizada, até que o número de tentativas realizadas alcance o valor de NUMBER_OF_RETRIES.
    *
    @param strCommand Comando a ser executado.
    * 
    @param apInfo  Objeto que representa o ponto de acesso.
    *    
    @param nCurrentTry Número da tentativa atual de execução do comando.
    * 
   @return Retorna true se a operação foi realizada com sucesso ou false, caso contrário.
    */    
    protected static boolean execCommand(String strCommand, JAPInfo apInfo, int nCurrentTry)
    {
        String strHost = apInfo.getIP();
        InetAddress addressAp = InetAddress.getByName(strHost);
        // Se o número da tentativa de execução do comando atual é maior do que o limite máximo,
        // e o AP está respondendo ping --schara
        if((nCurrentTry > NUMBER_OF_RETRIES) && (addressAp.isReachable(1000)))
        {
            // Insere no banco de dados que o AP está sem conexão (Reachable = 0)
            JDataManagement.addReachableInfoToDB(apInfo.getMAC(), 0);
            
            JMail.sendUnreachableMail(apInfo);
            
            return false;
        }
        else
        {
            JExecCommandThread threadExec  = new JExecCommandThread(strCommand, strHost);
            
            threadExec.start();
            // aguarda TIMEOUT*1000 [ms]
            try
            {
                threadExec.join(TIMEOUT*1000);
            }
            catch (InterruptedException ex)
            {
                Logger.getLogger(Main.JAVA_LOG).error(JLogger.getDateTime() + " " + JLogger.getTime() + " " + ex);
            }
            // Se a execução da thread não terminou no tempo desejado,
            if(!threadExec.isFinished())
            {
                //interrompe e limpa a thread.
                threadExec.interrupt();
                threadExec.cleanUp();             
            }
            // se o resultado da execução é inválido,
            if(!threadExec.getResult())
            {
                // Aguarda RETRY_INTERVAL*1000 [ms],
                try
                {
                    Thread.sleep(RETRY_INTERVAL * 1000);
                }
                catch (InterruptedException ex)
                {
                    Logger.getLogger(Main.JAVA_LOG).error(JLogger.getDateTime() + " " + JLogger.getTime() + " " + ex);
                }
                // e executa novamente o comando com o número da execução atual incrementado.
                return execCommand(strCommand, apInfo, ++nCurrentTry);
            }
            // se o resultado da execução do comando foi válido (se não houve erro na execução),
            else
            {
                // insere no banco de dados a informação de que o controlador consegue se conectar ao AP (Reachable = 1)
                if(!JDataManagement.addReachableInfoToDB(apInfo.getMAC(), 1))
                {
                    return false;
                }
                return true;
            }
        }
    }
}
