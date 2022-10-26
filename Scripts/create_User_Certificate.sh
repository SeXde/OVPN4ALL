#!/bin/bash

# Trap SigINT
trap ctrl_c INT

function ctrl_c() {
    printf "Exiting ...\n"
    sleep 1
    exit 1
}

# Check if arguments are wrong
if test "$#" -ne 2; then
    printf "Usage: %s [Username] [Password].\n" "$0" >&2
    exit 1
fi

cd ~/OVPN4ALL || (printf "OVPN4ALL directory not found"; exit 1)

# Create user certificate
cd Install/EasyRSA || (printf "EasyRSA directory not found" ; exit 1)
echo -en "$1\n" | ./easyrsa --passout=pass:"$2" gen-req "$1" &> /dev/null || (printf "cannot gen user cert" ; exit 1)
printf "Done!\n"

# Sign user certificate
printf "Signing %s certificate ...\n" "$1"
echo -en "yes\n" | ./easyrsa sign-req client "$1" &> /dev/null || (printf "cannot sign user cert" ; exit 1)
printf "Done!\n"
exit 0
