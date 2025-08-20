package com.interview.test.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.Properties;

/**
 * Configuration Manager for handling application properties and environment settings
 */
public class ConfigurationManager {

    private static final Logger logger = LoggerFactory.getLogger(ConfigurationManager.class);
    private static ConfigurationManager instance;
    private Properties properties;

    private static final String CONFIG_FILE = "application.properties";
    private static final String DEFAULT_BASE_URL = "http://3.68.165.45";
    private static final String DEFAULT_THREAD_COUNT = "3";
    private static final String DEFAULT_TIMEOUT = "30000";
    private static final String DEFAULT_ENVIRONMENT = "TEST";

    private ConfigurationManager() {
        loadProperties();
    }

    public static synchronized ConfigurationManager getInstance() {
        if (instance == null) {
            instance = new ConfigurationManager();
        }
        return instance;
    }

    private void loadProperties() {
        properties = new Properties();

        // Load from classpath
        try (InputStream input = getClass().getClassLoader().getResourceAsStream(CONFIG_FILE)) {
            if (input != null) {
                Objects.requireNonNull(properties).load(input);
                logger.info("Configuration loaded from {}", CONFIG_FILE);
            } else {
                logger.warn("Configuration file {} not found, using defaults", CONFIG_FILE);
            }
        } catch (IOException e) {
            logger.error("Error loading configuration: {}", e.getMessage());
        }

        // Override with system properties if available (from Gradle)
        System.getProperties().forEach((key, value) -> {
            if (key.toString().startsWith("app.") || key.toString().startsWith("test.") || key.toString().startsWith("api.")) {
                properties.setProperty(key.toString(), value.toString());
                logger.debug("System property {} -> {}", key, value);
            }
        });

        // Override with environment variables
        System.getenv().forEach((key, value) -> {
            if (key.startsWith("TEST_")) {
                String propKey = key.toLowerCase().replace("test_", "").replace("_", ".");
                properties.setProperty(propKey, value);
                logger.debug("Environment variable {} -> {}", key, propKey);
            }
        });
    }

    public String getBaseUrl() {
        return getProperty("app.base.url", DEFAULT_BASE_URL);
    }

    public int getThreadCount() {
        return Integer.parseInt(getProperty("test.thread.count", DEFAULT_THREAD_COUNT));
    }

    public int getRequestTimeout() {
        return Integer.parseInt(getProperty("api.request.timeout", DEFAULT_TIMEOUT));
    }

    public String getEnvironment() {
        return getProperty("test.environment", DEFAULT_ENVIRONMENT);
    }

    public int getConnectionTimeout() {
        return Integer.parseInt(getProperty("api.connection.timeout", "10000"));
    }

    public int getSocketTimeout() {
        return Integer.parseInt(getProperty("api.socket.timeout", DEFAULT_TIMEOUT));
    }

    public int getRetryCount() {
        return Integer.parseInt(getProperty("api.retry.count", "3"));
    }

    public int getRetryDelay() {
        return Integer.parseInt(getProperty("api.retry.delay", "1000"));
    }

    public String getValidEditor() {
        return getProperty("test.data.valid.editor", "supervisor");
    }

    public String getAdminEditor() {
        return getProperty("test.data.admin.editor", "admin");
    }

    public String getInvalidEditor() {
        return getProperty("test.data.invalid.editor", "invalid_user");
    }

    public boolean isLoggingEnabled() {
        return Boolean.parseBoolean(getProperty("test.logging.enabled", "true"));
    }

    public boolean isAllureEnabled() {
        return Boolean.parseBoolean(getProperty("test.allure.enabled", "true"));
    }

    public String getProperty(String key) {
        return properties.getProperty(key);
    }

    public String getProperty(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }

    public void logConfiguration() {
        logger.info("=== Test Configuration ===");
        logger.info("Base URL: {}", getBaseUrl());
        logger.info("Thread Count: {}", getThreadCount());
        logger.info("Request Timeout: {}ms", getRequestTimeout());
        logger.info("Connection Timeout: {}ms", getConnectionTimeout());
        logger.info("Socket Timeout: {}ms", getSocketTimeout());
        logger.info("Environment: {}", getEnvironment());
        logger.info("Logging Enabled: {}", isLoggingEnabled());
        logger.info("Allure Enabled: {}", isAllureEnabled());
        logger.info("Retry Count: {}", getRetryCount());
        logger.info("Retry Delay: {}ms", getRetryDelay());
        logger.info("Valid Editor: {}", getValidEditor());
        logger.info("Admin Editor: {}", getAdminEditor());
        logger.info("========================");
    }
}