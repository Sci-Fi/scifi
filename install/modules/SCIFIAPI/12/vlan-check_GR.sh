#!/bin/sh
# version 20140630
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

function turn_on_wifi {

# Parameters:
# $1 -> interface : wlan0, wlan0-1, wlan0-2
# 
case $1 of

wlan0)
uci set wireless.@wifi-iface[0].disabled=0
;;
"wlan0-1")
uci set wireless.@wifi-iface[1].disabled=0
;;
"wlan0-2")
uci set wireless.@wifi-iface[2].disabled=0
;;
esac
uci commit wireless
wifi

	}


function turn_off_wifi {

# Parameters:
# $1 -> interface : wlan0, wlan0-1, wlan0-2
# 
case $1 of

wlan0)
uci set wireless.@wifi-iface[0].disabled=1
;;
"wlan0-1")
uci set wireless.@wifi-iface[1].disabled=1
;;
"wlan0-2")
uci set wireless.@wifi-iface[2].disabled=1
;;
esac

uci commit wireless
wifi

	}



function InterfaceStatus {

# Parameters:	
# $1 -> interface : wlan0, wlan0-1, wlan0-2
# $2 -> 0/1, 0 if not pinging server through vlan, 1 if pinging

if  [$(! /sbin/ifconfig $1 |/bin/grep UP| /usr/bin/wc -l  )="0"];
	then 

# desligado

  	if  [$2="1"]; 
  		then

# pinging, turn on interface, zero status
# ligar interface, colocar zero no status

		turn_on_wifi $1
     		logger SCIFI - Communication with server is ok. Turning $1 on.

		echo "0"> /tmp/status$1
		fi

  	else
  	
# not pinging

	Case `cat /tmp/status$1` of

# está ligado, está respondendo a ping?

	0) if [$2="0"];
		then
 		echo "1"> /tmp/status$1
		logger SCIFI - The AP can not communicate with server. Warning 1 $1
		fi

		;;

	1) if [ $2="0" ];

 		then 
 			echo "2"> /tmp/status$1
			logger SCIFI - The AP can not communicate with server. Warning 2 $1

		else 
			echo "0"> /tmp/status$1
			logger SCIFI - Communication with the server is ok.

		fi

		;;

	2) if [ $2= "0" ];
		then
			logger SCIFI - The AP can not communicate with server. Turning off $1
 			turn_off_wifi $1

		else 
			echo "0"> /tmp/status$1
			logger SCIFI - Communication with server is ok. 

		fi

		;;

	esac
	fi
  fi
fi
}


# Main PROGRAM

# random number to randomize wait time to prevent address collision and to generate temporary addresses

random=`head /dev/urandom | tr -dc "0123456789" | head -c2`
sleep $random 


ifconfig br-203 192.168.0.1$random netmask 255.255.128.0
ifconfig br-204 192.168.128.1$random netmask 255.255.128.0
ping203=$(ping -I br-203 -w10 192.168.0.1 |  grep loss | awk '{print $4;exit}')
ping204=$(ping -I br-204 -w10 192.168.128.1 |  grep loss | awk '{print $4;exit}')
pinglan=$(ping -I br-204 -w10 172.17.0.1 |  grep loss | awk '{print $4;exit}')
ping205=$(ping -I br-lan -w10 10.2.0.1 | grep loss | awk '{print $4;exit}')
ifconfig br-204 0.0.0.0
ifconfig br-203 0.0.0.0

WIFI0= "wlan0"
WIFI1= "wlan0-1"
WIFI2= "wlan0-2"

# verificando comunicacao na br-205 (em bridge com wlan0)

InterfaceStatus $WIFI0 $ping205

# verificando comunicacao na br-203 (em bridge com wlan0-1)

InterfaceStatus $WIFI1 $ping203

# verificando comunicacao na br-204 (em bridge com wlan0-2)

InterfaceStatus $WIFI2 $ping204


exit 0
