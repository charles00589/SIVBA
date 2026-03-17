# Deliverable 5: API & Network Security Configuration
## SIVBA — Vulnerable Bank Mobile App

**Project**: SIVBA Security Hardening  
**Document**: API & Network Security Configuration  
**Version**: 1.0  
**Date**: March 2026

---

## 1. Executive Summary

The SIVBA API currently has no rate limiting, no WAF, unrestricted CORS, unauthenticated endpoints, and no firewall rules. This document configures comprehensive API and network security controls including rate limiting per endpoint, a Web Application Firewall (WAF) with OWASP CRS rules, a backend firewall, and an endpoint security matrix.

---

## 2. Current API Security Weaknesses

| Issue | Impact |
|---|---|
| No rate limiting | Brute-force attacks, credential stuffing, DDoS |
| No WAF | SQL injection, XSS, path traversal unblocked |
| Unauthenticated `/debug/users`, `/check_balance`, `/transactions` | Full data exposure without any login |
| Wide-open CORS (`CORS(app)`) | Cross-origin API abuse from any domain |
| No API versioning strategy | Inconsistent security posture across versions |
| Debug endpoints exposed in production | Entire user DB returned with no auth |
| No input size limits | DoS via large request bodies |

---

## 3. API Authentication Hardening

### 3.1 Enforce JWT on All Data Endpoints

```python
# auth.py — token_required decorator
from functools import wraps
from flask import request, jsonify, g
import jwt, os

PUBLIC_KEY = open(os.environ['JWT_PUBLIC_KEY_PATH']).read()

def token_required(f):
    @wraps(f)
    def decorated(*args, **kwargs):
        token = None

        # Extract from Authorization header
        auth_header = request.headers.get('Authorization', '')
        if auth_header.startswith('Bearer '):
            token = auth_header.split(' ')[1]

        if not token:
            return jsonify({'error': 'Token is missing'}), 401

        try:
            payload = jwt.decode(token, PUBLIC_KEY, algorithms=['RS256'])
            
            # Check JTI blocklist (for logged-out tokens)
            if redis_client.exists(f"blocklist:{payload.get('jti')}"):
                return jsonify({'error': 'Token has been revoked'}), 401
            
            g.current_user = payload
        except jwt.ExpiredSignatureError:
            return jsonify({'error': 'Token has expired'}), 401
        except jwt.InvalidTokenError:
            return jsonify({'error': 'Invalid token'}), 401

        return f(*args, **kwargs)
    return decorated
```

### 3.2 Endpoint Authentication Status (Before vs After)

| Endpoint | Current | Hardened |
|---|---|---|
| `POST /login` | ❌ No auth (correct) | ✅ Public |
| `POST /register` | ❌ No auth (correct) | ✅ Public |
| `GET /check_balance/:id` | ❌ **No auth** | ✅ JWT required |
| `GET /transactions/:id` | ❌ **No auth** | ✅ JWT required |
| `POST /transfer` | ✅ JWT | ✅ JWT required |
| `POST /request_loan` | ✅ JWT | ✅ JWT required |
| `GET /debug/users` | ❌ **No auth — DELETE** | 🚫 Route removed |
| `GET /sup3r_s3cr3t_admin` | ✅ JWT (weak) | ✅ JWT + `admin` role |
| `POST /admin/approve_loan` | ✅ JWT | ✅ JWT + `admin` role |
| `POST /admin/create_admin` | ✅ JWT | ✅ JWT + `super_admin` role |
| `POST /api/mfa/setup` | ❌ Missing | ✅ JWT required |
| `POST /api/refresh` | ❌ Missing | ✅ Refresh token |

---

## 4. Rate Limiting Configuration

### 4.1 Flask-Limiter Setup

```bash
pip install Flask-Limiter redis
```

```python
# rate_limiting.py
from flask_limiter import Limiter
from flask_limiter.util import get_remote_address
import redis

# Use Redis backend for distributed rate limiting
redis_client = redis.Redis(host=os.environ['REDIS_HOST'], port=6379, db=0)

limiter = Limiter(
    app=app,
    key_func=get_remote_address,
    storage_uri=f"redis://{os.environ['REDIS_HOST']}:6379",
    default_limits=["500 per day", "100 per hour"],
    headers_enabled=True  # Return X-RateLimit-* headers
)
```

