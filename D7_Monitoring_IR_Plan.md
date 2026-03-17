# Deliverable 7: Monitoring & Incident Response Plan
## SIVBA — Vulnerable Bank Mobile App

**Project**: SIVBA Security Hardening  
**Document**: Monitoring & Incident Response Plan  
**Version**: 1.0  
**Date**: March 2026

---

## 1. Executive Summary

SIVBA currently has **zero monitoring capability** — no centralized logging, no alerting, no SIEM, and no incident response process. This document defines a comprehensive monitoring strategy and incident response framework covering: event logging, anomaly detection alerts, severity classification, and detailed playbooks for the four most likely incident types.

---

## 2. Monitoring Strategy

### 2.1 What to Monitor

| Category | Events to Log |
|---|---|
| **Authentication** | Login success/failure, logout, token refresh, MFA success/failure |
| **Authorization** | Denied access attempts, role escalation attempts, admin panel access |
| **Financial Transactions** | All transfers (from, to, amount), loan requests, loan approvals |
| **Account Changes** | Password reset requests/completions, profile updates |
| **Admin Actions** | User deletion, admin creation, loan approvals (with approver ID) |
| **API Abuse** | Rate limit triggers, 400/401/403/429 error bursts |
| **System Events** | App startup/shutdown, DB errors, unhandled exceptions |
| **Network** | WAF blocks, firewall denies, unusual request sizes |

### 2.2 Log Format (Structured JSON)

```json
{
  "timestamp": "2026-03-17T22:58:24+05:30",
  "level": "WARNING",
  "event_type": "login_failure",
  "user_id": null,
  "username": "admin",
  "ip_address": "192.168.1.100",
  "user_agent": "Burp Suite/2024.1",
  "endpoint": "/login",
  "http_method": "POST",
  "http_status": 401,
  "message": "Invalid credentials for username: admin",
  "request_id": "req-abc123",
  "session_id": null
}
```

### 2.3 Python Logging Implementation

```python
# logging_config.py
import logging, json
from datetime import datetime
from flask import request, g

class StructuredLogger:
    def __init__(self):
        self.logger = logging.getLogger('sivba_security')
        handler = logging.FileHandler('/var/log/sivba/security.log')
        handler.setFormatter(logging.Formatter('%(message)s'))
        self.logger.addHandler(handler)
        self.logger.setLevel(logging.INFO)

    def log(self, event_type: str, level: str = 'INFO', **kwargs):
        entry = {
            'timestamp': datetime.utcnow().isoformat(),
            'level': level,
            'event_type': event_type,
            'ip_address': request.remote_addr if request else None,
            'endpoint': request.path if request else None,
            'http_method': request.method if request else None,
            **kwargs
        }
        getattr(self.logger, level.lower())(json.dumps(entry))

# Global logger instance
audit_log = StructuredLogger()

# Usage in routes:
audit_log.log('login_failure', level='WARNING',
    username=username, http_status=401,
    message='Invalid credentials')

audit_log.log('transfer_completed', level='INFO',
    user_id=current_user['user_id'],
    from_account=from_account,
    to_account=to_account,
    amount=amount)
```

### 2.4 Database Audit Trigger

```sql
-- audit_log table
CREATE TABLE audit_log (
    id          SERIAL PRIMARY KEY,
    event_time  TIMESTAMP NOT NULL DEFAULT NOW(),
    table_name  VARCHAR(50),
    operation   VARCHAR(10),  -- INSERT, UPDATE, DELETE
    record_id   INTEGER,
    changed_by  INTEGER REFERENCES users(id),
    old_values  JSONB,
    new_values  JSONB
);

-- Trigger on financial tables
CREATE OR REPLACE FUNCTION log_table_changes()
RETURNS TRIGGER AS $$
BEGIN
    INSERT INTO audit_log (table_name, operation, record_id, old_values, new_values)
    VALUES (
        TG_TABLE_NAME,
        TG_OP,
        COALESCE(NEW.id, OLD.id),
        CASE WHEN TG_OP = 'DELETE' OR TG_OP = 'UPDATE' THEN row_to_json(OLD) ELSE NULL END,
        CASE WHEN TG_OP = 'INSERT' OR TG_OP = 'UPDATE' THEN row_to_json(NEW) ELSE NULL END
    );
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER users_audit
    AFTER INSERT OR UPDATE OR DELETE ON users
    FOR EACH ROW EXECUTE FUNCTION log_table_changes();

CREATE TRIGGER transactions_audit
    AFTER INSERT ON transactions
    FOR EACH ROW EXECUTE FUNCTION log_table_changes();
```

