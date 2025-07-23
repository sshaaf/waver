package dev.shaaf.waver.config;

/**
 * When Config is missing should Exit.
 */
public class MissingConfigurationException extends IllegalStateException {
    public MissingConfigurationException(String message) {
        super(message);
    }
}
