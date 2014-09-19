#!/bin/sh
# version 20140701
# This files switches off wifi interfaces if there is no wired (vlan) access
#
# Luiz Magalhaes
# schara (at) telecom.uff.br
# Helga
# Glaudco
# 
# uncomment for debug
#set -xv

export PATH=/bin:/sbin:/usr/bin:/usr/sbin;

# random number to randomize wait time to prevent address collision and to generate temporary addresses

random=`head /dev/urandom | tr -dc "0123456789" | head -c2`
sleep $random 

pinglan=$(ping -I br-lan -w10 172.17.0.1 |  grep loss | awk '{print $4;exit}')

if  [ $pinglan -gt 1 ]; then 

ifconfig br-203 192.168.0.1$random netmask 255.255.128.0
ping203=$(ping -I br-203 -w10 192.168.0.1 |  grep loss | awk '{print $4;exit}')
ifconfig br-203 0.0.0.0

ifconfig br-204 192.168.128.1$random netmask 255.255.128.0
ping204=$(ping -I br-204 -w10 192.168.128.1 |  grep loss | awk '{print $4;exit}')
ifconfig br-204 0.0.0.0

ifconfig br-206 10.2.0.1$random netmask 255.255.0.0
ping206=$(ping -I br-206 -w10 10.2.0.1 | grep loss | awk '{print $4;exit}')
ifconfig br-206 0.0.0.0

# checking if the wireless interface is up or down
WIFI0=$(! ifconfig |grep "wlan0 " &>/dev/null ; echo $? )
wlan0_status=$WIFI0
WIFI1=$(! ifconfig |grep "wlan0-1" &>/dev/null ; echo $? )
wlan1_status=$WIFI1
WIFI2=$(! ifconfig |grep "wlan0-2" &>/dev/null ; echo $? )
wlan2_status=$WIFI2

# if it is equal to 1, wifi will be reset
wifiup=0

# verificando comunicacao na br-206 (em bridge com wlan0)
# verificando comunicacao na br-203 (em bridge com wlan0-1)
# verificando comunicacao na br-204 (em bridge com wlan0-2)

for loopcount in "1" "2" "3"; do
	
	case "$loopcount" in
	
		"1")
		   interface="wlan0"
		   pngst=$ping206
	   	   ;;
   	   	"2") 
   	   	   interface="wlan0-1"
   		   pngst=$ping203
	   	   ;;
   	   	"3")
   	   	   interface="wlan0-2"
   		   pngst=$ping204
	   	   ;;
   	       	esac
	
	if  [ $(! /sbin/ifconfig $interface |/bin/grep UP| /usr/bin/wc -l  ) = "0" ];
	  then 
# desligado
    	  if  [ $pngst -gt 1 ]; 
		then
# pinging, turn on interface, zero status
# ligar interface, colocar zero no status
		case "$interface" in
			"wlan0")
			wlan0_status=1
			if [ $WIFI0 -eq 0 ]; then wifiup=1; fi
			;;
			"wlan0-1")
			wlan1_status=1
			if [ $WIFI1 -eq 0 ]; then wifiup=1; fi
			;;
			"wlan0-2")
			wlan2_status=1
			if [ $WIFI2 -eq 0 ]; then wifiup=1; fi
			;;
		esac
     		logger SCIFI - Communication with server is ok. Turning $interface on.
		echo "0"> /tmp/status$interface
		fi
	else
# not pinging
	case `cat /tmp/status$interface` in
#  está ligado, está respondendo a ping?

		0)
		   if [ $pngst = "0" ];
			then
			echo "1"> /tmp/status$interface
			logger SCIFI - The AP can not communicate with server. Warning 1 $interface
			fi
		;;

		1)
		   if [ $pngst = "0" ];
			then 
				echo "2"> /tmp/status$interface
				logger SCIFI - The AP can not communicate with server. Warning 2 $interface
			else 
				echo "0"> /tmp/status$interface
				logger SCIFI - Communication with the server is ok. $interface
			fi
		;;
			   	   		   	   		   	       			    	    																												     						  											 																			 				 																												
		2)
		   if [ $pngst = "0" ];
			then
				logger SCIFI - The AP can not communicate with server. Turning off $interface
				case $interface in
					wlan0) wlan0_status=0; ifconfig wlan0 down
					;;
					"wlan0-1") wlan1_status=0; ifconfig wlan0-1 down
					;;
					"wlan0-2") wlan2_status=0; ifconfig wlan0-2 down
					;;
				esac
			else 
				echo "0"> /tmp/status$interface
				logger SCIFI - Communication with server is ok. $interface
			fi
		;;
		*) echo "0"> /tmp/status$interface
		;;
		esac
		fi
done

if [ $wifiup -eq 1 ]
 then
	logger 'SCIFI - Turning on wlan interfaces...'       
	wifi

        if [ $wlan0_status -eq 0 ]
         then
	     ifconfig wlan0 down
        fi
       
        if [ $wlan1_status -eq 0 ]
         then
             ifconfig wlan0-1 down
        fi

        if [ $wlan2_status -eq 0 ]
         then
             ifconfig wlan0-2 down
        fi
fi

# if the AP can not ping on control vlan (i.e, it can not authenticate clients via 802.1x ) it will turn off all wlans interfaces
else
        logger 'SCIFI - The AP can not communicate with the server via control vlan. Turning off all wlan interfaces.'
	wifi down
fi

exit 0
