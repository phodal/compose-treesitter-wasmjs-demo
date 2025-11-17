import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.CanvasBasedWindow
import kotlinx.coroutines.await
import kotlin.js.Promise

@OptIn(ExperimentalComposeUiApi::class)
suspend fun main() {
    suspend fun initialize() {
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
