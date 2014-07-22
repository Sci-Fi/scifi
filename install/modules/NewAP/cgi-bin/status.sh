#!/bin/sh
# version 20140722
# Script for dynamically generating the page for 
# showing network and configuration status of new APs
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
echo "<head>"
echo "<TITLE> Status do AP </TITLE>"
echo "</head>"
echo "<body>"

echo "<H1> Status do AP </H1>"

mac=$(ifconfig eth0|grep HWaddr|cut -c39-55)
nome="dados"$mac

if [ -e $nome ];
        then
        echo " <H2>  AP $mac ja foi configurado </H2> "
        echo "<pre> "
        cat $nome
        echo "</pre>"
        else
        echo "<H2> Por favor configure o AP </H2> "
        fi

echo "<h3>"
echo "Mac: " $mac
# ifconfig eth0|grep HWaddr|cut -c39-

echo "</h3>"
echo "<pre>"
# env
echo " dados das interfaces de rede"
echo ""
ifconfig
echo "</pre>"

echo "</body>"