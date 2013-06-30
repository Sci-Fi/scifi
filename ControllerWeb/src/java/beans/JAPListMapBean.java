/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package beans;

import data.JAPInfo;
import database.JAPInfoDBManager;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.context.FacesContext;

public class JAPListMapBean extends JAPInfo {

    public JAPListMapBean() {
        //Esta Classe, é para que o usuário altere um AP especifico, escolhido no mapa.
        super("", "", 0, "", "", -1, -1, 10, 25, 1, null, 0, 1, 0.00, 0.00, 0);

        boolean verifica_ip_existe = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().containsKey("IP");
        boolean verifica_mac_existe = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().containsKey("MAC");
        boolean verifica_location_existe = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().containsKey("location");
        boolean verifica_listtxpower_existe = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().containsKey("listtxpower");
        boolean verifica_underloadThreshold_existe = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().containsKey("underloadThreshold");
        boolean verifica_overloadThreshold_existe = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().containsKey("overloadThreshold");
        boolean verifica_region_existe = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().containsKey("region");
        boolean verifica_latitude_existe = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().containsKey("latitude");
        boolean verifica_longitude_existe = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().containsKey("longitude");

        if (verifica_ip_existe && verifica_mac_existe && verifica_location_existe && verifica_listtxpower_existe && verifica_underloadThreshold_existe && verifica_overloadThreshold_existe && verifica_region_existe && verifica_latitude_existe && verifica_longitude_existe) {
            String PARAM_IP = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("IP");
            String PARAM_MAC = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("MAC");
            String PARAM_LOCATION = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("location");
            String PARAM_LISTTXPOWER = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("listtxpower");
            Integer PARAM_UNDERLOADTHRESHOLD = Integer.parseInt(FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("underloadThreshold"));
            Integer PARAM_OVERLOADTHRESHOLD = Integer.parseInt(FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("overloadThreshold"));
            Integer PARAM_REGION = Integer.parseInt(FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("region"));
            Double PARAM_LATITUDE = Double.parseDouble(FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("latitude"));
            Double PARAM_LONGITUDE = Double.parseDouble(FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("longitude"));

            this.setIP(PARAM_IP);
            this.setMAC(PARAM_MAC);
            this.setLocation(PARAM_LOCATION);
            this.setListTxPower(PARAM_LISTTXPOWER);
            this.setUnderloadThreshold(PARAM_UNDERLOADTHRESHOLD);
            this.setOverloadThreshold(PARAM_OVERLOADTHRESHOLD);
            this.setRegion(PARAM_REGION);
            this.setLatitude(PARAM_LATITUDE);
            this.setLongitude(PARAM_LONGITUDE);
        }
    }

    public void updateList() {
        int nStatus = 0;

        if (JAPInfoDBManager.updateAPList(MAC, IP, location, listTxPower, underloadThreshold, overloadThreshold, region, latitude, longitude)) {
            nStatus = 1;
        }

        try {
            FacesContext.getCurrentInstance().getExternalContext().redirect("admin.jsf?edited=" + nStatus);
        } catch (IOException ex) {
            Logger.getLogger(JNewAPBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
