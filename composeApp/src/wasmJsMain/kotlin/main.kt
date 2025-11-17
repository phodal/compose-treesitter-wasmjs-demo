import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.CanvasBasedWindow
import kotlinx.coroutines.await

@OptIn(ExperimentalComposeUiApi::class)
suspend fun main() {
    suspend fun initialize() {
        (WebTreeSitter.ParserModule.init().await() as JsAny)
        val language: TreeSitterLanguage = WebTreeSitter.ParserModule.Language.load(Language.JAVA.getWasmPath()).await()!!
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

        val result = parser?.parse(javaCode)
        println(result?.rootNode)
    }

    initialize()

    CanvasBasedWindow(canvasElementId = "ComposeTarget") {
        ParserDemo()
    }
}