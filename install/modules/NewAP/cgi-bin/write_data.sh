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
#env
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
echo ""
mac=$(ifconfig eth0|grep HWaddr|cut -c39-55)
ifconfig br-lan | grep -A2 br-lan > "dados"$mac
echo "" >> "dados"$mac
ifconfig wlan0 | grep -A2 wlan0 >> "dados"$mac
echo "" >> "dados"$mac
echo $QUERY_STRING >> "dados"$mac