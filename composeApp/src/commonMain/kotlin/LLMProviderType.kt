enum class LLMProviderType(val displayName: String) {
    OPENAI("OpenAI"),
    ANTHROPIC("Anthropic"),
    GOOGLE("Google"),
    DEEPSEEK("DeepSeek"),
    OLLAMA("Ollama"),
    OPENROUTER("OpenRouter"),
    GLM("GLM"),
    QWEN("Qwen"),
    KIMI("Kimi"),
    CUSTOM_OPENAI_BASE("custom-openai-base");
}
