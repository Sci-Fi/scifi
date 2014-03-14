#!/bin/bash
# Easy Life SCIFI
#
# Configuration Tool for an Easy Life
# Version 20130917
#
# MRTG module
#
# Cosme Faria Corrêa
# John Doe
# ...
#
#set -xv        

clear


cat <<-EOF
  =========================================
  |           Easy Life for SCIFI         |
  =========================================
                  MRTG Module

  This module will:
  1) Install MRTG
  2) Copy Templates
  3) Setup Cron
  4) Setup Log
  5) Setup Apache
  6) Setup MRTG
  7) Scripts
  8) Start Apache
  
  Press <Enter> key

EOF
read

#1 Install MRTG
yum install mrtg php -y

#2 Copy Templates
mv /etc/mrtg /etc/mrtg.`date +%Y%m%d-%H%M%S`
cp -pr $ModDir/MRTG/etcmrtg /etc/mrtg 
mv /var/www/mrtg /var/www/mrtg.`date +%Y%m%d-%H%M%S`
cp -pr $ModDir/MRTG/varwwwmrtg /var/www/mrtg

#3 Setup Cron
rm /etc/cron.d/mrtg
cp -p $ModDir/MRTG/mrtg.cron /etc/cron.d/mrtg

#4 Setup Log
mkdir /var/log/mrtg
rm /etc/logrotate.d/mrtg 2>/dev/null
cp -p $ModDir/MRTG/mrtg.logrotate /etc/logrotate.d/mrtg

#5 Setup MRTG in Apache
rm /etc/httpd/conf.d/mrtg.conf
case "$MRTGAUTH" in
    [nN] )
      cp $ModDir/MRTG/mrtg.no.conf /etc/httpd/conf.d/mrtg.conf
      ;;
    [yY] )
      cp $ModDir/MRTG/mrtg.users.conf /etc/httpd/conf.d/mrtg.conf
      ;;
    [gG] )
      cp $ModDir/MRTG/mrtg.group.conf /etc/httpd/conf.d/mrtg.conf
      ;;
esac
sed -i s/LDAPSERVER/$LDAPSERVER/g /etc/httpd/conf.d/mrtg.conf
sed -i s/LDAPSUFIX/$LDAPSUFIX/g /etc/httpd/conf.d/mrtg.conf
sed -i s/MRTGGROUP/$MRTGGROUP/g /etc/httpd/conf.d/mrtg.conf

#6 Setup MRTG

# Subs

#7 Apache restart
service httpd restart

#8 Scripts
cp -f  $ModDir/MRTG/*.sh $SCRIPTDIR
ln -s $SCRIPTDIR/*.sh /usr/bin/ 2>/dev/null


echo MRTG module finished
echo 'Press <Enter> to exit'
read
