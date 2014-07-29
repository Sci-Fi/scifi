#!/bin/bash
# Easy Life SCIFI
#
# Configuration Tool for an Easy Life
# Version 20140729
#
# Base module
#
# Cosme Faria Corrêa - cosmefc@id.uff.br
# John Doe
# ...
#
set -xv        

clear
cat <<-EOF
  =========================================
  |           Easy Life for SCIFI         |
  =========================================
                  Base Module

  This module will install:
  *) EPEL
  *) Some utilities
  *) Create Directories
  *) arp table setup

  Press <Enter> to continue

EOF
read

# 1) EPEL
echo Installing EPEL
if [ $SOVERSION -eq 7 ] 
	then
	EPEL='epel-release-7-0.2.noarch.rpm'
	else
	EPEL='epel-release-6-8.noarch.rpm';
fi

yum localinstall $ModDir"Base/"$EPEL  -y --nogpgcheck

# 2) Utilities
echo Installing Utilities
yum install git screen vim htop tree coreutils yumex setuptool authconfig glibc-common openssl unzip -y

# 3) Create directories
mkdir -p $SCRIPTDIR
mkdir /etc/scifi

#4) arp table setup
echo 'net.ipv4.neigh.default.gc_thresh1 = 4096' >> /etc/sysctl.conf
echo 'net.ipv4.neigh.default.gc_thresh2 = 8192' >> /etc/sysctl.conf
echo 'net.ipv4.neigh.default.gc_thresh3 = 8192' >> /etc/sysctl.conf


echo Base module finished
echo 'Press <Enter> to exit'
read