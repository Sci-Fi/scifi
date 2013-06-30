/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package beans;

import database.JPropertyDBManager;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;

/**
 * Este � um java bean que representa as propriedades do controlador, como o tempo de execu��o dos algoritmos e das coletas de dados.
 * @author ferolim
 */

@ManagedBean
@RequestScoped
public class JPropertiesListBean
{
    private List listProperties = null;
    /**
     * Atualiza no banco de dados a lista de propriedades do controlador e seus valores.
     */
    public void updateProperiesList()
    {
        if(JPropertyDBManager.updatePropertiesList(listProperties)) {
            try
            {
                FacesContext.getCurrentInstance().getExternalContext().redirect("admin.jsf");

                //AQUI IRÁ ATUALIZAR A REGIAO DO TXT DOS APs
            }
            catch (IOException ex)
            {
                Logger.getLogger(JAPListBean.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    /**
     * M�todo que retorna a lista de propriedades do controlador. Esta lista est� guardada na vari�vel listProperties.
     * @return Retorna a lista de propriedades do controlador.
     */
    public List getListProperties()
    {        
        if(listProperties == null)
        {
            listProperties = JPropertyDBManager.getPropertiesListFromDB();
        }

        return listProperties;
    }
}
