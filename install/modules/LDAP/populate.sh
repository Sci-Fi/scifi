#!/bin/bash
# Configuration Tool for an Easy Life
# Version 20130819

# Initialize LDPA

# 1- Stop  LDAP server
# 2- Clean base
# 3- Initial load
# 4- Setup permissions
# 5- Start  LDAP server
#
# Cosme Faria Corrêa
# John Doe
# ...
#
# Uncomment for  debug
# set -xv

DOMINIO_INST=$DOMAIN
RAIZ_BASE_LDAP="dc="`echo $DOMINIO_INST | sed -e 's/\./,dc=/g'`
ORGANIZACAO="`echo $RAIZ_BASE_LDAP | sed -e 's/^dc=//; s/,dc=/./'`"
DC="`echo $ORGANIZACAO | sed 's/^\.//; s/\..*$//'`"
PASS_ADMIN=$LDAPADMPASSWD
PASS_USER=$LDAPPRIMARYPASSWD
PASS_READER_SHIB=$SHIBPASS
PASS_READER_RADIUS=$RADIUSPASS
HASH_PASS_ADMIN=$( slappasswd -h {SSHA} -u -s $PASS_ADMIN )
HASH_PASS_USER=$( slappasswd -h {SSHA} -u -s $PASS_USER )
HASH_PASS_USER_NT=`printf $LDAPPRIMARYPASSWD | iconv -t utf16le | openssl md4 | cut -c10-`
HASH_PASS_READER_SHIB=$( slappasswd -h {SSHA} -u -s $PASS_READER_SHIB )
HASH_PASS_READER_RADIUS=$( slappasswd -h {SSHA} -u -s $PASS_READER_RADIUS )

# 1- Stop LDAP sever
/sbin/service slapd stop

# 2- Clean base
mv /var/lib/ldap/  /var/lib/ldap`date +%Y%m%d-%H%M%S`
mkdir /var/lib/ldap/
cp /etc/openldap/DB_CONFIG.example /var/lib/ldap/DB_CONFIG

# 3- Initial load
cat <<-EOF | slapadd
dn: $RAIZ_BASE_LDAP
objectClass: top
objectClass: dcObject
objectClass: organization
o: $ORGANIZACAO
dc: $DC
structuralObjectClass: organization

dn: ou=People,$RAIZ_BASE_LDAP
objectClass: organizationalUnit
objectClass: top
ou: People
structuralObjectClass: organizationalUnit

dn: ou=Group,$RAIZ_BASE_LDAP
objectClass: top
objectClass: organizationalUnit
ou: Group
structuralObjectClass: organizationalUnit

dn: sambaDomainName=$SAMBADOMAIN,$RAIZ_BASE_LDAP
sambaDomainName: $SAMBADOMAIN
sambaSID: $SAMBASID
structuralObjectClass: sambaDomain
entryUUID: e625ed54-58b0-1030-9b5d-81628d64ee16
creatorsName: cn=Manager,$RAIZ_BASE_LDAP
createTimestamp: 20110811215915Z
sambaMinPwdLength: 5
sambaPwdHistoryLength: 0
sambaLogonToChgPwd: 0
sambaMaxPwdAge: -1
sambaMinPwdAge: 0
sambaLockoutDuration: 30
sambaLockoutObservationWindow: 30
sambaLockoutThreshold: 0
sambaForceLogoff: -1
sambaRefuseMachinePwdChange: 0
objectClass: sambaDomain
sambaNextRid: 1000
sambaNextGroupRid: 1000
sambaNextUserRid: 1000
sambaAlgorithmicRidBase: 1000

dn: ou=Computer,$RAIZ_BASE_LDAP
ou: Computer
objectClass: organizationalUnit
structuralObjectClass: organizationalUnit

dn: ou=Idmap,$RAIZ_BASE_LDAP
ou: Idmap
objectClass: organizationalUnit
structuralObjectClass: organizationalUnit

dn: cn=users,ou=Group,$RAIZ_BASE_LDAP
structuralObjectClass: posixGroup
userPassword:: Kg==
objectClass: posixGroup
objectClass: top
gidNumber: 100
cn: users
memberUid: $LDAPPRIMARYUID

dn: cn=Domain Admins,ou=Group,$RAIZ_BASE_LDAP
gidNumber: 512
sambaGroupType: 2
displayName: Domain Admins
description: Netbios Domain Administrators
memberUid: $LDAPPRIMARYUID
objectClass: posixGroup
objectClass: sambaGroupMapping
sambaSID: $SAMBASID-512
cn: Domain Admins
structuralObjectClass: posixGroup

dn: cn=Domain Users,ou=Group,$RAIZ_BASE_LDAP
gidNumber: 513
sambaGroupType: 2
displayName: Domain Users
description: Netbios Domain Users
objectClass: posixGroup
objectClass: sambaGroupMapping
sambaSID: $SAMBASID-513
cn: Domain Users
structuralObjectClass: posixGroup
memberUid: $LDAPPRIMARYUID

