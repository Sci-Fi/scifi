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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.model.SelectItem;

/**
 * Classe que implementa m�todos de acesso ao banco de dados relacionados aos APs controlados.
 * @author ferolim e carlosmaciel
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
    public final static int NUMBEROFUSERS = 16;
    public final static int REGIONNAME = 17;
    public final static int LATITUDE = 13;
    public final static int LONGITUDE = 14;
    public final static int EMAILSENT = 15;

    public static PreparedStatement pSql = null;

    /**
     * M�todo que adiciona no banco de dados um novo AP controlado.
     * @param strMAC MAC do AP.
     * @param strIP IP do AP.
     * @param strLocation Localiza��o do AP.
     * @param strListTxPower Lista de poss�veis pot�ncias de transmiss�o do AP.
     * @return Retorna true se a opera��o foi realizada com sucesso ou false, caso contr�rio.
     */
    public static boolean addAP(String strMAC, String strIP, String strLocation, String strListTxPower, Integer underloadThreshold, Integer overloadThreshold, Integer regionId, Double latitude, Double longitude)
    {
        boolean bReturn = true;

        Connection connection = getConnection();

        String strSql = "INSERT INTO \"APInfo\" (\"MAC\", \"IP\", \"Location\", \"TxPowerList\", \"UnderloadLimit\", "
                + "\"OverloadLimit\", \"Region\", \"Latitude\", \"Longitude\") VALUES ('" + strMAC + "','" + strIP + "',"
                + "'" + strLocation + "','" + strListTxPower + "','" + underloadThreshold + "','" + overloadThreshold + "',"
                + "" + regionId + ", " + latitude + ", " + longitude + ")";

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
     * M�todo que retorna em qual AP uma esta��o est� associada. A localiza��o do AP � retornada.
     * @param strSTAMAC MAC da esta��o.
     * @return Retorna uma string contendo a localiza��o do AP ao qual a esta��o est� associada.
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

                if (!strMACAP.isEmpty())
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
     * Fun��o que remove do banco de dados o AP dado pelo MAC contido no par�metro strMAC.
     * @param strMAC MAC do AP a ser removido.
     * @return Retorna true se a opera��o foi realizada com sucesso ou false, caso contr�rio.
     */
    public static boolean removeAP(String strMAC)
    {
        Connection connection = getConnection();

        String strSqlRemoveCellInfo = "DELETE FROM \"APInfo_CellInfo\" WHERE \"MAC_AP\" = '" + strMAC + "'";
        String strSqlRemoveSTARelationship = "DELETE FROM \"APInfo_STAInfo\" WHERE \"MAC_AP\" = '" + strMAC + "'";
        String strSqlRemoveSTA = "DELETE FROM \"STAInfo\" WHERE \"MAC\" IN (SELECT \"MAC_STA\" FROM \"APInfo_STAInfo\" WHERE \"MAC_AP\" = '" + strMAC + "')";

        boolean bReturn = true;

        try
        {
            //Transa��es executam as atualiza��es (create, update e delete) mas n�o atualizam no banco de dados durante a execu��o de cada uma
            //  s� � alterado o banco de dados, quando o comando COMMIT � chamado.
            connection.setAutoCommit(false);

            bReturn = JDBManager.execute(connection, strSqlRemoveCellInfo) && JDBManager.execute(connection, strSqlRemoveSTARelationship)
                    && JDBManager.execute(connection, strSqlRemoveSTA);

            if (bReturn)
            {
                String strSql = "DELETE FROM \"APInfo\" WHERE \"MAC\" = '" + strMAC + "'";
                bReturn = JDBManager.execute(connection, strSql);
            }

            connection.commit();
            connection.setAutoCommit(true);
            closeConnection(connection);
        }
        catch (Exception ex)
        {
            try
            {
                connection.rollback();
            }
            catch (SQLException ex1)
            {
                Logger.getLogger(JAPInfoDBManager.class.getName()).log(Level.SEVERE, null, ex1);
            }

            bReturn = false;
        }

        return bReturn;
    }

    /**
     * Este m�todo busca no banco de dados os APs controlados pelo SciFi usando par�metros padr�es.
     * @return Retorna uma lista de JAPInfos.
     */
    public static ArrayList<JAPInfo> getAPListFromDB()
    {
        return getAPListFromDB(IP, true);
    }

    /**
     * Este m�todo busca no banco de dados os APs controlados pelo SciFi.
     * @param nSortType Tipo de ordena��o
     * @param bAscending Indica se a ordena��o � ascendente ou descendente
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
                                rs.getInt(LOAD), rs.getInt(UNDERLOAD), rs.getInt(OVERLOAD), rs.getInt(ENABLED),
                                rs.getInt(REGION), rs.getInt(NUMBEROFUSERS), rs.getInt(REACHABLE),
                                rs.getDouble(LATITUDE), rs.getDouble(LONGITUDE), rs.getInt(EMAILSENT)));

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
     * Este m�todo método verifica no banco de dados se o AP informado, pelo MAC ou IP, existe.
     * @return Retorna uma lista de JAPInfos.
     */
    public static ArrayList<JAPInfo> checkExistenceAP(String MACorIP)
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

                //rs = JDBManager.executeQuery(sSql, "SELECT * FROM \"APInfo\"");
                rs = JDBManager.executeQuery(sSql, "SELECT *, (SELECT COUNT(\"MAC_STA\") FROM \"APInfo_STAInfo\" WHERE \"MAC_AP\" = \"MAC\") AS \"NumberOfUsers\", (SELECT \"Description\" FROM \"Region\" WHERE \"ID\" = \"Region\") AS \"RegionName\" FROM \"APInfo\" WHERE \"MAC\" = '" + MACorIP + "' OR \"IP\" = '" + MACorIP + "'");
                if (rs != null && rs.next())
                {
                    listTemp.add(new JAPInfo(rs.getString(MAC), rs.getString(IP), rs.getInt(CHANNEL),
                            rs.getString(LOCATION), rs.getArray(TXPOWERLIST).toString(), rs.getInt(CURTXPOWER),
                            rs.getInt(LOAD), rs.getInt(UNDERLOAD), rs.getInt(OVERLOAD), rs.getInt(ENABLED),
                            rs.getInt(REGION), rs.getInt(NUMBEROFUSERS), rs.getInt(REACHABLE),
                            rs.getDouble(LATITUDE), rs.getDouble(LONGITUDE), rs.getInt(EMAILSENT)));

                    listTemp.get(listTemp.size() - 1).setRegionName(rs.getString(REGIONNAME));
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
     * Este método retorna o reachable do AP informado.
     * @return Retorna uma lista de JAPInfos.
     */
    public static int getReachable(String MAC)
    {
        Connection connection = getConnection();
        Statement sSql = null;
        ResultSet rs = null;

        if (connection != null)
        {
            try
            {
                sSql = connection.createStatement();
                rs = JDBManager.executeQuery(sSql, "SELECT \"Reachable\" FROM \"APInfo\" WHERE \"MAC\" = '" + MAC + "'");

                if (rs != null && rs.next())
                {
                    return rs.getInt(1);
                }

                return -1;
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

        return -1;
    }

    /**
     * Este m�todo busca no banco de dados todos os APs da região especificada controlados pelo SciFi usando par�metros padr�es.
     * @return Retorna uma lista de JAPInfos.
     */
    public static ArrayList<JAPInfo> getAPListFromRegionDB(int nRegion)
    {
        return getAPListFromRegionDB(nRegion, IP, true);
    }

    /**
     * Este m�todo busca no banco de dados os APs controlados pelo SciFi das Regioes especificadas.
     * @param nRegion Região especificada
     * @param nSortType Tipo de ordena��o
     * @param bAscending Indica se a ordena��o � ascendente ou descendente
     * @return Retorna uma lista de JAPInfos.
     */
    public static ArrayList<JAPInfo> getAPListFromRegionDB(int nRegion, int nSortType, boolean bAscending)
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

                rs = JDBManager.executeQuery(sSql, "SELECT *, (SELECT COUNT(\"MAC_STA\") FROM \"APInfo_STAInfo\" WHERE \"MAC_AP\" = \"MAC\") AS \"NumberOfUsers\", "
                        + "(SELECT \"Description\" FROM \"Region\" WHERE \"ID\" = \"Region\") AS \"RegionName\" "
                        + "FROM \"APInfo\" WHERE \"Region\" = " + nRegion + " ORDER BY " + nSortType + " " + (bAscending ? "ASC" : "DESC"));
                if (rs != null)
                {
                    while (rs.next())
                    {
                        listTemp.add(new JAPInfo(rs.getString(MAC), rs.getString(IP), rs.getInt(CHANNEL),
                                rs.getString(LOCATION), rs.getArray(TXPOWERLIST).toString(), rs.getInt(CURTXPOWER),
                                rs.getInt(LOAD), rs.getInt(UNDERLOAD), rs.getInt(OVERLOAD), rs.getInt(ENABLED), rs.getInt(REGION),
                                rs.getInt(NUMBEROFUSERS), rs.getInt(REACHABLE), rs.getDouble(LATITUDE), rs.getDouble(LONGITUDE),
                                rs.getInt(EMAILSENT)));

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

	/*
     * Altera as propriedades do mapa
     * @param lat Latitude
     * @param lng Longitude
     * @param zoom Zoom
     * @return True para procedimento ocorreu certo
     */
    public static boolean updateDefaultMap(double lat, double lng, int zoom)
    {
        boolean bReturn = true;
        // se conecta ao servidor de aplica��o.
        Connection connection = getConnection();

        String strSql = "UPDATE \"Config\" SET \"Value\"=" + lat + " WHERE \"Property\"='Latitude'";
        String strSql2 = "UPDATE \"Config\" SET \"Value\"=" + lng + " WHERE \"Property\"='Longitude'";
        String strSql3 = "UPDATE \"Config\" SET \"Value\"=" + zoom + " WHERE \"Property\"='Zoom'";

        try
        {
            connection.setAutoCommit(false);

            PreparedStatement pstm = connection.prepareStatement(strSql);
            PreparedStatement pstm2 = connection.prepareStatement(strSql2);
            PreparedStatement pstm3 = connection.prepareStatement(strSql3);

            pstm.executeUpdate();
            pstm2.executeUpdate();
            pstm3.executeUpdate();

            connection.commit();
            connection.setAutoCommit(true);

            closeConnection(connection);
            pstm.close();
            pstm2.close();
            pstm3.close();

            return true;
        }
        catch (SQLException ex)
        {
            try
            {
                connection.rollback();
                return false;
            }
            catch (SQLException ex1)
            {
                Logger.getLogger(JAPInfoDBManager.class.getName()).log(Level.SEVERE, null, ex1);
            }
        }

        return bReturn;
    }

    public static boolean updateCoordinates(double lat, double lng, String MAC)
    {
        boolean bReturn = true;
        // se conecta ao servidor de aplica��o.
        Connection connection = getConnection();

        String strSql = "UPDATE \"APInfo\" SET \"Latitude\"='" + lat + "', \"Longitude\"='" + lng + "'"
                + " WHERE \"MAC\"='" + MAC + "'";

        if (!JDBManager.execute(connection, strSql))
        {
            bReturn = false;
        }
        // libera a conex�o com o servidor de aplica��o.
        closeConnection(connection);

        return bReturn;
    }

    /**
     * Este m�todo atualiza no banco de dados a lista de APs controlados e suas informa��es.
     * @param listAP Lista atualizada de pontos de acesso controlados.
     * @return Retorna true se a opera��o foi realizada com sucesso ou false, caso contr�rio.
     */
    public static boolean updateAPList(List listAP)
    {
        boolean bReturn = true;
        // se conecta ao servidor de aplica��o.
        Connection connection = getConnection();

        for (int nInd = 0; nInd < listAP.size(); nInd++)
        {
            String strSql = "UPDATE \"APInfo\" SET \"IP\"='" + ((JAPInfo) listAP.get(nInd)).getIP() + "', \"Location\"='" + ((JAPInfo) listAP.get(nInd)).getLocation() + "',"
                    + " \"TxPowerList\"='" + ((JAPInfo) listAP.get(nInd)).getListTxPower() + "', \"UnderloadLimit\"='" + ((JAPInfo) listAP.get(nInd)).getUnderloadThreshold()
                    + "', \"OverloadLimit\"='" + ((JAPInfo) listAP.get(nInd)).getOverloadThreshold() + "', \"Region\"='" + ((JAPInfo) listAP.get(nInd)).getRegion() + "', "
                    + "\"Latitude\"='" + ((JAPInfo) listAP.get(nInd)).getLatitude() + "', \"Longitude\"='" + ((JAPInfo) listAP.get(nInd)).getLongitude() + "'"
                    + " WHERE \"MAC\"='" + ((JAPInfo) listAP.get(nInd)).getMAC() + "'";

            if (!JDBManager.execute(connection, strSql))
            {
                bReturn = false;
            }
        }
        // libera a conex�o com o servidor de aplica��o.
        closeConnection(connection);

        return bReturn;
    }

    public static boolean updateAPList(String strMAC, String strIP, String strLocation, String strListTxPower, Integer underloadThreshold, Integer overloadThreshold, Integer regionId, Double latitude, Double longitude)
    {
        boolean bReturn = true;
        // se conecta ao servidor de aplica��o.
        Connection connection = getConnection();

        String strSql = "UPDATE \"APInfo\" SET \"IP\"='" + strIP + "', \"Location\"='" + strLocation + "',"
                + " \"TxPowerList\"='" + strListTxPower + "', \"UnderloadLimit\"='" + underloadThreshold
                + "', \"OverloadLimit\"='" + overloadThreshold + "', \"Region\"='" + regionId + "', "
                + "\"Latitude\"='" + latitude + "', \"Longitude\"='" + longitude + "'"
                + " WHERE \"MAC\"='" + strMAC + "'";

        if (!JDBManager.execute(connection, strSql))
        {
            bReturn = false;
        }
        // libera a conex�o com o servidor de aplica��o.
        closeConnection(connection);

        return bReturn;
    }

    /**
     * Atualiza no banco de dados se o AP est� habilitado ou n�o
     * @param strMAC MAC do AP a ser habilitado ou desabilitado
     * @param enable Indica se o AP ser� habilitado ou desabilitado
     * @return Retorna true se a opera��o foi realizada com sucesso ou false, caso contr�rio.
     */
    public static boolean enableAP(String strMAC, boolean enable)
    {
        boolean bReturn = true;
        // se conecta ao servidor de aplica��o.
        Connection connection = getConnection();

        String strSql = "UPDATE \"APInfo\" SET \"Enabled\"='" + (enable ? "1" : "0") + "' WHERE \"MAC\"='" + strMAC + "'";

        if (!JDBManager.execute(connection, strSql))
        {
            bReturn = false;
        }

        // libera a conex�o com o servidor de aplica��o.
        closeConnection(connection);

        return bReturn;
    }

    /**
     * Este m�todo busca no banco de dados a informa��o de Scan de um AP.
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
     * M�todo que busca no banco todas as regi�es existentes.
     *
    @return Retorna a lista de regi�es.
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

                rs = JDBManager.executeQuery(sSql, "SELECT \"ID\", \"Description\" FROM \"Region\" ORDER BY \"Description\"");

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
