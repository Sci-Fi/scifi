# Update API in APs
# Cosme Corrêa - cosmefc@id.uff.br
# Glauco Quintino glaucoq@id.uff.br
# uncomment for debug
#set -xv

ERRO () {
echo 
echo 'Update APs API'
echo 
echo sintax:   $0  
echo 
echo example:   $0 
echo 
exit
}

# Test # of parameters
if [ "$#" -eq "0" ]
        then
        echo 'Error, wrong # of parameters';
        ERRO;
        exit;
fi

if [ "`/usr/share/scifi/scripts/scifi-type.sh`" = "CONTROLLER" ]
        then
#       copy pacakge to ap
        scp -pri /etc/scifi/controller_key /etc/scifi/SCIFIAPI root@$1:/tmp/
#       execute it remotely
        ssh -i /etc/scifi/controller_key  root@$1 '/tmp/SCIFIAPI/up-ap.sh 1'
        else
#       AP
        if [ "`/usr/share/scifi/scripts/scifi-version.sh >/dev/null; echo $?`" = "12" ]
        then
        echo 'O AP já esta atualizado'
        else

#       export PATH=/bin:/sbin:/usr/bin:/usr/sbin;
        LOCATION=$(cat /etc/config/snmpd |grep sysLocation)
        NAME=$(cat /etc/config/snmpd |grep sysName)
        mkdir /etc/scifi
        mkdir /usr/share/scifi
        mkdir /usr/share/scifi/scripts
        mv /etc/dropbear/authorized_keys /etc/scifi
        ln -s /etc/scifi/authorized_keys /etc/dropbear/
        sh /etc/scripts/ap_type.sh > /etc/scifi/scifi-type.txt
        > /etc/scifi/scifi-connected2.txt
        > /etc/scifi/scifi-coordinates.txt
        > /etc/scifi/scifi-tags.txt
        > /etc/scifi/scifi-neighborhood.txt
        mv /etc/scripts/* /usr/share/scifi/scripts 2>/dev/null
        ln -s /usr/share/scifi/scripts/* /etc/scripts
#       copy this
        cp -f /tmp/SCIFIAPI/*.sh /usr/share/scifi/scripts/
        cp -f /tmp/SCIFIAPI/scifi-version.txt /etc/scifi/
        cp -f /tmp/SCIFIAPI/scifi-subversion.txt /etc/scifi/
        cp -f /tmp/SCIFIAPI/snmpd /etc/config/snmpd
        sed -i "76s/^.*/$LOCATION /" /etc/config/snmpd
        sed -i "78s/^.*/$NAME /" /etc/config/snmpd
        chmod 700 /usr/share/scifi/scripts/ -R
        rm -f /etc/snmp/snmpd.conf
        ln -s /var/run/snmpd.conf /etc/snmp/
        /etc/init.d/snmpd restart
fi
fi
exit 0
