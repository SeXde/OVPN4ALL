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

mkdir "$WD_DIR"/Logs || { printf "Cannot create Logs folder"; exit 1; }
log "Creating Install folder" "mkdir $WD_DIR/Install" "Cannot create Install folder"
log "Creating Users folder" "mkdir $WD_DIR/Users" "Cannot create Users folder"
log "Creating Server folder" "mkdir $WD_DIR/Server" "Cannot create Server folder"
exit 0
