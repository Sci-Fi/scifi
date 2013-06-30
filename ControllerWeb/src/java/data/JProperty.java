/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package data;

import database.JPropertyDBManager;
import java.util.ArrayList;

/**
 * Classe que representa uma propriedade do controlador SciFi, como o intervalo entre execu��es dos algor�tmos e coletas de dados. Esta propriedade pode ser alterada via interface de usu�rio.
 * @author controlador
 */
public class JProperty
{

    private String name;
    private String value;
    private String description;
    private String type;
    private int order;

    /**
     * Construtor de JProperty
     * @param strName Nome da propriedade.
     * @param strValue Valor da propriedade.
     */
    public JProperty(String strName, String strValue, String strDescription, String strType, int order)
    {
        this.name = strName;
        this.value = strValue;
        this.description = strDescription;
        this.type = strType;
        this.order = order;
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
     * @return A descri��o da propriedade
     */
    public String getDescription()
    {
        return description;
    }

    /**
     * @param strDescription Descri��o da propriedade
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
        if (type.equals("IP"))
        {
            return "IP";
        }
        else
        {
            if (type.equals("SECONDS"))
            {
                return "Segundos";
            }
            else
            {
                if (type.equals("UINT"))
                {
                    return "Inteiro Positivo";
                }
                else
                {
                    if (type.equals("PERCENTAGE"))
                    {
                        return "Porcentagem";
                    }
                    else
                    {
                        if (type.equals("DECIMAL"))
                        {
                            return "Decimal";
                        }
                        else
                        {
                            if (type.equals("STRING"))
                            {
                                return "Texto";
                            }
                            else
                            {
                                if (type.equals("EMAIL"))
                                {
                                    return "Email";
                                }
                            }
                        }
                    }
                }
            }
        }

        return "";
    }

    public static JProperty getProperty(String strName)
    {
        ArrayList<JProperty> listProperties = JPropertyDBManager.getPropertiesListFromDB();

        if (listProperties != null)
        {
            for (int nInd = 0; nInd < listProperties.size(); nInd++)
            {
                if (listProperties.get(nInd).getName().equals(strName))
                {
                    return listProperties.get(nInd);
                }
            }
        }

        return null;
    }

    public int getOrder()
    {
        return order;
    }

    public void setOrder(int order)
    {
        this.order = order;
    }
}
