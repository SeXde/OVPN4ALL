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

cd OVPN4ALL || (printf "OVPN4ALL directory not found" >&2 && exit 1)

# Fill server config file
printf "Configuring server file ...\n"
cp ../client_example.ovpn /tmp/"$1".ovpn || exit 1
printf "writing server ...\n"
sed -i "s/<fill_server>/$2/g" /tmp/"$1".ovpn
printf "writing port ...\n"
sed -i "s/<fill_port>/$3/g" /tmp/"$1".ovpn

ta=$(cat Install/EasyRSA/ta.key)
ca=$(cat Install/EasyRSA/pki/ca.crt)
cert=$(grep  -zo -- "-*BEGIN.*END.*-*$" Install/EasyRSA/pki/issued/"$1".crt)
key=$(cat Install/EasyRSA/pki/private/"$1".key)

printf "writing ta ...\n"
echo -en "<tls-auth>\n$ta\n</tls-auth>\n" >> /tmp/"$1".ovpn

printf "writing ca ...\n"
echo -en "<ca>\n$ca\n</ca>\n" >> /tmp/"$1".ovpn

printf "writing cert ...\n"
echo -en "<cert>\n$cert\n</cert>\n" >> /tmp/"$1".ovpn

printf "writing key ...\n"
echo -en "<key>\n$key\n</key>\n" >> /tmp/"$1".ovpn
printf "Done!\n"
rm Clients/"$1".ovpn
cp /tmp/"$1".ovpn Clients/"$1".ovpn
exit 0
