/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package beans;

import data.JAPInfo;
import data.JCellInfo;
import database.JAPInfoDBManager;
import javax.faces.event.ActionEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

/**
 * Este � um java bean que representa a lista de APs controlados.
 * @author Felipe Rolim
 */
@ManagedBean
@SessionScoped
public class JAPListBean extends JAPInfo
{

    static private List listAP = null;
    private List listConnections = null;
    private List listScan = null;
    private Integer numberOfUsers;
    private String userLocation = null;
    private String selectedMAC = "";
    private int sortType = JAPInfoDBManager.IP;
    private boolean sortAscending = true;
    private ArrayList<JAPInfo> listUpdatedRegionAP = new ArrayList<JAPInfo>();

    public JAPListBean()
    {
        super("", "", 0, "", "", -1, -1, 10, 25, 1, null, 0, 1, 0.00, 0.00, 0);
    }

    /**
     * Este m�todo busca no banco de dados a lista de APs controlados.
     * @return Retorna a lista de APs.
     */
    public List getListAP()
    {
        //a lista de pontos de acesso � sempre carregada do banco, apesar do escopo de sess�o
        //deste Bean, pois o controlador tamb�m altera informa��es sobre os APs,
        //como o canal, pot�ncia, etc.

        listAP = JAPInfoDBManager.getAPListFromDB(sortType, sortAscending);

        return listAP;
    }

    public List getListConnections()
    {
        //a lista de pontos de acesso � sempre carregada do banco, apesar do escopo de sess�o
        //deste Bean, pois o controlador tamb�m altera informa��es sobre os APs,
        //como o canal, pot�ncia, etc.

        //listConnections = JAPInfoDBManager.getHistoricalConnections(sortAscending);

        return listConnections;
    }

    /**
     * Este m�todo busca no banco de dados a lista de APs controlados.
     * @return Retorna a lista de APs.
     */
    public List getListCellInfo()
    {
        Map<String, String> mapParameter = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();

        // pegando o MAC da esta��o.
        String strMAC = mapParameter.get("MAC");

        if (strMAC != null && !strMAC.isEmpty())
        {
            listScan = JAPInfoDBManager.getScanInfoFromDB(strMAC);
        }

        return listScan;
    }

    /**
     * M�todo que retorna o MAC informado na queryString
     * @return Retorna a lista de APs.
     */
    public String getMACFromQueryString()
    {
        Map<String, String> mapParameter = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();

        // pegando o MAC da esta��o.
        String strMAC = "";

        String strTemp = mapParameter.get("MAC");

        if (strTemp != null && !strTemp.isEmpty())
        {
            strMAC = strTemp;
        }

        return strMAC;
    }

    /**
     * M�todo que retorna o MAC informado na queryString
     * @return Retorna a lista de APs.
     */
    public String getIPFromQueryString()
    {
        Map<String, String> mapParameter = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();

        String strTemp = mapParameter.get("IP");

        if (strTemp != null && !strTemp.isEmpty())
        {
            return strTemp;
        }

        strTemp = mapParameter.get("MAC");

        if (strTemp != null && !strTemp.isEmpty())
        {
            JAPInfo apInfo = getAPInfoFromMAC(strTemp);

            if (apInfo != null)
            {
                return apInfo.getIP();
            }
        }

        return "";
    }

    /**
     * Busca o objeto da classe JAPInfo correspondente ao MAC fornecido
     * @return Retorna o objeto APInfo ou null caso n�o exista.
     */
    protected JAPInfo getAPInfoFromMAC(String strMAC)
    {
        for (int nInd = 0; nInd < listAP.size(); nInd++)
        {
            if (((JAPInfo) listAP.get(nInd)).getMAC().equals(strMAC))
            {
                return ((JAPInfo) listAP.get(nInd));
            }
        }

        return null;
    }

    public int getRowIndex(String strMAC) {
    	for (int nInd = 0; nInd < listAP.size(); nInd++)
        {
            if (((JAPInfo) listAP.get(nInd)).getMAC().equals(strMAC))
            {
                return nInd;
            }
        }

        return 0;
    }

