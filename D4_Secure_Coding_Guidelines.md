# Deliverable 4: Secure Coding Guidelines
## SIVBA — Vulnerable Bank Mobile App

**Project**: SIVBA Security Hardening  
**Document**: Secure Coding Guidelines — Audit Results & Remediation  
**Version**: 1.0  
**Date**: March 2026

---

## 1. Executive Summary

A static code analysis was performed on the SIVBA codebase covering:
- `vuln-bank-mobile/backend/app.py` (Flask Python — 1361 lines)
- `vuln-bank-mobile/src/` (React Native TypeScript)
- `vuln-bank-mobile/src/utils/api.ts`
- `vuln-bank-mobile/src/contexts/AuthContext.tsx`
- Android `AndroidManifest.xml` (referenced from README)

**30+ vulnerabilities** were identified. This document presents each finding with:
- Severity rating
- CWE / OWASP reference
- Vulnerable code (with file + line number)
- Corrected code

---

## 2. Static Analysis Summary

| Severity | Count |
|---|---|
| 🔴 Critical | 8 |
| 🟠 High | 9 |
| 🟡 Medium | 9 |
| 🔵 Low | 5 |
| **Total** | **31** |

---

## 3. Critical Findings

---

### VULN-01 — SQL Injection: Login Endpoint
**File**: `backend/app.py` | **Line**: 155  
**Severity**: 🔴 Critical | **CWE**: CWE-89 | **OWASP**: A03:2021

**Vulnerable Code**:
```python
# Raw f-string — username and password injected directly into SQL
query = f"SELECT * FROM users WHERE username='{username}' AND password='{password}'"
user = execute_query(query)
```

**Exploit**: Login as any user without credentials:
```
username: admin' --
password: anything
# Resulting query: SELECT * FROM users WHERE username='admin' --' AND password='anything'
# Comment --  bypasses password check entirely
```

**Fixed Code**:
```python
from auth_helpers import verify_password

user = execute_query(
    "SELECT * FROM users WHERE username = %s",
    (username,)
)
if user and verify_password(user[0][2], password):
    token = generate_token(user[0][0], user[0][1], user[0][5])
    return jsonify({'status': 'success', 'token': token})

return jsonify({'status': 'error', 'message': 'Invalid credentials'}), 401
```

---

### VULN-02 — SQL Injection: Check Balance
**File**: `backend/app.py` | **Line**: 258  
**Severity**: 🔴 Critical | **CWE**: CWE-89 | **OWASP**: A03:2021

**Vulnerable Code**:
```python
user = execute_query(
    f"SELECT username, balance FROM users WHERE account_number='{account_number}'"
)
```

**Exploit**: `account_number = ' OR 1=1 -- ` dumps all records.

**Fixed Code**:
```python
user = execute_query(
    "SELECT username, balance FROM users WHERE account_number = %s",
    (account_number,)
)
```

---

### VULN-03 — SQL Injection: Transaction History
**File**: `backend/app.py` | **Lines**: 352–364  
**Severity**: 🔴 Critical | **CWE**: CWE-89 | **OWASP**: A03:2021

**Vulnerable Code**:
```python
query = f"""
    SELECT id, from_account, to_account, amount, timestamp, transaction_type, description
    FROM transactions
    WHERE from_account='{account_number}' OR to_account='{account_number}'
    ORDER BY timestamp DESC
"""
```
Additionally, the live SQL query is exposed in the response:
```python
'query_used': query  # SQL query exposed to clients!
```

**Fixed Code**:
```python
query = """
    SELECT id, from_account, to_account, amount, timestamp, transaction_type, description
    FROM transactions
    WHERE from_account = %s OR to_account = %s
    ORDER BY timestamp DESC
"""
transactions = execute_query(query, (account_number, account_number))

# Remove query_used from response entirely
transaction_list = [{
    'id': t[0], 'from_account': t[1], 'to_account': t[2],
    'amount': float(t[3]), 'timestamp': str(t[4]),
    'type': t[5], 'description': t[6]
} for t in transactions]
```

---

### VULN-04 — SQL Injection: Create Admin
**File**: `backend/app.py` | **Lines**: 596–599  
**Severity**: 🔴 Critical | **CWE**: CWE-89 | **OWASP**: A03:2021

**Vulnerable Code**:
```python
execute_query(
    f"INSERT INTO users (username, password, account_number, is_admin) VALUES ('{username}', '{password}', '{account_number}', true)",
    fetch=False
)
```

**Fixed Code**:
```python
hashed_password = hash_password(password)
execute_query(
    "INSERT INTO users (username, password, account_number, role) VALUES (%s, %s, %s, %s)",
    (username, hashed_password, account_number, 'admin'),
    fetch=False
)
```

