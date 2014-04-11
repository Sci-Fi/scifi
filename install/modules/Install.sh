#!/bin/bash
# Easy Life SCIFI
#
# Configuration Tool for an Easy Life
# Version 20130819
#
# Install module
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
                  Install Module

  This module will install:
  1) EPEL
  2) Some utilities
  3) Create Directories
  4) Copy conf files
  5) Some subs

  Press <Enter> key

EOF
read

# 1) EPEL
echo Installing EPEL
yum localinstall "$ModDir"Install/epel-release-6-8.noarch.rpm -y --nogpgcheck

# 2) Utilities
echo Installing Utilities
yum install screen vim htop tree coreutils yumex setuptool authconfig glibc-common openssl unzip -y

# 3) Create directories
mkdir -p $SCRIPTDIR
mkdir /etc/scifi

# 4) Copy conf files
cp -f $ModDir/Install/etcscifi/* /etc/scifi/

echo Install module finished
echo 'Press <Enter> to exit'
read
