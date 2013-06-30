/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package beans;

import database.JAPInfoDBManager;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.context.FacesContext;

/**
 *
 * @author ferolim
 */
public class JRegionBean
{
    protected String regionName = "";
    protected String regionToRemove = "";
    Boolean addRegion;
    
    
    /**
     * Método que adicina um novo AP controlado no banco de dados.
     */
    public void addRegion()
    {
        int nStatus = 0;
        
        if (JAPInfoDBManager.addRegion(regionName))
        {
            nStatus = 1;
        }
        
        try 
        {
            FacesContext.getCurrentInstance().getExternalContext().redirect("new_region.jsf?added=" + nStatus);
        } 
        catch (IOException ex) 
        {
            Logger.getLogger(JNewAPBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void removeRegion()
    {
        int nStatus = 0;
        
        if (JAPInfoDBManager.removeRegion(regionToRemove))
        {
            nStatus = 1;
        }
        
        try 
        {
            FacesContext.getCurrentInstance().getExternalContext().redirect("remove_region.jsf?removed=" + nStatus);
        } 
        catch (IOException ex) 
        {
            Logger.getLogger(JNewAPBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void setRegionName(String regionName)
    {
        this.regionName = regionName;
    }
    
    public String getRegionName()
    {
        return regionName;
    }
    
    public void setRegionToRemove(String regionToRemove)
    {
        this.regionToRemove = regionToRemove;
    }
    
    public String getRegionToRemove()
    {
        return regionToRemove;
    }
}
