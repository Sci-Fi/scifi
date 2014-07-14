export PATH=/bin:/sbin:/usr/bin:/usr/sbin;

delay=$(head /dev/urandom | tr -dc "0123456789" | head -c2)
kernel_version=$(uname -r)

sleep $delay

logger 'SCIFI - restarting Wifi drivers and interfaces '

rmmod ath9k
rmmod mac80211
rmmod ath9k_common
rmmod ath9k_hw
rmmod ath
rmmod cfg80211
insmod /lib/modules/$kernel_version/cfg80211.ko 
insmod /lib/modules/$kernel_version/ath.ko 
insmod /lib/modules/$kernel_version/ath9k_hw.ko 
insmod /lib/modules/$kernel_version/ath9k_common.ko 
insmod /lib/modules/$kernel_version/mac80211.ko 
insmod /lib/modules/$kernel_version/ath9k.ko
/etc/init.d/led restart
wifi up
