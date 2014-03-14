#!/bin/sh
# version 20131021
# Include devices for Nagios
# Cosme Corrêa
# cosmefc@id.uff.br
# Uncomment for debug
set -xv

ERRO () {
echo $1
echo " "
echo Include APs for Nagios Monitoring
echo 
echo sintax:    $0 DEVICE [TEMPLATE  -f]
echo 
echo "examples:  $0 ap0009"
echo "           $0 ap0009 TL-740N -f"
echo 
echo 'Templates List:'
ls  --format=single-column $DirTemplates
echo 
exit 1 
}

# Setting variables
SCIFIver=12
SCIFIOIDEC=.1.3.6.1.4.1.2021.8.1.100. # Exit code
SCIFIOIDST=.1.3.6.1.4.1.2021.8.1.101. # String

SCIFIverOID=.1.3.6.1.4.1.2021.8.1.100.1
SCIFIlabel=SCIFI
SCIFIlabelOID=.1.3.6.1.4.1.2021.8.1.101.1
SCIFItypeOID=.1.3.6.1.4.1.2021.8.1.101.3
SCIFIcoordinatesOID=.1.3.6.1.4.1.2021.8.1.101.4
SCIFItagsOID=.1.3.6.1.4.1.2021.8.1.101.5
SCIFIconnected2OID=.1.3.6.1.4.1.2021.8.1.101.6
DirTemplates=/etc/nagios/templates/
DirNagios=/etc/nagios/
DirConfAPs=/etc/nagios/aps/

DEVICE=$1
UPDEVICE=` echo $DEVICE | cut -d"." -f1 | tr [:lower:] [:upper:]`
DWDEVICE=`echo $UPDEVICE | tr [:upper:] [:lower:]`
TEMPLATE=$2
ForceFlag=$3
COMMUNITY=public
# Is this a SCIFI devive?
[ "$SCIFIlabel" != "`snmpget -v 2c -c $COMMUNITY $DEVICE $SCIFIlabelOID | cut -d' ' -f4-`" ] && ERRO "This is not a SCIFI device"
read
# Is this version ok?
[ "$SCIFIver" != "`snmpget -v 2c -c $COMMUNITY $DEVICE $SCIFIverOID | cut -d' ' -f4-`" ] && ERRO "This device must be $SCIFIver version"

DEVLOCATION=`snmpget -v 2c -c public $DEVICE sysLocation.0`
TEMPLATE=`snmpget -v 2c -c $COMMUNITY $DEVICE $SCIFItypeOID`
COORDINATES=`snmpget -v 2c -c $COMMUNITY $DEVICE $SCIFIcoordinatesOID`
TAGS=`snmpget -v 2c -c $COMMUNITY $DEVICE $SCIFItagsOID`
COORDINATES=`snmpget -v 2c -c $COMMUNITY $DEVICE $SCIFIcoordinatesOID`
CONNECTED2=`snmpget -v 2c -c $COMMUNITY $DEVICE $SCIFIconnected2OID`
read

# Does template exist?
[ -a $DirTemplates$TEMPLATE ] || ERRO "Model '$TEMPLATE' does not exist"

# Copy template
# Arquivo de Configuração #
CONFILE=$DirConfAPs$DWDEVICE'.cfg'

# Copia modelo
cp $DirTemplates$TEMPLATE $CONFILE


# Some subs
sed -i s/APxxxx/$UPNOME/g $CONFILE
sed -i s/DEVLOCATION/$DEVLOCATION/g $CONFILE
sed -i s/TAGS/$TAGS/g $CONFILE
sed -i s/DEVLOCATION/$DEVLOCATION/g $CONFILE
sed -i s/DEVICE/$DEVICE/g $CONFILE
sed -i s/CONNECTED2/$CONNECTED2/g $CONFILE



# Create groups
# TO DO