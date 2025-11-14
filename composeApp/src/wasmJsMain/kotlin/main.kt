import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.CanvasBasedWindow
import kotlinx.coroutines.await

@OptIn(ExperimentalComposeUiApi::class)
suspend fun main() {
    suspend fun initialize() {
        // Initialize web-tree-sitter
        // The tree-sitter.wasm file will be copied to the output directory by webpack
        WebTreeSitter.ParserModule.init().await() as JsAny
    }

    initialize()

    CanvasBasedWindow(canvasElementId = "ComposeTarget") {
        App()
    }
}