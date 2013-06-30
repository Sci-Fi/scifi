/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package connection;

import apcontroller.Main;
import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.SCPClient;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import log.JLogger;
import org.apache.log4j.Logger;

/**
 * Esta classe é uma Thread para execução do SCP.
 * @author ferolim
 */
public class JSCPThread extends JBaseThread
{
    protected String m_strHost, m_strHostFile;
    protected File m_fileDest;

    public JSCPThread(String strHost, String strHostFile, File fileDest)
    {
        m_strHost = strHost;
        m_strHostFile = strHostFile;
        m_fileDest = fileDest;
    }
    /**
     * Este método realiza a conexão com o host e executa o SCP, verificando se houve erro durante o processo.
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

            File fileKey = new File("controller_key");
            
            boolean isAuthenticated = m_connection.authenticateWithPublicKey(JRouterConnection.USERNAME, fileKey, null);

            if (isAuthenticated == false)
            {
                throw new IOException("Authentication failed.");
            }
            // cria um cliente SCP
            SCPClient scpClient = new SCPClient(m_connection);
            // inicia o SCP
            if(m_fileDest.exists() && m_fileDest.canWrite())
            {
                FileOutputStream fileStream = new FileOutputStream(m_fileDest);
            
                scpClient.get(m_strHostFile, fileStream);

                fileStream.close();
                
                // Se chegou até aqui, tudo ocorreu bem e o resultado do SCP é válido.
                m_bResult = true;
            }

            // Fecha a conexão.
            m_connection.close();

            // a thread terminou.
            setFinished(true);
            
             Logger.getLogger(Main.CONNECTION_LOG).info(JLogger.getDateTime() + " " + JLogger.getTime() + " Connecting to (SCP): " + m_strHost + " - Host File: " + m_strHostFile + " Result: File downloaded successfully."); 
        }
        catch (Exception ex)
        {
            // Uma falha na execução do bloco try causando o termino da thread
            // indica que houve algum erro durante a execução do SCP. Neste caso, o m_bResult é falso.
            
            // informa que a thread terminou.
            setFinished(true);
            // Registrar no Log de conexão que houve erro durante o SCP.
            Logger.getLogger(Main.CONNECTION_LOG).warn(JLogger.getDateTime() + " " + JLogger.getTime() + " Error during SCP connection with AP: " + m_strHost + " : " + ex);
        }
    }

    /**
     * Variável que determina se o arquivo recebido via SCP é valido. 
     */    
    public boolean getResult()
    {
        return m_bResult;
    }
}
