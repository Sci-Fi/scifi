#!/bin/sh
# version 20140726
#
# Script that runs the server-side backend
# for receiving configuration data of new APs
#
# Luiz Magalhaes
# schara (at) telecom.uff.br
#

set -xv
while true; do

	nc -l 2048 >> new_aps.txt
	./exec_scp.sh &
done
