#!/bin/bash
# Easy Life SCIFI
#
# Configuration Tool for an Easy Life
# Version 20140401
#
# SCIFIWeb module
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
SCIFIWeb Module

This module will:
a) Install postgresql JDBC
b) Set user name and password to access scifi administrative web interface
c) Create SSL Certificate
d) Configure JBossAS standalone.xml
e) Install SCIFI Web application

Press <Enter> key

EOF

read

# a) Install postgresql JDBC 

standalone="/usr/share/jboss-as-7.1.1.Final/standalone/configuration/standalone.xml"
oldstandalone="/usr/share/jboss-as-7.1.1.Final/standalone/configuration/standalone.xml.old.$(date +%Y%m%d-%H%M%S)"
su - jboss -c "cp $standalone $oldstandalone"

su - jboss -c "mkdir -p /usr/share/jboss-as-7.1.1.Final/modules/org/postgresql/main"

if [ -f $ModDir'SCIFIWeb/'postgresql-9.2-1002.jdbc4.jar ]; 
 then 
  echo "File postgresql-9.2-1002.jdbc4.jar has already been downloaded."
 else
  wget http://jdbc.postgresql.org/download/postgresql-9.2-1002.jdbc4.jar -O $ModDir'SCIFIWeb/'postgresql-9.2-1002.jdbc4.jar
fi

cp $ModDir'SCIFIWeb/'postgresql-9.2-1002.jdbc4.jar /usr/share/jboss-as-7.1.1.Final/modules/org/postgresql/main
chmod -R 644  /usr/share/jboss-as-7.1.1.Final/modules/org/postgresql/main
chown -R jboss:jboss /usr/share/jboss-as-7.1.1.Final/modules/org/postgresql/main

su - jboss -c "sh /usr/share/jboss-as-7.1.1.Final/bin/standalone.sh -Djboss.bind.address=0.0.0.0 -Djboss.bind.address.management=0.0.0.0 &"
sleep 30
su - jboss -c 'sh /usr/share/jboss-as-7.1.1.Final/bin/jboss-cli.sh --connect --commands=/subsystem=datasources/jdbc-driver=postgresql-driver:add(driver-name=postgresql-driver,driver-class-name=org.postgresql.Driver,driver-module-name=org.postgresql)'

# b) Set user name and password to access scifi administrative web interface

export JBOSS_HOME=/usr/share/jboss-as-7.1.1.Final/
export CLASSPATH=${JBOSS_HOME}/modules/org/picketbox/main/picketbox-4.0.7.Final.jar:${JBOSS_HOME}/modules/org/jboss/logging/main/jboss-logging-3.1.0.GA.jar:$CLASSPATH
scifiwebpass=$(echo -n $SCIFIWEBPASSWD | openssl dgst -sha1 -binary | openssl base64)
su - jboss -c "echo '$SCIFIWEBUSERNAME=$scifiwebpass' > /usr/share/jboss-as-7.1.1.Final/standalone/configuration/controller-users.properties"
su - jboss -c "echo '$SCIFIWEBUSERNAME=Admin' > /usr/share/jboss-as-7.1.1.Final/standalone/configuration/controller-roles.properties" 

# c) Create SSL Certificate

su - jboss -c "cd /usr/share/jboss-as-7.1.1.Final/standalone/configuration; keytool -genkey -alias ControllerWebCert -keyalg RSA -keystore ControllerWebCert.keystore -validity 10950 -storepass $SSLCERTIFICATEPASSWD -keypass $SSLCERTIFICATEPASSWD -dname cn=$MACHINE;"

# d) Configure JBossAS standalone.xml

senha_criptografada='$(java org.picketbox.datasource.security.SecureIdentityLoginModule $SCIFIDBPASSWD)'

awk -v senha_keystore="$SSLCERTIFICATEPASSWD" -v senha="$senha_criptografada" '

	/<security-domains>/{print;print "                <security-domain name=\042EncryptDBPassword\042>\012                   <authentication>\012                       <login-module code=\042org.picketbox.datasource.security.SecureIdentityLoginModule\042 flag=\042required\042>\012                            <module-option name=\042username\042 value=\042scifi\042/>\012                            <module-option name=\042password\042 value=\042"senha"\042/>\012                       </login-module>\012                   </authentication>\012                </security-domain>";next}1

	/<datasources>/{print "                <datasource jndi-name=\042java:/ControllerDB\042 enabled=\042true\042 pool-name=\042ControllerDB\042 use-java-context=\042true\042 >\012                    <connection-url>jdbc:postgresql://localhost:5432/controladorbd</connection-url>\012                    <driver>postgresql-driver</driver>\012                    <pool>\012                       <min-pool-size>5</min-pool-size>\012                       <max-pool-size>20</max-pool-size>\012                       <prefill>true</prefill>\012                    </pool>\012                    <security>\012                       <security-domain>EncryptDBPassword</security-domain>\012                    </security>\012                </datasource>";next}
	
	/<subsystem xmlns="urn:jboss:domain:web:1.1" default-virtual-server="default-host" native="false">/{print "            <connector name=\042http\042 protocol=\042HTTP/1.1\042 scheme=\042http\042 socket-binding=\042http\042  redirect-port=\0428443\042 />\012            <connector name=\042https\042 scheme=\042https\042 protocol=\042HTTP/1.1\042 socket-binding=\042https\042 enable-lookups=\042false\042 secure=\042true\042>\012               <ssl name=\042ControllerWeb-ssl\042 password=\042"senha_keystore"\042 protocol=\042TLSv1\042 key-alias=\042ControllerWebCert\042 certificate-key-file=\042${jboss.server.config.dir}/ControllerWebCert.keystore\042 />\012            </connector>";next}
	
	/<security-domains>/{print "<security-domain name=\042Controller\042 cache-type=\042default\042>\012  <authentication>\012   	<login-module code=\042org.jboss.security.auth.spi.UsersRolesLoginModule\042 flag=\042required\042>\012   	<module-option name=\042usersProperties\042 value=\042${jboss.server.config.dir}/controller-users.properties\042/>\012   	<module-option name=\042rolesProperties\042 value=\042${jboss.server.config.dir}/controller-roles.properties\042/>\012   	<module-option name=\042hashAlgorithm\042 value=\042SHA-1\042/>\012   	<module-option name=\042hashEncoding\042 value=\042base64\042/>\012   	</login-module>\012   </authentication>\012</security-domain>";next}

' $oldstandalone > $standalone

sed '/<connector name="http" protocol="HTTP\/1.1" scheme="http" socket-binding="http"\/>/d' $standalone

# e) Install SCIFI Web application
ControllerWeb="ControllerWeb-svn-rev206.war"
su - jboss -c "sh /usr/share/jboss-as-7.1.1.Final/bin/jboss-cli.sh --connect --commands=deploy\ $ModDir'SCIFIWeb/'$ControllerWeb;"
su - jboss -c "sh /usr/share/jboss-as-7.1.1.Final/bin/jboss-cli.sh --connect command=:shutdown;"

sleep 5
echo SCIFIWeb module finished
echo 'Press <Enter> to exit'
read

