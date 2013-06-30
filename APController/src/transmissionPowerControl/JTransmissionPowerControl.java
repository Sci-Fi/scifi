/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package transmissionPowerControl;

import data.JAPInfo;
import data.JCellInfo;
import database.JDataManagement;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Classe responsável pela execução do algoritmo de controle de potência.
 * @author ferolim
 */
public class JTransmissionPowerControl
{
    /**
     * O objetivo deste método é executar o algoritmo de controle de potência. 
     * Os APs controlados que são enxergados por algum outro AP da rede irão reduzir suas potências.
     * Os APs que não são enxergados irão aumentar suas potências.
     * APs que estão sozinhos em um canal irão aumentar suas potências ao máximo.
     */
    public static HashMap<String, Integer> runTransmissionPowerControl()
    {
        //o mapa de resultado indica o MAC e um valor (-1,0,1). 
        //-1 -> diminuir
        //0  -> máximo
        //1  -> aumentar
        HashMap<String, Integer> result = new HashMap<String, Integer>();
        
        ArrayList<JAPInfo> listAP = JDataManagement.loadAPList();

        ArrayList<JAPInfo> listTemp, listMustReducePower = new ArrayList<JAPInfo>();

        for(int nInd = 0; nInd < listAP.size(); nInd++)
        {
            //APs no mesmo canal do AP atual e que são enxergados devem ter sua potência reduzida.
            listTemp = getAPsOnSameChannel(listAP.get(nInd), listAP);

            for(int nTempInd = 0; nTempInd < listTemp.size(); nTempInd++)
            {
                // protação para que um mesmo AP não seja adicionado mais de uma vez à lista.
                if(!listMustReducePower.contains(listTemp.get(nTempInd)))
                {
                    listMustReducePower.add(listTemp.get(nTempInd));
                }
            }
        }
        
        // diminuindo as potências dos APs listados
        for(int nInd = 0; nInd < listMustReducePower.size(); nInd++)
        {
            JAPInfo apInfo = listMustReducePower.get(nInd);

            result.put(apInfo.getMAC(), -1);
        }
        // Os APs que não tiveram sua potência reduzida, terão a potência aumentada.
        // Se o AP estiver sozinho no canal, sua potência será aumentada para a máxima.
        for(int nInd = 0; nInd < listAP.size(); nInd++)
        {
            if(!listMustReducePower.contains(listAP.get(nInd)))
            {
                if(isAPAloneInChannel(listAP.get(nInd), listAP))
                {
                    result.put(listAP.get(nInd).getMAC(), 0);
                }
                else
                {
                    result.put(listAP.get(nInd).getMAC(), 1);
                }
            }
        }
        
        return result;
    }

      
    /**
     * O objetivo deste método é buscar todos os APs, contidos na lista (listAP), 
     * que estão no mesmo canal que o primeiro parâmetro (apInfo) e são exergados por ele.  

    @param  apInfo  AP cujos vizinhos serão analisados.
     *  

    @param  listAP lista em que a busca por APs no mesmo canal será realizada.
     *  

    @return lista contendo os APs no mesmo canal que o primeiro parâmetro (apInfo) enxerga e pertencem ao segundo parâmetro (listAP).
     *  
     */
    protected static ArrayList<JAPInfo> getAPsOnSameChannel(JAPInfo apInfo, ArrayList<JAPInfo> listAP)
    {
        ArrayList<JAPInfo> listAPsSameChannel = new ArrayList<JAPInfo>();

        //pegando a lista de vizinhos que apInfo exerga
        ArrayList<JCellInfo> listCellInfo = JDataManagement.getCellList(apInfo.getMAC());

        // se algum ap vizinho (enxergado) está na variável listAP e está no mesmo canal que apInfo,
        //ele é adicionado na lista de retorno.
        for(int nInd = 0; nInd < listCellInfo.size(); nInd++)
        {
            JCellInfo cellInfo = listCellInfo.get(nInd);

            if((JAPInfo.isMACOnTheList(cellInfo.getMAC(), listAP)) && (JAPInfo.getAPInfoByMAC(cellInfo.getMAC(), listAP).getChannel() == apInfo.getChannel()))
            {
                listAPsSameChannel.add(JAPInfo.getAPInfoByMAC(cellInfo.getMAC(), listAP));
            }
        }

        return listAPsSameChannel;
    }

    /**
     * O objetivo deste método é verificar se o AP (apInfo) é o único, dentre os contidos em listAP, que está em um determinado canal.
     
    @param  apInfo  AP cujos vizinhos serão analisados.
     *  

    @param  listAP lista em que a busca por APs no mesmo canal será realizada.
     *  

    @return true se apInfo estiver sozinho no canal ou false, caso contrário.
     *  
     */
    
    protected static boolean isAPAloneInChannel(JAPInfo apInfo, ArrayList<JAPInfo> listAP)
    {
        int nChannel = apInfo.getChannel();
        String strMAC = apInfo.getMAC();
                 
        for(int nInd = 0; nInd < listAP.size(); nInd++)
        {
            // Se o AP atual da lista listAP está no mesmo canal que apInfo, e não é o próprio apInfo, o método retorna falso.
            if((!listAP.get(nInd).getMAC().equals(strMAC)) && (listAP.get(nInd).getChannel() == nChannel))
            {
                return false;
            }
        }

        return true;
    }
}
