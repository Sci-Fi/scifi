#!/bin/sh                           
# version 20131017
# Return the typu of device
# Cosme Corrêa
# cosmefc@id.uff.br
# Glauco Quintino
# glaucoq@id.uff.br
#
# uncomment for debug
#set -xv

#TYPE=`cat /etc/scifi/scifi-type.txt  2>/dev/null`
TYPE=`cut -d' ' -f1 /etc/scifi/scifi-type.txt  2>/dev/null`
VERSION=`cut -d' ' -f2 /etc/scifi/scifi-type.txt  2>/dev/null || echo 'Erro' `

echo $TYPE
exit $VERSION