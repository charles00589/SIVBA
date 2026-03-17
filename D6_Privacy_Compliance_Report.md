# Deliverable 6: Data Privacy & Compliance Report
## SIVBA — Vulnerable Bank Mobile App

**Project**: SIVBA Security Hardening  
**Document**: Data Privacy & Compliance Report  
**Version**: 1.0  
**Date**: March 2026

---

## 1. Executive Summary

This report audits the SIVBA banking application against three major data privacy and financial security regulations: **GDPR**, **CCPA**, and **PCI DSS**. The audit reveals **critical non-compliance** across all three frameworks, primarily due to plaintext credential storage, absence of consent mechanisms, unrestricted data access, no breach notification capability, and missing card data protections.

---

## 2. Data Inventory

Before conducting the compliance audit, all personal and financial data processed by SIVBA was catalogued:

| Data Type | Classification | Where Stored | Currently Protected? |
|---|---|---|---|
| Username | Personal Data | PostgreSQL DB | ❌ Plaintext |
| Password | Sensitive Personal Data | PostgreSQL DB | ❌ PLAINTEXT (no hashing!) |
| Account Number | Financial Data | DB + AsyncStorage | ❌ Unencrypted |
| Account Balance | Financial Data | PostgreSQL DB | ❌ Plaintext |
| Transaction History | Financial Data | PostgreSQL DB | ❌ Unauthenticated access |
| Virtual Card Number | PAN (PCI DSS Scope) | PostgreSQL DB | ❌ Plaintext |
| CVV | Sensitive Auth Data | PostgreSQL DB | ❌ Plaintext |
| JWT Token | Auth Credential | AsyncStorage | ❌ Unencrypted |
| Password Reset PIN | Sensitive Auth Data | DB + API response | ❌ Plaintext in both |
| Loan Amount/Status | Financial Data | PostgreSQL DB | ❌ Unauthenticated access |
| IP Address | Personal Data (GDPR) | Server logs | ⚠️ Logged but unsecured |
| User-Agent | Technical Data | DB (registration) | ❌ Unnecessarily stored |

---

## 3. GDPR Compliance Audit

**Regulation**: General Data Protection Regulation (EU) 2016/679  
**Applicability**: Any application processing data of EU residents

### 3.1 GDPR Findings

| Article | Requirement | SIVBA Status | Gap |
|---|---|---|---|
| Art. 5(1)(a) | Lawfulness, fairness, transparency | ❌ Non-compliant | No privacy policy, no consent mechanism |
| Art. 5(1)(b) | Purpose limitation | ❌ Non-compliant | User-Agent stored with no stated purpose |
| Art. 5(1)(c) | Data minimisation | ❌ Non-compliant | Excessive data returned in API responses |
| Art. 5(1)(e) | Storage limitation | ❌ Non-compliant | No data retention or deletion policy |
| Art. 5(1)(f) | Integrity & confidentiality | ❌ Non-compliant | Passwords plaintext, tokens unencrypted |
| Art. 6 | Lawful basis for processing | ❌ Non-compliant | No consent collected at registration |
| Art. 13 | Information to data subjects | ❌ Non-compliant | No privacy notice at data collection |
| Art. 17 | Right to erasure (Right to be forgotten) | ⚠️ Partial | `/admin/delete_account` exists but no self-service |
| Art. 25 | Privacy by design & default | ❌ Non-compliant | App collects maximum data, exposes maximum data |
| Art. 32 | Security of processing | ❌ Non-compliant | No encryption, no pseudonymisation |
| Art. 33 | Breach notification (72 hours) | ❌ Non-compliant | No monitoring, no alerting, no breach capability |
| Art. 35 | Data Protection Impact Assessment (DPIA) | ❌ Non-compliant | No DPIA conducted |

### 3.2 GDPR Remediation Plan

