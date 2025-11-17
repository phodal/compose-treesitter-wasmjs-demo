import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.CanvasBasedWindow
import kotlinx.coroutines.await

@OptIn(ExperimentalComposeUiApi::class)
suspend fun main() {
    suspend fun initialize() {
        (WebTreeSitter.ParserModule.init().await() as JsAny)
        val language: WebTreeSitter.ParserModule.Language = WebTreeSitter.ParserModule.Language.load(Language.JAVA.getWasmPath()).await()!!
        val parser: WebTreeSitter.ParserModule = WebTreeSitter.ParserModule()
        parser?.setLanguage(language!!)

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

        val tree = parser?.parse(javaCode)
        println(tree?.rootNode)

        val query: Query = language.query("(class_declaration)")
        println(query)
        if (tree?.rootNode != null) {
            val captures = query.captures(tree.rootNode!!, null)
            println(captures)
        }
        println(query)
    }

    initialize()

    CanvasBasedWindow(canvasElementId = "ComposeTarget") {
        ParserDemo()
    }
}