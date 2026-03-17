# Deliverable 2: Encryption & Data Protection Plan
## SIVBA — Vulnerable Bank Mobile App

**Project**: SIVBA Security Hardening  
**Document**: Encryption & Data Protection Plan  
**Version**: 1.0  
**Date**: March 2026

---

## 1. Executive Summary

The SIVBA application currently transmits and stores sensitive financial data — including passwords, JWT tokens, account numbers, and transaction histories — with **no encryption** at rest and inadequate protection in transit. This plan defines a comprehensive encryption strategy using **TLS 1.3** for data in transit and **AES-256** for data at rest, along with a key management framework to protect the banking application and its users.

---

## 2. Current Encryption Weaknesses

| Data | Current State | Risk |
|---|---|---|
| JWT token (mobile) | Stored in `AsyncStorage` — **plaintext** | Stolen by any app on rooted device |
| Username / account number (mobile) | `AsyncStorage` — **plaintext** | Full account enumeration on device access |
| Passwords (database) | Stored as **plaintext string** | Full credential dump if DB is compromised |
| Flask session secret | Hardcoded `"secret123"` | JWT forgery; session hijacking |
| API traffic | HTTP allowed (`usesCleartextTraffic=true`) | MitM interception |
| Card numbers (database) | Plaintext | Full PAN exposure violates PCI DSS |
| Password reset PIN | Stored **plaintext** in DB, also returned in API response | Account takeover |

---

## 3. Encryption in Transit (TLS 1.3)

### 3.1 Protocol Requirements

All communication between the mobile app and backend API **must** use TLS 1.3. TLS 1.0, 1.1, and 1.2 must be disabled.

| Requirement | Configuration |
|---|---|
| Protocol | TLS 1.3 only |
| Cipher Suites | `TLS_AES_256_GCM_SHA384`, `TLS_CHACHA20_POLY1305_SHA256` |
| Key Exchange | X25519 (ECDHE — Perfect Forward Secrecy) |
| Certificate | Minimum RSA-2048 or ECDSA P-256 from trusted CA |
| HSTS | `max-age=31536000; includeSubDomains; preload` |
| OCSP Stapling | Enabled |
| Cleartext Traffic | **Disabled** in `AndroidManifest.xml` |

### 3.2 Nginx TLS Configuration

```nginx
# /etc/nginx/sites-available/vulnbank
server {
    listen 443 ssl http2;
    server_name vulnbank.org;

    # Certificate
    ssl_certificate     /etc/ssl/certs/vulnbank.crt;
    ssl_certificate_key /etc/ssl/private/vulnbank.key;

    # TLS 1.3 ONLY
    ssl_protocols TLSv1.3;
    ssl_ciphers TLS_AES_256_GCM_SHA384:TLS_CHACHA20_POLY1305_SHA256;
    ssl_prefer_server_ciphers on;

    # Session settings
    ssl_session_cache shared:SSL:10m;
    ssl_session_timeout 1d;
    ssl_session_tickets off;  # Disable for PFS

    # OCSP Stapling
    ssl_stapling on;
    ssl_stapling_verify on;
    resolver 8.8.8.8 8.8.4.4 valid=300s;

    # Security headers
    add_header Strict-Transport-Security "max-age=31536000; includeSubDomains; preload" always;
    add_header X-Frame-Options DENY always;
    add_header X-Content-Type-Options nosniff always;
    add_header X-XSS-Protection "1; mode=block" always;
    add_header Referrer-Policy "no-referrer" always;
}

# Redirect all HTTP to HTTPS
server {
    listen 80;
    server_name vulnbank.org;
    return 301 https://$host$request_uri;
}
```

### 3.3 Android — Disable Cleartext Traffic

**Current (Vulnerable)** `AndroidManifest.xml`:
```xml
<application
    android:usesCleartextTraffic="true"
    ...>
```

