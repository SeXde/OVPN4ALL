#!/bin/bash

# Trap SigINT
trap ctrl_c INT

ARG_NUMBER=1

function ctrl_c() {
    printf "Exiting ...\n"
    sleep 1
    exit 1
}

# Check if arguments are wrong
if test "$#" -ne $ARG_NUMBER; then
    printf "Usage: %s [Log file].\n" "$0" >&2
    printf "Expected %s argument/s but %s were passed" "$ARG_NUMBER" "$#"
    exit 1
fi

LOG_FILE="$1"

function log() {
    printf "%s ...\n" "$1" &>> "$LOG_FILE"
    bash -c "$2" &>> "$LOG_FILE" ||  { printf "%s" "$3" &>> "$LOG_FILE"; exit 1; }
    
}

(( EUID )) && { printf "Run this script with root priviliges.\n" &>> "$LOG_FILE" ; exit 1; }
log "Checking internet connection" "ping -c 1 www.google.com" "Cannot create Install folder"
log "Updating repositories" "apt update -y" "Cannot update repositories"
log "Installing OpenVPN" "apt install openvpn -y" "Cannot install OpenVPN"
log "Installing OpenSSL" "apt install openssl -y" "Cannot install OpenSSL"
log "Installing Sed" "apt install sed -y" "Cannot install Sed"
log "Installing xxd" "apt install xxd -y" "Cannot install xxd"
log "Installing zip" "apt install zip -y" "Cannot install zip"
exit 0
