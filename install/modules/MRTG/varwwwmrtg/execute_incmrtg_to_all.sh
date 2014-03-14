#!/bin/bash
ls -l | grep ap | awk '{print $9}'| while read line;
do
echo "$line atualizado";
sudo bash /etc/mrtg/incmrtg.sh $line ap;
done;
