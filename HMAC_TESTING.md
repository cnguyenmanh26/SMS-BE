# HMAC Authentication Testing Guide

## Overview
HMAC authentication has been implemented following AWS S3 and Stripe signature standards with:
- **Canonical string signing**
- **Replay attack prevention** (nonce tracking)
- **Timestamp validation** (5-minute tolerance)
- **SHA-256 body checksums**

## Step 1: Create API Credentials

**Login as Admin:**
```bash
POST http://localhost:8080/api/auth/login
Content-Type: application/json

{
  "username": "admin",
  "password": "admin123"
}
```

**Create API Credential:**
```bash
POST http://localhost:8080/api/credentials
Authorization: Bearer YOUR_ADMIN_JWT_TOKEN
Content-Type: application/json

{
  "description": "Test API Credential"
}
```

**Response:**
```json
{
  "success": true,
  "message": "API credential created successfully",
  "data": {
    "id": 1,
    "appId": "app_abc123...",
    "secretKey": "def456ghi789...",  // ⚠️ SAVE THIS! Only shown once
    "description": "Test API Credential",
    "enabled": true,
    "createdAt": "2025-12-05T11:00:00"
  }
}
```

**⚠️ IMPORTANT:** Save the `secretKey` immediately - it will never be shown again!

## Step 2: Generate HMAC Signature

### Using Node.js Script

```bash
cd sms-backend/scripts
node generate-hmac.js
```

**Update the script with your credentials:**
```javascript
const APP_ID = 'app_abc123...';  // From step 1
const SECRET_KEY = 'def456ghi789...';  // From step 1
```

### Manual Calculation

**1. Calculate Body Checksum (SHA-256):**
```
body = ""  // Empty for GET requests
bodyChecksum = sha256(body) = "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855"
```

**2. Create Canonical String:**
```
GET
/api/students
1733385600000
550e8400-e29b-41d4-a716-446655440000
e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855
```

**3. Generate HMAC-SHA256 Signature:**
```
signature = hmac_sha256(canonicalString, secretKey)
```

## Step 3: Make HMAC Request

### Using cURL

```bash
curl -X GET "http://localhost:8080/api/students" \
  -H "X-App-ID: app_abc123..." \
  -H "X-App-Timestamp: 1733385600000" \
  -H "X-App-Nonce: 550e8400-e29b-41d4-a716-446655440000" \
  -H "Authorization: HMAC-SHA256 a1b2c3d4e5f6..."
```

### Using Postman

**Headers:**
- `X-App-ID`: `app_abc123...`
- `X-App-Timestamp`: `1733385600000` (current Unix timestamp in milliseconds)
- `X-App-Nonce`: `550e8400-e29b-41d4-a716-446655440000` (UUID v4)
- `Authorization`: `HMAC-SHA256 a1b2c3d4e5f6...`

**Pre-request Script (Postman):**
```javascript
const CryptoJS = require('crypto-js');

const APP_ID = 'app_abc123...';
const SECRET_KEY = 'def456ghi789...';

const method = pm.request.method;
const uri = pm.request.url.getPath();
const timestamp = Date.now().toString();
const nonce = pm.variables.replaceIn('{{$guid}}');
const body = pm.request.body.raw || '';

// Calculate body checksum
const bodyChecksum = CryptoJS.SHA256(body).toString();

// Create canonical string
const canonicalString = `${method}\n${uri}\n${timestamp}\n${nonce}\n${bodyChecksum}`;

// Generate signature
const signature = CryptoJS.HmacSHA256(canonicalString, SECRET_KEY).toString();

// Set headers
pm.request.headers.add({key: 'X-App-ID', value: APP_ID});
pm.request.headers.add({key: 'X-App-Timestamp', value: timestamp});
pm.request.headers.add({key: 'X-App-Nonce', value: nonce});
pm.request.headers.add({key: 'Authorization', value: `HMAC-SHA256 ${signature}`});
```

## Step 4: Enable HMAC Authentication

**Update `application.properties`:**
```properties
hmac.auth.enabled=true
hmac.timestamp.tolerance=300000
```

**Restart backend:**
```bash
.\mvnw.cmd spring-boot:run
```

## Security Features

### 1. Replay Attack Prevention
- Each nonce can only be used once
- Nonces are tracked in `NonceCache`
- Reusing a nonce returns 401 Unauthorized

### 2. Timestamp Validation
- Requests must be within 5 minutes (configurable)
- Prevents old requests from being replayed
- Protects against time-based attacks

### 3. Request Integrity
- Body checksum ensures request wasn't tampered with
- Signature covers method, URI, timestamp, nonce, and body
- Any modification invalidates the signature

## API Credential Management

### List All Credentials
```bash
GET http://localhost:8080/api/credentials
Authorization: Bearer ADMIN_JWT_TOKEN
```

### Delete Credential
```bash
DELETE http://localhost:8080/api/credentials/{id}
Authorization: Bearer ADMIN_JWT_TOKEN
```

### Toggle Credential (Enable/Disable)
```bash
PUT http://localhost:8080/api/credentials/{id}/toggle
Authorization: Bearer ADMIN_JWT_TOKEN
```

## Testing Scenarios

### ✅ Valid Request
- All headers present
- Valid signature
- Fresh timestamp
- Unused nonce
→ **200 OK**

### ❌ Invalid Signature
- Wrong secret key
- Modified request
→ **401 Unauthorized: Invalid signature**

### ❌ Replay Attack
- Reused nonce
→ **401 Unauthorized: Nonce already used**

### ❌ Expired Timestamp
- Timestamp > 5 minutes old
→ **401 Unauthorized: Invalid timestamp**

### ❌ Invalid App-ID
- Non-existent App-ID
- Disabled credential
→ **401 Unauthorized: Invalid App-ID**

## Production Considerations

1. **Secret Key Storage**: Store in environment variables, not in code
2. **Nonce Cache**: Use Redis instead of in-memory cache for distributed systems
3. **HTTPS Only**: Always use HTTPS in production
4. **Rate Limiting**: Implement rate limiting per App-ID
5. **Audit Logging**: Log all HMAC authentication attempts
6. **Key Rotation**: Implement secret key rotation policy

## Troubleshooting

### "Invalid signature"
- Check canonical string format
- Verify secret key is correct
- Ensure body checksum matches

### "Nonce already used"
- Generate new UUID for each request
- Don't reuse nonces

### "Invalid timestamp"
- Sync system clocks
- Check timestamp is in milliseconds
- Ensure within 5-minute window

### "Invalid App-ID"
- Verify App-ID exists
- Check credential is enabled
- Confirm not deleted
