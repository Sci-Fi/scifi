/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package timers;

import apcontroller.JTaskExecuter;
import apcontroller.JTaskExecuter.JTask;
import database.JDataManagement;
import apcontroller.Main;
import channelSelection.JChannelSelection;
import java.util.HashMap;
import java.util.Timer;
import log.JLogger;
import org.apache.log4j.Logger;
import transmissionPowerControl.JTransmissionPowerControl;

/**
 * Classe que realiza o agendamento da execução dos algoritmos de alocação de canal e controle de potência dos pontos de acesso.
 * Os timers definidos aqui executam os algoritmos nos intervalos de tempo determinados pelo administrador da rede através da interface gráfica.
 * @author ferolim
 */
public class JAlgorithmTimer
{
    protected static Timer m_timerChannel;
    protected static Timer m_timerPower;
    
    protected static HashMap<String, Integer> m_channelSelectionResult;
    protected static HashMap<String, Integer> m_powerControlResult;

    /**
     * Inicia os temporizadores de execução dos algoritmos.
     */
    public static void start()
    {        
        m_timerChannel = new Timer();

        m_timerPower = new Timer();
        // busca no banco de dados o valor do intervalo de tempo para a realização da alocação de canal.
        String strTemp = JDataManagement.getPropertyValue("ChannelSelectionInterval");
        int nChannelSeconds = Integer.valueOf(strTemp);
        // busca no banco de dados o valor do intervalo de tempo para a realização do controle de potência.
        strTemp = JDataManagement.getPropertyValue("PowerControlInterval");
        int nPowerSeconds = Integer.valueOf(strTemp);
        // inicia os timers da alocação de canal e controle de potência de forma que os algoritmos sejam executados nos intervalos definidos.
        JChannelTask taskChannel = new JChannelTask();
        taskChannel.setTimer(m_timerChannel);
        
        m_timerChannel.schedule(taskChannel, nChannelSeconds*1000);
        
        JPowerTask taskPower = new JPowerTask();
        taskPower.setTimer(m_timerPower);

        m_timerPower.schedule(taskPower, nPowerSeconds*1000);
    }
    
    /**
     * Cancela os temporizadores de execução dos algoritmos, incluindo todas os
     * agendamentos já realizados.
     */
    public static void cancel()
    {
        if(m_timerChannel != null)
        {
            m_timerChannel.cancel();
        }
        
        if(m_timerPower != null)
        {
            m_timerPower.cancel();
        }
    }
    
    /**
    * Executa o algoritmo de seleção de canal (faz o agendamento da execução com delay 0).
    */    
    public static void runChannelSelection()
    {
        Timer timerTemp = new Timer();

        timerTemp.schedule(new JChannelTask(), 0);
    }
    /**
    * Executa o algoritmo de controle de potência (faz o agendamento da execução com delay 0).
    */ 
    public static void runPowerControl()
    {
        Timer timerTemp = new Timer();

        timerTemp.schedule(new JPowerTask(), 0);
    }
    
     /**
     * Classe que indica a operação que define o novo canal de operação do AP, baseado
     * no resultado do algoritmo de seleção de canal
     */
    public static class JSetChannelTask extends JTask
    {        
        public void run()
        {
            Integer nNewChannel = m_channelSelectionResult.get(m_apInfo.getMAC());
            
            m_apInfo.setChannel(nNewChannel);
        }
    }

