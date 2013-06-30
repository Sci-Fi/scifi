/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package connection;

import apcontroller.Main;
import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.Session;
import log.JLogger;
import org.apache.log4j.Logger;

/**
 * Classe base para as classes de threads. Possui funções que informam o status e realizam a limpeza das threads.
 * @author ferolim
 */

public class JBaseThread extends Thread
{
    protected boolean m_bFinished;
    protected Session m_session;
    protected Connection m_connection;
    public static final int TIMEOUT = 30;
    protected boolean m_bResult;

    /**
     * Indica se a thread foi ou não finalizada.
     * @param bFinished True se a thread foi finalizada, false caso contrário.
     */
    protected void setFinished(boolean bFinished)
    {
        m_bFinished = bFinished;
    }
    /**
     * Função que indica se a thread foi ou não finalizada.
     * @return Retorna true se a thread foi finalizada e false, caso contrário.
     */
    public boolean isFinished()
    {
        return m_bFinished;
    }
    /**
     * Função que inicia a JCleanUpThread.
     */
    public void cleanUp()
    {
        JCleanUpThread threadCleanUp  = new JCleanUpThread();

        threadCleanUp.start();
        // aguardar que a thread termine dentro do tempo delimitado.
        try
        {
            threadCleanUp.join(TIMEOUT*1000);
        }
        catch (InterruptedException ex)
        {
            Logger.getLogger(Main.JAVA_LOG).error(JLogger.getDateTime() + " " + JLogger.getTime() + " " + ex);
        }
        // se a thread não terminou, avisar no log.
        if(threadCleanUp.isAlive())
        {
            Logger.getLogger(Main.CONNECTION_LOG).warn(JLogger.getDateTime() + " " + JLogger.getTime() + " Could not execute Clean Up. Thread still running.");
        }
    }

    /**
     * Thread que tenta fechar a sessão e a conexão de comunicação com o ponto de acesso.
     * Uma thread de execução de comando ou SCP pode não ter terminado por não ter conseguido finalizar a conexão com o AP.
     * A JCleanUpThread tenta fazer com que a thread com problemas seja finalizada.
     */    
    public class JCleanUpThread extends Thread
    {
        @Override
        public void run()
        {
            if(m_session != null)
            {
                m_session.close();
            }

            if(m_connection != null)
            {
                m_connection.close();
            }
        }
    }
}
