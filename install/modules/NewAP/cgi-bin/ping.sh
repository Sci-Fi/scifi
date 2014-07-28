#!/bin/sh
# version 20140728
# Script for testing connectivity by pinging an IP
#
# Luiz Magalhaes
# schara (at) telecom.uff.br
#
# This file is located in the /www/cgi-bin directory, 
# and is called by /www/cgi-bin/status.sh 
# 
# before using this, the image should contain, or the administrator should install 
# the uhttp package, so there is a web server, and it should be running
#
echo "Content-type: text/html"
read QUERY_STRING
echo $QUERY_STRING
echo ""
IP=$(echo $QUERY_STRING |  awk -F"=" '{print $2}')
echo ""

echo ""
echo "Pinging host"
echo ""
echo ""
echo ""
echo "<pre>"

ping -c 4 -w 5 $IP

echo "</pre>"

echo ""
