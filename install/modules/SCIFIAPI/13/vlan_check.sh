export PATH=/bin:/sbin:/usr/bin:/usr/sbin;
set xv
random=`head /dev/urandom | tr -dc "0123456789" | head -c2`
sleep $random 

pinglan=$(ping -I br-lan -w10 172.17.0.1 |  grep loss | awk '{print $4;exit}')

if  [ $pinglan -gt 1 ]; then

SCIFI='SCIFI'
found=$(snmpwalk -v 1 -c public 10.0.0.1 1.3.6.1.4.1.8072.1.3.2.3.1.2.13 | awk '{print $4; exit}' | sed -e 's/\"//g')

ifconfig br-203 192.168.0.1$random netmask 255.255.128.0
ifconfig br-204 192.168.128.1$random netmask 255.255.128.0
ping203=$(ping -I br-203 -w10 192.168.0.1 |  grep loss | awk '{print $4;exit}')
ping204=$(ping -I br-204 -w10 192.168.128.1 |  grep loss | awk '{print $4;exit}')
ping200=$(ping -I br-lan -w10 10.0.0.1 | grep loss | awk '{print $4;exit}')
ifconfig br-204 0.0.0.0
ifconfig br-203 0.0.0.0

# verificando se a interface sem fio esta ligada ou desligado
WIFI0=$(! ifconfig |grep "wlan0 " &>/dev/null ; echo $? )
status0=$WIFI0
WIFI1=$(! ifconfig |grep "wlan0-1" &>/dev/null ; echo $? )
status1=$WIFI1
WIFI2=$(! ifconfig |grep "wlan0-2" &>/dev/null ; echo $? )
status2=$WIFI2

wifiup=0

# verificando comunicacao na br-lan (em bridge com wlan0)

if [[ "$found" == "$SCIFI" || $ping200 -ge 1 ]]  && [ "$WIFI0" == "1" ]
 then
     count0=0
     echo 0 > /tmp/wifi0_status.txt
     logger SCIFI - Communication with the server is ok on br-lan. Keeping wlan0 up. r $random
     status0=1
     
 else
     if [ "$WIFI0" == "1" ]
      then
          count0=$(cat /tmp/wifi0_status.txt)
          if [ $? -eq 1 ]
           then
               count0=0
          fi    
                            
          if [ $count0 -lt 3 ]
           then
               count0=$(($count0+1))
               logger SCIFI - The AP can not communicate with the server via br-lan. Warning n $count0 r $random
               echo $count0 > /tmp/wifi0_status.txt
     
           else
               logger SCIFI - The AP can not communicate with the server via br-lan. Turning off wlan0. r $random
               ifconfig wlan0 down
               status0=0
          fi
        
     else
         if [[ "$found" == "$SCIFI" || $ping200 -ge 1 ]] && [ "$WIFI0" != "1" ]                                   
	  then    
	      count0=0
	      echo 0 > /tmp/wifi0_status.txt                                                                     
	      logger SCIFI - Communication with the server was reestablished on br-lan. Turning on wlan0. r $random
	      status0=1
	      wifiup=1
          else
              logger SCIFI - The AP can not communicate with the server via br-lan. Keeping wlan0 down. r $random
              status0=0
         fi   
     fi
fi


# verificando comunicacao na br-203 (em bridge com wlan0-1)

if [ $ping203 -ge 1 ] && [ "$WIFI1" == "1" ]
 then
	count1=0
	echo 0 > /tmp/wifi1_status.txt	
	logger SCIFI - Communication with the server is ok on br-203. Keeping wlan0-1 up. r $random
	status1=1     
 else
     if [ "$WIFI1" == "1" ]
      then
         count1=$(cat /tmp/wifi1_status.txt)
         if [ $? -eq 1 ]
          then
              count1=0
	 fi
	
	 if [ $count1 -lt 3 ]
          then
              count1=$(($count1+1))
	      logger SCIFI - The AP can not communicate with the server via br-203. Warning n $count1 r $random
              echo $count1 > /tmp/wifi1_status.txt
          else
              logger SCIFI - The AP can not communicate with the server via br-203. Turning off wlan0-1. r $random
              ifconfig wlan0-1 down
              status1=0
	 fi
	
     else
         if [ $ping203 -ge 1 ] && [ "$WIFI1" != "1" ]
          then
              count1=0
              echo 0 > /tmp/wifi1_status.txt
              logger SCIFI - Communication with the server was reestablished on br-203. Turning on wlan0-1. r $random
              status1=1
              wifiup=1   	
          else
              logger SCIFI - The AP can not communicate with the server via br-203. Keeping wlan0-1 down. r $random
	      status1=0
         fi 
     fi            
fi

# verificando comunicacao na br-204 (em bridge com wlan0-2)

if [ $ping204 -ge 1 ] && [ "$WIFI2" == "1" ]
 then                                                                                                  
     count2=0                                                                                   
     echo 0 > /tmp/wifi2_status.txt                                                             
     logger SCIFI - Communication with the server is ok on br-204. Keeping wlan0-2 up. r $random
     status2=1
 else
     if [ "$WIFI2" == "1" ]
      then
          count2=$(cat /tmp/wifi2_status.txt)
          if [ $? -eq 1 ]
           then
               count2=0
          fi           	        		        
        
          if [ $count2 -lt 3 ]
           then
               count2=$(($count2+1))
               logger SCIFI - The AP can not communicate with the server via br-204. Warning n $count2 r $random
               echo $count2 > /tmp/wifi2_status.txt
           else
               logger SCIFI - The AP can not communicate with the server via br-204. Turning off wlan0-2. r $random
               ifconfig wlan0-2 down
               status2=0
          fi       
      
      else
          if [ $ping204 -ge 1 ] && [ "$WIFI2" != "1" ]                                   
           then    
               count2=0
               echo 0 > /tmp/wifi2_status.txt                                                                     
    	       logger SCIFI - Communication with the server was reestablished on br-204. Turning on wlan0-2. r $random
      	       status2=1
      	       wifiup=1
           else
               logger SCIFI - The AP can not communicate with the server via br-204. Keeping wlan0-2 down. r $random
               status2=0
          fi
     fi                                                                                                     
fi

# ligando e desligando interfaces sem fio

if [ $wifiup -eq 1 ]
 then
	logger 'SCIFI - Turning on wlan interfaces...'       
	wifi

        if [ $status0 -eq 0 ]
         then
	     logger 'SCIFI - Turning off wlan0.'
	     ifconfig wlan0 down
        fi
       
        if [ $status1 -eq 0 ]
         then
             logger 'SCIFI - Turning off wlan0-1.'
             ifconfig wlan0-1 down
        fi

        if [ $status2 -eq 0 ]
         then
             logger 'SCIFI - Turning off wlan0-2.'
             ifconfig wlan0-2 down
        fi
fi            
       
# informando que o AP nao conseguiu receber resposta do servico snmpd do servidor
#if [ "$found" != "$SCIFI" ] && [ $ping200 -ge 1 ]
#then
# logger 'SCIFI - The SNMP query was not successful.'
#fi

# if the AP can not ping on control vlan (i.e, it can not authenticate clients via 802.1x ) it will turn off all wlans interfaces
else
        logger 'SCIFI - The AP can not communicate with the server via control vlan. Turning off all wlan interfaces.'
        wifi down
fi

exit 0
