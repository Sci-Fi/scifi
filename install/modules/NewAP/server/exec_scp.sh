#!/bin/bash

config_key="/usr/share/scifi/core/config_key"
ip=$(tail -1 new_aps.txt|awk '{print $2}')

        #echo $status $ip $mac

        echo $ip
        echo -n "# " >> new_aps.txt
        date >> new_aps.txt
        echo >> new_aps.txt
        ./expect.sh root@$ip
        scp -i $config_key "root@"$ip":/www/dados*" dados

