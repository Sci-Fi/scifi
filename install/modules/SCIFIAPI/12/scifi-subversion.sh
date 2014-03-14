#!/bin/sh                           
# version 20131017
# Apoio a Identificação automática do SCIFI e sua versão
# Return a string with suversion
# Cosme Corrêa
# cosmefc@id.uff.br
#  Descomente para debug
#set -xv

SUBVERSION=`cat /etc/scifi/scifi-subversion.txt 2>/dev/null`

echo $SUBVERSION
exit $SUBVERSION