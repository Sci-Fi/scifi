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
    sed -i '/sh \/usr\/share\/scifi\/StartController.sh/d' /etc/rc.local 
    # a) install SCIFI core
    cp -r $ModDir/SCIFICore/* /usr/share/scifi
    echo $SCIFIDBPASSWD >> /usr/share/scifi/login_config
    echo "sh /usr/share/scifi/StartController.sh" >> /etc/rc.local
    chown scifi:scifi /usr/share/scifi/* 

  fi
 fi
fi

echo SCIFICore module finished
echo 'Press <Enter> to exit'
read
