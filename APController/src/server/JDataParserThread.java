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
    private static final int AUTH_RESPONSE = 2;
    private static final int ASYNC_RESPONSE_CONNECT = 1;
    private static final int ASYNC_RESPONSE_DISCONNECT = 0;
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
            //}
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
                                        JCommander.reboot(JAPInfo.getAPInfoByMAC(strData, JDataManagement.loadAPList()));
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
//                                        if(strType.equals("UPDATE_REGION")) 
//                                        {
//                                            ArrayList<JAPInfo> listAP = JDataManagement.loadAPList();
//
//                                            for(int nInd = 0; nInd < listAP.size(); nInd++)
//                                            {
//                                                /* Atualiza a regiao no arquivo de cada AP.
//                                                 * O arquivo se encontra em /tmp e tem nome "Regiao_alocada"
//                                                 */                                                
//                                                JCommander.updateRegionAP(listAP.get(nInd), listAP.get(nInd).getRegion());                                                
//                                            }
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
    
//    /**
//     * Decodifica uma mensagem enviada por um ponto de acesso.
//     * 
//     * @param strMessage Mensagem enviada pelo ponto de acesso.
//     */
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
//                    
//                case ASYNC_RESPONSE_CONNECT : strSTAMAC = strSplit[2];
//                                      strAPMAC = strSplit[3];
//                                      
//                                      return handleAsyncResponse_connect(strAPMAC, strSTAMAC);
//                    
//                case ASYNC_RESPONSE_DISCONNECT : strSTAMAC = strSplit[2];
//                                      strAPMAC = strSplit[3];
//                                      
//                                      return handleAsyncResponse_disconnect(strAPMAC, strSTAMAC);
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
    protected boolean handleAuthResponse(String strAPMAC, String strSTAMAC)
    {
        JSTAInfo staInfo = new JSTAInfo(strSTAMAC);
        ArrayList<JSTAInfo> listSTA = new ArrayList<JSTAInfo>();
        listSTA.add(staInfo);
        // adiciona no banco de dados a informação sobre a nova estação associada e a que AP ela está associada.
        return JDataManagement.addSTAInfo(strAPMAC, listSTA);
    }
    
    /*
     * Esta função regista a conexão do Cliente com o AP.
     * Adiciona no banco de dados a informação sobre a nova estação associada, a data e a hora da associação e a que AP ela está associada.
     * @param strAPMAC MAC do AP.
     * @param strSTAMAC MAC do cliente associado.         
     * @return Retorna true se a operação ocorreu com sucesso ou false, caso contrário.
     */    
//    protected boolean handleAsyncResponse_connect(String strAPMAC, String strSTAMAC)
//    {
//        return JDataManagement.registerConnectionSTA_AP(strSTAMAC, strAPMAC.trim(), true);
//    }    
//    
//    /*
//     * Esta função regista a conexão do Cliente com o AP.
//     * Adiciona no banco de dados a informação sobre a nova estação associada, a data e a hora da associação e a que AP ela está associada.
//     * @param strAPMAC MAC do AP.
//     * @param strSTAMAC MAC do cliente associado.         
//     * @return Retorna true se a operação ocorreu com sucesso ou false, caso contrário.
//     */    
//    protected boolean handleAsyncResponse_disconnect(String strAPMAC, String strSTAMAC)
//    {
//        return JDataManagement.registerConnectionSTA_AP(strSTAMAC, strAPMAC.trim(), false);
//    }    
}
