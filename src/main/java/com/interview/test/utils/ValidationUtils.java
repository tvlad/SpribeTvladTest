package com.interview.test.utils;

import com.interview.test.config.ConfigurationManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Pattern;

/**
 * Utility class for data validation operations
 */
public class ValidationUtils {

    private static final Logger logger = LoggerFactory.getLogger(ValidationUtils.class);
    private static final ConfigurationManager config = ConfigurationManager.getInstance();

    // Validation patterns
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@([A-Za-z0-9.-]+\\.[A-Za-z]{2,})$"
    );

    private static final Pattern LOGIN_PATTERN = Pattern.compile("^[a-zA-Z0-9_-]{3,50}$");
    private static final Pattern SCREEN_NAME_PATTERN = Pattern.compile("^[a-zA-Z0-9_\\s-]{2,30}$");

    /**
     * Validates login format
     */
    public static boolean isValidLogin(String login) {
        if (login == null || login.trim().isEmpty()) {
            return false;
        }

        int minLength = Integer.parseInt(config.getProperty("validation.login.min.length", "3"));
        int maxLength = Integer.parseInt(config.getProperty("validation.login.max.length", "50"));

        return login.length() >= minLength &&
                login.length() <= maxLength &&
                LOGIN_PATTERN.matcher(login).matches();
    }

    /**
     * Validates password strength
     */
    public static boolean isValidPassword(String password) {
        if (password == null || password.trim().isEmpty()) {
            return false;
        }

        int minLength = Integer.parseInt(config.getProperty("validation.password.min.length", "6"));
        return password.length() >= minLength;
    }

    /**
     * Validates age range
     */
    public static boolean isValidAge(Integer age) {
        if (age == null) {
            return false;
        }

        int minAge = Integer.parseInt(config.getProperty("validation.age.min", "1"));
        int maxAge = Integer.parseInt(config.getProperty("validation.age.max", "150"));

        return age >= minAge && age <= maxAge;
    }

    /**
     * Validates screen name format
     */
    public static boolean isValidScreenName(String screenName) {
        if (screenName == null || screenName.trim().isEmpty()) {
            return false;
        }

        int minLength = Integer.parseInt(config.getProperty("validation.screenname.min.length", "2"));
        int maxLength = Integer.parseInt(config.getProperty("validation.screenname.max.length", "30"));

        return screenName.length() >= minLength &&
                screenName.length() <= maxLength &&
                SCREEN_NAME_PATTERN.matcher(screenName).matches();
    }

    /**
     * Validates role value
     */
    public static boolean isValidRole(String role) {
        if (role == null || role.trim().isEmpty()) {
            return false;
        }

        String validRoles = config.getProperty("roles.valid", "supervisor,admin,user,moderator");
        return java.util.Arrays.asList(validRoles.split(",")).contains(role);
    }

    /**
     * Validates gender value
     */
    public static boolean isValidGender(String gender) {
        if (gender == null || gender.trim().isEmpty()) {
            return false;
        }

        String validGenders = config.getProperty("genders.valid", "MALE,FEMALE,OTHER");
        return java.util.Arrays.asList(validGenders.split(",")).contains(gender);
    }
}