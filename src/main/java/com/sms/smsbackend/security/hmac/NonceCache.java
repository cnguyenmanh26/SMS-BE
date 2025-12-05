package com.sms.smsbackend.security.hmac;

import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * In-memory cache for tracking used nonces to prevent replay attacks
 * In production, use Redis or similar distributed cache
 */
@Component
public class NonceCache {
    
    private final Set<String> usedNonces = ConcurrentHashMap.newKeySet();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    
    public NonceCache() {
        // Clean up old nonces every 10 minutes
        scheduler.scheduleAtFixedRate(this::cleanup, 10, 10, TimeUnit.MINUTES);
    }

    /**
     * Check if nonce has been used
     */
    public boolean isNonceUsed(String nonce) {
        return usedNonces.contains(nonce);
    }

    /**
     * Mark nonce as used
     */
    public void markNonceAsUsed(String nonce) {
        usedNonces.add(nonce);
    }

    /**
     * Clean up old nonces (simple implementation)
     * In production, use TTL-based cache like Redis
     */
    private void cleanup() {
        // Keep cache size manageable
        if (usedNonces.size() > 10000) {
            usedNonces.clear();
        }
    }

    /**
     * Shutdown scheduler on bean destruction
     */
    public void shutdown() {
        scheduler.shutdown();
    }
}
