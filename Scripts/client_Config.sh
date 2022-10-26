#!/bin/bash

# Trap SigINT
trap ctrl_c INT

function ctrl_c() {
    printf "Exiting ...\n"
    sleep 1
    exit 1
}

# Check if arguments are wrong
if test "$#" -ne 3; then
    printf "Usage: %s [Client Name] [Server] [Port].\n" "$0" >&2
    exit 1
fi

cd ~/OVPN4ALL || (printf "OVPN4ALL directory not found" ; exit 1)

# Fill server config file
printf "Configuring server file ...\n"
cp ~/OVPN4ALL/Scripts/client_example.ovpn /tmp/"$1".ovpn || (printf "Cannot copy client conf example" ; exit 1)
printf "writing server ...\n"
sed -i "s/<fill_server>/$2/g" /tmp/"$1".ovpn || (printf "Cannot set server ip inside client conf" ; exit 1)
printf "writing port ...\n"
sed -i "s/<fill_port>/$3/g" /tmp/"$1".ovpn || (printf "Cannot copy port inside conf example" ; exit 1)

ta=$(cat ~/OVPN4ALL/Install/EasyRSA/ta.key)
ca=$(cat ~/OVPN4ALL/Install/EasyRSA/pki/ca.crt)
cert=$(grep  -zo -- "-*BEGIN.*END.*-*$" ~/OVPN4ALL/Install/EasyRSA/pki/issued/"$1".crt)
key=$(cat ~/OVPN4ALL/Install/EasyRSA/pki/private/"$1".key)

printf "writing ta ...\n"
echo -en "<tls-auth>\n$ta\n</tls-auth>\n" >> /tmp/"$1".ovpn || (printf "Cannot set ta" ; exit 1)

printf "writing ca ...\n"
echo -en "<ca>\n$ca\n</ca>\n" >> /tmp/"$1".ovpn || (printf "Cannot set ca" ; exit 1)

printf "writing cert ...\n"
echo -en "<cert>\n$cert\n</cert>\n" >> /tmp/"$1".ovpn || (printf "Cannot set cert" ; exit 1)

printf "writing key ...\n"
echo -en "<key>\n$key\n</key>\n" >> /tmp/"$1".ovpn || (printf "Cannot set key" ; exit 1)
printf "Done!\n"
rm ~/OVPN4ALL/db/users/"$1".ovpn &> /dev/null
cp /tmp/"$1".ovpn ~/OVPN4ALL/db/users/"$1".ovpn  || (printf "Cannot copy .ovpn file" ; exit 1)
exit 0
