#!/bin/bash
cd /var/www/mrtg
FIOUT=ap*
for f in $FIOUT
do 
  echo "Processing $f file..."
  # take action on each file. $f store current file name
  echo $f

 sed -ibak 's/_eth0/_net/g' /var/www/mrtg/$f/index.html
 sed -ibak 's/_eth0/_net/g' /var/www/mrtg/$f/index.php
done
