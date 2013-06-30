/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package server;

import timers.JAPDataCollectorTimer;
import data.JAPInfo;
import timers.JAlgorithmTimer;
import apcontroller.JCommander;
import timers.JConfigCheckTimer;
import database.JDataManagement;
import data.JSTAInfo;
import apcontroller.Main;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import log.JLogger;
import org.apache.log4j.Logger;

/**
 * Classe que trata as informações provenientes do cliente e adiciona as informações obtidas no banco de dados.
  * @author ferolim
 */
public class JDataParserThread implements Runnable
{
    private static final String AP_TYPE = "AP";
    private static final String WEB_TYPE = "WEB";
    //private static final int AUTH_RESPONSE = 2;
    private Socket m_socketClient;
    /**
     * Construtor da classe JDataParserThread. Inicializa a variável m_socketClient.
     * @param socketClient Socket da conexão com o cliente.
     */
    public JDataParserThread(Socket socketClient)
    {
        m_socketClient = socketClient;
    }

    /**
     * Método que realiza o tratamento dos dados recebidos do cliente e atualiza a informação no banco de dados.
     */
    public void run()
    {
        try
        {
            // O formato da informação recebida é a seguinte:
            // tipo_da_mensagem&mensagem!dado
            // o string deve ser terminado com o caracter \0
            
            DataInputStream inputStream = new DataInputStream(m_socketClient.getInputStream());
            DataOutputStream outputStream = new DataOutputStream(m_socketClient.getOutputStream());

            byte byteBuffer[] = new byte[1024];

            inputStream.read(byteBuffer);
                        
            String strMessage = new String(byteBuffer);

            int nEndChar = strMessage.indexOf("\0");
            if(nEndChar != -1)
            {
                strMessage = strMessage.substring(0, nEndChar);
            }
            
            Logger.getLogger(Main.CONNECTION_LOG).info(JLogger.getDateTime() + " " + JLogger.getTime() + " Message: " + strMessage);
       
            boolean bAnswer = decodeMessage(strMessage);
                        
            outputStream.writeBoolean(bAnswer);
            
            m_socketClient.close();
        }
        catch (IOException ioe)
        {
            Logger.getLogger("CONNECTION_LOG").error(JLogger.getDateTime() + " " + JLogger.getTime() + " IOException on socket listen: " + ioe);
        }
    }
    
    /**
     * Identifica se a mensagem recebida veio de um ponto de acesso ou da interface web.
     * Sabendo sua origem, o método de tratamento de mensagem para cada caso é chamado.
     * 
     * @param strMessage Mensagem recebida.
     */
    protected boolean decodeMessage(String strMessage)
    {
        String[] strSplit = strMessage.split("&");

        if(strSplit.length == 2)
        {
//            if(strSplit[0].equals(AP_TYPE))
//            {
//                return decodeMessageFromAP(strSplit[1]);
//            }
//            else
//            {
                if(strSplit[0].equals(WEB_TYPE))
                {
                    return decodeMessageFromWeb(strSplit[1]);
                }
//            }
        }
        
        return false;
    }
    
