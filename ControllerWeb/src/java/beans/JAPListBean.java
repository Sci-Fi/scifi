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
 * Este é um java bean que representa a lista de APs controlados.
 * @author Felipe Rolim
 */
@ManagedBean
@SessionScoped
public class JAPListBean
{
    private List listAP = null;
    private List listScan = null;
    private Integer numberOfUsers;
    private String userLocation = null;
    private String selectedMAC = "";
    private int sortType = JAPInfoDBManager.IP;
    private boolean sortAscending = true;

    public JAPListBean()
    {

    }
    /**
     * Este método busca no banco de dados a lista de APs controlados.
     * @return Retorna a lista de APs.
     */
    public List getListAP()
    {
        //a lista de pontos de acesso é sempre carregada do banco, apesar do escopo de sessão
        //deste Bean, pois o controlador também altera informações sobre os APs,
        //como o canal, potência, etc.
        
        listAP = JAPInfoDBManager.getAPListFromDB(sortType, sortAscending);
            
        return listAP;
    }
    
     /**
     * Este método busca no banco de dados a lista de APs controlados.
     * @return Retorna a lista de APs.
     */
    public List getListCellInfo()
    {        
        Map<String,String> mapParameter = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();

        // pegando o MAC da estação.
        String strMAC = mapParameter.get("MAC");

        if(strMAC != null && !strMAC.isEmpty())
        {  
           listScan = JAPInfoDBManager.getScanInfoFromDB(strMAC);
        }
  
        return listScan;
    }
    
     /**
     * Método que retorna o MAC informado na queryString
     * @return Retorna a lista de APs.
     */
    public String getMACFromQueryString()
    {        
        Map<String,String> mapParameter = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();

        // pegando o MAC da estação.
        String strMAC = "";
        
        String strTemp = mapParameter.get("MAC");

        if(strTemp != null && !strTemp.isEmpty())
        {  
           strMAC = strTemp;
        }
  
        return strMAC;
    }
    
     /**
     * Método que retorna o MAC informado na queryString
     * @return Retorna a lista de APs.
     */
    public String getIPFromQueryString()
    {        
        Map<String,String> mapParameter = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        
        String strTemp = mapParameter.get("IP");
        
        if(strTemp != null && !strTemp.isEmpty())
        {
            return strTemp;
        }
        
        strTemp = mapParameter.get("MAC");

        if(strTemp != null && !strTemp.isEmpty())
        {  
           JAPInfo apInfo = getAPInfoFromMAC(strTemp);
            
           if(apInfo != null)
           {
               return apInfo.getIP();
           }
        }
     
        return "";
    }
    
     /**
     * Busca o objeto da classe JAPInfo correspondente ao MAC fornecido
     * @return Retorna o objeto APInfo ou null caso não exista.
     */
    protected JAPInfo getAPInfoFromMAC(String strMAC)
    {
        for(int nInd = 0; nInd < listAP.size(); nInd++)
        {
            if(((JAPInfo)listAP.get(nInd)).getMAC().equals(strMAC))
            {
                return ((JAPInfo)listAP.get(nInd));
            }
        }
        
        return null;
    }
    
