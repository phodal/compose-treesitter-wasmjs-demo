/**
 * Supported programming languages for tree-sitter parsing
 */
enum class Language {
    JAVA,
    KOTLIN,
    JAVASCRIPT,
    TYPESCRIPT,
    PYTHON,
    RUST,
    GO,
    C,
    CPP,
    CSHARP,
    RUBY,
    PHP,
    SWIFT,
    SCALA,
    LUA,
    BASH;
    
    /**
     * Get the language identifier used in tree-sitter WASM file names
     */
    fun getLanguageId(): String {
        return when (this) {
            CSHARP -> "c_sharp"
            CPP -> "cpp"
            else -> name.lowercase()
        }
    }
    
    /**
     * Get the path to the language WASM file
     */
    fun getWasmPath(): String {
        return "wasm/tree-sitter-${getLanguageId()}.wasm"
    }
}

