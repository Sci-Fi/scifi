/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package data;


/**
 * Classe que representa um AP controlado e seus parâmetros.
 * @author ferolim e carlosmaciel
 */
public class JAPInfo implements Comparable
{
    protected String location;
    protected Integer numberOfUsers;
    protected String MAC;
    protected Integer loadStatus;
    protected String loadStatusText;
    protected String IP;
    protected Integer channel;
    protected String listTxPower = null;
    protected Integer curTxPower;
    protected Integer underloadThreshold;
    protected Integer overloadThreshold;
    protected Integer enabled;
    protected Integer region;
    protected String regionName;
    protected Integer reachable;
    protected Double latitude;
    protected Double longitude;
    protected Integer emailSent;

    public final static int STATUS_LOW = 0;
    public final static int STATUS_NORMAL = 1;
    public final static int STATUS_FULL = 2;
    /**
     * Contrutor do JAPInfo.
     * @param MAC MAC do AP.
     * @param IP IP do AP.
     * @param channel Canal do AP.
     * @param location Localização do AP.
     * @param listTxPower Lista de possíveis potências de transmissão do AP.
     * @param curTxPower Potência de transmissão do AP.
     * @param loadStatus Status de carga do AP.
     * @param underloadThreshold  Limite de carga baixa
     * @param overloadThreshold Limite de sobrecarga
     */
    public JAPInfo(String MAC, String IP, Integer channel, String location, String listTxPower, Integer curTxPower, Integer loadStatus, Integer underloadThreshold, Integer overloadThreshold, Integer enabled, Integer region, Integer numberOfUsers, Integer reachable, Double latitude, Double longitude, Integer emailSent)
    {
        this.location = location;
        this.MAC = MAC.toUpperCase();
        this.IP = IP;
        this.channel = channel;
        this.listTxPower = listTxPower;
        this.curTxPower = curTxPower;
        this.loadStatus = loadStatus;
        this.underloadThreshold = underloadThreshold;
        this.overloadThreshold = overloadThreshold;
        this.enabled = enabled;
        this.region = region;
        this.numberOfUsers = numberOfUsers;
        this.reachable = reachable;
        this.latitude = latitude;
        this.longitude = longitude;
        this.emailSent = emailSent;
    }
    /**
     * Variável que determina o número de estações associadas ao AP.
     * @param numberOfUsers
     */
    public void setNumberOfUsers(Integer numberOfUsers)
    {
        this.numberOfUsers = numberOfUsers;
    }

    public String getLocation()
    {
        return location;
    }

    public void setLocation(String location)
    {
        this.location = location;
    }

    public String getRegionName()
    {
        return regionName;
    }

    public void setRegionName(String regionName)
    {
        this.regionName = regionName;
    }

    public String getMAC()
    {
        return MAC;
    }

    public void setMAC(String MAC)
    {
        this.MAC = MAC.toUpperCase();
    }

    public Integer getRegion()
    {
        return region;
    }

    public void setRegion(Integer region)
    {
        this.region = region;
    }

    public Integer getNumberOfUsers()
    {
        return numberOfUsers;
    }
    /**
     * Compara dois pontos de acesso em relação ao número de estações associadas.
     * @param o objeto JAPInfo a ser comparado.
     * @return Retorna negativo, zero ou positivo se o número de estações associadas ao AP que chama a função é menor, igual ou maior do que o do parâmetro.
     */
    public int compareTo(Object o)
    {
        return numberOfUsers.compareTo(((JAPInfo) o).numberOfUsers);
    }

    public Integer getLoadStatus()
    {
        return loadStatus;
    }
    /**
     * Converte o número que representa o status de carga do AP em um texto descrevendo o status de carga.
     * @return Retorna a string que contém o texto com a descrição do status de carga.
     */
    public String getLoadStatusText()
    {
        switch (loadStatus)
        {
            case JAPInfo.STATUS_LOW:
                loadStatusText = "Carga Baixa";
                break;

            case JAPInfo.STATUS_NORMAL:
                loadStatusText = "Carga Média";
                break;

            case JAPInfo.STATUS_FULL:
                loadStatusText = "Sobrecarregado";
                break;
        }

        return loadStatusText;
    }

    public String getIP()
    {
        return IP;
    }

    public void setIP(String IP)
    {
        this.IP = IP;
    }

    public Integer getChannel()
    {
        return channel;
    }

    public void setChannel(Integer channel)
    {
        this.channel = channel;
    }

    public String getListTxPower()
    {
        return listTxPower;
    }

    public void setListTxPower(String listTxPower)
    {
        this.listTxPower = listTxPower;
    }

    public Integer getCurTxPower()
    {
        return curTxPower;
    }

    public void setCurTxPower(Integer curTxPower)
    {
        this.curTxPower = curTxPower;
    }

    public Integer getUnderloadThreshold()
    {
        return underloadThreshold;
    }

    public void setUnderloadThreshold(Integer underloadThreshold)
    {
        this.underloadThreshold = underloadThreshold;
    }

    public Integer getOverloadThreshold()
    {
        return overloadThreshold;
    }

    public void setOverloadThreshold(Integer overloadThreshold)
    {
        this.overloadThreshold = overloadThreshold;
    }

    public boolean getEnabled()
    {
        return enabled.equals(1);
    }

    public void setEnabled(boolean enabled)
    {
        this.enabled = enabled ? 1 : 0;
    }

    public boolean getReachable()
    {
        return reachable.equals(1);
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Integer getEmailSent() {
        return emailSent;
    }

    public void setEmailSent(Integer emailSent) {
        this.emailSent = emailSent;
    }
}
