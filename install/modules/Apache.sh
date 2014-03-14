#!/bin/bash
# Easy Life SCIFI
#
# Configuration Tool for an Easy Life
# Version 20130917
#
# Apache Module
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
                Apache Module

  This module will:
  1) Install Apache
  2) Start Apache

    Press <Enter> key

EOF
read

#1 Install Apache
yum install httpd httpd-tools mod_authz_ldap  -y

#2 Start Apache
chkconfig httpd on
service httpd restart

echo Apache module finished
echo 'Press <Enter> to exit'
read