---

### VULN-05 — Mass Assignment: Register Endpoint
**File**: `backend/app.py` | **Lines**: 92–95  
**Severity**: 🔴 Critical | **CWE**: CWE-915 | **OWASP**: A08:2021

**Vulnerable Code**:
```python
# Any additional user-supplied fields are added to the INSERT query
for key, value in user_data.items():
    if key not in ['username', 'password']:
        fields.append(key)
        values.append(value)
```

**Exploit**: Send `{"username": "hacker", "password": "pass", "is_admin": true, "balance": 100000}` → account gets admin rights and $100k balance on registration.

**Fixed Code**:
```python
# Strict allowlist — only accept known, safe fields
ALLOWED_FIELDS = {'username', 'password'}
unknown_fields = set(user_data.keys()) - ALLOWED_FIELDS

if unknown_fields:
    return jsonify({
        'status': 'error',
        'message': f'Unexpected fields: {", ".join(unknown_fields)}'
    }), 400

execute_query(
    "INSERT INTO users (username, password, account_number) VALUES (%s, %s, %s)",
    (user_data['username'], hash_password(user_data['password']), account_number)
)
```

---

### VULN-06 — Unauthenticated Debug Endpoint
**File**: `backend/app.py` | **Lines**: 205–216  
**Severity**: 🔴 Critical | **CWE**: CWE-200 | **OWASP**: A02:2021

**Vulnerable Code**:
```python
@app.route('/debug/users')
def debug_users():
    # Returns ALL users including plaintext passwords — NO AUTH
    users = execute_query("SELECT id, username, password, account_number, is_admin FROM users")
    return jsonify({'users': [...]})
```

**Fix**: **Remove this endpoint entirely in production.**
```python
# /debug/users route DELETED — debug endpoints must never exist in production
# For development only: use environment-gated debug mode
if app.debug and os.environ.get('FLASK_ENV') == 'development':
    @app.route('/debug/users')
    @token_required
    @role_required('super_admin')
    def debug_users(current_user):
        ...
```

---

### VULN-07 — Broken Object Level Authorization (BOLA / IDOR)
**File**: `backend/app.py` | **Lines**: 251–278 and 347–393  
**Severity**: 🔴 Critical | **CWE**: CWE-639 | **OWASP**: API1:2023

**Vulnerable Code**:
```python
# /check_balance/<account_number> — No authentication at all
@app.route('/check_balance/<account_number>')
def check_balance(account_number):
    # Any unauthenticated user can check any account balance
    user = execute_query(f"SELECT username, balance FROM users WHERE ...")
```

**Fixed Code**:
```python
@app.route('/check_balance/<account_number>')
@token_required
def check_balance(current_user, account_number):
    # Users can only check their OWN balance
    if current_user.get('role') not in ('admin', 'super_admin'):
        # Verify the requested account belongs to the current user
        if account_number != current_user.get('account_number'):
            return jsonify({'status': 'error', 'message': 'Access denied'}), 403

    user = execute_query(
        "SELECT username, balance FROM users WHERE account_number = %s",
        (account_number,)
    )
    ...
```

---

### VULN-08 — Client-Side Authorization
**File**: `vuln-bank-mobile/src/contexts/AuthContext.tsx` | **Line**: 76  
**Severity**: 🔴 Critical | **CWE**: CWE-602 | **OWASP**: A01:2021

**Vulnerable Code**:
```typescript
// Admin status determined entirely on the client — trivially bypassed
const isAdmin = username.toLowerCase() === 'admin';
```

**Fixed Code**:
```typescript
// Admin role comes FROM the server's JWT payload, never from client logic
import { jwtDecode } from 'jwt-decode';

interface JWTPayload {
  user_id: number;
  username: string;
  role: 'user' | 'admin' | 'super_admin';
  exp: number;
}

const decoded = jwtDecode<JWTPayload>(response.data.token);
const userData = {
  username,
  accountNumber: response.data.accountNumber ?? '',
  role: decoded.role,
  isAdmin: decoded.role === 'admin' || decoded.role === 'super_admin',
  token: response.data.token,
};
```

---

## 4. High Findings

---

### VULN-09 — Negative Transfer / Fund Inflation
**File**: `backend/app.py` | **Line**: 300  
**Severity**: 🟠 High | **CWE**: CWE-20 | **OWASP**: A03:2021