    /**
     * Método que atualiza no banco de dados as informações dos APs controlados.
     * A lista atual está armazenada na variável listAP.
     * A variável listAP é atualizada conforme o usurário insira, através da interface, novos valores para os parâmetros dos APs e salve as informações.
     */
    public void updateList()
    {
        if(JAPInfoDBManager.updateAPList(listAP))
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

    /**
     * Método que descreve a ação a ser tomada quando o evento de "clicar o botão de excluir um AP" na interface de usuário ocorre.
     * A ação, neste caso, é a remoção do ponto de acesso determinado pela variável MACtoRemove.
     */
    public void removeAP()
    {
        JAPInfoDBManager.removeAP(selectedMAC);

        for(int nInd = 0; nInd < listAP.size(); nInd++)
        {
            if(((JAPInfo)listAP.get(nInd)).getMAC().equals(selectedMAC))
            {
                listAP.remove(listAP.get(nInd));
                
                break;
            }
        }

        try 
        {
            FacesContext.getCurrentInstance().getExternalContext().redirect("ap_info.jsf");
        } 
        catch (IOException ex) 
        {
            Logger.getLogger(JNewAPBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    /**
     * Método que retorna o nÃºmero de estações conectadas a um AP.
     * Este valor fica guardado na variável numberOfUsers.
     * @return Retorna o nÃºmero de estações associadas a um AP.
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
     * Método que habilita o AP caso esteja desabilitado ou vice-versa.
     */
    public void enableAP()
    {
        JAPInfo apInfo = getAPInfoFromMAC(selectedMAC);
        
        if(apInfo != null)
        {
            if(JAPInfoDBManager.enableAP(selectedMAC, !apInfo.getEnabled()))
            {
                apInfo.setEnabled(!apInfo.getEnabled());
            }
        }
        
    }
    /**
     * Monta a string que é utilizada para preencher o estilo da linha para cada ponto de acesso.
     * O estilo preenche com cores as linhas da tabela de pontos de acesso.
     * Existem 3 cores distintas. Cada cor representa o nÃ­vel de carga do AP.
     * @return Retorna uma string contendo os nomes dos estilos (low, normal ou full) separados por vÃ­rgula.
     */
    public String getRowColor()
    {
        String strClass = new String();

        for (int nInd = 0; nInd < listAP.size(); nInd++)
        {
            switch(((JAPInfo) listAP.get(nInd)).getLoadStatus())
            {
                case JAPInfo.STATUS_LOW: strClass += "low,"; break;

                case JAPInfo.STATUS_NORMAL: strClass += "normal,"; break;

                case JAPInfo.STATUS_FULL: strClass += "full,"; break;
            }
        }

        return strClass;
    }
    /**
     * Monta a string que é utilizada para preencher o estilo da linha para cada ponto de acesso, indicando 
     * se o AP está desabilitado ou não e se sua conexão cabeada com o controlador está funcionando.
     * @return Retorna uma string contendo os nomes dos estilos (enabled ou disabled) separados por vÃ­rgula.
     */
    public String getRowColorEnabled()
    {
        String strClass = new String();

        for (int nInd = 0; nInd < listAP.size(); nInd++)
        {
            // se a conexão entre AP e controlador não está funcionando
            if(!(((JAPInfo) listAP.get(nInd)).getReachable()))
            {
                strClass += "unreachable,";
            }
            // se a conexão está funcionando        
            else
            {
                // se o AP está habilitado
                if(((JAPInfo) listAP.get(nInd)).getEnabled())
                {
                    strClass += "enabled,";
                }
                // se não está habilitado
                else
                {
                    strClass += "disabled,";
                }
            }
        }

        return strClass;
    }    
     /**
     * Monta a string que é utilizada para preencher o estilo da linha para a tabela de scan.
     * As linhas marcadas em verde indicam os pontos da rede SciFi.
     * @return Retorna uma string contendo os nomes dos estilos separados por vÃ­rgula.
     */
    public String getRowColorScan()
    {
        String strClass = "";

        for (int nInd = 0; nInd < listScan.size(); nInd++)
        {
            String strMACTemp = ((JCellInfo) listScan.get(nInd)).getMAC();
            
            if(isMACOnTheList(strMACTemp, (ArrayList<JAPInfo>) listAP))
            {
                strClass += "highlight,";
            }
            else
            {
                strClass += " ,";
            }
        }

        return strClass;
    }
    /**
     * Método que preenche a variável userLocation com a localização do AP ao qual uma determinada estação está associada e a retorna.
     * @return Retorna a localização do AP ao qual a estação está associada.
     */
    public String getUserLocation()
    {
        if(userLocation == null)
        {
            Map<String,String> mapParameter = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();

            // pegando o MAC da estação.
            String strMAC = mapParameter.get("MAC");

            if(strMAC != null && !strMAC.isEmpty())
            {   
                // pegando a localização da estação.
                userLocation = JAPInfoDBManager.getSTALocation(strMAC);
            }
            else
            {

                userLocation =  "";
            }
        }
        
        return userLocation;
    }
    
     /**
     * Método chamado no clique do link de ordenação por IP.
     */
    public void sortByIPAction()
    {
        if(sortType != JAPInfoDBManager.IP)
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
     * Método chamado no clique do link de ordenação por MAC.
     */
    public void sortByMACAction()
    {
        if(sortType != JAPInfoDBManager.MAC)
        {
            setSortAscending(true);
            
            sortType = JAPInfoDBManager.MAC;;
        }
        else
        {
            setSortAscending(!isSortAscending());
        }
    }
    
    /**
     * Método chamado no clique do link de ordenação pela localização.
     */
    public void sortByLocationAction()
    {
        if(sortType != JAPInfoDBManager.LOCATION)
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
     * Método chamado no clique do link de ordenação pela localização.
     */
    public void sortByRegionAction()
    {        
        if(sortType != JAPInfoDBManager.REGIONNAME)
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
     * Método chamado no clique do link de ordenação por canal.
     */
    public void sortByChannelAction()
    {        
        if(sortType != JAPInfoDBManager.CHANNEL)
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
     * Método chamado no clique do link de ordenação pela lista de potência.
     */
    public void sortByTxPowerListAction()
    {
        if(sortType != JAPInfoDBManager.TXPOWERLIST)
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
     * Método chamado no clique do link de ordenação pela potência.
     */
    public void sortByPowerAction()
    {        
        if(sortType != JAPInfoDBManager.CURTXPOWER)
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
     * Método chamado no clique do link de ordenação pelo nÃºmero de usuários.
     */
    public void sortByNumberOfUsersAction()
    {        
        if(sortType != JAPInfoDBManager.NUMBEROFUSERS)
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
     * Método chamado no clique do link de ordenação pela carga do ponto de acesso.
     */
    public void sortByStatusAction()
    {        
        if(sortType != JAPInfoDBManager.LOAD)
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
     * Método chamado no clique do link de ordenação pelo limite de carga baixa.
     */
    public void sortByUnderloadThresholdAction()
    {
        if(sortType != JAPInfoDBManager.UNDERLOAD)
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
     * Método chamado no clique do link de ordenação pelo limite de sobrecarga.
     */
    public void sortByOverloadThresholdAction()
    {        
        if(sortType != JAPInfoDBManager.OVERLOAD)
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
     * Método chamado pelo clique de um botão que executa alguma operação que necessita saber o MAC selecionado.
     * @param event Evento que gera a chamada deste método.
     */
    public void selectMAC(ActionEvent event)
    {
        selectedMAC = (String) event.getComponent().getAttributes().get("selectedMAC");
    }

    /**
     * @return Retorna se a ordenação é ascendente
     */
    public boolean isSortAscending()
    {
        return sortAscending;
    }

    /**
     * @param bAscending define se a ordenação será ascendente ou descendente
     */
    public void setSortAscending(boolean bAscending)
    {
        sortAscending = bAscending;
    }
    
    /**
    * O objetivo deste método é verificar se um AP com um determinado MAC se encontra em uma lista de APs.
     
   @param  strMAC  MAC do AP procurado.
     * 
   @param  listAP Lista de APs em que o AP com o MAC determinado no primeiro parâmetro será procurado.
     *   
   @return Retorna true se o AP com o MAC procurado (parâmetro 1) está na lista de de APs (parâmetro 2).
    */     
    protected boolean isMACOnTheList(String strMAC, ArrayList<JAPInfo> listAP)
    {
        strMAC = strMAC.toUpperCase();
        
        for (int nInd = 0; nInd < listAP.size(); nInd++)
        {
            // se o MAC do AP em questão é igual ao MAC do parâmetro de entrada, retorna true
            if (listAP.get(nInd).getMAC().equals(strMAC))
            {
                return true;
            }
        }

        return false;
    }
    
    /**
    * Busca todas as regiões existentes no banco de dados
     *   
   @return Retorna uma lista com todas as regiões.
    */
    public Collection getRegions()
    {
        return JAPInfoDBManager.loadRegions();
    }
}
