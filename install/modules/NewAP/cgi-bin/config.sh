#!/bin/sh
# version 20140722
# Script for dynamically generating the page for 
# web configuration of new APs
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
echo "<head> <TITLE> Configurar AP </TITLE> </head>"
echo "<body>"
echo ""
echo "Configurar AP"
echo ""
echo ""
echo ""
#echo -n " Mac: "
#ifconfig eth0|grep HWaddr|cut -c39-
echo "<H1> Rede: "
echo "</H1>"
echo "<pre>"
# atencao: trocar "br-lan" para a interface apropriada. Provavelmente eth0 (ou eth1)

ifconfig br-lan| grep -A2 br-lan
echo ""
echo ""
echo "</pre>"
if [ `ifconfig br-lan | grep UP | awk '{print $1}'` = "UP" ];
        then echo " <H2> A rede esta funcionando </H2>"
        else echo " <H2> Por favor verifique a rede cabeada </H2>"
        fi

nome="dados"$mac

if [ -e $nome ];
        then
        echo " <H2>  AP $mac ja foi configurado </H2> <br> "
	   echo " preencher novamente irá sobre-escrever o arquivo <br>"
        echo "<pre> "
        cat $nome
	   echo ""
	   grep "NAP=" $nome | awk -F"&" '{ printf (" Numero do AP: %s\n Sigla do campus: %s \n %s \n %s \n %s \n %s \n %s \n %s \n\n", $1, $2, $3, $4, $5, $6, $7, $8, $9)}'
        echo "</pre>"

echo ""
echo "<br>"
echo ""
echo "Preencha os campos abaixo: <br>"
echo "<br>"
echo "<br>"
echo " <form name=\"ap\" action=\"/cgi-bin/write_data.sh/\" method=\"post\">"
echo "Numero do AP: <input type=\"text\" name=\"NAP\" value=\"000\" maxlength=\"4\"> <br>"
#echo "Campus:  <input type=\"text\" name=\"Campus\" value=\"GRAGOATA\"> <br>"
echo ""
echo "<br>"
echo "<select name=\"campi\">"
echo "<option value=\"GR\">Gragoata</option>"
echo "<option value=\"HU\">HUAP</option>"
echo "<option value=\"PV\">Praia Vermelha</option>"
echo "<option value=\"VL\">Valonguinho</option>"
echo "<option value=\"RT\">Reitoria</option> "
echo "<option value=\"DI\">Direito</option> "
echo "<option value=\"OU\">Outro</option> "

echo "</select>"
echo "<br>"
echo ""
echo "<br>"
echo "Departamento:  <input type=\"text\" name=\"Dept\" value=\"BLOCO X\"> <br>"
echo "Local:  <input type=\"text\" name=\"sala\" value=\"Sala YYY\"> <br>"
echo ""
echo ""
echo "<br> <br> Os campos a seguir sao opcionais: <br>"
echo "<br>"
echo ""
echo "Campo OBS: entre aqui os comentarios sobre a instalacao. Detalhes sobre o local e informacoes se nao existir o nome do campus <br> "
echo ""
echo "<br>"

echo "<textarea rows=\"4\" cols=\"40\" name=\"OBS\">"
echo " "
echo "</textarea>"
echo ""
echo ""
echo " <br> <br> <br>GPS: <br> <br>"
echo ""
echo "Latitude: <input type=\"text\" name=\"Latitude\" value=\"-22.903xxx\"> <br>"
echo "Longitude: <input type=\"text\" name=\"Longitude\" value=\"-43.132yyy\"> <br>"
echo "Altura: <input type=\"text\" name=\"Altura\" value=\"3\" maxlength=\"3\"> <br>"

echo "<input type=\"submit\" value=\"Enviar\">"
echo "</body>"
