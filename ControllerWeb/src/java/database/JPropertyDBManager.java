/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package database;

import data.JProperty;
import beans.JAPListBean;
import java.sql.Statement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Classe que implementa mï¿½todos de acesso ao banco de dados relacionados ï¿½s propriedades do controlador.
 * @author ferolim
 */
public class JPropertyDBManager extends JDBManager
{
    private final static int PROPERTY_COL = 1;
    private final static int VALUE_COL = 2;
    private final static int DESC_COL = 3;
    private final static int TYPE_COL = 4;
    private final static int ORDER_COL = 5;
   /**
     * Mï¿½todo que atualiza os valores das propriedades no banco de dados.
     * @param listProperties Lista das propriedades cujos valores serï¿½o atualizados.
     * @return Retorna true se a operaï¿½ï¿½o foi realizada com sucesso ou false, caso contrï¿½rio.
     */
    public static boolean updatePropertiesList(List listProperties)
    {
        boolean bReturn = true;

        Connection connection = getConnection();

        for (int nInd = 0; nInd < listProperties.size(); nInd++)
        {
            String strSql = "UPDATE \"Config\" SET  \"Value\"='" + ((JProperty) listProperties.get(nInd)).getValue() + "' WHERE \"Property\"='" + ((JProperty) listProperties.get(nInd)).getName() + "'";

            if (!JDBManager.execute(connection, strSql))
            {
                bReturn = false;
            }
        }

        closeConnection(connection);

        return bReturn;
    }

    /**
     * Busca o valor de uma propriedade específica
     * @param property Especifica a propriedade
     * @return retorna o valor da propriedade
     */
    public static String getValueProperty(String property)
    {
        Connection connection = getConnection();
        Statement sSql = null;
        ResultSet rs = null;

        try
        {
            sSql = connection.createStatement();

            rs = JDBManager.executeQuery(sSql, "SELECT \"Value\" FROM \"Config\" WHERE \"Property\"='" + property + "'");

            if (rs != null && rs.next())
            {
                return rs.getString(1);
            }
        }
        catch (SQLException ex)
        {
            Logger.getLogger(JAPListBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        finally
        {
            closeResultSet(rs);
            closeStatement(sSql);
            closeConnection(connection);
        }

        return null;
    }

    /**
     * Mï¿½todo que busca no banco de dados as propriedades e cria um objeto JProperty para cada uma delas.
     * @return Retorna uma lista de objetos JProperty, representando todas as propriedades do controlador.
     */
    public static ArrayList<JProperty> getPropertiesListFromDB()
    {
        Connection connection = getConnection();
        Statement sSql = null;
        ResultSet rs = null;
        ArrayList<JProperty> listTemp = new ArrayList<JProperty>();

        try
        {
            sSql = connection.createStatement();

            rs = JDBManager.executeQuery(sSql, "SELECT * FROM \"Config\" ORDER BY \"Order\" ASC");

            if (rs != null)
            {
                while (rs.next())
                {
                    listTemp.add(new JProperty(rs.getString(PROPERTY_COL), rs.getString(VALUE_COL), rs.getString(DESC_COL), rs.getString(TYPE_COL), rs.getInt(ORDER_COL)));
                }
            }
        }
        catch (SQLException ex)
        {
            Logger.getLogger(JAPListBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        finally
        {
            closeResultSet(rs);
            closeStatement(sSql);
            closeConnection(connection);
        }

        return listTemp;
    }
}
