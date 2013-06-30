/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package data;

/**
 * Esta classe representa uma estação associada a um dos pontos de acesso da rede controlada.
 * @author Felipe Rolim
 */
public class JSTAInfo
{
    private String m_strMAC;

    public JSTAInfo(String strMAC)
    {
        m_strMAC = strMAC.toUpperCase();
    }
   
    /**
    * MAC da estação.
    */
    public String getMAC()
    {
        return m_strMAC;
    }
}