**Fixed** `res/xml/network_security_config.xml`:
```xml
<?xml version="1.0" encoding="utf-8"?>
<network-security-config>
    <domain-config cleartextTrafficPermitted="false">
        <domain includeSubdomains="true">vulnbank.org</domain>
        <pin-set expiration="2027-01-01">
            <!-- SHA-256 fingerprint of server's leaf certificate -->
            <pin digest="SHA-256">AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA=</pin>
            <!-- Backup pin (next rotation certificate) -->
            <pin digest="SHA-256">BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB=</pin>
        </pin-set>
    </domain-config>
</network-security-config>
```

**Apply in** `AndroidManifest.xml`:
```xml
<application
    android:networkSecurityConfig="@xml/network_security_config"
    android:usesCleartextTraffic="false"
    ...>
```

### 3.4 Database Connection Encryption

```python
# Current (no TLS)
DATABASE_URL = "postgresql://user:pass@localhost/vulnbank"

# Secure (TLS required)
DATABASE_URL = "postgresql://user:pass@localhost/vulnbank?sslmode=require&sslrootcert=/etc/ssl/certs/ca.crt"
```

---

## 4. Encryption at Rest — Mobile Client

### 4.1 Replace AsyncStorage with Encrypted Storage

**Current (Vulnerable)** — `AuthContext.tsx`:
```typescript
// JWT token stored in plaintext
await AsyncStorage.setItem('jwt_token', response.data.token);
await AsyncStorage.setItem('username', username);
await AsyncStorage.setItem('account_number', userData.accountNumber);
await AsyncStorage.setItem('is_admin', String(isAdmin));
```

**Fixed** — using `react-native-encrypted-storage` (AES-256-CBC backed by Android Keystore):

```bash
# Install
npm install react-native-encrypted-storage
```

```typescript
// src/utils/secureStorage.ts
import EncryptedStorage from 'react-native-encrypted-storage';

export const secureSet = async (key: string, value: string): Promise<void> => {
  await EncryptedStorage.setItem(key, value);
};

export const secureGet = async (key: string): Promise<string | null> => {
  return await EncryptedStorage.getItem(key);
};

export const secureClear = async (keys: string[]): Promise<void> => {
  await Promise.all(keys.map(k => EncryptedStorage.removeItem(k)));
};
```

**Updated** `AuthContext.tsx` login:
```typescript
import { secureSet, secureGet, secureClear } from '../utils/secureStorage';

// On login — store encrypted
await secureSet('jwt_token', response.data.token);
await secureSet('username', username);
await secureSet('account_number', userData.accountNumber);
// Do NOT store is_admin on device — derive from JWT payload server-side

// On load — retrieve encrypted
const token = await secureGet('jwt_token');

// On logout — wipe encrypted
await secureClear(['jwt_token', 'username', 'account_number']);
```

> **Note**: `react-native-encrypted-storage` uses the **Android Keystore System** under the hood, which provides hardware-backed AES-256 encryption tied to the device. Data cannot be read even on rooted devices without the keystore key.

---

## 5. Encryption at Rest — Server Side

### 5.1 Password Hashing (Argon2id)

**Current (Vulnerable)** — `app.py`:
```python
# Passwords stored and compared as PLAINTEXT strings
query = f"SELECT * FROM users WHERE username='{username}' AND password='{password}'"
```

**Fixed** — using `argon2-cffi`:

```bash
pip install argon2-cffi
```

```python
# auth_helpers.py
from argon2 import PasswordHasher
from argon2.exceptions import VerifyMismatchError

ph = PasswordHasher(
    time_cost=2,       # Number of iterations
    memory_cost=65536, # 64 MB
    parallelism=2,     # Threads
    hash_len=32,
    salt_len=16
)

def hash_password(plaintext: str) -> str:
    """Hash a password with Argon2id."""
    return ph.hash(plaintext)

def verify_password(stored_hash: str, plaintext: str) -> bool:
    """Verify a password against its Argon2id hash."""
    try:
        return ph.verify(stored_hash, plaintext)
    except VerifyMismatchError:
        return False
    # Also check if rehash is needed
```

