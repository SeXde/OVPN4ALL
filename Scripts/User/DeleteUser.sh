#!/bin/bash

# Trap SigINT
trap ctrl_c INT

ARG_NUMBER=3

function ctrl_c() {
    printf "Exiting ...\n"
    sleep 1
    exit 1
}

# Check if arguments are wrong
if test "$#" -ne $ARG_NUMBER; then
    printf "Usage: %s [Working directory] [Log file] [Username].\n" "$0" >&2
    printf "Expected %s argument/s but %s were passed" "$ARG_NUMBER" "$#"
    exit 1
fi

LOG_FILE="$2"
WD_DIR="$1"
USERNAME="$3"

function log() {
    printf "%s ...\n" "$1" &>> "$WD_DIR/$LOG_FILE"
    bash -c "$2" &>> "$WD_DIR/$LOG_FILE" ||  { printf "%s" "$3" &>> "$WD_DIR/$LOG_FILE"; exit 1; }
    
}

log "Revoking user certificate" "cd $WD_DIR/Install/EasyRSA ; echo -en 'yes\n' | ./easyrsa revoke $USERNAME" "Cannot revoke user certificate"
log "Uploading crl" "cd $WD_DIR/Install/EasyRSA ; ./easyrsa gen-crl" "Cannot upload crl"
log "Coping new crl" "cp $WD_DIR/Install/EasyRSA/pki/crl.pem /var/lib/openvpn/chroot/crl.pem" "Cannot upload crl"
/home/sexde/OVPN4ALL
rm "$WD_DIR"/Users/"$USERNAME".ovpn &>> /dev/null
exit 0
