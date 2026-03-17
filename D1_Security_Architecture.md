# Deliverable 1: Security Architecture Document
## SIVBA — Vulnerable Bank Mobile App

**Project**: SIVBA Security Hardening  
**Document**: Security Architecture Review & Enhancement  
**Version**: 1.0  
**Date**: March 2026  
**Author**: Security Engineering Team  

---

## 1. Executive Summary

This document presents a comprehensive security architecture review of the **SIVBA (Simulated Intentionally Vulnerable Bank Application)** mobile banking system. The review identifies critical weaknesses across all architectural layers and proposes a hardened, defence-in-depth security architecture aligned with industry standards including OWASP Mobile Top 10, NIST SP 800-53, and PCI DSS.

The current SIVBA architecture contains **30+ intentional security vulnerabilities** spanning insecure data storage, broken authentication, SQL injection, unrestricted API access, and absence of encryption — making it an immediate security risk if deployed in a real environment. This document defines the target secure architecture to remediate these issues.

---

## 2. Current Architecture Assessment

### 2.1 System Components (As-Is)

| Component | Technology | Description |
|---|---|---|
| Mobile Client | React Native 0.79.1 (TypeScript) | Android banking app |
| Local Storage | AsyncStorage (unencrypted) | JWT tokens, credentials stored in plaintext |
| Backend API | Flask (Python) | REST API serving all banking operations |
| Database | PostgreSQL | User accounts, transactions, loans, cards |
| Network | HTTP/HTTPS (no pinning) | No certificate pinning enforced |
| Authentication | JWT Bearer tokens | Weak secret key, no expiry enforcement |
| Admin Access | URL-based obscurity | `/sup3r_s3cr3t_admin` — no proper RBAC |

### 2.2 Current Architecture Diagram (As-Is)

```
┌─────────────────────────────────┐
│         Android Device          │
│  ┌───────────────────────────┐  │
│  │   React Native App        │  │
│  │  (No cert pinning)        │  │
│  │  AsyncStorage (plaintext) │  │
│  └──────────┬────────────────┘  │
└─────────────┼───────────────────┘
              │ HTTP/HTTPS (no TLS enforcement)
              │ No WAF / No Rate Limiting
              ▼
┌─────────────────────────────────┐
│         Flask API Server        │
│  - No input validation          │
│  - SQL injection vulnerable     │
│  - Hardcoded secret key         │
│  - Debug endpoints exposed      │
│  - Wide-open CORS               │
└──────────────┬──────────────────┘
               │ Plain SQL queries
               ▼
┌─────────────────────────────────┐
│        PostgreSQL Database      │
│  - Plaintext passwords          │
│  - No field-level encryption    │
│  - No audit logging             │
└─────────────────────────────────┘
```

### 2.3 Identified Weakness Summary

| Layer | Critical Issues |
|---|---|
| **Mobile Client** | Plaintext AsyncStorage, client-side admin check, no cert pinning, hardcoded secrets |
| **Transport** | No TLS 1.3 enforcement, cleartext traffic allowed, no HSTS |
| **API Layer** | No WAF, no rate limiting, SQL injection on 6+ endpoints, unauthenticated debug routes |
| **Authentication** | Hardcoded JWT secret `"secret123"`, no token expiry, no MFA |
| **Database** | Plaintext password storage, no field encryption, no audit log |
| **Infrastructure** | Wide-open CORS, no firewall rules, no monitoring |

---

## 3. Proposed Secure Architecture (To-Be)

### 3.1 Security Architecture Principles

The hardened architecture is guided by the following principles:

1. **Defence in Depth** — Multiple independent security controls at each layer
2. **Least Privilege** — Users and services access only what they need
3. **Zero Trust** — Every request is authenticated and authorized, regardless of origin
4. **Fail Secure** — Systems default to denial on failure or ambiguity
5. **Privacy by Design** — Data minimization, encryption at rest and in transit
6. **Auditability** — All security-relevant events are logged and monitored

### 3.2 Proposed Architecture Diagram (To-Be)

