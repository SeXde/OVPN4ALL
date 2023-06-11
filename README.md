# OVPN4ALL
## _Create your own VPN server in seconds_

OVPN4ALL is an open-source project whose main task is to facilitate the creation and administration of a VPN server.
It has been built with *Sveltekit* and *Spring Boot*.

### Features:
- VPN Server configuration
- Create and manage users
- VPN state info and logging

### How to use it:

This project is under development. For the moment, you can download the virtual box machine and try it:
- Download OpenVPN4all VM [here](https://mega.nz/file/z153QAgI#PrtCfff5yVx9Lnsn-u1H5th8_Cfm4K_6SwACHW9bKpQ).
- User: `sexde`, password: `Ovpn4all2023!`
- (Recommended) Configure a static ip for VM.
- Check VM ip address ` ip a | grep enp0s3`
- Choose any port for the vpn server.
- Configure router settings for redirecting traffic from the chosen port to the VM ip address
- Execute `ovpn4all [VM ip]`. This script receives the VM ip address and starts DB, Front and Back.
- Open with your favorite browser the url `http://[VM ip]:5173`. It's highly recommended to use dark mode when using OVPN4ALL webpage.

If you have any problem, please check if your host machine has any kind of firewall or antivirus blocking the virtual machine network interface. Also, check if your router configuration is working properly.
