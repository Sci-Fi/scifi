/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package loader;

import database.JDataManagement;
import data.JRegion;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TimerTask;
import log.JLogger;
import org.apache.log4j.Logger;

/**
 *Classe responsável por analisar se todas as regiões possuem uma instância associada, ou, caso alguma região tenha sido removida
 * remover as instâncias correspondentes
 * @author ferolim
 */
public class JProcessTimerTask extends TimerTask
{
    public class JControllerProcess
    {
        public JRegion region;
        public Process process;
        public Boolean active;
        public JStreamGobbler error;
        public JStreamGobbler output;
        
        public void destroy()
        {
            process.destroy();
            process = null;
            
            error.interrupt();
            error = null;
            
            output.interrupt();
            output = null;
        }
    }
    
    protected String m_strExternalIP;
    protected String m_strInternalIP;
    protected Integer m_nPort;
    
    public class JCleaner extends Thread
    {
        @Override
        public void run()
        {            
            //percorre todas as instâncias destruindo-as
            Iterator<JControllerProcess> iterator = m_mapProcess.values().iterator();
         
            JControllerProcess curProcess;

            while(iterator.hasNext())
            {
                curProcess = iterator.next();
                
                curProcess.destroy();
            }
        }
    }
    
    protected static Map<Integer, JControllerProcess> m_mapProcess = new HashMap<Integer, JControllerProcess>();
    
    protected Process startControllerProcess(Integer nRegionID) throws IOException
    {
        if (m_strInternalIP != null)
        {
            //se o controlador possui 2 interfaces de rede, inicia a instância do controlador passando os quatro args
            ProcessBuilder builder = new ProcessBuilder("java", "-cp", "APController.jar", "apcontroller.Main", String.valueOf(nRegionID), m_strExternalIP, m_strInternalIP, String.valueOf(m_nPort));
            builder.redirectErrorStream(true);
            return builder.start();
        }
        else
        {
           //se o controlador possui 1 interface de rede, inicia a instância do controlador passando três args
            ProcessBuilder builder = new ProcessBuilder("java", "-cp", "APController.jar", "apcontroller.Main", String.valueOf(nRegionID), m_strExternalIP, String.valueOf(m_nPort));
            builder.redirectErrorStream(true);
            return builder.start();         
        }
    }
    
    public void setParameters(String strExternalIP, String strInternalIP, Integer nPort)
    {
        m_strExternalIP = strExternalIP;
        m_strInternalIP = strInternalIP;
        m_nPort = nPort;
    }
    
    @Override
    public void run()
    {
        //carrega a lista de regiões
        ArrayList<JRegion> listRegion = JDataManagement.loadRegions();

        JControllerProcess process = null;

        Iterator<JControllerProcess> iterator = m_mapProcess.values().iterator();

        JControllerProcess curProcess;

        //inicialmente marca todas como inativas
        while(iterator.hasNext())
        {
            curProcess = iterator.next();
            curProcess.active = false;
        }

        for(int nInd = 0; nInd < listRegion.size(); nInd++)
        {
            JRegion region = listRegion.get(nInd);
            
            //busca o processo correspondente à região
            process = m_mapProcess.get(region.getID());

            if(process == null)
            {
                //caso não exista cria-se uma instância do controlador
                process = new JControllerProcess();

                process.region = region;

                try 
                {
                    process.process = startControllerProcess(process.region.getID());
                } 
                catch (IOException ex)
                {
                    Logger.getLogger(JLoader.LOADER_LOG).error(JLogger.getDateTime() + " " + JLogger.getTime() + " Could not create process for region: " + region.getDescription());
                    break;
                }
                
                if(process.process != null)
                {

                    //redirecionando as saídas
                    process.error = new JStreamGobbler(process.process.getErrorStream(), "ERROR");
                    
                    process.error.start();
                    
                    process.output = new JStreamGobbler(process.process.getInputStream(), "OUTPUT");
                    
                    process.output.start();
                    
                    process.active = true;

                    m_mapProcess.put(process.region.getID(), process);
                
                    Logger.getLogger(JLoader.LOADER_LOG).info(JLogger.getDateTime() + " " + JLogger.getTime() + " Created process for region: " + region.getDescription());
                }
            }
            else
            {
                //se já existe, marca-se a instância como ativa
                process.active = true;
            }
        }

        ArrayList<Integer> listIDsToRemove = new ArrayList<Integer>();

        iterator = m_mapProcess.values().iterator();

        //percorre todos os processos
        while(iterator.hasNext())
        {
            curProcess = iterator.next();
            
            if(!curProcess.active)
            {
                //caso não esteja ativo (região removida), o processo é destruído...
                curProcess.destroy();

                listIDsToRemove.add(curProcess.region.getID());
                
                Logger.getLogger(JLoader.LOADER_LOG).info(JLogger.getDateTime() + " " + JLogger.getTime() + " Removed process for region: " + curProcess.region.getDescription());
            }
        }

        //... e removido da lista
        for(int nInd = 0; nInd < listIDsToRemove.size(); nInd++)
        {
            m_mapProcess.remove(listIDsToRemove.get(nInd));
        }
    }
    
}
