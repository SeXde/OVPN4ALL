#!/bin/bash

# Trap SigINT
trap ctrl_c INT

ARG_NUMBER=4

function ctrl_c() {
    printf "Exiting ...\n"
    sleep 1
    exit 1
}

# Check if arguments are wrong
if test "$#" -ne $ARG_NUMBER; then
    printf "Usage: %s [Working directory] [Log file] [Username] [Password].\n" "$0" >&2
    printf "Expected %s argument/s but %s were passed" "$ARG_NUMBER" "$#"
    exit 1
fi

LOG_FILE="$2"
WD_DIR="$1"
USERNAME="$3"
PASSWORD="$4"

function log() {
    printf "%s ...\n" "$1" &>> "$WD_DIR/$LOG_FILE"
    bash -c "$2" &>> "$WD_DIR/$LOG_FILE" ||  { printf "%s" "$3" &>> "$WD_DIR/$LOG_FILE"; exit 1; }
    
}

log "Creating user certificate" "cd $WD_DIR/Install/EasyRSA ; echo -en '$USERNAME\n' | ./easyrsa --passout=pass:$PASSWORD gen-req $USERNAME" "Cannot gen user cert"
log "Signing user certificate" "cd $WD_DIR/Install/EasyRSA ; echo -en 'yes\n' | ./easyrsa sign-req client $USERNAME" "Cannot sign user cert"
exit 0
