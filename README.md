# OVPN4ALL
## _Create your own VPN server in seconds_

OVPN4ALL is an open-source project whose main task is to facilitate the creation and administration of a VPN server.
It has been build with *Sveltekit* and *Spring Boot*.

### Features:
- VPN Server configuration
- Create and manage users
- VPN state info and logging

### How to use it:

This proyect is under development, for the moment you can download the virtual box machine and try it:
- Download OpenVPN4all VM [here](https://duckduckgo.com).
- User: *sexde*, password: *Ovpn4all2023!*
- (Recommended) Configure a static ip for VM.
-  Execute `sudo docker start ovpn4all-mysql`. This will start the database
-  Execute `ovpn4all`. This is a custom scripts which execute Front, Back and then opens a firefox window.
-  If no firefox window is shown open manually a new window and type `http://localhost:5173`.

If you have any problem, please check if your host machine has any kind of firewall or antivirus blocking the virtual machine network interface, also check if your router configuration it's working properly.
