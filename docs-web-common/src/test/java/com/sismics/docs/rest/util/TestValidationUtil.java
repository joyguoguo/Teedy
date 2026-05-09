package com.sismics.docs.rest.util;

import com.sismics.rest.exception.ClientException;
import com.sismics.rest.util.ValidationUtil;
import jakarta.json.JsonObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Date;

/**
 * Test the validations.
 *
 * @author jtremeaux 
 */
public class TestValidationUtil {
    @Test
    public void testValidateRequired() throws Exception {
        ValidationUtil.validateRequired("value", "field");

        assertClientException("field must be set", () -> ValidationUtil.validateRequired(null, "field"));
    }

    @Test
    public void testValidateLength() throws Exception {
        Assertions.assertEquals("abc", ValidationUtil.validateLength("  abc  ", "field", 2, 4, false));
        Assertions.assertNull(ValidationUtil.validateLength(null, "field", 1, 5, true));
        Assertions.assertEquals("", ValidationUtil.validateLength("   ", "field", 1, 5, true));

        assertClientException("field must be set", () -> ValidationUtil.validateLength(null, "field", 1, 5, false));
        assertClientException("field must be more than 3 characters", () -> ValidationUtil.validateLength("ab", "field", 3, 5, false));
        assertClientException("field must be less than 3 characters", () -> ValidationUtil.validateLength("abcd", "field", 1, 3, false));
    }

    @Test
    public void testValidateStringNotBlank() throws Exception {
        Assertions.assertEquals("hello", ValidationUtil.validateStringNotBlank("  hello  ", "field"));

        assertClientException("field must be more than 1 characters", () -> ValidationUtil.validateStringNotBlank("   ", "field"));
    }

    @Test
    public void testValidateEmail() throws Exception {
        ValidationUtil.validateEmail("user@example.com", "email");

        assertClientException("email must be an email", () -> ValidationUtil.validateEmail("invalid-email", "email"));
    }

    @Test
    public void testValidateHttpUrl() throws Exception {
        ValidationUtil.validateHttpUrl("http://www.google.com", "url");
        ValidationUtil.validateHttpUrl("https://www.google.com", "url");
        Assertions.assertEquals("https://www.google.com", ValidationUtil.validateHttpUrl(" https://www.google.com ", "url"));

        assertClientException("url must be an HTTP(s) URL", () -> ValidationUtil.validateHttpUrl("ftp://www.google.com", "url"));
        assertClientException("url must be an HTTP(s) URL", () -> ValidationUtil.validateHttpUrl("http://", "url"));
    }

    @Test
    public void testValidateHexColor() throws Exception {
        ValidationUtil.validateHexColor("#A1B2C3", "color", false);
        ValidationUtil.validateHexColor(null, "color", true);

        assertClientException("color must be less than 7 characters", () -> ValidationUtil.validateHexColor("#1234567", "color", false));
    }

    @Test
    public void testValidateTagName() throws Exception {
        ValidationUtil.validateTagName("project_alpha");

        assertClientException("Spaces and colons are not allowed in tag name", () -> ValidationUtil.validateTagName("bad tag"));
        assertClientException("Spaces and colons are not allowed in tag name", () -> ValidationUtil.validateTagName("bad:tag"));
    }

    @Test
    public void testValidateAlphanumericAndUsername() throws Exception {
        ValidationUtil.validateAlphanumeric("Alpha_123", "code");
        ValidationUtil.validateUsername("user.name@example", "username");

        assertClientException("code must have only alphanumeric or underscore characters", () -> ValidationUtil.validateAlphanumeric("bad-code", "code"));
        assertClientException("username must have only alphanumeric, underscore characters or @ and .", () -> ValidationUtil.validateUsername("bad name", "username"));
    }

    @Test
    public void testValidateRegex() throws Exception {
        ValidationUtil.validateRegex("AB12", "token", "[A-Z]{2}[0-9]{2}");

        assertClientException("token must match [A-Z]{2}[0-9]{2}", () -> ValidationUtil.validateRegex("ab12", "token", "[A-Z]{2}[0-9]{2}"));
    }

    @Test
    public void testValidateNumbers() throws Exception {
        Assertions.assertEquals(Integer.valueOf(42), ValidationUtil.validateInteger("42", "count"));
        Assertions.assertEquals(Long.valueOf(9000000000L), ValidationUtil.validateLong("9000000000", "size"));

        assertClientException("count is not a number", () -> ValidationUtil.validateInteger("forty-two", "count"));
        assertClientException("size is not a number", () -> ValidationUtil.validateLong("nine", "size"));
    }

    @Test
    public void testValidateDate() throws Exception {
        Date date = ValidationUtil.validateDate("1714300000000", "date", false);
        Assertions.assertEquals(1714300000000L, date.getTime());
        Assertions.assertNull(ValidationUtil.validateDate(null, "date", true));

        assertClientException("date must be set", () -> ValidationUtil.validateDate("", "date", false));
        assertClientException("date must be a date", () -> ValidationUtil.validateDate("not-a-date", "date", false));
    }

    private static void assertClientException(String expectedMessage, ThrowingRunnable runnable) {
        try {
            runnable.run();
            Assertions.fail("Expected ClientException");
        } catch (ClientException e) {
            JsonObject entity = (JsonObject) e.getResponse().getEntity();
            Assertions.assertEquals(expectedMessage, entity.getString("message"));
        } catch (Exception e) {
            Assertions.fail("Expected ClientException but got: " + e);
        }
    }

    @FunctionalInterface
    private interface ThrowingRunnable {
        void run() throws Exception;
    }
}
