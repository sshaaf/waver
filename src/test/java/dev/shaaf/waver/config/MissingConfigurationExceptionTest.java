package dev.shaaf.waver.config;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MissingConfigurationExceptionTest {

    private static final String TEST_MESSAGE = "Test configuration error message";

    @Test
    void constructor_setsMessage() {
        // Create the exception with a test message
        MissingConfigurationException exception = new MissingConfigurationException(TEST_MESSAGE);
        
        // Verify the message is set correctly
        assertEquals(TEST_MESSAGE, exception.getMessage());
    }
    
    @Test
    void exceptionType_extendsIllegalStateException() {
        // Create the exception
        MissingConfigurationException exception = new MissingConfigurationException(TEST_MESSAGE);
        
        // Verify it's an instance of IllegalStateException
        assertTrue(exception instanceof IllegalStateException);
    }
}