package com.interview.test.listeners;

import com.interview.test.config.ConfigurationManager;
import io.qameta.allure.Allure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


/**
 * Allure Environment Writer for adding environment information to reports
 */
class AllureEnvironmentWriter implements ITestListener {

    private static final Logger logger = LoggerFactory.getLogger(AllureEnvironmentWriter.class);
    private static final ConfigurationManager config = ConfigurationManager.getInstance();

    @Override
    public void onStart(ITestContext context) {
        if (config.isAllureEnabled()) {
            writeEnvironmentProperties();
        }
    }

    private void writeEnvironmentProperties() {
        try {
            StringBuilder environment = new StringBuilder();
            environment.append("Base URL=").append(config.getBaseUrl()).append("\n");
            environment.append("Environment=").append(config.getEnvironment()).append("\n");
            environment.append("Thread Count=").append(config.getThreadCount()).append("\n");
            environment.append("Request Timeout=").append(config.getRequestTimeout()).append(" ms\n");
            environment.append("Connection Timeout=").append(config.getConnectionTimeout()).append(" ms\n");
            environment.append("Java Version=").append(System.getProperty("java.version")).append("\n");
            environment.append("OS=").append(System.getProperty("os.name")).append(" ")
                    .append(System.getProperty("os.version")).append("\n");
            environment.append("User=").append(System.getProperty("user.name")).append("\n");
            environment.append("Execution Time=").append(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)).append("\n");

            Allure.addAttachment("Environment", environment.toString());
            logger.debug("Environment information added to Allure report");

        } catch (Exception e) {
            logger.error("Failed to write environment properties to Allure: {}", e.getMessage());
        }
    }
}
