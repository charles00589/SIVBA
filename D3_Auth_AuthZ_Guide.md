# Deliverable 3: Authentication & Authorization Configuration Guide
## SIVBA — Vulnerable Bank Mobile App

**Project**: SIVBA Security Hardening  
**Document**: Authentication & Authorization Configuration Guide  
**Version**: 1.0  
**Date**: March 2026

---

## 1. Executive Summary

The SIVBA application's current authentication and authorization system is critically flawed:
- Admin privileges are determined **client-side** (`username === 'admin'`)
- JWT tokens use a **hardcoded symmetric secret** (`"secret123"`)
- There is **no multi-factor authentication**
- There is **no biometric authentication**
- There is **no formal RBAC** enforced server-side

This guide defines and configures a hardened authentication system incorporating **MFA (TOTP)**, **biometric authentication**, **RS256 JWT**, and a server-enforced **Role-Based Access Control (RBAC)** model to replace the current broken implementation.

---

## 2. Current Authentication Weaknesses

| Issue | Location | Impact |
|---|---|---|
| Client-side admin check | `AuthContext.tsx:76` `username === 'admin'` | Any user named "admin" gets full admin access |
| JWT stored unencrypted | `AsyncStorage` | Stolen token = full account access |
| Hardcoded JWT secret | `app.py:41` `"secret123"` | Attacker can forge any JWT |
| No MFA | Entire app | Account takeover via stolen credentials |
| No token expiry enforcement | Backend | Stolen tokens valid indefinitely |
| SQL injection in login | `app.py:155` | Auth bypass without any credentials |
| No lockout on failed logins | `/login` route | Brute-force attack possible |

---

## 3. Hardened Authentication Architecture

```
User Opens App
      │
      ▼
 ┌──────────────────────────────────────────────┐
 │           Step 1: Biometric Check            │
 │   react-native-biometrics → Android Keystore │
 │   (Fingerprint / Face ID)                    │
 │   ── fails → prompt PIN (device-level) ──    │
 └──────────────────────┬───────────────────────┘
                        │ Passes
                        ▼
 ┌──────────────────────────────────────────────┐
 │         Step 2: Username + Password          │
 │   POST /login → Flask validates vs Argon2id  │
 │   hash in DB                                 │
 └──────────────────────┬───────────────────────┘
                        │ Valid credentials
                        ▼
 ┌──────────────────────────────────────────────┐
 │           Step 3: TOTP MFA                   │
 │   User enters 6-digit code from             │
 │   authenticator app (Google Auth / Authy)    │
 │   Flask verifies via pyotp                   │
 └──────────────────────┬───────────────────────┘
                        │ Valid OTP
                        ▼
 ┌──────────────────────────────────────────────┐
 │ Server issues: RS256 JWT (15-min access)      │
 │              + Refresh token (7-day)          │
 │ Mobile stores in EncryptedStorage (AES-256)  │
 └──────────────────────────────────────────────┘
```

---

## 4. Multi-Factor Authentication (TOTP)

### 4.1 How TOTP Works

Time-based One-Time Password (TOTP) per RFC 6238:
1. During setup, server generates a **shared secret** per user
2. Secret is encoded as a QR code → user scans with Authenticator app
3. On login, app displays a 6-digit code that rotates every 30 seconds
4. Server independently computes the expected code using the same secret + current time

### 4.2 Backend — TOTP Setup (Flask + pyotp)

```bash
pip install pyotp qrcode[pil]
```

```python
# mfa.py
import pyotp, qrcode, base64, io

def generate_totp_secret() -> str:
    """Generate a new TOTP secret for a user."""
    return pyotp.random_base32()

def get_totp_uri(secret: str, username: str) -> str:
    """Generate the otpauth:// URI for QR code scanning."""
    totp = pyotp.TOTP(secret)
    return totp.provisioning_uri(name=username, issuer_name="VulnerableBank")

def verify_totp(secret: str, code: str) -> bool:
    """Verify a user-supplied TOTP code."""
    totp = pyotp.TOTP(secret)
    # valid_window=1 allows ±30 seconds for clock drift
    return totp.verify(code, valid_window=1)

def generate_qr_code_base64(uri: str) -> str:
    """Return QR code as base64-encoded PNG for frontend display."""
    img = qrcode.make(uri)
    buffer = io.BytesIO()
    img.save(buffer, format='PNG')
    return base64.b64encode(buffer.getvalue()).decode()
```

