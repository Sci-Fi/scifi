/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package data;

/**
 * Esta classe representa um ponto de acesso que é enxergado por outro AP da rede controlada.
 * As informações contidas aqui são obtidas através do processo de scan e podem ser ou não de APs pertencentes à rede controlada.
 * Dentre elas estão o MAC do AP enxergado, seu canal, SSID, nível de sinal registrado pelo scan, e qualidade com que o AP é enxergado.
 * @author Felipe Rolim
 */
public class JCellInfo
{
    private String m_strMAC;
    private int m_nChannel;
    private double m_dSignalLevel;
    private int m_nQuality;
    private String m_strESSID;

    public JCellInfo(String strMAC, int nChannel, double dSignalLevel, int nQuality, String strESSID)
    {
        m_strMAC = strMAC.toUpperCase();
        m_nChannel = nChannel;
        m_dSignalLevel = dSignalLevel;
        m_nQuality = nQuality;
        m_strESSID = strESSID;
    }

   /**
    * MAC do AP escutado.
    */      
    public String getMAC()
    {
        return m_strMAC;
    }

   /**
    * Canal do ponto de acesso escutado.
    */      
    public int getChannel()
    {
        return m_nChannel;
    }

   /**
    * Nível de sinal do ponto de acesso registrado pelo scan.
    */      
    public double getSignalLevel()
    {
        return m_dSignalLevel;
    }

   /**
    * Qualidade com que o ponto de acesso é escutado.
    */      
    public int getQuality()
    {
        return m_nQuality;
    }
   /**
    * ESSID do ponto de acesso escutado.
    */
    public String getESSID()
    {
        return m_strESSID;
    }
}