```
┌──────────────────────────────────────────────┐
│              Android Device                  │
│  ┌────────────────────────────────────────┐  │
│  │          React Native App              │  │
│  │  ✅ Certificate Pinning (TLS 1.3)     │  │
│  │  ✅ EncryptedStorage (AES-256)        │  │
│  │  ✅ Biometric Auth (FingerprintAPI)   │  │
│  │  ✅ TOTP MFA                          │  │
│  │  ✅ No secrets in source code         │  │
│  └──────────────┬─────────────────────────┘  │
└─────────────────┼────────────────────────────┘
                  │ TLS 1.3 (mutual auth)
                  │ Certificate Pinned
                  ▼
┌──────────────────────────────────────────────┐
│            API Gateway / WAF Layer           │
│  ✅ ModSecurity WAF (SQLi, XSS rules)       │
│  ✅ Rate Limiting (per endpoint / per IP)   │
│  ✅ DDoS Protection                         │
│  ✅ HTTPS only — HSTS enforced              │
│  ✅ IP Allowlisting for admin routes        │
└──────────────────┬───────────────────────────┘
                   │ Filtered, authenticated requests only
                   ▼
┌──────────────────────────────────────────────┐
│            Flask API Application             │
│  ✅ JWT (RS256, 15-min expiry + refresh)    │
│  ✅ Parameterized queries only              │
│  ✅ Input validation (Marshmallow/Pydantic) │
│  ✅ RBAC decorator on all routes            │
│  ✅ No debug endpoints in production        │
│  ✅ Secrets via environment variables       │
│  ✅ CORS restricted to known origins        │
└──────────────────┬───────────────────────────┘
                   │ Connection pooled, least-privilege DB user
                   ▼
┌──────────────────────────────────────────────┐
│            PostgreSQL Database               │
│  ✅ Passwords hashed with Argon2id          │
│  ✅ PII fields encrypted (AES-256-GCM)     │
│  ✅ DB user has minimal privileges          │
│  ✅ Audit log table for all mutations       │
│  ✅ TLS between app and DB                  │
└──────────────────────────────────────────────┘
                   │
                   ▼
┌──────────────────────────────────────────────┐
│           Monitoring & SIEM Layer            │
│  ✅ Centralized logging (ELK / Loki)        │
│  ✅ Alerting on anomalies                   │
│  ✅ Incident response playbooks             │
└──────────────────────────────────────────────┘
```

---

## 4. Layer-by-Layer Security Controls

### 4.1 Mobile Client Layer

| Control | Implementation | Standard |
|---|---|---|
| Encrypted Local Storage | `react-native-encrypted-storage` (AES-256) | OWASP M2 |
| Certificate Pinning | SHA-256 pin in `network_security_config.xml` | OWASP M3 |
| Biometric Authentication | `react-native-biometrics` (Android BiometricPrompt) | OWASP M4 |
| MFA (TOTP) | Client-side TOTP input, server-side `pyotp` verification | OWASP M4 |
| No Hardcoded Secrets | All API base URLs from `BuildConfig`, no tokens in source | OWASP M9 |
| Code Obfuscation | ProGuard/R8 enabled for release APK | OWASP M9 |
| Rooted Device Detection | `SafetyNet Attestation` / `Play Integrity API` | OWASP M1 |

### 4.2 Transport Layer

| Control | Implementation | Standard |
|---|---|---|
| TLS 1.3 Only | Nginx `ssl_protocols TLSv1.3;` | PCI DSS Req 4 |
| Strong Cipher Suites | `TLS_AES_256_GCM_SHA384`, `TLS_CHACHA20_POLY1305_SHA256` | NIST SP 800-52r2 |
| HSTS | `Strict-Transport-Security: max-age=31536000; includeSubDomains` | OWASP TLS |
| Certificate Pinning | App pins server's leaf certificate SHA-256 | OWASP M3 |
| OCSP Stapling | Enabled on Nginx for real-time revocation checks | PKI Best Practice |

