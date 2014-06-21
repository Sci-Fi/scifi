# Scan AP neighborhood
# Version 20140620
# Helga
# Schara schara (at) telecom.uff.br
# Cosme Corrêa - cosmefc@id.uff.br
# Glauco Quintino glaucoq@id.uff.br
# uncomment for debug
#set -xv

export PATH=/bin:/sbin:/usr/bin:/usr/sbin;
logger 'SCIFI - scan will begin now'
ifconfig wlan1
err=$?
if [ "$err" -eq 1 ]
	then
	iw phy0 interface add wlan1 type station
	ifconfig wlan1 hw ether 00:11:22:33:44:55
	ifconfig wlan1 up
fi
iwlist wlan1 scan > /tmp/scan.txt
# Make
grep Cell /tmp/scan.txt | awk '{print $5;}' | sort -u | awk '{printf ( $1",") > "/tmp/scifi-neighborhood.txt"}'
exit 0
