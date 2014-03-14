#!/bin/sh                           
# version 20131017
# Return the Tags of device
# Cosme Corrêa
# cosmefc@id.uff.br
# uncomment for debug
#set -xv

TAGS=`cat /etc/scifi/scifi-tags.txt 2>/dev/null`

echo $TAGS
exit 0