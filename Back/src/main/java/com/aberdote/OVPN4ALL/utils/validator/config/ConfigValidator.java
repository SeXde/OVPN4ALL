package com.aberdote.OVPN4ALL.utils.validator.config;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.regex.Pattern;

public class ConfigValidator {

    public static boolean validateIp(String ip) {
        final String ipPattern = "(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)";
        return ip.matches(ipPattern);
    }

    public static boolean validatePort(String port) {
        if (port == null || port.isEmpty()) return false;
        int portNumber;
        try {
            portNumber = Integer.parseInt(port);
        } catch(Exception e) {
            return false;
        }
        return portNumber > 0 && portNumber < 65536;
    }

    public static boolean validateNetmask(String netmask) {
        final String netmaskPattern = "(255)\\.(0|128|192|224|240|248|252|254|255)\\.(0|128|192|224|240|248|252|254|255)\\.(0|128|192|224|240|248|252|254|255)";
        return netmask.matches(netmaskPattern);
    }

    public static boolean validateFQDN(String fqdn) {
        final String fqdnPattern = "^(?!://)(?=.{1,255}$)((.{1,63}\\.){1,127}(?![0-9]*$)[a-z0-9-]+\\.?)$";
        return Pattern.compile(fqdnPattern).matcher(fqdn).matches();
    }

    public static boolean validatePublicIp(String ip) {
        if (!validateIp(ip)) return false;
        try {
            final InetAddress ipAdd = InetAddress.getByName(ip);
            return !ipAdd.isSiteLocalAddress();
        } catch (UnknownHostException e) {
            return false;
        }
    }

    public static boolean validatePrivateIp(String ip) {
        if (!validateIp(ip)) return false;
        try {
            final InetAddress ipAdd = InetAddress.getByName(ip);
            return ipAdd.isSiteLocalAddress();
        } catch (UnknownHostException e) {
            return false;
        }
    }


}
