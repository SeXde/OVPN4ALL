package com.aberdote.OVPN4ALL.common.constanst;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class RoleConstants {

    public final static String ROLE_USER = "ROLE_USER";
    public final static String ROLE_ADMIN = "ROLE_ADMIN";
    public final static Set<String> ROLES = new HashSet<>(Arrays.asList(ROLE_USER, ROLE_ADMIN));

}
