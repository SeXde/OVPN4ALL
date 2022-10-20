package com.aberdote.OVPN4ALL.util.validator.config;

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

}
