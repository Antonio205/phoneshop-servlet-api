package com.es.phoneshop.security;

import java.util.stream.IntStream;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class DosProtectionServiceImplTest {

    private DosProtectionService dosProtectionService;

    @Before
    public void setup() {
        dosProtectionService = DosProtectionServiceImpl.getInstance();
    }

    @Test
    public void givenCorrectCountNumber_whenIsAllowed_thenReturnTrue() {
        String ip = "127.0.0.2";

        simulateRequests(ip, 5);
        boolean result = dosProtectionService.isAllowed(ip);

        assertTrue(result);
    }

    @Test
    public void givenIp_whenIsAllowed_thenReturnTrue() {
        String ip = "127.0.0.1";

        boolean result = dosProtectionService.isAllowed(ip);

        assertTrue(result);
    }

    @Test
    public void givenIncorrectCountNumber_whenIsAllowed_thenReturnFalse() {
        String ip = "127.0.0.3";

        simulateRequests(ip, 21);
        boolean result = dosProtectionService.isAllowed(ip);

        assertFalse(result);
    }

    private void simulateRequests(String ip, int count) {
        IntStream.range(0, count)
                .forEach(i -> dosProtectionService.isAllowed(ip));
    }

}

