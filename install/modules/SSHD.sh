#!/bin/bash
# Easy Life SCIFI
#
# Configuration Tool for an Easy Life
# Version 20130918
#
# LDAP module
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
                 SSHD Module

  This module will:
  *) Install SSHD
  *) Copy Template
  *) Setup
  *) Start

  Press <Enter> key

EOF
read

#1
yum install openssh-server -y

#2
mv /etc/ssh/sshd_config /etc/ssh/sshd_config.`date +%Y%m%d-%H%M%S` 2>/dev/null
cp -p $ModDir/SSHD/sshd_config /etc/ssh/

#3
#sed -i s/SSHDGROUP/$SSHDGROUP/g /etc/ssh/sshd_config
case "$SSHDAUTH" in
	[gG])
		ALLOWAUTH="AllowGroups "$SSHDGROUP
		;;
	[uU])
		ALLOWAUTH="AllowUsers "$SSHDUSERSP
		;;
esac
sed -i s/ALLOWAUTH/"$ALLOWAUTH"/g /etc/ssh/sshd_config

#4
chkconfig sshd on
service sshd restart

echo SSHD module finished
echo 'Press <Enter> to exit'
read
