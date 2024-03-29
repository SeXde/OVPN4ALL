package com.aberdote.OVPN4ALL.integration.argument.provider;

import com.aberdote.OVPN4ALL.dto.RoleDTO;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;

import java.util.stream.Stream;

import static com.aberdote.OVPN4ALL.common.constanst.RoleConstants.ROLE_USER;

public class RegularUserProvider extends GoodUserProvider implements ArgumentsProvider {

    private final static Integer MAX = 20;

    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext) throws Exception {
        return provideUsers(new RoleDTO(ROLE_USER), MAX, true)
                .map(Arguments::of);
    }

}