    /**
    * Esta classe representa o conjunto de tarefas que serão agendadas pelo timer de seleção de canal para execução.
    */     
    public static class JChannelTask extends JBaseTimerTask
    {
        public void run()
        {
            try
            {
                // Checa se outras operações de coleta de dados ou execução de algoritmos está ocorrendo no ponto de acesso.
                // Outra operação só ocorrerá quando esta se encerrar.
                Main.m_mutexGlobal.acquire();
                
                //insere no log que a seleção de canais começou e a data e hora
                Logger.getLogger(Main.JAVA_LOG).info(JLogger.getDateTime() + " " + JLogger.getTime() + " Channel Allocation Algorithm started.");                        
                
                // executa o algoritmo de alocação de canal.
                m_channelSelectionResult = JChannelSelection.runChannelSelection();
                
                //definindo os canais dos APs baseado no resultado do algoritmo
                JTaskExecuter.executeTasks(JSetChannelTask.class.getName());
                
                // busca no banco de dados o valor do intervalo de tempo de execução do algoritmo de seleção de canal.
                String strTemp = JDataManagement.getPropertyValue("ChannelSelectionInterval");
                int nChannelSeconds = Integer.valueOf(strTemp);
                // agenda a execução da nova seleção de canal com o intervalo de tempo definido no banco de dados.
                if(m_timer != null)
                {
                    JChannelTask taskChannel = new JChannelTask();
                    taskChannel.setTimer(m_timer);
                    
                    m_timer.schedule(taskChannel, nChannelSeconds*1000);
                }
            }
            catch (InterruptedException ex)
            {
                Logger.getLogger(Main.JAVA_LOG).error(JLogger.getDateTime() + " " + JLogger.getTime() + " Erro during the execution of the Channel Selection Algorithm: " + ex);
            }
            catch (IllegalStateException ex)
            {
                Logger.getLogger(Main.JAVA_LOG).info(JLogger.getDateTime() + " " + JLogger.getTime() + " Cannot schedule on a cancelled timer.");
            }
            finally
            {
                //insere no log que o algoritmo de seleção de canais terminou e a data e hora.
                Logger.getLogger(Main.JAVA_LOG).info(JLogger.getDateTime() + " " + JLogger.getTime() + " Channel Allocation Algorithm ended."); 
                
                // libera a execução de outras operações no ponto de acesso.
                Main.m_mutexGlobal.release();                
            }           
        }
    }
    
    /**
     * Classe que indica a operação que define o novo canal de operação do AP, baseado
     * no resultado do algoritmo de seleção de canal
     */
    public static class JSetPowerTask extends JTask
    {        
        public void run()
        {
            Integer nOption = m_powerControlResult.get(m_apInfo.getMAC());
            
            switch(nOption)
            {
                case -1:     m_apInfo.decPower(); break;
                case  0:     m_apInfo.setMaxPower(); break;
                case  1:     m_apInfo.incPower(); break;
            }
        }
    }

    /**
    * Esta classe representa o conjunto de tarefas que serão agendadas pelo timer do controle de potência para execução.
    */     
    public static class JPowerTask extends JBaseTimerTask
    {
        public void run()
        {
            try
            {
                // Checa se outras operações de coleta de dados ou execução de algoritmos está ocorrendo no ponto de acesso.
                // Outra operação só ocorrerá quando esta se encerrar.
                Main.m_mutexGlobal.acquire();
                
                //insere no log que o controle de potência começou e a data e hora
                Logger.getLogger(Main.JAVA_LOG).info(JLogger.getDateTime() + " " + JLogger.getTime() + " Power Control Algorithm started.");                        
                
                // executa o algoritmo de controle de potência.
                m_powerControlResult = JTransmissionPowerControl.runTransmissionPowerControl();
                
                //definindo as potências dos APs baseado no resultado do algoritmo
                JTaskExecuter.executeTasks(JSetPowerTask.class.getName());
                
                // busca no banco de dados o valor do intervalo de tempo de execução do algoritmo de controle de potência.
                String strTemp = JDataManagement.getPropertyValue("PowerControlInterval");
                int nPowerSeconds = Integer.valueOf(strTemp);
                // agenda a execução do novo controle de potência com o intervalo de tempo definido no banco de dados.
                if(m_timer != null)
                {
                    JPowerTask taskPower = new JPowerTask();
                    taskPower.setTimer(m_timer);
                    
                    m_timer.schedule(taskPower, nPowerSeconds*1000);
                }
            }
            catch (InterruptedException ex)
            {
                Logger.getLogger(Main.JAVA_LOG).error(JLogger.getDateTime() + " " + JLogger.getTime() + " Erro during the execution of the Power Selection Algorithm: " + ex);
            }
            catch (IllegalStateException ex)
            {
                Logger.getLogger(Main.JAVA_LOG).info(JLogger.getDateTime() + " " + JLogger.getTime() + " Cannot schedule on a cancelled timer.");
            }
            finally
            {
                //insere no log que o controle de potência terminou e a data e hora
                Logger.getLogger(Main.JAVA_LOG).info(JLogger.getDateTime() + " " + JLogger.getTime() + " Power Control Algorithm ended.");                        
                
                // libera a execução de outras operações no ponto de acesso.
                Main.m_mutexGlobal.release();                
            }           
        }
    }
}
