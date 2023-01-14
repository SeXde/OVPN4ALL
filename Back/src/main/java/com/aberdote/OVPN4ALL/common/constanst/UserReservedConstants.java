package com.aberdote.OVPN4ALL.common.constanst;

import java.util.HashSet;
import java.util.Set;

public class UserReservedConstants {

    public final static Set<String> PASSWORD_FORBIDDEN = new HashSet<>(Set.of("'"));
    public final static String MAX_USERS_PER_PAGE = "10";

}
