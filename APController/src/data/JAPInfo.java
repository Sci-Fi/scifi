/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package data;

import apcontroller.JCommander;
import java.util.ArrayList;

/**
 * Esta classe representa o ponto de acesso.
 * Informações relativas ao ponto de acesso, como IP, MAC, Canal, Localização,
 * lista de possíveis potências de transmissão, potência de transmissão corrente e limites de carga do AP são guardados aqui.
 * @author Felipe Rolim
 */
public final class JAPInfo
{

    protected final static int DEFAULT_CHANNEL = 11;
    protected String m_strIP;
    protected String m_strMAC;
    protected int m_nChannel;
    protected int m_nRegion;
    protected String m_strLocalization;
    protected ArrayList<Integer> m_listTxPower = null;
    protected int m_nCurrentTxPowerIndex;
    private int m_nUnderloadLimit;
    private int m_nOverloadLimit;

    public JAPInfo(String strIP, String strMAC, ArrayList<Integer> listTxPower, String strLocalization, Integer nUnderloadLimit, Integer nOverloadLimit, Integer nCurrentChannel, Integer nCurrentTxPower, Integer nRegion)
    {
        m_strIP = strIP;
        m_strMAC = strMAC.toUpperCase();
        m_strLocalization = strLocalization;
        m_nRegion = nRegion;
        m_nUnderloadLimit = nUnderloadLimit;
        m_nOverloadLimit = nOverloadLimit;
        m_listTxPower = listTxPower;
        m_nChannel = nCurrentChannel;

        updateTxPowerIndex(nCurrentTxPower);
    }

   /**
    * O objetivo deste método é atualizar o índice que indica a potência com a qual o AP está operando (m_nCurrentTxPowerIndex).
    * Existe um lista de potências possíveis (m_listTxPower). Cada potência listada possui um índice.
     
   @param  nCurrentTxPower Potência atual do ponto de acesso.

    */     
    protected void updateTxPowerIndex(Integer nCurrentTxPower)
    {
        // Se o valor de potência estiver incorreto, a potência do AP é considerada máxima como proteção (índice máximo).
        if (nCurrentTxPower < 0)
        {
            m_nCurrentTxPowerIndex = m_listTxPower.size() - 1;
        }
        else
        {
            // varre a lista de potências possíveis atualizando o índice.
            // ao encontrar uma potência igual a do parâmetro de entrada, o índice é encontrado também e a função termina.
            for (m_nCurrentTxPowerIndex = 0; m_nCurrentTxPowerIndex < m_listTxPower.size(); m_nCurrentTxPowerIndex++)
            {
                if (nCurrentTxPower.equals(m_listTxPower.get(m_nCurrentTxPowerIndex)))
                {
                    return;
                }
            }
            // se a potência requerida não foi encontrada, o índice é atualizado para o máximo.
            m_nCurrentTxPowerIndex = m_listTxPower.size() - 1;
        }
    }

   /**
    * Lista de possíveis potências de transmissão do ponto de acesso.
    */    
    public ArrayList<Integer> getTxPowers()
    {
        return m_listTxPower;
    }

   /**
    * Endereço IP do ponto de acesso.
    */    
    public String getIP()
    {
        return m_strIP;
    }

    protected void setIP(String strIP)
    {
        m_strIP = strIP;
    }

   /**
    * Endereço MAC do ponto de acesso.
    */    
    public String getMAC()
    {
        return m_strMAC;
    }

    protected void setMAC(String strMAC)
    {
        m_strMAC = strMAC.toUpperCase();
    }

    
    /*
     * Região onde o AP está.
     */
    
    public int getRegion() {
        return m_nRegion;
    }

    public void setRegion(int m_nRegion) {
        this.m_nRegion = m_nRegion;
    }
    
   /**
    * O objetivo deste método é verificar se um AP com um determinado MAC se encontra em uma lista de APs.
     
   @param  strMAC  MAC do AP procurado.
     * 
   @param  listAP Lista de APs em que o AP com o MAC determinado no primeiro parâmetro será procurado.
     *   
   @return Retorna true se o AP com o MAC procurado (parâmetro 1) está na lista de de APs (parâmetro 2).
    */     
    public static boolean isMACOnTheList(String strMAC, ArrayList<JAPInfo> listAP)
    {
        strMAC = strMAC.toUpperCase();
        
        for (int nInd = 0; nInd < listAP.size(); nInd++)
        {
            // se o MAC do AP em questão é igual ao MAC do parâmetro de entrada, retorna true
            if (listAP.get(nInd).getMAC().equals(strMAC))
            {
                return true;
            }
        }

        return false;
    }

