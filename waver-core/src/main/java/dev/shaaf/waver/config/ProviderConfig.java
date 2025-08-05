package dev.shaaf.waver.config;

/**
 * Provider specific configs, using sealed with records
 */
public sealed interface ProviderConfig {
    String getApiKey();

    // A record for OpenAI configuration
    record OpenAI(String apiKey) implements ProviderConfig {
        @Override
        public String getApiKey() {
            return apiKey;
        }

    }

    // A record for Gemini configuration
    record Gemini(String apiKey) implements ProviderConfig {
        @Override
        public String getApiKey() {
            return apiKey;
        }
    }
}