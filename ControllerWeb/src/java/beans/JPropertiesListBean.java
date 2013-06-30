/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package beans;

import database.JPropertyDBManager;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

/**
 * Este é um java bean que representa as propriedades do controlador, como o tempo de execução dos algoritmos e das coletas de dados.
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
        JPropertyDBManager.updatePropertiesList(listProperties);
    }
    /**
     * Método que retorna a lista de propriedades do controlador. Esta lista está guardada na variável listProperties.
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
