package com.example.vulnerable;

import java.io.IOException;

/**
 * Intentionally vulnerable example for SAST scanner validation.
 * CWE-78: OS Command Injection leading to remote code execution.
 */
public class RemoteCodeExecutionExample {

    public void pingHost(String host) throws IOException {
        // Invoking a shell means metacharacters in `host` are interpreted, so a
        // value like "; rm -rf /" runs as a separate command. exec(String) alone
        // would not do this: it tokenizes on whitespace and never spawns a shell.
        String command = "ping -c 1 " + host;
        Runtime.getRuntime().exec(new String[] {"/bin/sh", "-c", command});
    }
}
