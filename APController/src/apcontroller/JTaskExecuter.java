/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package apcontroller;

import database.JDataManagement;
import data.JAPInfo;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import log.JLogger;
import org.apache.log4j.Logger;

/**
 * Classe que executa uma tarefa em todos os APs do sistema
 * @author ferolim
 */
public abstract class JTaskExecuter
{
    /**
     * Classe base para as tarefas que serão executadas
     */
    public static abstract class JTask implements Runnable
    {
        protected JAPInfo m_apInfo;
        
        /**
         * Define o AP sob o qual a tarefa será executada
         * @param apInfo AP que será utilizado nesta tarefa
         */
        void setAP(JAPInfo apInfo)
        {
            m_apInfo = apInfo;
        }
    }
    
    /**
     * Executada determinada tarefa para todos os APs do sistema
     * @param strClassName Nome da classe da tarefa, cujo pai deve ser a classe JTask
     */
    public static void executeTasks(String strClassName)
    {
        try 
        {
            ArrayList<JAPInfo> listAP = JDataManagement.loadAPList();

            ExecutorService taskExecutor = Executors.newFixedThreadPool(listAP.size());

            //carregando a classe cujo nome foi especificado como parâmetro
            Class loadedClass = ClassLoader.getSystemClassLoader().loadClass(strClassName);
            
            JTask task;
            
            for(int nInd = 0; nInd < listAP.size(); nInd++)
            {
                Object obj = loadedClass.newInstance();
                
                task = (JTask) obj;
                
                //definindo o AP da tarefa
                task.setAP(listAP.get(nInd));
                
                taskExecutor.execute(task);
            }
            
            taskExecutor.shutdown();

            //esperando a execução de todas as tarefas
            taskExecutor.awaitTermination(60, TimeUnit.SECONDS);
        } 
        catch (InterruptedException ex)
        {
            Logger.getLogger(Main.JAVA_LOG).error(JLogger.getDateTime() + " " + JLogger.getTime() + " Could execute tasks: " + ex);
        }
        catch (ClassNotFoundException ex)
        {
            Logger.getLogger(Main.JAVA_LOG).error(JLogger.getDateTime() + " " + JLogger.getTime() + " Could locate class: " + ex);
        }
        catch (InstantiationException ex)
        {
            Logger.getLogger(Main.JAVA_LOG).error(JLogger.getDateTime() + " " + JLogger.getTime() + " Could create object for class: " + ex);
        }
        catch (IllegalAccessException ex)
        {
            Logger.getLogger(Main.JAVA_LOG).error(JLogger.getDateTime() + " " + JLogger.getTime() + " Could access object for class: " + ex);
        }
        catch (IllegalArgumentException ex)
        {
            Logger.getLogger(Main.JAVA_LOG).error(JLogger.getDateTime() + " " + JLogger.getTime() + " There is no AP in this region.");
        }
    }
}
