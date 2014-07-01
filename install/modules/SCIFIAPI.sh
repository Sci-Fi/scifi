#!/bin/bash
# Easy Life SCIFI
#
# Configuration Tool for an Easy Life
# Version 20130918
#
# SCIFIAPI module
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
                SCIFIAPI Module

  This module will:
  *) Copy scripts
  *) Create links
  *) Some setup
  

  Press <Enter> key

EOF
read

#1
cp -rp $ModDir/SCIFIAPI /usr/share/scifi/

#2 
ln -s /usr/share/scifi/SCIFIAPI/current/up-ap.sh /usr/share/scifi/scripts/
ln -s $SCRIPTDIR/up-ap.sh /usr/bin/

#3
chmod 700 /usr/share/scifi/SCIFAPI/*

echo SCIFIAPI module finished
echo 'Press <Enter> to exit'
read
