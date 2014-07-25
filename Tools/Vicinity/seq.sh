#!/bin/bash
#set -xv

# finding if there is an IP in leases.txt that is not in /etc/dnsmasq.d/hosts
grep SCIFI /var/lib/dnsmasq/dnsmasq.leases > leases.txt

# finding other APs that received an IP address in the user range
grep  SCIFI9120481441 /var/log/messages | grep DHCPACK | awk '{print $8 " " $9}'
 | sort -u > lista_log.txt

# pinging the APs to see if they are really present, and cleaning the address fr
om the one that made it in leases
for d in `awk '{ print $1}' lista_log.txt `; do if [ $(ping -c 1 -w 4 $d| grep l
oss| awk '{print $4}') -gt 0 ]; then grep $d lista_log.txt; fi; done | grep -v `
awk ' { print $2 } ' leases.txt ` > ipmac_lost.txt


# getting the other APs that are in leases.txt
grep ap0 /var/lib/dnsmasq/dnsmasq.leases >> leases.txt

# running scan in all APs

for d in `awk '{print $3}' leases.txt`;
        do echo $d
        ssh -i /usr/share/scifi/core/controller_key root@$d "/etc/scripts/scan.s
h"
done

for d in `awk '{print $1}' ipmac_lost.txt`;
        do echo $d
        ssh -i /usr/share/scifi/core/controller_key root@$d "/etc/scripts/scan.s
h"
done

# copying the scan.txt files over here.

mkdir scan
for d in `awk '{print $3}' leases.txt`;
        do echo $d
        scp -i /usr/share/scifi/core/controller_key root@$d:/tmp/scan.txt scan/s
can_$d.txt
done

for d in `awk '{print $1}' ipmac_lost.txt`;
        do echo $d
        scp -i /usr/share/scifi/core/controller_key root@$d:/tmp/scan.txt scan/s
can_$d.txt
done

# getting the current channel number for all APs

for d in `awk '{print $3}' leases.txt`;
        do echo $d
 ssh -i /usr/share/scifi/core/controller_key root@$d "/etc/scripts/get_ch
annel.sh"
done

for d in `awk '{print $1}' ipmac_lost.txt`;
        do echo $d
        ssh -i /usr/share/scifi/core/controller_key root@$d "/etc/scripts/get_ch
annel.sh"
done

# copying channel.txt over here

mkdir channel
for d in `awk '{print $3}' leases.txt`;
        do echo $d
        scp -i /usr/share/scifi/core/controller_key root@$d:/tmp/channel.txt cha
nnel/channel_$d.txt
done

for d in `awk '{print $1}' ipmac_lost.txt`;
        do echo $d
        scp -i /usr/share/scifi/core/controller_key root@$d:/tmp/channel.txt cha
nnel/channel_$d.txt
done



# creating a file with all the macs from eduroam hosts
# creating a file for each ap (mac) with the macs of their neighbours
# creating a file for each ap (mac) with all characteristcs of their neighbors -
 mac, ssid, channel, power
# creating a file will all macs and their respectives channels and ssids


mkdir macs
for d in `awk '{print $3}' leases.txt`; do
        NOH=$(grep "$d " leases.txt|awk '{print $2}')
        echo $NOH
        echo $NOH >> macs/listamacs_eduroam.txt
        mkdir macs/$NOH
        awk '/Address:/ {print $5;}' scan/scan_$d.txt >> macs/$NOH/vizinhos0.txt
        awk -f viz.awk scan/scan_$d.txt >> macs/$NOH/vizinhos_completo.txt
        awk -f viz2.awk scan/scan_$d.txt >> macs/mac_CH_SSID.aux
        done
for d in `awk '{print $1}' ipmac_lost.txt`; do
        NOH=$(grep $d ipmac_lost.txt|awk '{print $2}')
        echo $NOH
        echo $NOH >> macs/listamacs_eduroam.txt
        mkdir macs/$NOH
        awk '/Address:/ {print $5;}' scan/scan_$d.txt >> macs/$NOH/vizinhos0.txt
        awk -f viz.awk scan/scan_$d.txt >> macs/$NOH/vizinhos_completo.txt
        awk -f viz2.awk scan/scan_$d.txt >> macs/mac_CH_SSID.aux
        done

