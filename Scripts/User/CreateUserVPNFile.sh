#!/bin/bash

# Trap SigINT
trap ctrl_c INT

ARG_NUMBER=5

function ctrl_c() {
    printf "Exiting ...\n"
    sleep 1
    exit 1
}

# Check if arguments are wrong
if test "$#" -ne $ARG_NUMBER; then
    printf "Usage: %s [Working directory] [Log file] [Client Name] [Server] [Port].\n" "$0" >&2
    printf "Expected %s argument/s but %s were passed" "$ARG_NUMBER" "$#"
    exit 1
fi

LOG_FILE="$2"
WD_DIR="$1"
USERNAME="$3"
SERVER="$4"
PORT="$5"

function log() {
    printf "%s ...\n" "$1" &>> "$WD_DIR/$LOG_FILE"
    bash -c "$2" &>> "$WD_DIR/$LOG_FILE" ||  { printf "%s" "$3" &>> "$WD_DIR/$LOG_FILE"; exit 1; }
    
}


log "Configuring server file" "cp $WD_DIR/Scripts/Config/client_example.ovpn /tmp/$USERNAME.ovpn" "Cannot copy client conf example"
log "Writing server" "sed -i 's/<fill_server>/$SERVER/g' /tmp/$USERNAME.ovpn" "Cannot set server ip inside client conf"
log "Writing port" "sed -i 's/<fill_port>/$PORT/g' /tmp/$USERNAME.ovpn" "Cannot set port inside client conf"
TA=$(cat "$WD_DIR"/Install/EasyRSA/ta.key)
CA=$(cat "$WD_DIR"/Install/EasyRSA/pki/ca.crt)
CERT=$(grep  -zo -- "-*BEGIN.*END.*-*$" "$WD_DIR"/Install/EasyRSA/pki/issued/"$USERNAME".crt)
KEY=$(cat "$WD_DIR"/Install/EasyRSA/pki/private/"$USERNAME".key)
log "Writing ta" "echo -en '<tls-auth>\n$TA\n</tls-auth>\n' >> /tmp/$USERNAME.ovpn" "Cannot set ta"
log "Writing ca" "echo -en '<ca>\n$CA\n</ca>\n' >> /tmp/$USERNAME.ovpn" "Cannot set ca"
log "Writing cert" "echo -en '<cert>\n$CERT\n</cert>\n' >> /tmp/$USERNAME.ovpn" "Cannot set cert"
log "Writing key" "echo -en '<key>\n$KEY\n</key>\n' >> /tmp/$USERNAME.ovpn" "Cannot set key"
rm "$WD_DIR"/Users/"$USERNAME".ovpn &>> /dev/null
log "Moving user config file" "mv /tmp/$USERNAME.ovpn $WD_DIR/Users/$USERNAME.ovpn" "Cannot move user config file"
rm /tmp/"$USERNAME".ovpn &>> /dev/null
exit 0
