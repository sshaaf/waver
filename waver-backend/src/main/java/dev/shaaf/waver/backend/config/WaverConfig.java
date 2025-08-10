package dev.shaaf.waver.backend.config;

import dev.shaaf.waver.config.FormatConverter;
import dev.shaaf.waver.config.llm.LLMProvider;
import io.smallrye.config.ConfigMapping;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.Optional;

@ApplicationScoped
@ConfigMapping(prefix = "waver")
public interface WaverConfig {
    LLMProvider llmProvider();
    String outputPath();;
    boolean verbose();;
    FormatConverter.OutputFormat outputFormat();;
    OpenAI openai();;
    Gemini gemini();;

    // This is now also a config mapping interface
    interface OpenAI {
        Optional<String> apiKey();
    }
    // This is now also a config mapping interface
    interface Gemini {
        Optional<String> apiKey();
    }
}