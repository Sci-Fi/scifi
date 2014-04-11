# Inicializando o Nocat. Descomente a linha abaixo caso queira que o Captive Portal (splash) seja ativado.
#/usr/share/nocat/bin/gateway

# Liberando os APs no Nocat. Insira uma linha para cada AP contendo MAC da interface cabeada e IP do AP.
#sh /usr/share/nocat/bin/access.fw permit 00:27:22:29:F1:E5 10.0.0.2 Public

# Inicialização do Jboss. A opção -b 0.0.0.0 libera acesso à interface administrativa do scifi para qualquer IP.
# Por padrão, o acesso à interface de gerência do servidor de aplicações Jboss é liberado apenas para localhost. Para liberar a acesso para outro ip, insira a opção -bmanagement ip.
su - jboss -c "sh /usr/share/jboss-as-7.1.1.Final/bin/standalone.sh -b 0.0.0.0 &"

sleep 60

# Inicialização do Núcleo Central de Processamento do SCIFI. Um servidor tcp é criado em localhost,porta 5000 para receber mensagens provenientes da interface web de gerência. 
su - scifi -c "cd /usr/share/scifi/core;java -cp APController.jar loader.JLoader 127.0.0.1 5000 &> /dev/null"

exit 0
