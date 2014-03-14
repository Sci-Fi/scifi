#!/bin/bash

# Initialize LDPA

# 1- Stop  LDAP server
# 2- Clean base
# 3- Initial load
# 4- Setup permissions
# 5- Start  LDAP server

# Uncomment for  debug
set -xv

#DOMINIO_INST="`/bin/hostname -d`"
#DOMINIO_INST="`/bin/dnsdomainname`"
DOMINIO_INST="niteroi.org.br"
RAIZ_BASE_LDAP="dc="`echo $DOMINIO_INST | sed -e 's/\./,dc=/g'`
ORGANIZACAO="`echo $RAIZ_BASE_LDAP | sed -e 's/^dc=//; s/,dc=/./'`"
DC="`echo $ORGANIZACAO | sed 's/^\.//; s/\..*$//'`"
PASS_ADMIN=$LDAPADMPASSWD
PASS_USER=$LDAPPRIMARYPASSWD
PASS_READER_SHIB=$SHIBPASS
PASS_READER_RADIUS=$RADIUSPASS
HASH_PASS_ADMIN=$( slappasswd -h {SSHA} -u -s $PASS_ADMIN )
HASH_PASS_USER=$( slappasswd -h {SSHA} -u -s $PASS_USER )
HASH_PASS_USER_NT=`echo $LDAPPRIMARYPASSWD | iconv -t utf16le | openssl md4 `
HASH_PASS_READER_SHIB=$( slappasswd -h {SSHA} -u -s $PASS_READER_SHIB )
HASH_PASS_READER_RADIUS=$( slappasswd -h {SSHA} -u -s $PASS_READER_RADIUS )

# 1- Stop LDAP sever
/sbin/service slapd stop