**Updated `register` endpoint**:
```python
from auth_helpers import hash_password

# In /register:
hashed = hash_password(user_data.get('password'))
execute_query(
    "INSERT INTO users (username, password, account_number) VALUES (%s, %s, %s)",
    (user_data.get('username'), hashed, account_number)
)
```

**Updated `login` endpoint**:
```python
from auth_helpers import verify_password

# In /login — parameterized, no f-string:
user = execute_query(
    "SELECT * FROM users WHERE username = %s",
    (username,)
)

if user and verify_password(user[0][2], password):
    token = generate_token(user[0][0], user[0][1], user[0][5])
    # ...
```

### 5.2 AES-256 Field-Level Encryption for PII

Sensitive database fields (account numbers, card numbers, card CVVs) are encrypted with AES-256-GCM before storage.

```bash
pip install cryptography
```

```python
# encryption.py
import os, base64
from cryptography.hazmat.primitives.ciphers.aead import AESGCM

# Key must be 32 bytes (256-bit), stored in environment variable
FIELD_ENCRYPTION_KEY = bytes.fromhex(os.environ['FIELD_ENC_KEY'])  # 64 hex chars

def encrypt_field(plaintext: str) -> str:
    """Encrypt a string field using AES-256-GCM. Returns base64 ciphertext."""
    aesgcm = AESGCM(FIELD_ENCRYPTION_KEY)
    nonce = os.urandom(12)  # 96-bit nonce
    ciphertext = aesgcm.encrypt(nonce, plaintext.encode(), None)
    # Store: base64(nonce + ciphertext)
    return base64.b64encode(nonce + ciphertext).decode()

def decrypt_field(encrypted: str) -> str:
    """Decrypt an AES-256-GCM encrypted field."""
    aesgcm = AESGCM(FIELD_ENCRYPTION_KEY)
    data = base64.b64decode(encrypted)
    nonce, ciphertext = data[:12], data[12:]
    return aesgcm.decrypt(nonce, ciphertext, None).decode()
```

**Usage in card creation**:
```python
from encryption import encrypt_field, decrypt_field

# Encrypt before storing
encrypted_card_number = encrypt_field(generate_card_number())
encrypted_cvv = encrypt_field(generate_cvv())

execute_query(
    "INSERT INTO virtual_cards (user_id, card_number, cvv) VALUES (%s, %s, %s)",
    (user_id, encrypted_card_number, encrypted_cvv)
)

# Decrypt before returning to user
card['card_number'] = decrypt_field(raw_card_number)
```

---

## 6. Flask Secret Key & JWT Signing

### 6.1 Flask Application Secret

**Current (Vulnerable)**:
```python
app.secret_key = "secret123"  # Hardcoded — CWE-798
```

**Fixed**:
```python
import os
import secrets

# In production: generate once and store in .env / secrets manager
# secrets.token_hex(32) → 64-char hex string
app.secret_key = os.environ.get('FLASK_SECRET_KEY')
if not app.secret_key:
    raise RuntimeError("FLASK_SECRET_KEY environment variable not set!")
```

Generate the key:
```bash
python -c "import secrets; print(secrets.token_hex(32))"
# e.g.: a4d8f21bc34567de890a12bc...
```

Store in `.env`:
```
FLASK_SECRET_KEY=a4d8f21bc34567de890a12bc...
```

### 6.2 JWT — Switch from HS256 to RS256

| Property | Current | Hardened |
|---|---|---|
| Algorithm | HS256 (symmetric) | RS256 (asymmetric) |
| Secret | `"secret123"` | 2048-bit RSA private key |
| Expiry | Not enforced | 15 minutes (access), 7 days (refresh) |
| Storage | AsyncStorage plaintext | EncryptedStorage (AES-256) |

```bash
# Generate RSA key pair
openssl genrsa -out jwt_private.pem 2048
openssl rsa -in jwt_private.pem -pubout -out jwt_public.pem
```

