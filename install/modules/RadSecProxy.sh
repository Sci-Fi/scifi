#!/bin/bash
# Easy Life SCIFI
#
# Configuration Tool for an Easy Life
# Version 20140522
#
# RadSecProxy module
#
# Luiz Magalhaes
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
               RadSecProxy Module

  This module will:
  *) Install Packets
  *) Compile radsec
  *) Setup
  *) Start

  Press <Enter> key
  
EOF
read

#Uninished
# de acordo com o report, o exit abaixo está impedindo (obviamente) que o script continue...

exit

#1 Install Packets
yum install gcc openssl openssl-devel -y

#2 Compile radsec
cp $ModDir/RadSecProxy/radsecproxy-1.6.5.tar.gz /tmp/
cd /tmp
tar -xvf radsecproxy-1.6.5.tar.gz
cd radsecproxy-1.6.5
./configure && make && make install

#3 Setup
# some bkps
now=`date +%Y%m%d-%H%M%S`
mv /etc/raddb/clients.conf /etc/raddb/clients.conf.$now
mv /etc/raddb/proxy.conf /etc/raddb/proxy.conf.$now
mv /etc/radsecproxy.conf /etc/radsecproxy.conf.$now
# new confs
cp -Rf $ModDir/RadSecProxy/RNP/clients.conf /etc/raddb/clients.conf
cp -Rf $ModDir/RadSecProxy/RNP/proxy.conf /etc/raddb
cp -Rf $ModDir/RadSecProxy/RNP/radsecproxy.conf /etc
#  Certs
cp -Rf $ModDir/RadSecProxy/RNP/*.crt /etc/raddb/certs
cp -Rf $ModDir/RadSecProxy/RNP/*.key /etc/raddb/certs
# scifi net
cat $ModDir/RadSecProxy/scifi.txt >> /etc/raddb/clients.conf
sed -i s/RADIUSPASS/$RADIUSPASS/g /etc/raddb/clients.conf

#4 Start
cp $ModDir/RadSecProxy/radsecproxy /etc/init.d/
chmod 755 /etc/init.d/radsecproxy
chkconfig radsecproxy on
service radsecproxy restart
service radiusd restart

echo RadSecProxy  module finished
echo 'Press <Enter> to exit'
read
