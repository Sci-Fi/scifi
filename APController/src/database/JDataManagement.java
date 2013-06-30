/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package database;

import apcontroller.Main;
import data.JSTAInfo;
import data.JAPInfo;
import data.JRegion;
import data.JCellInfo;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import log.JLogger;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * Esta classe é responsável pela conexão, requisição e atualização de dados no banco de dados.
 *
 * @author ferolim
 */
public class JDataManagement
{

    protected static Connection m_dbConnection = null;
    private static HashMap<String, ArrayList<JCellInfo>> m_hashCellInfo = new HashMap<String, ArrayList<JCellInfo>>();
    private static HashMap<String, ArrayList<JSTAInfo>> m_hashSTAInfo = new HashMap<String, ArrayList<JSTAInfo>>();
    protected static int APINFO_MAC_COL = 1;
    protected static int APINFO_IP_COL = 2;
    protected static int APINFO_CHANNEL_COL = 3;
    protected static int APINFO_LOCATION_COL = 4;
    protected static int APINFO_TXPOWER_LIST_COL = 5;
    protected static int APINFO_CURRENT_TXPOWER_COL = 6;
    protected static int APINFO_LOADSTATUS_COL = 7;
    protected static int APINFO_UNDERLOAD_LIMIT_COL = 8;
    protected static int APINFO_OVERLOAD_LIMIT_COL = 9;
    protected static int APINFO_ENABLED_COL = 10;
    protected static int APINFO_REGION_COL = 11;    
    protected static int REGION_ID_COL = 1;
    protected static int REGION_DESCRIPTION_COL = 2;
    protected static int APINFO_REACHABLE_COL = 12;
    protected static int APINFO_EMAILSENT_COL = 15;
    //protected static int APINFO_UPDATEREGION_COL = 16;
    //protected static int CONNECTION_INFO_STATUS_COL = 5;
    //protected static int CONNECTION_INFO_MAC_AP_COL = 4;
    //private static int TIMEINFO_STATIONDUMP_ID = 1;

    //static class
    private JDataManagement()
    {
    }
    
//    public static int getTIMEINFO_STATIONDUMP_ID()
//    {
//        return TIMEINFO_STATIONDUMP_ID;
//    }    
    
     /**
     * Método de acesso à variável m_hashSTAInfo 
     * @return m_hashSTAInfo
     */    
    public static HashMap<String, ArrayList<JSTAInfo>> getHashSTAInfo()
    {
        return m_hashSTAInfo;
    }
    
    /**
     * Método de acesso à variável m_hashSTAInfo 
     * @param strMACAP MAC do AP
     * @param listSTAInfo Lista de clientes associados ao AP
     */
    public static void setHashSTAInfo(String strMACAP, ArrayList<JSTAInfo> listSTAInfo)
    {
        m_hashSTAInfo.put(strMACAP, listSTAInfo);
    }
    
   /**
    * Método que inicia a conexão com o banco de dados.
     
   @param  strDBPath Caminho para o banco de dados (IP, porta e nome do banco de dados)
     * 
      
   @return Retorna true se a conexão foi inicializada com sucesso ou false, caso contrário.
    */ 
    public static boolean initConnection(String strDBPath)
    {
        if (m_dbConnection == null)
        {
            try
            {
                Class.forName("org.postgresql.Driver");
            }
            catch (ClassNotFoundException cnfe)
            {
                System.out.println("org.postgresql.Driver class not found - " + cnfe.toString());
                return false;
            }

            try
            {
                String strUsername, strPassword;

                BufferedReader readerFile = new BufferedReader(new FileReader("login_config"));

                strUsername = readerFile.readLine();
                strPassword = readerFile.readLine();
                readerFile.close();
                        
                m_dbConnection = DriverManager.getConnection("jdbc:postgresql://" + strDBPath, strUsername, strPassword);
            }
            catch (SQLException se)
            {
                System.out.println("Could not establish connection - " + se.toString());
                return false;
            }
            catch (IOException e)
            {
                System.out.println("Config file not found!:"+e.getMessage());
                return false;
            }
        }

        return true;
    }
    
   /**
    * Método que executa comandos no banco de dados.
     
   @param  strSQl String que informa o comando a ser executado.
     * 
      
   @return Retorna true se a execução ocorreu com sucesso ou false, caso contrário.
    */    
    protected static boolean execute(String strSQl)
    {
        Statement sSql = null;

        try
        {
            sSql = m_dbConnection.createStatement();
        }
        catch (SQLException se)
        {
            System.out.println("Could not create statement - " + se.toString());
            return false;
        }

        try
        {
            sSql.execute(strSQl);
        }
        catch (SQLException se)
        {
            System.out.println("Could not execute sql - " + se.toString());
            return false;
        }

        return true;
    }

    
   /**
    * Método que executa consultas ao banco de dados.
     
   @param  strSQl String que informa os parâmetros para a consulta no banco de dados.
     * 
      
   @return Retorna um objeto ResultSet, contendo os resultados da consulta.
    */     
    protected static ResultSet executeQuery(String strSQl)
    {
        Statement sSql = null;
        ResultSet rsReturn = null;

        try
        {
            sSql = m_dbConnection.createStatement();
        }
        catch (SQLException se)
        {
            System.out.println("Could not create statement - " + se.toString());
            return null;
        }

        try
        {
            rsReturn = sSql.executeQuery(strSQl);
        }
        catch (SQLException se)
        {
            System.out.println("Could not execute sql - " + se.toString());
        }

        return rsReturn;
    }

