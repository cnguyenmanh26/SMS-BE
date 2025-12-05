package com.sms.smsbackend.security.hmac;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.codec.digest.HmacAlgorithms;
import org.apache.commons.codec.digest.HmacUtils;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

/**
 * HMAC Utility for generating and validating HMAC-SHA256 signatures
 * Following AWS S3 and Stripe signature standards
 */
@Component
public class HmacUtility {

    /**
     * Generate canonical string for HMAC signing
     * Format: HTTP_METHOD\nURI_PATH\nTIMESTAMP\nNONCE\nBODY_CHECKSUM
     */
    public String generateCanonicalString(String method, String uri, String timestamp, 
                                         String nonce, String body) {
        String bodyChecksum = calculateBodyChecksum(body);
        return String.format("%s\n%s\n%s\n%s\n%s", 
            method.toUpperCase(), 
            uri, 
            timestamp, 
            nonce, 
            bodyChecksum
        );
    }

    /**
     * Calculate SHA-256 checksum of request body
     */
    public String calculateBodyChecksum(String body) {
        if (body == null || body.isEmpty()) {
            body = "";
        }
        return DigestUtils.sha256Hex(body);
    }

    /**
     * Generate HMAC-SHA256 signature
     */
    public String generateSignature(String canonicalString, String secretKey) {
        HmacUtils hmacUtils = new HmacUtils(HmacAlgorithms.HMAC_SHA_256, secretKey.getBytes(StandardCharsets.UTF_8));
        byte[] signatureBytes = hmacUtils.hmac(canonicalString);
        return bytesToHex(signatureBytes);
    }

    /**
     * Validate HMAC signature
     */
    public boolean validateSignature(String method, String uri, String timestamp, 
                                     String nonce, String body, String signature, 
                                     String secretKey) {
        String canonicalString = generateCanonicalString(method, uri, timestamp, nonce, body);
        String expectedSignature = generateSignature(canonicalString, secretKey);
        return expectedSignature.equalsIgnoreCase(signature);
    }

    /**
     * Validate timestamp (within tolerance window)
     */
    public boolean validateTimestamp(String timestamp, long toleranceMs) {
        try {
            long requestTime = Long.parseLong(timestamp);
            long currentTime = System.currentTimeMillis();
            long diff = Math.abs(currentTime - requestTime);
            return diff <= toleranceMs;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Convert byte array to hex string
     */
    private String bytesToHex(byte[] bytes) {
        StringBuilder result = new StringBuilder();
        for (byte b : bytes) {
            result.append(String.format("%02x", b));
        }
        return result.toString();
    }
}
