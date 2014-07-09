export PATH=/bin:/sbin:/usr/bin:/usr/sbin;
logger 'SCIFI - collecting data about associated stations'
rm /tmp/sta.txt
#remover sta linha quando nao houver necessidade da primeira ser em branco
echo " " >> /tmp/sta.txt
iw wlan0 station dump > /tmp/sta_tmp.txt
cat /tmp/sta_tmp.txt | while read LINE ; do
	TEST=$(echo $LINE | awk '{ print $1 }')
	if [ $TEST = "Station" ]
	then
	TEST=$(echo $LINE | awk '{ print $2 }')
	 echo $TEST >> /tmp/sta.txt
	fi
		done
rm /tmp/sta_tmp.txt
