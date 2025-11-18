import kotlin.js.Promise

/**
 * Load the lg2 module
 * This is the default export from wasm-git/lg2.js
 */
@JsModule("wasm-git/lg2.js")
external fun lg2(config: LibGit2Config? = definedExternally): Promise<LibGit2Module>

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

external interface LibGit2Config : JsAny {
    var locateFile: ((String) -> String)?
}

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
