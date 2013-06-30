/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package data;

import database.JPropertyDBManager;
import java.util.ArrayList;

/**
 * Classe que representa uma propriedade do controlador SciFi, como o intervalo entre execuções dos algorítmos e coletas de dados. Esta propriedade pode ser alterada via interface de usuário.
 * @author controlador
 */
public class JProperty
{
    private String name;
    private String value;
    private String description;
    private String type;
    /**
     * Construtor de JProperty
     * @param strName Nome da propriedade.
     * @param strValue Valor da propriedade.
     */
    public JProperty(String strName, String strValue, String strDescription, String strType)
    {
        name = strName;
        value = strValue;
        description = strDescription;
        type = strType;
    }
    /**
     * Nome da propriedade.
     * @return Retorna o nome da propriedade.
     */
    public String getName()
    {
        return name;
    }

    public String getValue() 
    {
        return value;
    }
    /**
     * Define o valor da propriedade.
     * @param strValue Valor da propriedade.
     */
    public void setValue(String strValue)
    {
        value = strValue;
    }

    /**
     * @return A descrição da propriedade
     */
    public String getDescription()
    {
        return description;
    }

    /**
     * @param strDescription Descrição da propriedade
     */
    public void setDescription(String strDescription)
    {
        description = strDescription;
    }

    /**
     * @return O tipo da propriedade.
     */
    public String getType()
    {
        return type;
    }
    
    /**
     * @return Retorna o nome do tipo da propriedade 
     */
    public String getTypeName()
    {
        if(type.equals("IP"))
        {
            return "IP";
        }
        else
        {
            if(type.equals("SECONDS"))
            {
                return "Segundos";
            }
            else
            {
                if(type.equals("UINT"))
                {
                    return "Inteiro Positivo";
                }
                else
                {
                    if(type.equals("PERCENTAGE"))
                    {
                        return "Porcentagem";
                    }
                }
            }
        }
        
        return "";
    }
    
    public static JProperty getProperty(String strName)
    {
        ArrayList<JProperty> listProperties = JPropertyDBManager.getPropertiesListFromDB();
        
        if(listProperties != null)
        {
            for(int nInd = 0; nInd < listProperties.size(); nInd++)
            {
                if(listProperties.get(nInd).getName().equals(strName))
                {
                    return listProperties.get(nInd);
                }
            }
        }
        
        return null;
    }
}