    /**
    * Método que apaga do banco de dados as informações de scan (CellInfos escutados) do pontos de acesso especificado como parâmetro.   
    * @param  strMAC String com o MAC do AP que terá sua informação de scan apagada.
    * @return Retorna true se a operação ocorreu com sucesso ou false, caso contrário.
    */     
    public static boolean clearScanInfo(String strMAC)
    {
        return execute("DELETE FROM \"APInfo_CellInfo\" WHERE \"MAC_AP\" = '" + strMAC + "'");
    }
    
    /**
    * Método que apaga do banco de dados as CellInfos que não estão associadas a nenhum AP, ou seja, não há nehum AP as escutando.
     * 
   @return Retorna true se a operação ocorreu com sucesso ou false, caso contrário.
    */     
    public static boolean clearUnreachableCellInfos()
    {
        return execute("DELETE FROM \"CellInfo\" WHERE \"MAC\" NOT IN (SELECT DISTINCT \"MAC_CellInfo\" FROM \"APInfo_CellInfo\")");
    }
    
   /**
   * Método que apaga do banco de dados todas as informações sobre as estações clientes associadas aos APs obtidos pelo Station Dump.   
   @return Retorna true se a operação ocorreu com sucesso ou false, caso contrário.
   */     
    public static boolean clearSTAInfo()
    {
        return execute("DELETE FROM \"APInfo_STAInfo\"")
                && execute("DELETE FROM \"STAInfo\"");
    }

    /**
    * Método que apaga do banco de dados informações sobre estações associadas aos pontos de acesso.   
    * 
    @param listSTA Lista das estações que serão excluidas do banco de dados.
    * 
    @return Retorna true se a operação ocorreu com sucesso ou false, caso contrário.
    */    
    protected static boolean clearSTAs(ArrayList<JSTAInfo> listSTA)
    {
        for(int nInd = 0; nInd < listSTA.size(); nInd++)
        {
            if(!execute("DELETE FROM \"STAInfo\" WHERE \"MAC\" = '" + listSTA.get(nInd).getMAC() +"'"))
            {
                return false;
            }
        }

        return true;
    }
    
    /**
    * Método que apaga do banco de dados a relação de stações associadas a um determinado ponto de acesso.   
    * 
    @param strMAC MAC do ponto de acesso cujas as associações serão apagadas do banco de dados.
    * 
    @return Retorna true se a operação ocorreu com sucesso ou false, caso contrário.
    */ 
    protected static boolean clearSTAsAssociatedWithAP(String strMAC)
    {
        return execute("DELETE FROM \"APInfo_STAInfo\" WHERE \"MAC_AP\" = '" + strMAC +"'");
    }

    /**
    * Método que adiciona informação de scan à variável m_hashCellInfo, fazendo a relação entre a lista dos APs escutados (CellInfos) e o ponto de acesso que gerou o scan.
    * A variável m_hashCellInfo mapeia a lista de JCellInfos provenientes do scan de um AP com o seu MAC.
    * Ao final, as informações de CellInfo são atualizadas no banco de dados através da função addScanInfoToDB.
    * 
    @param strAPMAC MAC do ponto de acesso que realizou o scan.
    * 
    @param listCellInfo Lista contendo os JCellInfos provenientes do scan realizado pelo AP.
    *     
    @return Retorna true se a operação ocorreu com sucesso ou false, caso contrário.
    */     
    public static boolean addScanInfo(String strAPMAC, ArrayList<JCellInfo> listCellInfo)
    {
        // Pega a lista autal de CellInfos do AP.
        ArrayList<JCellInfo> listCellInfoOld = m_hashCellInfo.get(strAPMAC);

        if (listCellInfoOld == null)
        {
            //Se a lista está vazia, atualiza ela com a nova informação de scan do AP.
            m_hashCellInfo.put(strAPMAC, listCellInfo);
        }
        else
        {
            // Se a lista já continha informação, a inserção de novos CellInfos é feita através da função updateCellInfoList.     
            m_hashCellInfo.put(strAPMAC, updateCellInfoList(listCellInfoOld, listCellInfo));
        }
        // A nova informação sobre os CellInfos é atualizada no banco de dados.
        addScanInfoToDB(strAPMAC);

        return true;
    }

