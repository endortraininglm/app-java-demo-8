package com.example;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.util.StringUtils;
import org.apache.commons.collections.CollectionUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.opensymphony.xwork2.ActionSupport;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * Sample Java application with vulnerable dependencies
 * This application is intended for demonstration purposes only
 */
public class App extends ActionSupport {
    private static final Logger logger = LogManager.getLogger(App.class);

    public static void main(String[] args) {
        logger.info("Starting vulnerable application");
        
        // Using Log4j (CVE-2021-44228)
        logger.info("System username: ${java:os}");
        
        // Using Spring Framework
        String text = "Hello, World!";
        logger.info("Using Spring StringUtils: {}", StringUtils.capitalize(text));
        
        // Using Apache Commons Collections
        List<String> list = new ArrayList<>();
        list.add("Item 1");
        list.add("Item 2");
        logger.info("List is empty: {}", CollectionUtils.isEmpty(list));
        
        // Using Jackson Databind
        try {
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> data = new HashMap<>();
            data.put("name", "John");
            data.put("age", 30);
            String json = mapper.writeValueAsString(data);
            logger.info("JSON: {}", json);
            
            // Vulnerable deserialization
            mapper.enableDefaultTyping();
            String vulnerableJson = "{\"@class\":\"java.util.HashMap\",\"name\":\"John\",\"age\":30}";
            Map<String, Object> result = mapper.readValue(vulnerableJson, Map.class);
            logger.info("Deserialized: {}", result);
        } catch (Exception e) {
            logger.error("Error processing JSON", e);
        }
        
        logger.info("Application completed");
    }
    
    // Struts 2 action method (vulnerable to OGNL injection)
    public String execute() {
        logger.info("Executing Struts action");
        return SUCCESS;
    }
}