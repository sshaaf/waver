package dev.shaaf.waver.llm;

public enum ModelType {
    OPENAI_GPT_4_MINI("gpt-4o-mini"),
    OPENAI_GPT_4("gpt-4"),
    OPENAI_GPT_3_5_TURBO("gpt-3.5-turbo"),
    GEMINI_1_5_PRO("gemini-1.5-pro"),
    GEMINI_1_5_FLASH("gemini-1.5-flash");

    private final String modelName;

    ModelType(String modelName) {
        this.modelName = modelName;
    }

    public String getModelName() {
        return modelName;
    }

    public boolean isOpenAI() {
        return this.name().startsWith("OPENAI");
    }

    public boolean isGemini() {
        return this.name().startsWith("GEMINI");
    }
}