import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.CanvasBasedWindow
import kotlinx.coroutines.await
import kotlinx.coroutines.launch
import kotlinx.coroutines.GlobalScope
import kotlin.js.Promise

@OptIn(ExperimentalComposeUiApi::class)
suspend fun main() {
    suspend fun initialize() {
        // Initialize wasm-git
        val git: LibGit2Module = lg2().await()
        debugGit(git)
        
        // Create GitRepository instance
        val repo = GitRepository(git)
        
        // Configure git user
        repo.configureUser("Test User", "test@example.com")
        
        // Example: Clone a repository
        console.log("=== Starting Git Clone Demo ===")
        try {
            // Clone the test repository
            val files = repo.clone("https://github.com/unit-mesh/untitled")
            console.log("Repository cloned successfully!")
            console.log("Files in repository: ${files.joinToString(", ")}")
            
            // Execute git status
            console.log("\n=== Git Status ===")
            console.log("Executing: git status")
            try {
                val status = repo.status()
                console.log(status.toString())
            } catch (e: Throwable) {
                console.warn("Status error: ${e.message}")
            }
            
            // Execute git log (show last 5 commits)
            console.log("\n=== Git Log (last 5 commits) ===")
            try {
                repo.log("--oneline", "-5")
            } catch (e: Throwable) {
                console.warn("Log error: ${e.message}")
            }
            
            // Execute git branch - use simpler command
            console.log("\n=== Git Branches ===")
            console.log("Executing: git branch -a")
            try {
                repo.execute("branch", "-a")
            } catch (e: Throwable) {
                console.warn("Branch command error: ${e.message}")
                // Try without -a flag
                try {
                    repo.execute("branch")
                } catch (e2: Throwable) {
                    console.warn("Branch list failed: ${e2.message}")
                }
            }
            
            // Example: Read a file (adjust path based on actual repo structure)
            console.log("\n=== README.md content ===")
            try {
                val readme = repo.readFile("README.md")
                console.log(readme.take(200) + "...") // Show first 200 chars
            } catch (e: Throwable) {
                console.warn("README.md not found or couldn't be read: ${e.message}")
            }
            
            // Example: Execute git diff (if there are any changes)
            console.log("\n=== Git Diff ===")
            repo.diff()
            
        } catch (e: Throwable) {
            console.error("Git operation failed: ${e.message}")
            e.printStackTrace()
        }

        // Initialize TreeSitter (original code)
        val initPromise: Promise<JsAny> = WebTreeSitter.Parser.init().unsafeCast()
        initPromise.await<JsAny>()

        val loadPromise: Promise<WebTreeSitter.Parser.Language> =
            WebTreeSitter.Parser.Language.load(CodeLanguage.JAVA.getWasmPath()).unsafeCast()
        val language: WebTreeSitter.Parser.Language = loadPromise.await()

        val parser = WebTreeSitter.Parser()
        parser.setLanguage(language)

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

        val tree = parser.parse(javaCode)
        debugNode(tree.rootNode)

        val queryObj = language.query("(class_declaration (_) @classBody)")
        debug(queryObj)
        val captures = queryObj.captures(tree.rootNode)
        captures.toList().forEach {
            println(it.node.type)
            println(it.node.text)
        }
    }

    initialize()

    CanvasBasedWindow(canvasElementId = "ComposeTarget") {
        ParserDemo()
    }
}

fun debug(queryObj: Query): Unit = js("""console.log(queryObj)""")
fun debugNode(queryObj: SyntaxNode): Unit = js("""console.log(queryObj)""")
fun debugGit(git: LibGit2Module): Unit = js("""console.log(git)""")
