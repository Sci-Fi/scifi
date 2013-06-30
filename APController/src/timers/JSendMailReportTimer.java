/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package timers;

import apcontroller.JTaskExecuter.JTask;
import apcontroller.Main;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import org.apache.log4j.Logger;
import log.JLogger;
import util.JMail;

/**
 *
 * @author controlador
 */
public class JSendMailReportTimer {
    
    /**
     * Classe que envia relatorio por email de APs incomunicante de tempos em tempos.
     * 
     * @author carlosmaciel
     */
    
    protected static Timer m_timerConfig;
        
    private static Date setSchedule() {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, 23);
        c.set(Calendar.MINUTE, 59);
        c.set(Calendar.SECOND, 0);

        Date time = c.getTime();
        
        return time;
    }
    
    /**
     * Inicia os temporizadores de coletas de configurações.
     */
    public static void start() 
    {
        m_timerConfig = new Timer();

        // inicia os timers do envio do relatorio
        JConfigSendMailTask taskConfigSendMail = new JConfigSendMailTask();
        taskConfigSendMail.setTimer(m_timerConfig);
        
        Logger.getLogger(Main.JAVA_LOG).info(JLogger.getDateTime() + " " + JLogger.getTime() + " Mail scheduled !. "); 
        
        m_timerConfig.schedule(taskConfigSendMail, setSchedule());
    }
   
     /**
     * Cancela os temporizadores.
     */
    public static void cancel()
    {        
        if(m_timerConfig != null)
        {
            m_timerConfig.cancel();
        }
    }
        
    /**
     * Classe que indica as operações que devem ser realizadas para confirmar o envio do relatorio
     */
    public static class JSendMailTask extends JTask
    {     
        public void run()
        {
            JMail.sendDailyReportMail();
        }
    }
    
    /**
    * Esta classe representa o conjunto de tarefas que serão agendadas pelo timer.
    */ 
    public static class JConfigSendMailTask extends JBaseTimerTask
    {
        public void run()
        {
            try
            {
                // Outra operação só ocorrerá quando esta se encerrar.
                //Main.m_mutexGlobal.acquire();
                 
                //insere no log que o envio de email começou e a data e hora
                Logger.getLogger(Main.JAVA_LOG).info(JLogger.getDateTime() + " " + JLogger.getTime() + " Send Mail started. ");                        

                //executando a rotina de envio de emails em todos os APs
                //JTaskExecuter.executeTasks(JSendMailTask.class.getName());
                JMail.sendDailyReportMail();

                // agenda a execução da nova coleta de configurações com o intervalo de tempo definido no banco de dados.
                if(m_timer != null)
                {                    
                    JConfigSendMailTask taskConfigSendMail = new JConfigSendMailTask();
                    taskConfigSendMail.setTimer(m_timer);
                    
                    Logger.getLogger(Main.JAVA_LOG).info(JLogger.getDateTime() + " " + JLogger.getTime() + " Mail scheduled !");
                    
                    int oneDay = 24*60*60*1000;
                    m_timer.schedule(taskConfigSendMail, oneDay);
                }
            }
            catch (IllegalStateException ex)
            {
                Logger.getLogger(Main.JAVA_LOG).info(JLogger.getDateTime() + " " + JLogger.getTime() + " Cannot schedule on a cancelled timer.");
            }
            finally
            {
                Logger.getLogger(Main.JAVA_LOG).info(JLogger.getDateTime() + " " + JLogger.getTime() + " Send Mail ended. ");                        
                
                // libera a execução de outras operações no ponto de acesso.
                //Main.m_mutexGlobal.release();                
            }
        }
    }
}