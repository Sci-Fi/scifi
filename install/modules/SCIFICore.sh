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
    rm -rf /usr/share/scifi/core
    sed -i '/sh \/usr\/share\/scifi\/core\/StartController.sh/d' /etc/rc.local 
    # a) Install SCIFI core
    mkdir /usr/share/scifi/core
    cp -r $ModDir/SCIFICore/* /usr/share/scifi/core
    echo $SCIFIDBPASSWD >> /usr/share/scifi/core/login_config
    echo "sh /usr/share/scifi/core/StartController.sh" >> /etc/rc.local
    
    # b) Create linux user "scifi" and set his password 
    adduser -U scifi
    echo -e "$SCIFIPASSWD\n$SCIFIPASSWD" | passwd scifi
    chown -fR scifi:scifi /usr/share/scifi/core 
    chmod 0600 /usr/share/scifi/core/login_config
    
  fi
 fi
fi

echo SCIFICore module finished
echo 'Press <Enter> to exit'
read
