# Authentication Testing Guide

## JWT Authentication Testing

### 1. Test Login (Get JWT Token)

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d "{\"username\":\"admin\",\"password\":\"admin123\"}"
```

**Expected Response:**
```json
{
  "success": true,
  "message": "Đăng nhập thành công",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "type": "Bearer",
    "id": 1,
    "username": "admin",
    "email": "admin@sms.com",
    "studentCode": null,
    "roles": ["ROLE_ADMIN"]
  }
}
```

### 2. Test Protected Endpoint (Get Students)

```bash
# Replace YOUR_JWT_TOKEN with the token from login response
curl -X GET http://localhost:8080/api/students \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### 3. Test Register New User

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d "{\"username\":\"testuser\",\"email\":\"test@example.com\",\"password\":\"password123\",\"studentCode\":\"SV001\",\"roles\":[\"USER\"]}"
```

---

## HMAC Authentication Testing

### Overview
HMAC authentication yêu cầu:
- **X-App-ID**: Public identifier (App ID)
- **X-App-Timestamp**: Unix timestamp (milliseconds)
- **X-App-Nonce**: UUID v4 (unique per request)
- **Authorization**: `HMAC-SHA256 <signature>`

### Canonical String Format
```
HTTP_METHOD\n
URI_PATH\n
TIMESTAMP\n
NONCE\n
BODY_CHECKSUM
```

### Example: Generate HMAC Signature

#### Using PowerShell

```powershell
# Function to generate HMAC signature
function Generate-HmacSignature {
    param(
        [string]$method,
        [string]$uri,
        [string]$timestamp,
        [string]$nonce,
        [string]$body,
        [string]$secretKey
    )
    
    # Calculate body checksum (SHA256)
    $bodyBytes = [System.Text.Encoding]::UTF8.GetBytes($body)
    $sha256 = [System.Security.Cryptography.SHA256]::Create()
    $bodyHash = $sha256.ComputeHash($bodyBytes)
    $bodyChecksum = [System.BitConverter]::ToString($bodyHash).Replace("-", "").ToLower()
    
    # Create canonical string
    $canonicalString = "$method`n$uri`n$timestamp`n$nonce`n$bodyChecksum"
    
    # Generate HMAC-SHA256 signature
    $hmac = New-Object System.Security.Cryptography.HMACSHA256
    $hmac.Key = [System.Text.Encoding]::UTF8.GetBytes($secretKey)
    $signatureBytes = $hmac.ComputeHash([System.Text.Encoding]::UTF8.GetBytes($canonicalString))
    $signature = [System.BitConverter]::ToString($signatureBytes).Replace("-", "").ToLower()
    
    return @{
        BodyChecksum = $bodyChecksum
        CanonicalString = $canonicalString
        Signature = $signature
    }
}

# Example usage
$method = "GET"
$uri = "/api/students"
$timestamp = [DateTimeOffset]::UtcNow.ToUnixTimeMilliseconds().ToString()
$nonce = [guid]::NewGuid().ToString()
$body = ""
$secretKey = "your-secret-key-here"

$result = Generate-HmacSignature -method $method -uri $uri -timestamp $timestamp -nonce $nonce -body $body -secretKey $secretKey

Write-Host "Timestamp: $timestamp"
Write-Host "Nonce: $nonce"
Write-Host "Signature: $($result.Signature)"

# Make request
curl -X GET "http://localhost:8080$uri" `
  -H "X-App-ID: your-app-id" `
  -H "X-App-Timestamp: $timestamp" `
  -H "X-App-Nonce: $nonce" `
  -H "Authorization: HMAC-SHA256 $($result.Signature)"
```

#### Using Node.js

```javascript
const crypto = require('crypto');

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
        signature
    };
}

// Example usage
const method = 'GET';
const uri = '/api/students';
const timestamp = Date.now().toString();
const nonce = crypto.randomUUID();
const body = '';
const secretKey = 'your-secret-key-here';

const result = generateHmacSignature(method, uri, timestamp, nonce, body, secretKey);

console.log('Timestamp:', timestamp);
console.log('Nonce:', nonce);
console.log('Signature:', result.signature);

// Example curl command
console.log(`
curl -X ${method} "http://localhost:8080${uri}" \\
  -H "X-App-ID: your-app-id" \\
  -H "X-App-Timestamp: ${timestamp}" \\
  -H "X-App-Nonce: ${nonce}" \\
  -H "Authorization: HMAC-SHA256 ${result.signature}"
`);
```

---

## Testing Steps

### Step 1: Test JWT Authentication (Current)

1. Start backend: `.\mvnw.cmd spring-boot:run`
2. Test login with admin credentials
3. Use JWT token to access protected endpoints

### Step 2: Implement HMAC (Next)

**Note**: HMAC authentication chưa được implement. Cần:
1. Create `HmacUtils.java` - Generate and validate HMAC
2. Create `HmacAuthenticationFilter.java` - Filter for HMAC requests
3. Create `ApiCredential` entity - Store App-ID and Secret Key
4. Create `NonceCache` - Track used nonces (replay attack prevention)
5. Update `SecurityConfig` - Add HMAC filter

### Step 3: Test HMAC Authentication

1. Generate API credentials (App-ID and Secret Key)
2. Use script above to generate signature
3. Send request with HMAC headers

---

## Quick Test Commands

### Test JWT Login
```bash
curl -X POST http://localhost:8080/api/auth/login -H "Content-Type: application/json" -d "{\"username\":\"admin\",\"password\":\"admin123\"}"
```

### Test Protected Endpoint (Replace TOKEN)
```bash
curl -X GET http://localhost:8080/api/students -H "Authorization: Bearer YOUR_TOKEN_HERE"
```

### Test Unauthorized Access
```bash
curl -X GET http://localhost:8080/api/students
# Should return 401 Unauthorized
```
