#!/bin/bash

OUTPUT=$(cat /var/log/openvpn.log)
echo "$OUTPUT"
exit 0