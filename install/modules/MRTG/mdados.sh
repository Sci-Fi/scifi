#!/bin/bash
FILES=/etc/mrtg/devices/ap*.cfg
for f in $FILES
do
  echo "Processing $f file..."
  # take action on each file. $f store current file name
  echo $f
# sed -ibak 's/_eth0/_net/g' $f
# sed 's/eth0/Net/g' $f
done
