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

# Calls # 1,2,3,4,5,6 , 7, 11 and 12
ID | VERSION | SUBVERSION | DEVICE | COORDINATES | TAGS | CONNECTED2 | SEGMENT | FIRMWARE)
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

*) echo "not found"
   ;;
esac