### 4.2 Per-Endpoint Rate Limits

| Endpoint | Limit | Rationale |
|---|---|---|
| `POST /login` | **5/min, 20/hour** | Brute-force protection |
| `POST /register` | **3/min, 10/hour** | Account creation abuse |
| `POST /transfer` | **10/min, 50/hour** | Financial transaction limit |
| `POST /request_loan` | **3/min, 10/hour** | Loan abuse prevention |
| `POST /api/v2/forgot-password` | **3/15min** | PIN brute-force protection |
| `POST /api/v2/reset-password` | **3/15min** | PIN attempt limiting |
| `POST /api/mfa/verify` | **5/5min** | OTP brute-force prevention |
| `POST /upload_profile_picture` | **5/hour** | Abuse prevention |
| `GET /check_balance/:id` | **30/min** | BOLA enumeration protection |

```python
# Applied per route
@app.route('/login', methods=['POST'])
@limiter.limit("5 per minute;20 per hour")
def login(): ...

@app.route('/transfer', methods=['POST'])
@token_required
@limiter.limit("10 per minute;50 per hour")
def transfer(current_user): ...

@app.route('/api/v2/reset-password', methods=['POST'])
@limiter.limit("3 per 15 minutes")
def reset_password_v2(): ...
```

### 4.3 Custom 429 Error Handler

```python
@app.errorhandler(429)
def rate_limit_exceeded(e):
    return jsonify({
        'status': 'error',
        'message': 'Too many requests. Please try again later.',
        'retry_after': e.retry_after  # Seconds until reset
    }), 429
```

---

## 5. Web Application Firewall (WAF)

### 5.1 ModSecurity + OWASP CRS (Nginx)

```bash
# Install ModSecurity for Nginx
apt-get install libmodsecurity3 libnginx-mod-http-modsecurity

# Download OWASP Core Rule Set
cd /etc/nginx
git clone https://github.com/coreruleset/coreruleset owasp-crs
cp owasp-crs/crs-setup.conf.example owasp-crs/crs-setup.conf
```

**Nginx ModSecurity Config** (`/etc/nginx/modsec/modsecurity.conf`):
```nginx
# Enable ModSecurity
SecRuleEngine On

# Anomaly scoring mode (recommended over DetectionOnly)
SecDefaultAction "phase:1,log,auditlog,pass"
SecDefaultAction "phase:2,log,auditlog,pass"

# Include OWASP CRS rules
Include /etc/nginx/owasp-crs/crs-setup.conf
Include /etc/nginx/owasp-crs/rules/*.conf

# Request size limits
SecRequestBodyLimit       10485760  # 10MB max
SecRequestBodyNoFilesLimit 1048576  # 1MB max (non-file)

# Response body inspection
SecResponseBodyAccess On
SecResponseBodyLimit 10485760

# Audit log
SecAuditEngine On
SecAuditLog /var/log/modsec_audit.log
SecAuditLogParts ABCEFHJKZ
```

**Apply in Nginx server block**:
```nginx
server {
    listen 443 ssl;
    modsecurity on;
    modsecurity_rules_file /etc/nginx/modsec/modsecurity.conf;
    ...
}
```

### 5.2 Custom WAF Rules for SIVBA

