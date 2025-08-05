package dev.shaaf.waver.backend.config;

import dev.shaaf.waver.config.FormatConverter;
import dev.shaaf.waver.config.llm.LLMProvider;
import io.smallrye.config.ConfigMapping;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.Optional;

@ApplicationScoped
@ConfigMapping(prefix = "waver")
public interface WaverConfig {
    LLMProvider LLMProvider();
    String outputPath();;
    String projectName();;
    boolean verbose();;
    FormatConverter.OutputFormat outputFormat();;
    OpenAI openai();;
    Gemini gemini();;

    public static class OpenAI {
        public Optional<String> apiKey;
    }
    public static class Gemini {
        public Optional<String> apiKey;
    }
}