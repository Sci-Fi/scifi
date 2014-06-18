#!/bin/sh                           
# version 20140618
# Return the neighborhood of device
# Cosme Corrêa
# cosmefc@id.uff.br
# uncomment for debug
#set -xv

NEIGH=`cat /etc/scifi/scifi-neighborhood.txt 2>/dev/null`

if NUMBER=`wc -w /tmp/scifi-neighborhood.txt`
	then
		NUMBER=`echo $NUMBER | cut -d' ' -f1'`
	else
		NUMBER=0
fi

echo $NEIGH
exit $NUMBER