package dev.shaaf.waver.llm;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.googleai.GoogleAiGeminiChatModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.shaaf.waver.config.MissingConfigurationException;

import java.util.Arrays;
import java.util.HashMap;

public class ModelProviderFactory {

    static HashMap<LLMProvider, ModelType> providerModelMap = new HashMap<LLMProvider, ModelType>() {{
        put(LLMProvider.OpenAI, ModelType.OPENAI_GPT_4_MINI);
        put(LLMProvider.Gemini, ModelType.GEMINI_1_5_FLASH);
    }};

    public static ChatModel buildChatModel(LLMProvider llmProvider, String apiKey) {

        return switch (llmProvider) {
            case OpenAI -> buildOpenAIChatModel(apiKey);
            case Gemini -> buildGeminiChatModel(apiKey);
            case null, default -> {
                throw new MissingConfigurationException(
                        "Unsupported or missing LLM provider. Valid options are: " + Arrays.toString(LLMProvider.values())
                );
            }
        };
    }

    private static ChatModel buildOpenAIChatModel(String apiKey) {
        return OpenAiChatModel.builder()
                .apiKey(apiKey)
                .modelName(providerModelMap.get(LLMProvider.OpenAI).getModelName())
                .build();
    }

    private static ChatModel buildGeminiChatModel(String apiKey) {
        return GoogleAiGeminiChatModel.builder()
                .apiKey(apiKey)
                .modelName(providerModelMap.get(LLMProvider.Gemini).getModelName())
                .build();
    }
}