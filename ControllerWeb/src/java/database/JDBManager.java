/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package database;

import java.sql.Statement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

/**
 * Classe que cuida da conexão com o Servidor de Aplicação e operações no banco de dados.
 * O Servidor de Aplicação disponibiliza algumas conexões que podem ser utilizadas para comunicação com o banco de dados.
 * A aplicação deve se conectar ao Servidor para obter e modificar informações no banco de dados.
 * @author Felipe Rolim
 */
public class JDBManager
{    
    /**
     * Método que pega uma das conexões do pool do Servidor de Aplicação. No caso do SciFi, o Jboss é utilizado como Servidor de Aplicação.
     * @return Retorna a conexão com o Servidor de Aplicação.
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
        // se falhar para pegar a conexão na primeira tentativa, tentar novamente.
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
     * Método que libera a conexão com o Servidor de Aplicação.
     * @param connection A conexão que será liberada.
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
     * Método que fecha o conjunto de resultados de uma requisição ao banco de dados.
     * É necessário fechá-lo antes de finalizar uma conexão.
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
     * Método que fecha o Statement criado para realizar uma operação no banco de dados.
     * É necessário fechá-lo antes de finalizar uma conexão.
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
     * Método que executa uma operação no banco de dados.
     * @param connection Conexão que será utilizada para comunicação com o banco de dados.
     * @param strSQl Comando a ser executado.
     * @return Retorna true se a execução ocorreu com sucesso ou false, caso contrário.
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
     * Método que executa uma consulta ao banco de dados.
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
}
