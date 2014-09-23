/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package apcontroller;

import data.JSTAInfo;
import data.JCellInfo;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Classe que realiza o tratamento dos dados coletados dos pontos de acesso.
 * @author Felipe Rolim
 */
public class JHostFileParser
{

    private static final int STATION = 1;
    private static final int INACTIVE_TIME = 2;
    private static final int RX_BYTES = 3;
    private static final int RX_PACKETS = 4;
    private static final int TX_BYTES = 5;
    private static final int TX_PACKETS = 6;
    private static final int SIGNAL = 7;
    private static final int TX_BITRATE = 8;
    private static final int CHANNEL = 9;
    private static final int SIGNAL_LEVEL_AND_QUALITY = 10;
    private static final int ESSID = 11;
    private static final int NONE = 12;
    private static final int CELL = 13;

    private JHostFileParser()
    {
    }
    /**
     * Método que trata a informação contida no arquivo proveniente da operação de station dump no ponto de acesso.
     * 
     * @param strFile Caminho para o arquivo com as informações de station dump.
     * @return Retorna um lista de JSTAInfos representando as estações associadas ao AP que realizou o station dump.
     */
    public static ArrayList<JSTAInfo> parseSTAFile(String strFile)
    {
        ArrayList<JSTAInfo> listSTA = new ArrayList<JSTAInfo>();

        try
        {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(strFile));
            
      //O arquivo deve estar no seguinte formato (exemplo com uma estação associada): 
      //ADDR               AID CHAN RATE RSSI  DBM  IDLE  TXSEQ  TXFRAG  RXSEQ  RXFRAG CAPS ACAPS ERP    STATE     MODE
      //a0:75:91:ae:00:00    1    1   1M   43  -52     0      2      10    683       0 ESs          0       17   Normal WME
            
            String[] strSplit;
            String strLine, strMAC = "";

            //a primeira linha deve ser ignorada
            bufferedReader.readLine();

            while ((strLine = bufferedReader.readLine()) != null)
            {
                //alguns separadores contém 3 ou 2 espaços em branco. Substituir 2 por 1.
                while(strLine.contains("  "))
                {
                    strLine = strLine.replace("  ", " ");
                }

                strLine = strLine.replace("\t", " ");
                strLine = strLine.trim();

                strSplit = strLine.split(" ");

                strMAC = strSplit[0];

                Pattern patternMAC = Pattern.compile("((?:(\\d{1,2}|[a-fA-F]{1,2}){2})(?::|-*)){6}");
                Matcher matcher = patternMAC.matcher(strMAC);
                // se a string contém um endereço MAC nos conformes do modelo, adiciona a informação de MAC da estação.
                if(matcher.matches())
                {
                    listSTA.add(new JSTAInfo(strMAC));
                }
            }
            
            bufferedReader.close();

        }
        catch (FileNotFoundException ex)
        {
            System.out.println(ex);
            listSTA = null;
        }
        catch (IOException ex)
        {
            System.out.println(ex);
            listSTA = null;
        }

