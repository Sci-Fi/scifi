#!/bin/bash
# Easy Life SCIFI
#
# Configuration Tool for an Easy Life
# Version 20130918
#
# Nagios module
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
                  Nagios

  This module will:
  1) Install Nagios
  2) Copy Templates
  3) SetUp Templates
  4) Setup HTTPD
  5) Setup Start
  6) Scripts
  7) Put InternetGateway in /etc/hosts

  Press <Enter> key

EOF

#1 Install Nagios
yum  install nagios nagios-plugins-all -y

#2 Copy Templates
mv /etc/nagios /etc/nagios.`date +%Y%m%d-%H%M%S`
cp -rp $ModDir/Nagios/nagios /etc/

#3 SetUp Templates
sed -i s/IGNAME/$IGNAME/g /etc/nagios/routers/InternetGateway.cfg
sed -i s/IGIP/$IGIP/g /etc/nagios/routers/InternetGateway.cfg

#4 Setup HTTPD
rm /etc/httpd/conf.d/nagios.conf
case "$NAGIOSAUTH" in
    [yY] )
      cp $ModDir/Nagios/nagios-users.conf /etc/httpd/conf.d/nagios.conf
      ;;
    [gG] )
      cp $ModDir/Nagios/nagios-group.conf /etc/httpd/conf.d/nagios.conf
      ;;
esac
sed -i s/LDAPSERVER/$LDAPSERVER/g /etc/httpd/conf.d/nagios.conf
sed -i s/LDAPSUFIX/$LDAPSUFIX/g /etc/httpd/conf.d/nagios.conf
sed -i s/NAGIOSGROUP/$NAGIOSGROUP/g /etc/httpd/conf.d/nagios.conf

#5
chkconfig nagios on
service nagios restart
service httpd restart

#6 Scripts
cp -f  $ModDir/Nagios/*.sh $SCRIPTDIR
ln -s $SCRIPTDIR/*.sh /usr/bin/ 2>/dev/null

#7 Put InternetGateway in /etc/hosts
sed -i s/$IGIP/'#'$IGIP/g /etc/hosts
echo $IGIP' '$IGNAME' #'" Added by EL-SCIFI - `date +%Y%m%d-%H%M%S`" >> /etc/hosts

echo Nagios module finished
echo 'Press <Enter> to exit'
read
