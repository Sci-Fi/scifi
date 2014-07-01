#!/bin/bash
# Easy Life SCIFI
#
# Configuration Tool for an Easy Life
# Version 20130913
#
# LogRotate module
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
                  LogRotate Module

  This module will:
  *) Setup rsyslog
  *) Install LogRotate
  *) Setup LogRotate

  Press <Enter> to continue

EOF
read

#0 Setup rsyslog
mv /etc/rsyslog.conf /etc/rsyslog.conf.`date +%Y%m%d-%H%M%S`
cp -p "$ModDir"Logs/rsyslog.conf /etc/

#1 Install LogRotate
echo Installing LogRotate
yum install logrotate -y 

#2 Setup LogRotate
mv /etc/logrotate.conf /etc/logrotate.conf.`date +%Y%m%d-%H%M%S`
cp $ModDir/Logs/logrotate.conf /etc/
sed -i s/DURATION/$DURATION/g /etc/logrotate.conf

echo LogRotate module finished
echo 'Press <Enter> to exit'
read
