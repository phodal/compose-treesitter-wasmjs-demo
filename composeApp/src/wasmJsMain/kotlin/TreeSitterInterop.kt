import kotlin.js.Promise

// Tree-sitter Language interface
external interface TreeSitterLanguage : JsAny {
    fun query(query: String): Promise<Query>
}

external interface Query: JsAny {}

// Tree-sitter Parser interface
external interface TreeSitterParser : JsAny {
    fun parse(input: String): TreeSitterTree
}

// Tree-sitter Tree interface
external interface TreeSitterTree : JsAny {
    val rootNode: TreeSitterNode
}

// Tree-sitter Node interface
external interface TreeSitterNode : JsAny {
    val type: String
    val text: String
    val startPosition: TreeSitterPoint
    val endPosition: TreeSitterPoint
    val childCount: Int
    fun child(index: Int): TreeSitterNode?
}

// Tree-sitter Point interface
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

        fun setLanguage(language: TreeSitterLanguage)
        fun parse(input: String): TreeSitterTree

        class Language : JsAny {
            companion object {
                fun load(path: String): Promise<Language>
            }
        }
    }

    @JsName("default")
    fun Parser(): TreeSitterParser
}