/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import apcontroller.Main;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import loader.JLoader;
import log.JLogger;
import org.apache.log4j.Logger;

/**
 * Classe que representa o servidor que aguarda conexões provenientes dos pontos de acesso ou da interface web para a recepção
 * de informações assíncronas. 
 * O objetivo é que o AP informe ao controlador em tempo real que uma estação se conectou a ele, para que a tabela de estações associadas seja atualizada em tempo real, e que comandos enviados da interface web sejam executados.
 * 
 * @author ferolim
 */
public class JServer implements Runnable
{
    int m_nPort;
    String m_strServerIP;
    /**
     * Contrutor da classe JServer. Inicializa as variáveis de IP e porta do servidor.
     * @param strServerIP IP do servidor.
     * @param nPort Porta do servidor.
     */
    public JServer(String strServerIP, int nPort)
    {
        m_strServerIP = strServerIP;
        m_nPort = nPort;
    }
    /**
     * Método que inicializa o servidor.
     */
    public void run()
    {
        try
        {
            InetAddress ia = InetAddress.getByName(m_strServerIP);
            //o número máximo de conexões é 20
            ServerSocket serverListener = new ServerSocket(m_nPort, 20, ia);
            Socket socketClient;
            
            Logger.getLogger(JLoader.LOADER_LOG).info(JLogger.getDateTime() + " " + JLogger.getTime() + " Listening Server created IP: " + m_strServerIP + " PORT: " + m_nPort);
            Logger.getLogger(Main.JAVA_LOG).info(JLogger.getDateTime() + " " + JLogger.getTime() + " Listening Server created IP: " + m_strServerIP + " PORT: " + m_nPort);
            
            while (true)
            {
                // aguarda a conexão do cliente.
                socketClient = serverListener.accept();                
                JDataParserThread parserThread = new JDataParserThread(socketClient);
                // inicia uma thread para tratamento das informações do cliente.
                Thread t = new Thread(parserThread);
                t.start();
            }
        }
        catch (IOException ex)
        {
            Logger.getLogger(JLoader.LOADER_LOG).error(JLogger.getDateTime() + " " + JLogger.getTime() + " Could not create listening server: " + ex);
        }       
    }
}