**MFA Enrollment Endpoint**:
```python
@app.route('/api/mfa/setup', methods=['POST'])
@token_required
def mfa_setup(current_user):
    from mfa import generate_totp_secret, get_totp_uri, generate_qr_code_base64
    
    secret = generate_totp_secret()
    uri = get_totp_uri(secret, current_user['username'])
    
    # Store secret (encrypted with field-level AES-256)
    execute_query(
        "UPDATE users SET totp_secret = %s WHERE id = %s",
        (encrypt_field(secret), current_user['user_id']),
        fetch=False
    )
    
    return jsonify({
        'status': 'success',
        'qr_code': generate_qr_code_base64(uri),
        'manual_entry_key': secret  # Show once for manual entry
    })

@app.route('/api/mfa/verify', methods=['POST'])
@token_required
def mfa_verify(current_user):
    from mfa import verify_totp
    
    data = request.get_json()
    code = data.get('code', '')
    
    encrypted_secret = execute_query(
        "SELECT totp_secret FROM users WHERE id = %s",
        (current_user['user_id'],)
    )[0][0]
    secret = decrypt_field(encrypted_secret)
    
    if verify_totp(secret, code):
        # Issue full JWT with mfa_verified=True
        token = generate_token(current_user['user_id'], current_user['username'], current_user['is_admin'], mfa=True)
        return jsonify({'status': 'success', 'token': token})
    
    return jsonify({'status': 'error', 'message': 'Invalid MFA code'}), 401
```

**Updated Login Flow**:
```python
@app.route('/login', methods=['POST'])
@limiter.limit("5 per minute")  # Rate limiting
def login():
    data = request.get_json()
    username = data.get('username', '').strip()
    password = data.get('password', '')

    # Parameterized query — no SQL injection
    user = execute_query(
        "SELECT * FROM users WHERE username = %s",
        (username,)
    )

    if user and verify_password(user[0][2], password):
        if user[0]['totp_secret']:
            # MFA required — issue partial token
            partial_token = generate_partial_token(user[0][0], user[0][1])
            return jsonify({
                'status': 'mfa_required',
                'partial_token': partial_token,
                'message': 'Enter your authenticator code'
            })
        else:
            # No MFA enrolled — issue full token (prompt enrollment)
            token = generate_token(user[0][0], user[0][1], user[0][5])
            return jsonify({'status': 'success', 'token': token})

    # Generic error — no username enumeration
    return jsonify({'status': 'error', 'message': 'Invalid credentials'}), 401
```

### 4.3 Frontend — TOTP Input Screen (React Native)

```typescript
// MFAScreen.tsx
import React, { useState } from 'react';
import { View, Text, TextInput, Alert } from 'react-native';
import { post, ENDPOINTS } from '../utils/api';

const MFAScreen = ({ partialToken, onSuccess }) => {
  const [code, setCode] = useState('');

  const handleVerify = async () => {
    if (code.length !== 6) {
      Alert.alert('Error', 'Please enter a 6-digit code');
      return;
    }

    const response = await post(ENDPOINTS.mfa.verify, { code }, partialToken);
    if (response.ok) {
      await secureSet('jwt_token', response.data.token);
      onSuccess();
    } else {
      Alert.alert('Invalid Code', 'Please try again');
      setCode('');
    }
  };

  return (
    <View>
      <Text>Enter your 6-digit authenticator code</Text>
      <TextInput
        value={code}
        onChangeText={setCode}
        keyboardType="numeric"
        maxLength={6}
        secureTextEntry
      />
      <Button title="Verify" onPress={handleVerify} />
    </View>
  );
};
```

---

## 5. Biometric Authentication

### 5.1 Biometric Auth Flow

Biometric authentication provides a **device-level gate** before the network login. It confirms the person holding the device is the enrolled owner — without sending biometric data to the server.

```
App Launch → Biometric Prompt (Android BiometricPrompt)
                    │
         ┌──────────┴──────────┐
         │                     │
    Fingerprint OK         Face ID OK
         └──────────┬──────────┘
                    │
          Retrieve encrypted JWT
          from EncryptedStorage
                    │
          If JWT valid & not expired →
          Authenticate silently
                    │
          If JWT expired →
          Prompt re-login (user+pass+TOTP)
```

### 5.2 React Native Implementation

```bash
npm install react-native-biometrics
```

```typescript
// src/utils/biometrics.ts
import ReactNativeBiometrics, { BiometryTypes } from 'react-native-biometrics';

const rnBiometrics = new ReactNativeBiometrics();

export const isBiometricsAvailable = async (): Promise<boolean> => {
  const { available, biometryType } = await rnBiometrics.isSensorAvailable();
  return available && (
    biometryType === BiometryTypes.Biometrics ||
    biometryType === BiometryTypes.FaceID ||
    biometryType === BiometryTypes.TouchID
  );
};

export const authenticateWithBiometrics = async (): Promise<boolean> => {
  try {
    const { success } = await rnBiometrics.simplePrompt({
      promptMessage: 'Verify your identity to access VulnerableBank',
      cancelButtonText: 'Use Password',
      fallbackPromptMessage: 'Use device PIN'
    });
    return success;
  } catch {
    return false;
  }
};
```

