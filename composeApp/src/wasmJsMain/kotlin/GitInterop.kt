import kotlin.js.Promise

/**
 * Import wasm-git/lg2.js as default export
 * lg2.js exports an async function: async function(moduleArg = {})
 *
 * https://raw.githubusercontent.com/petersalomonsen/githttpserver/refs/heads/master/public/libgit2_webworker.js
 */
@JsModule("wasm-git/lg2.js")
external fun lg2(config: JsAny? = definedExternally): Promise<LibGit2Module>

/**
 * LibGit2 Module - returned by lg2() function
 */
external interface LibGit2Module : JsAny {
    val FS: EmscriptenFS

    /**
     * Called when WASM runtime is initialized
     */
    var onRuntimeInitialized: (() -> Unit)?

    /**
     * Call git command with arguments
     * Returns exit code (0 for success)
     */
    fun callMain(args: JsArray<JsString>): Int
}

/**
 * Emscripten File System API
 */
external interface EmscriptenFS : JsAny {
    /**
     * Write a file to the virtual file system
     */
    fun writeFile(path: String, data: String)

    /**
     * Read a file from the virtual file system
     */
    fun readFile(path: String, options: JsAny?): String

    /**
     * Read directory contents
     */
    fun readdir(path: String): JsArray<JsString>

    /**
     * Create a directory
     */
    fun mkdir(path: String)

    /**
     * Change current directory
     */
    fun chdir(path: String)

    /**
     * Sync file system with IndexedDB
     * @param populate true to load from IndexedDB, false to save to IndexedDB
     * @param callback callback function
     */
    fun syncfs(populate: Boolean, callback: () -> Unit)
}

/**
 * Console for logging
 */
@JsName("console")
external object WasmConsole : JsAny {
    fun log(message: String)
    fun error(message: String)
    fun warn(message: String)
}

/**
 * Helper to create JS array of strings
 */
fun jsArrayOf(vararg elements: String): JsArray<JsString> {
    val array = JsArray<JsString>()
    elements.forEach { array[array.length] = it.toJsString() }
    return array
}

/**
 * Helper extension to convert JsArray to Kotlin List
 */
fun <T : JsAny> JsArray<T>.toList(): List<T> {
    val result = mutableListOf<T>()
    for (i in 0 until this.length) {
        val item = this[i]
        if (item != null) {
            result.add(item)
        }
    }
    return result
}