        return listSTA;
    }
    /**
     * Método que trata a informação contida no arquivo proveniente da operação de scan no ponto de acesso.
     * @param strFile Caminho para o arquivo com as informações de scan.
     * @return Retorna uma lista de JCellInfos que representam os APs escutados pelo AP que realizou o scan.
     */
    public static ArrayList<JCellInfo> parseAPFile(String strFile)
    {
        ArrayList<JCellInfo> listCell = new ArrayList<JCellInfo>();

        try
        {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(strFile));          
/*     O arquivo deve estar no seguinte formato (exemplo contendo duas CellInfos):
      
      wlan1     Scan completed :
          Cell 01 - Address: 00:14:BF:20:AA:23
                    Channel:1
                    Frequency:2.412 GHz (Channel 1)
                    Quality=37/70  Signal level=-73 dBm  
                    Encryption key:on
                    ESSID:"eduroam"
                   Bit Rates:1 Mb/s; 2 Mb/s; 5.5 Mb/s; 11 Mb/s; 18 Mb/s
                              24 Mb/s; 36 Mb/s; 54 Mb/s
                    Bit Rates:6 Mb/s; 9 Mb/s; 12 Mb/s; 48 Mb/s
                    Mode:Master
                    Extra:tsf=0000022f43577189
                    Extra: Last beacon: 760ms ago
                    IE: Unknown: 0007656475726F616D
                    IE: Unknown: 010882840B162430486C
                    IE: Unknown: 030101
                    IE: Unknown: 2A0100
                    IE: Unknown: 2F0100
                    IE: IEEE 802.11i/WPA2 Version 1
                        Group Cipher : CCMP
                        Pairwise Ciphers (1) : CCMP
                        Authentication Suites (1) : 802.1x
                       Preauthentication Supported
                   IE: Unknown: 32040C121860
                    IE: Unknown: DD06001018020004
          Cell 02 - Address: 00:25:9C:AE:A5:23
                    Channel:6
                    Frequency:2.437 GHz (Channel 6)
                    Quality=41/70  Signal level=-69 dBm  
                    Encryption key:on
                    ESSID:"MediaLab"
                    Bit Rates:1 Mb/s; 2 Mb/s; 5.5 Mb/s; 11 Mb/s; 18 Mb/s
                              24 Mb/s; 36 Mb/s; 54 Mb/s
                    Bit Rates:6 Mb/s; 9 Mb/s; 12 Mb/s; 48 Mb/s
                    Mode:Master
                    Extra:tsf=0000008963216345
                    Extra: Last beacon: 492ms ago
                    IE: Unknown: 00084D656469614C6162
                    IE: Unknown: 010882848B962430486C
                    IE: Unknown: 030106
                    IE: Unknown: 2A0106
                    IE: Unknown: 2F0106
                    IE: IEEE 802.11i/WPA2 Version 1
                        Group Cipher : CCMP
                        Pairwise Ciphers (1) : CCMP
                        Authentication Suites (1) : PSK
                    IE: Unknown: 32040C121860
                    IE: Unknown: DD06001018020404
 */
            String[] strSplit;
            String strLine = "", strMAC = "", strESSID = "";
            int nChannel = -1, nQuality = -1;
            double dSignal = -1.0;

            int nCurrentRead = NONE;

            int nInfoCount = 0;

            boolean bSkipReading = false;

            while ((strLine = bSkipReading ? strLine : bufferedReader.readLine()) != null)
            {
                strLine = strLine.replace("\t", " ");
                strLine = strLine.trim();

                strSplit = strLine.split(" ");

                switch (nCurrentRead)
                {
                    case CELL:
                    {
                        strMAC = strSplit[4];

                        bSkipReading = false;

                        nCurrentRead = NONE;

                        nInfoCount++;
                    }
                    break;

                    case CHANNEL:
                    {
                        String strValue = strSplit[3].substring(0, strSplit[3].length() - 1);
                        
                        nChannel = Integer.valueOf(strValue);

                        nCurrentRead = NONE;

                        bSkipReading = false;

                        nInfoCount++;
                    }
                    break;

                    case SIGNAL_LEVEL_AND_QUALITY:
                    {
                        strLine = strLine.replace("Quality=", "");
                        strLine = strLine.replace(" Signal level=", "");

                        strSplit = strLine.split(" ");

                        dSignal = Double.valueOf(strSplit[1]);

                        strSplit = strSplit[0].split("/");

                        nQuality = Integer.valueOf(strSplit[0]);

                        bSkipReading = false;

                        nCurrentRead = NONE;

                        nInfoCount++;
                    }
                    break;

                    case ESSID:
                    {
                        strLine = strLine.replace("\"", "");
                        strLine = strLine.replace("ESSID:", "");

                        strESSID = strLine;

                        bSkipReading = false;

                        nCurrentRead = NONE;

                        nInfoCount++;
                    }
                    break;

                    case NONE:
                    {
                        if (strLine.contains("Quality"))
                        {
                            nCurrentRead = SIGNAL_LEVEL_AND_QUALITY;

                            bSkipReading = true;
                        }

                        if (strLine.contains("Cell"))
                        {
                            nCurrentRead = CELL;

                            bSkipReading = true;
                        }

                        if (strLine.contains("ESSID"))
                        {
                            nCurrentRead = ESSID;

                            bSkipReading = true;
                        }

                        if (strLine.contains("Frequency"))
                        {
                            nCurrentRead = CHANNEL;

                            bSkipReading = true;
                        }
                    }
                    break;
                }

                if(nInfoCount == 4)
                {
		    // only add if not EDUROAM, cadastro or visitantes
		    if ((! strESSID.equals("eduroam")) && (! strESSID.equals("cadastro")) && (! strESSID.equals("visitantes")))
			{

			    listCell.add(new JCellInfo(strMAC, nChannel, dSignal, nQuality, strESSID));
			}
                    nInfoCount = 0;
                }
            }
            
            bufferedReader.close();
        }
        catch (FileNotFoundException ex)
        {
            System.out.println(ex);
            listCell = null;
        }
        catch (IOException ex)
        {
            System.out.println(ex);
            listCell = null;
        }

        return listCell;
    }
    
     /**
     * Método que trata a informação contida no arquivo proveniente da operação de obtenção de canal.
     * @param strFile Caminho para o arquivo com a informação de canal.
     * @return Retorna o canal contido no arquivo.
     */   
    public static Integer parseChannelFile(String strFile)
    {
        Integer nChannel = null;
        
        try
        {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(strFile));          

            String strLine = bufferedReader.readLine();
            
            if(strLine != null)
            {
                nChannel = Integer.valueOf(strLine);
            }     
            
            bufferedReader.close();
        }
        catch (FileNotFoundException ex)
        {
            System.out.println(ex);
        }
        catch (IOException ex)
        {
            System.out.println(ex);
        }
        
        return nChannel;
    }
     
    /**
    * Método que trata a informação contida no arquivo proveniente da operação de obtenção de potência.
    * @param strFile Caminho para o arquivo com a informação de potência.
    * @return Retorna a potência contida no arquivo.
    */  
    public static Integer parsePowerFile(String strFile)
    {
        Integer nPower = null;
        
        try
        {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(strFile));          

            String strLine = bufferedReader.readLine();
            
            if(strLine != null)
            {
                nPower = Integer.valueOf(strLine);
            }
            
            bufferedReader.close();
        }
        catch (FileNotFoundException ex)
        {
            System.out.println(ex);
        }
        catch (IOException ex)
        {
            System.out.println(ex);
        }
        
        return nPower;
    }
}
