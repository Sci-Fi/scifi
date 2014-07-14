export PATH=/bin:/sbin:/usr/bin:/usr/sbin;
logger 'SCIFI - getting number of associated stations'
nsta=$(iw wlan0 station dump | grep -c Station)
echo $nsta
exit $nsta
