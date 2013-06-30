/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package apcontroller;

import connection.JRouterConnection;
import database.JDataManagement;
import data.JSTAInfo;
import data.JAPInfo;
import data.JCellInfo;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import log.JLogger;
import org.apache.log4j.Logger;
/**
 * Esta classe é responsável pela atualização da potência e do canal no ponto de acesso, e pela execução das operações
 * de scan e station dump.
 * @author Felipe Rolim
 */
public class JCommander
{
    private static final String DEFAULT_DIR = ".";
    private static final String TEXT_EXT = ".txt";
    private static final String SCAN_FILE_PATH = DEFAULT_DIR + "/scan";
    private static final String STA_FILE_NAME = "sta";
    private static final String POWER_FILE_NAME = "power";
    private static final String CHANNEL_FILE_NAME = "channel";
    private static final String ROUTER_SCAN_FILE_PATH = "/tmp/scan.txt";
    private static final String ROUTER_STA_FILE_PATH = "/tmp/sta.txt";
    private static final String ROUTER_POWER_FILE_PATH = "/tmp/power.txt";
    private static final String ROUTER_CHANNEL_FILE_PATH = "/tmp/channel.txt";

    
    /**
    * Método que atualiza o Status de habilitação do AP no arquivo /tmp/AP_Enabled contido no AP.
    * Através deste arquivo o AP saberá se está habilitado ou não para enviar mensagens assíncronas ao controlador.
    * @param apInfo Objeto que representa e guarda informações sobre o AP.
    * @return Retorna true caso o script seja executado corretamente, e false caso não.
    */
    
//    public static boolean updateFileEnabledAP(final JAPInfo apInfo, int Enabled)
//    {   
//        //boolean bResult = JRouterConnection.execCommand("ash /etc/scripts/sta_async_event.sh " + strInternalIP + " " + nPort + " &", apInfo);
//        boolean bResult = JRouterConnection.execCommand("echo " + Enabled + " > /tmp/AP_Enabled", apInfo);
//        
//        return bResult;
//    }
    
    /**
     * Método que apaga a informação de uma determinada estação da tabela de associação do AP. A estação é desassociada do AP.
     * @param apInfo Objeto que representa o AP em questão
     * @param strSTAMAC MAC da estação que será desassociada.
     * @return true se a operação ocorrer corretamente.
     */
//    public static boolean deleteSTA(JAPInfo apInfo, String strSTAMAC)
//    {
//        boolean bResult = JRouterConnection.execCommand("sh /etc/scripts/sta_del.sh " + strSTAMAC, apInfo);
//        return bResult;
//    }
    
    /**
    * Método que cria um arquivo texto especifico para um AP.
     
   @param apInfo Objeto que representa e guarda informações sobre o AP.
     * 
   @param  strFileName Nome do arquivo.
     * 
   @return Retorna o arquivo, caso tenha sido criado.
    */ 
    protected static File getAPSpecificFile(String strFileName, JAPInfo apInfo)
    {
        //o arquivo possue o MAC do AP nele, com o : substituido por _
        File fileTemp = new File(DEFAULT_DIR + "/" + strFileName + "_" + apInfo.getMAC().replace(":", "_") + TEXT_EXT);
        try 
        {
            fileTemp.createNewFile();
        } 
        catch (IOException ex)
        {
            Logger.getLogger(Main.JAVA_LOG).error(JLogger.getDateTime() + " " + JLogger.getTime() + " Could not create file: " + ex);
        }
        
        return fileTemp;
    }       
    
    /**
    * Método que configura o canal do ponto de acesso. Se a operação ocorrer com sucesso, a informação de canal do AP é atualizada no banco de dados.
     
   @param apInfo Objeto que representa e guarda informações sobre o AP.
     * 
   @param  nChannel Canal que será configurado no ponto de acesso.
     * 
   @return Retorna true se a operação foi realizada com sucesso ou false, caso contrário.
    */    
    public static boolean setChannel(JAPInfo apInfo, int nChannel)
    {
        boolean bResult = JRouterConnection.execCommand("sh /etc/scripts/set_channel.sh " + nChannel, apInfo);
        // se o comando foi executado corretamente,
        if(bResult)
        {
            // atualiza o canal do AP no banco de dados
            JDataManagement.updateChannel(apInfo.getMAC(), nChannel);
        }

        return bResult;
    }

