/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package connection;

import apcontroller.Main;
import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.StreamGobbler;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import log.JLogger;
import org.apache.log4j.Logger;

/**
 * Esta classe é uma Thread para execução de comandos em um host.
 * @author ferolim
 */
public class JExecCommandThread extends JBaseThread
{
    protected String m_strCommand;
    protected String m_strHost;

    public JExecCommandThread(String strCommand, String strHost)
    {
        m_strCommand = strCommand;
        m_strHost = strHost;
    }

    /**
     * Este método realiza a conexão com o host e executa o comando, verificando se houve erro durante o processo.
     */    
    @Override
    public void run()
    {
        try
        {
            m_bResult = false;

            setFinished(false);
            
            /* Cria uma instância para a conexão*/

            m_connection = new Connection(m_strHost);

            /* Executa a conexão */

            m_connection.connect();           

            /* Authenticate */
            File fileKey = new File("controller_key");
            
            boolean isAuthenticated = m_connection.authenticateWithPublicKey(JRouterConnection.USERNAME, fileKey, null);
            
            if (isAuthenticated == false)
            {
                throw new IOException("Authentication failed.");
            }

            // Cria uma sessão
            m_session = m_connection.openSession();
             // Executa o comando
            m_session.execCommand(m_strCommand);

            InputStream stdout = new StreamGobbler(m_session.getStdout());
            InputStream stderr = new StreamGobbler(m_session.getStderr());

            BufferedReader stdoutReader = new BufferedReader(new InputStreamReader(stdout));
            BufferedReader stderrReader = new BufferedReader(new InputStreamReader(stderr));

            while (true)
            {
                String line = stdoutReader.readLine();
                if (line == null)
                {
                    break;
                }
            }

            while (true)
            {
                String line = stderrReader.readLine();
                if (line == null)
                {
                    break;
                }
            }

            Integer nExitStatus = m_session.getExitStatus();

            if (nExitStatus == null)
            {
                nExitStatus = 0;
            }

            Logger.getLogger(Main.CONNECTION_LOG).info(JLogger.getDateTime() + " " + JLogger.getTime() + " Connecting to (EXEC): " + m_strHost + " - Command: " + m_strCommand + " Exit status: " + nExitStatus);

            // Fecha a sessão
            m_session.close();
            // Fecha a conexão
            m_connection.close();
            // define o resultado como válido
            m_bResult = true;
            // indica que a thread foi finalizada
            setFinished(true);
        }
        catch (Exception ex)
        {
            // Uma falha na execução do bloco try causando o termino da thread
            // indica que houve algum erro durante a execução do comando. Neste caso, o m_bResult é falso.
            
            // informa que a thread terminou.
            setFinished(true);
            Logger.getLogger(Main.CONNECTION_LOG).warn(JLogger.getDateTime() + " " + JLogger.getTime() + " Could not connect to AP: " + m_strHost + " : " + ex);
        }
    }

    /**
     * Variável que determina se o comando foi executado corretamente. 
     */     
    public boolean getResult()
    {
        return m_bResult;
    }
}
