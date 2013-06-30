/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package loader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 *Classe que redireciona fluxos de saída para a saída padrão
 * @author ferolim
 */
public class JStreamGobbler extends Thread
{
    InputStream m_inputStream;
    String m_strType;

    JStreamGobbler(InputStream inputStream, String strType)
    {
        m_inputStream = inputStream;
        m_strType = strType;
    }

    @Override
    public void run()
    {
        try 
        {
            InputStreamReader isr = new InputStreamReader(m_inputStream);
            BufferedReader br = new BufferedReader(isr);
            String line = null;
            while ((line = br.readLine()) != null)
            {
                System.out.println(m_strType + ">" + line);
            }
        } 
        catch (IOException ioe)
        {
        }
    }
}
