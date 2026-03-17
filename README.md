# SIVBA: Simulated Intentionally Vulnerable Bank Application
## 🛡️ Security Hardening & Penetration Testing Project

![Security Status](https://img.shields.io/badge/Security-Hardened-success?style=for-the-badge&logo=shield)
![Compliance](https://img.shields.io/badge/Compliance-GDPR%20%7C%20PCI%20DSS-blue?style=for-the-badge)
![Vulnerabilities Patched](https://img.shields.io/badge/Vulnerabilities-30%2B%20Remediated-orange?style=for-the-badge)

This repository contains the complete security transformation of the **SIVBA** (Simulated Intentionally Vulnerable Bank Application). From a critically vulnerable state to a hardened, enterprise-ready security posture.

---

## 📂 Project Structure

This project is organized into **8 core security deliverables** covering the entire SDLC:

| Deliverable | Description | Key Focus Area |
| :--- | :--- | :--- |
| [**D1: Architecture**](./D1_Security_Architecture.md) | Secure Network & App Design | Defense-in-Depth, Threat Modeling |
| [**D2: Encryption**](./D2_Encryption_Data_Protection.md) | Data-at-Rest & In-Transit | TLS 1.3, AES-256, Argon2id |
| [**D3: Auth & RBAC**](./D3_Auth_AuthZ_Guide.md) | Identity & Access Management | MFA, Biometrics, Server-side RBAC |
| [**D4: Secure Coding**](./D4_Secure_Coding_Guidelines.md) | Vulnerability Audit & Fixes | SQLi, XSS, BOLA, Mass Assignment |
| [**D5: Network Sec**](./D5_API_Network_Security.md) | Perimeter Protections | ModSecurity WAF, OWASP CRS, Firewall |
| [**D6: Compliance**](./D6_Privacy_Compliance_Report.md) | Regulatory Alignment | GDPR, CCPA, PCI DSS v4.0 |
| [**D7: Monitoring**](./D7_Monitoring_IR_Plan.md) | Detection & Response | ELK Stack, Alerting, IR Playbooks |
| [**D8: User Training**](./D8_User_Training_Pack.md) | Human Element | Phishing & Fraud Awareness |

---

## 🚀 Key Hardening Highlights

### 🔹 Virtual Patching with WAF
Before remediation, the application was vulnerable to simple SQL injection. We implemented a **Web Application Firewall (WAF)** using ModSecurity and the OWASP Core Rule Set to virtually patch these vulnerabilities at the edge.

### 🔹 Advanced Cryptography
- **In-Transit**: Enforced TLS 1.3 with Perfect Forward Secrecy.
- **At-Rest**: Replaced insecure `AsyncStorage` with hardware-backed **AES-256 Encrypted Storage**.
- **Passwords**: Migrated from plaintext to **Argon2id** (memory-hard hashing).

### 🔹 Identity Hardening
Removed broken client-side admin checks and replaced them with **RS256 signed JWT claims** and **Multi-Factor Authentication (TOTP)**.

---

## 📸 Visual Proof of Hardening
Check the [**Screenshots Folder**](./screenshots/) for visual "Before vs After" comparisons of:
- Vulnerable vs. Secure Code snippets.
- Successful vs. Blocked attack attempts.

---

## 🛠️ Tech Stack & Tools Used
- **Backend**: Python (Flask)
- **Frontend**: React Native (TypeScript)
- **Security**: Nginx / ModSecurity / OWASP CRS
- **Analysis**: Static analysis (SAST), Manual Code Review, Threat Modeling

---
*Created as part of the SIVBA Security Project.*
