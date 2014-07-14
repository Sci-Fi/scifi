export PATH=/bin:/sbin:/usr/bin:/usr/sbin;
p='udhcpc'
found=$(cat /proc/$(cat /var/run/*dhcp*-br-lan.pid)/cmdline | awk '{print $1; exit}')
if [ "$found" != "$p" ]
  then  
    count=$(cat /tmp/udhcpc_status.txt)
    if [ $? -eq 1 ]
      then
        count=0
    fi    
    if [ $count -lt 10 ]
      then
	logger 'SCIFI - udhcpc is down - watchdog will restart network' 
        /etc/init.d/network restart 
        count=$(($count+1))
        echo $count > /tmp/udhcpc_status.txt
    else
      logger 'SCIFI - udhcpc is down - watchdog will reboot AP'
      reboot -f
    fi
else
  echo 0 > /tmp/udhcpc_status.txt
fi
exit 0
