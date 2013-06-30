/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package database;

import data.JAPInfo;
import beans.JAPListBean;
import data.JCellInfo;
import java.sql.Statement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.model.SelectItem;

/**
 * Classe que implementa métodos de acesso ao banco de dados relacionados aos APs controlados.
 * @author ferolim
 */
public class JAPInfoDBManager extends JDBManager
{
    public final static int MAC = 1;
    public final static int IP = 2;
    public final static int CHANNEL = 3;
    public final static int LOCATION = 4;
    public final static int TXPOWERLIST = 5;
    public final static int CURTXPOWER = 6;
    public final static int LOAD = 7;
    public final static int UNDERLOAD = 8;
    public final static int OVERLOAD = 9;
    public final static int ENABLED = 10;
    public final static int REGION = 11;
    public final static int REACHABLE = 12;
    public final static int NUMBEROFUSERS = 13;
    public final static int REGIONNAME = 14;
    
    /**
     * Método que adiciona no banco de dados um novo AP controlado.
     * @param strMAC MAC do AP.
     * @param strIP IP do AP.
     * @param strLocation Localização do AP.
     * @param strListTxPower Lista de possíveis potências de transmissão do AP.
     * @return Retorna true se a operação foi realizada com sucesso ou false, caso contrário.
     */
    public static boolean addAP(String strMAC, String strIP, String strLocation, String strListTxPower, Integer underloadThreshold, Integer overloadThreshold, Integer regionId)
    {
        boolean bReturn = true;
        
        Connection connection = getConnection();
        
        String strSql = "INSERT INTO \"APInfo\" (\"MAC\", \"IP\", \"Location\", \"TxPowerList\", \"UnderloadLimit\", \"OverloadLimit\", \"Region\") VALUES ('" + strMAC + "','" + strIP + "','" + strLocation + "','" + strListTxPower + "','" + underloadThreshold + "','" + overloadThreshold + "','" + regionId + "')";

        if (!JDBManager.execute(connection, strSql))
        {
            bReturn = false;
        }
        
        closeConnection(connection);

        return bReturn;        
    }
    
