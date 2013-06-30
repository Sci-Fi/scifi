/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package loadBalance;

import apcontroller.Main;
import data.JAPInfo;
import database.JDataManagement;
import java.util.ArrayList;
import log.JLogger;
import org.apache.log4j.Logger;

/**
 * Esta classe é responsável por definir e atualizar no banco de dados o status de carga dos pontos de acesso controlados.
 * @author ferolim
 */
public class JLoadBalance
{
    // no banco de dados, os números 0, 1 e 2 representam o nível de carga do ponto de acesso como definido a seguir:
    private static final int UNDERLOAD_INDEX = 0;
    private static final int BALANCED_INDEX = 1;
    private static final int OVERLOAD_INDEX = 2;

    
    /**
    * O objetivo deste método é definir e atualizar no banco de dados, através da função defineLoadStatus, a informação de carga de cada ponto de acesso controlado.
    
    @return Retorna true se a atualização ocorreu com sucesso ou falso, caso contrário.
    */    
    public static boolean updateLoadStatus()
    {
        ArrayList<JAPInfo> listAP = JDataManagement.loadAPList();

        for (int nInd=0; nInd < listAP.size(); nInd++)
        {
            // define o status de carga para cada ponto de acesso controlado.
            // se ocorrer erro, retorna false.
            if (!defineLoadStatus(listAP.get(nInd)))
            {
                return false;
            }
        }
  
        return true;
    }
    
   
    /**
    * O objetivo deste método é definir o status de carga do ponto de acesso dado pelo parâmetro apInfo e inserir seu status no banco de dados. 

    @return Retorna true se a inserção ocorreu com sucesso ou falso, caso contrário.
    */      
    protected static boolean defineLoadStatus(JAPInfo apInfo)
    {
        
        int nStatus = OVERLOAD_INDEX;
        // se o número de clientes associados ao AP é menor do que o limite de carga baixa, 
        if (JDataManagement.getNumberOfSTAs(apInfo.getMAC()) < apInfo.getUnderloadLimit())
        {
            // o status do AP é de carga baixa.
            nStatus = UNDERLOAD_INDEX;
        }
        else
        {
            // Se o número de associados é maior do que o limite de carga baixa, e menor do que o limite de carga média,
            if (JDataManagement.getNumberOfSTAs(apInfo.getMAC()) < apInfo.getOverloadLimit())
            {
                // o status do AP é balanceado.
                nStatus = BALANCED_INDEX;
            }
        }
        // se o AP não se enquadrar em carga baixa ou balanceado, seu status inicial (sobrecarregado) é mantido.
        // adiciona o status de carga no banco de dados, retornando false se houver erro.
        return JDataManagement.addLoadInfoToDB(apInfo.getMAC(), nStatus);
    }
}
