#!/bin/sh                           
# version 20130703
# # of system users
# Cosme Corrêa
# cosmefc@id.uff.br
# Luiz Magalhaes
# schara (at) telecom.uff.br
#
#  Descomente para debug
#set -xv

ERRO () {
echo 
echo '# of system users'
echo 
echo sintax:   $0  
echo 
echo example:   $0 
echo 
echo Obs: using /etc/mrtg/devices.inc
exit
}

USUSNMP () {
	echo `snmpget -v 2c -c $1 $2 .1.3.6.1.4.1.2021.8.1.101.1 2> /dev/null | cut -d: -f4`
}

USUMRTG () {
if [ `head -1 /var/www/mrtg/"$1"/"$1"_usu.log | cut -d' ' -f3` != '-1' ] 
	then
	echo `head -2 /var/www/mrtg/"$1"/"$1"_usu.log | tail -1 | cut -d' ' -f2`
fi
}

# Variables
TOTAL=0
COMMUNITY=public

# Test # of parametres
if [ "$#" -ne "0" ]
	then
	echo 'Erro, nº errado de parâmetros';
	ERRO;
	exit;
fi

#if [ "`/usr/share/scifi/scripts/scifi-type.sh`" = "CONTROLLER" ]
#	then
	# make APs list
	LISTA=`cut -d'/' -f5 /etc/mrtg/devices.inc | grep ap`
	for AP in $LISTA
		do
		AP=`expr substr $AP 1 6`
#		USU=`USUSNMP $COMMUNITY $AP`
		USU=`USUMRTG $AP`
		USU=${USU/' '/}
		TOTAL=$(( TOTAL + USU ))
		done
#	else
#	export PATH=/bin:/sbin:/usr/bin:/usr/sbin;
	# total of connected clients,  2 wireless interfaces
#	nsta1=$(iw wlan0 station dump | grep -c Station)
#	nsta2=$(iw wlan0-1 station dump | grep -c Station)
#	TOTAL=$(($nsta1+$nsta2))
#fi
echo $TOTAL
exit $TOTAL
