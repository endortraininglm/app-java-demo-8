# Add Source-Level Vulnerability Examples for AI SAST Validation

## Context

`app-java-demo-8` already carries intentionally vulnerable *dependencies*
(log4j-core 2.14.1, spring-core 4.3.19, commons-collections 3.2.1,
struts2-core 2.3.30, jackson-databind 2.9.9) for validating Endor's SCA
scanning, and has a working Endor scan trigger (`endor-release-branch-scan.yml`,
supports `scan_sast` input already, currently defaulted off).

This repo has no source-level vulnerable *code patterns* yet — everything
flagged so far comes from vulnerable library versions, not from code the
scanner would need to reason about via SAST/AI-SAST analysis. This spec adds
a small set of intentionally vulnerable classes covering three categories —
SQL Injection, Auth Bypass, and Remote Code Execution — to validate SAST
detection.

## Goal

- One canonical, unambiguous example per category (3 total), each isolated
  in its own class so scanner findings map cleanly to a single file/vuln.
- No new Maven dependencies — examples must compile with `mvn compile`
  using only JDK-native APIs plus whatever is already in `pom.xml`.
- Each example is clearly labeled as intentional via a header comment
  naming the CWE, matching the existing comment style in `pom.xml` /
  `App.java` (e.g. `// Log4j with known vulnerabilities (CVE-2021-44228)`).
- Code only needs to be statically analyzable, not necessarily runnable
  end-to-end (no DB driver, no servlet container, no live network calls
  required) — SAST operates on source, not execution.

## Design

New package `com.example.vulnerable` under `src/main/java/com/example/vulnerable/`,
three classes:

1. **`SqlInjectionExample.java`** — CWE-89. Method
   `findUserByName(Connection conn, String username)` builds a SQL query via
   string concatenation and executes it with `Statement.executeQuery`
   instead of a parameterized `PreparedStatement`. Uses only `java.sql.*`
   (JDK-native, no driver dependency needed to compile).

2. **`AuthBypassExample.java`** — CWE-288 / CWE-798. Method
   `login(String username, String password)` contains a hard-coded backdoor
   username (`"svc-maint"`) that returns `true` and skips the real
   credential check entirely — authentication bypass via an alternate path
   guarded by a hard-coded credential.

3. **`RemoteCodeExecutionExample.java`** — CWE-78 (OS Command Injection
   leading to RCE). Method `pingHost(String host)` concatenates unsanitized
   input directly into a command string passed to
   `Runtime.getRuntime().exec(...)` — the canonical pattern used in
   OWASP Benchmark / Juliet test suites for this CWE.

Each file's header comment states the CWE id and that the code is an
intentional example for SAST scanner validation, so nobody mistakes it for
an accidental real vulnerability.

No changes to `App.java` or `pom.xml`.

## Test plan

1. `mvn compile` succeeds with the three new classes added.
2. Manually confirm each class contains exactly the intended vulnerable
   sink (concatenated SQL string into `executeQuery`, hard-coded backdoor
   branch in `login`, concatenated input into `Runtime.exec`) and nothing
   else that could mask or dilute the pattern.
3. (Follow-up, outside this spec's scope) run an Endor scan with
   `scan_sast: true` against this branch and confirm all three findings are
   reported.

## Outcome

Three new files under `com.example.vulnerable`, each a minimal, clearly
labeled, single-purpose example of a distinct vulnerability class, ready to
serve as ground truth for AI SAST detection validation.
