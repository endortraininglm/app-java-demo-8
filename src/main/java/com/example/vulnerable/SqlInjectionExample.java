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