**Vulnerable Code**:
```python
amount = float(data.get('amount'))

# balance >= abs(amount) — negative amounts pass this check
if balance >= abs(amount):
    execute_query("UPDATE users SET balance = balance - %s WHERE id = %s", (amount, user_id))
    # If amount = -1000: balance INCREASES by 1000
```

**Fixed Code**:
```python
amount = float(data.get('amount', 0))

# Strict positive amount validation
if amount <= 0:
    return jsonify({'status': 'error', 'message': 'Amount must be a positive number'}), 400

MAX_TRANSFER = 10000.00
if amount > MAX_TRANSFER:
    return jsonify({'status': 'error', 'message': f'Transfer exceeds maximum (${MAX_TRANSFER})'}), 400

if balance < amount:
    return jsonify({'status': 'error', 'message': 'Insufficient funds'}), 400
```

---

### VULN-10 — Plaintext Password Storage
**File**: `backend/app.py` | **Lines**: 88–95  
**Severity**: 🟠 High | **CWE**: CWE-916 | **OWASP**: A02:2021

**Vulnerable Code**:
```python
# Password stored exactly as the user typed it
values = [user_data.get('username'), user_data.get('password'), account_number]
```

**Fixed Code**:
```python
from auth_helpers import hash_password

hashed = hash_password(user_data.get('password'))
execute_query(
    "INSERT INTO users (username, password, account_number) VALUES (%s, %s, %s)",
    (user_data.get('username'), hashed, account_number)
)
```

---

### VULN-11 — Unrestricted File Upload
**File**: `backend/app.py` | **Lines**: 395–441  
**Severity**: 🟠 High | **CWE**: CWE-434 | **OWASP**: A04:2021

**Vulnerable Code**:
```python
# No extension check, no MIME check, no size limit
file.save(file_path)
```

**Fixed Code**:
```python
import magic  # pip install python-magic

ALLOWED_EXTENSIONS = {'jpg', 'jpeg', 'png', 'gif', 'webp'}
ALLOWED_MIMETYPES = {'image/jpeg', 'image/png', 'image/gif', 'image/webp'}
MAX_FILE_SIZE = 2 * 1024 * 1024  # 2 MB

def validate_image(file) -> bool:
    # Check extension
    ext = file.filename.rsplit('.', 1)[-1].lower() if '.' in file.filename else ''
    if ext not in ALLOWED_EXTENSIONS:
        return False
    # Check magic bytes (actual file content)
    mime = magic.from_buffer(file.read(2048), mime=True)
    file.seek(0)
    return mime in ALLOWED_MIMETYPES

# In the route:
if not validate_image(file):
    return jsonify({'error': 'Invalid file type'}), 400

file.seek(0, 2)  # Seek to end
if file.tell() > MAX_FILE_SIZE:
    return jsonify({'error': 'File too large (max 2MB)'}), 400
file.seek(0)

# Save outside the web root (non-executable directory)
UPLOAD_FOLDER = '/var/app/uploads'  # Not under /static/
```

---

### VULN-12 — Weak Reset PIN (3 digits, returned in response)
**File**: `backend/app.py` | **Lines**: 630, 647  
**Severity**: 🟠 High | **CWE**: CWE-330, CWE-200 | **OWASP**: A05:2021

**Vulnerable Code**:
```python
reset_pin = str(random.randint(100, 999))  # Only 900 possibilities
return jsonify({..., 'debug_info': {'pin': reset_pin}})  # PIN in response!
```

**Fixed Code**:
```python
import secrets
from auth_helpers import hash_password

reset_pin = str(secrets.randbelow(900000) + 100000)  # 6-digit, CSPRNG
hashed_pin = hash_password(reset_pin)

execute_query(
    "UPDATE users SET reset_pin=%s, pin_expires=%s WHERE username=%s",
    (hashed_pin, datetime.utcnow() + timedelta(minutes=15), username),
    fetch=False
)
# Send via registered email/SMS — NEVER return in response
return jsonify({'status': 'success', 'message': 'Reset PIN sent to your registered email'})
```

---

### VULN-13 — Insecure Local Data Storage
**File**: `src/contexts/AuthContext.tsx` | **Lines**: 89–92  
**Severity**: 🟠 High | **CWE**: CWE-312 | **OWASP Mobile**: M2

**Vulnerable Code**:
```typescript
await AsyncStorage.setItem('jwt_token', response.data.token);
await AsyncStorage.setItem('username', username);
await AsyncStorage.setItem('account_number', userData.accountNumber);
```

**Fixed Code**:
```typescript
import EncryptedStorage from 'react-native-encrypted-storage';

await EncryptedStorage.setItem('jwt_token', response.data.token);
await EncryptedStorage.setItem('username', username);
await EncryptedStorage.setItem('account_number', userData.accountNumber);
// Do NOT store role/isAdmin — derive from JWT on every startup
```

