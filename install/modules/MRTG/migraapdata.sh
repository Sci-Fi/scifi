#!/bin/bash                           
# Varsion 23130821
# Migrate MRTG data from an old device to a new device                     
# Cosme Corrêa
# cosmefc@id.uff.br
# uncomment for debug
# set -xv

ERRO () {
echo $1
echo 
echo Migrate data from oldAP for newAP
echo 
echo sintax:   $0 oldAP newAP  
echo 
echo exemple:   $0 ap0011 ap0111
echo 
echo Press any key to see AP list
read -n1
echo 
cd $DirConfMRTG
ls ap*.cfg | cut -d. -f1 | more
echo 
exit 1
}

# Setting variables
DirTemplates=/etc/mrtg/modelos/
DirMRTG=/etc/mrtg/
DirConfMRTG=/etc/mrtg/devices/
DirOutMRTG=/var/www/mrtg/
OLDDEV=`echo $1 | tr [:upper:] [:lower:]`
NEWDEV=`echo $2 | tr [:upper:] [:lower:]`
UOLDDEV=`echo $OLDDEV | tr [:lower:] [:upper:]`
UNEWDEV=`echo $NEWDEV | tr [:lower:] [:upper:]`
COMMUNITY=public
FileINC=$DirMRTG"devices.inc"
LockMRTG=/var/lock/mrtg/mrtg_l

# Test nº of parameters
if [ "$#" -ne "2" ]
	then
	ERRO 'Wrong number of parameters';
fi

# Is OLDDEV in MRTG?
grep $OLDDEV  $FileINC ||  ERRO `echo $OLDDEV is not in MRTG `

# Is NEWDEV in MRTG?
grep $NEWDEV  $FileINC && ERRO `echo $NEWDEV is in MRTG`

echo Erro aqui;
exit

# Is NEWDEV on?
if ! ping -c1 $NEWDEV > /dev/null 
then
	echo Is $NEWDEV ON?;
	ERRO; 
	exit
fi

# Is there NEWDEV in DNS?
if ! host $NEWDEV > /dev/null 
	then
	echo "No DNS for $NEWDEV";
	ERRO;
	exit;
fi

# MRTG runs every 5 minutes this script can not run at same time
[ -f $LockMRTG ] && ERRO `echo MRTG is running. Try latter`

# Setup /etc/mrtg/devices/NEWDEV.cfg
# This will be depreced by new incmrtg.sh
#mv $DirConfMRTG$OLDDEV.cfg $DirConfMRTG$NEWDEV.cfg
#sed -i s/$OLDDEV/$NEWDEV/g $DirConfMRTG$NEWDEV.cfg
#sed -i s/$UOLDDEV/$UNEWDEV/g $DirConfMRTG$NEWDEV.cfg
rm -f $DirConfMRTG$OLDDEV.cfg
/etc/mrtg/incmrtg.sh $NEWDEV || ERRO `echo 'There is something wrong with $NEWDEV'`

# Setup devices.inc
sed -i s/$OLDDEV/$NEWDEV/g $FileINC 

# Setup /var/www/mrtg/NEWDEV/
mv $DirOutMRTG$OLDDEV $DirOutMRTG$NEWDEV
sed -i s/$OLDDEV/$NEWDEV/g $DirOutMRTG$NEWDEV/index.*
sed -i s/$UOLDDEV/$UNEWDEV/g $DirOutMRTG$NEWDEV/index.*
for OLDNAME in `ls $DirOutMRTG$NEWDEV/$OLDDEV*`; do
  NEWNAME=${OLDNAME/$OLDDEV/$NEWDEV}
  mv $OLDNAME $NEWNAME
done
echo ""
echo Migratiom from $OLDDEV to $NEWDEV is ok