### 4.3 API Gateway / WAF Layer

| Control | Implementation | Standard |
|---|---|---|
| Web Application Firewall | ModSecurity with OWASP Core Rule Set (CRS 3.3+) | OWASP Top 10 |
| Rate Limiting | `flask-limiter`; login: 5/min; transfer: 10/min | OWASP API4 |
| DDoS Protection | Cloudflare / AWS Shield | Availability |
| IP-based Admin Access | Admin routes restricted to management CIDR | Least Privilege |
| Request Size Limits | Max body: 1MB (Nginx `client_max_body_size`) | Abuse Prevention |

### 4.4 Application Layer

| Control | Implementation | Standard |
|---|---|---|
| Authentication | JWT RS256 (asymmetric), 15-min access token, 7-day refresh | OWASP Session |
| Authorization | RBAC with `@role_required` decorator on every route | OWASP API5 |
| Input Validation | Marshmallow schemas on all request bodies | OWASP A03 |
| Parameterized Queries | SQLAlchemy ORM or `%s` placeholders — no f-strings | CWE-89 |
| Output Encoding | `html.escape()` on all user-controlled response fields | CWE-79 |
| Secret Management | All secrets from `.env` / AWS Secrets Manager — no hardcoding | CWE-798 |
| Error Handling | Generic error messages; full detail in server-side logs only | CWE-209 |
| File Upload | MIME type + magic bytes validation; store outside web root | CWE-434 |
| CORS | Restrict `Access-Control-Allow-Origin` to trusted domains | OWASP CORS |

### 4.5 Database Layer

| Control | Implementation | Standard |
|---|---|---|
| Password Hashing | Argon2id (preferred) or bcrypt (cost factor ≥ 12) | CWE-916 |
| PII Encryption | AES-256-GCM for account numbers, card details | PCI DSS Req 3 |
| DB User Privileges | Separate read-only and read-write DB users; no superuser | Least Privilege |
| Connection Security | TLS between Flask and PostgreSQL (`sslmode=require`) | PCI DSS Req 4 |
| Audit Logging | Trigger-based `audit_log` table for INSERT/UPDATE/DELETE | PCI DSS Req 10 |
| Backup Encryption | AES-256 encrypted backups, off-site storage | BCP |

---

## 5. Access Control Architecture (RBAC)

### 5.1 Role Definitions

| Role | Description | Assigned To |
|---|---|---|
| `user` | Standard authenticated bank customer | All registered users |
| `admin` | Bank administrator | Privileged staff accounts |
| `super_admin` | Full system access including admin management | Senior security team only |

### 5.2 Endpoint Permission Matrix

| Endpoint | `user` | `admin` | `super_admin` | Auth Required |
|---|---|---|---|---|
| `POST /login` | ✅ | ✅ | ✅ | ❌ (public) |
| `POST /register` | ✅ | ✅ | ✅ | ❌ (public) |
| `GET /check_balance/:id` | Own only | ✅ All | ✅ All | ✅ JWT |
| `GET /transactions/:id` | Own only | ✅ All | ✅ All | ✅ JWT |
| `POST /transfer` | ✅ | ✅ | ✅ | ✅ JWT |
| `POST /request_loan` | ✅ | ✅ | ✅ | ✅ JWT |
| `GET /admin/users` | ❌ | ✅ | ✅ | ✅ JWT + Role |
| `POST /admin/approve_loan` | ❌ | ✅ | ✅ | ✅ JWT + Role |
| `POST /admin/create_admin` | ❌ | ❌ | ✅ | ✅ JWT + Role |
| `DELETE /admin/delete_account` | ❌ | ✅ | ✅ | ✅ JWT + Role |
| `GET /debug/users` | ❌ | ❌ | ❌ | **ROUTE REMOVED** |

### 5.3 Admin Access Control Design

```
Current (Vulnerable):
  Client: if (username === 'admin') → isAdmin = true
  No server-side validation of role

Hardened:
  Server: JWT payload contains { user_id, username, role: "admin" }
  All /admin/* routes protected by @role_required("admin") decorator
  Admin routes further restricted to management IP CIDR range
```

