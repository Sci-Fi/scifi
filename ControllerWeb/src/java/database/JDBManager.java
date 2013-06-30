/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package database;

import java.sql.Statement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

/**
 * Classe que cuida da conex�o com o Servidor de Aplica��o e opera��es no banco de dados.
 * O Servidor de Aplica��o disponibiliza algumas conex�es que podem ser utilizadas para comunica��o com o banco de dados.
 * A aplica��o deve se conectar ao Servidor para obter e modificar informa��es no banco de dados.
 * @author Felipe Rolim
 */
public class JDBManager
{    
    /**
     * M�todo que pega uma das conex�es do pool do Servidor de Aplica��o. No caso do SciFi, o Jboss � utilizado como Servidor de Aplica��o.
     * @return Retorna a conex�o com o Servidor de Aplica��o.
     */
    protected static Connection getConnection()
    {
        Connection connection = null;
        
        try 
        {
            Context ic = new InitialContext();
            DataSource dataSource = (DataSource) ic.lookup("java:comp/env/jdbc/ControllerDB");
            connection = dataSource.getConnection();
        } 
        
        catch (SQLException ex)
        {
            Logger.getLogger(JDBManager.class.getName()).log(Level.SEVERE, null, ex);
        }        
        catch (NamingException ex)
        {
            Logger.getLogger(JDBManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        // se falhar para pegar a conex�o na primeira tentativa, tentar novamente.
        if(connection==null)
        {
            try 
            {
                Context ic = new InitialContext();
                DataSource dataSource = (DataSource) ic.lookup("java:comp/env/jdbc/ControllerDB");
                connection = dataSource.getConnection();
            } 
            
            catch (SQLException ex) 
            {
                Logger.getLogger(JDBManager.class.getName()).log(Level.SEVERE, null, ex);
            } 
            
            catch (NamingException ex) 
            {
                Logger.getLogger(JDBManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        return connection;
    }
    /**
     * M�todo que libera a conex�o com o Servidor de Aplica��o.
     * @param connection A conex�o que ser� liberada.
     */
    protected static void closeConnection(Connection connection)
    {
        if(connection != null)
        {
            try 
            {
                connection.close();
            } 
            catch (SQLException ex)
            {
                System.out.println("Could not close connection");
            }
        }
    }
    /**
     * M�todo que fecha o conjunto de resultados de uma requisi��o ao banco de dados.
     * � necess�rio fech�-lo antes de finalizar uma conex�o.
     * @param rs Result Set a ser finalizado.
     */
    protected static void closeResultSet(ResultSet rs)
    {
        if(rs != null)
        {
            try 
            {
                rs.close();
            } 
            catch (SQLException ex)
            {
                System.out.println("Could not close the ResultSet");
            }
        }
    }
    /**
     * M�todo que fecha o Statement criado para realizar uma opera��o no banco de dados.
     * � necess�rio fech�-lo antes de finalizar uma conex�o.
     * @param statement Statement a ser finalizado.
     */
    protected static void closeStatement(Statement statement)
    {
        if(statement != null)
        {
            try 
            {
                statement.close();
            } 
            catch (SQLException ex)
            {
                System.out.println("Could not close the Statement");
            }
        }
    }

    /**
     * M�todo que fecha o PreparedStatement criado para realizar uma opera��o no banco de dados.
     * � necess�rio fech�-lo antes de finalizar uma conex�o.
     * @param statement Statement a ser finalizado.
     */
    protected static void closePreparedStatement(PreparedStatement pstatement)
    {
        if(pstatement != null)
        {
            try
            {
                pstatement.close();
            }
            catch (SQLException ex)
            {
                System.out.println("Could not close the Statement");
            }
        }
    }
    
    /**
     * M�todo que executa uma opera��o no banco de dados.
     * @param connection Conex�o que ser� utilizada para comunica��o com o banco de dados.
     * @param strSQl Comando a ser executado.
     * @return Retorna true se a execu��o ocorreu com sucesso ou false, caso contr�rio.
     */
    protected static boolean execute(Connection connection, String strSQl)
    {
        Statement sSql = null;
        
        Boolean bReturn = true;
        
        if(connection != null)
        {
            try
            {
                sSql = connection.createStatement();
                
                sSql.execute(strSQl);
                
                sSql.close();
            }
            catch (SQLException se)
            {
                System.out.println("Could not execute sql - " + se.toString());
                bReturn = false;
            }
        }
        else
        {
            bReturn = false;
        }
        
        return bReturn;
    }
    
    /**
     * M�todo que executa uma consulta ao banco de dados.
     * @param sSql Statement a ser utilizado para executar a consulta.
     * @param strSQl Consulta a ser executada.
     * @return Retorna o conjunto de resultados gerados pela consulta.
     */
    protected static ResultSet executeQuery(Statement sSql, String strSQl)
    {
        ResultSet rsReturn = null;
        
        if(sSql != null)
        {
            try
            {
                rsReturn = sSql.executeQuery(strSQl);
            }
            catch (SQLException se)
            {
                System.out.println("Could not execute sql - " + se.toString());
            }
        }
        
        return rsReturn;
    }

    /**
     * M�todo que executa uma opera��o no banco de dados com proteção contra SQL Injection (PreparedStatement).
     * @param connection Conex�o que ser� utilizada para comunica��o com o banco de dados.
     * @param strSQl Comando a ser executado.
     * @return Retorna true se a execu��o ocorreu com sucesso ou false, caso contr�rio.
     */
    protected static boolean safe_execute(Connection connection, String strSQl, PreparedStatement pSql)
    {
        Boolean bReturn = true;

        if(connection != null)
        {
            try
            {
                pSql.executeUpdate();

                pSql.close();
            }
            catch (SQLException se)
            {
                System.out.println("Could not execute sql - " + se.toString());
                bReturn = false;
            }
        }
        else
        {
            bReturn = false;
        }

        return bReturn;
    }

    /**
     * M�todo que executa uma consulta sem SQL Injection ao banco de dados.
     * @param sSql Statement a ser utilizado para executar a consulta.
     * @param strSQl Consulta a ser executada.
     * @return Retorna o conjunto de resultados gerados pela consulta.
     */
    protected static ResultSet safe_executeQuery(PreparedStatement pSql, String strSQl)
    {
        ResultSet rsReturn = null;

        if(pSql != null)
        {
            try
            {
                rsReturn = pSql.executeQuery();
            }
            catch (SQLException se)
            {
                System.out.println("Could not execute sql - " + se.toString());
            }
        }

        return rsReturn;
    }
}
