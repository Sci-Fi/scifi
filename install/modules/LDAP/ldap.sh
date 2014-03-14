 #!/bin/bash
#
# versão 20131021
#
# Cosme Faria Corrêa
# cosmefc@id.uff.br
#
# Descomente para debug
set -xv

Principal() {
   clear
   suffix="dc=visitantes"
   people="ou=People",$suffix
   group="ou=Group",$suffix
   rootdn="cn=Manager"
   rootpw="Batata"
     echo "+--------------------------------------------------------+"
     echo "|                       SCIFI*                           |"
     echo "+--------------------------------------------------------+"
     echo "|                     MENU do Ldap                       |"
     echo "+--------------------------------------------------------+"
     echo
     echo
     echo -ne "#======= Menu do seu LDAP ========#"
     echo
     echo "1. Adicionar um Usuario"
     echo "2. Deletar um Usuario"
     echo "3. Modificar senha de Usuario"
     echo "4. Adicionar um Grupo"
     echo "5. Remover um Grupo"
     echo "6. Adicionar um Usuario no Grupo"
     echo "7. Remover um Usuario no Grupo"
     echo "8. Listar Usuario"
     echo "9. Sair"    
     echo -n "Entre com a opcao desejada => "
     read opcao
     echo
     case $opcao in
        1) Adicionar ;;
        2) Deletar ;;
        3) Modificar ;;
        4) Adicionar_grupo ;;
        5) Remover_grupo ;;
        6) Alterar_grupo ;;   
        7) Alterar_del_grupo ;;
        8) Lista_usuario ;;
        9) exit ;;
        *) "Opcao desconhecida." ; echo ; Principal ;;
     esac
  }

#1#################Adicionando usuario####################
Adicionar() {
uidnumber=`ldapsearch -LL -x -b $people  uidNumber | grep uidNumber | tail -1 | awk '{print $2}'`
   nextuid=`expr $uidnumber + 1`
      echo -n "Digite o login:"
      read uid
      echo -n "Digite somente o nome:"
      read cn
      echo -n "Digite o sobrenome:"
      read sn
      echo -n "Digite o numero do CPF:"
      read numcpf
      echo -n "Digite o n do grupo primário. (100)-->"
      read numsetor
      echo
      echo -n "Digite a senha: "
      read -s pass
      senha=`slappasswd -c crypt -s $pass`
      passNT=`printf $pass | iconv -t utf16le | openssl md4 `
      echo
   (
   echo "dn:uid=$uid,$people"
   echo "objectClass: top"
   echo "objectClass: person"
   echo "objectClass: eduperson"
   echo "objectClass: brperson"
   echo "objectClass: posixAccount"
   echo "objectClass: inetOrgPerson"
   echo "cn:$cn"
   echo "sn:$sn"
   echo "uid: $uid"
   echo "brPersonCPF: $numcpf"
   echo "userPassword: $senha"
   echo "homeDirectory: /home/$uid"
   echo "loginShell: /bin/bash"
   echo "uidNumber: $nextuid"
   echo "gidNumber: $numsetor"
   )| ldapadd -x -D $rootdn,$suffix -w $rootpw
     echo "Pressione qualquer tecla para continuar..."
     read msg
     Principal
  }

#2#################Deletar usuario######################
Deletar () {
   echo -n "Digite o login a ser excluido:"
   read cn
   echo $LDAPDN
   (
   echo "uid=$cn,$people"
   )| ldapdelete -x -D $rootdn,$suffix -w $rootpw
     echo "Pressione qualquer tecla para continuar..."
     read msg
     Principal
  }

#3#####################Alterar senha######################
Modificar() {
   echo -n "Digite o login -> "
   read userldap
   cn=$userldap
   echo -n "Digite a senha: "
   read -s pass
   senha=`slappasswd -c crypt -s $pass`
   echo
   LDAPDN=`ldapsearch -h localhost -x -b $people -D $rootdn,$suffix -w $rootpw "(uid=$cn)" | grep dn`
   (
   echo "$LDAPDN"
   echo "changetype: modify"
   echo "replace: userPassword"
   echo "userPassword: $senha"
   )| ldapmodify -x -D $rootdn,$suffix -w $rootpw
     echo "Pressione qualquer tecla para continuar..."
     read msg
     Principal
  }

#4###################Adicionando Grupo########################
Adicionar_grupo() {
gidnumber=`ldapsearch -h localhost -x -b $group -D $rootdn,$suffix -w $rootpw gidNumber | grep gidNumber: | sort | cut -d : -f 2 | tail -n 1| sed s/\ //g`
nextgid=`expr $gidnumber + 1`
   echo -n "Digite o novo grupo:"
   read uid
   (
   echo "dn:cn=$uid,$group"
   echo "objectClass: posixGroup"
   echo "cn: $uid"
   echo "gidNumber: $nextgid"
   )| ldapadd -x -D $rootdn,$suffix -w $rootpw
     echo "Pressione qualquer tecla para continuar..."
     read msg
     Principal
  }

#5##################Remover Grupo############################
Remover_grupo() {
   echo -n "Digite o grupo a ser excluido:"
   read cn
   echo $LDAPDN
   (
   echo "cn=$cn,$group"
   )| ldapdelete -x -D $rootdn,$suffix -w $rootpw
     echo "Pressione qualquer tecla para continuar..."
     read msg
     Principal
  }

#6#############Alterando o grupo de um login###################
Alterar_grupo() {
   echo -n "Digite o login a ser alterado:"
   read uid
   echo -n "Digite o grupo:"
   read cn
   (
   echo "dn: cn=$cn,$group"
   echo "changetype: modify"
   echo "add: memberUid"
   echo "memberUid: $uid"
   )| ldapmodify -x -D $rootdn,$suffix -w $rootpw
    echo "Pressione qualquer tecla para continuar..."
     read msg
     Principal
  }

#7#############Removendo login de um grupo###################
Alterar_del_grupo() {
        echo -n "Digite o login a ser retirado:"
        read uid
        echo -n "Digite o grupo:"
        read cn
        (
        echo "dn: cn=$cn,$group"
        echo "changetype: modify"
        echo "delete: memberUid"
        echo "memberUid: $uid"
        )| ldapmodify -x -D $rootdn,$suffix -w $rootpw
    echo "Pressione qualquer tecla para continuar..."
     read msg
     Principal
  }

#8##############Lista Usuario####################
Lista_usuario() {
   echo -n "Digite o login:"
   read user_id
   ldapsearch -LLL -x -D $rootdn,$suffix -w $rootpw uid=$user_id
    echo "Pressione qualquer tecla para continuar..."
     read msg
     Principal
  }
     Principal
