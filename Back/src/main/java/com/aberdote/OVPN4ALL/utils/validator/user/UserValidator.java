package com.aberdote.OVPN4ALL.utils.validator.user;

import com.aberdote.OVPN4ALL.common.constanst.RoleConstants;
import com.aberdote.OVPN4ALL.entity.RoleEntity;
import com.aberdote.OVPN4ALL.entity.UserEntity;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

public class UserValidator {

    public static boolean validateEmail(String email) {
        final String emailPattern = "^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$";
        return Pattern.compile(emailPattern).matcher(email).matches();
    }

    public static boolean validateRole(UserEntity user, String roleName) {
        final Map<String, Set<String>> allowedRoles = new HashMap<>();
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