    public static boolean addRegion(String strRegionName)
    {
        boolean bReturn = true;
        
        Connection connection = getConnection();
        
        String strSql = "INSERT INTO \"Region\" (\"Description\") VALUES ('" + strRegionName + "')";

        if (!JDBManager.execute(connection, strSql))
        {
            bReturn = false;
        }
        
        closeConnection(connection);

        return bReturn;        
    }
    /**
     * Método que retorna em qual AP uma estação está associada. A localização do AP é retornada.
     * @param strSTAMAC MAC da estação.
     * @return Retorna uma string contendo a localização do AP ao qual a estação está associada.
     */
    public static String getSTALocation(String strSTAMAC)
    {
        String strLocation = "";
        Connection connection = getConnection();
        Statement sSql = null, sSqlTemp = null;
        ResultSet rs = null, rsTemp = null;

        try
        {
            strSTAMAC = strSTAMAC.toUpperCase();
            
            sSql = connection.createStatement();
            
            rs = JDBManager.executeQuery(sSql, "SELECT \"MAC_AP\" FROM \"APInfo_STAInfo\" WHERE \"MAC_STA\" = '" + strSTAMAC + "'");
            
            if (rs != null)
            {
                String strMACAP = "";
                while (rs.next())
                {
                    strMACAP = rs.getString(1);
                }

                if(!strMACAP.isEmpty())
                {
                    sSqlTemp = connection.createStatement();
                    
                    rsTemp = JDBManager.executeQuery(sSqlTemp, "SELECT \"Location\" FROM \"APInfo\" WHERE \"MAC\" = '" + strMACAP + "'");

                    while (rsTemp.next())
                    {
                        strLocation = rsTemp.getString(1);
                    }
                }
            }
        }
        catch (SQLException ex)
        {
            Logger.getLogger(JAPListBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        finally
        {
            closeResultSet(rsTemp);
            closeStatement(sSqlTemp);
            closeResultSet(rs);
            closeStatement(sSql);
            closeConnection(connection);
        }

        return strLocation;
    }
    /**
     * Função que remove do banco de dados o AP dado pelo MAC contido no parâmetro strMAC.
     * @param strMAC MAC do AP a ser removido.
     * @return Retorna true se a operação foi realizada com sucesso ou false, caso contrário.
     */
    public static boolean removeAP(String strMAC)
    {
        Connection connection = getConnection();
        
        String strSqlRemoveCellInfo = "DELETE FROM \"APInfo_CellInfo\" WHERE \"MAC_AP\" = '" + strMAC + "'";
        
        String strSqlRemoveSTARelationship = "DELETE FROM \"APInfo_STAInfo\" WHERE \"MAC_AP\" = '" + strMAC + "'";
        
        String strSqlRemoveSTA = "DELETE FROM \"STAInfo\" WHERE \"MAC\" IN (SELECT \"MAC_STA\" FROM \"APInfo_STAInfo\" WHERE \"MAC_AP\" = '" + strMAC + "')";
        
        boolean bReturn = JDBManager.execute(connection, strSqlRemoveCellInfo) && JDBManager.execute(connection, strSqlRemoveSTARelationship)
                            && JDBManager.execute(connection, strSqlRemoveSTA);
                            
        
        if(bReturn)
        {
            String strSql = "DELETE FROM \"APInfo\" WHERE \"MAC\" = '" + strMAC + "'";
            
            bReturn = JDBManager.execute(connection, strSql);
        }
        
        closeConnection(connection);
        
        return bReturn;
    }
    
    /**
     * Este método busca no banco de dados os APs controlados pelo SciFi usando parâmetros padrões.
     * @return Retorna uma lista de JAPInfos.
     */
    public static ArrayList<JAPInfo> getAPListFromDB()
    {
        return getAPListFromDB(IP,true);
    }
    /**
     * Este método busca no banco de dados os APs controlados pelo SciFi.
     * @param nSortType Tipo de ordenação
     * @param bAscending Indica se a ordenação é ascendente ou descendente
     * @return Retorna uma lista de JAPInfos.
     */
    public static ArrayList<JAPInfo> getAPListFromDB(int nSortType, boolean bAscending)
    {
        Connection connection = getConnection();
        Statement sSql = null;
        ResultSet rs = null;

        ArrayList<JAPInfo> listTemp = new ArrayList<JAPInfo>();

        if (connection != null)
        {
            try
            {
                sSql = connection.createStatement();

                rs = JDBManager.executeQuery(sSql, "SELECT *, (SELECT COUNT(\"MAC_STA\") FROM \"APInfo_STAInfo\" WHERE \"MAC_AP\" = \"MAC\") AS \"NumberOfUsers\", (SELECT \"Description\" FROM \"Region\" WHERE \"ID\" = \"Region\") AS \"RegionName\" FROM \"APInfo\" ORDER BY " + nSortType + " " + (bAscending ? "ASC" : "DESC"));
                if (rs != null)
                {
                    while (rs.next())
                    {
                        listTemp.add(new JAPInfo(rs.getString(MAC), rs.getString(IP), rs.getInt(CHANNEL),
                                rs.getString(LOCATION), rs.getArray(TXPOWERLIST).toString(), rs.getInt(CURTXPOWER),
                                rs.getInt(LOAD), rs.getInt(UNDERLOAD), rs.getInt(OVERLOAD), rs.getInt(ENABLED), rs.getInt(REGION), rs.getInt(NUMBEROFUSERS),rs.getInt(REACHABLE)));
                        
                        listTemp.get(listTemp.size() - 1).setRegionName(rs.getString(REGIONNAME));
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
        }

        return listTemp;
    }
    /**
     * Este método atualiza no banco de dados a lista de APs controlados e suas informações.
     * @param listAP Lista atualizada de pontos de acesso controlados.
     * @return Retorna true se a operação foi realizada com sucesso ou false, caso contrário.
     */
    public static boolean updateAPList(List listAP)
    {
        boolean bReturn = true;
        // se conecta ao servidor de aplicação.
        Connection connection = getConnection();
        
        for (int nInd = 0; nInd < listAP.size(); nInd++)
        {
            String strSql = "UPDATE \"APInfo\" SET \"IP\"='" + ((JAPInfo) listAP.get(nInd)).getIP() + "', \"Location\"='" + ((JAPInfo) listAP.get(nInd)).getLocation() + "',"
                    + " \"TxPowerList\"='" + ((JAPInfo) listAP.get(nInd)).getListTxPower() + "', \"UnderloadLimit\"='" + ((JAPInfo) listAP.get(nInd)).getUnderloadThreshold()
                    + "', \"OverloadLimit\"='" + ((JAPInfo) listAP.get(nInd)).getOverloadThreshold() + "', \"Region\"='" + ((JAPInfo) listAP.get(nInd)).getRegion() + "' WHERE \"MAC\"='" + ((JAPInfo) listAP.get(nInd)).getMAC() + "'";

            if (!JDBManager.execute(connection, strSql))
            {
                bReturn = false;
            }
        }
        // libera a conexão com o servidor de aplicação.
        closeConnection(connection);

        return bReturn;
    }
    
     /**
     * Atualiza no banco de dados se o AP está habilitado ou não
     * @param strMAC MAC do AP a ser habilitado ou desabilitado
     * @param enable Indica se o AP será habilitado ou desabilitado
     * @return Retorna true se a operação foi realizada com sucesso ou false, caso contrário.
     */
    public static boolean enableAP(String strMAC, boolean enable)
    {
        boolean bReturn = true;
        // se conecta ao servidor de aplicação.
        Connection connection = getConnection();

        String strSql = "UPDATE \"APInfo\" SET \"Enabled\"='" + (enable ? "1" : "0") + "' WHERE \"MAC\"='" + strMAC + "'";

        if (!JDBManager.execute(connection, strSql))
        {
            bReturn = false;
        }

        // libera a conexão com o servidor de aplicação.
        closeConnection(connection);

        return bReturn;
    }
    
     /**
     * Este método busca no banco de dados a informação de Scan de um AP.
     * @return Retorna uma lista de JAPInfos.
     */
    public static ArrayList<JCellInfo> getScanInfoFromDB(String strMAC)
    {
        Connection connection = getConnection();
        Statement sSql = null;
        ResultSet rs = null;

        ArrayList<JCellInfo> listTemp = new ArrayList<JCellInfo>();

        if (connection != null)
        {
            try
            {
                sSql = connection.createStatement();

                rs = JDBManager.executeQuery(sSql, "SELECT \"MAC\", \"Channel\", \"SignalLevel\", \"Quality\", \"ESSID\" FROM \"CellInfo\" INNER JOIN \"APInfo_CellInfo\" ON \"MAC\" = \"MAC_CellInfo\" WHERE \"MAC_AP\" = \'" + strMAC + "\'");

                if (rs != null)
                {
                    while (rs.next())
                    {
                        listTemp.add(new JCellInfo(rs.getString(1), rs.getInt(2), rs.getDouble(3), rs.getInt(4), rs.getString(5)));
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
        }

        return listTemp;
    }
    
    /**
    * Método que busca no banco todas as regiões existentes.
    *          
    @return Retorna a lista de regiões.
    */        
    public static ArrayList<SelectItem> loadRegions()
    {      
        Connection connection = getConnection();
        Statement sSql = null;
        ResultSet rs = null;

        ArrayList<SelectItem> listRegion = new ArrayList<SelectItem>();

        if (connection != null)
        {
            try
            {
                sSql = connection.createStatement();

                rs = JDBManager.executeQuery(sSql, "SELECT \"ID\", \"Description\" FROM \"Region\"");

                if (rs != null)
                {
                    while (rs.next())
                    {
                        Integer nID = rs.getInt(1);
                        String strDesc = rs.getString(2);
                    
                        listRegion.add(new SelectItem(nID, strDesc));
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
        }

        return listRegion;
    }

    public static boolean removeRegion(String regionID)
    {
        boolean bReturn = true;
        
        Connection connection = getConnection();
        
        String strSql = "DELETE FROM \"Region\" WHERE \"ID\" = '" + regionID + "'";

        if (!JDBManager.execute(connection, strSql))
        {
            bReturn = false;
        }
        
        closeConnection(connection);

        return bReturn; 
    }
}
