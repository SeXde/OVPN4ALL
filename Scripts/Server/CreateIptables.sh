#!/bin/bash

# Trap SigINT
trap ctrl_c INT

function ctrl_c() {
    printf "Exiting ...\n"
    sleep 1
    exit 1
}

# Check if user has sudo privileges
(( EUID )) && { printf "Run this script with root priviliges.\n"; exit 1; }

# Check if arguments are wrong
if test "$#" -ne 1; then
    printf "Usage: %s [Port].\n" "$0" >&2
    exit 1
fi

echo 1 > /proc/sys/net/ipv4/ip_forward

# Flush All Iptables Chains/Firewall rules #
iptables -F
 
# Delete all Iptables Chains #
iptables -X
 
# Flush all counters too #
iptables -Z 
# Flush and delete all nat and  mangle #
iptables -t nat -F
iptables -t nat -X
iptables -t mangle -F
iptables -t mangle -X
iptables -t raw -F
iptables -t raw -X

iptables -P INPUT ACCEPT
iptables -P FORWARD ACCEPT
iptables -P OUTPUT ACCEPT
iptables -A INPUT -i enp0s3 -m state --state NEW -p udp --dport "$1" -j ACCEPT
iptables -A INPUT -i tun+ -j ACCEPT
iptables -A FORWARD -i tun+ -j ACCEPT
iptables -A FORWARD -i tun+ -o enp0s3 -m state --state RELATED,ESTABLISHED -j ACCEPT
iptables -A FORWARD -i enp0s3 -o tun+ -m state --state RELATED,ESTABLISHED -j ACCEPT
iptables -t nat -A POSTROUTING -s 10.8.0.0/24 -o enp0s3 -j MASQUERADE
iptables -A OUTPUT -o tun+ -j ACCEPT
exit 0