dn: cn=Domain Guests,ou=Group,$RAIZ_BASE_LDAP
gidNumber: 514
sambaGroupType: 2
displayName: Domain Guests
description: Netbios Domain Guests
objectClass: posixGroup
objectClass: sambaGroupMapping
sambaSID: $SAMBASID-514
cn: Domain Guests
structuralObjectClass: posixGroup

dn: cn=Domain Computers,ou=Group,$RAIZ_BASE_LDAP
gidNumber: 515
sambaGroupType: 2
displayName: Domain Computers
description: Netbios Domain Computers
objectClass: posixGroup
objectClass: sambaGroupMapping
sambaSID: $SAMBASID-515
cn: Domain Computers
structuralObjectClass: posixGroup

dn: cn=Administrators,ou=Group,$RAIZ_BASE_LDAP
sambaGroupType: 5
displayName: Administrators
description: Netbios Domain Members can fully administer the computer/sambaDom
 ainName
sambaSID: S-1-5-32-544
structuralObjectClass: posixGroup
entryUUID: f19d14a0-58b0-1030-9b66-81628d64ee16
creatorsName: cn=Manager,$RAIZ_BASE_LDAP
createTimestamp: 20110811215934Z
objectClass: posixGroup
objectClass: sambaGroupMapping
objectClass: top
gidNumber: 544
cn: Administrators
memberUid: $LDAPPRIMARYUID

dn: cn=Account Operators,ou=Group,$RAIZ_BASE_LDAP
gidNumber: 548
sambaGroupType: 5
displayName: Account Operators
description: Netbios Domain Users to manipulate users accounts
objectClass: posixGroup
objectClass: sambaGroupMapping
sambaSID: S-1-5-32-548
cn: Account Operators
structuralObjectClass: posixGroup

dn: cn=Print Operators,ou=Group,$RAIZ_BASE_LDAP
gidNumber: 550
sambaGroupType: 5
displayName: Print Operators
description: Netbios Domain Print Operators
objectClass: posixGroup
objectClass: sambaGroupMapping
sambaSID: S-1-5-32-550
cn: Print Operators
structuralObjectClass: posixGroup

dn: cn=Backup Operators,ou=Group,$RAIZ_BASE_LDAP
gidNumber: 551
sambaGroupType: 5
displayName: Backup Operators
description: Netbios Domain Members can bypass file security to back up files
objectClass: posixGroup
objectClass: sambaGroupMapping
sambaSID: S-1-5-32-551
cn: Backup Operators
structuralObjectClass: posixGroup

dn: cn=Replicators,ou=Group,$RAIZ_BASE_LDAP
gidNumber: 552
sambaGroupType: 5
displayName: Replicators
description: Netbios Domain Supports file replication in a sambaDomainName
objectClass: posixGroup
objectClass: sambaGroupMapping
sambaSID: S-1-5-32-552
cn: Replicators
structuralObjectClass: posixGroup

dn: cn=NetAdmins,ou=Group,$RAIZ_BASE_LDAP
objectClass: posixGroup
objectClass: sambaGroupMapping
cn: NetAdmins
gidNumber: 1001
userPassword:: Kg==
memberUid: $LDAPPRIMARYUID
sambaSID: $SAMBASID-1001
displayName: Network Administrators
description: Network Administrators
sambaGroupType: 2
structuralObjectClass: posixGroup

dn: cn=Manager,$RAIZ_BASE_LDAP
objectClass: simpleSecurityObject
objectClass: organizationalRole
cn: Manager
description: Administrador da base LDAP
userPassword: $HASH_PASS_ADMIN

dn: cn=reader-shib,$RAIZ_BASE_LDAP
objectClass: simpleSecurityObject
objectClass: organizationalRole
cn: reader-shib
description: shibboleth reader
userPassword: $HASH_PASS_READER_SHIB

dn: cn=reader-radius,$RAIZ_BASE_LDAP
objectClass: simpleSecurityObject
objectClass: organizationalRole
cn: reader-radius
description: radius reader
userPassword: $HASH_PASS_READER_RADIUS

dn: uid=$LDAPPRIMARYUID,ou=People,$RAIZ_BASE_LDAP
structuralObjectClass: inetOrgPerson
sn: $LDAPPRIMARYSN
givenName: $LDAPPRIMARYCN
mail: $LDAPPRIMARYUIDMAIL
uidNumber: 100001
gidNumber: 100
homeDirectory: /home/"$LDAPPRIMARYUID"
loginShell: /bin/bash
userPassword: $HASH_PASS_USER
uid: $LDAPPRIMARYUID
sambaSID: $SAMBASID-100001
cn: $LDAPPRIMARYCN
sambaNTPassword: $HASH_PASS_USER_NT
displayName: $LDAPPRIMARYDISPLAYNAME
objectClass: person
objectClass: inetOrgPerson
objectClass: eduPerson
objectClass: brPerson
objectClass: schacPersonalCharacteristics
objectClass: posixAccount
objectClass: shadowAccount
objectClass: sambaSamAccount


EOF

# 4- Ajusta as permissões
chown -R ldap:ldap /var/lib/ldap /etc/openldap/slapd.conf
chmod 600 /etc/openldap/slapd.conf /var/lib/ldap/*

# 5- Reinicia o LDAP
/sbin/service slapd start