    /**
     * M�todo que atualiza no banco de dados as informa��es dos APs controlados.
     * A lista atual est� armazenada na vari�vel listAP.
     * A vari�vel listAP � atualizada conforme o usur�rio insira, atrav�s da interface, novos valores para os par�metros dos APs e salve as informa��es.
     * updateRegionDefaultAP() chama o m�todo que atualiza em cada AP, o txt que informa a regi�o alocada a ele. O txt � /tmp/Regiao_alocada
     */
    public void updateList()
    {

        if (JAPInfoDBManager.updateAPList(listAP))
        {
            try
            {
                FacesContext.getCurrentInstance().getExternalContext().redirect("ap_info.jsf");
            }
            catch (IOException ex)
            {
                Logger.getLogger(JAPListBean.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }


    //NAO USADA
    public void updateAP()
    {
        if (JAPInfoDBManager.updateAPList(MAC, IP, location, listTxPower, underloadThreshold, overloadThreshold, region, latitude, longitude))
        {
            try
            {
                FacesContext.getCurrentInstance().getExternalContext().redirect("ap_info.jsf");

                //AQUI IRÁ ATUALIZAR A REGIAO DO TXT DOS APs
            }
            catch (IOException ex)
            {
                Logger.getLogger(JAPListBean.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     * M�todo que descreve a a��o a ser tomada quando o evento de "clicar o bot�o de excluir um AP" na interface de usu�rio ocorre.
     * A a��o, neste caso, � a remo��o do ponto de acesso determinado pela vari�vel MACtoRemove.
     */
//    public void removeAP()
//    {
//        JAPInfoDBManager.removeAP(selectedMAC);
//
//        for (int nInd = 0; nInd < listAP.size(); nInd++)
//        {
//            if (((JAPInfo) listAP.get(nInd)).getMAC().equals(selectedMAC))
//            {
//                listAP.remove(listAP.get(nInd));
//
//                break;
//            }
//        }
//
//        try
//        {
//            FacesContext.getCurrentInstance().getExternalContext().redirect("ap_info.jsf");
//        }
//        catch (IOException ex)
//        {
//            Logger.getLogger(JNewAPBean.class.getName()).log(Level.SEVERE, null, ex);
//        }
//    }

    /**
     * M�todo que descreve a a��o a ser tomada quando o evento de "clicar o bot�o de excluir um AP" na interface de usu�rio ocorre.
     * A a��o, neste caso, � a remo��o do ponto de acesso determinado pela vari�vel MACtoRemove.
     */
    public boolean removeAP(String MAC)
    {
        boolean bReturn = JAPInfoDBManager.removeAP(MAC);

//        if (bReturn)
//        {
            //atualizar lista de APs
            
//            for (int nInd = 0; nInd < listAP.size(); nInd++)
//            {
//                if (((JAPInfo) listAP.get(nInd)).getMAC().equals(MAC))
//                {
//                    listAP.remove(listAP.get(nInd));
//
//                    break;
//                }
//            }
//        }

        return bReturn;
    }

    /**
     * M�todo que retorna o número de esta��es conectadas a um AP.
     * Este valor fica guardado na vari�vel numberOfUsers.
     * @return Retorna o número de esta��es associadas a um AP.
     */
    public Integer getNumberOfUsers()
    {
        numberOfUsers = 0;

        for (int nInd = 0; nInd < listAP.size(); nInd++)
        {
            numberOfUsers += ((JAPInfo) listAP.get(nInd)).getNumberOfUsers();
        }

        return numberOfUsers;
    }

    /**
     * M�todo que habilita o AP caso esteja desabilitado ou vice-versa.
     */
//    public void enableAP()
//    {
//        JAPInfo apInfo = getAPInfoFromMAC(selectedMAC);
//
//        if (apInfo != null)
//        {
//			//Quando atualizar, enviar um comando para o controlador, informando a atualiza磯.
//            JControllerCommanderBean commandBean = new JControllerCommanderBean();
//            commandBean.updateEnabled(selectedMAC, (!apInfo.getEnabled()) ? 1 : 0, apInfo.getRegion());
//        }
//
//    }

    /**
     * M�todo que habilita o AP caso esteja desabilitado ou vice-versa.
     */
    public int enableAP(String MAC, int nEnabled, int nRegion)
    {
//        return (new JControllerCommanderBean().updateEnabled(MAC, nEnabled, nRegion));
        boolean enabled = (nEnabled != 0);
        
        if(JAPInfoDBManager.enableAP(MAC, enabled))
        {
            return 1;
        }
        else
        {
            return 0;
        }
    }

    /**
     * M�todo que verifica se o AP está habilitado ou não, se está comunicável ou não para que o reboot possa ser habilitado.
     */
    public void rebootAP()
    {
        JAPInfo apInfo = getAPInfoFromMAC(selectedMAC);

        if (apInfo != null)
        {
            JControllerCommanderBean commandBean = new JControllerCommanderBean();
            commandBean.rebootAP();
        }

    }

    /**
     * M�todo que verifica se o AP está habilitado ou não, se está comunicável ou não para que o reboot possa ser habilitado.
     */
    public int rebootAP(String MAC, int nRegion)
    {
        return (new JControllerCommanderBean().rebootAP(MAC, nRegion));
    }

    /**
     * Monta a string que � utilizada para preencher o estilo da linha para cada ponto de acesso.
     * O estilo preenche com cores as linhas da tabela de pontos de acesso.
     * Existem 3 cores distintas. Cada cor representa o nível de carga do AP.
     * @return Retorna uma string contendo os nomes dos estilos (low, normal ou full) separados por vírgula.
     */
    public String getRowColor()
    {
        String strClass = new String();

        for (int nInd = 0; nInd < listAP.size(); nInd++)
        {
            switch (((JAPInfo) listAP.get(nInd)).getLoadStatus())
            {
                case JAPInfo.STATUS_LOW:
                    strClass += "low,";
                    break;

                case JAPInfo.STATUS_NORMAL:
                    strClass += "normal,";
                    break;

                case JAPInfo.STATUS_FULL:
                    strClass += "full,";
                    break;
            }
        }

        return strClass;
    }

    /**
     * Monta a string que � utilizada para preencher o estilo da linha para cada ponto de acesso, indicando 
     * se o AP est� desabilitado ou n�o e se sua conex�o cabeada com o controlador est� funcionando.
     * @return Retorna uma string contendo os nomes dos estilos (enabled ou disabled) separados por vírgula.
     */
    public String getRowColorEnabled()
    {
        String strClass = new String();

        for (int nInd = 0; nInd < listAP.size(); nInd++)
        {
            if (((JAPInfo) listAP.get(nInd)).getEnabled())
            {
                if (!(((JAPInfo) listAP.get(nInd)).getReachable()))
                {
                    // se a conex�o entre AP e controlador n�o est� funcionando
                    strClass += "unreachable,";
                }
                // se a conex�o est� funcionando
                else
                {
                    // se o AP est� habilitado
                    strClass += "enabled,";
                }
            }
            // se n�o est� habilitado
            else
            {
                strClass += "disabled,";
            }
        }

        return strClass;
    }

    /**
     * Habilita e desabilita o botão Reboot.
     * @return Retorna uma string contendo os nomes dos estilos (enabled ou disabled) separados por vírgula.
     */
    public String getRebootEnabled()
    {
        String strClass = new String();

        JAPInfo apInfo = getAPInfoFromMAC(selectedMAC);
        strClass = "reiniciar_desabilitar";

        if (apInfo != null)
        {
            if (apInfo.getEnabled() && apInfo.getReachable())
            {
                strClass = "reiniciar";
            }
        }

        return strClass;
    }

    /**
     * Monta a string que � utilizada para preencher o estilo da linha para a tabela de scan.
     * As linhas marcadas em verde indicam os pontos da rede SciFi.
     * @return Retorna uma string contendo os nomes dos estilos separados por vírgula.
     */
    public String getRowColorScan()
    {
        String strClass = "";

        for (int nInd = 0; nInd < listScan.size(); nInd++)
        {
            String strMACTemp = ((JCellInfo) listScan.get(nInd)).getMAC();

            if (listAP != null)
            {
                if (isMACOnTheList(strMACTemp, (ArrayList<JAPInfo>) listAP))
                {
                    strClass += "highlight,";
                }
                else
                {
                    strClass += " ,";
                }
            }
        }

        return strClass;
    }

    /**
     * M�todo que preenche a vari�vel userLocation com a localiza��o do AP ao qual uma determinada esta��o est� associada e a retorna.
     * @return Retorna a localiza��o do AP ao qual a esta��o est� associada.
     */
    public String getUserLocation()
    {
        if (userLocation == null)
        {
            Map<String, String> mapParameter = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();

            // pegando o MAC da esta��o.
            String strMAC = mapParameter.get("MAC");

            if (strMAC != null && !strMAC.isEmpty())
            {
                // pegando a localiza��o da esta��o.
                userLocation = JAPInfoDBManager.getSTALocation(strMAC);
            }
            else
            {

                userLocation = "";
            }
        }

        return userLocation;
    }

    /**
     * M�todo chamado no clique do link de ordena��o por IP.
     */
    public void sortByIPAction()
    {
        if (sortType != JAPInfoDBManager.IP)
        {
            setSortAscending(true);

            sortType = JAPInfoDBManager.IP;
        }
        else
        {
            setSortAscending(!isSortAscending());
        }
    }

    /**
     * M�todo chamado no clique do link de ordena��o por MAC.
     */
    public void sortByMACAction()
    {
        if (sortType != JAPInfoDBManager.MAC)
        {
            setSortAscending(true);

            sortType = JAPInfoDBManager.MAC;
        }
        else
        {
            setSortAscending(!isSortAscending());
        }
    }

    /**
     * M�todo chamado no clique do link de ordena��o pela localiza��o.
     */
    public void sortByLocationAction()
    {
        if (sortType != JAPInfoDBManager.LOCATION)
        {
            setSortAscending(true);

            sortType = JAPInfoDBManager.LOCATION;
        }
        else
        {
            setSortAscending(!isSortAscending());
        }
    }

    /**
     * M�todo chamado no clique do link de ordena��o pela localiza��o.
     */
    public void sortByRegionAction()
    {
        if (sortType != JAPInfoDBManager.REGIONNAME)
        {
            setSortAscending(true);

            sortType = JAPInfoDBManager.REGIONNAME;
        }
        else
        {
            setSortAscending(!isSortAscending());
        }
    }

    /**
     * M�todo chamado no clique do link de ordena��o por canal.
     */
    public void sortByChannelAction()
    {
        if (sortType != JAPInfoDBManager.CHANNEL)
        {
            setSortAscending(true);

            sortType = JAPInfoDBManager.CHANNEL;
        }
        else
        {
            setSortAscending(!isSortAscending());
        }
    }

    /**
     * M�todo chamado no clique do link de ordena��o pela lista de pot�ncia.
     */
    public void sortByTxPowerListAction()
    {
        if (sortType != JAPInfoDBManager.TXPOWERLIST)
        {
            setSortAscending(true);

            sortType = JAPInfoDBManager.TXPOWERLIST;
        }
        else
        {
            setSortAscending(!isSortAscending());
        }
    }

    /**
     * M�todo chamado no clique do link de ordena��o pela pot�ncia.
     */
    public void sortByPowerAction()
    {
        if (sortType != JAPInfoDBManager.CURTXPOWER)
        {
            setSortAscending(true);

            sortType = JAPInfoDBManager.CURTXPOWER;
        }
        else
        {
            setSortAscending(!isSortAscending());
        }
    }

    /**
     * M�todo chamado no clique do link de ordena��o pelo número de usu�rios.
     */
    public void sortByNumberOfUsersAction()
    {
        if (sortType != JAPInfoDBManager.NUMBEROFUSERS)
        {
            setSortAscending(true);

            sortType = JAPInfoDBManager.NUMBEROFUSERS;
        }
        else
        {
            setSortAscending(!isSortAscending());
        }
    }

    /**
     * M�todo chamado no clique do link de ordena��o pela carga do ponto de acesso.
     */
    public void sortByStatusAction()
    {
        if (sortType != JAPInfoDBManager.LOAD)
        {
            setSortAscending(true);

            sortType = JAPInfoDBManager.LOAD;
        }
        else
        {
            setSortAscending(!isSortAscending());
        }
    }

    /**
     * M�todo chamado no clique do link de ordena��o pelo limite de carga baixa.
     */
    public void sortByUnderloadThresholdAction()
    {
        if (sortType != JAPInfoDBManager.UNDERLOAD)
        {
            setSortAscending(true);

            sortType = JAPInfoDBManager.UNDERLOAD;
        }
        else
        {
            setSortAscending(!isSortAscending());
        }
    }

    /**
     * M�todo chamado no clique do link de ordena��o pelo limite de sobrecarga.
     */
    public void sortByOverloadThresholdAction()
    {
        if (sortType != JAPInfoDBManager.OVERLOAD)
        {
            setSortAscending(true);

            sortType = JAPInfoDBManager.OVERLOAD;
        }
        else
        {
            setSortAscending(!isSortAscending());
        }
    }

    /**
     * M�todo chamado no clique do link de ordena��o pela latitude.
     */
    public void sortByLatitude()
    {
        if (sortType != JAPInfoDBManager.LATITUDE)
        {
            setSortAscending(true);

            sortType = JAPInfoDBManager.LATITUDE;
        }
        else
        {
            setSortAscending(!isSortAscending());
        }
    }

    /**
     * M�todo chamado no clique do link de ordena��o pela longitude.
     */
    public void sortByLongitude()
    {
        if (sortType != JAPInfoDBManager.LONGITUDE)
        {
            setSortAscending(true);

            sortType = JAPInfoDBManager.LONGITUDE;
        }
        else
        {
            setSortAscending(!isSortAscending());
        }
    }

    /**
     * M�todo chamado pelo clique de um bot�o que executa alguma opera��o que necessita saber o MAC selecionado.
     * @param event Evento que gera a chamada deste m�todo.
     */
    public void selectMAC(ActionEvent event)
    {
        selectedMAC = (String) event.getComponent().getAttributes().get("selectedMAC");
    }

    /**
     * @return Retorna se a ordena��o � ascendente
     */
    public boolean isSortAscending()
    {
        return sortAscending;
    }

    /**
     * @param bAscending define se a ordena��o ser� ascendente ou descendente
     */
    public void setSortAscending(boolean bAscending)
    {
        sortAscending = bAscending;
    }

    /**
     * O objetivo deste m�todo � verificar se um AP com um determinado MAC se encontra em uma lista de APs.

    @param  strMAC  MAC do AP procurado.
     * 
    @param  listAP Lista de APs em que o AP com o MAC determinado no primeiro par�metro ser� procurado.
     *   
    @return Retorna true se o AP com o MAC procurado (par�metro 1) est� na lista de de APs (par�metro 2).
     */
    protected boolean isMACOnTheList(String strMAC, ArrayList<JAPInfo> listAP)
    {
        strMAC = strMAC.toUpperCase();

        for (int nInd = 0; nInd < listAP.size(); nInd++)
        {
            // se o MAC do AP em quest�o � igual ao MAC do par�metro de entrada, retorna true
            if (listAP.get(nInd).getMAC().equals(strMAC))
            {
                return true;
            }
        }

        return false;
    }

    /**
     * Busca todas as regi�es existentes no banco de dados
     *   
    @return Retorna uma lista com todas as regi�es.
     */
    public Collection getRegions()
    {
        return JAPInfoDBManager.loadRegions();
    }
}