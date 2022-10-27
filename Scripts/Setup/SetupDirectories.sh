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
    "$2" &>> "$LOG_FILE" ||  { printf "%s" "$3" &>> "$LOG_FILE"; exit 1; }
    
}

mkdir ../Logs || { printf "Cannot create Logs folder"; exit 1; }
log "Creating Install folder" "mkdir ../Install" "Cannot create Install folder"
log "Creating Users folder" "mkdir ../Users" "Cannot create Users folder"
log "Creating Server folder" "mkdir ../Server" "Cannot create Server folder"

exit 0;
