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
