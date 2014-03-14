BACKUPDIR="/var/log/mrtg-backup"
DATE=`date +%Y%M%d`
if [ ! -d $BACKUPDIR ]; then
   mkdir $BACKUPDIR
fi

for AP in ap*; do
   cd $AP
   for LOG in *.log; do
      cp $LOG $BACKUPDIR/${LOG}$DATE
      bzip2 $BACKUPDIR/${LOG}$DATE
   done
   cd ..
done
