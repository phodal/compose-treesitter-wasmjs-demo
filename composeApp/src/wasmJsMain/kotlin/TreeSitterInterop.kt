import kotlin.js.Promise

@JsModule("web-tree-sitter")
external object WebTreeSitter {
    @JsName("default")
    object ParserModule : JsAny {
        fun init(): Promise<JsAny>
    }
}