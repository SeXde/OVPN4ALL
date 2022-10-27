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
    prinf "Expected %s argument/s but %s were passed" "$ARG_NUMBER" "$#"
    exit 1
fi

LOG_FILE="$1"

function log() {
    printf "%s ...\n" "$1" &>> "$LOG_FILE"
    bash -c "$2" &>> "$LOG_FILE" ||  { printf "%s" "$3" &>> "$LOG_FILE"; exit 1; }
    
}

# Check if user has sudo privileges
(( EUID )) && { printf "Run this script with root priviliges.\n" &>> "$LOG_FILE" ; exit 1; }

# Check if host has internet connection
log "Checking internet connection" "ping -c 1 www.google.com" "Cannot create Install folder"

# Update repositories
log "Updating repositories" "apt update -y" "Cannot update repositories"

# Install OpenVPN
log "Installing OpenVPN" "apt install openvpn -y" "Cannot install OpenVPN"

# Install OpenSSL
log "Installing OpenSSL" "apt install openssl -y" "Cannot install OpenSSL"

# Install Sed
log "Installing Sed" "apt install sed -y" "Cannot install Sed"

exit 0
