#!/bin/sh
# version 20140527
# Return the type of device
# Cosme Corr..a
# cosmefc@id.uff.br
# uncomment for debug
#set -xv


UPTIMEOID=.1.3.6.1.2.1.25.1.1.0
#UPTIMEOID=.1.3.6.1.6.3.10.2.1.3
DEVICE=127.0.0.1
VERSION=2c
COMMUNITY=public
#RESULT=`snmpget -v $VERSION -c $COMMUNITY $DEVICE $UPTIMEOID | cut -d'(' -f2 | cut -d')' -f1 `
RESULT=$(cat /proc/uptime |awk '{print $1}' | cut -d. -f1 )
#echo $RESULT
MINUTES=$((RESULT/60))
echo $MINUTES
exit 0
