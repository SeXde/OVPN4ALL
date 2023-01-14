package com.aberdote.OVPN4ALL.utils.client;


import com.aberdote.OVPN4ALL.client.ManagementInterfaceClient;
import com.aberdote.OVPN4ALL.service.CommandService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
public class ManagementInterfaceClientTest {

    @Mock
    private CommandService commandService;
    private ManagementInterfaceClient managementInterfaceClient;

    @BeforeEach
    public void setUp() throws IOException, InterruptedException {
        managementInterfaceClient = new ManagementInterfaceClient();
        managementInterfaceClient.init();
    }

    @AfterEach
    public void cleanUp() {
        managementInterfaceClient.close();
    }

    @DisplayName("Test kill command")
    @Test
    public void killCommand() {
        assertTrue(managementInterfaceClient.killUser("416c7661726f"));
    }

}