# 2- Clean base
/bin/rm /var/lib/ldap/* -rf
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

dn: ou=people,$RAIZ_BASE_LDAP
objectClass: organizationalUnit
objectClass: top
ou: people
structuralObjectClass: organizationalUnit

dn: ou=group,$RAIZ_BASE_LDAP
objectClass: top
objectClass: organizationalUnit
ou: group
structuralObjectClass: organizationalUnit

dn: sambaDomainName=NITEROI,$RAIZ_BASE_LDAP
sambaDomainName: NITEROI
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
entryCSN: 20110816135558.319799Z#000000#000#000000
modifiersName: cn=Manager,$RAIZ_BASE_LDAP
modifyTimestamp: 20110816135558Z

dn: ou=Computers,$RAIZ_BASE_LDAP
ou: Computers
objectClass: organizationalUnit
structuralObjectClass: organizationalUnit
entryUUID: f13f67c4-58b0-1030-9b5e-81628d64ee16
creatorsName: cn=Manager,$RAIZ_BASE_LDAP
createTimestamp: 20110811215934Z
entryCSN: 20110811215934.206413Z#000000#000#000000
modifiersName: cn=Manager,$RAIZ_BASE_LDAP
modifyTimestamp: 20110811215934Z

dn: ou=Idmap,$RAIZ_BASE_LDAP
ou: Idmap
objectClass: organizationalUnit
structuralObjectClass: organizationalUnit
entryUUID: f1477252-58b0-1030-9b5f-81628d64ee16
creatorsName: cn=Manager,$RAIZ_BASE_LDAP
createTimestamp: 20110811215934Z
entryCSN: 20110811215934.259113Z#000000#000#000000
modifiersName: cn=Manager,$RAIZ_BASE_LDAP
modifyTimestamp: 20110811215934Z

dn: cn=users,ou=group,$RAIZ_BASE_LDAP
structuralObjectClass: posixGroup
userPassword:: Kg==
objectClass: posixGroup
objectClass: top
gidNumber: 100
cn: users
memberUid: $LDAPPRIMARYUID

dn: cn=root,ou=people,$RAIZ_BASE_LDAP
sambaPwdCanChange: 0
gecos: Netbios Domain Administrator
objectClass: inetOrgPerson
objectClass: sambaSamAccount
objectClass: posixAccount
objectClass: shadowAccount
uidNumber: 0
sambaAcctFlags: [U          ]
cn: root
sambaPwdMustChange: 2147483647
sambaPrimaryGroupSID: $SAMBASID-512
sambaNTPassword: XXX
sambaKickoffTime: 2147483647
loginShell: /bin/false
sambaPwdLastSet: 0
sambaSID: $SAMBASID-500
sambaLogoffTime: 2147483647
sn: root
sambaLogonTime: 0
uid: root
gidNumber: 0
homeDirectory: /dev/null
sambaLMPassword: XXX
structuralObjectClass: inetOrgPerson
entryUUID: f14a8d7a-58b0-1030-9b60-81628d64ee16
creatorsName: cn=Manager,$RAIZ_BASE_LDAP
createTimestamp: 20110811215934Z
entryCSN: 20110811215934.279469Z#000000#000#000000
modifiersName: cn=Manager,$RAIZ_BASE_LDAP
modifyTimestamp: 20110811215934Z

dn: cn=Domain Admins,ou=group,$RAIZ_BASE_LDAP
gidNumber: 512
sambaGroupType: 2
displayName: Domain Admins
description: Netbios Domain Administrators
memberUid: root
objectClass: posixGroup
objectClass: sambaGroupMapping
sambaSID: $SAMBASID-512
cn: Domain Admins
structuralObjectClass: posixGroup
entryUUID: f16d8d48-58b0-1030-9b62-81628d64ee16
creatorsName: cn=Manager,$RAIZ_BASE_LDAP
createTimestamp: 20110811215934Z
entryCSN: 20110811221959.386654Z#000000#000#000000
modifiersName: cn=Manager,$RAIZ_BASE_LDAP
modifyTimestamp: 20110811221959Z

dn: cn=Domain Users,ou=group,$RAIZ_BASE_LDAP
gidNumber: 513
sambaGroupType: 2
displayName: Domain Users
description: Netbios Domain Users
objectClass: posixGroup
objectClass: sambaGroupMapping
sambaSID: $SAMBASID-513
cn: Domain Users
structuralObjectClass: posixGroup
entryUUID: f186e978-58b0-1030-9b63-81628d64ee16
creatorsName: cn=Manager,$RAIZ_BASE_LDAP
createTimestamp: 20110811215934Z
memberUid: $LDAPPRIMARYUID
entryCSN: 20111209122722.981037Z#000000#001#000000
modifiersName: cn=Manager,$RAIZ_BASE_LDAP
modifyTimestamp: 20111209122722Z

dn: cn=Domain Guests,ou=group,$RAIZ_BASE_LDAP
gidNumber: 514
sambaGroupType: 2
displayName: Domain Guests
description: Netbios Domain Guests
objectClass: posixGroup
objectClass: sambaGroupMapping
sambaSID: $SAMBASID-514
cn: Domain Guests
structuralObjectClass: posixGroup
entryUUID: f18dbc3a-58b0-1030-9b64-81628d64ee16
creatorsName: cn=Manager,$RAIZ_BASE_LDAP
createTimestamp: 20110811215934Z
entryCSN: 20110811221958.713157Z#000000#000#000000
modifiersName: cn=Manager,$RAIZ_BASE_LDAP
modifyTimestamp: 20110811221958Z

dn: cn=Domain Computers,ou=group,$RAIZ_BASE_LDAP
gidNumber: 515
sambaGroupType: 2
displayName: Domain Computers
description: Netbios Domain Computers
objectClass: posixGroup
objectClass: sambaGroupMapping
sambaSID: $SAMBASID-515
cn: Domain Computers
structuralObjectClass: posixGroup
entryUUID: f1955fda-58b0-1030-9b65-81628d64ee16
creatorsName: cn=Manager,$RAIZ_BASE_LDAP
createTimestamp: 20110811215934Z
entryCSN: 20110811222000.010170Z#000000#000#000000
modifiersName: cn=Manager,$RAIZ_BASE_LDAP
modifyTimestamp: 20110811222000Z

dn: cn=Administrators,ou=group,$RAIZ_BASE_LDAP
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
entryCSN: 20110812143701.498457Z#000000#000#000000
modifiersName: cn=Manager,$RAIZ_BASE_LDAP
modifyTimestamp: 20110812143701Z

dn: cn=Account Operators,ou=group,$RAIZ_BASE_LDAP
gidNumber: 548
sambaGroupType: 5
displayName: Account Operators
description: Netbios Domain Users to manipulate users accounts
objectClass: posixGroup
objectClass: sambaGroupMapping
sambaSID: S-1-5-32-548
cn: Account Operators
structuralObjectClass: posixGroup
entryUUID: f1a4bade-58b0-1030-9b67-81628d64ee16
creatorsName: cn=Manager,$RAIZ_BASE_LDAP
createTimestamp: 20110811215934Z
entryCSN: 20110811221959.075009Z#000000#000#000000
modifiersName: cn=Manager,$RAIZ_BASE_LDAP
modifyTimestamp: 20110811221959Z

dn: cn=Print Operators,ou=group,$RAIZ_BASE_LDAP
gidNumber: 550
sambaGroupType: 5
displayName: Print Operators
description: Netbios Domain Print Operators
objectClass: posixGroup
objectClass: sambaGroupMapping
sambaSID: S-1-5-32-550
cn: Print Operators
structuralObjectClass: posixGroup
entryUUID: f1ac695a-58b0-1030-9b68-81628d64ee16
creatorsName: cn=Manager,$RAIZ_BASE_LDAP
createTimestamp: 20110811215934Z
entryCSN: 20110811221959.230743Z#000000#000#000000
modifiersName: cn=Manager,$RAIZ_BASE_LDAP
modifyTimestamp: 20110811221959Z

dn: cn=Backup Operators,ou=group,$RAIZ_BASE_LDAP
gidNumber: 551
sambaGroupType: 5
displayName: Backup Operators
description: Netbios Domain Members can bypass file security to back up files
objectClass: posixGroup
objectClass: sambaGroupMapping
sambaSID: S-1-5-32-551
cn: Backup Operators
structuralObjectClass: posixGroup
entryUUID: f1b413c6-58b0-1030-9b69-81628d64ee16
creatorsName: cn=Manager,$RAIZ_BASE_LDAP
createTimestamp: 20110811215934Z
entryCSN: 20110811221959.698949Z#000000#000#000000
modifiersName: cn=Manager,$RAIZ_BASE_LDAP
modifyTimestamp: 20110811221959Z

dn: cn=Replicators,ou=group,$RAIZ_BASE_LDAP
gidNumber: 552
sambaGroupType: 5
displayName: Replicators
description: Netbios Domain Supports file replication in a sambaDomainName
objectClass: posixGroup
objectClass: sambaGroupMapping
sambaSID: S-1-5-32-552
cn: Replicators
structuralObjectClass: posixGroup
entryUUID: f1bbc1de-58b0-1030-9b6a-81628d64ee16
creatorsName: cn=Manager,$RAIZ_BASE_LDAP
createTimestamp: 20110811215935Z
entryCSN: 20110811221959.854563Z#000000#000#000000
modifiersName: cn=Manager,$RAIZ_BASE_LDAP
modifyTimestamp: 20110811221959Z

dn: cn=netadmins,ou=group,$RAIZ_BASE_LDAP
objectClass: posixGroup
objectClass: sambaGroupMapping
cn: Residentes
gidNumber: 1001
userPassword:: Kg==
memberUid: $LDAPPRIMARYUID
sambaSID: $SAMBASID-1001
displayName: Network Administrators
description: Network Administrators
sambaGroupType: 2
structuralObjectClass: posixGroup
entryUUID: c05e4a54-fb57-1030-9689-71548aa8ba0a
creatorsName: cn=Manager,$RAIZ_BASE_LDAP
createTimestamp: 20120305214146Z
entryCSN: 20120305214146.524276Z#000000#001#000000
modifiersName: cn=Manager,$RAIZ_BASE_LDAP
modifyTimestamp: 20120305214146Z

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
userPassword: $HASH_PASS_READER_SHIB_LEITOR_SHIB

dn: cn=reader-radius,$RAIZ_BASE_LDAP
objectClass: simpleSecurityObject
objectClass: organizationalRole
cn: reader-radius
description: radius reader
userPassword: $HASH_PASS_READER_RADIUS_LEITOR_RADIUS

dn: uid=$LDAPPRIMARYUID,ou=people,$RAIZ_BASE_LDAP
structuralObjectClass: inetOrgPerson
entryUUID: bfe9a746-1f5e-1031-98b4-0faf5e61b5fd
creatorsName: cn=Manager,$RAIZ_BASE_LDAP
createTimestamp: 20120420180234Z
sn: $LDAPPRIMARYSN
givenName: $LDAPPRIMARYCN
mail: $LDAPPRIMARYUID'@'$DOMAIN
uidNumber: 100001
gidNumber: 100
homeDirectory: /home/"$LDAPPRIMARYUID"
loginShell: /bin/bash
userPassword:: $HASH_PASS_USER
uid: $LDAPPRIMARYUID
sambaSID: $SAMBASID-100001
cn: $LDAPPRIMARYCN
sambaNTPassword: $HASH_PASS_USER_NT
displayName: $LDAPPRIMARYCN
objectClass: person
objectClass: inetOrgPerson
objectClass: eduPerson
objectClass: brPerson
objectClass: schacPersonalCharacteristics
objectClass: posixAccount
objectClass: shadowAccount
objectClass: sambaSamAccount
entryCSN: 20130517140550.690790Z#000000#001#000000
modifiersName: cn=Manager,$RAIZ_BASE_LDAP
modifyTimestamp: 20130517140550Z

EOF

# 4- Ajusta as permissões
chown -R ldap:ldap /var/lib/ldap /etc/openldap/slapd.conf
chmod 600 /etc/openldap/slapd.conf /var/lib/ldap/*

# 5- Reinicia o LDAP
/sbin/service slapd start

