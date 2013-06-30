/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package beans;
/**
 *
 * @author ferolim
 */
public class JRowCounterBean
{
    private Integer m_nRow = 0;

    public Integer getRow()
    {
        return ++m_nRow;
    }

}
