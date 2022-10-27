#!/bin/bash

# Trap SigINT
trap ctrl_c INT

function ctrl_c() {
    printf "Exiting ...\n"
    sleep 1
    exit 1
}

rm -r OVPN4ALL &> /dev/null
mkdir -p OVPN4ALL/Server
mkdir -p OVPN4ALL/Clients
mkdir -p OVPN4ALL/Install
exit 0