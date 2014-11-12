#!/bin/bash
# This scripts reads a file called "tabela.txt" that
# contains the name of the AP, the campus and department
# that the AP was installed in and produces
# an insert to a web page that lets the user choose a subset
# of APs in MRTG according to campus and department
#

iconv -f utf8 -t ascii//TRANSLIT tabela.txt> tabela2.txt
awk '{print $2}' tabela2.txt | sort -u > campus.txt
awk '{print $2 "_" $3}' tabela2.txt | sort -u > campus_department.txt
cat campus.txt campus_department.txt | sort > lista.txt
for d in `cat lista.txt`; do echo "<option value=\"$d\">$d</option>"; done >  menu_loc.html
cp menu_loc.html /var/www/mrtg/classes

