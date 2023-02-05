package io.github.zeroarst.dependencyupdatescommenter

import io.github.zeroarst.dependencyupdatescommenter.constants.Order
import io.github.zeroarst.dependencyupdatescommenter.extensions.srcDirs
import io.github.zeroarst.dependencyupdatescommenter.executers.Conductor
import io.github.zeroarst.dependencyupdatescommenter.executers.RegexConfig
import io.github.zeroarst.dependencyupdatescommenter.utils.getDucLogger
import kotlinx.coroutines.runBlocking
import org.gradle.api.DefaultTask
import org.gradle.api.provider.Property
import org.gradle.api.tasks.*
import org.gradle.api.tasks.options.Option
import java.io.File


abstract class CommentDependencyUpdatesTask : DefaultTask() {

    @get:Input
    @get:Option(
        option = "scanPath",
        description = """"Path to process. Can be a file or directory. With/without "/" will work. Ex. "src/main/kotlin", "/src/main/kotlin", "/.", ".". Default to the source set folder."""
    )
    @get:Optional
    abstract val scanPath: Property<String>

    @get:Input
    @get:Option(option = "scanSubDirectories", description = "Whether to scan sub directories if scanPath is a directory. Default is false.")
    @get:Optional
    abstract val scanSubDirectories: Property<Boolean>

    @get:Input
    @get:Option(option = "order", description = "updates order. Default is LATEST_AT_BOTTOM")
    @get:Optional
    abstract val order: Property<Order>

    @get:Input
    @get:Option(
        option = "maximumVersionCount",
        description = "How many new version items to be added. Default is $EXT_DEFAULT_MAX_NEW_VERSION_COUNT."
    )
    @get:Optional
    abstract val maximumVersionCount: Property<Int>

    @get:Input
    @get:Option(
        option = "usingLatestVerComment",
        description = "What to put in the comment if using the latest version."
    )
    @get:Optional
    abstract val usingLatestVerComment: Property<String>

    @get:Input
    @get:Option(
        option = "generateNewFile",
        description = """Whether generate new file for the result with suffix "2". Ex. DependenciesCRLF.kt => DependenciesCRLF2.kt. Useful if you want to see the result instead of overwriting the content. """
    )
    @get:Optional
    abstract val generateNewFile: Property<Boolean>

    @get:Input
    @get:Option(
        option = "pickLatestGroupedByMajorAndMinor",
        description = """Group all versions by major and minor numbers then pick the latest one from each group. For example, if there are updates "1.1.0", "1.1.1", "1.1.2", "1.2.0", "1.2.1", "1.2.2", it will give "1.1.1", "1.2.2". true by default."""
    )
    @get:Optional
    abstract val pickLatestGroupedByMajorAndMinor: Property<Boolean>

    @get:Input
    @get:Option(
        option = "onlyReleaseVersion",
        description = "Ignore all updates that have qualifier. Ex. beta, alpha, RC...etc."
    )
    @get:Optional
    abstract val onlyReleaseVersion: Property<Boolean>


    private val logger = getDucLogger(this::class.java.simpleName)

    init {
        this.group = "dependency updates commenter"
        // default property values
        this.scanPath.convention("")
        this.scanSubDirectories.convention(false)
        this.order.convention(Order.LATEST_AT_TOP)
        this.maximumVersionCount.convention(EXT_DEFAULT_MAX_NEW_VERSION_COUNT)
        this.usingLatestVerComment.convention(EXT_DEFAULT_USING_LATEST_COMMENT)
        this.generateNewFile.convention(false)
        this.pickLatestGroupedByMajorAndMinor.convention(true)
        this.onlyReleaseVersion.convention(false)
    }

    @TaskAction
    fun execute() {
        logger.debug(
            "properties: ${
                mapOf(
                    "scanPath" to this.scanPath.get(),
                    "scanSubDirectories" to this.scanSubDirectories.get(),
                    "order" to this.order.get(),
                    "onlyReleaseVersion" to this.onlyReleaseVersion.get(),
                    "maximumVersionCount" to this.maximumVersionCount.get(),
                    "usingLatestVerComment" to this.usingLatestVerComment.get(),
                    "generateNewFile" to this.generateNewFile.get(),
                )
            }"
        )
        logger.debug(RegexConfig.constituted.toString())

        runBlocking {
            Conductor.scanFilesAndProcess(
                findSrcDir(), this@CommentDependencyUpdatesTask
            )
        }

        logger.debug("task completed.")
    }


    /**
     * Finds the first existing source file/directory.
     */
    private fun findSrcDir(): File {

        var scanPathValue = this.scanPath.get()
        require(!File(scanPathValue).isAbsolute) { "You cannot specify absolute path: $scanPath" }

        // trim "/" prefix.
        scanPathValue = scanPathValue.removePrefix("/")

        // find candidate sources.
        val candidateSrcs = if (scanPathValue.isNotBlank()) {
            listOf(File("${project.projectDir}/${scanPathValue}"))
        } else // exclude the one that is in buildDir, which we added when applying plugin.
            project.srcDirs.filter { !it.path.contains(project.buildDir.path) }

        // find first existing src dir from candidate src dirs.
        val existingSrc = candidateSrcs.let {
            it.firstOrNull { file -> file.exists() }
                ?: error("File/dir does not exist. Checked paths:\n${it.joinToString("\n")}\nYou can configure \"scanPath\" to your source directory.")
        }
        logger.debug("Found existing file/dir: $existingSrc")
        return existingSrc
    }

    companion object {
        const val EXT_DEFAULT_USING_LATEST_COMMENT = "State of the art! You are using the latest version."
        const val EXT_DEFAULT_MAX_NEW_VERSION_COUNT = 20
    }
}