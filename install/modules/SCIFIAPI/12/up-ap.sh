# Update API in APs
# Version 20140618
# Cosme Corrêa - cosmefc@id.uff.br
# Glauco Quintino glaucoq@id.uff.br
# uncomment for debug
#set -xv

ERRO () {
echo 
echo 'Update APs API'
echo 
echo sintax:   $0 DEVICE
echo 
echo example:   $0 ap0050
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
#               copy package to ap
                scp -pri /etc/scifi/controller_key /etc/scifi/SCIFIAPI root@$1:/tmp/
#               execute it remotely
                ssh -i /etc/scifi/controller_key  root@$1 '/tmp/SCIFIAPI/up-ap.sh' $1
        else
#               AP
                if [ "`grep VERSION /etc/scifi/scifi.conf  |awk '{print $2;exit}'`" = "12" ]
                then
                        echo O AP $1 já esta atualizado
                else

                        LOCATION=$(cat /etc/config/snmpd |grep sysLocation)
                        NAME=$(cat /etc/config/snmpd |grep sysName)
                        mkdir /etc/scifi
                        mkdir /usr/share/scifi
                        mkdir /usr/share/scifi/scripts
                        ln -s /etc/dropbear/authorized_keys /etc/scifi
       #                sh /etc/scripts/ap_type.sh > /etc/scifi/scifi-type.txt
       #                touch /etc/scifi/scifi-connected2.txt
       #                touch /etc/scifi/scifi-coordinates.txt
       #                touch /etc/scifi/scifi-tags.txt
       #                ln -s /etc/scifi/scifi-neighborhood.txt /tmp/scifi-neighborhood.txt
        #               rm -f /etc/scripts/nsta.sh
        #               rm -f /etc/scripts/ap_type.sh
        #               rm -f /etc/scripts/SCIFI.sh
                        cp -f /tmp/SCIFIAPI/scifi.conf /etc/scifi/
                        sed -i "s/teste/$(./ap_type.sh | awk '{print $1;exit}')/" /etc/scifi/scifi.conf
                        rm -f /etc/scripts/*
#                       copy this
                        cp -f /tmp/SCIFIAPI/*.sh /usr/share/scifi/scripts/
                        rm -f /usr/share/scifi/scripts/up-ap.sh
                        ln -s /usr/share/scifi/scripts/* /etc/scripts
#                       For retrocompatibility, these will be removed in the future
                        ln -s /usr/share/scifi/scripts/scifi-users.sh /etc/scripts/nsta.sh
        #               ln -s /usr/share/scifi/scripts/scifi-type.sh /etc/scripts/ap_type.sh

        #               cp -f /tmp/SCIFIAPI/scifi-version.txt /etc/scifi/
        #               cp -f /tmp/SCIFIAPI/scifi-subversion.txt /etc/scifi/
#                       SNMP
                        cp -f /tmp/SCIFIAPI/snmpd.ap /etc/config/snmpd
                        sed -i "76s/^.*/$LOCATION /" /etc/config/snmpd
                        sed -i "78s/^.*/$NAME /" /etc/config/snmpd
                        rm -f /etc/snmp/snmpd.conf
                        ln -s /var/run/snmpd.conf /etc/snmp/
                        /etc/init.d/snmpd restart
                        chmod 700 /usr/share/scifi/scripts/ -R

                fi
fi
exit 0
                                                                        
