#!/bin/bash
# Easy Life SCIFI
#
# Configuration Tool for an Easy Life
# Version 20140623
#
# Firewall module
#
# Cosme Faria Corrêa - cosmefc@id.uff.br
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
  *) Download FirewallB
  *) Install FirewallB
  *) Setup FW
  *) Setup firewall logrotate
  *) Start

  Press <Enter> to continue

EOF

read

#1 Download FirewallB
cd /tmp
wget  http://ufpr.dl.sourceforge.net/project/fwbuilder/Current_Packages/5.1.0/fwbuilder-5.1.0.3599-1.el6.x86_64.rpm

#2 Install FirewallB
yum localinstall fwbuilder-5.1.0.3599-1.el6.x86_64.rpm -y

#3 Setup FirewallB
cp -p $ModDir/Firewall/firewall /etc/init.d/
cp -p $ModDir/Firewall/FW-SCIFI.fw /etc/init.d/
cp -p $ModDir/Firewall/FW-SCIFI.fwb /usr/share/scifi/scripts

#4 Setup logrotate
rm /etc/logrotate.d/iptables 2> /dev/null
cp -p $ModDir/Firewall/firewall.logrotate /etc/logrotate.d/iptables

#5 start FB
chkconfig firewall on
chkconfig iptables off
chkconfig ip6tables off


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

Press <Enter> to exit

EOF
read
