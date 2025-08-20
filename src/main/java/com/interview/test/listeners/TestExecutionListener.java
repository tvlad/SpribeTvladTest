package com.interview.test.listeners;

import com.interview.test.config.ConfigurationManager;
import io.qameta.allure.Allure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.*;

import java.io.ByteArrayInputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * TestNG Execution Listener for test lifecycle management and reporting
 */
public class TestExecutionListener implements ITestListener, ISuiteListener, IInvokedMethodListener {

    private static final Logger logger = LoggerFactory.getLogger(TestExecutionListener.class);
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final ConfigurationManager config = ConfigurationManager.getInstance();

    private static final AtomicInteger totalTests = new AtomicInteger(0);
    private static final AtomicInteger passedTests = new AtomicInteger(0);
    private static final AtomicInteger failedTests = new AtomicInteger(0);
    private static final AtomicInteger skippedTests = new AtomicInteger(0);

    private long suiteStartTime;
    private long testStartTime;

    @Override
    public void onStart(ISuite suite) {
        suiteStartTime = System.currentTimeMillis();
        logger.info("=================================");
        logger.info("TEST SUITE STARTED: {}", suite.getName());
        logger.info("Start Time: {}", LocalDateTime.now().format(formatter));
        logger.info("=================================");

        // Log configuration
        config.logConfiguration();

        // Reset counters
        totalTests.set(0);
        passedTests.set(0);
        failedTests.set(0);
        skippedTests.set(0);
    }

    @Override
    public void onFinish(ISuite suite) {
        long duration = System.currentTimeMillis() - suiteStartTime;

        logger.info("=================================");
        logger.info("TEST SUITE FINISHED: {}", suite.getName());
        logger.info("End Time: {}", LocalDateTime.now().format(formatter));
        logger.info("Total Duration: {} ms ({} seconds)", duration, duration / 1000.0);
        logger.info("=================================");
        logger.info("TEST EXECUTION SUMMARY:");
        logger.info("Total Tests: {}", totalTests.get());
        logger.info("Passed: {}", passedTests.get());
        logger.info("Failed: {}", failedTests.get());
        logger.info("Skipped: {}", skippedTests.get());

        if (totalTests.get() > 0) {
            double passRate = (passedTests.get() * 100.0) / totalTests.get();
            logger.info("Pass Rate: {:.2f}%", passRate);
        }

        logger.info("=================================");

        // Add summary to Allure report
        if (config.isAllureEnabled()) {
            addExecutionSummaryToAllure(duration);
        }
    }

    @Override
    public void onTestStart(ITestResult result) {
        testStartTime = System.currentTimeMillis();
        totalTests.incrementAndGet();

        String testName = result.getMethod().getMethodName();
        String className = result.getTestClass().getName();

        logger.info("STARTING TEST: {}.{}", className, testName);

        // Add test description to Allure
        if (config.isAllureEnabled()) {
            Allure.getLifecycle().updateTestCase(testCase -> {
                testCase.setDescription("Test execution started at: " + LocalDateTime.now().format(formatter));
            });
        }
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        long duration = System.currentTimeMillis() - testStartTime;
        passedTests.incrementAndGet();

        String testName = result.getMethod().getMethodName();
        logger.info("PASSED: {} (Duration: {} ms)", testName, duration);

        if (config.isAllureEnabled()) {
            Allure.addAttachment("Test Duration", String.valueOf(duration) + " ms");
        }
    }

    @Override
    public void onTestFailure(ITestResult result) {
        long duration = System.currentTimeMillis() - testStartTime;
        failedTests.incrementAndGet();

        String testName = result.getMethod().getMethodName();
        Throwable throwable = result.getThrowable();

        logger.error("FAILED: {} (Duration: {} ms)", testName, duration);
        if (throwable != null) {
            logger.error("Failure reason: {}", throwable.getMessage());
            logger.error("Stack trace:", throwable);
        }

        if (config.isAllureEnabled()) {
            // Add failure details to Allure
            Allure.addAttachment("Test Duration", String.valueOf(duration) + " ms");
            Allure.addAttachment("Failure Reason", throwable != null ? throwable.getMessage() : "Unknown");

            if (throwable != null) {
                Allure.addAttachment("Stack Trace",
                        new ByteArrayInputStream(getStackTrace(throwable).getBytes()).toString(), "text/plain");
            }
        }
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        long duration = System.currentTimeMillis() - testStartTime;
        skippedTests.incrementAndGet();

        String testName = result.getMethod().getMethodName();
        Throwable throwable = result.getThrowable();

        logger.warn("SKIPPED: {} (Duration: {} ms)", testName, duration);
        if (throwable != null) {
            logger.warn("Skip reason: {}", throwable.getMessage());
        }

        if (config.isAllureEnabled()) {
            Allure.addAttachment("Test Duration", String.valueOf(duration) + " ms");
            if (throwable != null) {
                Allure.addAttachment("Skip Reason", throwable.getMessage());
            }
        }
    }

    @Override
    public void beforeInvocation(IInvokedMethod method, ITestResult testResult) {
        if (method.isTestMethod()) {
            String methodName = method.getTestMethod().getMethodName();
            String threadName = Thread.currentThread().getName();
            logger.debug("Executing test method: {} on thread: {}", methodName, threadName);
        }
    }

    @Override
    public void afterInvocation(IInvokedMethod method, ITestResult testResult) {
        if (method.isTestMethod()) {
            String methodName = method.getTestMethod().getMethodName();
            String threadName = Thread.currentThread().getName();
            String status = getTestStatus(testResult.getStatus());
            logger.debug("Completed test method: {} on thread: {} with status: {}",
                    methodName, threadName, status);
        }
    }

    private String getTestStatus(int status) {
        return switch (status) {
            case ITestResult.SUCCESS -> "PASSED";
            case ITestResult.FAILURE -> "FAILED";
            case ITestResult.SKIP -> "SKIPPED";
            default -> "UNKNOWN";
        };
    }

    private String getStackTrace(Throwable throwable) {
        StringBuilder sb = new StringBuilder();
        sb.append(throwable.toString()).append("\n");
        for (StackTraceElement element : throwable.getStackTrace()) {
            sb.append("\tat ").append(element.toString()).append("\n");
        }
        return sb.toString();
    }

    private void addExecutionSummaryToAllure(long duration) {
        StringBuilder summary = new StringBuilder();
        summary.append("TEST EXECUTION SUMMARY\n");
        summary.append("======================\n");
        summary.append("Total Tests: ").append(totalTests.get()).append("\n");
        summary.append("Passed: ").append(passedTests.get()).append("\n");
        summary.append("Failed: ").append(failedTests.get()).append("\n");
        summary.append("Skipped: ").append(skippedTests.get()).append("\n");
        summary.append("Total Duration: ").append(duration).append(" ms\n");

        if (totalTests.get() > 0) {
            double passRate = (passedTests.get() * 100.0) / totalTests.get();
            summary.append("Pass Rate: ").append(String.format("%.2f%%", passRate)).append("\n");
        }

        summary.append("Environment: ").append(config.getEnvironment()).append("\n");
        summary.append("Base URL: ").append(config.getBaseUrl()).append("\n");
        summary.append("Thread Count: ").append(config.getThreadCount()).append("\n");

        Allure.addAttachment("Execution Summary", summary.toString());
    }
}