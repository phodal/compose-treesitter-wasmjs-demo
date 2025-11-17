import kotlin.js.Promise

// Point
external interface Point : JsAny {
    val row: Int
    val column: Int
}

// Range
external interface Range : JsAny {
    val startIndex: Int
    val endIndex: Int
    val startPosition: Point
    val endPosition: Point
}

// Edit
external interface Edit : JsAny {
    val startIndex: Int
    val oldEndIndex: Int
    val newEndIndex: Int
    val startPosition: Point
    val oldEndPosition: Point
    val newEndPosition: Point
}

// Logger
typealias Logger = (message: String, params: JsAny, type: String) -> Unit

// Input
typealias Input = (index: Int, position: Point?) -> String?

// Options
external interface Options : JsAny {
    var includedRanges: JsArray<Range>?
}

// QueryOptions
external interface QueryOptions : JsAny {
    var startPosition: Point?
    var endPosition: Point?
    var startIndex: Int?
    var endIndex: Int?
    var matchLimit: Int?
    var maxStartDepth: Int?
}

// QueryCapture
external interface QueryCapture : JsAny {
    val name: String
    val text: String?
    val node: SyntaxNode
    val setProperties: JsAny?
    val assertedProperties: JsAny?
    val refutedProperties: JsAny?
}

// QueryMatch
external interface QueryMatch : JsAny {
    val pattern: Int
    val captures: JsArray<QueryCapture>
}

// PredicateResult
external interface PredicateResult : JsAny {
    val operator: String
    val operands: JsArray<JsAny>
}

// Query
external interface Query : JsAny {
    val captureNames: JsArray<JsAny>
    val predicates: JsArray<JsAny>
    val setProperties: JsArray<JsAny>
    val assertedProperties: JsArray<JsAny>
    val refutedProperties: JsArray<JsAny>
    val matchLimit: Int
    
    fun delete()
    fun captures(node: SyntaxNode, options: QueryOptions?): JsArray<QueryCapture>
    fun matches(node: SyntaxNode, options: QueryOptions?): JsArray<QueryMatch>
    fun predicatesForPattern(patternIndex: Int): JsArray<PredicateResult>
    fun disableCapture(captureName: String)
    fun disablePattern(patternIndex: Int)
    fun isPatternGuaranteedAtStep(byteOffset: Int): Boolean
    fun isPatternRooted(patternIndex: Int): Boolean
    fun isPatternNonLocal(patternIndex: Int): Boolean
    fun startIndexForPattern(patternIndex: Int): Int
    fun didExceedMatchLimit(): Boolean
}

// SyntaxNode
external interface SyntaxNode : JsAny {
    val tree: Tree
    val id: Int
    val typeId: Int
    val grammarId: Int
    val type: String
    val grammarType: String
    val isNamed: Boolean
    val isMissing: Boolean
    val isExtra: Boolean
    val hasChanges: Boolean
    val hasError: Boolean
    val isError: Boolean
    val text: String
    val parseState: Int
    val nextParseState: Int
    val startPosition: Point
    val endPosition: Point
    val startIndex: Int
    val endIndex: Int
    val parent: SyntaxNode?
    val children: JsArray<SyntaxNode>
    val namedChildren: JsArray<SyntaxNode>
    val childCount: Int
    val namedChildCount: Int
    val firstChild: SyntaxNode?
    val firstNamedChild: SyntaxNode?
    val lastChild: SyntaxNode?
    val lastNamedChild: SyntaxNode?
    val nextSibling: SyntaxNode?
    val nextNamedSibling: SyntaxNode?
    val previousSibling: SyntaxNode?
    val previousNamedSibling: SyntaxNode?
    val descendantCount: Int
    
    fun equals(other: SyntaxNode): Boolean
    fun child(index: Int): SyntaxNode?
    fun namedChild(index: Int): SyntaxNode?
    fun childForFieldName(fieldName: String): SyntaxNode?
    fun childForFieldId(fieldId: Int): SyntaxNode?
    fun fieldNameForChild(childIndex: Int): String?
    fun childrenForFieldName(fieldName: String): JsArray<SyntaxNode>
    fun childrenForFieldId(fieldId: Int): JsArray<SyntaxNode>
    fun firstChildForIndex(index: Int): SyntaxNode?
    fun firstNamedChildForIndex(index: Int): SyntaxNode?
    fun descendantForIndex(index: Int): SyntaxNode
    fun descendantForIndex(startIndex: Int, endIndex: Int): SyntaxNode
    fun namedDescendantForIndex(index: Int): SyntaxNode
    fun namedDescendantForIndex(startIndex: Int, endIndex: Int): SyntaxNode
    fun descendantForPosition(position: Point): SyntaxNode
    fun descendantForPosition(startPosition: Point, endPosition: Point): SyntaxNode
    fun namedDescendantForPosition(position: Point): SyntaxNode
    fun namedDescendantForPosition(startPosition: Point, endPosition: Point): SyntaxNode
    fun descendantsOfType(types: JsAny, startPosition: Point?, endPosition: Point?): JsArray<SyntaxNode>
    fun walk(): TreeCursor
}

