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
    printf "Usage: %s [Port] [Gateway] [Subnet].\n" "$0" >&2
    exit 1
fi

cd OVPN4ALL || (printf "OVPN4ALL directory not found" >&2 && exit 1)

# Fill server config file
printf "Configuring server file ...\n"
cp ../server_example.conf /tmp/OVPN4ALL.conf || exit 1
printf "writing port ...\n"
sed -i "s/<fill_port>/$1/g" /tmp/OVPN4ALL.conf
printf "writing gateway ...\n"
sed -i "s/<fill_gateway>/$2/g" /tmp/OVPN4ALL.conf
printf "writing subnet ...\n"
sed -i "s/<fill_subnet>/$3/g" /tmp/OVPN4ALL.conf

ta=$(cat Install/EasyRSA/ta.key)
ca=$(cat  Install/EasyRSA/pki/ca.crt)
cert=$(grep  -zo -- "-*BEGIN.*END.*-*$"  Install/EasyRSA/pki/issued/OVP4ALL-Server.crt)
key=$(cat  Install/EasyRSA/pki/private/OVP4ALL-Server.key)

printf "writing ta ...\n"
echo -en "<tls-auth>\n$ta\n</tls-auth>\n" >> /tmp/OVPN4ALL.conf

printf "writing ca ...\n"
echo -en "<ca>\n$ca\n</ca>\n" >> /tmp/OVPN4ALL.conf

printf "writing cert ...\n"
echo -en "<cert>\n$cert\n</cert>\n" >> /tmp/OVPN4ALL.conf

printf "writing key ...\n"
echo -en "<key>\n$key\n</key>\n" >> /tmp/OVPN4ALL.conf

rm Server/OVPN4ALL.conf
cp /tmp/OVPN4ALL.conf Server/OVPN4ALL.conf
printf "Done!\n"
exit 0
