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
 * Este é um java bean que representa um novo AP que será inserido na lista de APs controlados do banco de dados.
 * @author ferolim
 */
public class JNewAPBean extends JAPInfo
{
    /**
     * Construtor do JNewAPBean.
     */
    
    protected String selectedRegion;
    
    public JNewAPBean()
    {
        super("","",0,"","",-1,-1,10,25,1,null,0,1);
    }
    
    /**
     * Método que adicina um novo AP controlado no banco de dados.
     */
    public void addAP()
    {
        int nStatus = 0;
        
        if (JAPInfoDBManager.addAP(MAC, IP, location, listTxPower, underloadThreshold, overloadThreshold, region))
        {
            nStatus = 1;
        }
        
        try 
        {
            FacesContext.getCurrentInstance().getExternalContext().redirect("new_ap.jsf?added=" + nStatus);
        } 
        catch (IOException ex) 
        {
            Logger.getLogger(JNewAPBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
