package dev.shaaf.waver.config.llm;

import dev.shaaf.waver.config.llm.ModelType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.junit.jupiter.api.Assertions.*;

class ModelTypeTest {

    @Test
    void getModelName_returnsCorrectName() {
        // Test a few representative model types
        assertEquals("gpt-4o-mini", ModelType.OPENAI_GPT_4_MINI.getModelName());
        assertEquals("gpt-3.5-turbo", ModelType.OPENAI_GPT_3_5_TURBO.getModelName());
        assertEquals("gemini-1.5-pro", ModelType.GEMINI_1_5_PRO.getModelName());
    }
    
    @ParameterizedTest
    @EnumSource(value = ModelType.class, names = {"OPENAI_GPT_4_MINI", "OPENAI_GPT_4", "OPENAI_GPT_3_5_TURBO"})
    void isOpenAI_returnsTrue_forOpenAIModels(ModelType modelType) {
        assertTrue(modelType.isOpenAI(), "OpenAI models should return true for isOpenAI()");
        assertFalse(modelType.isGemini(), "OpenAI models should return false for isGemini()");
    }
    
    @ParameterizedTest
    @EnumSource(value = ModelType.class, names = {"GEMINI_1_5_PRO", "GEMINI_1_5_FLASH"})
    void isGemini_returnsTrue_forGeminiModels(ModelType modelType) {
        assertTrue(modelType.isGemini(), "Gemini models should return true for isGemini()");
        assertFalse(modelType.isOpenAI(), "Gemini models should return false for isOpenAI()");
    }
    
    @Test
    void modelTypeNames_followNamingConvention() {
        // Verify that all OpenAI models start with "OPENAI_"
        for (ModelType type : ModelType.values()) {
            if (type.isOpenAI()) {
                assertTrue(type.name().startsWith("OPENAI_"), 
                        "OpenAI model names should start with OPENAI_");
            }
        }
        
        // Verify that all Gemini models start with "GEMINI_"
        for (ModelType type : ModelType.values()) {
            if (type.isGemini()) {
                assertTrue(type.name().startsWith("GEMINI_"), 
                        "Gemini model names should start with GEMINI_");
            }
        }
    }
}