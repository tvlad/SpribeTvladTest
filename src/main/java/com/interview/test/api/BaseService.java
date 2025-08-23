package com.interview.test.api;

import com.interview.test.config.ConfigurationManager;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;

/**
 * Base service class that provides common functionality for all API service classes
 * @param <T> the service type for method chaining
 */
public abstract class BaseService<T extends BaseService<T>> {

    protected final ConfigurationManager config = ConfigurationManager.getInstance();
    protected static final Logger logger = LoggerFactory.getLogger(BaseService.class);
    protected Response response;
    protected String editor;
    protected Integer expectedStatusCode;

    /**
     * Constructor with default values (for positive tests)
     */
    protected BaseService() {
        this.editor = getDefaultEditor();
        this.expectedStatusCode = getDefaultExpectedStatusCode();
    }

    /**
     * Constructor with custom editor (for negative tests with custom editor)
     */
    protected BaseService(String editor) {
        this.editor = editor;
        this.expectedStatusCode = getDefaultExpectedStatusCode();
    }

    /**
     * Constructor with custom editor and expected status code (for negative tests)
     */
    protected BaseService(String editor, Integer expectedStatusCode) {
        this.editor = editor;
        this.expectedStatusCode = expectedStatusCode;
    }

    /**
     * Each service should define its default editor
     */
    protected abstract String getDefaultEditor();

    /**
     * Each service should define its default expected status code
     */
    protected abstract Integer getDefaultExpectedStatusCode();

    /**
     * Each service should define its schema file path
     */
    protected abstract String getSchemaPath();

    /**
     * Generic JSON schema validation
     */
    @Step("Verify JSON schema compliance")
    public T verifyJsonSchema() {
        response.then()
                .assertThat()
                .body(matchesJsonSchemaInClasspath(getSchemaPath()));
        return (T) this;
    }

    /**
     * JSON schema validation with custom schema path
     */
    @Step("Verify JSON schema compliance against custom schema")
    public T verifyJsonSchema(String schemaPath) {
        response.then()
                .assertThat()
                .body(matchesJsonSchemaInClasspath(schemaPath));
        return (T) this;
    }

    /**
     * Generic status code verification with custom expected code
     */
    @Step("Verify status code is {expectedStatusCode}")
    public T verifyStatusCode(int expectedStatusCode) {
        response.then().statusCode(expectedStatusCode);
        return (T) this;
    }

    /**
     * Status code verification using service's expected status code
     */
    @Step("Verify status code matches expected")
    public T verifyStatusCode() {
        response.then().statusCode(this.expectedStatusCode);
        return (T) this;
    }

    /**
     * Validate response time against configured timeout
     */
    @Step("Verify response time is within timeout")
    public T verifyResponseTime() {
        if (response.getTime() >= config.getRequestTimeout()) {
            throw new AssertionError(String.format(
                    "Response time %d ms exceeded timeout %d ms",
                    response.getTime(),
                    config.getRequestTimeout()
            ));
        }
        return (T) this;
    }

    /**
     * Get the current response
     */
    public Response getResponse() {
        return response;
    }

    /**
     * Get the editor used for this service
     */
    public String getEditor() {
        return editor;
    }

    /**
     * Get the expected status code for this service
     */
    public Integer getExpectedStatusCode() {
        return expectedStatusCode;
    }
}