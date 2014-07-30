#!/bin/sh
# version 20140722
# Script for dynamically generating the page for 
# showing and writing configuration data of new APs
#
# Luiz Magalhaes
# schara (at) telecom.uff.br
#
# This file is located in the /www/cgi-bin directory, 
# and is called by index.html 
# 
# before using this, the image should contain, or the administrator should install 
# the uhttp package, so there is a web server, and it should be running
#
echo "Content-type: text/html"
echo ""
echo "Dados Gravados com sucesso"
echo ""
echo ""
echo ""
echo "<pre>"
echo "Dados de rede: "
echo " "
ifconfig br-lan | grep -A2 br-lan
echo ""
ifconfig wlan0 | grep -A2 wlan0
echo ""
echo "========================================================="
echo ""
echo "Dados lidos :"
echo ""
echo "</pre>"
read QUERY_STRING
echo $QUERY_STRING
echo ""
echo $QUERY_STRING |  awk -F"&" '{ printf (" Numero do AP: %s\n Sigla do campus: %s \n Departamento: %s \n Local: %s \n %s \n %s \n %s \n %s \n %s \n %s \n %s \n", $1, $2, $3, $4, $5, $6, $7, $8, $9, $10, $11)}'
echo ""
echo ""
maceth0=$(ifconfig -a eth0|grep HWaddr|cut -c39-55)
ifconfig br-lan | grep -A2 br-lan > "dados"$maceth0
echo "" >> "dados"$maceth0
ifconfig wlan0 | grep -A2 wlan0 >> "dados"$maceth0
echo "" >> "dados"$maceth0
echo $QUERY_STRING >> "dados"$maceth0
echo ""
device=$(cat device.txt)
echo "device $device" >> "dados"$maceth0

ip=$(ifconfig br-lan| grep "inet addr:" | awk -F":" '{print $2}'| awk '{print $1}')

STATUS=$(awk '{print $1}' status.txt)
if [ $STATUS -lt 2 ]; 
	then
	STATUS1="3"
	STATUS2="1"
	else
	STATUS1="5"
	STATUS2="4"
fi

if [ `ifconfig br-lan | grep UP | awk '{print $1}'` = "UP" ];
	then echo $STATUS1 " " $ip " " $maceth0 > status.txt
        else echo $STATUS2 " " $ip " " $maceth0 > status.txt                          
fi    
