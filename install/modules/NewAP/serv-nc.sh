#!/bin/sh
# version 20140726
#
# Script that runs the server-side backend
# for receiving configuration data of new APs
#
# Luiz Magalhaes
# schara (at) telecom.uff.br
#

#set -xv
while true; do

	nc -l 2048 >> new_aps.txt
	ip=$(tail -1 new_aps.txt|awk '{print $2}')

	#echo $status $ip $mac

	echo $ip
	echo -n "# " >> new_aps.txt
	date >> new_aps.txt
	echo >> new_aps.txt
	scp -i /usr/share/scifi/core/controller_key "root@"$ip":/www/dados*" dados

done