// TreeCursor
external interface TreeCursor : JsAny {
    val nodeType: String
    val nodeTypeId: Int
    val nodeStateId: Int
    val nodeText: String
    val nodeId: Int
    val nodeIsNamed: Boolean
    val nodeIsMissing: Boolean
    val startPosition: Point
    val endPosition: Point
    val startIndex: Int
    val endIndex: Int
    val currentNode: SyntaxNode
    val currentFieldName: String
    val currentFieldId: Int
    val currentDepth: Int
    val currentDescendantIndex: Int
    
    fun reset(node: SyntaxNode)
    fun resetTo(cursor: TreeCursor)
    fun delete()
    fun gotoParent(): Boolean
    fun gotoFirstChild(): Boolean
    fun gotoLastChild(): Boolean
    fun gotoFirstChildForIndex(goalIndex: Int): Boolean
    fun gotoFirstChildForPosition(goalPosition: Point): Boolean
    fun gotoNextSibling(): Boolean
    fun gotoPreviousSibling(): Boolean
    fun gotoDescendant(goalDescendantIndex: Int)
}

// Tree
external interface Tree : JsAny {
    val rootNode: SyntaxNode
    
    fun rootNodeWithOffset(offsetBytes: Int, offsetExtent: Point): SyntaxNode
    fun copy(): Tree
    fun delete()
    fun edit(edit: Edit): Tree
    fun walk(): TreeCursor
    fun getChangedRanges(other: Tree): JsArray<Range>
    fun getIncludedRanges(): JsArray<Range>
    fun getEditedRange(other: Tree): Range
}

// LookaheadIterable
external interface LookaheadIterable : JsAny {
    val currentTypeId: Int
    val currentType: String
    
    fun delete()
    fun resetState(stateId: Int): Boolean
}

// Parser - 保持与原 WebTreeSitter 结构兼容
@JsModule("web-tree-sitter")
external object WebTreeSitter {
    @JsName("default")
    class ParserModule : JsAny {
        companion object {
            fun init(): Promise<JsAny>
        }

        fun delete()
        fun parse(input: String): Tree
        fun getIncludedRanges(): JsArray<Range>
        fun getTimeoutMicros(): Int
        fun setTimeoutMicros(timeout: Int)
        fun reset()
        fun setLanguage(language: Language?)

        class Language : JsAny {
            val version: Int
            val fieldCount: Int
            val stateCount: Int
            val nodeTypeCount: Int
            
            fun fieldNameForId(fieldId: Int): String?
            fun fieldIdForName(fieldName: String): Int?
            fun idForNodeType(type: String, named: Boolean): Int
            fun nodeTypeForId(typeId: Int): String?
            fun nodeTypeIsNamed(typeId: Int): Boolean
            fun nodeTypeIsVisible(typeId: Int): Boolean
            fun nextState(stateId: Int, typeId: Int): Int
            fun query(source: String): Query
            fun lookaheadIterator(stateId: Int): LookaheadIterable?

            companion object {
                fun load(input: String): Promise<Language>
            }
        }
    }
}

// Type aliases for backward compatibility
typealias TreeSitterParser = WebTreeSitter.ParserModule
typealias TreeSitterTree = Tree
typealias TreeSitterNode = SyntaxNode
typealias TreeSitterPoint = Point
