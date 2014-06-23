#!/bin/sh
# version 20140621
# Central script for SCIFI-API
#
# Luiz Magalhaes
# schara (at) telecom.uff.br
# Cosme Corr..a
# cosmefc@id.uff.br
# uncomment for debug
#set -xv

case "$1" in

# Calls # 1,2,3,4,5,6 and 7
ID | VERSION | SUBVERSION | DEVICE | COORDINATES | TAGS | CONNECTED2)
awk -F'=' -v search=$1 '{if ($1==search) print $2;}' /etc/scifi/scifi.conf
        ;;

# Call #8
USERS) 
if [ "`snmpget -v 2c -c public 127.0.0.1 .1.3.6.1.4.1.2021.8.1.101.4 | cut -d' ' -f4`" = "CONTROLLER" ]
	then
	LIST=`cut -d'/' -f5 /etc/mrtg/devices.inc | grep ap`
	for AP in $LIST
		do
		AP=`expr substr $AP 1 6`
		USU=0		
		if [ `head -1 /var/www/mrtg/"$AP"/"$AP"_usu.log | cut -d' ' -f3` != '-1' ] 
			then
			echo USU=`head -2 /var/www/mrtg/"$AP"/"$AP"_usu.log | tail -1 | cut -d' ' -f2`
		fi
		USU=${USU/' '/}
		TOTAL=$(( TOTAL + USU ))
		done
	echo $TOTAL
	else
	echo $((`iw wlan0 station dump|grep -c Station`+`iw wlan0-1 station dump | grep -c Station`))
fi
        ;;

# Call #9
NEIGHBORHOOD) if [ -f "/tmp/scifi-neighborhood.txt"];
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
