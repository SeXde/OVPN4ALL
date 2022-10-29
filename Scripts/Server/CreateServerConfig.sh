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
    printf "Usage: %s [Working directory] [Log file] [Port] [Gateway] [Subnet].\n" "$0" >&2
    printf "Expected %s argument/s but %s were passed" "$ARG_NUMBER" "$#"
    exit 1
fi

LOG_FILE="$2"
WD_DIR="$1"
PORT="$3"
GATEWAY="$4"
SUBNET="$5"

function log() {
    printf "%s ...\n" "$1" &>> "$WD_DIR/$LOG_FILE"
    bash -c "$2" &>> "$WD_DIR/$LOG_FILE" ||  { printf "%s" "$3" &>> "$WD_DIR/$LOG_FILE"; exit 1; }
    
}

log "Configuring server file" "cp $WD_DIR/Scripts/Config/server_example.conf /tmp/OVPN4ALL.conf" "Cannot copy server conf file"
log "Writing port" "sed -i 's/<fill_port>/$PORT/g' /tmp/OVPN4ALL.conf" "Cannot set port"
log "Writing gateway" "sed -i 's/<fill_gateway>/$GATEWAY/g' /tmp/OVPN4ALL.conf" "Cannot set gateway"
log "Writing subnet" "sed -i 's/<fill_subnet>/$SUBNET/g' /tmp/OVPN4ALL.conf" "Cannot set subnet"
TA=$(cat "$WD_DIR"/Install/EasyRSA/ta.key)
CA=$(cat  "$WD_DIR"/Install/EasyRSA/pki/ca.crt)
CERT=$(grep  -zo -- "-*BEGIN.*END.*-*$"  "$WD_DIR"/Install/EasyRSA/pki/issued/OVPN4ALL-Server.crt)
KEY=$(cat  "$WD_DIR"/Install/EasyRSA/pki/private/OVPN4ALL-Server.key)
log "Writing ta" "echo -en '<tls-auth>\n$TA\n</tls-auth>\n' >> /tmp/OVPN4ALL.conf" "Cannot write ta"
log "Writing ca" "echo -en '<ca>\n$CA\n</ca>\n' >> /tmp/OVPN4ALL.conf" "Cannot write ca"
log "Writing cert" "echo -en '<cert>\n$CERT\n</cert>\n' >> /tmp/OVPN4ALL.conf " "Cannot write cert"
log "Writing key" "echo -en '<key>\n$KEY\n</key>\n' >> /tmp/OVPN4ALL.conf" "Cannot write key"
rm "$WD_DIR"/Server/OVPN4ALL.conf &> /dev/null
log "Setting server conf" "cp /tmp/OVPN4ALL.conf $WD_DIR/Server/OVPN4ALL.conf" "Cannot set server page"
rm /tmp/OVPN4ALL.conf &> /dev/null
exit 0
