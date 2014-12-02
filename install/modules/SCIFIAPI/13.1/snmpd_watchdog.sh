#!/bin/sh
# version 20141202
# this script tries to restart snmpd 10 times, with a 10 second interval. It should run from cron once a day
#
# Luiz Magalhaes
# schara (at) telecom.uff.br
# set -xv

/etc/init.d/snmpd restart
sleep 10
i=0
while [ $i -lt 10 ];
        do
        while [ `ps | grep -c snmpd` -lt 2 ];
                do
                /etc/init.d/snmpd restart
                sleep 10
                if [ `ps | grep -c snmpd` -gt 1 ];
                        then exit
                fi
                break
                done
        let i=i+1
        done



