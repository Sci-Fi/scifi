/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package loader;

import database.JDataManagement;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.Timer;
import log.JLogger;
import org.apache.log4j.Logger;
import server.JServer;

/**
 *Classe que inicia diversas instâncias do controlador, uma para cada região
 * @author ferolim
 */
public class JLoader
{
    public static final String LOADER_LOG = "LoaderLog";
    
    public static void main(String[] args) throws IOException
    {
        //os argumentos são (0)IP Externo do controlador, (1)IP Interno do controlador e (2)número da porta base do servidor do controlador
        //o número de argumentos pode ser apenas 2 caso deseje-se utilizar apenas uma interface de rede
        if((args.length == 3 || args.length == 2))
        {
            if (initLogger() && JDataManagement.initConnection(getDBPath()) )
            {
                 
                Timer timerCheckProcesses = new Timer();

                JProcessTimerTask timerTask = new JProcessTimerTask();

                switch (args.length)
                {
                    case 2:
                     timerTask.setParameters(args[0], null, Integer.valueOf(args[1]));
                     //inicia um servidor escutando na porta base
                     initServer(args[0], Integer.valueOf(args[1]));
                     break;

                    case 3:
                     timerTask.setParameters(args[0], args[1], Integer.valueOf(args[2]));
                     //inicia um servidor na interface interna escutando na porta base
                     initServer(args[1], Integer.valueOf(args[2]));
                     break;

                    default:
                     Logger.getLogger(LOADER_LOG).error(JLogger.getDateTime() + " " + JLogger.getTime() + " Number of args do not correspond to default.");
                     break;                     
                }           
                                
                timerCheckProcesses.scheduleAtFixedRate(timerTask, 0, 30000);

                //adicionando um hook para limpar todos os processos criados ao finalizar o programa
                Runtime.getRuntime().addShutdownHook(timerTask.new JCleaner());
                      
            }
            // Caso o controlador não consiga se conectar ao banco de dados:       
            else
            {
                Logger.getLogger(LOADER_LOG).error(JLogger.getDateTime() + " " + JLogger.getTime() + " Could not connect to database. Suggestion: Check DB Path in DBConfig.txt");
            }    
        }
        // Caso o número de argumentos não esteja correto
        else
        {
            Logger.getLogger(LOADER_LOG).error(JLogger.getDateTime() + " " + JLogger.getTime() + " Number of args do not correspond to default, which is 2 or 3.");   
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
            Logger.getLogger(LOADER_LOG).error(JLogger.getDateTime() + " " + JLogger.getTime() + " DB configuration file not found!");
        }
        catch (NoSuchElementException ex)
        {
            Logger.getLogger(LOADER_LOG).error(JLogger.getDateTime() + " " + JLogger.getTime() + " Empty DB configuration file!");
        }

        return strPath;
    }

    private static boolean initLogger()
    {
        return JLogger.createLog(LOADER_LOG, "Loader");
    }
    
    private static void initServer(String strIP, Integer nPort)
    {
        Thread threadServerInternal = new Thread(new JServer(strIP, nPort));
        threadServerInternal.start();
    }
}