| Priority | Action |
|---|---|
| 🔴 Critical | Encrypt passwords with Argon2id |
| 🔴 Critical | Implement breach detection and 72-hour notification capability |
| 🔴 Critical | Add consent screen at registration with clear privacy notice |
| 🟠 High | Remove excessive data exposure from API responses |
| 🟠 High | Implement user self-service data deletion endpoint |
| 🟠 High | Stop storing User-Agent in DB (no stated purpose) |
| 🟡 Medium | Implement data retention policy (auto-delete after X years) |
| 🟡 Medium | Conduct formal DPIA for the banking application |
| 🟡 Medium | Appoint a Data Protection Officer (DPO) |

---

## 4. CCPA Compliance Audit

**Regulation**: California Consumer Privacy Act (CCPA / CPRA)  
**Applicability**: Businesses processing data of California residents

### 4.1 CCPA Findings

| CCPA Right | Requirement | SIVBA Status | Gap |
|---|---|---|---|
| Right to Know | Tell users what data is collected and why | ❌ Non-compliant | No disclosure at point of collection |
| Right to Access | Users can request all data held about them | ❌ Non-compliant | No data export / subject access mechanism |
| Right to Delete | Users can request deletion of their data | ⚠️ Partial | Admin can delete; no self-service |
| Right to Opt-Out | Opt out of sale of personal information | ✅ N/A | SIVBA does not sell data (training app) |
| Right to Non-Discrimination | Equal service regardless of rights exercise | ✅ N/A | Not applicable |
| Right to Correct | Users can correct inaccurate personal data | ❌ Non-compliant | No profile update endpoint |
| Notice at Collection | Privacy notice before or at data collection | ❌ Non-compliant | Registration form has no privacy notice |

### 4.2 CCPA Remediation Plan

| Action | Implementation |
|---|---|
| Add Privacy Notice at registration | Modal or linked Privacy Policy before account creation |
| Implement data access export | `GET /api/my-data` → returns JSON of all user's stored data |
| Implement self-service deletion | `DELETE /api/my-account` with confirmation and re-auth |
| Add profile update endpoint | `PUT /profile/update` for name, contact info correction |

---

## 5. PCI DSS Compliance Audit

**Standard**: Payment Card Industry Data Security Standard (v4.0)  
**Applicability**: Any system that stores, processes, or transmits cardholder data

> [!IMPORTANT]
> SIVBA stores virtual card numbers and CVVs in plaintext in the database. This is a **critical PCI DSS violation**. Real CVVs must **never** be stored after authorization.

### 5.1 PCI DSS Findings by Requirement

| Req. | Title | SIVBA Status | Gap |
|---|---|---|---|
| **Req 1** | Install & maintain network security controls | ❌ Non-compliant | No firewall, no network segmentation |
| **Req 2** | Apply secure configurations | ❌ Non-compliant | Hardcoded secrets, default/weak keys |
| **Req 3** | Protect stored account data | ❌ Critical | Card numbers & CVVs stored in plaintext |
| **Req 4** | Protect cardholder data in transit | ❌ Non-compliant | HTTP cleartext allowed, no TLS pinning |
| **Req 5** | Protect systems from malicious software | ⚠️ Unknown | No evidence of AV/malware controls |
| **Req 6** | Develop and maintain secure systems | ❌ Non-compliant | 30+ known vulnerabilities unpatched |
| **Req 7** | Restrict access by business need | ❌ Non-compliant | No RBAC, unauthenticated endpoints |
| **Req 8** | Identify and authenticate system components | ❌ Non-compliant | Hardcoded `secret123`, no MFA |
| **Req 9** | Restrict physical access | ⚠️ Unknown | Physical security not assessed |
| **Req 10** | Log and monitor all access | ❌ Non-compliant | No audit logging, no SIEM |
| **Req 11** | Test security systems and processes | ❌ Non-compliant | No pen testing, no vulnerability scanning |
| **Req 12** | Support information security with policies | ❌ Non-compliant | No security policy documented |

### 5.2 PCI DSS Critical Remediations (Cardholder Data)

