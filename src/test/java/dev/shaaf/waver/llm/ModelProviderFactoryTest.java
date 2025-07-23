package dev.shaaf.waver.llm;

import dev.shaaf.waver.config.MissingConfigurationException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ModelProviderFactoryTest {

    private static final String TEST_API_KEY = "test-api-key";

    @Test
    void buildChatModel_withNullProvider_throwsMissingConfigurationException() {
        // Execute and verify
        assertThrows(MissingConfigurationException.class, () -> 
            ModelProviderFactory.buildChatModel(null, TEST_API_KEY)
        );
    }
}