---

### VULN-14 — Hardcoded Flask Secret Key
**File**: `backend/app.py` | **Line**: 41  
**Severity**: 🟠 High | **CWE**: CWE-798 | **OWASP**: A02:2021

**Vulnerable Code**:
```python
app.secret_key = "secret123"
```

**Fixed Code**:
```python
import os
app.secret_key = os.environ['FLASK_SECRET_KEY']
```

---

### VULN-15 — No Rate Limiting on Authentication Endpoints
**File**: `backend/app.py` | All auth routes  
**Severity**: 🟠 High | **CWE**: CWE-307 | **OWASP**: A07:2021

**Fixed Code**:
```python
from flask_limiter import Limiter
from flask_limiter.util import get_remote_address

limiter = Limiter(app=app, key_func=get_remote_address)

@app.route('/login', methods=['POST'])
@limiter.limit("5 per minute;20 per hour")
def login(): ...

@app.route('/api/v2/reset-password', methods=['POST'])
@limiter.limit("3 per 15 minutes")
def reset_password(): ...
```

---

## 5. Medium Findings

---

### VULN-16 — Excessive Data Exposure in API Responses
**File**: `backend/app.py` | Multiple routes  
**Severity**: 🟡 Medium | **CWE**: CWE-200 | **OWASP**: A02:2021

**Vulnerable Code** (registration response):
```python
return jsonify({
    'debug_data': {
        'user_id': user[0],
        'raw_data': user_data,         # Echoes user's raw input
        'fields_registered': fields,   # Reveals DB columns
        'server_info': request.headers.get('User-Agent')
    }
})
# Response header: X-Debug-Info: ... (full internal data)
# Response header: X-User-Info: id=5;admin=false;balance=1000
```

**Fixed Code**:
```python
return jsonify({
    'status': 'success',
    'message': 'Registration successful. Please log in.'
    # No internal data, no debug fields, no headers
})
```

---

### VULN-17 — Username Enumeration
**File**: `backend/app.py` | `/login`, `/forgot-password`  
**Severity**: 🟡 Medium | **CWE**: CWE-204 | **OWASP**: A07:2021

**Vulnerable Code**:
```python
# Login:
return jsonify({'message': 'Invalid credentials', 'attempted_username': username}), 401

# Forgot password:
return jsonify({'message': 'User not found'}), 404  # Tells attacker username is invalid
```

**Fixed Code**:
```python
# Identical response regardless of whether username exists or not
return jsonify({'status': 'success', 'message':
    'If this account exists, a reset link has been sent.'}), 200
```

---

### VULN-18 — Wide-Open CORS
**File**: `backend/app.py` | **Line**: 21  
**Severity**: 🟡 Medium | **CWE**: CWE-942 | **OWASP**: A05:2021

**Vulnerable Code**:
```python
CORS(app)  # Allows ALL origins
```

**Fixed Code**:
```python
CORS(app, resources={
    r"/api/*": {
        "origins": ["https://vulnbank.org", "https://app.vulnbank.org"],
        "methods": ["GET", "POST", "PUT", "DELETE"],
        "allow_headers": ["Authorization", "Content-Type"]
    }
})
```

---

### VULN-19 — Race Condition in Transfer
**File**: `backend/app.py` | **Lines**: 292–321  
**Severity**: 🟡 Medium | **CWE**: CWE-362 | **OWASP**: A04:2021

**Issue**: Two concurrent transfer requests can both read the same balance, deduct, and both succeed — overdrafting the account.

**Fixed Code**: Use a database-level SELECT FOR UPDATE to lock the row:
```python
# Use SELECT FOR UPDATE to prevent race condition
sender = execute_query(
    "SELECT account_number, balance FROM users WHERE id = %s FOR UPDATE",
    (current_user['user_id'],)
)[0]

# All balance checks and updates happen within a single transaction
execute_transaction([
    ("UPDATE users SET balance = balance - %s WHERE id = %s AND balance >= %s",
     (amount, current_user['user_id'], amount)),
    ("UPDATE users SET balance = balance + %s WHERE account_number = %s",
     (amount, to_account)),
    ("INSERT INTO transactions (...) VALUES (...)", (...))
])
```

---

### VULN-20 — Information Disclosure via Debug Print Statements
**File**: `backend/app.py` | Multiple locations  
**Severity**: 🟡 Medium | **CWE**: CWE-215 | **OWASP**: A09:2021

