import ai.koog.prompt.llm.LLMCapability
import ai.koog.prompt.llm.LLMProvider
import ai.koog.prompt.llm.LLModel

class ModelRegistry {
    fun createGenericModel(
        provider: LLMProviderType,
        modelName: String,
        contextLength: Long = 128000L
    ): LLModel {
        val llmProvider = if (provider == LLMProviderType.OPENAI) LLMProvider.OpenAI
        else if (provider == LLMProviderType.ANTHROPIC) LLMProvider.Anthropic
        else if (provider == LLMProviderType.GOOGLE) LLMProvider.Google
        else if (provider == LLMProviderType.DEEPSEEK) LLMProvider.DeepSeek
        else if (provider == LLMProviderType.OLLAMA) LLMProvider.Ollama
        else if (provider == LLMProviderType.OPENROUTER) LLMProvider.OpenRouter
        else if (provider == LLMProviderType.GLM) LLMProvider.OpenAI // Use OpenAI-compatible provider
        else if (provider == LLMProviderType.QWEN) LLMProvider.OpenAI // Use OpenAI-compatible provider
        else if (provider == LLMProviderType.KIMI) LLMProvider.OpenAI // Use OpenAI-compatible provider
        else if (provider == LLMProviderType.CUSTOM_OPENAI_BASE) LLMProvider.OpenAI // Use OpenAI-compatible provider
        else LLMProvider.OpenAI

        val capabilities = listOf(LLMCapability.Completion, LLMCapability.Temperature)

        return LLModel(
            LLMProvider.OpenAI,
            modelName,
            capabilities,
            contextLength
        )
    }
}