    /**
    * Método que adiciona informação de scan no banco de dados e faz a relação entre os CellInfos e o AP que gerou o scan.
    * 
    @param strAPMAC MAC do ponto de acesso que realizou o scan.
    *      
    @return Retorna true se a operação ocorreu com sucesso ou false, caso contrário.
    */     
    private static boolean addScanInfoToDB(String strAPMAC)
    {
        // obtém a lista de CellInfos do AP.
        ArrayList<JCellInfo> listNewCellInfo = m_hashCellInfo.get(strAPMAC);
        
        //limpando os dados atuais
        clearScanInfo(strAPMAC);

        //lista com os MACs de todas as cellinfos contidas no banco de dados
        ArrayList<String> listMACsCellInfos = getCellInfosMACs();
        
        if (listNewCellInfo != null)
        {
            for (int nInd = 0; nInd < listNewCellInfo.size(); nInd++)
            {
                JCellInfo newCellInfo = listNewCellInfo.get(nInd);
                
                //Separa as CellInfos que já estão no BD.                                
                boolean bFound = false;
                
                for(int nInd2 = 0; nInd2 < listMACsCellInfos.size(); nInd2++)
                {                   
                    if(newCellInfo.getMAC().equals(listMACsCellInfos.get(nInd2)))
                    {
                        bFound = true;
                        
                        // se esta CellInfo já está no banco de dados, somente a informação de canal e SSID dela é atualizada
                        
                        //tabela que guarda os CellInfos
                        execute("UPDATE \"CellInfo\" SET \"Channel\"='" + newCellInfo.getChannel() + "', \"ESSID\"='" + newCellInfo.getESSID() + "' WHERE \"MAC\"='" + newCellInfo.getMAC() + "'");
                        //tabela que guarda a relação entre CellInfo e AP.
                        execute("INSERT INTO \"APInfo_CellInfo\" VALUES ('" + strAPMAC + "', '" + newCellInfo.getMAC() + "', '" + newCellInfo.getSignalLevel() + "', '" + newCellInfo.getQuality() + "')");
                        
                        break;
                    }
                }
                
                //se esta CellInfo ainda não está no banco de dados.
                if (!bFound)
                {
                    //tabela que guarda os CellInfos
                    execute("INSERT INTO \"CellInfo\" VALUES ('" + newCellInfo.getMAC() + "', '" + newCellInfo.getChannel() + "', '" + newCellInfo.getESSID() + "')");
                    //tabela que guarda a relação entre CellInfo e AP.
                    execute("INSERT INTO \"APInfo_CellInfo\" VALUES ('" + strAPMAC + "', '" + newCellInfo.getMAC() + "', '" + newCellInfo.getSignalLevel() + "', '" + newCellInfo.getQuality() + "')");
                }
            }
        }
        return true;
    }
    
    /**
    * Método que adiciona informação sobre stações associadas aos APs na variável m_hashSTAInfo.
    * A variável m_hashSTAInfo mapeia a lista de JSTAInfo provenientes do station dump de um AP com o seu MAC.
    * Ao final, as informações de STAInfo e as relações de associação com o AP são atualizadas no banco de dados através da função addStationDumpInfoToDB. 
    @param strAPMAC MAC do ponto de acesso que gerou o station dump.
    @param listSTAInfo Lista de JSTAInfos (estações associadas) provenientes do station dump do AP.
    @return Retorna true se a operação ocorreu com sucesso ou false, caso contrário.
    */     
    public static boolean addSTAInfo(String strAPMAC, ArrayList<JSTAInfo> listSTAInfo)
    {
        // pega a lista atual de estações associadas ao AP
        ArrayList<JSTAInfo> listSTAInfoOld = m_hashSTAInfo.get(strAPMAC);
   
        if(listSTAInfoOld != null)
        {
            //Se a lista contém estações, apaga do banco de dados a relação de estações associadas a este AP.
            clearSTAsAssociatedWithAP(strAPMAC);
            // Apaga do banco de dados as estações contidas na lista.
            clearSTAs(listSTAInfoOld);
        }
            
        //Atualiza o m_hashSTAInfo com a nova listagem de estações.
        setHashSTAInfo(strAPMAC, listSTAInfo);
        //Adiciona a nova listagem de STAs e suas relações de associação com o AP no banco de dados.
        addStationDumpInfoToDB(strAPMAC);
        
        return true;
    }
   
    /**
     * Método que retorna todos os clientes (STA) associados a um determinado AP.
     * @param strAPMAC MAC do AP em questão
     * @return Uma lista de JSTAInfo contendo objetos que representam os clientes.
     */
    public static ArrayList<JSTAInfo> getSTAList(String strAPMAC)
    {
        ArrayList<JSTAInfo> listSTA = new ArrayList<JSTAInfo>();
                
        try
        {
            ResultSet rs = executeQuery("SELECT \"MAC_STA\" FROM \"APInfo_STAInfo\" WHERE \"MAC_AP\" = '" + strAPMAC.toUpperCase() + "'" );
            while (rs.next())  
            {              
                listSTA.add(new JSTAInfo(rs.getString(1)));
            }
            
            return listSTA; 
        }

        catch (SQLException ex)
        {
            Logger.getLogger(JDataManagement.class.getName()).log(Level.ERROR, null, ex);
            return null;
        }
                
    }
        
    /**
    * Método que adiciona informação de station dump (estações associadas aos APs) no banco de dados e faz a relação entre os STAInfos e o AP ao qual estão associados.
    * 
    @param strAPMAC MAC do ponto de acesso que realizou o station dump.
    *      
    @return Retorna true se a operação ocorreu com sucesso ou false, caso contrário.
    */     
    private static boolean addStationDumpInfoToDB(String strAPMAC)
    {
        // obtém a lista de estações associadas ao ponto de acesso.
        ArrayList<JSTAInfo> listSTAInfo = m_hashSTAInfo.get(strAPMAC);

        if (listSTAInfo != null)
        {
            for (int nInd = 0; nInd < listSTAInfo.size(); nInd++)
            {
                JSTAInfo staInfo = listSTAInfo.get(nInd);
                // tabela que guarda os STAInfos.
                execute("INSERT INTO \"STAInfo\" VALUES ('" + staInfo.getMAC() + "')");
                // tabela que guarda a relação de associação entre STAInfo e AP.
                execute("INSERT INTO \"APInfo_STAInfo\" VALUES ('" + strAPMAC + "', '" + staInfo.getMAC() + "')");
            }
        }

        return true;
    }

