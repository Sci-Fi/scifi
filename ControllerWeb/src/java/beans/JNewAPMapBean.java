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

/**
 * Este � um java bean que representa um novo AP que ser� inserido na lista de APs controlados do banco de dados.
 * @author ferolim
 */
public class JNewAPMapBean extends JAPInfo
{
    /**
     * Construtor do JNewAPBean.
     */

    protected String selectedRegion;

    public JNewAPMapBean()
    {
        super("","",0,"","",-1,-1,10,25,1,null,0,1,0.00,0.00,0);

        if (FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().containsKey("latitude") && FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().containsKey("longitude")) {

            Double LATITUDE = Double.parseDouble(FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("latitude"));
            Double LONGITUDE = Double.parseDouble(FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("longitude"));

            this.setLatitude(LATITUDE);
            this.setLongitude(LONGITUDE);
        }
    }

    /**
     * M�todo que adicina um novo AP controlado no banco de dados.
     */
    public void addAP()
    {
        int nStatus = 0;

        if (JAPInfoDBManager.addAP(MAC, IP, location, listTxPower, underloadThreshold, overloadThreshold, region, latitude, longitude))
        {
            nStatus = 1;
        }

        try
        {
            FacesContext.getCurrentInstance().getExternalContext().redirect("admin.jsf?added=" + nStatus);
        }
        catch (IOException ex)
        {
            Logger.getLogger(JNewAPBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}

