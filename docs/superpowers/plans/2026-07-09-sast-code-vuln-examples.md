# SAST Code Vulnerability Examples Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Add three intentionally vulnerable Java classes (SQL Injection, Auth Bypass, Remote Code Execution) to `app-java-demo-8` so an AI SAST scanner has concrete source-level findings to detect, complementing the repo's existing dependency-level (SCA) vulnerabilities.

**Architecture:** New package `com.example.vulnerable` under `src/main/java/com/example/vulnerable/`, one class per vulnerability category. Each class is self-contained, uses only JDK-native APIs (no new Maven dependencies), and carries a header comment naming its CWE and stating it is an intentional example for SAST validation.

**Tech Stack:** Java 8, Maven (existing `pom.xml`, unmodified). No test framework is present in this repo and none is added — verification is `mvn compile` plus a direct grep check that the intended vulnerable sink line is present in each file (this matches the Test Plan in `docs/superpowers/specs/2026-07-09-sast-code-vuln-examples-design.md`).

## Global Constraints

- No new Maven dependencies — every example must compile using only JDK-native APIs plus what's already declared in `pom.xml`.
- Exactly one example per category (3 total): SQL Injection, Auth Bypass, RCE.
- Each new file lives under package `com.example.vulnerable`.
- Each file's header comment must name the CWE id(s) and state it's an intentional example for SAST scanner validation.
- Do not modify `App.java` or `pom.xml`.
- Code must be statically analyzable; it does not need to run end-to-end (no DB driver, no servlet container, no live network required).

---

### Task 1: SQL Injection example

**Files:**
- Create: `src/main/java/com/example/vulnerable/SqlInjectionExample.java`

**Interfaces:**
- Consumes: nothing from other tasks.
- Produces: `com.example.vulnerable.SqlInjectionExample.findUserByName(Connection conn, String username)` — not consumed by any other task in this plan; each example class is independent.

- [ ] **Step 1: Create the vulnerable class**

Create `src/main/java/com/example/vulnerable/SqlInjectionExample.java`:

```java
package com.example.vulnerable;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Intentionally vulnerable example for SAST scanner validation.
 * CWE-89: SQL Injection via unsanitized string concatenation into a SQL query.
 */
public class SqlInjectionExample {

    public List<String> findUserByName(Connection conn, String username) throws SQLException {
        List<String> results = new ArrayList<>();
        String query = "SELECT * FROM users WHERE username = '" + username + "'";
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(query);
        while (rs.next()) {
            results.add(rs.getString("username"));
        }
        return results;
    }
}
```

- [ ] **Step 2: Compile and verify success**

Run: `mvn -q compile`
Expected: no output, exit code 0 (Maven's `-q` flag suppresses output on success).

- [ ] **Step 3: Verify the vulnerable sink is present**

Run: `grep -n "executeQuery(query)" src/main/java/com/example/vulnerable/SqlInjectionExample.java`
Expected: one matching line printed (confirms the concatenated query reaches `executeQuery` unparameterized).

- [ ] **Step 4: Commit**

```bash
git add src/main/java/com/example/vulnerable/SqlInjectionExample.java
git commit -m "Add CWE-89 SQL injection example for SAST validation"
```

---

### Task 2: Auth Bypass example

**Files:**
- Create: `src/main/java/com/example/vulnerable/AuthBypassExample.java`

**Interfaces:**
- Consumes: nothing from other tasks.
- Produces: `com.example.vulnerable.AuthBypassExample.login(String username, String password)` — not consumed by any other task in this plan.

- [ ] **Step 1: Create the vulnerable class**

Create `src/main/java/com/example/vulnerable/AuthBypassExample.java`:

```java
package com.example.vulnerable;

/**
 * Intentionally vulnerable example for SAST scanner validation.
 * CWE-288 / CWE-798: Authentication bypass via a hard-coded backdoor credential.
 */
public class AuthBypassExample {

    public boolean login(String username, String password) {
        if ("svc-maint".equals(username)) {
            // Backdoor: bypasses the real credential check below.
            return true;
        }
        return realAuthCheck(username, password);
    }

    private boolean realAuthCheck(String username, String password) {
        return "admin".equals(username) && "correct-password".equals(password);
    }
}
```

- [ ] **Step 2: Compile and verify success**

Run: `mvn -q compile`
Expected: no output, exit code 0.

- [ ] **Step 3: Verify the backdoor branch is present**

Run: `grep -n "svc-maint" src/main/java/com/example/vulnerable/AuthBypassExample.java`
Expected: one matching line printed (confirms the hard-coded backdoor username is still in the file).

- [ ] **Step 4: Commit**

```bash
git add src/main/java/com/example/vulnerable/AuthBypassExample.java
git commit -m "Add CWE-288/CWE-798 auth bypass example for SAST validation"
```

---

### Task 3: Remote Code Execution example

**Files:**
- Create: `src/main/java/com/example/vulnerable/RemoteCodeExecutionExample.java`

**Interfaces:**
- Consumes: nothing from other tasks.
- Produces: `com.example.vulnerable.RemoteCodeExecutionExample.pingHost(String host)` — not consumed by any other task in this plan.

- [ ] **Step 1: Create the vulnerable class**

Create `src/main/java/com/example/vulnerable/RemoteCodeExecutionExample.java`:

```java
package com.example.vulnerable;

import java.io.IOException;

/**
 * Intentionally vulnerable example for SAST scanner validation.
 * CWE-78: OS Command Injection leading to remote code execution.
 */
public class RemoteCodeExecutionExample {

    public void pingHost(String host) throws IOException {
        Runtime.getRuntime().exec("ping -c 1 " + host);
    }
}
```

- [ ] **Step 2: Compile and verify success**

Run: `mvn -q compile`
Expected: no output, exit code 0.

- [ ] **Step 3: Verify the vulnerable sink is present**

Run: `grep -n "Runtime.getRuntime().exec" src/main/java/com/example/vulnerable/RemoteCodeExecutionExample.java`
Expected: one matching line printed (confirms unsanitized `host` reaches `exec` via string concatenation).

- [ ] **Step 4: Commit**

```bash
git add src/main/java/com/example/vulnerable/RemoteCodeExecutionExample.java
git commit -m "Add CWE-78 RCE (command injection) example for SAST validation"
```

---

### Task 4: Full build verification

**Files:**
- None created or modified — this task only verifies the combined result of Tasks 1-3.

**Interfaces:**
- Consumes: all three files produced by Tasks 1-3.
- Produces: nothing further.

- [ ] **Step 1: Clean build the whole project**

Run: `mvn -q clean compile`
Expected: no output, exit code 0 (confirms all three new classes compile together with the existing `App.java` and no dependency changes were introduced).

- [ ] **Step 2: Confirm exactly three new files exist and nothing else changed**

Run: `git status --short`
Expected: empty output (everything already committed in Tasks 1-3; this just confirms no stray uncommitted changes).

Run: `git diff --stat 2b5d461..HEAD -- pom.xml src/main/java/com/example/App.java`
Expected: empty output (confirms `pom.xml` and `App.java` were not touched by this plan).

- [ ] **Step 3: List the new files for a final sanity check**

Run: `git log --oneline --name-only -3 -- src/main/java/com/example/vulnerable/`
Expected: three commits listed, each touching exactly one file under `src/main/java/com/example/vulnerable/`.