    /**
    * Método que recebe uma nova lista de CellInfos e a antiga lista, e gera uma nova lista atualizada.
    * A atualização é feita da seguinte forma:
    * Novas CellInfos são adicionadas na lista atualizada.
    * CellInfos antigas presentes na lista nova têm sua potência de sinal e qualidade ponderadas antes de serem adicionadas na lista atualizada.
    * CellInfos que possuem qualidade zero não são inseridas na lista atualizada.
    * 
    @param listCellInfoOld Lista de CellInfos antes de ocorrer a atualização com a nova informação de scan do AP.
    * 
    @param listCellInfoNew Nova lista de CellInfos obtida a partir de scan pelo AP.
    *    
    @return Retorna a lista de Cellinfo do AP atualizada.
    */     
    protected static ArrayList<JCellInfo> updateCellInfoList(ArrayList<JCellInfo> listCellInfoOld, ArrayList<JCellInfo> listCellInfoNew)
    {
        ArrayList<JCellInfo> listMerge = new ArrayList<JCellInfo>();
        boolean bFound;
        // constantes de ponderação
        String strTemp = JDataManagement.getPropertyValue("Alfa");
        Double fAlfa = Double.valueOf(strTemp) / 100.0f;;
        
        Double fBeta = 1.0 - fAlfa;
        
        for (int nInd = 0; nInd < listCellInfoOld.size(); nInd++)
        {
            JCellInfo cellInfoOld = listCellInfoOld.get(nInd);

            bFound = false;

            for (int nInd2 = 0; nInd2 < listCellInfoNew.size(); nInd2++)
            {
                JCellInfo cellInfoNew = listCellInfoNew.get(nInd2);
                // se uma das CellInfo antigas também está presente na nova lista de CellInfos,
                if (cellInfoOld.getMAC().equals(cellInfoNew.getMAC()))
                {
                    // sua nova qualidade ponderada é somada à antiga, também ponderada.
                    // Alfa pondera a antiga qualidade e Beta pondera a nova qualidade.
                    // A ponderação evita que variações bruscas na qualidade ocorram.
                    int nAvgQuality = (int) ((cellInfoOld.getQuality() * fAlfa) + (cellInfoNew.getQuality() * fBeta));
                    // conversão do sinal de dBm para mW.
                    double dOldmW = Math.pow(10.0, (cellInfoOld.getSignalLevel() / 10.0));
                    double dNewmW = Math.pow(10.0, (cellInfoNew.getSignalLevel() / 10.0));
                    // a potência do sinal também é ponderada.
                    // Alfa pondera o antigo valor de sinal e Beta, o novo.
                    double dAvgSignal = 10 * Math.log10((dOldmW * fAlfa) + (dNewmW * fBeta));
                    // o CellInfo com sinal e qualidade ponderados é adicionado à lista de retorno.
                    listMerge.add(new JCellInfo(cellInfoNew.getMAC(), cellInfoNew.getChannel(), dAvgSignal, nAvgQuality, cellInfoNew.getESSID()));
                     
                    bFound = true;

                    break;
                }
            }
            // se a CellInfo antiga não está presente na nova lista de CellInfos, 
            if (!bFound)
            {
                //sua qualidade é ponderada como se a nova qualidade fosse igual a zero.
                // isso evita que um CellInfo que não foi encontrado no scan atual seja eliminado da lista repentinamente, tendo em vista que o motivo da desaparição pode ser momentâneo.
                int nAvgQuality = (int) (cellInfoOld.getQuality() * fAlfa);
                // conversão do sinal de dBm para mW.
                double dOldmW = Math.pow(10.0, (cellInfoOld.getSignalLevel() / 10.0));
                //a potência do sinal é ponderada.
                double dAvgSignal = 10 * Math.log10(dOldmW * fAlfa);
                // adiciona o CellInfo à lista de retorno.
                listMerge.add(new JCellInfo(cellInfoOld.getMAC(), cellInfoOld.getChannel(), dAvgSignal, nAvgQuality, cellInfoOld.getESSID()));
            }
        }

        for (int nInd = 0; nInd < listCellInfoNew.size(); nInd++)
        {
            JCellInfo cellInfoNew = listCellInfoNew.get(nInd);

            bFound = false;

            for (int nInd2 = 0; nInd2 < listCellInfoOld.size(); nInd2++)
            {
                // se o item da lista de CellInfos nova está presente na antiga lista,
                if (cellInfoNew.getMAC().equals(listCellInfoOld.get(nInd2).getMAC()))
                {
                    bFound = true;
                }
            }
            // se o item da lista de CellInfos nova não está presente na antiga lista,
            if (!bFound)
            {
                // adiciona na lista de retorno o item presente na nova lista, mas não na antiga.
                listMerge.add(cellInfoNew);
            }
        }
        // remove CellInfos cuja a qualidade é zero.
        removeUnreachableAPs(listMerge);

        return listMerge;
    }

    /**
    * Método que remove CellInfos com qualidade nula de uma determinada lista.
    * 
    @param listMerge Lista em que CellInfos com qualidade zero serão procuradas.
    *     
    */      
    protected static void removeUnreachableAPs(ArrayList<JCellInfo> listMerge)
    {
        int nInd = 0;

        while (nInd < listMerge.size())
        {
            // se a qualidade do Cellinfo for igual a zero
            if (listMerge.get(nInd).getQuality() == 0)
            {
                // o elemento é removido.
                listMerge.remove(nInd);
            }
            else
            {
                nInd++;
            }
        }
    }

