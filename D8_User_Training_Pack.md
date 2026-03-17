# Deliverable 8: User Training Pack
## SIVBA — Vulnerable Bank Mobile App

**Project**: SIVBA Security Hardening  
**Document**: User Security Awareness Training Pack  
**Version**: 1.0  
**Date**: March 2026  
**Audience**: All VulnerableBank App Users

---

## Welcome

This training pack has been created to help you stay safe while using the **VulnerableBank** mobile application. By completing this training, you will learn how to protect your account, recognize threats, and report suspicious activity.

> ⏱️ **Estimated reading time**: 15 minutes  
> 📱 **Applies to**: VulnerableBank Android App users

---

## Module 1: Security Awareness Basics

### 1.1 Why Your Account Security Matters

Your VulnerableBank app contains access to your:
- Account balance and funds
- Transaction history
- Virtual bank cards
- Personal information

If a criminal gains access to your account, they can **steal your money, steal your identity, and make unauthorized transactions**. This training helps you prevent that.

---

### 1.2 Strong Password Guidelines

✅ **DO use a strong password:**

| Requirement | Example |
|---|---|
| Minimum 12 characters | `BlueSky#2024!Bank` |
| Mix of uppercase + lowercase | `MyBank@Secure99` |
| Include numbers and symbols | `H0rse!Battery&7` |
| Unique to this app (never reuse) | Use a password manager |

❌ **AVOID these common mistakes:**

| Bad Practice | Why It's Risky |
|---|---|
| Using your name/birthday | Easily guessed |
| `password`, `123456`, `qwerty` | First thing attackers try |
| Reusing the same password elsewhere | One breach → all accounts compromised |
| Simple patterns like `Password1!` | Password crackers know these |

**💡 Tip**: Use a free password manager like **Bitwarden** or **1Password** to generate and store unique passwords securely.

---

### 1.3 Protecting Your Device

Your phone is the key to your bank account. Protect it:

☑️ **Always**:
- Set a strong PIN/password/pattern lock (minimum 6 digits)
- Enable biometric login (fingerprint or Face ID)
- Keep your Android OS updated
- Enable "Find My Device" in case of loss

❌ **Never**:
- Leave your phone unlocked in public
- Root or jailbreak your device (this disables security protections)
- Install apps from unknown sources (non-Play Store APKs)
- Use public/untrusted Wi-Fi without a VPN for banking

---

### 1.4 Understanding App Permissions

The VulnerableBank app should only ask for:
- ✅ Internet access (to connect to the API)
- ✅ Biometric / fingerprint sensor (optional, for biometric login)

🚨 **Be suspicious** if the app asks for:
- Camera (not needed for this banking app)
- Contacts
- Location
- SMS/Call access

If you see unexpected permission requests, **do not approve** — contact support immediately.

---

## Module 2: Recognizing Phishing Attacks

### 2.1 What Is Phishing?

Phishing is when a criminal impersonates a trusted organization — like your bank — to trick you into giving up your credentials, personal information, or money.

**Phishing channels include:**
- SMS (called "Smishing")
- Email
- Fake websites that look like the real app
- Phone calls (called "Vishing")
- Fake customer support on social media

---

### 2.2 Real Phishing Examples — Learn to Spot Them

#### Example 1: Phishing SMS (Smishing)
```
FROM: VulnBank-Alert
"Your account has been suspended due to suspicious activity.
Verify your identity here: http://vulnb4nk-secure.ru/verify
or your account will be deleted."
```

🚨 **Red flags**:
- Fake URL (`vulnb4nk-secure.ru` instead of `vulnbank.org`)
- Creating urgency ("will be deleted")
- Unexpected message you didn't request
- Links to external websites

---

#### Example 2: Phishing Email
```
From: security@vuln-bank-alert.net
Subject: URGENT: Verify your account

Dear Customer,

We detected unusual login activity on your account.
Please click below to confirm your identity within 24 hours
or your account will be frozen.

[Click Here to Verify]
```

🚨 **Red flags**:
- Sender email domain doesn't match the real bank (`vuln-bank-alert.net` ≠ `vulnbank.org`)
- Creating fear/urgency
- Suspicious link (hover over it — where does it actually go?)
- Generic greeting ("Dear Customer" instead of your name)

---

### 2.3 The Phishing Red Flag Checklist

Before clicking any link or entering credentials, ask yourself:

| Question | Safe? |
|---|---|
| Do I recognize the sender? | ✅ / ❌ |
| Is the URL exactly `vulnbank.org`? | ✅ / ❌ |
| Was this message expected? | ✅ / ❌ |
| Is there urgency or a threat? | 🚨 Suspicious |
| Are they asking for my password / PIN? | 🚨 **NEVER share this** |
| Are there grammar / spelling errors? | 🚨 Suspicious |

> 🔑 **Golden Rule**: Your bank will **NEVER** ask you for your password, PIN, or OTP code over email, SMS, or phone.

---

### 2.4 What To Do If You Receive a Phishing Message

1. **Do NOT click** any links or attachments
2. **Do NOT reply** to the message
3. **Screenshot the message** for reporting
4. **Report it** to: `security@vulnbank.org`
5. **Delete the message**
6. If you already clicked or entered credentials: **change your password immediately** and **contact support**

