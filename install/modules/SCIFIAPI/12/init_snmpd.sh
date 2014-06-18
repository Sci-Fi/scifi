export PATH=/bin:/sbin:/usr/bin:/usr/sbin;
sleep 600
logger 'SCIFI - Starting Snmpd'
/etc/init.d/snmpd start