    /**
    * Método que busca no banco de dados a lista de APs habilitados em uma determinada região de controle.
    @return Retorna uma lista contendo os JAPInfos representando os APs da rede controlada, com seus parâmetros preenchidos (IP, MAC, localização...).
    */     
    public static ArrayList<JAPInfo> loadAPList()
    {
        ArrayList<JAPInfo> listAP = new ArrayList<JAPInfo>();

        ResultSet setAP = executeQuery("SELECT * FROM \"APInfo\" WHERE \"Region\" = '" + Main.getRegionId() + "' AND \"Enabled\"=1");

        if (setAP != null)
        {
            try
            {
                while (setAP.next())
                {                    
                    String strMAC = setAP.getString(APINFO_MAC_COL);
                    String strIP = setAP.getString(APINFO_IP_COL);
                    String strLocation = setAP.getString(APINFO_LOCATION_COL);
                    Integer arrayTxPower[] = (Integer[]) setAP.getArray(APINFO_TXPOWER_LIST_COL).getArray();
                    Integer nRegion = setAP.getInt(APINFO_REGION_COL);
                    Integer nUnderloadLimit = setAP.getInt(APINFO_UNDERLOAD_LIMIT_COL);
                    Integer nOverloadLimit = setAP.getInt(APINFO_OVERLOAD_LIMIT_COL);
                    Integer nCurrentTxPower = setAP.getInt(APINFO_CURRENT_TXPOWER_COL);
                    Integer nCurrentChannel = setAP.getInt(APINFO_CHANNEL_COL);
                    Integer nReachable = setAP.getInt(APINFO_REACHABLE_COL);
                    Integer nEnabled = setAP.getInt(APINFO_ENABLED_COL);
                    Integer nEmailSent = setAP.getInt(APINFO_EMAILSENT_COL);
                    
                    ArrayList<Integer> listTxPower = new ArrayList<Integer>();
                    listTxPower.addAll(Arrays.asList(arrayTxPower));

                    listAP.add(new JAPInfo(strIP, strMAC, listTxPower, strLocation, nUnderloadLimit, nOverloadLimit, nCurrentChannel, nCurrentTxPower, nRegion, nReachable, nEnabled, nEmailSent));
                }
            }
            catch (SQLException ex)
            {
                Logger.getLogger(JDataManagement.class.getName()).log(Level.ERROR, null, ex);
            }
        }

        return listAP;
    }
    
    /**
    * Método que busca no banco de dados a lista de todos APs habilitados e desabilitados de uma dada região de controle.
    @return Retorna uma lista contendo os JAPInfos representando os APs da rede controlada, com seus parâmetros preenchidos (IP, MAC, localização...).
    */     
//    public static ArrayList<JAPInfo> loadAllAPList()
//    {
//        ArrayList<JAPInfo> listAP = new ArrayList<JAPInfo>();
//
//        ResultSet setAP = executeQuery("SELECT * FROM \"APInfo\" WHERE \"Region\" = '" + Main.getRegionId() + "'");
//
//        if (setAP != null)
//        {
//            try
//            {
//                while (setAP.next())
//                {                    
//                    String strMAC = setAP.getString(APINFO_MAC_COL);
//                    String strIP = setAP.getString(APINFO_IP_COL);
//                    String strLocation = setAP.getString(APINFO_LOCATION_COL);
//                    Integer arrayTxPower[] = (Integer[]) setAP.getArray(APINFO_TXPOWER_LIST_COL).getArray();
//                    Integer nRegion = setAP.getInt(APINFO_REGION_COL);
//                    Integer nUnderloadLimit = setAP.getInt(APINFO_UNDERLOAD_LIMIT_COL);
//                    Integer nOverloadLimit = setAP.getInt(APINFO_OVERLOAD_LIMIT_COL);
//                    Integer nCurrentTxPower = setAP.getInt(APINFO_CURRENT_TXPOWER_COL);
//                    Integer nCurrentChannel = setAP.getInt(APINFO_CHANNEL_COL);
//                    Integer nReachable = setAP.getInt(APINFO_REACHABLE_COL);
//                    Integer nEnabled = setAP.getInt(APINFO_ENABLED_COL);
//                    Integer nEmailSent = setAP.getInt(APINFO_EMAILSENT_COL);
//                    
//                    ArrayList<Integer> listTxPower = new ArrayList<Integer>();
//                    listTxPower.addAll(Arrays.asList(arrayTxPower));
//
//                    listAP.add(new JAPInfo(strIP, strMAC, listTxPower, strLocation, nUnderloadLimit, nOverloadLimit, nCurrentChannel, nCurrentTxPower, nRegion, nReachable, nEnabled, nEmailSent));
//                }
//            }
//            catch (SQLException ex)
//            {
//                Logger.getLogger(JDataManagement.class.getName()).log(Level.ERROR, null, ex);
//            }
//        }
//
//        return listAP;
//    }
    
