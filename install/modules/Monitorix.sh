#!/bin/bash
# Easy Life SCIFI
#
# Configuration Tool for an Easy Life
# Version 20130819
#
# Monitorix module
#
# Cosme Faria Corrêa
# John Doe
# ...
#
# set -xv        

clear
cat <<-EOF
  =========================================
  |           Easy Life for SCIFI         |
  =========================================
                Monitorix Module

  This module will:
  1) Install Monitorix
  2) Setup Monitorix

  Press <Enter> key

EOF
read

#1 Installing Monitorix
echo Installing Monitorix
yum install monitorix -y


#2 Setup Monitorix
echo Setup Monitorix
# Fix a problem
chown 755 /etc/init.d/monitorix
# Setup monitorix.conf
sed -i s/'Place a title here'/$MACHINE' - Monitoring'/g /etc/monitorix/monitorix.conf
sed -i s/'enabled = y'/'enabled = n'/g /etc/monitorix/monitorix.conf
# Setup Monitorix in HTTPD
case "$MONITORIXAUTH" in
    [nN] )
      cp /usr/share/doc/monitorix*/monitorix-apache.conf /etc/httpd/conf.d/
      ;;
    [yY] )
      cp $ModDir/Monitorix/Monitorix-users.conf /etc/httpd/conf.d/monitorix-apache.conf
      ;;
    [gG] )
      cp $ModDir/Monitorix/Monitorix-group.conf /etc/httpd/conf.d/monitorix-apache.conf
      ;;
esac
sed -i s/LDAPSERVER/$LDAPSERVER/g /etc/httpd/conf.d/monitorix-apache.conf
sed -i s/LDAPSUFIX/$LDAPSUFIX/g /etc/httpd/conf.d/monitorix-apache.conf
sed -i s/MONITORIXGROUP/$MONITORIXGROUP/g /etc/httpd/conf.d/monitorix-apache.conf

# Fix a problem
mkdir -p /usr/share/monitorix/imgs/
chown apache:apache /usr/share/monitorix/imgs/ -R
chown apache:apache /var/lib/monitorix/www/imgs/ -R

# Setup Monitorix start
chkconfig monitorix on
service monitorix restart
service httpd restart

echo Monitorix module finished
echo 'Press <Enter> to exit'
read
