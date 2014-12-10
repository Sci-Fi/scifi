export PATH=/bin:/sbin:/usr/bin:/usr/sbin;
sleep 360
logger 'SCIFI - Starting Snmpd'
sh /usr/share/scifi/scripts/snmpd_watchdog.sh