   /**
    * Método que configura a potência do ponto de acesso. Se a operação ocorrer com sucesso, a informação de potência do AP é atualizada no banco de dados.
     
   @param apInfo Objeto que representa e guarda informações sobre o AP.
     * 
   @param  nPower  Potência que será configurado no ponto de acesso.
     * 
   @return Retorna true se a operação foi realizada com sucesso ou false, caso contrário.
    */     
    public static boolean setPower(JAPInfo apInfo, int nPower)
    {
        boolean bResult = JRouterConnection.execCommand("sh /etc/scripts/set_power.sh " + nPower, apInfo);

        if(bResult)
        {
            JDataManagement.updateTxPower(apInfo.getMAC(), nPower);
        }

        return bResult;
    }

   /**
    * Método que ordena que o ponto de acesso execute a operação de scan, copia o arquivo com o resultado do ponto de acesso e converte o resultado em uma lista de CellInfos.
    * Se a operação ocorrer com sucesso, a informação do scan é atualizada no banco de dados.
    *
     
   @param apInfo Objeto que representa e guarda informações sobre o AP.
    *
   @return Retorna true se a operação foi realizada com sucesso ou false, caso contrário.
    */     
    public static boolean scan(JAPInfo apInfo)
    {
        if(JRouterConnection.execCommand("sh /etc/scripts/scan.sh ", apInfo))
        {
            //pega o número de identificação da região à qual o AP pertence
            Integer apRegionNumber = Main.getRegionId();                      
            
            File fileTemp = new File(SCAN_FILE_PATH + apRegionNumber + TEXT_EXT);
            try 
            {
                fileTemp.createNewFile();
            } 
            catch (IOException ex)
            {
                Logger.getLogger(Main.JAVA_LOG).error(JLogger.getDateTime() + " " + JLogger.getTime() + " Could not create file: " + ex);
            }
            
            if(JRouterConnection.scpFrom(apInfo, ROUTER_SCAN_FILE_PATH, fileTemp))
            {
                ArrayList<JCellInfo> listCellInfo = JHostFileParser.parseAPFile(SCAN_FILE_PATH + apRegionNumber + TEXT_EXT);

                if(listCellInfo != null)
                {
                    JDataManagement.addScanInfo(apInfo.getMAC(), listCellInfo);
                }

                return true;
            }
        }

        return false;
    }
    
   /**
    * Método que ordena que obtém a potência de transmissão do ponto de acesso, copia o arquivo com o valor e converte o resultado em um inteiro.
    *
     
   @param apInfo Objeto que representa e guarda informações sobre o AP.
    *
   @return Retorna true se a operação foi realizada com sucesso ou false, caso contrário.
    */     
    public static Integer getPower(JAPInfo apInfo)
    {
        if(JRouterConnection.execCommand("sh /etc/scripts/get_power.sh ", apInfo))
        {
            File fileTemp = getAPSpecificFile(POWER_FILE_NAME, apInfo);
            
            if(fileTemp != null && JRouterConnection.scpFrom(apInfo, ROUTER_POWER_FILE_PATH, fileTemp))
            {
                Integer nPower = JHostFileParser.parsePowerFile(fileTemp.getAbsolutePath());

                fileTemp.delete();

                return nPower;
            }
        }

        return null;
    }
    
    /**
    * Método que ordena que obtém o canal do ponto de acesso, copia o arquivo com o valor e converte o resultado em um inteiro.
    *
     
   @param apInfo Objeto que representa e guarda informações sobre o AP.
    *
   @return Retorna true se a operação foi realizada com sucesso ou false, caso contrário.
    */     
    public static Integer getChannel(JAPInfo apInfo)
    {
        if(JRouterConnection.execCommand("sh /etc/scripts/get_channel.sh ", apInfo))
        {
            File fileTemp = getAPSpecificFile(CHANNEL_FILE_NAME, apInfo);
            
            if(fileTemp != null && JRouterConnection.scpFrom(apInfo, ROUTER_CHANNEL_FILE_PATH, fileTemp))
            {
                Integer nChannel = JHostFileParser.parseChannelFile(fileTemp.getAbsolutePath());

                fileTemp.delete();

                return nChannel;
            }
        }

        return null;
    }

   /**
    * Método que ordena que o ponto de acesso execute a operação de station dump, copia o arquivo com o resultado do ponto de acesso e converte o resultado em uma lista de STAInfos.
    * Se a operação ocorrer com sucesso, a informação do station dump é atualizada no banco de dados.
    *
     
   @param apInfo Objeto que representa e guarda informações sobre o AP.
    *
   @return Retorna true se a operação foi realizada com sucesso ou false, caso contrário.
    */         
    public static boolean stationDump(JAPInfo apInfo)
    {

        // executa o station dump
        if(JRouterConnection.execCommand("sh /etc/scripts/sta.sh ", apInfo))
        {
            
            File fileTemp = getAPSpecificFile(STA_FILE_NAME, apInfo);
            
            if(fileTemp != null && JRouterConnection.scpFrom(apInfo, ROUTER_STA_FILE_PATH,  fileTemp))
            {
                ArrayList<JSTAInfo> listSTAInfo = JHostFileParser.parseSTAFile(fileTemp.getAbsolutePath());

                if(listSTAInfo != null)
                {
                    JDataManagement.addSTAInfo(apInfo.getMAC(), listSTAInfo);
                }
                
                fileTemp.delete();

                return true;
            }
        }

        return false;
    }
    
