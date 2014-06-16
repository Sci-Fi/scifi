#!/bin/sh                           
# version 20130710
# Apoio a Identificação automática do SCIFI e sua versão
# Retorna a string com SCIFI e o código de retorno com a versão
# Cosme Corrêa
# cosmefc@id.uff.br
#  Descomente para debug
#set -xv

SYSTEM='SCIFI'
#VERSION=12
VERSION=`cat /etc/scifi/scifi-version.txt 2>/dev/null`

echo $SYSTEM $VERSION
exit $VERSION
