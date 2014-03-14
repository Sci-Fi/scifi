#!/bin/bash                                                         
clear                                                               
#set -xv                                                            

DIRBKP=/home/LDAP
INICIO=`date +%Y%m%d-%H%M%S`
INICIOFORMAT=`date +%H:%M:%S-%Y/%m/%d`

# ADQUIRINDO INFORMACOES DO DIRETORIO DE BACKUPS
ls -tr $DIRBKP > lista.$INICIO                      
cat lista.$INICIO | grep .ldif > listaok.$INICIO
LISTA=`tail -n 8 listaok.$INICIO`               
rm -f lista.$INICIO                             
rm -f listaok.$INICIO                           

# ESCOLHA DE QUAL BACKUP RESTAURAR
echo ""                           
echo "Qual backup deve ser restaurado ??"
select var in SAIR $LISTA; do            
   break                                 
done                                     
echo "Sera restaurado o backup $var"     
RESTAURA=$DIRBKP/$var                    

echo "### SISTEMA DE RESTAURACAO DE LDAP ###"
echo ""                                      
echo "###   Sera restaurado o arquivo    ###"
if [ -n "$RESTAURA" ]; then                  
        echo "         $RESTAURA"            
else                                         
        echo "         ?????????"            
fi                                           
echo ""                                      
echo "######################################"
echo " Iniciando em $INICIOFORMAT"           
echo ""                                      
if [ -n "$RESTAURA" ]; then                  
        if [ -f "$RESTAURA" ]; then          
                echo " Arquivo Informado Existe, Continuando..."
                OK=1                                            
        else                                                    
                #setterm -store -background black -foreground red
                echo " >>> O arquivo informado nao existe ! <<<" 
                echo " >>> Verifique o nome corretamente !  <<<" 
                #setterm -store -background black -foreground white
        fi                                                         
else                                                               
        echo " >>> Informe um arquivo a ser importado ! <<<"       
fi                                                                 

if [ -n "$OK" ]; then
echo "Finalizando o LDAP ..."
service slapd stop            

echo "Movendo o diretorio do /var/lib/ldap"
mv /var/lib/ldap /var/lib/ldap.$INICIO

echo "Criando novo diretorio"
mkdir /var/lib/ldap
echo "Copiando DB_CONFIG..."
cp /etc/openldap/DB_CONFIG.example /var/lib/ldap/DB_CONFIG
echo "Checando permissoes..."
chown ldap:ldap /var/lib/ldap/ -Rf

echo "Reiniciando servico / Parando servico"
service slapd start
service slapd stop

echo "Carregando base $RESTAURA"
echo " isto pode demorar alguns segundos..."
slapadd -vl $RESTAURA

echo "Adicao finalizada"

echo "Ajustando permissoes"
chown ldap:ldap /var/lib/ldap/ -Rf

echo "Reiniciando Servico.."
service slapd start
service slapd restart

echo "+--------------------------------------------"
echo " A restauracao foi efetuada, caso tenha verificado"
echo "      algum erro, por favor, tente novamente ou corrija-os"
echo ""
echo "  Deve ter aparecido a seguinte mensagem acima:"
echo "           ***********************************************"
echo "           ** Conferindo arquivos de configuração       **"
echo "           ** para slapd: config file testing succeeded **"
echo "           ***********************************************"
echo ""
echo "   apenas os 2 primeiros status podem ter sido [FALHOU]"
echo "+--------------------------------------------"
echo ""
echo "Caso apos tentativas voce nao consiga restaurar o sistema"
fi
echo ""
echo "    O seu contato e"
echo "           Cosme Correa - 21-9219-5949"
echo ""
echo ""
echo ""