---

## Module 3: Fraud Prevention

### 3.1 Common Banking Fraud Patterns

#### Fraud Type 1: Account Takeover
**How it works**: Criminal obtains your password (via phishing, data breach, or guessing), then logs in and transfers your money.

**Prevention**:
- Use a unique strong password
- Enable MFA (Multi-Factor Authentication) in the app
- Never share your OTP code with anyone
- Monitor your transaction history regularly

---

#### Fraud Type 2: Social Engineering Call ("Vishing")
**How it works**: Someone calls claiming to be bank support. They say your account is compromised and ask you to "verify" by sharing your OTP or transferring funds to a "safe account".

> ⚠️ **There is no such thing as a "safe account"** — this is always fraud.

**Prevention**:
- Hang up and call the bank back directly using the official number from the app/website
- Never share your:  OTP / PIN / Password / Full card number over the phone
- No bank employee will ever ask for these

---

#### Fraud Type 3: Fake Investment / Transfer Scam
**How it works**: A stranger (often via social media or messaging) offers a "guaranteed" high-return investment, asks you to transfer funds through the app.

**Prevention**:
- Never transfer money to people you have not met in person
- Verify investment opportunities through official channels
- If an offer sounds too good to be true, it is

---

#### Fraud Type 4: Unauthorized Transaction
**How it works**: Small test transactions appear on your account before a larger fraud.

**Prevention**:
- Check your transaction history **daily**
- Enable push notifications for every transaction
- Report any unrecognized transaction immediately (even small ones)

---

### 3.2 Protecting Your Virtual Cards

Your VulnerableBank virtual cards are tied to your account. To protect them:

✅ **Do**:
- Freeze your card in the app when not in use
- Set transaction limits for each card
- Use a unique card per online merchant (if feature is available)
- Regularly check card transaction history

❌ **Never**:
- Share your full card number + expiry + CVV with anyone
- Screenshot your card details and share the image
- Enter card details on websites without a 🔒 padlock and `https://` in the address bar

---

### 3.3 Recognizing a Secure Website

When banking online (not in the app):

| Check | Secure | Unsafe |
|---|---|---|
| URL prefix | `https://` ✅ | `http://` ❌ |
| Padlock icon | 🔒 Present ✅ | Missing ❌ |
| Domain name | `vulnbank.org` ✅ | `vu1nbank.org` ❌ |
| Extended Validation | Green company name ✅ | No EV info ⚠️ |

---

## Module 4: Using the App Safely

### 4.1 Safe Login Practices

✅ Always:
- Use biometric login when available
- Enable MFA (Multi-Factor Authentication) in app settings
- Log out when finished (don't just close the app)

❌ Never:
- Log in on someone else's phone
- Log in on a shared/public device
- Allow your browser or another app to save your banking password

---

### 4.2 Transaction Safety Tips

Before sending a transfer:

1. **Double-check the account number** — one wrong digit sends money to the wrong person
2. **Verify the recipient** — call them to confirm if it's a large amount
3. **Be alert to urgency** — legitimate requests are never "must send in the next 10 minutes"
4. **Review the amount** — confirm it matches what you intended

---

### 4.3 Keeping the App Updated

App updates often contain **security fixes**. Always:
- Keep the app updated to the latest version
- Only update from the **official Google Play Store**
- Enable automatic updates

---

## Module 5: Reporting Suspicious Activity

### When to Report

Report immediately if you notice:
- 🚨 A transaction you did not make
- 🚨 A login you did not initiate
- 🚨 Your password no longer works (possible takeover)
- 🚨 You receive a phishing message claiming to be the bank
- 🚨 Your card was charged for something you didn't buy

### How to Report

| Channel | Contact | When to Use |
|---|---|---|
| **In-App Report** | Settings → Report Issue | Any time — fastest |
| **Security Email** | `security@vulnbank.org` | Phishing, suspicious messages |
| **Phone Support** | Shown in app Settings | Account compromise, fraud |
| **Emergency** | Block card instantly in app | Unauthorized card use |

---

## Quick Reference Card

> 📋 **Print and keep this**

| Situation | Action |
|---|---|
| Received suspicious SMS/email | Do NOT click — report to `security@vulnbank.org` |
| Someone asks for your OTP | **Refuse and hang up** — report it |
| Unrecognized transaction | Report in-app immediately |
| Device lost/stolen | Log in from another device → revoke sessions |
| Forgot password | Use only the official "Forgot Password" in the app |
| Someone claims to be bank support | Hang up, call back on the official number |
| Suspicious investment offer | Do not send money — it is a scam |

---

## Training Completion Acknowledgment

By completing this training, you confirm that you:

- ✅ Understand the importance of strong, unique passwords
- ✅ Can recognize phishing attempts across SMS, email, and phone
- ✅ Know how to protect your virtual cards
- ✅ Understand how to report suspicious activity
- ✅ Commit to practicing safe banking habits

> 🔒 **Stay safe. Stay vigilant. When in doubt — report it.**  
> **security@vulnbank.org**

---

*End of User Training Pack — Deliverable 8 of 8*
