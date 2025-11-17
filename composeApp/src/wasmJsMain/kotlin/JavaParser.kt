import kotlinx.coroutines.await

/**
 * A simple Java code parser using tree-sitter
 */
class JavaParser {
    private var parser: TreeSitterParser? = null
    private var language: Language? = null
    
    /**
     * Initialize the parser with Java language support
     */
    suspend fun initialize() {

    }
    
    /**
     * Parse Java source code and return the syntax tree
     */
    fun parse(sourceCode: String): TreeSitterTree? {
        return parser?.parse(sourceCode)
    }
    
    /**
     * Parse Java source code and return a formatted string representation
     */
    fun parseToString(sourceCode: String): String {
        val tree = parse(sourceCode)
        return tree?.rootNode?.toString() ?: "Failed to parse"
    }
    
    /**
     * Extract all method names from Java source code
     */
    fun extractMethodNames(sourceCode: String): List<String> {
        val tree = parse(sourceCode) ?: return emptyList()
        val methodNames = mutableListOf<String>()
        
        // Traverse the tree to find method declarations
        traverseNode(tree.rootNode) { node ->
            if (node.type == "method_declaration") {
                // Find the method name (identifier node)
                for (i in 0 until node.childCount) {
                    val child = node.child(i)
                    if (child?.type == "identifier") {
                        methodNames.add(child.text)
                        break
                    }
                }
            }
        }
        
        return methodNames
    }
    
    /**
     * Extract all class names from Java source code
     */
    fun extractClassNames(sourceCode: String): List<String> {
        val tree = parse(sourceCode) ?: return emptyList()
        val classNames = mutableListOf<String>()
        
        // Traverse the tree to find class declarations
        traverseNode(tree.rootNode) { node ->
            if (node.type == "class_declaration") {
                // Find the class name (identifier node)
                for (i in 0 until node.childCount) {
                    val child = node.child(i)
                    if (child?.type == "identifier") {
                        classNames.add(child.text)
                        break
                    }
                }
            }
        }
        
        return classNames
    }
    
    /**
     * Traverse the syntax tree and apply a function to each node
     */
    private fun traverseNode(node: TreeSitterNode, action: (TreeSitterNode) -> Unit) {
        action(node)
        
        for (i in 0 until node.childCount) {
            val child = node.child(i)
            if (child != null) {
                traverseNode(child, action)
            }
        }
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

