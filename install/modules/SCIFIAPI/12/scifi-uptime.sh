#!/bin/sh                           
# version 20140527
# Return the type of device
# Cosme Corrêa
# cosmefc@id.uff.br
# uncomment for debug
#set -xv


UPTIMEOID=.1.3.6.1.2.1.25.1.1.0
#UPTIMEOID=.1.3.6.1.6.3.10.2.1.3
DEVICE=127.0.0.1
VERSION=2c
COMMUNITY=public
RESULT=`snmpget -v $VERSION -c $COMMUNITY $DEVICE $UPTIMEOID | cut -d'(' -f2 | cut -d')' -f1 `
echo $RESULT
exit 0