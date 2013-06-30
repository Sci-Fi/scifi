/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package apcontroller;

import database.JDataManagement;
import data.JAPInfo;
import data.JRegion;
import timers.JAlgorithmTimer;
import timers.JAPDataCollectorTimer;
import timers.JConfigCheckTimer;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.concurrent.Semaphore;
import log.JLogger;
import org.apache.log4j.Logger;
import server.JServer;
import timers.JSendMailReportTimer;

/**
 * Classe principal que executa o controlador.
 * @author ferolim
 */
public class Main
{

    public final static String CONNECTION_LOG = "ConnectionLog";
    public final static String JAVA_LOG = "JAVALog";
   //public final static Integer REACHABLE = 1;
    
    // Este Mutex é usado para prevenir a execução simultânea de operações de coleta de dados de scan, station dump, algoritmo de seleção de canal, controle de potência e envio de relatorio de emails.
    // Somente uma operação por vez irá ocorrer. 
    public static Semaphore m_mutexGlobal = new Semaphore(1);
    private static Integer m_nRegion = -1;

    /**
     * Método que executa a coleta de dados e os algoritmos de seleção de canal e controle de potência e agenda as próximas execuções.
     * Inicializa o servidor que aguarda a chegada de informações assíncronas enviadas pelos pontos de acesso.
     * Cria os arquivos de log.
     * Estabelece a conexão com o banco de dados.
     * @param args Argumentos passados via linha de comando. Os parâmetros esperados são o  IP do controlador e porta do servidor do controlador.
     */
    public static void main(String[] args)
    {        
        // se recebe os quatro (ou três) argumentos requiridos e consegue criar os arquivos de log e conectar ao banco de dados,
        // os argumentos são (0)ID da REGIAO, (1)IP EXTERNO, (2)IP INTERNO e (3)PORTA base
        // apenas três arugmentos são utilizados quando o controlador utiliza apenas 1 IP
        if((args.length == 4 || args.length == 3))
        {    
            if(JDataManagement.initConnection(getDBPath()))                
            {
                m_nRegion = Integer.valueOf(args[0]);

                if(initLogger())
                {
                    //a porta é a porta base + identificador da região + 1

                    switch (args.length)
                    {
                     case 3:
                         initServers(args[1], Integer.valueOf(args[2]) + m_nRegion + 1);                     
                         break;

                     case 4:
                         initServers(args[1], args[2], Integer.valueOf(args[3]) + m_nRegion + 1);     
                         break;

                     default:
                         Logger.getLogger(Main.JAVA_LOG).error(JLogger.getDateTime() + " " + JLogger.getTime() + "Number of args do not correspond to default.");
                         break;                     
                    }


                    //Não se pode criar parâmetro no método firstRun, pois ele também é acessado pela JDataParserThread quando se faz o RESTART.
                    // IP da interface do controlador que se conecta aos APs e porta do servidor para esta região de controle
                    //IP_INTERNAL = args[2];
                    //N_PORT = Integer.valueOf(args[3]) + m_nRegion + 1;

                    // roda algoritmos e coleta de dados
                    firstRun();
                    //Agenda as próximas execuções dos algoritmos e coleta de dados.    
                    JSendMailReportTimer.start();
                    
                    JAPDataCollectorTimer.start();

                    JAlgorithmTimer.start();

                    JConfigCheckTimer.start();
                }
                // caso não tenha iniciado o logger com sucesso
                else
                {
                     Logger.getLogger(Main.JAVA_LOG).error(JLogger.getDateTime() + " " + JLogger.getTime() + " Logger failed to initialize properly.");
                }
            }
            // caso não tenha conseguido se conectar ao banco de dados        
            else
            {
                Logger.getLogger(Main.JAVA_LOG).error(JLogger.getDateTime() + " " + JLogger.getTime() + " Could not connect to database. Suggestion: Check DB Path in DBConfig.txt ");                
            }
        }
        // caso o número de argumentos esteja incorreto
        else
        {
            Logger.getLogger(Main.JAVA_LOG).error(JLogger.getDateTime() + " " + JLogger.getTime() + " Number of args do not correspond to default, which is 2 or 3.");            
        }
    }
    
    protected static void initServers(String strExternalIP, String strInternalIP, Integer nPort)
    {
        //Dois servidores são criados pois uma máquina pode ter duas interfaces de rede (configuração padrão do Controlador).
                
        // inicializa o servidor que aguarda a chegada de informações assíncronas enviadas pelos pontos de acesso ou comandos da
        // da interface web utilizando o IP externo
        Thread threadServerExternal = new Thread(new JServer(strExternalIP, nPort));
        threadServerExternal.start();
        
        // inicializa o servidor que aguarda a chegada de informações assíncronas enviadas pelos pontos de acesso ou comandos da
        // da interface web utilizando o IP interno
        Thread threadServerInternal = new Thread(new JServer(strInternalIP, nPort));
        threadServerInternal.start();
    }
    
    protected static void initServers(String strExternalIP, Integer nPort)
    {
        // Um servidore é criado pois uma máquina pode ter apenas uma interface de rede.
        // Este servidor poderá ser utilizado para receber mensagens da interface web e dos pontos de acesso da rede.
        
        // Inicializa o servidor que aguarda a chegada de informações assíncronas enviadas pelos pontos de acesso ou comandos da interface web.
        Thread threadServerExternal = new Thread(new JServer(strExternalIP, nPort));
        threadServerExternal.start();

    }
    
    
    /**
     * Método que inicializa os arquivos de log.
     * @return Retorna se os arquivos de log foram iniciados com sucesso.
     */
    protected static boolean initLogger()
    {
        JRegion region = JDataManagement.getRegionById(getRegionId());
        
        return JLogger.createLog(JAVA_LOG, "Java", region.getDescription()) && JLogger.createLog(CONNECTION_LOG, "Connection", region.getDescription());
    }

    /**
     * Método que roda os algoritmos e a coleta de dados pela primeira vez (sem delays), no momento da inicialização do controlador.
     */
    public static void firstRun()
    {
        try 
        {
            m_mutexGlobal.acquire();
            
            ArrayList<JAPInfo> listAP = JDataManagement.loadAPList();
            
            for(int nInd = 0; nInd < listAP.size(); nInd++)
            {
                listAP.get(nInd).setMaxPower();
                JDataManagement.clearScanInfo(listAP.get(nInd).getMAC());
            }
            
            JDataManagement.clearSTAInfo();
            JDataManagement.clearUnreachableCellInfos();

            JAPDataCollectorTimer.runScan();

            JAPDataCollectorTimer.runSTADump();

            JAlgorithmTimer.runChannelSelection();

            JAlgorithmTimer.runPowerControl();
            
            m_mutexGlobal.release();
        } 
        catch (InterruptedException ex)
        {
            Logger.getLogger(Main.JAVA_LOG).error(JLogger.getDateTime() + " " + JLogger.getTime() + " Error during first run: " + ex);
        }
    }
    
    /**
    * Este método busca no arquivo de configurações da conexão o caminho do banco de dados (IP, porta e o nome do banco).       
    */     
    protected static String getDBPath()
    {
        String strPath = "";
        
        try
        {
            Scanner scanner = new Scanner(new FileInputStream("DBConfig.txt"));

            strPath = scanner.nextLine();
        }
        catch (FileNotFoundException ex)
        {
            System.err.print("DB configuration file not found!");
        }
        catch (NoSuchElementException ex)
        {
            System.err.print("Empty DB configuration file!");
        }

        return strPath;
    }

    public static Integer getRegionId()
    {
        return m_nRegion;
    }
}
