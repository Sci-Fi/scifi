/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package channelSelection;

import data.JAPInfo;
import database.JDataManagement;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Classe que representa o vértice do grafo de comunicação entre os pontos de acesso.
 * Este grafo é utilizado pelo controlador para a escolha dos canais que serão utilizados pelos APs.
 * O vértice é a representação de um ponto de acesso.
 * @author helgadb
 */
public class JVertex
{
    // canais suportados pelo algoritmo. 
    private static final ArrayList<Integer> AVAILABLE_CHANNELS = new ArrayList<Integer>()
    {
        {
            add(1);
            add(6);
            add(11);
        }
    };
    
    private JAPInfo m_apInfo = null;
    // a variável m_nchannel é utilizada apenas para APs que não pertencem à rede controlada.
    private int m_nChannel = -1;
    private int m_nClientsQty = -1;
    private ArrayList<JEdge> m_listEdges = null;
    private boolean m_bColored = false;

    public JVertex()
    {
        m_listEdges = new ArrayList<JEdge>();
        m_bColored = false;
    }
    /**
     * Canal do ponto de acesso representado pelo vértice em questão.
     */    
    public int getChannel()
    {
        return m_nChannel;
    }

    public void setChannel(int nChannel)
    {
        m_nChannel = nChannel;
    }
    /**
     * Adiciona arestas ao vértice em questão.
     */    
    public void addEdge(JEdge edgeNeighbor)
    {
        m_listEdges.add(edgeNeighbor);
    }

    /**
     * Este método calcula o nível de saturação do vértice. 
     * Este valor representa o número de canais ocupados por pontos de acesso vizinhos.
     *
    @return Retorna o número de canais ocupados por pontos de acesso vizinhos do vértice em questão.
     *  
     */
    public int getSaturationLevel()
    {
        ArrayList<Integer> listChannels = new ArrayList<Integer>();

        for (int nInd = 0; nInd < m_listEdges.size(); nInd++)
        {
            // se o vértice escutado está colorido e seu canal ainda não está na lista, ele é adicionado.
            if(m_listEdges.get(nInd).getVertex().isColored())
            {
                Integer nChannel = new Integer(new Integer(m_listEdges.get(nInd).getVertex().getChannel()));

                if (!listChannels.contains(nChannel))
                {
                    listChannels.add(nChannel);
                }
            }
        }
        //o número de canais ocupados pelos vértices escutados é retornado.
        return listChannels.size();
    }
    /**
     * APs da rede possuem um objeto JAPInfo.
     * JAPinfo não contém o número de clientes associados. Este valor é guardado na variável
     * m_nClientQty do vértice.
     @param apInfo Objeto APInfo que contém informações sobre o AP representado pelo vértice.
     */
    public void setAPInfo(JAPInfo apInfo)
    {
        m_apInfo = apInfo;

        m_nClientsQty = JDataManagement.getNumberOfSTAs(m_apInfo.getMAC());
    }

    public JAPInfo getAPInfo()
    {
        return m_apInfo;
    }
    /**
     * Número de clientes associados ao AP representado pelo vértice em questão.
     */
    public int getClientsQty()
    {
        return m_nClientsQty;
    }
    /**
     * Booleano que diz se o vértice já está colorido (possui canal).
     * APs não controlados são representados por vértices coloridos com cor (canal) fixa.
     * APs da rede recebem cor conforme o algoritmo de alocação de canal é executado.
     */
    public boolean isColored()
    {
        return m_bColored;
    }

    public void setColored(boolean bColored)
    {
        m_bColored = bColored;
    }
    /**
     * O objetivo desta função é colorir o vértice. 
     * A função getBestColor é utilizada para definir a melhor cor para o vértice.
     */
    public void color()
    {
        m_nChannel = getBestColor();

        // agora o vértice está colorido.
        m_bColored = true;
    }
    /**
     * Define a melhor cor (canal) para o vértice (AP). Busca um canal que não esteja sendo utilizado por APs vizinhos.
     * Caso todos os possíveis canais estejam ocupados, o canal menos ocupado é utilizado.
     */
    public int getBestColor()
    {
        ArrayList<Integer> listChannelTemp = (ArrayList<Integer>) AVAILABLE_CHANNELS.clone();
        // retira da lista de canais disponíveis os canais já ocupados por APs vizinhos.
        for (int nInd = 0; nInd < m_listEdges.size(); nInd++)
        {
            int nChannel = m_listEdges.get(nInd).getVertex().getChannel();
            listChannelTemp.remove(new Integer(nChannel));
        }
        // se ainda existem canais que não foram utilizados por APs vizinhos, retorna o primeiro da lista.
        if (listChannelTemp.size() > 0)
        {
            return listChannelTemp.get(0);
        }
        // se não existem canais desocupados, o canal menos ocupado é escolhido.
        else
        {
            return getLeastCrowdedChannel();
        }
    }
    /**
     * Escolhe o canal menos ocupado para colorir o vértice. 
     * Esta função é utilizada por getBestColor no caso em que pontos de acesso vizinhos estejam ocupando todos os canais possíveis.
     * O canal menos ocupado é aquele que possui a menor qualidade total.
     * A qualidade total é a soma das qualidades de cada aresta do vértice em questão.
     *
     @return Retorna o canal menos ocupado.
     */
    protected int getLeastCrowdedChannel()
    {
        // mapeia um valor de qualidade para um canal.
        HashMap<Integer, Integer> hashChannel = new HashMap<Integer, Integer>();

        for (int nInd = 0; nInd < m_listEdges.size(); nInd++)
        {
            int nChannel = m_listEdges.get(nInd).getVertex().getChannel();

            int nQuality = 0;
            // Se o canal já está no hashmap, sua qualidade é obtida.
            if (hashChannel.containsKey(nChannel))
            {
                nQuality = hashChannel.get(nChannel);
            }
            // O valor da qualidade da aresta em questão é adicionado à qualidade do canal.
            nQuality += m_listEdges.get(nInd).getQuality();
            // o novo valor de qualidade do canal é adicionado no hashmap.
            hashChannel.put(nChannel, nQuality);
        }

        int nLowestValue = Integer.MAX_VALUE;
        int nSelectedChannel = -1;
        // este bloco escolhe a menor qualidade.
        for (int nInd = 0; nInd < AVAILABLE_CHANNELS.size(); nInd++)
        {
            //busca a qualidade do canal em questão no hashmap.
            int nQuality = hashChannel.get(AVAILABLE_CHANNELS.get(nInd));
            
            if (nQuality < nLowestValue)
            {
                // se a qualidade é a menor até então, ela é guardada para a próxima comparação.
                nLowestValue = nQuality;
                // o canal com menor qualidade é o escolhido.
                nSelectedChannel = AVAILABLE_CHANNELS.get(nInd);
            }
        }

        return nSelectedChannel;
    }
}
