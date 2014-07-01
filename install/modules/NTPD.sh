#!/bin/bash
# Easy Life SCIFI
#
# Configuration Tool for an Easy Life
# Version 20130923
#
# NTPD module
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
                  NTPD Module

  This module will:
  *) Install NTPD
  *) Copy Templates
  *) Setup Cron
  *) Setup Log
  *) Setup Apache
  *) Setup NTPD

  Press <Enter> key

EOF
read

#1 Install NTPD
#yum install ntp ntpdate -y
yum install ntp -y

#2 Copy Templates
mv /etc/ntp.conf /etc/ntp.conf.`date +%Y%m%d-%H%M%S` 2>/dev/null
cp -pr $ModDir/NTPD/ntp.conf /etc/

#3 NTPD start
chkconfig ntpd
service ntpd restart
#chkconfig ntpdate
#service ntpdate restart

echo NTPD module finished
echo 'Press <Enter> to exit'
read
