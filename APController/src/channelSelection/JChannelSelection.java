/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package channelSelection;

import data.JAPInfo;
import database.JDataManagement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

/**
 * Classe responsável pela execução do algoritmo de alocação de canal dos pontos de acesso da rede controlada.
 * @author helgadb
 */
public class JChannelSelection
{   
    private static class JVertexComparator implements Comparator
    {
        /**
         * O objetivo deste método é comparar dois vértices.
         * A comparação é realizada utilizando o grau de saturação e o número de clientes associados.
         * Se o grau de saturação é igual para os dois vértices, o número de clientes será utilizado.
         * O vértice com maior grau de saturação terá prioridade na escolha do canal.
         * No caso de empate de graus de saturação, o vértice que possui mais clientes terá prioridade na escolha do canal.

        @param  o1 vértice (AP) 1
         *  

        @param  o2 vértice (AP) 2
         *  

        @return -1 se o nível de saturação do parâmetro o1 é maior do que o do parâmetro o2 ou
         * se o nível de saturação do parâmetro o1 é igual ao do parâmetro o2 e o número
         * de clientes associados ao parâmetro o1 é maior ou igual ao do parâmetro o2.
         * Caso contrário, retorna 1.
         */        
        public int compare(Object o1, Object o2)
        {
            JVertex vertex1 = (JVertex)o1;
            JVertex vertex2 = (JVertex)o2;

            // o vértice com maior grau de saturação terá prioridade na escolha do canal.
            if(vertex1.getSaturationLevel() > vertex2.getSaturationLevel())
            {
                return -1;
            }
            else
            {
                if(vertex1.getSaturationLevel() == vertex2.getSaturationLevel())
                {
                    //no caso de empate de graus de saturação, o vértice que possui mais clientes terá prioridade na escolha do canal.
                    if(vertex1.getClientsQty() >= vertex2.getClientsQty())
                    {
                        return -1;
                    }
                    else
                    {
                        return 1;
                    }
                }
                else
                {
                    return 1;
                }
            }
        }
    }

    /**
     * O objetivo deste método é realizar a alocação dos canais dos pontos de acesso da rede controlada.
     * Ordena a lista dos vértices não coloridos de acordo com o grau de saturação.
     * Vértices com maior grau terão prioridade na escolha do canal. 
     * Se houver empate, o vértice com maior número de clientes associados terá prioridade.
     */  
    public static HashMap<String, Integer> runChannelSelection()
    {
        HashMap<String, Integer> result = new HashMap<String, Integer>();
        
        // cria o grafo de comunicação entre os pontos de acesso.
        JGraph graph = new JGraph(JDataManagement.loadAPList());

        ArrayList<JVertex> listVertices = graph.getListOfNonColoredVertices();

        while(listVertices.size() > 0)
        {
            // Ordena a lista dos vértices não coloridos de acordo com o grau de saturação.
            // Vértices com maior grau terão prioridade na escolha do canal. 
            // Se houver empate, o vértice com maior número de clientes associados terá prioridade.
            Collections.sort(listVertices, new JVertexComparator());
            // pegando o vértice com maior grau de saturação.
            JVertex vertexSelected = listVertices.get(0);
            // colorindo o vértice
            vertexSelected.color();
            //adicionando no mapa de saída o novo canal do AP
            result.put(vertexSelected.getAPInfo().getMAC(), vertexSelected.getChannel());
            // removendo-o da lista de vértices não coloridos.
            listVertices.remove(vertexSelected);
        }
        
        return result;
    }
}