     /**
     * Decodifica uma mensagem enviada pela interface web.
     * 
     * @param strMessage Mensagem enviada pela interface web.
     */
    protected boolean decodeMessageFromWeb(String strMessage)
    {
        String[] strSplit = strMessage.split("!");
        
        String strType = strSplit[0];
        String strData = null;
        
        if(strSplit.length > 1)
        {
            strData = strSplit[1];
        }
        
        if(strType.equals("RESTART"))
        {            
            Timer timerTemp = new Timer();
            timerTemp.schedule(new TimerTask() {

                @Override
                public void run()
                {
                    JAlgorithmTimer.cancel();
                    JAPDataCollectorTimer.cancel();
                    JConfigCheckTimer.cancel();
                    
                    Main.firstRun();
                    
                    JAlgorithmTimer.start();
                    JAPDataCollectorTimer.start();
                    JConfigCheckTimer.start();
                }
            }, 0);
        }
        else
        {
            if(strType.equals("SCAN"))
            {
                JAPDataCollectorTimer.runScan();
            }
            else
            {
                if(strType.equals("CHANNEL"))
                {
                    JAlgorithmTimer.runChannelSelection();
                }
                else
                {
                    if(strType.equals("POWERCONTROL"))
                    {
                        JAlgorithmTimer.runPowerControl();
                    }
                    else
                    {
                        if(strType.equals("STA"))
                        {
                            JAPDataCollectorTimer.runSTADump();
                        }
                        else
                        {
                            if(strType.equals("RESETTIMER"))
                            {
                                JAlgorithmTimer.cancel();
                                JAPDataCollectorTimer.cancel();
                                JConfigCheckTimer.cancel();
                                
                                JAlgorithmTimer.start();
                                JAPDataCollectorTimer.start();
                                JConfigCheckTimer.start();
                            }
                            else
                            {
                                if(strType.equals("REBOOT"))
                                {
                                    if(strData == null)
                                    {
                                        JCommander.rebootAll();
                                    }
                                    else
                                    {
                                        JCommander.reboot(JDataManagement.loadAP(strData));
                                    }
                                }
                                else
                                {
                                    if(strType.equals("CONFIGCHECK"))
                                    {
                                        JConfigCheckTimer.runConfigCheck();
                                    }
//                                    else
//                                    {                                        
//                                        if(strType.equals("UPDATE_ENABLED")) 
//                                        {
//                                            String[] vetData = strData.split(";");
//
//                                            String strMAC = vetData[0];
//                                            int nEnabled = Integer.parseInt(vetData[1]);
//
//                                            if(!JDataManagement.updateEnabled(strMAC, nEnabled))
//                                            {                                                    
//                                                return false;                                             
//                                            }  /*                                             
//                                            else
//                                            {                                                    
//                                                JAPInfo apinfo = JDataManagement.loadAP(strMAC);
//                                                // Apenas APs comunicantes terão o arquivo atualizado. Isto foi criado para que o usuário não tenha que aguardar muito para a atualização do mapa da interface web.
//                                                if (apinfo.getReachable() == Main.REACHABLE)
//                                                {
//                                                    if(!JCommander.updateFileEnabledAP(apinfo, nEnabled))
//                                                    {
//
//                                                        return false;
//
//                                                    }
//
//                                                }
//                                            }*/
//                                        }
//                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
            

        
        return true;
    }
    
    /**
     * Decodifica uma mensagem enviada por um ponto de acesso.
     * 
     * @param strMessage Mensagem enviada pelo ponto de acesso.
     */
//    protected boolean decodeMessageFromAP(String strMessage)
//    {
//        // O formato da informação recebida é a seguinte:
//        // subtipo!tipo!STAMAC!APMAC
//        // o string deve ser terminado com o caracter \0
//                
//        String[] strSplit = strMessage.split("!");
//        
//        if(strSplit.length == 4)
//        {
//            int nSubtype = Integer.valueOf(strSplit[0]);
//            int nType = Integer.valueOf(strSplit[1]);
//            
//            String strSTAMAC;
//            String strAPMAC;
//            
//            //a variável type possue sempre o mesmo valor, o que indentifica
//            //a mensagem é o subtype
//            switch(nSubtype)
//            {
//                case AUTH_RESPONSE : strSTAMAC = strSplit[2];
//                                     strAPMAC = strSplit[3];
//                                     
//                                     return handleAuthResponse(strAPMAC, strSTAMAC);
//            }
//        }
//        
//        return false;
//    }

    /**
     * Método que trata a mensagem de autenticação enviada por um ponto de acesso.
     * 
     * @param strAPMAC MAC do AP.
     * @param strSTAMAC MAC do cliente associado.
     */
//    protected boolean handleAuthResponse(String strAPMAC, String strSTAMAC)
//    {
//        JSTAInfo staInfo = new JSTAInfo(strSTAMAC);
//        ArrayList<JSTAInfo> listSTA = new ArrayList<JSTAInfo>();
//        listSTA.add(staInfo);
//        // adiciona no banco de dados a informação sobre a nova estação associada e a que AP ela está associada.
//        return JDataManagement.addSTAInfo(strAPMAC, listSTA);
//    } 
    
}
