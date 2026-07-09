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