    /**
    * Método que busca no banco de dados a lista de todos APs incomunicantes desta região
    @return Retorna uma lista contendo os JAPInfos representando os APs incomunicantes da rede controlada, com seus parâmetros preenchidos (IP, MAC, localização...).
    */     
    public static ArrayList<JAPInfo> loadAllAPListUnreachables()
    {
        ArrayList<JAPInfo> listAP = new ArrayList<JAPInfo>();

        ResultSet setAP = executeQuery("SELECT * FROM \"APInfo\" WHERE \"Region\" = '" + Main.getRegionId() + "' AND \"Enabled\"=1 AND \"Reachable\"=0");

        if (setAP != null)
        {
            try
            {
                while (setAP.next())
                {                    
                    String strMAC = setAP.getString(APINFO_MAC_COL);
                    String strIP = setAP.getString(APINFO_IP_COL);
                    String strLocation = setAP.getString(APINFO_LOCATION_COL);
                    Integer arrayTxPower[] = (Integer[]) setAP.getArray(APINFO_TXPOWER_LIST_COL).getArray();
                    Integer nRegion = setAP.getInt(APINFO_REGION_COL);
                    Integer nUnderloadLimit = setAP.getInt(APINFO_UNDERLOAD_LIMIT_COL);
                    Integer nOverloadLimit = setAP.getInt(APINFO_OVERLOAD_LIMIT_COL);
                    Integer nCurrentTxPower = setAP.getInt(APINFO_CURRENT_TXPOWER_COL);
                    Integer nCurrentChannel = setAP.getInt(APINFO_CHANNEL_COL);
                    Integer nReachable = setAP.getInt(APINFO_REACHABLE_COL);
                    Integer nEnabled = setAP.getInt(APINFO_ENABLED_COL);
                    Integer nEmailSent = setAP.getInt(APINFO_EMAILSENT_COL);
                    
                    ArrayList<Integer> listTxPower = new ArrayList<Integer>();
                    listTxPower.addAll(Arrays.asList(arrayTxPower));

                    listAP.add(new JAPInfo(strIP, strMAC, listTxPower, strLocation, nUnderloadLimit, nOverloadLimit, nCurrentChannel, nCurrentTxPower, nRegion, nReachable, nEnabled, nEmailSent));
                }
            }
            catch (SQLException ex)
            {
                Logger.getLogger(JDataManagement.class.getName()).log(Level.ERROR, null, ex);
            }
        }

        return listAP;
    }
    
    /**
    * Método que busca no banco de dados a lista de todos APs incomunicantes
    @return Retorna uma lista contendo os JAPInfos representando os APs incomunicantes da rede controlada, com seus parâmetros preenchidos (IP, MAC, localização...).
    */     
//    public static ArrayList<JAPInfo> loadAllAPListToUpdateRegions()
//    {
//        ArrayList<JAPInfo> listAP = new ArrayList<JAPInfo>();
//
//        ResultSet setAP = executeQuery("SELECT * FROM \"APInfo\" WHERE \"Region\" = '" + Main.getRegionId() + "' AND \"UpdateRegion\"=1");
//
//        if (setAP != null)
//        {
//            try
//            {
//                while (setAP.next())
//                {                    
//                    String strMAC = setAP.getString(APINFO_MAC_COL);
//                    String strIP = setAP.getString(APINFO_IP_COL);
//                    String strLocation = setAP.getString(APINFO_LOCATION_COL);
//                    Integer arrayTxPower[] = (Integer[]) setAP.getArray(APINFO_TXPOWER_LIST_COL).getArray();
//                    Integer nRegion = setAP.getInt(APINFO_REGION_COL);
//                    Integer nUnderloadLimit = setAP.getInt(APINFO_UNDERLOAD_LIMIT_COL);
//                    Integer nOverloadLimit = setAP.getInt(APINFO_OVERLOAD_LIMIT_COL);
//                    Integer nCurrentTxPower = setAP.getInt(APINFO_CURRENT_TXPOWER_COL);
//                    Integer nCurrentChannel = setAP.getInt(APINFO_CHANNEL_COL);
//                    Integer nReachable = setAP.getInt(APINFO_REACHABLE_COL);
//                    Integer nEnabled = setAP.getInt(APINFO_ENABLED_COL);
//                    Integer nEmailSent = setAP.getInt(APINFO_EMAILSENT_COL);
//                    
//                    ArrayList<Integer> listTxPower = new ArrayList<Integer>();
//                    listTxPower.addAll(Arrays.asList(arrayTxPower));
//
//                    listAP.add(new JAPInfo(strIP, strMAC, listTxPower, strLocation, nUnderloadLimit, nOverloadLimit, nCurrentChannel, nCurrentTxPower, nRegion, nReachable, nEnabled, nEmailSent));
//                }
//            }
//            catch (SQLException ex)
//            {
//                Logger.getLogger(JDataManagement.class.getName()).log(Level.ERROR, null, ex);
//            }
//        }
//
//        return listAP;
//    }
    
