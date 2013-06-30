/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package data;

import java.text.DecimalFormat;

/**
 * Esta classe representa um ponto de acesso que é enxergado por outro AP da rede controlada.
 * As informações contidas aqui são obtidas através do processo de scan e podem ser ou não de APs pertencentes à  rede controlada.
 * Dentre elas estão o MAC do AP enxergado, seu canal, SSID, nível de sinal registrado pelo scan, e qualidade com que o AP é enxergado.
 * @author Felipe Rolim
 */
public class JCellInfo
{
    private String MAC;
    private Integer channel;
    private Double signalLevel;
    private Integer quality;
    private String ESSID;

    public JCellInfo(String strMAC, Integer nChannel, Double dSignalLevel, Integer nQuality, String strESSID)
    {
        MAC = strMAC.toUpperCase();
        channel = nChannel;
        signalLevel = roundSignalLevel(dSignalLevel, 2);
        quality = nQuality;
        ESSID = strESSID;
    }
    
    /**
    * Arrendonda um valor double para que ele passe a ter duas casas decimais
    * @param dValue Valor a ser arrendondado
    * @param nDecimalPlaces Número de casas decimais
    * @return Retorna o valor arredondado para duas casas decimais
    */  
    final protected double roundSignalLevel(double dValue, int nDecimalPlaces)
    {
        int nTemp=(int)((dValue*Math.pow(10,nDecimalPlaces)));
        return (((double)nTemp)/Math.pow(10,nDecimalPlaces));
    }

   /**
    * MAC do AP escutado.
    */      
    public String getMAC()
    {
        return MAC;
    }

   /**
    * Canal do ponto de acesso escutado.
    */      
    public int getChannel()
    {
        return channel;
    }

   /**
    * Nível de sinal do ponto de acesso registrado pelo scan.
    */      
    public double getSignalLevel()
    {
        return signalLevel;
    }

   /**
    * Qualidade com que o ponto de acesso é escutado.
    */      
    public int getQuality()
    {
        return quality;
    }
   /**
    * ESSID do ponto de acesso escutado.
    */
    public String getESSID()
    {
        return ESSID;
    }
}

