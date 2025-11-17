import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.CanvasBasedWindow
import kotlinx.coroutines.await
import kotlin.js.Promise

@OptIn(ExperimentalComposeUiApi::class)
suspend fun main() {
    suspend fun initialize() {
        val initPromise: Promise<JsAny> = WebTreeSitter.ParserModule.init().unsafeCast()
        initPromise.await<JsAny>()

        val loadPromise: Promise<WebTreeSitter.ParserModule.Language> =
            WebTreeSitter.ParserModule.Language.load(CodeLanguage.JAVA.getWasmPath()).unsafeCast()
        val language: WebTreeSitter.ParserModule.Language = loadPromise.await()

        val parser = WebTreeSitter.ParserModule()
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
        println(tree.rootNode.toString())

        val queryObj = language.query("(class_declaration)")
        debug(queryObj)
        val captures = queryObj.captures(tree.rootNode, null)
        println(captures)
    }

    initialize()

    CanvasBasedWindow(canvasElementId = "ComposeTarget") {
        ParserDemo()
    }
}

fun debug(queryObj: Query): Unit = js("""console.log(queryObj)""")
