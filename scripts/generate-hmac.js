const crypto = require('crypto');

/**
 * Generate HMAC-SHA256 signature for API authentication
 * Following AWS S3 / Stripe signature standard
 */
function generateHmacSignature(method, uri, timestamp, nonce, body, secretKey) {
    // Calculate body checksum (SHA256)
    const bodyChecksum = crypto
        .createHash('sha256')
        .update(body || '')
        .digest('hex');

    // Create canonical string
    const canonicalString = `${method}\n${uri}\n${timestamp}\n${nonce}\n${bodyChecksum}`;

    // Generate HMAC-SHA256 signature
    const signature = crypto
        .createHmac('sha256', secretKey)
        .update(canonicalString)
        .digest('hex');

    return {
        bodyChecksum,
        canonicalString,
        signature,
        timestamp,
        nonce
    };
}

/**
 * Test HMAC authentication
 */
function testHmacAuth() {
    // Configuration
    const APP_ID = 'test-app-id';  // Replace with actual App-ID
    const SECRET_KEY = 'test-secret-key';  // Replace with actual Secret Key
    const BASE_URL = 'http://localhost:8080';

    // Request details
    const method = 'GET';
    const uri = '/api/students';
    const timestamp = Date.now().toString();
    const nonce = crypto.randomUUID();
    const body = '';  // Empty for GET requests

    // Generate signature
    const result = generateHmacSignature(method, uri, timestamp, nonce, body, SECRET_KEY);

    console.log('=== HMAC Authentication Test ===\n');
    console.log('App-ID:', APP_ID);
    console.log('Timestamp:', timestamp);
    console.log('Nonce:', nonce);
    console.log('Body Checksum:', result.bodyChecksum);
    console.log('\nCanonical String:');
    console.log(result.canonicalString);
    console.log('\nSignature:', result.signature);

    console.log('\n=== cURL Command ===\n');
    console.log(`curl -X ${method} "${BASE_URL}${uri}" \\`);
    console.log(`  -H "X-App-ID: ${APP_ID}" \\`);
    console.log(`  -H "X-App-Timestamp: ${timestamp}" \\`);
    console.log(`  -H "X-App-Nonce: ${nonce}" \\`);
    console.log(`  -H "Authorization: HMAC-SHA256 ${result.signature}"`);

    console.log('\n=== PowerShell Command ===\n');
    console.log(`curl -X ${method} "${BASE_URL}${uri}" \``);
    console.log(`  -H "X-App-ID: ${APP_ID}" \``);
    console.log(`  -H "X-App-Timestamp: ${timestamp}" \``);
    console.log(`  -H "X-App-Nonce: ${nonce}" \``);
    console.log(`  -H "Authorization: HMAC-SHA256 ${result.signature}"`);
}

/**
 * Test with POST request (with body)
 */
function testHmacAuthWithBody() {
    const APP_ID = 'test-app-id';
    const SECRET_KEY = 'test-secret-key';
    const BASE_URL = 'http://localhost:8080';

    const method = 'POST';
    const uri = '/api/students';
    const timestamp = Date.now().toString();
    const nonce = crypto.randomUUID();
    const body = JSON.stringify({
        studentCode: 'SV999',
        fullName: 'Test Student',
        email: 'test@example.com'
    });

    const result = generateHmacSignature(method, uri, timestamp, nonce, body, SECRET_KEY);

    console.log('\n=== HMAC POST Request Test ===\n');
    console.log('Body:', body);
    console.log('Body Checksum:', result.bodyChecksum);
    console.log('Signature:', result.signature);

    console.log('\n=== cURL Command ===\n');
    console.log(`curl -X ${method} "${BASE_URL}${uri}" \\`);
    console.log(`  -H "Content-Type: application/json" \\`);
    console.log(`  -H "X-App-ID: ${APP_ID}" \\`);
    console.log(`  -H "X-App-Timestamp: ${timestamp}" \\`);
    console.log(`  -H "X-App-Nonce: ${nonce}" \\`);
    console.log(`  -H "Authorization: HMAC-SHA256 ${result.signature}" \\`);
    console.log(`  -d '${body}'`);
}

// Run tests
console.log('HMAC Signature Generator for SMS Backend\n');
console.log('NOTE: HMAC authentication is not yet implemented in the backend.');
console.log('This script generates the correct signature format for testing.\n');

testHmacAuth();
testHmacAuthWithBody();

// Export for use in other scripts
module.exports = { generateHmacSignature };
