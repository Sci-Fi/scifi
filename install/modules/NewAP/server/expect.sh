#!/usr/bin/expect -f
#debug mode: !/usr/bin/expect -D 1 
# para usar: 
#./script usuario@estacao 
# fonte: http://www.vivaolinux.com.br/dica/Usando-SSH-de-forma-automatica-com-senha-%28sem-publicar-chaves%29


set timeout 4

set xHost $argv
spawn ssh $xHost

expect {
    "*yes\/no*"
    {
    send "yes\r"
    exp_continue
    }
}
return 0 
