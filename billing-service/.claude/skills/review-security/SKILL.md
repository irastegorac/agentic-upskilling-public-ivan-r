---
name: review-security
description: Perform a focused security review of a Java class, checking for mutable
  secret fields, missing null checks, hardcoded credentials, improper input validation,
  and cryptographic misuse (weak algorithms, uncached keys). Use when the user asks
  to review security, check for vulnerabilities, audit a class, or run a security
  pass on Java code.
---

Perform a security review of the specified Java class.

## Steps

1. Read the target file.
2. Check each security layer below.
3. Before reporting, verify each finding — confirm the flagged line actually exhibits the vulnerability in context and is not a false positive.
4. Output each finding as:
   **[Severity]** Short title — `ClassName.java:line`
   - Problem: what is wrong
   - Fix: suggested code change
5. If no issues are found in a layer, explicitly state "No issues found."

## Security Layers to Check

### Mutable secret fields
- Are fields holding secrets, keys, or configuration declared `final`?
- Non-final fields can be mutated by subclasses or via reflection.

Example violation:
```java
private String secretKey; // missing final — can be mutated
```
Fix: `private final String secretKey;`

### Missing null checks
- Are values returned from external parsers, deserialization, or third-party APIs null-checked before use?
- Calling `.isEmpty()`, `.length()`, or any method on a potentially-null value causes NPE.

Example violation:
```java
if (claims.getSubject().isEmpty()) { ... } // NPE if getSubject() returns null
```
Fix: `if (subject == null || subject.isEmpty()) { ... }`

### Hardcoded secrets or magic number timeouts
- Are secrets, API keys, passwords, or expiry values hardcoded in the class?
- Magic number timeouts hide their unit (seconds? milliseconds?) and become stale.

Example violation:
```java
private static final int TOKEN_EXPIRY = 86400; // unit unclear
```
Fix: `private static final int TOKEN_EXPIRY_SECONDS = 86400;` and use `* 1000L` where milliseconds are needed.

### Cryptographic issues
- Is the signing key recomputed on every call instead of cached?
- Are deprecated or weak algorithms used?

Example violation:
```java
public Claims validate(String token) {
    Key key = new SecretKeySpec(...); // allocated on every call
```
Fix: compute the key once in the constructor and store as `private final Key signingKey`.

## Constraints

- Report every issue found, do not summarise or omit lower-severity findings.
- Do not modify the file — report only.
- If a finding requires context from another file (e.g. how the class is constructed), note that explicitly.