**Updated App.tsx** — biometric gate on startup:
```typescript
const checkBiometricAndLoad = async () => {
  const hasBiometrics = await isBiometricsAvailable();
  
  if (hasBiometrics) {
    const authenticated = await authenticateWithBiometrics();
    if (!authenticated) {
      // User cancelled → show login screen
      navigateTo('login');
      return;
    }
  }
  
  // Biometric passed (or not available) — try to load stored session
  const token = await secureGet('jwt_token');
  if (token && isTokenValid(token)) {
    navigateTo('dashboard');
  } else {
    navigateTo('login');
  }
};
```

---

## 6. Role-Based Access Control (RBAC)

### 6.1 Role Definitions

| Role | ID | Description | Default Users |
|---|---|---|---|
| `user` | 1 | Standard bank customer | All registered accounts |
| `admin` | 2 | Bank staff — manage users and loans | Created by super_admin only |
| `super_admin` | 3 | Full system access | Pre-seeded; max 2 accounts |

### 6.2 Permission Matrix

| Feature / Endpoint | `user` | `admin` | `super_admin` |
|---|---|---|---|
| View own balance | ✅ | ✅ | ✅ |
| View **any** balance | ❌ | ✅ | ✅ |
| Transfer funds | ✅ | ✅ | ✅ |
| Request loan | ✅ | ✅ | ✅ |
| Approve loan | ❌ | ✅ | ✅ |
| View own transactions | ✅ | ✅ | ✅ |
| View any transactions | ❌ | ✅ | ✅ |
| View all users | ❌ | ✅ | ✅ |
| Delete user account | ❌ | ✅ | ✅ |
| Create admin account | ❌ | ❌ | ✅ |
| Delete admin account | ❌ | ❌ | ✅ |
| View audit logs | ❌ | ✅ (own actions) | ✅ (all) |
| System configuration | ❌ | ❌ | ✅ |

### 6.3 Server-Side RBAC Implementation

**The client-side admin check must be fully removed. All authorization must happen server-side.**

```python
# decorators.py
from functools import wraps
from flask import jsonify, g

def role_required(*allowed_roles: str):
    """Decorator to enforce RBAC on a route."""
    def decorator(f):
        @wraps(f)
        def decorated_function(*args, **kwargs):
            current_user = g.current_user  # Set by @token_required
            user_role = current_user.get('role', 'user')
            
            if user_role not in allowed_roles:
                return jsonify({
                    'status': 'error',
                    'message': 'Insufficient permissions'
                }), 403
            
            return f(*args, **kwargs)
        return decorated_function
    return decorator
```

**Apply to all admin routes**:
```python
from decorators import role_required

@app.route('/admin/users')
@token_required
@role_required('admin', 'super_admin')
def list_users(current_user):
    # Only admin/super_admin reach here
    users = execute_query("SELECT id, username, account_number, balance, role FROM users")
    return jsonify({'users': [...]})

@app.route('/admin/create_admin', methods=['POST'])
@token_required
@role_required('super_admin')  # Only super_admin can create admins
def create_admin(current_user):
    ...
```

**Remove client-side admin check** from `AuthContext.tsx`:
```typescript
// REMOVE THIS (current vulnerable code):
// const isAdmin = username.toLowerCase() === 'admin';

// REPLACE WITH: Read role from JWT payload
import { jwtDecode } from 'jwt-decode';

const decoded = jwtDecode<{ role: string }>(response.data.token);
const userRole = decoded.role;  // 'user', 'admin', or 'super_admin'

const userData = {
  username,
  accountNumber: response.data.accountNumber ?? '',
  role: userRole,
  isAdmin: userRole === 'admin' || userRole === 'super_admin',
  token: response.data.token,
};
```

### 6.4 JWT Payload with Role

```python
# auth.py — include role in JWT, not just is_admin boolean
def generate_token(user_id, username, role='user', mfa=False):
    payload = {
        'user_id': user_id,
        'username': username,
        'role': role,          # 'user' | 'admin' | 'super_admin'
        'mfa_verified': mfa,
        'iat': datetime.utcnow(),
        'exp': datetime.utcnow() + timedelta(minutes=15)
    }
    return jwt.encode(payload, PRIVATE_KEY, algorithm='RS256')
```

---

## 7. Account Lockout & Brute-Force Protection

