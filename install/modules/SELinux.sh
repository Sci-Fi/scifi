#!/bin/bash
# Easy Life SCIFI
#
# Configuration Tool for an Easy Life
# Version 20131001
#
# SELinux module
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
                 SELinux Module

  This module will:
  *) Copy Templates
  *) Setup SELinux


  Press <Enter> to continue
EOF
read


#1 Copy Templates
mv /etc/selinux/config /etc/selinux/config.`date +%Y%m%d-%H%M%S`
cp -pr "$ModDir"SELinux/config /etc/selinux/config

#2 Setup SELinux
sed  -i s/SELPOL/$SELPOL/g /etc/selinux/config

echo SELinux Module finished
echo This machine must reboot
echo 'Press <Enter> to exit'
read
