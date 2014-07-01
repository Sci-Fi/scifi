#!/bin/bash
# Easy Life SCIFI
#
# Configuration Tool for an Easy Life
# Version 20130917
#
# LDAP module
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
                  LDAP Module

  This module will:
  *) Install LDAP
  *) Copy scripts
  *) Create BKP structure
  *) Insert BKP in cron
  *) Copy Schemas
  *) Setup slapd.conf
  *) Populate LDAP
  *) Setup Auth
  *) Setup Log
  *) Start

  Press <Enter> key

EOF
read

#0 Install LDAP
yum install openldap-clients openldap nss-pam-ldapd openldap-servers  -y

#1 Copy scripts
cp -p $ModDir/LDAP/ldap.sh $SCRIPTDIR
chmod 700 $SCRIPTDIR'ldap.sh'
chown root:root $SCRIPTDIR'ldap.sh'

cp -p $ModDir/LDAP/fazbkp.sh $SCRIPTDIR
chmod 700 $SCRIPTDIR'fazbkp.sh'
chown root:root $SCRIPTDIR'fazbkp.sh'

cp -p $ModDir/LDAP/restauraLDAP.sh $SCRIPTDIR
chmod 700 $SCRIPTDIR'restauraLDAP.sh'
chown root:root $SCRIPTDIR'restauraLDAP.sh'
cd /usr/bin
ln -s $SCRIPTDIR'fazbkp.sh' .
ln -s $SCRIPTDIR'restauraLDAP.sh' .
ln -s $SCRIPTDIR'ldap.sh' .
mv /etc/openldap/DB_CONFIG.example /etc/openldap/DB_CONFIG.example.`date +%Y%m%d-%H%M%S` 2>/dev/null
cp $ModDir/LDAP/DB_CONFIG.example /etc/openldap/

#2 Create BKP structure
mkdir -p /home/LDAP
chmod 700 /home/LDAP

#3 Insert BKP in cron
cp -p $ModDir/LDAP/ldap.cron /etc/cron.d/ldap
service crond restart

#4 Copy Schemas
cp -p $ModDir/LDAP/schema/* /etc/openldap/schema

#5 Setup slapd.conf
mv /etc/openldap/slapd.d /etc/openldap/slapd.d.`date +%Y%m%d-%H%M%S`
mv /etc/openldap/slapd.conf /etc/openldap/slapd.conf.`date +%Y%m%d-%H%M%S` 2>/dev/null
cp -p $ModDir/LDAP/slapd.conf /etc/openldap/
chmod 640 /etc/openldap/slapd.conf
chown ldap:ldap /etc/openldap/slapd.conf
# subs
sed -i s/LDAPSUFIX/$LDAPSUFIX/g /etc/openldap/slapd.conf
sed -i s/LDAPADMPASSWD/$LDAPADMPASSWD/g /etc/openldap/slapd.conf
sed -i s/LDAPSUFIX/$LDAPSUFIX/g $SCRIPTDIR/ldap.sh
sed -i s/LDAPADMPASSWD/$LDAPADMPASSWD/g $SCRIPTDIR/ldap.sh

#6 Populate LDAP
. $ModDir/LDAP/populate.sh

#7 Setup Auth
authconfig --passalgo=sha512 --enableldap --enableldapauth --ldapserver=$LDAPSERVER --ldapbasedn=$LDAPSUFIX --disablesmartcard --enableforcelegacy --enablemkhomedir --updateall

#8 Setup log
mv /etc/rsyslog.d/slapd.conf /etc/rsyslog.d/slapd.conf.`date +%Y%m%d-%H%M%S` 2>//dev/null
cp -p $ModDir/LDAP/slapd.rsyslog /etc/rsyslog.d/slapd.conf
touch /var/log/slapd.log
service rsyslog restart
cp -p $ModDir/LDAP/slapd.logrotate /etc/logrotate.d/slapd

#9 Start
chkconfig slapd on
service slapd restart

echo LDAP module finished
echo 'Press <Enter> to exit'
read
