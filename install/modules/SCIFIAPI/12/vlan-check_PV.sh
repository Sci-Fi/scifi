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
set -xv

export PATH=/bin:/sbin:/usr/bin:/usr/sbin;

# random number to randomize wait time to prevent address collision and to generate temporary addresses

random=`head /dev/urandom | tr -dc "0123456789" | head -c2`

# tirei para testes
# sleep $random 

ifconfig br-203 192.168.0.1$random netmask 255.255.128.0
ifconfig br-204 192.168.128.1$random netmask 255.255.128.0
ifconfig br-205 10.2.0.1$random netmask 255.255.0.0
ping203=$(ping -I br-203 -w10 192.168.0.1 |  grep loss | awk '{print $4;exit}')
ping204=$(ping -I br-204 -w10 192.168.128.1 |  grep loss | awk '{print $4;exit}')
pinglan=$(ping -I br-lan -w10 172.17.0.1 |  grep loss | awk '{print $4;exit}')
ping205=$(ping -I br-205 -w10 10.2.0.1 | grep loss | awk '{print $4;exit}')
ifconfig br-203 0.0.0.0
ifconfig br-204 0.0.0.0
ifconfig br-205 0.0.0.0

# verificando comunicacao na br-205 (em bridge com wlan0)

#InterfaceStatus $WIFI0 $ping205

# verificando comunicacao na br-203 (em bridge com wlan0-1)

#InterfaceStatus $WIFI1 $ping203

# verificando comunicacao na br-204 (em bridge com wlan0-2)

#InterfaceStatus $WIFI2 $ping204

# function InterfaceStatus {

# Parameters:	
# $1 -> interface : wlan0, wlan0-1, wlan0-2
# $2 -> 0/1, 0 if not pinging server through vlan, 1 if pinging

for loopcount in "1" "2" "3"; do
	echo "loopcount " $loopcount
	
	case "$loopcount" in
	
		"1") echo "1"
		   interface="wlan0"
		   echo "interface" $interface
		   pngst=$ping205
	   	   ;;
   	   	"2") echo "2" 
   	   	   interface="wlan0-1"
   		   pngst=$ping203
	   	   ;;
   	   	"3") echo "3"
   	   	   interface="wlan0-2"
   		   pngst=$ping203
	   	   ;;
   	       	esac
   	echo " interface " $interface " pngst" $pngst
	if  [ $(! /sbin/ifconfig $interface |/bin/grep UP| /usr/bin/wc -l  ) = "0" ];
	  then 
# desligado
echo "ping status" $pngst
    	  if  [ $pngst = "1" ]; 
		then
# pinging, turn on interface, zero status
# ligar interface, colocar zero no status
#		turn_on_wifi $1
echo $interface
		case "$interface" in
			"wlan0")
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
     		logger SCIFI - Communication with server is ok. Turning $interface on.
		echo "0"> /tmp/status$interface
		fi
	else
	echo "ping status" $pngst
# not pinging
	case `cat /tmp/status$interface` in
#  está ligado, está respondendo a ping?

		0) echo "status 0"
		   if [ $pngst = "0" ];
			then
			echo "1"> /tmp/status$interface
			logger SCIFI - The AP can not communicate with server. Warning 1 $interface
			fi
		;;

		1) echo "status 1"
		   if [ $pngst = "0" ];
			then 
				echo "2"> /tmp/status$interface
				logger SCIFI - The AP can not communicate with server. Warning 2 $interface
			else 
				echo "0"> /tmp/status$interface
				logger SCIFI - Communication with the server is ok. $interface
			fi
		;;
			   	   		   	   		   	       			    	    																												     						  											 																			 				 																												
		2) echo "status 2"
		   if [ $pngst = "0" ];
			then
				logger SCIFI - The AP can not communicate with server. Turning off $interface
# turn_off_wifi $1
				case $interface in
					wlan0) uci set wireless.@wifi-iface[0].disabled=1
					;;
					"wlan0-1") uci set wireless.@wifi-iface[1].disabled=1
					;;
					"wlan0-2") uci set wireless.@wifi-iface[2].disabled=1
					;;
				esac
				uci commit wireless
				wifi
			else 
				echo "0"> /tmp/status$interface
				logger SCIFI - Communication with server is ok. $interface
			fi
		;;
		*) echo "0"> /tmp/status$interface
		;;
		esac
		fi
#	fi
# }
done
exit 0
