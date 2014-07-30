#!/bin/bash
# Easy Life SCIFI
#
# Configuration Tool for an Easy Life
# Version 20140730
#
# Variables module
#
# Cosme Faria Corrêa
# John Doe
# ...
#
#set -xv        

clear


cat <<-EOF

  =========================================
  |           Easy Life for SCIFI         |
  =========================================
                Variables Module

  There are a lot of necessary information.
  You must setup the variables by yourself.

  Press <Enter> key

EOF

read
#vim $CFGFile
#vim $1
vim $CFGFile

echo Variables module finished
echo 'Press <Enter> to exit'
read