    /**
    Método que busca no banco de dados informações de um determinado AP.
    @param MAC MAC do AP
    @return Retorna o JAPInfo que possui o determinado MAC, com seus parâmetros preenchidos (IP, MAC, localização...).
    */     
    public static JAPInfo loadAP(String MAC)
    {
        JAPInfo apInfo = null;

        ResultSet setAP = executeQuery("SELECT * FROM \"APInfo\" WHERE \"Region\" = '" + Main.getRegionId() + "' AND \"MAC\" = '" + MAC + "'");

        if (setAP != null)
        {
            try
            {
                if (setAP.next())
                {                    
                    String strMAC = setAP.getString(APINFO_MAC_COL);
                    String strIP = setAP.getString(APINFO_IP_COL);
                    String strLocation = setAP.getString(APINFO_LOCATION_COL);
                    Integer arrayTxPower[] = (Integer[]) setAP.getArray(APINFO_TXPOWER_LIST_COL).getArray();
                    Integer nRegion = setAP.getInt(APINFO_REGION_COL);
                    Integer nUnderloadLimit = setAP.getInt(APINFO_UNDERLOAD_LIMIT_COL);
                    Integer nOverloadLimit = setAP.getInt(APINFO_OVERLOAD_LIMIT_COL);
                    Integer nCurrentTxPower = setAP.getInt(APINFO_CURRENT_TXPOWER_COL);
                    Integer nCurrentChannel = setAP.getInt(APINFO_CHANNEL_COL);
                    Integer nReachable = setAP.getInt(APINFO_REACHABLE_COL);
                    Integer nEnabled = setAP.getInt(APINFO_ENABLED_COL);
                    Integer nEmailSent = setAP.getInt(APINFO_EMAILSENT_COL);
                    
                    ArrayList<Integer> listTxPower = new ArrayList<Integer>();
                    listTxPower.addAll(Arrays.asList(arrayTxPower));

                    apInfo = new JAPInfo(strIP, strMAC, listTxPower, strLocation, nUnderloadLimit, nOverloadLimit, nCurrentChannel, nCurrentTxPower, nRegion, nReachable, nEnabled, nEmailSent);
                }
            }
            catch (SQLException ex)
            {
                Logger.getLogger(JDataManagement.class.getName()).log(Level.ERROR, null, ex);
            }
        }

        return apInfo;
    }
        
    /**
    * Método que retorna a lista de CellInfos do ponto de acesso determinado pelo parâmetro strAPMAC.
    * 
    @param strAPMAC MAC do AP.
    *      
    @return Retorna a lista de CellInfos relativa ao AP. Caso a lista não exista, uma lista vazia é retornada.
    */     
    public static ArrayList<JCellInfo> getCellList(String strAPMAC)
    {
        if (m_hashCellInfo.containsKey(strAPMAC))
        {
            return m_hashCellInfo.get(strAPMAC);
        }

        return new ArrayList<JCellInfo>();
    }

    /**
    * Método que calcula o número de estações associadas ao ponto de acesso determinado pelo parâmetro srtAPMAC. 
    * 
    @param strAPMAC MAC do AP.
    *      
    @return Retorna o número de estações associadas ao ponto de acesso. Caso o AP não tenha associados, retorna -1.
    */     
    public static int getNumberOfSTAs(String strAPMAC)
    {
        if (m_hashSTAInfo.containsKey(strAPMAC))
        {
            return m_hashSTAInfo.get(strAPMAC).size();
        }

        return -1;
    }

    /**
    * Método que atualiza as informações de carga dos pontos de acesso no banco de dados.
    * 
    @param strAPMAC MAC do ponto de acesso.
    *
    @param nLoadInfo índice que representa o status de carga do AP (0 = carga baixa; 1 = balanceado; 2 = sobrecarregado)
    *           
    @return Retorna true se a operação ocorreu com sucesso ou false, caso contrário.
    */     
    public static boolean addLoadInfoToDB(String strAPMAC, int nLoadInfo)
    {
        String strSql = "UPDATE \"APInfo\" SET \"LoadStatus\"='" + nLoadInfo + "' WHERE \"MAC\"='" + strAPMAC + "'";

        return execute(strSql);
    }
    
    /**
    Método que atualiza o status do AP (habilitado para controle ou não).
    @param strAPMAC MAC do ponto de acesso.
    @param nEnabled  Status que o AP será atualizado.
    @return Retorna true se a operação ocorreu com sucesso ou false, caso contrário.
    */     
//    public static boolean updateEnabled(String strAPMAC, int nEnabled)
//    {
//        String strSql = "UPDATE \"APInfo\" SET \"Enabled\"=" + nEnabled + " WHERE \"MAC\"='" + strAPMAC + "'";
//        
//        boolean bResult = execute(strSql);
//        
//        if(bResult) {
//            Logger.getLogger(Main.CONNECTION_LOG).info(JLogger.getDateTime() + " " + JLogger.getTime() + " Updated AP_Enabled " + strAPMAC +" to " + nEnabled);
//        }
//
//        return bResult;
//    }
    
    /**
    Método que atualiza o campo EmailSent na APInfo. Este campo, informa se já foi enviado email de AP incomunicante no dia atual.
    @param strAPMAC MAC do ponto de acesso.
    @return Retorna true se a operação ocorreu com sucesso ou false, caso contrário.
    */     
    public static boolean updateEmailSent(String strAPMAC)
    {
        String strSql = "UPDATE \"APInfo\" SET \"EmailSent\" = 1 WHERE \"MAC\"='" + strAPMAC + "'";
        
        boolean bResult = execute(strSql);

        return bResult;
    }
    
    /**
    Método que zera o campo EmailSent de todos APs na APInfo.
    @param strAPMAC MAC do ponto de acesso.
    @return Retorna true se a operação ocorreu com sucesso ou false, caso contrário.
    */     
    public static boolean updateRestartEmailSent()
    {
        String strSql = "UPDATE \"APInfo\" SET \"EmailSent\" = 0";
        
        boolean bResult = execute(strSql);

        return bResult;
    }
    
//    /**
//    Método que zera o campo temporario UpdateRegion de todos APs na APInfo.
//    @param strAPMAC MAC do ponto de acesso.
//    @return Retorna true se a operação ocorreu com sucesso ou false, caso contrário.
//    */     
//    public static boolean resetRestartUpdateRegion()
//    {
//        String strSql = "UPDATE \"APInfo\" SET \"UpdateRegion\"=0";
//        
//        boolean bResult = execute(strSql);
//
//        return bResult;
//    }

