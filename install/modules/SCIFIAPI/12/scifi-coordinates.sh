#!/bin/sh                           
# version 20131017
# Return the coordinates of device
# Cosme Corrêa
# cosmefc@id.uff.br
# uncomment for debug
#set -xv

COOR=`cat /etc/scifi/scifi-coordinates.txt 2>/dev/null`

echo $COOR
exit 0