**Requirement 3 — Card Data Protection**:
```
Current: card_number VARCHAR(16) -- stored plaintext
         cvv VARCHAR(3)          -- stored plaintext

Required:
  - Card numbers: Truncate (show only last 4 digits) OR encrypt with AES-256
  - CVVs: MUST NEVER BE STORED after authorization per PCI DSS 3.2.1
  - Card numbers in transit: Encrypt with TLS 1.3
```

**Remediation**:
```python
# Store encrypted PAN (Primary Account Number)
encrypted_pan = encrypt_field(card_number)  # AES-256-GCM

# NEVER store CVV — only used once during card creation for display
execute_query(
    "INSERT INTO virtual_cards (user_id, card_number_encrypted) VALUES (%s, %s)",
    (user_id, encrypted_pan)
    # No CVV column
)

# When displaying: truncate
def mask_card(encrypted_pan: str) -> str:
    pan = decrypt_field(encrypted_pan)
    return f"**** **** **** {pan[-4:]}"
```

### 5.3 PCI DSS Compliance Checklist

| Control | Status | Due |
|---|---|---|
| ☐ Deploy WAF (Req 6.4) | ❌ | Immediate |
| ☐ Encrypt card data at rest (Req 3.5) | ❌ | Immediate |
| ☐ Remove CVV storage (Req 3.2.1) | ❌ | Immediate |
| ☐ Enable TLS 1.3 only (Req 4.2) | ❌ | Immediate |
| ☐ Implement MFA for admin (Req 8.4) | ❌ | Sprint 1 |
| ☐ Deploy network firewall (Req 1.3) | ❌ | Sprint 1 |
| ☐ Enable audit logging (Req 10.2) | ❌ | Sprint 1 |
| ☐ Patch all known vulns (Req 6.3) | ❌ | Sprint 1–2 |
| ☐ Conduct pen test (Req 11.4) | ❌ | Sprint 3 |
| ☐ Write security policies (Req 12) | ❌ | Sprint 2 |

---

## 6. Privacy by Design — Implementation

GDPR Article 25 requires Privacy by Design. The following principles must be embedded into SIVBA's architecture:

| Principle | Implementation |
|---|---|
| Proactive not Reactive | Threat model before building new features |
| Privacy as Default | Collect minimum data; default opt-out of analytics |
| Privacy embedded in Design | Encryption, RBAC built into architecture (not added later) |
| Full Functionality | Privacy controls that don't limit banking functions |
| End-to-End Security | TLS, encrypted storage, encrypted DB fields |
| Visibility and transparency | Privacy notice, data use disclosure |
| Respect for User Privacy | Self-service data access, export, deletion |

---

## 7. Consolidated Compliance Gap Summary

| Gap | GDPR | CCPA | PCI DSS | Severity |
|---|---|---|---|---|
| Plaintext passwords | ✅ Art. 32 | — | Req 8 | 🔴 Critical |
| Plaintext card data | — | — | ✅ Req 3 | 🔴 Critical |
| No authentication on data endpoints | ✅ Art. 32 | ✅ Right to Know | ✅ Req 7 | 🔴 Critical |
| No consent at registration | ✅ Art. 6 | ✅ Notice at Collection | — | 🟠 High |
| No data breach notification | ✅ Art. 33 | — | ✅ Req 12 | 🟠 High |
| No audit logging | ✅ Art. 32 | — | ✅ Req 10 | 🟠 High |
| Excessive API data exposure | ✅ Art. 5(1)(c) | ✅ Right to Know | — | 🟠 High |
| No self-service data deletion | ✅ Art. 17 | ✅ Right to Delete | — | 🟡 Medium |
| No data retention policy | ✅ Art. 5(1)(e) | — | — | 🟡 Medium |
| No WAF / firewall | — | — | ✅ Req 1, 6 | 🟡 Medium |
| HTTP cleartext allowed | ✅ Art. 32 | — | ✅ Req 4 | 🟡 Medium |

---

*Next: Deliverable 7 — Monitoring & Incident Response Plan*
