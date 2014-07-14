export PATH=/bin:/sbin:/usr/bin:/usr/sbin;
logger SCIFI - changing channel to $1
uci set wireless.radio0.channel=$1
wifi up 
