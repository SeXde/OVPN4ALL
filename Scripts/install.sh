#!/bin/bash

# Trap SigINT
trap ctrl_c INT

function ctrl_c() {
    printf "Exiting ...\n"
    sleep 1
    exit 1
}

function install_smth() {
    printf "$1 ...\n"
    if ! $($2 &> /dev/null); then
        printf "$3.\n" >&2
        exit 1
    fi
    printf "Done!\n"
}

# Check if user has sudo privileges
(( EUID )) && { printf "Run this script with root priviliges.\n"; exit 1; }

# Check if host has internet connection
install_smth "Checking internet connection" "ping -c 1 www.google.com" "Your internet connection seems to be down, try again later"

# Update repositories
install_smth "Updating repositories" "apt update -y" "Cannot update repositories"

# Install OpenVPN
install_smth "Installing OpenVPN" "apt install openvpn -y" "Cannot install OpenVPN"

# Install OpenSSL
install_smth "Installing OpenSSL" "apt install openssl -y" "Cannot install OpenSSL"

# Install Sed
install_smth "Installing Sed" "apt install sed -y" "Cannot install Sed"

install_smth "Installing Sed" "mkdir ~/OVPN4ALL/Install" "Cannot create install dir"
install_smth "Installing Sed" "mkdir -p ~/OVPN4ALL/db/users" "Cannot create users dir"
install_smth "Installing Sed" "mkdir -p ~/OVPN4ALL/db/certs" "Cannot create certs dir"
install_smth "Installing Sed" "mkdir -p ~/OVPN4ALL/logs" "Cannot create logs dir"
install_smth "Installing Sed" "mkdir -p ~/OVPN4ALL/Server" "Cannot create server dir"

exit 0
