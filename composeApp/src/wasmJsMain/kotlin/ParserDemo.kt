import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@Composable
fun ParserDemo() {
    var parserOutput by remember { mutableStateOf("Click 'Parse Java Code' to see the result") }
    var isLoading by remember { mutableStateOf(false) }
    var javaCode by remember { 
        mutableStateOf("""
public class HelloWorld {
    public static void main(String[] args) {
        System.out.println("Hello, World!");
    }
    
    private void greet(String name) {
        System.out.println("Hello, " + name);
    }
    
    public int calculate(int a, int b) {
        return a + b;
    }
}
        """.trimIndent()) 
    }
    
    val scope = rememberCoroutineScope()
    
    MaterialTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Java Parser Demo",
                style = MaterialTheme.typography.h4,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            Text(
                "Edit the Java code below and click 'Parse' to analyze it:",
                style = MaterialTheme.typography.body1,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            // Java code input
            OutlinedTextField(
                value = javaCode,
                onValueChange = { javaCode = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                label = { Text("Java Code") },
                textStyle = LocalTextStyle.current.copy(fontFamily = FontFamily.Monospace)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Parse button
            Button(
                onClick = {
                    scope.launch {
                        isLoading = true
                        try {
                            val parser = JavaParser()
                            parser.initialize()

                            val result = StringBuilder()
                            result.appendLine("=== Java Code Analysis ===\n")

                            // Check for syntax errors first
                            if (parser.hasSyntaxErrors(javaCode)) {
                                result.appendLine("⚠️  Syntax errors detected in the code!\n")
                            }

                            // Extract package name
                            val packageName = parser.getPackageName(javaCode)
                            if (packageName != null) {
                                result.appendLine("📁 Package: $packageName")
                                result.appendLine()
                            }

                            // Extract import statements
                            val imports = parser.extractImports(javaCode)
                            if (imports.isNotEmpty()) {
                                result.appendLine("📥 Imports:")
                                imports.forEach { import ->
                                    result.appendLine("  - $import")
                                }
                                result.appendLine()
                            }

                            // Extract class names
                            val classNames = parser.extractClassNames(javaCode)
                            if (classNames.isNotEmpty()) {
                                result.appendLine("📦 Classes found: ${classNames.joinToString(", ")}")
                                result.appendLine()
                            }

                            // Extract method names
                            val methodNames = parser.extractMethodNames(javaCode)
                            if (methodNames.isNotEmpty()) {
                                result.appendLine("🔧 Methods found: ${methodNames.joinToString(", ")}")
                                result.appendLine()
                            }

                            // Extract field names
                            val fieldNames = parser.extractFieldNames(javaCode)
                            if (fieldNames.isNotEmpty()) {
                                result.appendLine("🏷️  Fields found: ${fieldNames.joinToString(", ")}")
                                result.appendLine()
                            }

                            // Extract string literals
                            val stringLiterals = parser.extractStringLiterals(javaCode)
                            if (stringLiterals.isNotEmpty()) {
                                result.appendLine("💬 String literals: ${stringLiterals.size} found")
                                stringLiterals.take(5).forEach { str ->
                                    result.appendLine("  - $str")
                                }
                                if (stringLiterals.size > 5) {
                                    result.appendLine("  ... and ${stringLiterals.size - 5} more")
                                }
                                result.appendLine()
                            }

                            // Show truncated syntax tree
                            result.appendLine("🌳 Syntax Tree (first 500 characters):")
                            val syntaxTree = parser.parseToString(javaCode)
                            if (syntaxTree.length > 500) {
                                result.appendLine(syntaxTree.take(500) + "\n... (truncated)")
                            } else {
                                result.appendLine(syntaxTree)
                            }

                            parserOutput = result.toString()
                        } catch (e: Exception) {
                            parserOutput = "Error: ${e.message}\n\nStack trace: ${e.stackTraceToString()}"
                        } finally {
                            isLoading = false
                        }
                    }
                },
                enabled = !isLoading,
                modifier = Modifier.padding(8.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = MaterialTheme.colors.onPrimary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text(if (isLoading) "Parsing..." else "Parse Java Code")
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Output display
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                elevation = 4.dp
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Text(
                        "Output:",
                        style = MaterialTheme.typography.h6,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Text(
                        parserOutput,
                        style = MaterialTheme.typography.body2.copy(fontFamily = FontFamily.Monospace)
                    )
                }
            }
        }
    }
}