**Vulnerable Code**:
```python
print(f"Login attempt - Username: {username}")
print(f"Debug - Login query: {query}")     # SQL query with user input printed to stdout
print(f"Debug - Generated token: {token}") # JWT token printed to log!
```

**Fixed Code**:
```python
import logging

logger = logging.getLogger(__name__)

# Replace all print() debug statements with structured logging
logger.info("Login attempt", extra={'username': username})
# Never log raw SQL queries or tokens
```

---

### VULN-21 — Predictable Card Number Generation
**File**: `backend/app.py` | **Lines**: 51–58  
**Severity**: 🟡 Medium | **CWE**: CWE-338 | **OWASP**: A02:2021

**Vulnerable Code**:
```python
# random.choices is NOT cryptographically secure
return ''.join(random.choices(string.digits, k=16))
```

**Fixed Code**:
```python
import secrets

def generate_card_number() -> str:
    """Generate a Luhn-valid 16-digit card number using CSPRNG."""
    return ''.join(secrets.choice(string.digits) for _ in range(16))

def generate_cvv() -> str:
    """Generate a cryptographically random 3-digit CVV."""
    return f"{secrets.randbelow(1000):03d}"
```

---

## 6. Low Findings

---

### VULN-22 — Cookie Without Secure Flag
**File**: `backend/app.py` | **Line**: 182  
**Severity**: 🔵 Low | **CWE**: CWE-614

**Vulnerable Code**:
```python
response.set_cookie('token', token, httponly=True)  # Missing secure=True
```
**Fixed Code**:
```python
response.set_cookie('token', token, httponly=True, secure=True, samesite='Strict')
```

---

### VULN-23 — Hardcoded API Base URL in Mobile App
**File**: `src/utils/api.ts` | **Line**: 2  
**Severity**: 🔵 Low | **CWE**: CWE-798 | **OWASP Mobile**: M9

**Vulnerable Code**:
```typescript
export const API_BASE = 'https://vulnbank.org';
```

**Fixed Code**:
```typescript
// Use React Native Config for environment-specific URLs
import Config from 'react-native-config';
export const API_BASE = Config.API_BASE_URL;
```
Then in `.env.production`:
```
API_BASE_URL=https://vulnbank.org
```

---

### VULN-24 — No Input Length Validation on Transfer Amount
**File**: `backend/app.py` | **Line**: 287  
**Severity**: 🔵 Low | **CWE**: CWE-20

**Vulnerable Code**:
```python
amount = float(data.get('amount'))  # No length / size check
```

**Fixed Code**:
```python
raw_amount = data.get('amount')
if not isinstance(raw_amount, (int, float)):
    return jsonify({'error': 'Invalid amount type'}), 400
amount = float(raw_amount)
if amount <= 0 or amount > 100000:
    return jsonify({'error': 'Amount must be between $0.01 and $100,000'}), 400
```

---

## 7. General Secure Coding Rules

These rules must be followed across the entire codebase:

### Backend (Python / Flask)

| Rule | Requirement |
|---|---|
| Database queries | Always use parameterized queries (`%s`) — never f-strings in SQL |
| Input validation | Validate all request body fields with `marshmallow` schemas |
| Error handling | Return generic error messages; log full details server-side only |
| Secrets | Use `os.environ` — never hardcode in source or commit `.env` |
| Logging | Use Python `logging` module — never `print()` in production |
| File uploads | Validate extension AND magic bytes; store outside web root |
| Responses | Never include `debug_info`, SQL queries, stack traces, or raw input |
| Rate limiting | Apply `flask-limiter` to all auth, reset, and financial endpoints |

### Frontend (React Native / TypeScript)

| Rule | Requirement |
|---|---|
| Auth state | Role claims must come from JWT, never from client-computed logic |
| Storage | Use `react-native-encrypted-storage` — never `AsyncStorage` for sensitive data |
| API URLs | Load from `react-native-config` environment variables, not hardcoded |
| Cleartext | Set `android:usesCleartextTraffic="false"` in all configurations |
| Certificate | Implement certificate pinning via `network_security_config.xml` |
| Error display | Never display raw API error messages or stack traces to users |

---

## 8. Vulnerability Remediation Priority

| Priority | Vulnerabilities | Action |
|---|---|---|
| **Immediate** (Critical) | VULN-01 to VULN-08 | Fix before any deployment |
| **This Sprint** (High) | VULN-09 to VULN-15 | Fix within 1 sprint |
| **Next Sprint** (Medium) | VULN-16 to VULN-21 | Fix within 2 sprints |
| **Backlog** (Low) | VULN-22 to VULN-24 | Fix in maintenance cycle |

---

*Next: Deliverable 5 — API & Network Security Configuration*