    /**
    * Método que reinicia o ponto de acesso. Quando o AP é reiniciado, a conexão é perdida. Desta forma, a execução dos
    * scripts é realizada por threads, para evitar que a execução do próximo script espera a conxeão do anterior dar timeout
    *
     
   @param apInfo Objeto que representa e guarda informações sobre o AP.
    *
   @return Retorna true se a operação foi realizada com sucesso ou false, caso contrário.
    */         
    public static boolean reboot(final JAPInfo apInfo)
    {
        Thread rebootThread = new Thread(new Runnable() 
        {           
            public void run()
            {
                try 
                {
                    Main.m_mutexGlobal.acquire();

                    //insere no log que o reboot começou e a data e hora
                    Logger.getLogger(Main.JAVA_LOG).info(JLogger.getDateTime() + " " + JLogger.getTime() + " Reboot Started for AP " + apInfo.getIP());                        
                    
                    JRouterConnection.execCommand("sh /etc/scripts/reboot.sh ", apInfo);

                    //esperando o AP reniciar
                    Thread.sleep(60000);
                    
                    //voltando com as configurações anteriores
                    JCommander.setChannel(apInfo, apInfo.getChannel());
                    JCommander.setPower(apInfo, apInfo.getPower());

                    //insere no log que o reboot terminou e a data e hora
                    Logger.getLogger(Main.JAVA_LOG).info(JLogger.getDateTime() + " " + JLogger.getTime() + " Reboot Ended for AP " + apInfo.getIP());                        
                   
                    Main.m_mutexGlobal.release();
                    
                } 
                catch (InterruptedException ex)
                {
                    Logger.getLogger(Main.JAVA_LOG).error(JLogger.getDateTime() + " " + JLogger.getTime() + " Error during the execution of Reboot: " + ex);
                }
            }
        });
        
        rebootThread.start();

        return true;
    }
    
    /**
    * Método que reinicia todos os pontos de acesso da região.
    *
    *
   @return Retorna true se a operação foi realizada com sucesso ou false, caso contrário.
    */         
    public static boolean rebootAll()
    {
        Thread rebootAllThread = new Thread(new Runnable()
        {
            public void run()
            {
                try 
                {
                    ArrayList<JAPInfo> listAP = JDataManagement.loadAPList();

                    Main.m_mutexGlobal.acquire();
                    
                    //insere no log que o reboot começou e a data e hora
                    Logger.getLogger(Main.JAVA_LOG).info(JLogger.getDateTime() + " " + JLogger.getTime() + " Reboot Started for all APs ");                        


                    Thread rebootThread = null;
                    
                    for(int nInd = 0; nInd < listAP.size(); nInd++)
                    {
                        final JAPInfo apInfo = listAP.get(nInd);
                        
                        rebootThread = new Thread(new Runnable() 
                        {           
                            public void run()
                            {
                                try 
                                {
                                    JRouterConnection.execCommand("sh /etc/scripts/reboot.sh ", apInfo);

                                    //esperando o AP reniciar
                                    Thread.sleep(60000);

                                    //voltando com as configurações anteriores
                                    JCommander.setChannel(apInfo, apInfo.getChannel());
                                    JCommander.setPower(apInfo, apInfo.getPower());
                                } 
                                catch (InterruptedException ex)
                                {
                                    Logger.getLogger(Main.JAVA_LOG).error(JLogger.getDateTime() + " " + JLogger.getTime() + " Error during the execution of Reboot: " + ex);
                                }
                            }
                        });

                        rebootThread.start();
                    }
                    
                    //esperando a última thread terminar
                    if(rebootThread != null)
                    {
                        rebootThread.join(120*1000);
                    }

                    //insere no log que o reboot terminou e a data e hora
                    Logger.getLogger(Main.JAVA_LOG).info(JLogger.getDateTime() + " " + JLogger.getTime() + " Reboot Ended for all APs ");                        
 
                    //o mutex só será liberado quando a última thread terminar
                    Main.m_mutexGlobal.release();
                } 
                catch (InterruptedException ex)
                {
                    Logger.getLogger(Main.JAVA_LOG).error(JLogger.getDateTime() + " " + JLogger.getTime() + " Error during the process of rebooting all APs: " + ex);
                }
            }
        });
        
        rebootAllThread.start();
        
        return true;
    }
}