```python
# auth.py — RS256 JWT signing
import jwt, os
from datetime import datetime, timedelta

PRIVATE_KEY = open(os.environ['JWT_PRIVATE_KEY_PATH']).read()
PUBLIC_KEY  = open(os.environ['JWT_PUBLIC_KEY_PATH']).read()

def generate_token(user_id, username, is_admin):
    payload = {
        'user_id': user_id,
        'username': username,
        'is_admin': is_admin,
        'iat': datetime.utcnow(),
        'exp': datetime.utcnow() + timedelta(minutes=15)
    }
    return jwt.encode(payload, PRIVATE_KEY, algorithm='RS256')

def verify_token(token):
    return jwt.decode(token, PUBLIC_KEY, algorithms=['RS256'])
```

---

## 7. Key Management Strategy

### 7.1 Key Inventory

| Key | Algorithm | Length | Rotation | Storage |
|---|---|---|---|---|
| Flask secret key | AES | 256-bit | Annually | AWS Secrets Manager |
| JWT signing key | RSA | 2048-bit | Every 6 months | AWS Secrets Manager |
| JWT verification key | RSA public | 2048-bit | With signing key | App config |
| Field encryption key | AES-256-GCM | 256-bit | Annually | AWS Secrets Manager / HashiCorp Vault |
| DB credentials | N/A | N/A | Quarterly | AWS Secrets Manager |
| TLS certificate | RSA/ECDSA | 2048/256-bit | Annually (auto via Let's Encrypt) | Server filesystem |

### 7.2 Key Rotation Procedure

1. **Generate new key** → store in Secrets Manager with a new version
2. **Deploy application** with new key (while retaining old key for in-flight token validation)
3. **Grace period** (24 hours): old key accepted for verification but not signing
4. **Retire old key** after grace period expires
5. **Audit log** rotation event in security log

### 7.3 Never-Do List

- ❌ Never hardcode secrets in source code
- ❌ Never commit `.env` files to version control
- ❌ Never log secrets or tokens
- ❌ Never transmit keys over unencrypted channels
- ❌ Never use symmetric keys for JWT in multi-service architectures

---

## 8. Reset PIN Security

**Current**: 3-digit PIN (100–999), returned in API response plaintext.

**Fixed**:
```python
import secrets
from auth_helpers import hash_password

# Generate 6-digit cryptographically random PIN
reset_pin = str(secrets.randbelow(900000) + 100000)  # 100000–999999

# Hash before storing (same Argon2id approach)
hashed_pin = hash_password(reset_pin)
execute_query(
    "UPDATE users SET reset_pin = %s, reset_pin_expires = %s WHERE username = %s",
    (hashed_pin, datetime.utcnow() + timedelta(minutes=15), username),
    fetch=False
)

# Send via secure email / SMS — NEVER return in API response
send_email(user_email, f"Your reset PIN is: {reset_pin}")

# Response:
return jsonify({'status': 'success', 'message': 'Reset PIN sent to your registered email.'})
# NO debug_info, NO pin in response
```

---

## 9. Encryption Controls Summary

| Control | Standard | Implementation |
|---|---|---|
| TLS 1.3 in transit | PCI DSS Req 4, NIST SP 800-52 | Nginx TLS config |
| Certificate Pinning | OWASP M3 | `network_security_config.xml` |
| Mobile encrypted storage | OWASP M2 | `react-native-encrypted-storage` |
| Password hashing | OWASP Top 10 A02, CWE-916 | Argon2id via `argon2-cffi` |
| PII field encryption | PCI DSS Req 3 | AES-256-GCM |
| JWT RS256 asymmetric | OWASP Session | `PyJWT` RS256 |
| Secret management | CWE-798 | AWS Secrets Manager / `.env` |
| DB connection TLS | PCI DSS Req 4 | PostgreSQL `sslmode=require` |

---

*Next: Deliverable 3 — Authentication & Authorization Configuration Guide*
