/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package timers;

import data.JAPInfo;
import apcontroller.JCommander;
import database.JDataManagement;
import apcontroller.JTaskExecuter;
import apcontroller.JTaskExecuter.JTask;
import apcontroller.Main;
import java.util.ArrayList;
import java.util.Timer;
import loadBalance.JLoadBalance;
import log.JLogger;
import org.apache.log4j.Logger;

/**
 * Classe que realiza o agendamento da execução da coleta de dados (scan e station dump) nos pontos de acesso.
 * Os timers definidos aqui executam as tarefas de scan e station dump nos intervalos de tempo determinados pelo administrador da rede através da interface gráfica.
 * @author ferolim
 */
public class JAPDataCollectorTimer
{

    protected static Timer m_timerScan;
    protected static Timer m_timerSTA;
    
    /**
     * Inicia os temporizadores de coletas de dados.
     */
    public static void start() 
    {
        m_timerScan = new Timer();

        m_timerSTA = new Timer();

        // busca no banco de dados o valor do intervalo de tempo para a realização do scan
        String strTemp = JDataManagement.getPropertyValue("ScanInterval");
        int nSecondsScan = Integer.valueOf(strTemp);
        // busca no banco de dados o valor do intervalo de tempo para a realização do station dump
        strTemp = JDataManagement.getPropertyValue("STADumpInterval");
        int nSecondsSTA = Integer.valueOf(strTemp);

        // inicia os timers do scan e do station dump de forma que sejam executados nos intervalos definidos.
        JScanRequestTask taskScan = new JScanRequestTask();
        taskScan.setTimer(m_timerScan);
        
        m_timerScan.schedule(taskScan, nSecondsScan * 1000);
        
        JSTARequestTask taskSTA = new JSTARequestTask();
        taskSTA.setTimer(m_timerSTA);

        m_timerSTA.schedule(taskSTA, nSecondsSTA * 1000);
    }
   
     /**
     * Cancela os temporizadores de coleta de dados, incluindo todas os
     * agendamentos já realizados.
     */
    public static void cancel()
    {
        if(m_timerScan != null)
        {
            m_timerScan.cancel();
        }
        
        if(m_timerSTA != null)
        {
            m_timerSTA.cancel();
        }
    }
    /**
    * Executa o scan (faz o agendamento da execução com delay 0).
    */    
    public static void runScan()
    {
        Timer timerTemp = new Timer();

        timerTemp.schedule(new JScanRequestTask(), 0);
    }
    /**
    * Executa o station dump (faz o agendamento da execução com delay 0).
    */ 
    public static void runSTADump()
    {
        Timer timerTemp = new Timer();

        timerTemp.schedule(new JSTARequestTask(), 0);
    }
    
