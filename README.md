# How to use OVPN4ALL
- 1º Execute ./Script/Install/install.sh with root privileges
- 2º Execute ./Script/EasyRSA/install_EasyRSA.sh
- 3º Execute ./Script/Configs/server_Config.sh <Port> <Gateway> <Subnet>
- 4º Execute ./Script/EasyRSA/create_User_Certificate.sh <Username> <Password>
- 5º Execute ./Script/Configs/client_Config.sh <ClientName> <Server> <Port>
- 6º Execute ./Script/iptables.sh <Port> with root privileges
