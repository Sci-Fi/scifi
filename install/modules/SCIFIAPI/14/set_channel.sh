export PATH=/bin:/sbin:/usr/bin:/usr/sbin;
logger SCIFI - changing channel to $1

# getting wireless interfaces and status (up=1 or down=0)
wInterfaces=$(cat /proc/net/wireless | tail -n +3 | awk -F":" '{print $1}' | sed 's/ //g' | awk '{ cmd="! ifconfig "$1" | grep UP | wc -l";
cmd | getline status; print $0";"status" " ; close(cmd) }')

# changing channel
uci set wireless.radio0.channel=$1
wifi 

# turning off wireless interfaces that were previously down
for i in $wInterfaces; do awk -v i="$i" 'BEGIN{split(i,r,";"); if (r[2] == 0 ) {cmd="ifconfig "r[1]" down"; system(cmd) } }';done;
