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

    public boolean equals(Object o)
    {
        JSTAInfo otherSTAInfo = (JSTAInfo) o;
        if(this.getMAC().equals(otherSTAInfo.getMAC()))
        {
            return true;
        }
        else
        {
            return false;
        }
    }
            
    
}