    /**
    Método que atualiza o status de conexão com o AP no banco de dados.
    @param strAPMAC MAC do ponto de acesso.
    @param nReachableInfo  índice que representa o status de conexão do AP (0 = sem conexão; 1 = com conexão)
    @return Retorna true se a operação ocorreu com sucesso ou false, caso contrário.
    */     
    public static boolean addReachableInfoToDB(String strAPMAC, int nReachableInfo)
    {
        String strSql = "UPDATE \"APInfo\" SET \"Reachable\"='" + nReachableInfo + "' WHERE \"MAC\"='" + strAPMAC + "'";

        return execute(strSql);
    }
    
    /**
    * Método que busca no banco de dados o valor de uma propriedade determinada pelo parâmetro strProperty.
    * 
    @param strProperty Propriedade da qual deseja-se saber saber o valor.
    *           
    @return Retorna o valor da propriedade dada pelo parâmetro strProperty. Retorna NULL caso a operação não ocorra com sucesso.
    */    
    public static String getPropertyValue(String strProperty)
    {
        try
        {
            ResultSet rs = executeQuery("SELECT \"Value\" FROM \"Config\" WHERE \"Property\" = '" + strProperty + "'");
            rs.next();
            return rs.getString(1);
        }

        catch (SQLException ex)
        {
            Logger.getLogger(JDataManagement.class.getName()).log(Level.ERROR, null, ex);
        }

        return null;
    }

    /**
    * Método que busca no banco de dados todos os MACs das CellInfos.
    *             
    @return Retorna um array de Strings com os MACs de todas as CellInfos
    */    
    public static ArrayList<String> getCellInfosMACs()
    {
        try
        {
            ArrayList<String> cellInfosMacs = new ArrayList<String>();
            
            ResultSet rs = executeQuery("SELECT * FROM \"CellInfo\" ");
            if (rs != null)
            {
                //rs.next posiciona o cursor para leitura da primeira linha, e assim por diante
                //quando não existem mais linhas para serem lidas, rs.next retorna false
                while (rs.next())  
                {
                    cellInfosMacs.add(rs.getString(1));                    
                }
            }
            return cellInfosMacs;
        }

        catch (SQLException ex)
        {
            Logger.getLogger(JDataManagement.class.getName()).log(Level.ERROR, null, ex);
        }

        return null;
    }
    
    /**
    * Método que atualiza a informação de canal de um ponto de acesso no banco de dados.
    * 
    @param strAPMAC MAC do ponto de acesso.
    *
    @param nChannel Canal do ponto de acesso.
    *           
    @return Retorna true se a operação ocorreu com sucesso ou false, caso contrário.
    */    
    public static boolean updateChannel(String strAPMAC, int nChannel)
    {
        String strSql = "UPDATE \"APInfo\" SET \"Channel\"='" + nChannel + "' WHERE \"MAC\"='" + strAPMAC + "'";

        return execute(strSql);
    }

    /**
    * Método que atualiza a informação de potência de um ponto de acesso no banco de dados.
    * 
    @param strAPMAC MAC do ponto de acesso.
    *
    @param nTxPower Potência do ponto de acesso.
    *           
    @return Retorna true se a operação ocorreu com sucesso ou false, caso contrário.
    */        
    public static boolean updateTxPower(String strAPMAC, int nTxPower)
    {
        String strSql = "UPDATE \"APInfo\" SET \"CurrentTxPower\"='" + nTxPower + "' WHERE \"MAC\"='" + strAPMAC + "'";

        return execute(strSql);
    }
    
    /**
    * Método que busca no banco todas as regiões existentes.
    *          
    @return Retorna a lista de regiões.
    */        
    public static ArrayList<JRegion> loadRegions()
    {
        ArrayList<JRegion> listRegion = new ArrayList<JRegion>();

        ResultSet setRegion = executeQuery("SELECT * FROM \"Region\"");

        if (setRegion != null)
        {
            try
            {
                while (setRegion.next())
                {
                    Integer nID = setRegion.getInt(REGION_ID_COL);
                    String strDesc = setRegion.getString(REGION_DESCRIPTION_COL);
                    
                    listRegion.add(new JRegion(nID, strDesc));
                }
            }
            catch (SQLException ex)
            {
                Logger.getLogger(JDataManagement.class.getName()).log(Level.ERROR, null, ex);
            }
        }

        return listRegion;
    }
    
    /**
    * Método que busca no banco as informações de uma região com base em seu número de identificação.
     *@param nRegionId Número de identificação da região
    *          
    @return Retorna o objeto que representa a região com a ID requerida.
    */        
    public static JRegion getRegionById(Integer nRegionId)
    {
        ResultSet setRegion = executeQuery("SELECT * FROM \"Region\" WHERE \"ID\" = '" + nRegionId + "'");
        JRegion region = null;
        
        if (setRegion != null)
        {
            try
            {
                while (setRegion.next())
                {
                    Integer nID = setRegion.getInt(REGION_ID_COL);
                    String strDesc = setRegion.getString(REGION_DESCRIPTION_COL);
                    
                    region = new JRegion(nID, strDesc);
                }
            }
            catch (SQLException ex)
            {
                Logger.getLogger(JDataManagement.class.getName()).log(Level.ERROR, null, ex);
            }
        }

        return region;
    }


}
