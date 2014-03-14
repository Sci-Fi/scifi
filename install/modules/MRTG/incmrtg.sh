#!/bin/bash                           
# version 20131017                     
# Include devices for MRTG
# Cosme Corrêa
# cosmefc@id.uff.br
# modified by schara@midiacom.uff.br
# Uncomment for debug
# set -xv

ERRO () {
echo $1
echo " "
echo Include APs for MRTG Monitoring
echo 
echo sintax:    $0 DEVICE [TEMPLATE  -f]
echo 
echo "exemples:  $0 ap0009"
echo "           $0 ap0009 TL-740N -f"
echo 
echo 'Templates List:'
ls  --format=single-column $DirTemplates | grep -v '.html' | grep -v '.php'
echo 
exit 1 
}

# Setting variables
SCIFIver=11
SCIFIverOID=.1.3.6.1.4.1.2021.8.1.100.1
SCIFIlabel=SCIFI
SCIFIlabelOID=.1.3.6.1.4.1.2021.8.1.101.1
SCIFItypeOID=.1.3.6.1.4.1.2021.8.1.101.3
DirTemplates=/etc/mrtg/templates/
DirMRTG=/etc/mrtg/
DirConfMRTG=/etc/mrtg/devices/
DirOutMRTG=/var/www/mrtg/
DISPOSITIVO=$1
TEMPLATE=$2
ForceFlag=$3
COMMUNITY=public
ARQINC=$DirMRTG"devices.inc"


case $# in
	1)
		#TEMPLATE=`snmpget -v 2c -c $COMMUNITY $DISPOSITIVO .1.3.6.1.4.1.2021.8.1.101.3` || ( echo "Someting is wrong about $DISPOSITIVO, test it" ; ERRO ; exit )
		TEMPLATE=`snmpget -v 2c -c $COMMUNITY $DISPOSITIVO .1.3.6.1.4.1.2021.8.1.101.3`
		if [ "$TEMPLATE" == "" ]
			then
			echo "Someting is wrong about $DISPOSITIVO, test it" ;
			ERRO ;
		fi
		TEMPLATE=`echo $TEMPLATE | cut -d" " -f4-`
		;;
	3)
		if [ "$ForceFlag" != '-f' ]
			then 
		 	echo 'Wrong parameters' ;
			ERRO ;
		fi
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

# Testa se MODEDLO existe em DirTemplates
if ! [ -a $DirTemplates$TEMPLATE ]
	then
	echo "Model '$TEMPLATE' does not exist";
	ERRO;
	exit;
fi

# Get name using SNMP
#NOME=`snmpget -v 1 -c public $DISPOSITIVO sysName.0 | cut -d" " -f4-`
NOME=$DISPOSITIVO
PNOME=`echo $NOME | cut -d"." -f1`

# Pega local por SNMP
LOCAL=`snmpget -v 1 -c public $DISPOSITIVO sysLocation.0 | cut -d" " -f4-`
# adicionei a linha abaixo - sch 27/07/2013
LOCALTXT=`snmpget -v 1 -c public $DISPOSITIVO sysLocation.0 | cut -d" " -f4- | awk -F "_-22." '{print $1}'`

echo 1
# pega o indice da interface wlan0
lista=`snmpwalk -v 2c -c public $DISPOSITIVO 1.3.6.1.2.1.2.2.1.2 | grep wlan0 | awk -F "." '{print $2}'| awk '{print $1}'`

echo 2

for b in $lista ;
do
         if [ `/usr/bin/snmpget -v 1 -c public $DISPOSITIVO 1.3.6.1.2.1.2.2.1.7.$b |grep -c up`!=0 ]; then
                oidwlan=$b
        fi 
done
echo 3

# Arquivo de Configuração ##########
ARQCONF=$DirConfMRTG$PNOME'.cfg'

# Copia modelo
cp $DirTemplates$TEMPLATE $ARQCONF

# Ajusta Nome
        sed -i s/apxxxx/$PNOME/g $ARQCONF

# Em maiuscula
UPNOME=`echo $PNOME | tr [:lower:] [:upper:]`

# Ajuste Rotulo
        sed -i s/APxxxx/$UPNOME/g $ARQCONF

# Ajuste PNGTITLE & PAGETOP - adicionado sch 27/07/2013 
        sed -i s/YYYYY/$LOCALTXT/g $ARQCONF
echo 5

# Ajuste OID do WLAN
sed -i s/ZZZZZ/$oidwlan/g $ARQCONF
echo 6

# Ajusta Local
sed -i "s/LOCAL/$LOCAL/" $ARQCONF

# Arquivo de Indice ##########
ARQINDI=$DirOutMRTG$PNOME'/index.html'

# Cria Diretorio
if [ ! -d $DirOutMRTG$PNOME ]; then 
	mkdir $DirOutMRTG$PNOME
fi
#mkdir $DirOutMRTG$PNOME

# Copia modelo
cp $DirTemplates$TEMPLATE'.html'  $ARQINDI

# Ajusta Nome
        sed -i s/apxxxx/$PNOME/g $ARQINDI

# Ajusta Rotulo
        sed -i s/APxxxx/$UPNOME/g $ARQINDI

# Inclui na Lista
#if [ `grep -c $PNOME $ARQINC` -eq 0 ] ; then
#	echo 'Include: '$ARQCONF >> $ARQINC
#fi

#echo Sucesso na inclusão de $PNOME
#echo .

# Arquivo de IndiceP ##########
ARQINDIP=$DirOutMRTG$PNOME'/index.php'

# Copia modelo
cp $DirTemplates$TEMPLATE'.php'  $ARQINDIP

# Ajusta Nome
        sed -i s/apxxxx/$PNOME/g $ARQINDIP

# Ajusta Rotulo
	sed -i s/APxxxx/$UPNOME/g $ARQINDIP


# Inclui na Lista
if [ `grep -c $PNOME $ARQINC` -eq 0 ] ; then
	echo 'Include: '$ARQCONF >> $ARQINC
fi

echo Success including $PNOME
echo .


exit