---

## 3. Alerting Rules

### 3.1 Alert Thresholds

| Alert | Trigger | Severity | Response |
|---|---|---|---|
| Brute Force Login | > 5 failures from 1 IP in 1 min | 🔴 P1 | Auto-block IP, notify SOC |
| Account Takeover | Successful login after 4 failures | 🔴 P1 | Lock account, notify user |
| Admin Panel Access (off-hours) | Admin login 10pm–6am | 🟠 P2 | Alert SOC, verify with admin |
| Large Transfer | Single transfer > $5,000 | 🟠 P2 | Flag for review, notify user |
| Negative Transfer Attempt | `amount < 0` in request | 🔴 P1 | Block + alert, log attacker IP |
| IDOR Attempt | Token user ≠ requested account | 🟠 P2 | Block + log |
| SQL Injection Detected | WAF SQLi rule triggered | 🔴 P1 | Block IP, alert SOC |
| Password Reset Spike | > 10 reset requests in 5 min | 🟠 P2 | Rate limit, alert SOC |
| New Admin Created | POST /admin/create_admin success | 🟠 P2 | Alert all existing admins |
| Deleted Account | DELETE user action | 🟡 P3 | Log with actor, timestamp |
| Rate Limit Triggered | 429 from same IP > 20 times/hour | 🟡 P3 | Monitor, flag for review |
| API Error Spike | >50 5xx errors in 1 min | 🟠 P2 | Alert engineering on-call |

### 3.2 ELK Stack / Alerting Setup

**Stack**: Elasticsearch + Logstash + Kibana (ELK) with Elastalert

```yaml
# elastalert/rules/brute_force.yaml
name: Brute Force Login Detection
type: frequency
index: sivba-logs-*
num_events: 5
timeframe:
  minutes: 1
filter:
  - term:
      event_type: login_failure
query_key: ip_address
alert:
  - slack
slack_webhook_url: "https://hooks.slack.com/services/..."
alert_text: |
  🚨 BRUTE FORCE DETECTED
  IP: {0}
  Failed logins in 1 min: {1}
  Time: {2}
alert_text_args:
  - ip_address
  - num_hits
  - "@timestamp"
```

---

## 4. Severity Classification

| Level | Name | Description | Response Time | Examples |
|---|---|---|---|---|
| P1 | **Critical** | Active attack or breach in progress | **Immediate** (< 15 min) | SQL injection, account takeover, data breach |
| P2 | **High** | Potential attack or policy violation | < 1 hour | Brute force, large unauthorized transfer |
| P3 | **Medium** | Suspicious activity, non-critical | < 4 hours | Rate limit triggers, off-hours logins |
| P4 | **Low** | Informational or minor anomaly | < 24 hours | Single failed login, minor config drift |

---

## 5. Incident Response Playbooks

### PLAYBOOK-01: Account Takeover

**Trigger**: Login from new device/IP after repeated failures, or suspicious transaction after login

**Steps**:
1. **Detect** — Alert triggered: `account_takeover` event
2. **Contain** (< 5 min)
   - Immediately revoke all active tokens for the affected user (Redis blocklist)
   - Lock the account: `UPDATE users SET locked=TRUE WHERE id=<id>`
3. **Investigate** (< 30 min)
   - Review all recent login events for the account
   - Check all transactions since last known legitimate login
   - Identify attack IP → check threat intel (AbuseIPDB, VirusTotal)
4. **Notify**
   - Email user: "Suspicious login detected. Your account has been locked for your protection."
   - Escalate to security team if transactions were made
5. **Remediate**
   - Force password reset with verified channel (registered email/SMS)
   - Require MFA enrollment before re-enabling
   - Block attacker IP at firewall level
6. **Post-Incident**
   - Document timeline and root cause
   - If data exfiltrated → trigger PLAYBOOK-03 (Data Breach)

---

### PLAYBOOK-02: SQL Injection Attack

**Trigger**: WAF logs `id:1001` (SQLi rule), or suspicious patterns in DB error logs

