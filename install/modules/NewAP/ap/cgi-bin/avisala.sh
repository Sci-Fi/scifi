#!/bin/sh 
# version 20140725           
# Luiz Magalhaes             
# schara (at) telecom.uff.br 
#                                                          
# this script sends the state                              
# 2 for non-configured                                     
# 3 for configured                                         
# 4 for sent                                               
# 5 for edited                                             
# 6 for sent edited                                        
#  which is given by the presence of the /www/data$MAC file
# states 0 and 1 are states without network connection
# which precludes the sending of the state            
# the file also contains the current IP and the AP mac address       
#                                                                    
# the script changes the state to sent                               
#                                                                    
# set -xv                                                            
                                                                     
# server port                                                                         
                                                                                      
PORT="2048"                                                                           
                                                                                      
#                                                                                     
                                                                                      
if [ -e status.txt ]; then
	                                                            
        STATUS=$(awk '{print $1}' status.txt)                                         
	if [ `ifconfig br-lan | grep UP | awk '{print $1}'` = "UP" ];
		then if [ $STATUS -eq 0 ];
			then 
			STATUS=2
			sed -i "s/0/2/" status.txt 
			fi
		     if [ $STATUS -eq 1 ];
			then
			STATUS=3
			sed -i "s/1/3/" status.txt
			fi	
		else STATUS=1
	fi	
        if [ $STATUS -gt 1 ]; then                                                    
                DR=$(route -n | awk '{ if ($1=="0.0.0.0") print $2}')                                                                         
                nc -w1 $DR $PORT < status.txt &                                       
                sleep 1                                                               
                for d in `ps | grep "nc -w1" | awk '{print$1}'`; do kill -HUP $d; done
                if [ $STATUS -eq 3 ]; then         
                        sed  -i "s/3/4/" status.txt
                fi                                
                if [ $STATUS -eq 5 ]; then        
                        sed -i "s/5/6/" status.txt
                fi
        fi
fi
