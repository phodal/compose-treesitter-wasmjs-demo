import kotlinx.coroutines.await
import kotlin.js.Promise

/**
 * Git Repository wrapper for wasm-git operations
 */
class GitRepository(private val git: LibGit2Module) {
    private var currentRepoRootDir: String? = null
    
    /**
     * Configure global git user settings
     */
    fun configureUser(username: String, email: String) {
        git.FS.writeFile(
            "/home/web_user/.gitconfig",
            "[user]\nname = $username\nemail = $email"
        )
        console.log("Git user configured: $username <$email>")
    }

    /**
     * Clone a repository
     * @param url Repository URL (e.g., "https://github.com/unit-mesh/untitled")
     * @return List of files in the cloned repository
     */
    suspend fun clone(url: String): List<String> {
        currentRepoRootDir = url.substringAfterLast('/')
        console.log("Cloning $url into $currentRepoRootDir...")
        
        val exitCode = git.callMain(jsArrayOf("clone", url, currentRepoRootDir!!))
        if (exitCode != 0) {
            console.error("Clone failed with exit code: $exitCode")
            throw Exception("Git clone failed with exit code: $exitCode")
        }

        console.log("Start change directory to $currentRepoRootDir")
        git.FS.chdir(currentRepoRootDir!!)
        console.log("Changed directory to $currentRepoRootDir")
        
        val files = readDir(".")
        console.log("Clone completed. Files: ${files.joinToString(", ")}")
        return files
    }

    /**
     * Execute git diff command
     * @param args Optional arguments (e.g., file path, commit refs)
     * @return Exit code (0 for success)
     */
    fun diff(vararg args: String): Int {
        val allArgs = listOf("diff") + args.toList()
        console.log("Executing: git ${allArgs.joinToString(" ")}")
        return git.callMain(jsArrayOf(*allArgs.toTypedArray()))
    }

    /**
     * Execute git status
     */
    fun status(): Int {
        console.log("Executing: git status")
        return git.callMain(jsArrayOf("status"))
    }

    /**
     * Execute git log
     * @param args Optional arguments (e.g., "--oneline", "-10")
     */
    fun log(vararg args: String): Int {
        val allArgs = listOf("log") + args.toList()
        console.log("Executing: git ${allArgs.joinToString(" ")}")
        return git.callMain(jsArrayOf(*allArgs.toTypedArray()))
    }

    /**
     * Execute git add
     * @param files Files to add
     */
    fun add(vararg files: String): Int {
        val allArgs = listOf("add") + files.toList()
        console.log("Executing: git ${allArgs.joinToString(" ")}")
        return git.callMain(jsArrayOf(*allArgs.toTypedArray()))
    }

    /**
     * Execute git commit
     * @param message Commit message
     */
    fun commit(message: String): Int {
        console.log("Executing: git commit -m \"$message\"")
        return git.callMain(jsArrayOf("commit", "-m", message))
    }

    /**
     * Execute git pull
     */
    fun pull(): Int {
        console.log("Executing: git pull")
        return git.callMain(jsArrayOf("pull", "origin"))
    }

    /**
     * Execute git push
     */
    fun push(): Int {
        console.log("Executing: git push")
        return git.callMain(jsArrayOf("push"))
    }

    /**
     * Execute git branch
     * @param args Optional arguments (e.g., branch name)
     */
    fun branch(vararg args: String): Int {
        val allArgs = mutableListOf("branch")
        allArgs.addAll(args.toList())
        console.log("Executing: git ${allArgs.joinToString(" ")}")
        return git.callMain(jsArrayOf(*allArgs.toTypedArray()))
    }
    
    /**
     * List all branches (alternative to branch -a)
     */
    fun listBranches(): Int {
        console.log("Executing: git branch --list")
        return git.callMain(jsArrayOf("branch", "--list"))
    }

    /**
     * Execute git checkout
     * @param branch Branch name or commit hash
     */
    fun checkout(branch: String): Int {
        console.log("Executing: git checkout $branch")
        return git.callMain(jsArrayOf("checkout", branch))
    }

    /**
     * Read a file from the repository
     * @param path File path relative to repository root
     */
    fun readFile(path: String): String {
        try {
            // Pass null to get string content (default encoding is utf8)
            return git.FS.readFile(path, null)
        } catch (e: Throwable) {
            console.error("Failed to read file $path: ${e.message}")
            throw e
        }
    }

    /**
     * Write a file to the repository
     * @param path File path relative to repository root
     * @param content File content
     */
    fun writeFile(path: String, content: String) {
        git.FS.writeFile(path, content)
        console.log("Written file: $path")
    }

    /**
     * Read directory contents
     * @param path Directory path
     */
    fun readDir(path: String): List<String> {
        val jsArray = git.FS.readdir(path)
        return (0 until jsArray.length).map { jsArray[it].toString() }
    }

    /**
     * Execute any git command
     * @param command Command and arguments
     */
    fun execute(vararg command: String): Int {
        console.log("Executing: git ${command.joinToString(" ")}")
        return git.callMain(jsArrayOf(*command))
    }

    /**
     * Get current repository root directory
     */
    fun getCurrentRepo(): String? = currentRepoRootDir
    
    /**
     * Get current branch name
     */
    fun getCurrentBranch(): String? {
        return try {
            // Try to get current branch using symbolic-ref
            val exitCode = git.callMain(jsArrayOf("symbolic-ref", "--short", "HEAD"))
            if (exitCode == 0) {
                // Successfully got branch name
                null // Output goes to console
            } else {
                null
            }
        } catch (e: Throwable) {
            console.warn("Could not determine current branch: ${e.message}")
            null
        }
    }
    
    /**
     * Check if file exists
     */
    fun fileExists(path: String): Boolean {
        return try {
            readFile(path)
            true
        } catch (e: Throwable) {
            false
        }
    }
}

/**
 * Console object for Kotlin/Wasm
 */
external object console : JsAny {
    fun log(message: String)
    fun error(message: String)
    fun warn(message: String)
}
