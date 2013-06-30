/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package log;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.SimpleLayout;

/**
 *
 * @author ferolim
 */
public class JLogger
{
    /**
     * Método que cria um arquivo de log.
     * @param strId Id do Log.
     * @param strFileName Nome do arquivo do Log.
     * @return Retorna true se o arquivo do log foi criado com sucesso.
     */
    public static boolean createLog(String strId, String strFileName, String strRegionName)
    {
        try
        {
            Logger logger = Logger.getLogger(strId);
            BasicConfigurator.configure();

            if(!strRegionName.isEmpty())
            {
                strRegionName = "_" + strRegionName;
            }
            
            FileAppender fileAppender = new FileAppender(new SimpleLayout(), strFileName + strRegionName + "_" + getDateTime() + ".log");

            logger.addAppender(fileAppender);
        }
        catch (IOException ex)
        {
            return false;
        }

        return true;
    }

    /**
     * Método para obtenção da data (yyyy-MM-dd). Esta informação será utilizada para identificar o dia da geração dos arquivos de log.
     * @return Retorna uma string com a data no formato ano-mês-dia.
     */
    public static String getDateTime()
    {
        // formato da data
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        // a data é preenchida pelo sistema
        Date date = new Date();
        // formata a data
        return dateFormat.format(date);
    }
    
     /**
     * Método para obtenção da hora (HH:mm:ss.SSS). Esta informação será utilizada para identificar a hora em que o controlador executou suas tarefas no log.
     * @return Retorna uma string com a hora no formato hora-minuto-segundos-milisegundos.
     */
    public static String getTime()
    {
        // formato da data
        DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss.SSS");
        // a data, hora é preenchida pelo sistema
        Date date = new Date();
        // formata a data
        return timeFormat.format(date);
    }

    public static boolean createLog(String strId, String strFileName)
    {
        return createLog(strId, strFileName, "");
    }
}
