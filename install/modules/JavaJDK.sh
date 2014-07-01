#!/bin/bash
# Easy Life SCIFI
#
# Configuration Tool for an Easy Life
# Version 20140401
#
# JavaJDK module
#
# Cosme Faria Corrêa
# helgadb 
# 
# ...
#
#set -xv

clear

cat <<-EOF
=========================================
| Easy Life for SCIFI |
=========================================
JavaJDK Module

This module will:
*) Install Java OpenJdk 1.7.0

Press <Enter> to continue
EOF

read

# a) Install Java OpenJdk 1.7.0
yum -y install java-1.7.0-openjdk-devel.x86_64

echo JavaJDK module finished
echo 'Press <Enter> to exit'
read

