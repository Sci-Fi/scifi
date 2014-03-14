#!/bin/bash                           
# version 20130921                     
# Include devices for SCIFI
# Cosme Corrêa
# cosmefc@id.uff.br
#
# Steps:
#  1- Initial checks
#  2- Get some informations
#  3- SetUP DNSMASQ
#  3- Call incmrtg.sh
#  5- Call incnagios.sh
#  6- Call incWebController.sh
#
# Uncomment for debug
# set -xv

exit

ERRO () {
echo $1
echo " "
echo Include APs in SCIFI
echo 
echo sintax:    $0 DEVICE 
echo 
echo "exemples:  $0 ap0009"
echo 
echo 
exit 1 
}

SetUpDNSMASQ () {

#test/include in /etc/dnsmasq.d/hosts
# Waiting definitions

# stop dnsmasq
service dnsmasq stop

# clean history in /var/lib/dnsmasq/dnsmasq.leases
NewLeases=`grep -v $DISPOSITIVO /var/lib/dnsmasq/dnsmasq.leases`
echo $NewLeases > /var/lib/dnsmasq/dnsmasq.leases

# restart AP
# Need talk to Helga

# start dnsmasq
service dnsmasq start

}



# Main routine
#  1- Initial checks

#Setting variables
SCIFIver=11
SCIFIverOID=.1.3.6.1.4.1.2021.8.1.100.1
SCIFIlabel=SCIFI
SCIFIlabelOID=.1.3.6.1.4.1.2021.8.1.101.1
SCIFItypeOID=.1.3.6.1.4.1.2021.8.1.101.3
DirTemplates=/etc/mrtg/templates/
DirMRTG=/etc/mrtg/
DirConfMRTG=/etc/mrtg/devices/
DirOutMRTG=/var/www/mrtg/
DISPOSITIVO=`cut -c1-6 $1`
TEMPLATE=$2
ForceFlag=$3
COMMUNITY=public
ARQINC=$DirMRTG"devices.inc"

case $# in
        1)
                TEMPLATE=`snmpget -v 2c -c $COMMUNITY $DISPOSITIVO .1.3.6.1.4.1.2021.8.1.101.3`
                if [ "$TEMPLATE" == "" ]
                        then
                        echo "Someting is wrong about $DISPOSITIVO, test it" ;
                        ERRO ;
                fi
                TEMPLATE=`echo $TEMPLATE | cut -d" " -f4-`
                ;;
        *)
                echo 'Wrong number of parameters';
                ERRO;
                exit
                ;;
esac

# Is this a SCIFI devive?
[ $SCIFIlabel != `snmpget -v 2c -c $COMMUNITY $DISPOSITIVO $SCIFIlabelOID | cut -d" " -f4-` ] && ERRO "This is not a SCIFI device"

# Is this a right version od SCIFI?
[ $SCIFIver != `snmpget -v 2c -c $COMMUNITY $DISPOSITIVO $SCIFIverOID | cut -d" " -f4-` ] && ERRO 'Wrong version of SCIFI device'

#  2- Get some informations


#  3- SetUP DNSMASQ
SetUpDNSMASQ $DISPOSITIVO

#  3- Call incmrtg.sh
incmrtg.sh $DISPOSITIVO

#  5- Call incnagios.sh
incnagios $DISPOSITIVO

#  6- Call incWebController.sh
incWebController.sh $DISPOSITIVO


exit

