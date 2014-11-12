#!/bin/bash
# This script put tags in the directories corresponding to
# each AP in mrtg. This allows the user to select just a portion
# of the APs to be displayed
# input is the output of gera_lista.sh

awk -f poe_tag.awk tabela2.txt > comandos.sh
bash comandos.sh
