/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package channelSelection;

import data.JAPInfo;
import data.JCellInfo;
import database.JDataManagement;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Esta classe cria um grafo de comunicação entre os pontos de acesso. Este grafo será utilizado pelo algoritmo de alocação de canais.
 * @author ferolim
 */
public class JGraph
{
    // mapeia um vértice para um MAC
    private HashMap<String, JVertex> m_hashVertices;
    ArrayList<JAPInfo> m_listAP;
     
    /**
     * O construtor insere os vértices correspondentes aos APs da rede controlada na lista m_hashVertices.
    @param  listAPs Lista dos APs da rede controlada.
     *         
     */ 
    public JGraph(ArrayList<JAPInfo> listAPs)
    {
        m_listAP = listAPs;
        m_hashVertices = new HashMap<String, JVertex>();

        for(int nInd = 0; nInd < listAPs.size(); nInd++)
        {
            // insere os vértices correspondentes aos APs da rede controlada na lista m_hashVertices
            // O canal é -1 porque a variável m_nChannel do vértice só é utilizada para APs não controlados (que não contém JAPInfo).
            addVertex(listAPs.get(nInd).getMAC(), -1);
        }
    }
    
    /**
     * O objetivo deste método é listar os vértices que não estão coloridos.     
    
    @return Retorna uma lista com os vértices não coloridos.
     */ 
    public ArrayList<JVertex> getListOfNonColoredVertices()
    {
        // cria uma lista contendo todos os vértices.
        ArrayList<JVertex> listAPs = new ArrayList<JVertex>(m_hashVertices.values());
        
        for(int nInd = 0; nInd < listAPs.size();)
        {
            //Se o vértice está colorido, ele é eliminado da lista.
            if(listAPs.get(nInd).isColored())
            {
                listAPs.remove(nInd);
            }
            else
            {
                 nInd++;
            }
        }
        // retorna a lista com os vértices não coloridos.
        return listAPs;
    }
    /**
     * O objetivo deste método é criar um vértice para representar um ponto de acesso e adicioná-lo ao m_hashVertices.

    @param  strMAC MAC do ponto de acesso que será adicionado como vértice.
     * 
    @param nChannel Canal do ponto de acesso que será adicionado como vértice.
     *     
    @return O vértice adicionado. Se o vértice não for adicionado, retorna NULL.
     */ 
    private JVertex addVertex(String strMAC, int nChannel)
    {
        // Se o m_hashVertices já contém um vértice com o mesmo MAC, retorna o próprio vértice.
        if(!m_hashVertices.containsKey(strMAC))
        {
            JVertex vertexTemp = null;

            vertexTemp = new JVertex();

            // Se o ponto de acesso pertence à rede controlada,
            if (JAPInfo.isMACOnTheList(strMAC, m_listAP))
            {
                // inicializa a variável m_apInfo do vértice.
                vertexTemp.setAPInfo(JAPInfo.getAPInfoByMAC(strMAC, m_listAP));
                // insere o novo vértice na lista m_hashVertices
                m_hashVertices.put(strMAC, vertexTemp);
                // preenche as arestas deste vértice
                buildEdges(vertexTemp);
                // retorna o vertice criado.
                return vertexTemp;
            }
            else
            {
                // se o AP não pertence à rede controlada e seu canal é válido,
                if (nChannel >= 1 && nChannel <= 14)
                {
                    // marcar este vértice como colorido
                    vertexTemp.setColored(true);
                    // Preencher a informação sobre seu canal. Se o canal não pertence à lista de possíveis canais, será aproximado de acordo com a função roundChannel.
                    vertexTemp.setChannel(roundChannel(nChannel));
                    // Adicionar o o vértice na lista m_hashVertices.
                    m_hashVertices.put(strMAC, vertexTemp);
                    // retornar o vértice criado.
                    return vertexTemp;
                }
                // se o canal do AP for inválido, retorna NULL
                return null;
            }
        }
        else
        {
            return m_hashVertices.get(strMAC);
        }
    }
    /**
     * O objetivo deste método é criar as arestas de um vértice.
    @param  vertex Vértice para o qual as arestas serão criadas.
     *     
     */
    private void buildEdges(JVertex vertex)
    {
        // Se o vértice representa um AP que pertence à rede controlada,
        if(vertex.getAPInfo() != null)
        {
            //Busca todos os APs que este vértice escuta,
            ArrayList<JCellInfo> listCells = JDataManagement.getCellList(vertex.getAPInfo().getMAC());

            for (int nInd = 0; nInd < listCells.size(); nInd++)
            {
                JCellInfo cellInfo = listCells.get(nInd);
                
                // Adiciona o vértice que representa o AP escutado à lista m_hashVertices.
                // Se o vertice já existir na lista, o próprio será retornado.
                JVertex vertexTemp = addVertex(cellInfo.getMAC(), cellInfo.getChannel());
                // se o vértice foi adicionado com sucesso, 
                if (vertexTemp != null)
                {                    
                    //criar uma aresta, 
                    JEdge edge = new JEdge();
                    //inicializar sua qualidade ,
                    edge.setQuality(cellInfo.getQuality());
                    //inicializar seu vértice de destino (vértice do AP escutado),
                    edge.setVertex(vertexTemp);
                    //e adicionar esta aresta à lista de arestas do vértice dado pelo parâmetro de entrada.
                    vertex.addEdge(edge);
                }
            }
        }
    }
    /**
     * O objetivo deste método é encontrar o canal mais próximo ao do parâmeto dentre os possíveis canais.
     * A interferência gerada por APs externos à rede com canais fora da lista de possíveis canais será 
     * tratata como se ocorrese no canal mais próximo da lista de possíveis canais.
    @param  nChannel  Canal que não pertence à lista de possíveis canais e terá seu valor aproximado.
     *     
    @return O valor do canal mais próximo dentre os possíveis.
     */ 
    private int roundChannel(int nChannel)
    {
        if (nChannel >= 1 && nChannel <= 3)
        {
            return 1;
        }
        else
        {
            if (nChannel >= 4 && nChannel <= 8)
            {
                return 6;
            }
            else
            {
                return 11;
            }
        }
    }
}
