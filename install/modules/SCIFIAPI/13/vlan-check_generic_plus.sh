#!/bin/sh
# version 20141125
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

# Segment dependent information

BRDATA="XXXX"
IPPREFIX="10.YYYY."
IPGW="10.YYYY.0.1"

# random number to randomize wait time to prevent address collision and to generate temporary addresses

random=`head /dev/urandom | tr -dc "0123456789" | head -c2`
sleep $random 

major_ip=`ifconfig br-lan | grep inet | awk '{print $2}'| awk -F"\." '{print $3}'`
minor_ip=`ifconfig br-lan | grep inet | awk '{print $2}'| awk -F"\." '{print $4}'`

pinglan=$(ping -I br-lan -w10 172.17.0.1 |  grep loss | awk '{print $4;exit}')

	ifconfig br-203 192.168.$major_ip.$minor_ip netmask 255.255.128.0
	ping203=$(ping -I br-203 -w10 192.168.0.1 |  grep loss | awk '{print $4}')
	ifconfig br-203 0.0.0.0

	let majorplus=major_ip+128
	ifconfig br-204 192.168.$majorplus.$minor_ip netmask 255.255.128.0
	ping204=$(ping -I br-204 -w10 192.168.128.1 |  grep loss | awk '{print $4}')
	ifconfig br-204 0.0.0.0

	ifconfig $BRDATA $IPPREFIX$major_ip.$minor_ip netmask 255.255.0.0
	pingDATA=$(ping -I $BRDATA -w10 $IPGW | grep loss | awk '{print $4}')
	ifconfig $BRDATA 0.0.0.0


# if it is equal to 1, wifi will be reset
	wifiup=0

# verificando comunicacao na br-205 (em bridge com wlan0)
# verificando comunicacao na br-203 (em bridge com wlan0-1)
# verificando comunicacao na br-204 (em bridge com wlan0-2)
# verificando comunicação na vlan de controle

	for interface in "wlan0" "wlan0-1" "wlan0-2";
		do
		case $interface in
		"wlan0")   pngst=$pingDATA
	   	   ;;
   	   	"wlan0-1") pngst=$ping203
	   	   ;;
   	   	"wlan0-2") pngst=$ping204
	   	   ;;
   	       	esac
   	       	
		intup=`/sbin/ifconfig $interface |/bin/grep UP| /usr/bin/wc -l`

		if  [ $intup = "0" ];
	  		then 

# desligado / interface is off

    	  		if  [ $pngst -gt 1 ]; 
				then

# pinging, turn on interface, zero status
# ligar interface, colocar zero no status

				wifiup=1;
     				logger SCIFI - Communication with server is ok. Turning $interface on if control vlan is ok.
				echo "0"> /tmp/status$interface
				fi
			else
			
# ligado / interface is on			
			
			if [ $pngst = "0" ];
				then
# not pinging
				case `cat /tmp/status$interface` in
			
#  está ligado, está respondendo a ping?
					0)
					echo "1"> /tmp/status$interface
					logger SCIFI - The AP can not communicate with server. Warning 1 $interface
					;;

					1)
					echo "2"> /tmp/status$interface
					logger SCIFI - The AP can not communicate with server. Warning 2 $interface
					;;
				
					2)
					echo "3"> /tmp/status$interface
					logger SCIFI - The AP can not communicate with server. Turning off $interface
					ifconfig $interface down
					;;
					
					3) logger SCIFI - The AP still can not communicate with server. Keeping $interface off
					;;
				
					*) echo "0"> /tmp/status$interface
					;;
				esac
				
				else 
				echo "0"> /tmp/status$interface
				logger SCIFI - Communication with server is ok. $interface
			fi
		fi
	done

if  [ $pinglan -gt 1 ]; 
	then
	echo "0"> /tmp/statuslan
	
	if [ $wifiup -eq 1 ]
 		then
		logger 'SCIFI - Turning on wlan interfaces...'       
		wifi
		for interface in "wlan0" "wlan0-1" "wlan0-2";
			do
			if [ `cat /tmp/status$interface` = "3" ];
				then 
				sleep 3
				ifconfig $interface down
			fi
			done
	fi
	else

# if the AP can not ping on control vlan (i.e, it can not authenticate clients via 802.1x ) it will turn off all wlans interface
	case `cat /tmp/statuslan` in
		0)
		echo "1"> /tmp/statuslan
		logger SCIFI - The AP can not communicate with server on control VLAN. Warning 1 
		;;

		1)
		echo "2"> /tmp/statuslan
		logger SCIFI - The AP can not communicate with server on control VLAN. Warning 2
		;;
				
		2)
		echo "3"> /tmp/statuslan
		logger 'SCIFI - The AP can not communicate with the server via control vlan. Turning off all wlan interfaces.'
		ifconfig wlan0 down
		ifconfig wlan0-1 down
		ifconfig wlan0-2 down
		;;
					
		3) logger SCIFI - The AP still can not communicate with server on control VLAN. Keeping interfaces off
		;;
				
		*) echo "0"> /tmp/statuslan
		;;
	esac
fi

# 1 - 3 medições de zero usuários
# 2 - memória acima de 80%
# 3 - CPU abaixo de 10%
# 4 - não rodou o wifi na última hora

if [ `cat /tmp/nsta.txt` -eq 0 ];
        then
        case `cat /tmp/zero_users.txt` in
                0) echo "1" > /tmp/zero_users.txt
                ;;
                1) echo "2" > /tmp/zero_users.txt
                ;;
                2) 
                echo "0" > /tmp/zero_users.txt
                idle=`top -b -n 2 | grep idle | awk -F"%" '{ print $4}' | awk '{print $2}'| tail -n 2 | head -n 1`
                if [ $idle -gt 89 ];
                        then
                        memTotal=`grep "MemTotal" /proc/meminfo | awk '{print $2}' `
                        memFree=`grep "MemFree" /proc/meminfo | awk '{print $2}' `
                        let memTotal=memTotal/6
                        if [ $memFree -lt $memTotal ];
                                then 
                                wifi
                                echo "3" > /tmp/zero_users.txt
                                sleep 3
                                logread > /tmp/logread.txt
                                logger 'SCIFI - watchdog reset wlan interfaces...'
                        fi
                fi
                ;;
                3) echo "4" > /tmp/zero_users.txt
                ;;
                4) echo "5" > /tmp/zero_users.txt
                ;;
                *) echo "0" > /tmp/zero_users.txt
                ;;
        esac
        else
        echo "0" > /tmp/zero_users.txt
fi

exit 0
