!/bin/sh
# version 20140527
# Return the type of device
# Cosme Corr..a
# cosmefc@id.uff.br
# uncomment for debug
#set -xv


RESULT=$(cat /proc/uptime |awk '{print $1}')

echo $RESULT
exit 0
