# OVPN4ALL
## _Create your own VPN server in seconds_

OVPN4ALL is an open-source project whose main task is to facilitate the creation and administration of a VPN server.
It has been build with *Sveltekit* and *Spring Boot*.

### Features:
- VPN Server configuration
- Create and manage users
- VPN state info and logging

### How to use it:

This proyect is under development, for the moment you can download the whole project and try it out in Ubuntu, with several steps.
- Setup an Ubuntu virtual machine
- Install Java 19
- Clone whole repository
- Execute SetupDirectories.sh and SetupInstallRoot.sh scripts as root
- Configure backend env variables (*.yml files)
- Add root scripts and commands to sudoers file ```sudo visudo```
- Run backend ```mvnw spring-boot:run```
- Run frontend ```npm run dev```

**You have to open the vpn server port configured in your router**
