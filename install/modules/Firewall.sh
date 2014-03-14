#!/bin/bash
# Easy Life SCIFI
#
# Configuration Tool for an Easy Life
# Version 20130819
#
# Firewall module
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
                  Firewall Module

  This module will:
  1) Install packets for FirewallB
  2) Setup FirewallB
  3) Install FW
  4) Start

  Press <Enter> key

EOF

read

# Install Firewall
wget http://www.fwbuilder.org/PACKAGE-GPG-KEY-fwbuilder.asc
rpm --import PACKAGE-GPG-KEY-fwbuilder.asc
cp -f $ModDir/Firewall/fwbuilder.repo /etc/yum.repos.d/fwbuilder.repo
yum install fwbuilder -y

cp -p $ModDir/Firewall/firewall /etc/init.d/
cp -p $ModDir/Firewall/FW-SCIFI.fw /etc/init.d/
cp -p $ModDir/Firewall/FW-SCIFI.fwb /usr/share/scifi/scripts

#4
chkconfig firewall on
chkconfig iptables 0ff
chkconfig ip6tables 0ff
#service firewall start

cat <<-EOF

Firewall module finished

You must:
- setup network in firewall
  - fwbuilder /usr/share/scifi/scripts/FW-SCIFI.fwb
- save
- compile
- overwrite
  - /etc/init.d/FW-SCIFI.fw
- restart firewall
  - service firewall restart 

echo Press <Enter> to exit

EOF
read
