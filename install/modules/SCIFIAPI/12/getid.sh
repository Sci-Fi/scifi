#!/bin/bash                           
# version 20140711                     
# Show ID linked to MAC
# Cosme Corrêa - cosmefc@id.uff.br
# 
# Uncomment for debug
# set -xv

ERRO () {
echo $1
echo " "
echo Show ID linked to MAC
echo 
echo sintax:    $0 MACADDRESS
echo 
echo "examples:  $0 52:54:00:01:1f:80"
echo "           $0 90-C1-15-D2-4B-47"
echo 
echo 
exit 1 
}

if [ $# -ne 1 ] 
	then ERRO 'Wrong # of parameters, it must be 1.'
fi

if [ ${#1} -ne 17 ]
	then ERRO 'Wrong length, it must be 17.'
fi

MAC=`echo $1 | tr ':' '-'| tr '[:lower:]' '[:upper:]'`

# With domain
echo `grep -E "Login OK.*$MAC" /var/log/radius/radius.log | tail -1 | cut -d'[' -f2 | cut -d']' -f1`

# Without domain
#echo `grep -E "Login OK.*$MAC" /var/log/radius/radius.log | tail -1 | cut -d'[' -f2 | cut -d']' -f1` | cut -d'@' -f1


exit
