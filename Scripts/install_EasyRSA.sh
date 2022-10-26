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

cd ~/OVPN4ALL/Install || (printf "Install directory not found";exit 1)

# Check if host has internet connection
install_smth "Checking internet connection" "ping -c 1 www.google.com" "Your internet connection seems to be down, try again later"

# Donwload latest version of Easy-RSA
(wget -q -O - "https://github.com/OpenVPN/easy-rsa/releases/download/v3.1.0/EasyRSA-3.1.0.tgz" | tar zxv) || (printf "Cannot install EasyRSA";exit 1)

 # Verify Easy-RSA download
install_smth "Downloading Easy-RSA" "ls  EasyRSA-3.1.0" "Cannot download EasyRSA"

# Rename EasyRSA folder
mv EasyRSA-"3.1.0" EasyRSA || (printf "Cannot rename EasyRSA directory" ;exit 1)

# Start pki
printf "Creating pki ...\n"
cd EasyRSA || (printf "EasyRSA directory not found" ;exit 1)
./easyrsa init-pki || (printf "Cannot create pki" ;exit 1)
cp ~/OVPN4ALL/Scripts/vars pki || (printf "Cannot copy vars to pki directory" ;exit 1)
printf "Done!\n"

# Build CA
printf "Building CA ...\n"
(echo -en "OVPN4ALL\n" | ./easyrsa build-ca nopass) || (printf "Cannot build CA" ;exit 1)
printf "Done!\n"

# Create Server certificate and sign it
printf "Creating server certificate and signing it ...\n"
echo -en "OVPN4ALL\n" | ./easyrsa gen-req OVP4ALL-Server nopass || (printf "Cannot generate Server cert" ;exit 1)
echo -en "yes\n" | ./easyrsa sign-req server OVP4ALL-Server || (printf "Cannot sign Server cert" ;exit 1)
printf "Done!\n"

# Gen ta key
printf "Generating ta key ...\n"
openvpn --genkey --secret ta.key || (printf "Cannot generate ta key";exit 1)
printf "Done!\n"
exit 0