---

## 6. Secure Communication Protocols

### 6.1 Protocol Stack

```
Application Layer:    HTTPS (Flask → Client), TLS-wrapped DB connections
Transport Layer:      TLS 1.3 (TLS 1.0/1.1/1.2 disabled)
Certificate:          EV/DV certificate from trusted CA (Let's Encrypt / DigiCert)
Certificate Pinning:  Mobile app pins server certificate SHA-256 fingerprint
Cipher Suites:        TLS_AES_256_GCM_SHA384, TLS_CHACHA20_POLY1305_SHA256
Key Exchange:         X25519 / ECDHE (Perfect Forward Secrecy)
```

### 6.2 Nginx TLS Configuration (Reference)

```nginx
server {
    listen 443 ssl;
    ssl_protocols TLSv1.3;
    ssl_ciphers TLS_AES_256_GCM_SHA384:TLS_CHACHA20_POLY1305_SHA256;
    ssl_prefer_server_ciphers on;
    ssl_session_cache shared:SSL:10m;
    ssl_session_timeout 10m;
    ssl_stapling on;
    ssl_stapling_verify on;

    add_header Strict-Transport-Security "max-age=31536000; includeSubDomains; preload" always;
    add_header X-Frame-Options DENY;
    add_header X-Content-Type-Options nosniff;
    add_header X-XSS-Protection "1; mode=block";
    add_header Content-Security-Policy "default-src 'self'";
}
```

---

## 7. Secret Management Strategy

| Secret Type | Current State | Secure State |
|---|---|---|
| Flask `secret_key` | Hardcoded `"secret123"` | Generated 256-bit random key from `secrets.token_hex(32)`, stored in env var |
| JWT signing key | Symmetric weak key | RSA-2048 private key in AWS Secrets Manager / HashiCorp Vault |
| DB credentials | Likely in code/config | Environment variables only, rotated quarterly |
| API keys | Hardcoded in `AndroidManifest.xml` | Fetched from secure backend at runtime, never embedded in APK |

---

## 8. Security Architecture Governance

| Activity | Frequency | Owner |
|---|---|---|
| Penetration Testing | Annually + after major changes | External security firm |
| Vulnerability Scanning | Weekly (automated) | Security team |
| Dependency Audit | Per release (`pip-audit`, `npm audit`) | Dev team |
| Security Code Review | Every pull request | Security champion |
| Threat Modeling | Per major feature | Architect + Security |
| Access Review | Quarterly | IT Governance |

---

## 9. Threat Model (STRIDE Summary)

| Threat | Category | Mitigation |
|---|---|---|
| SQL injection via login | **Tampering** | Parameterized queries, WAF SQLi rules |
| JWT secret compromise | **Elevation of Privilege** | RS256 asymmetric keys, key rotation |
| Credential theft from AsyncStorage | **Information Disclosure** | AES-256 encrypted storage |
| Account balance tampering (negative transfer) | **Tampering** | Server-side amount validation (`amount > 0`) |
| Admin panel brute-force | **Elevation of Privilege** | Rate limiting, IP allowlisting, MFA |
| MitM attack (no cert pinning) | **Information Disclosure** | Certificate pinning + TLS 1.3 |
| Mass assignment to gain admin | **Elevation of Privilege** | Allowlist-based deserialization only |
| Unauthenticated balance lookup (IDOR) | **Info Disclosure** | JWT required on all data endpoints |

---

## 10. Conclusion

The current SIVBA architecture presents a high-risk profile with critical vulnerabilities across all layers. The proposed secure architecture applies a defence-in-depth approach — hardening the mobile client, enforcing TLS 1.3 in transit, deploying a WAF, securing the application layer with RBAC and input validation, and encrypting sensitive data at rest. Implementation of this architecture will transform SIVBA from a vulnerable training application into a model of secure mobile banking design.

---

*Next: Deliverable 2 — Encryption & Data Protection Plan*
