#!/bin/bash                           
# versão 20090605                     
# sistema de bkp que executa as seguintes tarefas
#  - gera um ldif do LDAP                        
#  - envia email com log de erros                

# Chamar pelo cron com o seguinte formato:
# /root/fazbkp.sh >> /var/log/backup-ok.log 2> /var/log/backup-erro.log

# Cosme Corrêa - cosmefc@id.uff.br

#  Descomente para debug
set -xv                 

CLIENTE=`hostname -s`
BKPHOME=/home
INICIO=`date +%Y%m%d-%H%M%S`
DIAS=`date +%u`             
DIAM=`date +%d`             
DESTINATARIO="suporte@uff.br"
MENSAGEM="/var/log/backup-erro.log"    

# Atividade 1 - LDAP
echo Atividade 1 - LDAP
mkdir -p $BKPHOME/LDAP
/usr/sbin/slapcat -l $BKPHOME/LDAP/LDAP-$CLIENTE-$INICIO.ldif
FIM=`date +%Y%m%d-%H%M%S`                                    

# Atividade 2 - Transfere arquivo para o secundário
echo  Atividade 2 - Transfere arquivo para o secundário
# /usr/bin/scp  $BKPHOME/LDAP/LDAP-$CLIENTE-$INICIO.ldif root@xldap2.uff.br:/home/LDAP
FIM=`date +%Y%m%d-%H%M%S`                                    

# Atividade 3 - Envia e-mail com log de erros
echo  Atividade 3 - Envia e-mail com log de erros
ASSUNTO="Backup - "$CLIENTE" - "$INICIO
#/bin/mail -s "$ASSUNTO" "$DESTINATARIO" < $MENSAGEM
FIM=`date +%Y%m%d-%H%M%S`                                    

# FIM
echo FIM
date
echo .