   /**
    * O objetivo deste método é verificar se um AP com um determinado MAC se encontra em uma lista
    * e, em caso positivo, retornar o JAPInfo relativo a este AP. 
     
   @param  strMAC  MAC do AP procurado.
     * 
   @param  listAP Lista de APs em que o AP com o MAC determinado no primeiro parâmetro será procurado.
     *   
   @return Retorna o AP que possui o MAC desejado (parâmetro 1) e está na lista de de APs (parâmetro 2).
    * Se o AP não for encontrado na lista, retorna NULL.
    */    
    public static JAPInfo getAPInfoByMAC(String strMAC, ArrayList<JAPInfo> listAP)
    {
        strMAC = strMAC.toUpperCase();
        
        for (int nInd = 0; nInd < listAP.size(); nInd++)
        {
            if (listAP.get(nInd).getMAC().equals(strMAC))
            {
                return listAP.get(nInd);
            }
        }

        return null;
    }

   /**
    * Verifica se dois APInfos são iguais, ou seja, representam o mesmo ponto de acesso. A igualdade é verificada através dos MACs dos APInfos comparados.
     
   @param  object APinfo que será usado para comparação.
     *    
   @return Retorna true se os APInfos possuem o mesmo MAC e false, caso contrário.
    */    
    @Override
    public boolean equals(Object object)
    {
        return m_strMAC.equals(((JAPInfo) object).getMAC());
    }
   
   /**
    * Canal do ponto de acesso.
    */    
    public int getChannel()
    {
        return m_nChannel;
    }

    public void setChannel(int nChannel)
    {
        if (JCommander.setChannel(this, nChannel))
        {
            m_nChannel = nChannel;
        }

    }

   /**
    * Potência atual do ponto de acesso.
    */    
    public int getPower()
    {
        return m_listTxPower.get(m_nCurrentTxPowerIndex);
    }

   /**
    * O objetivo deste método é incrementar a potência do ponto de acesso.
    * Cada AP possui uma lista com as possíveis potências ordenada.
    * Ao ser incrementada, a potência é aumentada para a equivalente ao próximo índice desta lista.
    *   
    */     
    public void incPower()
    {
        // se este não é o maior índice possíve,
        if (m_nCurrentTxPowerIndex + 1 < m_listTxPower.size())
        {
            // guarda o índice atual
            int nOldIndex = m_nCurrentTxPowerIndex;
            // incrementa o índice
            m_nCurrentTxPowerIndex++;
            //Atualiza o valor da potência do ponto de acesso no banco de dados.
            //Se houver erro na atualização, o índice anterior é restaurado.
            //Isso garante que a informação contida no banco de dados esteja de acordo com a do JAPInfo.
            if (!JCommander.setPower(this, getPower()))
            {
                m_nCurrentTxPowerIndex = nOldIndex;
            }

        }
    }

   /**
    * O objetivo deste método é decrementar a potência do ponto de acesso.
    * Cada AP possui uma lista com as possíveis potências ordenada.
    * Ao ser decrementada, a potência é reduzida para a de índice anterior desta lista.
    */    
    public void decPower()
    {
        // se este não é o menor índice de potência possível,
        if (m_nCurrentTxPowerIndex - 1 >= 0)
        {
            //guarda o índice atual
            int nOldIndex = m_nCurrentTxPowerIndex;
            //decrementa o índice de potência
            m_nCurrentTxPowerIndex--;
            // atualiza a informação de potência no banco de dados
            // se ocorrer erro na atualização, o índice volta ao original.
            // Isto garante que a informação do banco esteja de acordo com a do JAPInfo.
            if (!JCommander.setPower(this, getPower()))
            {
                m_nCurrentTxPowerIndex = nOldIndex;
            }
        }
    }

   /**
    * O objetivo deste método é atualizar a potência do ponto de acesso para a máxima possível.
    */    
    public void setMaxPower()
    {
        int nOldIndex = m_nCurrentTxPowerIndex;
        // ajusta o índice de potência para o máximo.
        m_nCurrentTxPowerIndex = m_listTxPower.size() - 1;
        // Atualiza a informação de potência no banco de dados.
        // se ocorrer erro na atualização, o índice volta ao original.
        // Isto garante que a informação do banco esteja de acordo com a do JAPInfo.        
        if (!JCommander.setPower(this, getPower()))
        {
            m_nCurrentTxPowerIndex = nOldIndex;
        }
    }

   /**
    * Descrição da localização do ponto de acesso.
    */    
    public String getLocalization()
    {
        return m_strLocalization;
    }

   /**
    * Limite que determina se um ponto de acesso está com baixa carga.
    * Se o número de usuários do AP estiver abaixo deste limite, o AP é considerado com baixa carga.
    */     
    public int getUnderloadLimit()
    {
        return m_nUnderloadLimit;
    }

   /**
    * Limite que determina se um ponto de acesso está sobrecarregado.
    * Se o número de usuários do AP estiver acima deste limite, o AP é considerado sobrecarregado.
    * APs que não estão com baixa carga nem sobrecarregados são considerados balanceados.
    */         
    public int getOverloadLimit()
    {
        return m_nOverloadLimit;
    }
}