**Steps**:
1. **Detect** — ModSecurity WAF fires `detectSQLi` rule; Elastalert notifies SOC
2. **Contain** (< 5 min)
   - ModSecurity automatically blocks the request (WAF in `SecRuleEngine On` mode)
   - Block attacker IP at iptables level for 24 hours
   - ```bash
     iptables -I INPUT -s <ATTACKER_IP> -j DROP
     ```
3. **Investigate** (< 1 hour)
   - Review WAF logs for all requests from attacker IP
   - Check DB error logs for any successful injection attempts
   - Check if the attacked endpoint is parameterized
4. **Assess Impact**
   - Were any records returned/modified before WAF blocked?
   - Review audit_log table for any unauthorized DB changes
5. **Remediate**
   - If endpoint had f-string SQL: emergency patch with parameterized query
   - Deploy patch, restart Flask app
   - Remove attacker IP block after 7 days (with monitoring)
6. **Post-Incident**
   - Code audit of all similar endpoints
   - Add SQLi-specific test cases to test suite

---

### PLAYBOOK-03: Data Breach

**Trigger**: Evidence of unauthorized data access (e.g., `/debug/users` scraped, mass IDOR, DB dump)

**Steps**:
1. **Detect** — Anomalous data volume exfiltrated, or explicit breach evidence
2. **Contain** (Immediate)
   - Take API offline (`flask stop` or Nginx return 503) if breach is active
   - Revoke ALL active tokens (flush Redis token store)
   - Isolate DB server from network
3. **Notify Legal / DPO** (within 1 hour of detection)
   - Brief legal team on scope
   - Prepare for GDPR 72-hour breach notification to supervisory authority
4. **Investigate** (< 4 hours)
   - Identify what data was accessed (user records, transactions, cards)
   - Quantify affected users
   - Identify attack vector
5. **Notify Affected Users** (within 72 hours — GDPR Art. 33)
   - Email affected users: what data was accessed, what action to take
6. **Regulatory Notification**
   - GDPR: Notify Data Protection Authority within 72 hours
   - PCI DSS: Notify card brands (Visa/Mastercard) and acquiring bank
7. **Remediate**
   - Fix the vulnerability that enabled the breach
   - Force password reset for all users
   - Re-issue all virtual cards if card data was compromised
8. **Post-Incident**
   - Full third-party forensic investigation
   - Prepare post-incident report (timeline, root cause, improvements)

---

### PLAYBOOK-04: Unauthorized Admin Access

**Trigger**: Admin panel accessed by non-admin user, or admin account created unexpectedly

**Steps**:
1. **Detect** — Alert: `role_escalation_attempt` or `admin_created` event
2. **Contain** (< 5 min)
   - Revoke token of compromised account
   - Disable any newly created admin accounts
3. **Investigate**
   - Review how admin access was gained (JWT forgery? Mass assignment? Stolen token?)
   - Review all actions taken by the unauthorized admin account
4. **Remediate**
   - Fix the specific escalation vulnerability
   - Audit all admin actions taken during the window
   - Reverse any unauthorized changes (deleted accounts, approved loans)
5. **Post-Incident**
   - Conduct access review of all admin accounts
   - Implement stricter admin IP allowlisting

---

## 6. Monitoring Architecture

```
Mobile App / Backend API
         │
         ▼ (structured JSON logs)
    Logstash (log aggregation + parsing)
         │
         ▼
  Elasticsearch (index storage + search)
         │
    ┌────┴────┐
    │         │
  Kibana    Elastalert
(Dashboard) (Alert engine)
                │
      ┌─────────┼─────────┐
      │         │         │
   Slack      Email     PagerDuty
  (SOC #sec)  (CISO)   (On-call)
```

---

## 7. Key Performance Indicators (KPIs)

| KPI | Target |
|---|---|
| Mean Time to Detect (MTTD) | < 5 minutes for P1, < 30 min for P2 |
| Mean Time to Respond (MTTR) | < 15 minutes for P1, < 1 hour for P2 |
| Incident false positive rate | < 10% |
| Log retention period | 12 months (PCI DSS Req 10.7) |
| Audit log coverage | 100% of auth and financial events |
| Monitoring uptime | 99.9% |

---

*Next: Deliverable 8 — User Training Pack*
