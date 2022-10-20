package com.aberdote.OVPN4ALL.util;

import com.aberdote.OVPN4ALL.common.constanst.RoleConstants;
import com.aberdote.OVPN4ALL.entity.RoleEntity;
import com.aberdote.OVPN4ALL.entity.UserEntity;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Validator {

    public static boolean validateIp(String ip) {
        String ipPattern = "(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)";
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
        String netmaskPattern = "(255)\\.(0|128|192|224|240|248|252|254|255)\\.(0|128|192|224|240|248|252|254|255)\\.(0|128|192|224|240|248|252|254|255)";
        return netmask.matches(netmaskPattern);
    }

    public static boolean validateRole(UserEntity user, String roleName) {
        final Map<String, Set<String>>  allowedRoles = new HashMap<>();
        allowedRoles.put(RoleConstants.ROLE_USER,
                Set.of());
        allowedRoles.put(RoleConstants.ROLE_ADMIN,
                Set.of(RoleConstants.ROLE_USER,
                        RoleConstants.ROLE_ADMIN));
        allowedRoles.put(RoleConstants.ROLE_OWNER,
                Set.of(RoleConstants.ROLE_USER,
                        RoleConstants.ROLE_ADMIN,
                        RoleConstants.ROLE_OWNER));
        return user.getRoles()
                .stream()
                .map(RoleEntity::getRoleName)
                .anyMatch(userRoleName -> allowedRoles.get(userRoleName).contains(roleName));
    }

}
