package com.interview.test.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.qameta.allure.Allure;
import io.restassured.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.Map;

/**
 * Utility class for API-related operations and validations
 */
public class ApiUtils {

    private static final Logger logger = LoggerFactory.getLogger(ApiUtils.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Adds response details to Allure report
     */
    public static void attachResponseToAllure(Response response, String operationName) {
        try {
            StringBuilder responseInfo = new StringBuilder();
            responseInfo.append("Operation: ").append(operationName).append("\n");
            responseInfo.append("Status Code: ").append(response.getStatusCode()).append("\n");
            responseInfo.append("Response Time: ").append(response.getTime()).append(" ms\n");
            responseInfo.append("Content Type: ").append(response.getContentType()).append("\n\n");
            responseInfo.append("Headers:\n");

            response.getHeaders().forEach(header ->
                    responseInfo.append(header.getName()).append(": ").append(header.getValue()).append("\n")
            );

            responseInfo.append("\nResponse Body:\n");
            responseInfo.append(response.getBody().asPrettyString());

            Allure.addAttachment("API Response - " + operationName,
                    new ByteArrayInputStream(responseInfo.toString().getBytes()).toString(),
                    "text/plain");

        } catch (Exception e) {
            logger.warn("Failed to attach response to Allure: {}", e.getMessage());
        }
    }

    /**
     * Validates JSON response structure
     */
    public static boolean isValidJson(String jsonString) {
        try {
            objectMapper.readTree(jsonString);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Extracts specific field from JSON response
     */
    public static String extractFieldFromJson(Response response, String fieldPath) {
        try {
            JsonNode jsonNode = objectMapper.readTree(response.getBody().asString());
            String[] pathParts = fieldPath.split("\\.");

            JsonNode currentNode = jsonNode;
            for (String part : pathParts) {
                if (currentNode.has(part)) {
                    currentNode = currentNode.get(part);
                } else {
                    return null;
                }
            }

            return currentNode.asText();
        } catch (Exception e) {
            logger.error("Failed to extract field {} from JSON: {}", fieldPath, e.getMessage());
            return null;
        }
    }

    /**
     * Compares two JSON objects ignoring specified fields
     */
    public static boolean compareJsonIgnoringFields(String json1, String json2, List<String> fieldsToIgnore) {
        try {
            JsonNode node1 = objectMapper.readTree(json1);
            JsonNode node2 = objectMapper.readTree(json2);

            // Remove ignored fields
            if (fieldsToIgnore != null) {
                for (String field : fieldsToIgnore) {
                    removeField(node1, field);
                    removeField(node2, field);
                }
            }

            return node1.equals(node2);
        } catch (Exception e) {
            logger.error("Failed to compare JSON objects: {}", e.getMessage());
            return false;
        }
    }

    private static void removeField(JsonNode node, String fieldName) {
        if (node.isObject() && node.has(fieldName)) {
            ((com.fasterxml.jackson.databind.node.ObjectNode) node).remove(fieldName);
        }
    }

    /**
     * Validates response headers
     */
    public static boolean hasHeader(Response response, String headerName) {
        return response.getHeaders().hasHeaderWithName(headerName);
    }

    /**
     * Gets header value
     */
    public static String getHeaderValue(Response response, String headerName) {
        return response.getHeader(headerName);
    }

    /**
     * Validates response time is within acceptable range
     */
    public static boolean isResponseTimeAcceptable(Response response, long maxTimeMs) {
        return response.getTime() <= maxTimeMs;
    }

    /**
     * Logs API call details
     */
    public static void logApiCall(String method, String endpoint, int statusCode, long responseTime) {
        logger.info("API Call - Method: {}, Endpoint: {}, Status: {}, Time: {}ms",
                method, endpoint, statusCode, responseTime);
    }

    /**
     * Creates a map for error analysis
     */
    public static Map<String, Object> createErrorAnalysis(Response response) {
        return Map.of(
                "statusCode", response.getStatusCode(),
                "responseTime", response.getTime(),
                "contentType", response.getContentType() != null ? response.getContentType() : "unknown",
                "bodySize", response.getBody().asString().length(),
                "hasErrorBody", !response.getBody().asString().trim().isEmpty()
        );
    }
}