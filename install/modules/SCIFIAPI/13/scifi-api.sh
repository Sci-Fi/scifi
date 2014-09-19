#!/bin/sh
# version 20140630
# Central script for SCIFI-API
#
# Luiz Magalhaes
# schara (at) telecom.uff.br
# Cosme Corr..a
# cosmefc@id.uff.br
# uncomment for debug
#set -xv

case "$1" in

# Calls # 1,2,3,4,5,6 , 7, 11, 12, 14,17 and 18
ID | VERSION | SUBVERSION | DEVICE | COORDINATES | TAGS | CONNECTED2 | SEGMENT | FIRMWARE | PING_ENABLE | CAMPUS | DEPARTMENT)
awk -v search=$1 '{if ($1==search) print $2;}' /etc/scifi/scifi.conf
        ;;

# Call #8
USERS) 
# this version for APs only
	nsta=0;for i in `ifconfig | grep wlan0 | awk '{print $1}'`; do let nsta=$nsta+`iw $i station dump | grep -c Station`;done;echo $nsta;	
        ;;

# Call #9
NEIGHBORHOOD) if [ -f "/tmp/scifi-neighborhood.txt" ];
                then
                 cat /tmp/scifi-neighborhood.txt
                else
                 echo "0"
                fi
        ;;

# Call #10
UPTIME) echo $((`cut -d'.' -f1 /proc/uptime`/60))
        ;;

# Call #13
LEASES) echo "0"
	;;

# Call #15
LOSS) if [ -e "/tmp/loss.txt" ]; then cat /tmp/loss.txt; else echo "0"; fi
	;;

# Call #16
DELAY) if [ -e "/tmp/delay.txt" ]; then cat /tmp/delay.txt; else echo "0"; fi
	;;

# Call #19
DATA_VLAN) 
if [ "`awk  '{if ($1=="SEGMENT") print $2;}' /etc/scifi/scifi.conf`" == "SC" ]; then
        if [ -e "/tmp/wifi0_status.txt" ]; then cat /tmp/wifi0_status.txt; else echo "-1"; fi
else
        if [ -e "/tmp/statuswlan0" ]; then cat /tmp/statuswlan0; else echo "-1"; fi
fi
        ;;
                        
# Call #20
REGISTRATION_VLAN)
if [ "`awk  '{if ($1=="SEGMENT") print $2;}' /etc/scifi/scifi.conf`" == "SC" ]; then
        if [ -e "/tmp/wifi1_status.txt" ]; then cat /tmp/wifi1_status.txt; else echo "-1"; fi
else
        if [ -e "/tmp/statuswlan0-1" ]; then cat /tmp/statuswlan0-1; else echo "-1"; fi
fi
        ;;
                                                
# Call #21
VISITORS_VLAN) 
if [ "`awk  '{if ($1=="SEGMENT") print $2;}' /etc/scifi/scifi.conf`" == "SC" ]; then
        if [ -e "/tmp/wifi2_status.txt" ]; then cat /tmp/wifi2_status.txt; else echo "-1"; fi
else
        if [ -e "/tmp/statuswlan0-2" ]; then cat /tmp/statuswlan0-2; else echo "-1"; fi
fi
        ;;

# Call #22
DATA_RX)  ifconfig wlan0 | grep "RX bytes" | awk -F ":" '{print $2}' | awk '{print $1}'
        ;;

# Call #23
DATA_TX)  ifconfig wlan0 | grep "TX bytes" | awk -F ":" '{print $3}' | awk '{print $1}'
        ;;

# Call #24
REGISTRATION_RX) ifconfig wlan0-1 | grep "RX bytes" | awk -F ":" '{print $2}' | awk '{print $1}'
        ;;

# Call #25
REGISTRATION_TX) ifconfig wlan0-1 | grep "TX bytes" | awk -F ":" '{print $3}' | awk '{print $1}'
        ;;

# Call #26
VISITORS_RX) ifconfig wlan0-2 | grep "RX bytes" | awk -F ":" '{print $2}' | awk '{print $1}'
        ;;

# Call #27
VISITORS_TX) ifconfig wlan0-2 | grep "TX bytes" | awk -F ":" '{print $3}' | awk '{print $1}'
        ;;

# Call #28
WLOCAL_RX) ifconfig wlan0-3 | grep "RX bytes" | awk -F ":" '{print $2}' | awk '{print $1}'
        ;;

# Call #29
WLOCAL_TX) ifconfig wlan0-3 | grep "TX bytes" | awk -F ":" '{print $3}' | awk '{print $1}'
	;;
                                                                                
*) echo "not found"
   ;;

esac
