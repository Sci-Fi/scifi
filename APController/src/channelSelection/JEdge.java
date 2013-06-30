/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package channelSelection;

/**
 * Classe que representa a aresta que interliga dois vértices do grafo de comunicação entre os pontos de acesso.
 * Este grafo é utilizado pelo controlador para a escolha dos canais que serão utilizados pelos APs.
 * @author helgadb
 */
public class JEdge
{
    //qualidade com que o ponto de acesso representado por m_vertexDest é enxergado.
    private int m_nQuality;
    private JVertex m_vertexDest;
    /**
     * Qualidade com que um ponto de acesso escuta o outro.
     */
    public int getQuality()
    {
        return m_nQuality;
    }

    public void setQuality(int nQuality)
    {
        m_nQuality = nQuality;
    }
    /**
     * Ponto de acesso de destino da aresta. Representa o AP que é escutado.
     */
    public JVertex getVertex()
    {
        return m_vertexDest;
    }

    public void setVertex(JVertex vertexDest)
    {
        m_vertexDest = vertexDest;
    }
}
