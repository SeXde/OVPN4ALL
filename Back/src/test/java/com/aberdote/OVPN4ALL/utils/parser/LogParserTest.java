package com.aberdote.OVPN4ALL.utils.parser;

import com.aberdote.OVPN4ALL.dto.parser.UserInfoDTO;
import org.junit.jupiter.api.Test;

public class LogParserTest {

    @Test
    public void getUserInfo() {
        final String user = "5a6f65";
        final String text = """
                Fri Nov  4 19:40:18 2022 us=872771 MULTI: multi_init called, r=256 v=256
                Fri Nov  4 19:40:18 2022 us=872789 IFCONFIG POOL: base=10.8.0.2 size=252, ipv6=0
                Fri Nov  4 19:40:18 2022 us=872800 ifconfig_pool_read(), in='6875657665726f,10.8.0.2', TODO: IPv6
                Fri Nov  4 19:40:18 2022 us=872805 succeeded -> ifconfig_pool_set()
                Fri Nov  4 19:40:18 2022 us=872817 ifconfig_pool_read(), in='5a6f65,10.8.0.3', TODO: IPv6
                Fri Nov  4 19:40:18 2022 us=872821 succeeded -> ifconfig_pool_set()
                Fri Nov  4 19:40:18 2022 us=872827 IFCONFIG POOL LIST
                Fri Nov  4 19:40:18 2022 us=872831 6875657665726f,10.8.0.2
                Fri Nov  4 19:40:18 2022 us=872835 5a6f65,10.8.0.3
                Fri Nov  4 19:40:18 2022 us=872854 Initialization Sequence Completed
                Fri Nov  4 19:40:44 2022 us=492177 MULTI: multi_create_instance called
                Fri Nov  4 19:40:44 2022 us=492309 31.4.181.212:39032 Re-using SSL/TLS context
                Fri Nov  4 19:40:44 2022 us=492383 31.4.181.212:39032 Control Channel MTU parms [ L:1621 D:1140 EF:110 EB:0 ET:0 EL:3 ]
                Fri Nov  4 19:40:44 2022 us=492390 31.4.181.212:39032 Data Channel MTU parms [ L:1621 D:1450 EF:121 EB:406 ET:0 EL:3 ]
                Fri Nov  4 19:40:44 2022 us=492415 31.4.181.212:39032 Local Options String (VER=V4): 'V4,dev-type tun,link-mtu 1549,tun-mtu 1500,proto UDPv4,keydir 0,cipher AES-256-GCM,auth [null-digest],keysize 256,tls-auth,key-method 2,tls-server'
                Fri Nov  4 19:40:44 2022 us=492435 31.4.181.212:39032 Expected Remote Options String (VER=V4): 'V4,dev-type tun,link-mtu 1549,tun-mtu 1500,proto UDPv4,keydir 1,cipher AES-256-GCM,auth [null-digest],keysize 256,tls-auth,key-method 2,tls-client'
                Fri Nov  4 19:40:44 2022 us=492455 31.4.181.212:39032 TLS: Initial packet from [AF_INET]31.4.181.212:39032, sid=434ae02e 2bcf3d22
                Fri Nov  4 19:40:44 2022 us=570054 31.4.181.212:39032 WARNING: Failed to stat CRL file, not (re)loading CRL.
                Fri Nov  4 19:40:44 2022 us=762839 31.4.181.212:39032 VERIFY OK: depth=1, CN=OVPN4ALL
                Fri Nov  4 19:40:44 2022 us=763378 31.4.181.212:39032 VERIFY KU OK
                Fri Nov  4 19:40:44 2022 us=763392 31.4.181.212:39032 Validating certificate extended key usage
                Fri Nov  4 19:40:44 2022 us=763398 31.4.181.212:39032 ++ Certificate has EKU (str) TLS Web Client Authentication, expects TLS Web Client Authentication
                Fri Nov  4 19:40:44 2022 us=763402 31.4.181.212:39032 VERIFY EKU OK
                Fri Nov  4 19:40:44 2022 us=763406 31.4.181.212:39032 VERIFY OK: depth=0, CN=5a6f65
                Fri Nov  4 19:40:44 2022 us=769490 31.4.181.212:39032 peer info: IV_VER=3.git::081bfebe
                Fri Nov  4 19:40:44 2022 us=769506 31.4.181.212:39032 peer info: IV_PLAT=ios
                Fri Nov  4 19:40:44 2022 us=769510 31.4.181.212:39032 peer info: IV_NCP=2
                Fri Nov  4 19:40:44 2022 us=769515 31.4.181.212:39032 peer info: IV_TCPNL=1
                Fri Nov  4 19:40:44 2022 us=769519 31.4.181.212:39032 peer info: IV_PROTO=30
                Fri Nov  4 19:40:44 2022 us=769523 31.4.181.212:39032 peer info: IV_CIPHERS=AES-256-GCM:AES-128-GCM:CHACHA20-POLY1305
                Fri Nov  4 19:40:44 2022 us=769527 31.4.181.212:39032 peer info: IV_AUTO_SESS=1
                Fri Nov  4 19:40:44 2022 us=769532 31.4.181.212:39032 peer info: IV_GUI_VER=net.openvpn.connect.ios_3.3.2-5086
                Fri Nov  4 19:40:44 2022 us=769536 31.4.181.212:39032 peer info: IV_SSO=webauth,openurl,crtext
                Fri Nov  4 19:40:44 2022 us=769549 31.4.181.212:39032 WARNING: 'link-mtu' is used inconsistently, local='link-mtu 1549', remote='link-mtu 1521'
                Fri Nov  4 19:40:44 2022 us=821757 31.4.181.212:39032 Control Channel: TLSv1.3, cipher TLSv1.3 TLS_AES_256_GCM_SHA384, 521 bit EC, curve: secp521r1
                Fri Nov  4 19:40:44 2022 us=821819 31.4.181.212:39032 [5a6f65] Peer Connection Initiated with [AF_INET]31.4.181.212:39032
                Fri Nov  4 19:40:44 2022 us=821834 5a6f65/31.4.181.212:39032 MULTI_sva: pool returned IPv4=10.8.0.3, IPv6=(Not enabled)
                Fri Nov  4 19:40:44 2022 us=821854 5a6f65/31.4.181.212:39032 MULTI: Learn: 10.8.0.3 -> 5a6f65/31.4.181.212:39032
                Fri Nov  4 19:40:44 2022 us=821860 5a6f65/31.4.181.212:39032 MULTI: primary virtual IP for 5a6f65/31.4.181.212:39032: 10.8.0.3
                Fri Nov  4 19:40:44 2022 us=821883 5a6f65/31.4.181.212:39032 PUSH: Received control message: 'PUSH_REQUEST'
                Fri Nov  4 19:40:44 2022 us=821906 5a6f65/31.4.181.212:39032 SENT CONTROL [5a6f65]: 'PUSH_REPLY,route 192.168.5.1 255.255.255.0,redirect-gateway def1,dhcp-option DNS 1.1.1.1,dhcp-option DNS 1.0.0.1,route-gateway 10.8.0.1,topology subnet,ping 10,ping-restart 120,ifconfig 10.8.0.3 255.255.255.0,peer-id 0,cipher AES-256-GCM' (status=1)
                Fri Nov  4 19:40:44 2022 us=821919 5a6f65/31.4.181.212:39032 Data Channel MTU parms [ L:1549 D:1450 EF:49 EB:406 ET:0 EL:3 ]
                Fri Nov  4 19:40:44 2022 us=821988 5a6f65/31.4.181.212:39032 Outgoing Data Channel: Cipher 'AES-256-GCM' initialized with 256 bit key
                Fri Nov  4 19:40:44 2022 us=821994 5a6f65/31.4.181.212:39032 Incoming Data Channel: Cipher 'AES-256-GCM' initialized with 256 bit key
                Fri Nov  4 19:42:09 2022 us=235887 5a6f65/31.4.181.212:39032 SIGTERM[soft,remote-exit] received, client-instance exiting
                Mon Nov  7 02:40:44 2022 us=763406 31.4.181.212:39032 VERIFY OK: depth=0, CN=5a6f65
                Fri Nov  4 19:40:44 2022 us=769536 31.4.181.212:39032 peer info: IV_SSO=webauth,openurl,crtext
                Fri Nov  4 19:40:44 2022 us=769549 31.4.181.212:39032 WARNING: 'link-mtu' is used inconsistently, local='link-mtu 1549', remote='link-mtu 1521'
                Fri Nov  4 19:40:44 2022 us=821757 31.4.181.212:39032 Control Channel: TLSv1.3, cipher TLSv1.3 TLS_AES_256_GCM_SHA384, 521 bit EC, curve: secp521r1
                Fri Nov  4 19:40:44 2022 us=821819 31.4.181.212:39032 [5a6f65] Peer Connection Initiated with [AF_INET]31.4.181.212:39032
                Fri Nov  4 19:40:44 2022 us=821834 5a6f65/31.4.181.212:39032 MULTI_sva: pool returned IPv4=10.8.0.3, IPv6=(Not enabled)
                Fri Nov  4 19:40:44 2022 us=821854 5a6f65/31.4.181.212:39032 MULTI: Learn: 10.8.0.3 -> 5a6f65/31.4.181.212:39032
                Fri Nov  4 19:40:44 2022 us=821860 5a6f65/31.4.181.212:39032 MULTI: primary virtual IP for 5a6f65/31.4.181.212:39032: 10.8.0.3
                Fri Nov  4 19:40:44 2022 us=821883 5a6f65/31.4.181.212:39032 PUSH: Received control message: 'PUSH_REQUEST'
                Fri Nov  4 19:40:44 2022 us=821906 5a6f65/31.4.181.212:39032 SENT CONTROL [5a6f65]: 'PUSH_REPLY,route 192.168.5.1 255.255.255.0,redirect-gateway def1,dhcp-option DNS 1.1.1.1,dhcp-option DNS 1.0.0.1,route-gateway 10.8.0.1,topology subnet,ping 10,ping-restart 120,ifconfig 10.8.0.3 255.255.255.0,peer-id 0,cipher AES-256-GCM' (status=1)
                Fri Nov  4 19:40:44 2022 us=821919 5a6f65/31.4.181.212:39032 Data Channel MTU parms [ L:1549 D:1450 EF:49 EB:406 ET:0 EL:3 ]
                Fri Nov  4 19:40:44 2022 us=821988 5a6f65/31.4.181.212:39032 Outgoing Data Channel: Cipher 'AES-256-GCM' initialized with 256 bit key
                Fri Nov  4 19:40:44 2022 us=821994 5a6f65/31.4.181.212:39032 Incoming Data Channel: Cipher 'AES-256-GCM' initialized with 256 bit key
                Fri Nov  4 19:42:09 2022 us=235887 5a6f65/31.4.181.212:39032 SIGTERM[soft,remote-exit] received, client-instance exiting
                """;
        UserInfoDTO userInfoDTO =  LogParser.getUserInfo(user, text);
        System.out.println(userInfoDTO);
    }

}
