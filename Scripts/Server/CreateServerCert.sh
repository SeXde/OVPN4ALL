#!/bin/bash

# Trap SigINT
trap ctrl_c INT

ARG_NUMBER=2

function ctrl_c() {
    printf "Exiting ...\n"
    sleep 1
    exit 1
}

# Check if arguments are wrong
if test "$#" -ne $ARG_NUMBER; then
    printf "Usage: %s [Working directory] [Log file].\n" "$0" >&2
    printf "Expected %s argument/s but %s were passed" "$ARG_NUMBER" "$#"
    exit 1
fi

LOG_FILE="$2"
WD_DIR="$1"

function log() {
    printf "%s ...\n" "$1" &>> "$WD_DIR/$LOG_FILE"
    bash -c "$2" &>> "$WD_DIR/$LOG_FILE" ||  { printf "%s" "$3" &>> "$WD_DIR/$LOG_FILE"; exit 1; }
    
}

log "Checking internet connection" "ping -c 1 www.google.com" "Your internet connection seems to be down, try again later"
log "Downloading EASY-RSA" "cd $WD_DIR/Install ; wget -q -O - 'https://github.com/OpenVPN/easy-rsa/releases/download/v3.1.0/EasyRSA-3.1.0.tgz' | tar zxv" "Cannot download EasyRSA"
log "Moving EASY-RSA folder to Install directory" "cd $WD_DIR/Install ; mv EasyRSA-3.1.0 $WD_DIR/Install/EasyRSA" "Cannot move EASY-RSA directory"
log "Creating pki" "cd $WD_DIR/Install/EasyRSA ; ./easyrsa init-pki" "Cannot create pki"
log "Copying vars file into pki" "cp $WD_DIR/Scripts/Config/vars $WD_DIR/Install/EasyRSA/pki" "Cannot copy vars to pki directory"
log "Building CA" "cd $WD_DIR/Install/EasyRSA ; echo -en 'OVPN4ALL\n' | ./easyrsa build-ca nopass" "Cannot build CA"
log "Generate server certificate" "cd $WD_DIR/Install/EasyRSA ; echo -en 'OVPN4ALL\n' | ./easyrsa gen-req OVPN4ALL-Server nopass" "Cannot generate Server cert"
log "Sign server certificate" "cd $WD_DIR/Install/EasyRSA ; echo -en 'yes\n' | ./easyrsa sign-req server OVPN4ALL-Server" "Cannot sign Server cert"
log "Generating ta key" "openvpn --genkey --secret $WD_DIR/Install/EasyRSA/ta.key" "Cannot generate ta key"
exit 0