sort -u macs/mac_CH_SSID.aux > macs/mac_CH_SSID.txt
rm macs/mac_CH_SSID.aux


# list of macs: NOH.txt

awk '{print $2}' leases.txt > NOH.txt
awk '{print $2}' ipmac_lost.txt >> NOH.txt

# list of IPs: IP.txt

awk '{print $3}' leases.txt > IP.txt
awk '{print $1}' ipmac_lost.txt >> IP.txt

# repeat 10 times, add neighbours of neighbours to file

for d in 1 2 3 4 5 6 7 8 9 10; do 
	echo $d; 
	
	x=$d
	x2=$((x - 1)) 

	for NOH in `cat NOH.txt`; do
		echo $NOH
		comando="cat macs/$NOH/vizinhos"$x2".txt"
		echo $comando
		for i in `$comando`; do 
			if [ ${i:0:8} = "00:27:22" ] || [ ${i:0:8} = "00:15:6D"]
;
				then
				
				else
				echo "v " $i
				haux=${i:0:15}
				h=${haux,,}
				taux=$(! echo ${i:15:2} | tr '[:lower:]' '[:uppe
r:]')
				taux2=$((0x$taux - 0x1))
				taux3=$(printf "%02x" $taux2)
				t=${taux3:0:2}
			fi
			echo "w " $h$t 
			cat macs/$h$t/vizinhos0.txt >> "macs/$NOH/vizinhos"$x".a
ux"
			done
		sort -u "macs/$NOH/vizinhos"$x".aux" > "macs/$NOH/vizinhos"$x".t
xt"
		rm "macs/$NOH/vizinhos"$x".aux"
		done
	done

mkdir vicinity

updatedb

# find all the files with the 10th interaction of the neighbour algorithm, and c
reate a list of MACs with the same number of neighbours

locate vizinhos10.txt | grep `pwd` > lv10.txt
for d in `cat lv10.txt`; do
        echo $d
        nv=$(cat $d | wc -l)
        echo $nv
        echo $d >> vicinity/lists.$nv
        done

# create a file with the IP to MAC mapping

arp -a > arp.txt

# create a file with the AP name, its IP address and MAC

awk '{ print $4 " " $3 " " $2}' leases.txt > apipmac2.txt
awk '{ print $1 " " $1 " " $2}' ipmac_lost.txt >> apipmac2.txt


# for all IPs, get the sysLocation string 

for d in `cat IP.txt`; do echo $d ;  snmpget -v 2c -c public $d sysLocation.0; d
one 2>&1 >> sysLocation.txt

# create a file with ip and sysLocation 

awk -f sl.awk sysLocation.txt > ipsysL.txt

# intermediary step to merge the sysLocation file and the mac file, numbering th
e lines to be able to use a state machine for the merge

for d in `awk '{print $1}' ipsysL.txt`; do echo "linha1: " $d; grep "$d)" arp.tx
t| awk '{print "linha2: "$1 " " $4} '| sed s/.wifi.uff.br//g; grep "$d " ipsysL.
txt| awk '{ print "linha3: "$0}'; done > tag_linha.txt

# merging the files, creating a file with IP, name, MAC and sysLocation

awk -f tag.awk tag_linha.txt > ipapmacsysL.txt

# print the report, all the APs with the same number of neighbours, and all the 
neighbours

for d in `ls vicinity/lists.*`;
	do 
	echo "--------------------------------"
	count=0
 	echo
        numero=${d:15:3}
        echo "APs com $numero Vizinhos"
       echo

	for i in `cat $d`; 
		do
		mac=$(echo $i|awk -F "/" '{print $6}')
                grep -i $mac ipapmacsysL.txt
	done		

	for i  in `cat $d`;
                do

		if [ $count -eq 0 ];
			then
			echo
			echo "lista de vizinhos:"
			for j in `cat $i`;
				do
				grep $j macs/mac_CH_SSID.txt
				done
			let "count+=1"
			fi
		done
	done