```nginx
# /etc/nginx/modsec/sivba-custom-rules.conf

# Block SQL injection patterns in account numbers (URL parameter)
SecRule ARGS:account_number "@detectSQLi" \
    "id:1001,phase:2,deny,status:400,\
     msg:'SQL Injection in account_number',\
     logdata:'%{MATCHED_VAR}'"

# Block account enumeration — rate limit check_balance
SecRule REQUEST_URI "@contains /check_balance/" \
    "id:1002,phase:1,\
     setvar:'ip.balance_check_count=+1',\
     expirevar:'ip.balance_check_count=60'"
SecRule IP:BALANCE_CHECK_COUNT "@gt 20" \
    "id:1003,phase:1,deny,status:429,\
     msg:'Account enumeration detected'"

# Block negative amounts in transfer body
SecRule REQUEST_BODY "@rx \"amount\":\s*-" \
    "id:1004,phase:2,deny,status:400,\
     msg:'Negative transfer amount blocked'"

# Block access to debug endpoints
SecRule REQUEST_URI "@contains /debug/" \
    "id:1005,phase:1,deny,status:404,\
     msg:'Debug endpoint access blocked'"

# Block path traversal in file uploads
SecRule FILES_NAMES "@contains ../" \
    "id:1006,phase:2,deny,status:400,\
     msg:'Path traversal in filename'"
```

### 5.3 Cloudflare WAF (Alternative / Complementary)

For hosted deployments, Cloudflare WAF rules complement ModSecurity:

| Rule Name | Expression | Action |
|---|---|---|
| Block SQL injection | `http.request.body contains "' OR"` | Block |
| Block admin panel scraping | `http.request.uri contains "/sup3r_"` | Block + Log |
| Block abnormal body size | `http.request.body.size > 10000` | Block |
| Rate limit `/login` | 5 requests/minute per IP | Challenge |
| Geo-restrict admin | `ip.geoip.country ne "IN" and uri contains "/admin"` | Block |

---

## 6. Backend Firewall Configuration

### 6.1 iptables Rules

```bash
#!/bin/bash
# vulnbank-firewall.sh

# Flush existing rules
iptables -F
iptables -X

# Default policies: DROP all INPUT, FORWARD
iptables -P INPUT DROP
iptables -P FORWARD DROP
iptables -P OUTPUT ACCEPT

# Allow loopback
iptables -A INPUT -i lo -j ACCEPT

# Allow established connections
iptables -A INPUT -m state --state ESTABLISHED,RELATED -j ACCEPT

# Allow HTTPS (443)
iptables -A INPUT -p tcp --dport 443 -j ACCEPT

# Allow HTTP (80) — redirect to HTTPS only
iptables -A INPUT -p tcp --dport 80 -j ACCEPT

# Allow SSH from management IP only
iptables -A INPUT -p tcp --dport 22 -s 10.0.0.0/24 -j ACCEPT

# Allow PostgreSQL only from app server (not public internet)
iptables -A INPUT -p tcp --dport 5432 -s 127.0.0.1 -j ACCEPT
iptables -A INPUT -p tcp --dport 5432 -j DROP

# Allow Redis only from localhost
iptables -A INPUT -p tcp --dport 6379 -s 127.0.0.1 -j ACCEPT
iptables -A INPUT -p tcp --dport 6379 -j DROP

# Rate limit new connections to HTTPS (anti-DDoS)
iptables -A INPUT -p tcp --dport 443 \
    -m state --state NEW -m recent --set --name HTTPS_CONN
iptables -A INPUT -p tcp --dport 443 \
    -m state --state NEW -m recent --update \
    --seconds 60 --hitcount 100 --name HTTPS_CONN -j DROP

# Log and drop all other traffic
iptables -A INPUT -j LOG --log-prefix "BLOCKED: "
iptables -A INPUT -j DROP

# Save rules
iptables-save > /etc/iptables/rules.v4
```

### 6.2 Cloud Security Group (AWS/GCP Equivalent)

| Rule | Protocol | Port | Source | Action |
|---|---|---|---|---|
| HTTPS | TCP | 443 | 0.0.0.0/0 | Allow |
| HTTP (redirect) | TCP | 80 | 0.0.0.0/0 | Allow |
| SSH | TCP | 22 | Management CIDR | Allow |
| PostgreSQL | TCP | 5432 | App Server Private IP | Allow |
| Redis | TCP | 6379 | App Server Private IP | Allow |
| All other inbound | * | * | * | Deny |

---

## 7. Endpoint Security Matrix

