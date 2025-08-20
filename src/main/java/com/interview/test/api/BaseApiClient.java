package com.interview.test.api;

import com.interview.test.config.ConfigurationManager;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * Base API Client with common configuration and utilities
 */
public abstract class BaseApiClient {

    protected static final Logger logger = LoggerFactory.getLogger(BaseApiClient.class);
    protected static final ConfigurationManager config = ConfigurationManager.getInstance();

    static {
        // Configure RestAssured globally
        RestAssured.baseURI = config.getBaseUrl();
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    /**
     * Creates a request specification with common configuration
     */
    protected RequestSpecification createRequestSpec() {
        RequestSpecBuilder builder = new RequestSpecBuilder()
                .setContentType(ContentType.JSON)
                .setAccept(ContentType.JSON)
                .setRelaxedHTTPSValidation()
                .addHeader("User-Agent", "PlayerAPI-TestFramework/1.0");

        // Add timeout configuration
        builder.addRequestSpecification(
                RestAssured.given()
                        .config(RestAssured.config().httpClient(
                                RestAssured.config().getHttpClientConfig()
                                        .setParam("http.connection.timeout", config.getRequestTimeout())
                                        .setParam("http.socket.timeout", config.getRequestTimeout())
                        ))
        );

        // Add logging if enabled
        if (config.isLoggingEnabled()) {
            builder.addFilter(new RequestLoggingFilter())
                    .addFilter(new ResponseLoggingFilter());
        }

        // Add Allure reporting if enabled
        if (config.isAllureEnabled()) {
            builder.addFilter(new AllureRestAssured());
        }

        return builder.build();
    }

    /**
     * Creates a response specification for successful responses
     */
    protected ResponseSpecification createSuccessResponseSpec() {
        return new ResponseSpecBuilder()
                .expectResponseTime(org.hamcrest.Matchers.lessThan(
                        TimeUnit.MILLISECONDS.convert(config.getRequestTimeout(), TimeUnit.MILLISECONDS)))
                .build();
    }

    /**
     * Creates a response specification for error responses
     */
    protected ResponseSpecification createErrorResponseSpec(int expectedStatusCode) {
        return new ResponseSpecBuilder()
                .expectStatusCode(expectedStatusCode)
                .expectResponseTime(org.hamcrest.Matchers.lessThan(
                        TimeUnit.MILLISECONDS.convert(config.getRequestTimeout(), TimeUnit.MILLISECONDS)))
                .build();
    }

    /**
     * Logs API operation details
     */
    protected void logOperation(String operation, String endpoint) {
        logger.info("Executing API operation: {} on endpoint: {}", operation, endpoint);
    }

    /**
     * Logs API operation result
     */
    protected void logResult(String operation, int statusCode, long responseTime) {
        logger.info("API operation: {} completed with status: {} in {}ms",
                operation, statusCode, responseTime);
    }
}