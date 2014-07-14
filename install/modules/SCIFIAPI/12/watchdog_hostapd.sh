export PATH=/bin:/sbin:/usr/bin:/usr/sbin;
p='hostapd'
found=$(cat /proc/$(cat /var/run/wifi-phy0.pid)/cmdline | awk '{print $1; exit}')
if [ "$found" != "$p" ]
  then  
    count=$(cat /tmp/hostapd_status.txt)
    if [ $? -eq 1 ]
      then
        count=0
    fi    
    if [ $count -lt 10 ]
      then
	logger 'SCIFI - hostapd is down - watchdog will restart wifi interface' 
        wifi up
        count=$(($count+1))
        echo $count > /tmp/hostapd_status.txt
    else
      logger 'SCIFI - hostapd is down - watchdog will reboot AP'
      reboot -f
    fi
else
  echo 0 > /tmp/hostapd_status.txt
fi
exit 0
