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

#1 Install repo FirewallB
cd /tmp
wget http://www.fwbuilder.org/PACKAGE-GPG-KEY-fwbuilder.asc
rpm --import PACKAGE-GPG-KEY-fwbuilder.asc
cp -f $ModDir/Firewall/fwbuilder.repo /etc/yum.repos.d/fwbuilder.repo

#2 Install repo FirewallB
yum install fwbuilder -y

#3 Setup FirewallB
cp -p $ModDir/Firewall/firewall /etc/init.d/
cp -p $ModDir/Firewall/FW-SCIFI.fw /etc/init.d/
cp -p $ModDir/Firewall/FW-SCIFI.fwb /usr/share/scifi/scripts

#4 start FB
chkconfig firewall on
chkconfig iptables 0ff
chkconfig ip6tables 0ff

#5 Install repo netfilter
cd /etc/yum.repos.d/
wget http://download.opensuse.org/repositories/security:netfilter/RHEL_6/security:netfilter.repo

#6 Install netfilter
yum install libnetfilter_conntrack3 -y
yum install conntrack-tools -y

#7 Install as a service


#8 Setup log 

#9 Setup logrotate




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