| Endpoint | Method | Auth | Role | Rate Limit | WAF Rule | Input Validation |
|---|---|---|---|---|---|---|
| `/login` | POST | ❌ | Any | 5/min | SQLi, brute-force | Username len, format |
| `/register` | POST | ❌ | Any | 3/min | SQLi, mass-assign | Allowlist fields only |
| `/check_balance/:id` | GET | ✅ JWT | user (own) / admin | 30/min | IDOR check | Account # format |
| `/transfer` | POST | ✅ JWT | user | 10/min | Negative amount | amount > 0, ≤ 100k |
| `/transactions/:id` | GET | ✅ JWT | user (own) / admin | 30/min | IDOR check | Account # format |
| `/request_loan` | POST | ✅ JWT | user | 3/min | — | amount > 0 |
| `/upload_profile_picture` | POST | ✅ JWT | user | 5/hour | File type | MIME + ext + size |
| `/api/v2/forgot-password` | POST | ❌ | Any | 3/15min | SQLi | Username format |
| `/api/v2/reset-password` | POST | ❌ | Any | 3/15min | — | PIN = 6 digits |
| `/api/mfa/setup` | POST | ✅ JWT | user | 5/hour | — | — |
| `/api/mfa/verify` | POST | ✅ Partial | Any | 5/5min | — | 6-digit numeric |
| `/api/refresh` | POST | ✅ Refresh | Any | 10/min | — | Refresh token |
| `/api/logout` | POST | ✅ JWT | Any | 20/min | — | — |
| `/admin/users` | GET | ✅ JWT | admin+ | 20/min | — | — |
| `/admin/approve_loan/:id` | POST | ✅ JWT | admin+ | 10/min | — | loan_id integer |
| `/admin/create_admin` | POST | ✅ JWT | super_admin | 3/hour | SQLi | Username, password |
| `/admin/delete_account/:id` | POST | ✅ JWT | admin+ | 5/hour | — | user_id integer |
| `/debug/users` | GET | 🚫 | — | — | — | **ROUTE REMOVED** |

---

## 8. API Security Headers

Set on all responses via Nginx:

```nginx
add_header Strict-Transport-Security "max-age=31536000; includeSubDomains; preload" always;
add_header X-Frame-Options "DENY" always;
add_header X-Content-Type-Options "nosniff" always;
add_header X-XSS-Protection "1; mode=block" always;
add_header Referrer-Policy "no-referrer" always;
add_header Permissions-Policy "camera=(), microphone=(), geolocation=()" always;
add_header Content-Security-Policy "default-src 'self'; script-src 'self'; object-src 'none'" always;

# Remove server identification headers
server_tokens off;
proxy_hide_header X-Powered-By;
```

---

## 9. Input Validation with Marshmallow

```bash
pip install marshmallow
```

```python
# schemas.py
from marshmallow import Schema, fields, validate, ValidationError

class LoginSchema(Schema):
    username = fields.Str(required=True, validate=validate.Length(min=3, max=50))
    password = fields.Str(required=True, validate=validate.Length(min=8, max=128))

class TransferSchema(Schema):
    to_account   = fields.Str(required=True, validate=validate.Regexp(r'^\d{10}$'))
    amount       = fields.Float(required=True, validate=validate.Range(min=0.01, max=100000))
    description  = fields.Str(load_default='Transfer', validate=validate.Length(max=200))

class RegisterSchema(Schema):
    username = fields.Str(required=True, validate=[
        validate.Length(min=3, max=30),
        validate.Regexp(r'^[a-zA-Z0-9_]+$', error='Only alphanumeric and underscore')
    ])
    password = fields.Str(required=True, validate=validate.Length(min=8, max=128))
```

```python
# In route handlers:
from schemas import LoginSchema, TransferSchema
from marshmallow import ValidationError

@app.route('/login', methods=['POST'])
@limiter.limit("5 per minute")
def login():
    try:
        data = LoginSchema().load(request.get_json())
    except ValidationError as err:
        return jsonify({'status': 'error', 'errors': err.messages}), 422
    ...
```

---

*Next: Deliverable 6 — Data Privacy & Compliance Report*
