import kotlinx.coroutines.await
import kotlin.js.Promise

/**
 * Java parser using Tree-sitter for parsing Java code
 */
class JavaParser {
    private var parser: WebTreeSitter.Parser? = null
    private var language: WebTreeSitter.Parser.Language? = null
    private var isInitialized = false

    /**
     * Initialize the parser with Java language
     */
    suspend fun initialize() {
        if (isInitialized) return

        try {
            // Initialize Tree-sitter
            val initPromise: Promise<JsAny> = WebTreeSitter.Parser.init().unsafeCast()
            initPromise.await<JsAny>()

            // Load Java language
            val loadPromise: Promise<WebTreeSitter.Parser.Language> =
                WebTreeSitter.Parser.Language.load(CodeLanguage.JAVA.getWasmPath()).unsafeCast()
            language = loadPromise.await()

            // Create parser and set language
            parser = WebTreeSitter.Parser()
            parser?.setLanguage(language)

            isInitialized = true
        } catch (e: Exception) {
            throw Exception("Failed to initialize Java parser: ${e.message}", e)
        }
    }

    /**
     * Parse Java code and return the syntax tree
     */
    private fun parseCode(javaCode: String): Tree {
        if (!isInitialized) {
            throw IllegalStateException("Parser not initialized. Call initialize() first.")
        }

        return parser?.parse(javaCode)
            ?: throw IllegalStateException("Parser not properly initialized")
    }

    /**
     * Parse Java source code and return the syntax tree
     */
    fun parse(sourceCode: String): TreeSitterTree? {
        return parseCode(sourceCode).unsafeCast<TreeSitterTree>()
    }

    /**
     * Parse Java source code and return a formatted string representation
     */
    fun parseToString(sourceCode: String): String {
        val tree = parseCode(sourceCode)
        return formatNode(tree.rootNode, 0)
    }

    /**
     * Extract all method names from Java source code
     */
    fun extractMethodNames(sourceCode: String): List<String> {
        val tree = parseCode(sourceCode)
        val query = language?.query("""
            (method_declaration
                name: (identifier) @method.name)
            (constructor_declaration
                name: (identifier) @method.name)
        """.trimIndent()) ?: throw IllegalStateException("Language not initialized")

        val captures = query.captures(tree.rootNode).toArray()
        val methodNames = mutableListOf<String>()

        for (capture in captures) {
            if (capture.name == "method.name") {
                methodNames.add(capture.node.text)
            }
        }

        query.delete()
        return methodNames.distinct()
    }

    /**
     * Extract all class names from Java source code
     */
    fun extractClassNames(sourceCode: String): List<String> {
        val tree = parseCode(sourceCode)
        val query = language?.query("(class_declaration name: (identifier) @class.name)")
            ?: throw IllegalStateException("Language not initialized")

        val captures = query.captures(tree.rootNode).toArray()
        val classNames = mutableListOf<String>()

        for (capture in captures) {
            if (capture.name == "class.name") {
                classNames.add(capture.node.text)
            }
        }

        query.delete()
        return classNames.distinct()
    }

    /**
     * Extract field names from Java code
     */
    fun extractFieldNames(sourceCode: String): List<String> {
        val tree = parseCode(sourceCode)
        val query = language?.query("""
            (field_declaration
                declarator: (variable_declarator
                    name: (identifier) @field.name))
        """.trimIndent()) ?: throw IllegalStateException("Language not initialized")

        val captures = query.captures(tree.rootNode).toArray()
        val fieldNames = mutableListOf<String>()

        for (capture in captures) {
            if (capture.name == "field.name") {
                fieldNames.add(capture.node.text)
            }
        }

        query.delete()
        return fieldNames.distinct()
    }

    /**
     * Extract import statements from Java code
     */
    fun extractImports(sourceCode: String): List<String> {
        val tree = parseCode(sourceCode)
        val query = language?.query("(import_declaration (scoped_identifier) @import.name)")
            ?: throw IllegalStateException("Language not initialized")

        val captures = query.captures(tree.rootNode).toArray()
        val imports = mutableListOf<String>()

        for (capture in captures) {
            if (capture.name == "import.name") {
                imports.add(capture.node.text)
            }
        }

        query.delete()
        return imports.distinct()
    }

    /**
     * Get package declaration from Java code
     */
    fun getPackageName(sourceCode: String): String? {
        val tree = parseCode(sourceCode)
        val query = language?.query("(package_declaration (scoped_identifier) @package.name)")
            ?: throw IllegalStateException("Language not initialized")

        val captures = query.captures(tree.rootNode).toArray()
        var packageName: String? = null

        for (capture in captures) {
            if (capture.name == "package.name") {
                packageName = capture.node.text
                break
            }
        }

        query.delete()
        return packageName
    }

    /**
     * Find all string literals in the code
     */
    fun extractStringLiterals(sourceCode: String): List<String> {
        val tree = parseCode(sourceCode)
        val query = language?.query("(string_literal) @string")
            ?: throw IllegalStateException("Language not initialized")

        val captures = query.captures(tree.rootNode).toArray()
        val strings = mutableListOf<String>()

        for (capture in captures) {
            if (capture.name == "string") {
                strings.add(capture.node.text)
            }
        }

        query.delete()
        return strings
    }

    /**
     * Check if code contains syntax errors
     */
    fun hasSyntaxErrors(sourceCode: String): Boolean {
        val tree = parseCode(sourceCode)
        return tree.rootNode.hasError || tree.rootNode.isError
    }

    /**
     * Format a node and its children as a readable string
     */
    private fun formatNode(node: SyntaxNode, depth: Int): String {
        val indent = "  ".repeat(depth)
        val result = StringBuilder()

        result.appendLine("${indent}${node.type} [${node.startPosition.row}:${node.startPosition.column} - ${node.endPosition.row}:${node.endPosition.column}]")

        if (node.text.isNotEmpty() && node.isNamed) {
            val textPreview = node.text.take(50).replace("\n", "\\n")
            if (node.text.length > 50) {
                result.appendLine("${indent}  Text: \"$textPreview...\"")
            } else {
                result.appendLine("${indent}  Text: \"$textPreview\"")
            }
        }

        for (child in node.children.toArray()) {
            result.append(formatNode(child, depth + 1))
        }

        return result.toString()
    }
}

/**
 * Example usage function
 */
suspend fun demonstrateJavaParser(): String {
    val parser = JavaParser()
    parser.initialize()
    
    val javaCode = """
        public class HelloWorld {
            public static void main(String[] args) {
                System.out.println("Hello, World!");
            }
            
            private void greet(String name) {
                System.out.println("Hello, " + name);
            }
        }
    """.trimIndent()
    
    val result = StringBuilder()
    result.appendLine("=== Java Parser Demo ===\n")
    
    // Parse and show tree structure
    result.appendLine("Syntax Tree:")
    result.appendLine(parser.parseToString(javaCode))
    result.appendLine()
    
    // Extract class names
    val classNames = parser.extractClassNames(javaCode)
    result.appendLine("Classes found: ${classNames.joinToString(", ")}")
    
    // Extract method names
    val methodNames = parser.extractMethodNames(javaCode)
    result.appendLine("Methods found: ${methodNames.joinToString(", ")}")
    
    return result.toString()
}

