#!/bin/bash

# Trap SigINT
trap ctrl_c INT

function ctrl_c() {
    printf "Exiting ...\n" &>> /home/sexde/OVPN4ALL/logs/scripts.log
    sleep 1
    exit 1
}

# Check if arguments are wrong
if test "$#" -ne 4; then
    printf "Usage: %s [Username] [Password] [Server] [Port].\n" "$0" &>> /home/sexde/OVPN4ALL/logs/scripts.log
    printf "Got $# arguments" &>> /home/sexde/OVPN4ALL/logs/scripts.log
    exit 1
fi

bash -c "/home/sexde/OVPN4ALL/Scripts/create_User_Certificate.sh $1 $2" &>> /home/sexde/OVPN4ALL/logs/scripts.log || (printf "cannot create user cert" &>> /home/sexde/OVPN4ALL/logs/scripts.log ; exit 1)
bash -c "/home/sexde/OVPN4ALL/Scripts/client_Config.sh $1 $3 $4" &>> /home/sexde/OVPN4ALL/logs/scripts.log || (printf "cannot create client config" &>> /home/sexde/OVPN4ALL/logs/scripts.log ; exit 1)

exit 0
