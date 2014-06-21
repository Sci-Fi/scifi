#!/bin/sh
# version 20140620
# Luiz Magalhaes
# schara (at) telecom.uff.br
#
# uncomment for debug
#set -xv

case "$1" in

ID | VERSION | SUBVERSION | DEVICE | COORDINATES | TAGS | CONNECTED2)
awk -v search=$1 '{if ($1==search) print $2;}' /tmp/scifi/scifi-snmp.conf
        ;;

USERS) iw wlan0 station dump | grep -c Station
        ;;

NEIGHBORHOOD) if [ -f "/tmp/scifi-neighborhood.txt"];
                then
                cat /tmp/scifi-neighborhood.txt
                else
                echo "0"
                fi
        ;;

UPTIME) echo `cut -d' ' -f1 /proc/uptime | cut -d'.' -f1`

        ;;

*) echo "not found"
   ;;
esac