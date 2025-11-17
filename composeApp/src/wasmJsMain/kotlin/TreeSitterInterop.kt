import kotlin.js.Promise

external interface Query: JsAny {
    fun captures(node: TreeSitterNode, options: QueryOptions?): Promise<QueryCapture>
}

external interface QueryCapture: JsAny {
    var name: String
    val text: String?
}

external interface QueryOptions: JsAny {}

external interface TreeSitterParser : JsAny {
    fun parse(input: String): TreeSitterTree
}

external interface TreeSitterTree : JsAny {
    val rootNode: TreeSitterNode
}

external interface TreeSitterNode : JsAny {
    val type: String
    val text: String
    val startPosition: TreeSitterPoint
    val endPosition: TreeSitterPoint
    val childCount: Int
    fun child(index: Int): TreeSitterNode?
}

external interface TreeSitterPoint : JsAny {
    val row: Int
    val column: Int
}

@JsModule("web-tree-sitter")
external object WebTreeSitter {
    @JsName("default")
    class ParserModule : JsAny {
        companion object {
            fun init(): Promise<JsAny>
        }

        fun setLanguage(language: Language)
        fun parse(input: String): TreeSitterTree

        class Language : JsAny {
            fun query(query: String): Query

            companion object {
                fun load(path: String): Promise<Language>
            }
        }
    }
}