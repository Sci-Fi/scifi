#!/bin/bash
FILES=/etc/mrtg/devices/ap*.cfg
for f in $FILES
do
  echo "Processing $f file..."
  # take action on each file. $f store current file name
  echo $f
 sed -ibak 's/_eth0/_net/g' $f
 sed 's/eth0/Net/g' $f
done
FIOUT=/var/www/mrtg/ap*
for f in $FIOUT
do 
  echo "Processing $f file..."
  # take action on each file. $f store current file name
  echo $f
  mv /var/www/mrtg/$f/$f_eth0-day.png /var/www/mrtg/$f/$f_net-day.png
  mv /var/www/mrtg/$f/$f_eth0.html /var/www/mrtg/$f/$f_net.html
  mv /var/www/mrtg/$f/$f_eth0.log /var/www/mrtg/$f/$f_net.log
  mv /var/www/mrtg/$f/$f_eth0-month.png /var/www/mrtg/$f/$f_net-month.png
  mv /var/www/mrtg/$f/$f_eth0-week.png /var/www/mrtg/$f/$f_net-week.png
  mv /var/www/mrtg/$f/$f_eth0-year.png /var/www/mrtg/$f/$f_net-year.png
  mv /var/www/mrtg/$f/$f_eth0.old /var/www/mrtg/$f/$f_net.old

 sed -ibak 's/_eth0/_net/g' /var/www/mrtg/$f/$f_net.html
done