```python
# Using flask-limiter
from flask_limiter import Limiter
from flask_limiter.util import get_remote_address

limiter = Limiter(
    app=app,
    key_func=get_remote_address,
    default_limits=["200 per day", "50 per hour"]
)

@app.route('/login', methods=['POST'])
@limiter.limit("5 per minute")    # 5 login attempts per minute per IP
@limiter.limit("20 per hour")     # 20 attempts per hour
def login():
    ...

@app.route('/api/v2/reset-password', methods=['POST'])
@limiter.limit("3 per 15 minutes")  # Strict PIN attempt limiting
def reset_password():
    ...
```

**Progressive lockout**:
```python
# Track failed login attempts in Redis or DB
def check_lockout(username: str) -> bool:
    """Returns True if the account is locked."""
    result = execute_query(
        "SELECT failed_attempts, locked_until FROM users WHERE username = %s",
        (username,)
    )
    if result and result[0]:
        attempts, locked_until = result[0]
        if locked_until and datetime.utcnow() < locked_until:
            return True  # Account locked
    return False

def record_failed_login(username: str):
    execute_query("""
        UPDATE users SET
            failed_attempts = failed_attempts + 1,
            locked_until = CASE
                WHEN failed_attempts + 1 >= 10 THEN NOW() + INTERVAL '1 hour'
                WHEN failed_attempts + 1 >= 5  THEN NOW() + INTERVAL '15 minutes'
                ELSE NULL
            END
        WHERE username = %s
    """, (username,), fetch=False)

def clear_failed_logins(username: str):
    execute_query(
        "UPDATE users SET failed_attempts = 0, locked_until = NULL WHERE username = %s",
        (username,), fetch=False
    )
```

---

## 8. Token Lifecycle Management

| Token Type | Expiry | Storage | Rotation |
|---|---|---|---|
| Access Token (JWT) | 15 minutes | EncryptedStorage | On every refresh |
| Refresh Token | 7 days | EncryptedStorage | On each use (rolling) |
| MFA Partial Token | 5 minutes | Memory only (not stored) | Single-use |
| Reset PIN | 15 minutes | DB (hashed) | Single-use |

**Refresh Token Flow**:
```python
@app.route('/api/refresh', methods=['POST'])
@token_required_refresh  # Validates refresh token (different from access token)
def refresh_token(current_user):
    # Rotate refresh token (invalidate old, issue new)
    new_refresh = generate_refresh_token(current_user['user_id'])
    new_access  = generate_token(
        current_user['user_id'],
        current_user['username'],
        current_user['role'],
        mfa=current_user.get('mfa_verified', False)
    )
    
    # Invalidate old refresh token in DB
    execute_query(
        "UPDATE refresh_tokens SET revoked=TRUE WHERE token_hash=%s",
        (hash_token(old_refresh),), fetch=False
    )
    
    return jsonify({
        'access_token': new_access,
        'refresh_token': new_refresh
    })
```

---

## 9. Logout & Token Revocation

```python
@app.route('/api/logout', methods=['POST'])
@token_required
def logout(current_user):
    # Add current access token JTI to blocklist (Redis with TTL = token expiry)
    jti = current_user.get('jti')
    redis_client.setex(f"blocklist:{jti}", 900, "revoked")  # 15-min TTL

    # Revoke refresh token
    data = request.get_json()
    if data.get('refresh_token'):
        execute_query(
            "UPDATE refresh_tokens SET revoked=TRUE WHERE token_hash=%s",
            (hash_token(data['refresh_token']),), fetch=False
        )

    return jsonify({'status': 'success', 'message': 'Logged out successfully'})
```

Mobile client on logout:
```typescript
const logout = async () => {
  const token = await secureGet('jwt_token');
  const refreshToken = await secureGet('refresh_token');

  // Notify server to revoke tokens
  await post('/api/logout', { refresh_token: refreshToken }, token);

  // Wipe local encrypted storage
  await secureClear(['jwt_token', 'refresh_token', 'username', 'account_number']);

  navigateTo('login');
};
```

---

## 10. Configuration Summary Checklist

| Control | Status | Implementation |
|---|---|---|
| ✅ MFA (TOTP) | **Configure** | `pyotp` + Google Authenticator |
| ✅ Biometric Auth | **Configure** | `react-native-biometrics` |
| ✅ Server-side RBAC | **Configure** | `@role_required` decorator |
| ✅ Remove client-side admin check | **Fix** | Remove `username === 'admin'` from AuthContext |
| ✅ JWT RS256 | **Configure** | RSA-2048 key pair |
| ✅ 15-min access token expiry | **Configure** | `timedelta(minutes=15)` |
| ✅ Refresh tokens (7-day, rolling) | **Configure** | DB-based revocation |
| ✅ Rate limiting | **Configure** | `flask-limiter` per endpoint |
| ✅ Account lockout | **Configure** | DB-based lockout table |
| ✅ Token revocation on logout | **Configure** | Redis JTI blocklist |

---

*Next: Deliverable 4 — Secure Coding Guidelines*
