#!/bin/bash
# Easy Life SCIFI
#
# Configuration Tool for an Easy Life
# Version 20140401
#
# SCIFIDatabase module
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
SCIFIDatabase Module

This module will:
a) Create SCIFI default database user called "scifi" 
b) Create scifi database called "scifidb"

Press <Enter> key

EOF

read

# testing if postgres is installed

psql=$(! which psql &> /dev/null; echo $?);

if [ $psql -eq 0 ]
 then
  echo "**********************************************"
  echo "ERROR: Please install PostgreSQL module first."
  echo "**********************************************"
 else
  
  # making scifidb backup and resetting db configuration
  scifidb=$(! su - postgres -c "psql -c \"SELECT datname FROM pg_catalog.pg_database WHERE datname='scifidb'\"" | grep scifidb &>/dev/null; echo $?)
  if [ $scifidb -eq 1 ]; 
   then
    backup="$ModDir/SCIFIDatabase/scifidb.old.backup.$(date +%Y%m%d-%H%M%S)"
    touch $backup
    chown postgres:postgres $backup
    su - postgres -c "pg_dump --format=tar scifidb > $backup"
  fi
  
  su - postgres -c "psql postgres -c \"DROP DATABASE IF EXISTS scifidb\""
  su - postgres -c "psql postgres -c \"DROP ROLE IF EXISTS scifi \""
 
  # a) Create SCIFI default database user called "scifi" 
  su - postgres -c "psql postgres -c \"CREATE ROLE scifi WITH INHERIT LOGIN CONNECTION LIMIT -1 PASSWORD '$SCIFIDBPASSWD' VALID UNTIL 'infinity'\""

  # b) Create scifi database called "scifidb"
  su - postgres -c "psql postgres -c \"CREATE DATABASE scifidb WITH OWNER scifi TEMPLATE template0 ENCODING 'UTF8' LC_COLLATE 'pt_BR.UTF-8' LC_CTYPE 'pt_BR.UTF-8' TABLESPACE pg_default CONNECTION LIMIT -1;\""
  su - postgres -c "pg_restore -d scifidb -v $ModDir'SCIFIDatabase/'scifidb.backup"

fi

echo SCIFIDatabase module finished
echo 'Press <Enter> to exit'
read
