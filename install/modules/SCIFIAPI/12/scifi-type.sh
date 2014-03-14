#!/bin/sh                           
# version 20131017
# Return the typu of device
# Cosme Corrêa
# cosmefc@id.uff.br
# uncomment for debug
#set -xv

TYPE=`cat /etc/scifi/scifi-type.txt  2>/dev/null`

echo $TYPE
exit 0