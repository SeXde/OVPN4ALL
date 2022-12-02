package com.aberdote.OVPN4ALL.utils.validator.config;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ConfigValidatorTest {

    private static List<String> publicIps;
    private static List<String> privateIps;
    private static List<String> validPorts;
    private static List<String> wordList;
    private static List<String> fqdns;

    @BeforeAll
    static void setUp() throws IOException {
        publicIps = Files.readAllLines(Path.of("src/test/resources/publicIpList.txt"));
        privateIps = Files.readAllLines(Path.of("src/test/resources/privateIpList.txt"));
        validPorts = Files.readAllLines(Path.of("src/test/resources/validPorts.txt"));
        wordList = Files.readAllLines(Path.of("src/test/resources/wordList.txt"));
        fqdns = Files.readAllLines(Path.of("src/test/resources/fqdn.txt"));
    }

    @DisplayName("Test valid public ip")
    @ParameterizedTest
    @MethodSource("testPublicIps")
    void validatePublicIp(String publicIp) {
        assertTrue(ConfigValidator.validatePublicIp(publicIp));
    }

    @DisplayName("Test invalid public ip")
    @ParameterizedTest
    @MethodSource("testPrivateIps")
    void validatePublicIpNotValid(String publicIp) {
        assertFalse(ConfigValidator.validatePublicIp(publicIp));
    }

    @DisplayName("Test valid private ip")
    @ParameterizedTest
    @MethodSource("testPrivateIps")
    void validatePrivateIp(String privateIp) {
        assertTrue(ConfigValidator.validatePrivateIp(privateIp));
    }

    @DisplayName("Test valid ports")
    @ParameterizedTest
    @MethodSource("testValidPorts")
    void validatePort(String port) {
        assertTrue(ConfigValidator.validatePort(port));
    }

    @DisplayName("Test wordlist with public ip validator")
    @ParameterizedTest
    @MethodSource("testWordList")
    void validateWordListPublicIp(String publicIp) {
        assertFalse(ConfigValidator.validatePublicIp(publicIp));
    }

    @DisplayName("Test wordlist with private ip validator")
    @ParameterizedTest
    @MethodSource("testWordList")
    void validateWordListPrivateIp(String privateIp) {
        assertFalse(ConfigValidator.validatePrivateIp(privateIp));
    }

    @DisplayName("Test valid netmask")
    @ParameterizedTest
    @ValueSource(strings = {"255.255.0.0", "255.255.255.0", "255.255.192.0"})
    void validateNetmask(String netmask) {
        assertTrue(ConfigValidator.validateNetmask(netmask));
    }

    @DisplayName("Test wordlist with netmask validator")
    @ParameterizedTest
    @MethodSource("testWordList")
    void validateWordListNetmask(String netmask) {
        assertFalse(ConfigValidator.validateNetmask(netmask));
    }

    @DisplayName("Test valid fqdn")
    @ParameterizedTest
    @MethodSource("testFqdns")
    void validateFQDN(String fqdn) {
        assertTrue(ConfigValidator.validateFQDN(fqdn));
    }

    @DisplayName("Test wordlist with fqdn valdator")
    @ParameterizedTest
    @MethodSource("testWordList")
    void validateWordListFQDN(String fqdn) {
        assertFalse(ConfigValidator.validateFQDN(fqdn));
    }


    static Stream<String> testPublicIps() {
        return publicIps.stream();
    }
    static Stream<String> testPrivateIps() {
        return privateIps.stream();
    }
    static Stream<String> testValidPorts() {
        return validPorts.stream();
    }
    static Stream<String> testWordList() {
        return wordList.stream();
    }
    static Stream<String> testFqdns() {
        return fqdns.stream();
    }

}
