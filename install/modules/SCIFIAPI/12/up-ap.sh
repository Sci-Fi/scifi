#!/bin/sh                           
# version 20131104
# Update API in APs
# Cosme Corrêa - cosmefc@id.uff.br
# uncomment for debug
#set -xv

ERRO () {
echo 
echo 'Update APs API'
echo 
echo sintax:   $0  
echo 
echo example:   $0 
echo 
exit
}

# Test # of parameters
if [ "$#" -ne "0" ]
	then
	echo 'Error, wrong # of parameters';
	ERRO;
	exit;
fi

if [ "`/usr/share/scifi/scripts/scifi-type.sh`" = "CONTROLLER" ]
	then
#	copy pacakge to ap
	scp -pri /etc/scifi/controller_key /etc/scifi/SCIFIAPI root@$1:/tmp/
#	execute it remotely
	ssh -i /etc/scifi/controller_key  root@$1 '/tmp/SCIFIAPI/up-ap.sh'
	else
#	export PATH=/bin:/sbin:/usr/bin:/usr/sbin;
#	preserve old directory
	mv /usr/share/scifi/scripts/ /usr/share/scifi/scripts/.`date +%Y%m%d-%H%M%S` 2>/dev/null
	mkdir /usr/share/scifi/scripts/
#	copy this
	copy -f /tmp/SCIFIAPI/*.sh /usr/share/scifi/scripts/
	copy -f /tmp/SCIFIAPI/scifi-version.txt /etc/scifi/
	copy -f /tmp/SCIFIAPI/scifi-subversion.txt /etc/scifi/
	chmod 700 /usr/share/scifi/scripts/ -R
fi

exit 0 
