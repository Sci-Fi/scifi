/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package data;

/**
 *
 * @author ferolim
 */
public class JRegion
{
    private Integer m_nID;
    private String m_strDescription;
    
    public JRegion(Integer nID, String strDescription)
    {
        m_nID = nID;
        m_strDescription = strDescription;
    }

    /**
     * @return the m_nID
     */
    public Integer getID()
    {
        return m_nID;
    }

    /**
     * @return the m_strDescription
     */
    public String getDescription()
    {
        return m_strDescription;
    }

    /**
     * @param m_strDescription the m_strDescription to set
     */
    public void setDescription(String strDescription)
    {
        m_strDescription = strDescription;
    }
    
    
}
