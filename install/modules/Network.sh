#!/bin/bash
# Easy Life SCIFI
#
# Configuration Tool for an Easy Life
# Version 20130819
#
# Network module
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
                Network Module

  You must do it manualy.
  
           Hostname: $MACHINE.$DOMAINWIFI
  
  External Inteface: $EXTINT
            IP     : $EXTIP
            MASK   : $EXTMASK
            Gateway: $IGIP
            
  Internal Inteface: $INTINT
            IP     : 10.0.0.1
            MASK   : 255.0.0.0
            
     Search Domains: $DOMAINWIFI,$DOMAIN

   

  Press <Enter> to continue

EOF
echo "10.0.0.1 $MACHINE.$DOMAINWIFI" >> /etc/hosts
read
nm-connection-editor
system-config-network
echo  'Press <Enter> key'
read

