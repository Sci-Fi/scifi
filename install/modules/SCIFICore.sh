#!/bin/bash
# Easy Life SCIFI
#
# Configuration Tool for an Easy Life
# Version 20140411
#
# SCIFICore module
#
# Cosme Faria Corrêa
# helgadb 
# 
# ...
#
#set -xv

clear

cat <<-EOF
=========================================
| Easy Life for SCIFI |
=========================================
SCIFICore Module

This module will:
a) Install SCIFI Core, rev 206 (svn) 
b) Create linux user "scifi" and set his password 
c) Create SSH key for communication between the APs and scifi
d) Initialize SCIFI core and web

Press <Enter> key

EOF

read

# testing if postgre, scifi database and java are installed
java=$(! which java &> /dev/null; echo $?);
if [ $java -eq 0 ]
 then
  echo "****************************************"
  echo "ERROR: Please install JavaJDK module first."
  echo "****************************************"
 else
  psql=$(! which psql &> /dev/null; echo $?);
  if [ $psql -eq 0 ]
   then
    echo "**********************************************"
    echo "ERROR: Please install PostgreSQL module first."
    echo "**********************************************"
   else
   scifidb=$(! su - postgres -c "psql -c \"SELECT datname FROM pg_catalog.pg_database WHERE datname='scifidb'\"" | grep scifidb &>/dev/null; echo $?)
   if [ $scifidb -eq 0 ]
    then
     echo "*************************************************"
     echo "ERROR: Please install SCIFIDatabase module first."
     echo "*************************************************"
    else
    # resetting configurations
    if [ -f /usr/share/scifi/core/controller_key ]; then cp /usr/share/scifi/core/controller_key $ModDir/SCIFICore/controller_key.old.$(date +%Y%m%d-%H%M%S);fi; 
    if [ -f /usr/share/scifi/core/authorized_keys ]; then cp /usr/share/scifi/core/authorized_keys $ModDir/SCIFICore/authorized_keys.old.$(date +%Y%m%d-%H%M%S);fi; 
    rm -rf /usr/share/scifi/core 2> /dev/null
    sed -i '/sh \/usr\/share\/scifi\/core\/StartController.sh/d' /etc/rc.local 
    # a) Install SCIFI core
    mkdir /usr/share/scifi/core
    cp -r $ModDir/SCIFICore/* /usr/share/scifi/core
    rm /usr/share/scifi/core/*old* 2> /dev/null
    echo $SCIFIDBPASSWD >> /usr/share/scifi/core/login_config
    echo "sh /usr/share/scifi/core/StartController.sh" >> /etc/rc.local
    
    # b) Create linux user "scifi" and set his password 
    adduser -U scifi
    echo -e "$SCIFIPASSWD\n$SCIFIPASSWD" | passwd scifi
    chown -fR scifi:scifi /usr/share/scifi/core 
    chmod 0600 /usr/share/scifi/core/login_config
    
    #c) Create SSH key for communication between the APs and scifi
    su - scifi -c "cd /usr/share/scifi/core/; ssh-keygen -t rsa -f controller_key -q -N \"\""
    mv /usr/share/scifi/core/controller_key.pub /usr/share/scifi/core/authorized_keys
    chmod 0600 /usr/share/scifi/core/controller_key
    chmod 0600 /usr/share/scifi/core/authorized_keys

    #d) Initialize SCIFI core and web
    sh /usr/share/scifi/core/StartController.sh
   
  fi
 fi
fi

echo SCIFICore module finished
echo 'Press <Enter> to exit'
read