    /**
    * Esta classe representa o conjunto de tarefas que serão agendadas pelo timer do scan para execução.
    */ 
    public static class JScanRequestTask extends JBaseTimerTask
    {
        public void run()
        {
            //variável que guarda o número de APs (será utilizado para informar log)
            int listAPsSize;
            listAPsSize = 0;
            
            try
            {
                // Checa se outras operações de coleta de dados ou execução de algoritmos está ocorrendo no ponto de acesso.
                // Outra operação só ocorrerá quando esta se encerrar.
                Main.m_mutexGlobal.acquire();
                
                // busca no banco de dados a lista atual de APs da rede controlada.
                ArrayList<JAPInfo> listAPInfo = JDataManagement.loadAPList();
                
                //preencher o número de APs que será informado no log
                listAPsSize = listAPInfo.size();
                
                //insere no log que o scan começou e a data e hora
                Logger.getLogger(Main.JAVA_LOG).info(JLogger.getDateTime() + " " + JLogger.getTime() + " Scan started for " +listAPsSize +" APs.");                        

                
                for (int nInd = 0; nInd < listAPInfo.size(); nInd++)
                {
                    JAPInfo apInfo = listAPInfo.get(nInd);
                    // executa o scan no ponto de acesso.
                    JCommander.scan(apInfo);
                }
                
                // retira do banco de dados as CellInfos que não são escutadas por nenhum AP.
                JDataManagement.clearUnreachableCellInfos();
                
                // busca no banco de dados o valor do intervalo de tempo de execução do scan.
                String strTemp = JDataManagement.getPropertyValue("ScanInterval");
                int nSecondsScan = Integer.valueOf(strTemp);
                // agenda a execução do novo scan com o intervalo de tempo definido no banco de dados.
                if(m_timer != null)
                {                    
                    JScanRequestTask taskScan = new JScanRequestTask();
                    taskScan.setTimer(m_timer);
                    
                    m_timer.schedule(taskScan, nSecondsScan * 1000);
                }
            }
            catch (InterruptedException ex)
            {
                Logger.getLogger(Main.JAVA_LOG).error(JLogger.getDateTime() + " " + JLogger.getTime() + " Erro during the scan data collection: " + ex);
            }
            catch (IllegalStateException ex)
            {
                Logger.getLogger(Main.JAVA_LOG).info(JLogger.getDateTime() + " " + JLogger.getTime() + " Cannot schedule on a cancelled timer.");
            }
            finally
            {
                //insere no log que o scan terminou e a data e hora
                Logger.getLogger(Main.JAVA_LOG).info(JLogger.getDateTime() + " " + JLogger.getTime() + " Scan ended for " +listAPsSize +" APs.");                        
                
                // libera a execução de outras operações no ponto de acesso.
                Main.m_mutexGlobal.release();                
            }
        }
    }

    /**
     * Classe que indica a operação que deve ser realizada durante o processo de obter dados do usuário
     */
    public static class JStationDumpTask extends JTask
    {        
        public void run()
        {
            JCommander.stationDump(m_apInfo);
        }
    }
    
    /**
    * Esta classe representa o conjunto de tarefas que serão agendadas pelo timer do station dump para execução.
    */    
    public static class JSTARequestTask extends JBaseTimerTask
    {
        public void run()
        {
            try
            {
                // Checa se outras operações de coleta de dados ou execução de algoritmos está ocorrendo no ponto de acesso.
                // Outra operação só ocorrerá quando esta se encerrar.
                Main.m_mutexGlobal.acquire();
                
                //insere no log que o station dump começou e a data e hora
                Logger.getLogger(Main.JAVA_LOG).info(JLogger.getDateTime() + " " + JLogger.getTime() + " Station Dump started.");                        
                
                //executando o station dump em todos os APs
                JTaskExecuter.executeTasks(JStationDumpTask.class.getName());
                
                JLoadBalance.updateLoadStatus();
                
                // busca no banco de dados o valor do intervalo de tempo de execução do scan.
                String strTemp = JDataManagement.getPropertyValue("STADumpInterval");
                int nSecondsSTA = Integer.valueOf(strTemp);
                // agenda a execução do novo station dump com o intervalo de tempo definido no banco de dados.
                if(m_timer != null)
                {
                    JSTARequestTask taskSTA = new JSTARequestTask();
                    taskSTA.setTimer(m_timer);
                    
                    m_timer.schedule(taskSTA, nSecondsSTA * 1000);
                }
                
                
            }
            catch (IllegalStateException ex)
            {
                Logger.getLogger(Main.JAVA_LOG).info(JLogger.getDateTime() + " " + JLogger.getTime() + " Cannot schedule on a cancelled timer.");
            }
            catch (InterruptedException ex)
            {
                Logger.getLogger(Main.JAVA_LOG).error(JLogger.getDateTime() + " " + JLogger.getTime() + " Erro during the station data collection: " + ex);
            }
            finally
            {
                //insere no log que o station dump terminou e a data e hora
                Logger.getLogger(Main.JAVA_LOG).info(JLogger.getDateTime() + " " + JLogger.getTime() + " Station Dump ended.");                        
                
                // libera a execução de outras operações no ponto de acesso.
                Main.m_mutexGlobal.release();                
            }
        }
    }
}
