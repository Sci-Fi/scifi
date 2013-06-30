/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package beans;

import data.JProperty;
import database.JAPInfoDBManager;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

/**
 *
 * @author ferolim
 */
public class JControllerCommanderBean
{

    private String selectedMAC = "";

    /**
     * Mï¿½todo que estabelece uma comunicaï¿½ï¿½o com o Controlador.
     * @return Retorna o socket com a conexï¿½o estabelecida ou null, caso algum erro tenha ocorrido.
     */
    protected Socket connectToController(String strIP, Integer nPort)
    {
        try
        {
            Socket clientSocket = new Socket(strIP, nPort);

            return clientSocket;
        }
        catch (UnknownHostException ex)
        {
            Logger.getLogger(JControllerCommanderBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        catch (IOException ex)
        {
            Logger.getLogger(JControllerCommanderBean.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

    /**
     * Mï¿½todo que fecha a conexï¿½o com o Controlador.
     * @param clientSocket Socket com a conexï¿½o com o Controlador.
     */
    protected void closeSocket(Socket clientSocket)
    {
        try
        {
            clientSocket.close();
        }
        catch (IOException ex)
        {
            Logger.getLogger(JControllerCommanderBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Envia uma mensagem para o Controlador.
     * @param strMessage Mensagem a ser enviada.
     * @return Retorna true se a mensagem foi enviada ou false, caso contrï¿½rio.
     */
    protected boolean sendMessage(String strMessage, Socket clientSocket)
    {
        boolean bReturn = true;

        if (clientSocket != null)
        {
            DataOutputStream output;
            DataInputStream input;
            try
            {
                output = new DataOutputStream(clientSocket.getOutputStream());
                input = new DataInputStream(clientSocket.getInputStream());

                output.write(strMessage.getBytes());
                output.flush();

                bReturn = input.readBoolean();
            }
            catch (IOException ex)
            {
                Logger.getLogger(JControllerCommanderBean.class.getName()).log(Level.SEVERE, null, ex);

                bReturn = false;
            }
        }
        else
        {
            bReturn = false;
        }

        return bReturn;
    }
/**
 * Envia uma mensagem para todas as regiões de controle
 * @param strMessage Mensagem a ser enviada
 * @return true
 */
    protected boolean sendMessageToAll(String strMessage)
    {
        JProperty propServerIP = JProperty.getProperty("ServerIP");
        JProperty propServerPort = JProperty.getProperty("ServerPort");

        ArrayList<SelectItem> listRegions = JAPInfoDBManager.loadRegions();

        for (int nInd = 0; nInd < listRegions.size(); nInd++)
        {
            SelectItem item = listRegions.get(nInd);

            Socket clientSocket = connectToController(propServerIP.getValue(), Integer.valueOf(propServerPort.getValue()) + (Integer) item.getValue() + 1);

            if (clientSocket != null)
            {
                sendMessage(strMessage, clientSocket);

                closeSocket(clientSocket);
            }
        }

        return true;
    }
/**
 * Envia uma mensagem para uma região de controle.
 * @param strMessage Mensagem a ser enviada
 * @param nRegion ID da Região para a qual a mensagem será enviada
 * @return true se conseguiu enviar a mensagem e false caso contrário
 */
    protected boolean sendMessageToRegion(String strMessage, int nRegion)
    {
        boolean bReturn = true;
        
        try {
            JProperty propServerIP = JProperty.getProperty("ServerIP");
            JProperty propServerPort = JProperty.getProperty("ServerPort");

            Socket clientSocket = connectToController(propServerIP.getValue(), Integer.valueOf(propServerPort.getValue()) + nRegion + 1);

            if (clientSocket != null)
            {
                bReturn = sendMessage(strMessage, clientSocket);

                closeSocket(clientSocket);
            } else {
                bReturn = false;
            }
        } catch (Exception ex) {
            bReturn = false;
        }

        return bReturn;
    }

    /**
     * Forï¿½a o Controlador a reiniciar.
     */
    public void forceRestart()
    {
        redirectWithAnswer(sendMessageToAll("WEB&RESTART"), "commander.jsf");
    }

    /**
     * Forï¿½a o Controlador a executar um scan.
     */
    public void forceScan()
    {
        redirectWithAnswer(sendMessageToAll("WEB&SCAN"), "commander.jsf");
    }

    /**
     * Forï¿½a o Controlador a executar o algoritmo de seleï¿½ï¿½o de canal.
     */
    public void forceChannelSelection()
    {
        redirectWithAnswer(sendMessageToAll("WEB&CHANNEL"), "commander.jsf");
    }

    /**
     * Forï¿½a o Controlador a executar o algoritmo de controle de potÃªncia.
     */
    public void forcePowerControl()
    {
        redirectWithAnswer(sendMessageToAll("WEB&POWERCONTROL"), "commander.jsf");
    }

    /*
     * Forï¿½a o Controlador a coletar dados sobre os usuï¿½rios associados aso APs
     */
    public void forceSTAInfoCollection()
    {
        redirectWithAnswer(sendMessageToAll("WEB&STA"), "commander.jsf");
    }

    /*
     * Forï¿½a o Controlador a reiniciar os temporizadores de coleta de dados e algoritmos
     */
    public void forceTimerRestart()
    {
        redirectWithAnswer(sendMessageToAll("WEB&RESETTIMER"), "commander.jsf");
    }

    /*
     * Forï¿½a o Controlador a analisar as configuraï¿½ï¿½es dos pontos de acesso e atualizï¿½-las
     * caso estejam diferentes
     */
    public void forceConfigCheck()
    {
        redirectWithAnswer(sendMessageToAll("WEB&CONFIGCHECK"), "commander.jsf");
    }

    /*
     * Reinicia todos os pontos de acesso.
     */
    public void rebootAll()
    {
        redirectWithAnswer(sendMessageToAll("WEB&REBOOT"), "commander.jsf");
    }

    /*
     * Reinicia o ponto de acesso selecionado.
     */
    public void rebootAP()
    {
        if (selectedMAC != null)
        {
            redirectWithAnswer(sendMessageToAll("WEB&REBOOT!" + selectedMAC), "ap_info.jsf");
        }
    }

    /*
     * Reinicia o ponto de acesso do MAC informado pelo parametro.
     */
    public int rebootAP(String MAC, int nRegion)
    {
        return(sendMessageToRegion("WEB&REBOOT!" + MAC, nRegion) ? 1:0);
    }

    /*
     * Atualiza regiÃ£o de todos aps.
     */
    public void updateRegion(String strPage)
    {
        redirectWithAnswer(sendMessageToAll("WEB&UPDATE_REGION"), strPage);
    }

    /*
     * Informa que o status (habilitado ou desabilitado para controle) foi atualizado.
     */
//    public int updateEnabled(String MAC, int enabled, int nRegion)
//    {
//        return (sendMessageToRegion("WEB&UPDATE_ENABLED!" + MAC + ";" + enabled, nRegion)) ? 1:0;
//    }

    /**
     * Redireciona para a pï¿½gina de comandos com o status da ï¿½ltima execuï¿½ï¿½o.
     * 
     * @param bAnswer Indica se a execuï¿½ï¿½o do comando foi bem sucedida ou nï¿½o.
     */
    protected void redirectWithAnswer(boolean bAnswer, String strPage)
    {
        try
        {
            FacesContext.getCurrentInstance().getExternalContext().redirect(strPage + "?answer=" + (bAnswer == true ? 1 : 0));
        }
        catch (IOException ex)
        {
            Logger.getLogger(JControllerCommanderBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Redireciona para a pï¿½gina informado.
     *
     * @param bAnswer Indica se a execuï¿½ï¿½o do comando foi bem sucedida ou nï¿½o.
     */
    protected void redirectWithoutAnswer(String strPage)
    {
        try
        {
            FacesContext.getCurrentInstance().getExternalContext().redirect(strPage);
        }
        catch (IOException ex)
        {
            Logger.getLogger(JControllerCommanderBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Armazena o AP selecionado da lista
     * @param event Evento que gera a chamada deste mï¿½todo.
     */
    public void selectMAC(ActionEvent event)
    {
        selectedMAC = (String) event.getComponent().getAttributes().get("selectedMAC");
    }
}
