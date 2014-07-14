export PATH=/bin:/sbin:/usr/bin:/usr/sbin;
logger 'SCIFI - getting txpower value'
iwlist txpower | grep -m 1 wlan0 -A2 | grep Current | awk '{print $2}' | sed 's/Tx-Power=//g' > /tmp/power.txt
