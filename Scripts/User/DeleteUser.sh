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

log "Deleting user certificate" "rm $WD_DIR/Install/EasyRSA/pki/issued/$USERNAME.crt" "Cannot delete user certificate"
log "Deleting user private key" "rm $WD_DIR/Install/EasyRSA/pki/private/$USERNAME.key" "Cannot delete user private key"
log "Deleting user config file" "rm $WD_DIR/Users/$USERNAME.ovpn" "Cannot delete user config file"
exit 0
