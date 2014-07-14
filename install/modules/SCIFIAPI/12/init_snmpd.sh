export PATH=/bin:/sbin:/usr/bin:/usr/sbin;
sleep 360
logger 'SCIFI - Starting Snmpd'
/etc/init.d/snmpd start
