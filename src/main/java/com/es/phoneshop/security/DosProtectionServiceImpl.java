package com.es.phoneshop.security;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DosProtectionServiceImpl implements DosProtectionService {

    private static final long THRESHOLD = 20;
    private static final long MINUTE_IN_MILLISECONDS = 60000;
    private static DosProtectionServiceImpl instance;

    private final Map<String, Long> countMap = new ConcurrentHashMap<>();
    private final Map<String, Long> timestampMap = new ConcurrentHashMap<>();

    private DosProtectionServiceImpl() {

    }

    public static synchronized DosProtectionServiceImpl getInstance() {
        if (instance == null) {
            instance = new DosProtectionServiceImpl();
        }
        return instance;
    }

    @Override
    public boolean isAllowed(String ip) {
        Long count = countMap.get(ip);
        if (count == null) {
            count = 1L;
        } else {
            count++;
        }

        countMap.put(ip, count);

        Long currentTimestamp = Instant.now().toEpochMilli();
        Long lastTimestamp = timestampMap.get(ip);

        if (lastTimestamp != null && currentTimestamp - lastTimestamp <= MINUTE_IN_MILLISECONDS) {
            return count <= THRESHOLD;
        } else {
            timestampMap.put(ip, currentTimestamp);
        }

        return true;
    }
}
