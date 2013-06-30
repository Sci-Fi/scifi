/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package timers;

import apcontroller.JCommander;
import database.JDataManagement;
import apcontroller.JTaskExecuter;
import apcontroller.JTaskExecuter.JTask;
import apcontroller.Main;
import java.util.Timer;
import log.JLogger;
import org.apache.log4j.Logger;

/**
 * Classe que analisa de tempos em tempos as configurações dos pontos de acesso e
 * as compara com as do banco de dados. Caso estejam diferentes, as configurações dos
 * pontos de acesso são alteradas para aquelas contidas no banco de dados.
 * 
 * @author ferolim
 */
public class JConfigCheckTimer
{
    protected static Timer m_timerConfig;
    
    /**
     * Inicia os temporizadores de coletas de configurações.
     */
    public static void start() 
    {
        m_timerConfig = new Timer();

        // busca no banco de dados o valor do intervalo de tempo para a realização do análise de configuração
        String strTemp = JDataManagement.getPropertyValue("ConfigCheckInterval");
        int nSecondsConfigCheck = Integer.valueOf(strTemp);

        // inicia os timers do scan e do station dump de forma que sejam executados nos intervalos definidos.
        JConfigCheckTask taskConfigCheck = new JConfigCheckTask();
        taskConfigCheck.setTimer(m_timerConfig);
        
        m_timerConfig.schedule(taskConfigCheck, nSecondsConfigCheck * 1000);
    }
   
     /**
     * Cancela os temporizadores de coleta de configurações, incluindo todas os
     * agendamentos já realizados.
     */
    public static void cancel()
    {        
        if(m_timerConfig != null)
        {
            m_timerConfig.cancel();
        }
    }
    /**
    * Executa a coleta de configurações (faz o agendamento da execução com delay 0).
    */    
    public static void runConfigCheck()
    {
        Timer timerTemp = new Timer();

        timerTemp.schedule(new JConfigCheckTask(), 0);
    }
    
    /**
     * Classe que indica as operações que devem ser realizadas para confirmar as configuraçoes do AP
     */
    public static class JCheckConfigTask extends JTask
    {     
        public void run()
        {
            Integer nChannel = JCommander.getChannel(m_apInfo);
            Integer nPower = JCommander.getPower(m_apInfo);

            if(nChannel != null && nChannel != m_apInfo.getChannel())
            {
                Logger.getLogger(Main.CONNECTION_LOG).error(JLogger.getDateTime() + " " + JLogger.getTime() + " Different channel configuration on " + m_apInfo.getIP());
                JCommander.setChannel(m_apInfo, m_apInfo.getChannel());
            }

            if(nPower != null && nPower != m_apInfo.getPower())
            {
                Logger.getLogger(Main.CONNECTION_LOG).error(JLogger.getDateTime() + " " + JLogger.getTime() +" Different power configuration on " + m_apInfo.getIP());
                JCommander.setPower(m_apInfo, m_apInfo.getPower());
            }
        }
    }
    
    /**
    * Esta classe representa o conjunto de tarefas que serão agendadas pelo timer da coleta de configurações.
    */ 
    public static class JConfigCheckTask extends JBaseTimerTask
    {
        public void run()
        {
            try
            {
                // Checa se outras operações de coleta de dados ou execução de algoritmos está ocorrendo no ponto de acesso.
                // Outra operação só ocorrerá quando esta se encerrar.
                Main.m_mutexGlobal.acquire();
                 
                //insere no log que o check de configuração começou e a data e hora
                Logger.getLogger(Main.JAVA_LOG).info(JLogger.getDateTime() + " " + JLogger.getTime() + " Config check started. ");                        

                //executando o config check em todos os APs
                JTaskExecuter.executeTasks(JCheckConfigTask.class.getName());

                // busca no banco de dados o valor do intervalo de tempo de execução da coleta de configurações.
                String strTemp = JDataManagement.getPropertyValue("ConfigCheckInterval");
                int nSecondsConfigCheck = Integer.valueOf(strTemp);
                // agenda a execução da nova coleta de configurações com o intervalo de tempo definido no banco de dados.
                if(m_timer != null)
                {                    
                    JConfigCheckTask taskConfigCheck = new JConfigCheckTask();
                    taskConfigCheck.setTimer(m_timer);
                    
                    m_timer.schedule(taskConfigCheck, nSecondsConfigCheck * 1000);
                }
            }
            catch (InterruptedException ex)
            {
                Logger.getLogger(Main.JAVA_LOG).error(JLogger.getDateTime() + " " + JLogger.getTime() + " Error during the configuration check: " + ex);
            }
            catch (IllegalStateException ex)
            {
                Logger.getLogger(Main.JAVA_LOG).info(JLogger.getDateTime() + " " + JLogger.getTime() + " Cannot schedule on a cancelled timer.");
            }
            finally
            {
                //insere no log que o check de configuração terminou e a data e hora
                Logger.getLogger(Main.JAVA_LOG).info(JLogger.getDateTime() + " " + JLogger.getTime() + " Config check ended. ");                        
                
                // libera a execução de outras operações no ponto de acesso.
                Main.m_mutexGlobal.release();                
            }
        }
    }
}
