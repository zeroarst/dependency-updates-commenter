package io.github.zeroarst.dependencyupdatescommenter

import io.github.zeroarst.dependencyupdatescommenter.constants.Order
import io.github.zeroarst.dependencyupdatescommenter.extensions.srcDirs
import io.github.zeroarst.dependencyupdatescommenter.utils.*
import kotlinx.coroutines.flow.*
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
        description = "Path to scan files, excluding forward at the beginning. Ex. src/main/kotlin."
    )
    @get:Optional
    abstract val scanPath: Property<String>

    @get:Input
    @get:Option(option = "scanSubDirectories", description = "Whether to scan sub directories. Default is false.")
    @get:Optional
    abstract val scanSubDirectories: Property<Boolean>

    @get:Input
    @get:Option(option = "order", description = "updates order. Default is LATEST_AT_BOTTOM")
    @get:Optional
    abstract val order: Property<Order>

    @get:Input
    @get:Option(
        option = "onlyReleaseVersion",
        description = "Ignore all updates that have qualifier. Ex. beta, alpha, RC...etc."
    )
    @get:Optional
    abstract val onlyReleaseVersion: Property<Boolean>

    @get:Input
    @get:Option(option = "maximumVersionCount", description = "How many new version items to be added. Default is 5.")
    @get:Optional
    abstract val maximumVersionCount: Property<Int>


    @TaskAction
    fun execute() {

        // store config values for later use.
        config = config.copy(
            scanPath = this.scanPath.orNull ?: config.scanPath,
            scanSubDirectories = this.scanSubDirectories.orNull ?: config.scanSubDirectories,
            order = this.order.orNull ?: config.order,
            maximumVersionCount = this.maximumVersionCount.orNull ?: config.maximumVersionCount,
            onlyReleaseVersion = this.onlyReleaseVersion.orNull ?: config.onlyReleaseVersion,
        )

        ducLogger.debug("config: $config")

        runBlocking {
            scanFilesAndProcess(findSrcDir())
        }
    }

    suspend fun scanFilesAndProcess(dir: File) {
        ducLogger.debug("scanFilesAndProcess. dir: $dir")
        if (!dir.isDirectory) {
            ducLogger.debug("$dir is not a directory, skipping.")
            return
        }
        dir.listFiles()
            ?.asFlow()
            ?.collect { file ->
                if (dir.isDirectory && config.scanSubDirectories)
                    scanFilesAndProcess(dir)
                else if (file.extension == "kt") {
                    ducLogger.debug("start to process. file: $file")
                    val content = file.readText()

                    val newContent = content
                        .run(Parser::parse)
                        .map {
                            it to Resolver.resolve(it)
                        }
                        .asFlow()
                        .map { pair ->
                            val (parsedDeclarationDetails, resolvedDependencyDetailsResult) = pair
                            CommentData(
                                parsedContentDetails = parsedDeclarationDetails,
                                result = resolvedDependencyDetailsResult
                                    // return original throwable result or mapped result.
                                    .mapCatching { resolvedDependencyDetails ->
                                        resolvedDependencyDetails to Fetcher.fetch(resolvedDependencyDetails)
                                            .getOrThrow()
                                    }
                            )
                        }
                        .fold(content, Commenter::comment)
                    file.writeText(newContent)
                }
            }
    }

    /**
     * Finds the first existing source directory.
     */
    private fun findSrcDir(): File {
        val scanPathValue = config.scanPath
        val potentialSrcDirList = if (!scanPathValue.isNullOrBlank()) {
            require(!scanPathValue.startsWith("/")) { "Invalid scanPath=${scanPathValue}. Should not start with \"/\"." }
            listOf(File("${project.layout.projectDirectory}/${scanPathValue}"))
        } else project.srcDirs

        val existingSrcDir = potentialSrcDirList.let {
            it.firstOrNull { file -> file.exists() }
                ?: error("Unable to find source directory. Checked paths:\n${it.joinToString("\n")}\nYou can configure \"scanPath\" to your source directory.")
        }
        ducLogger.debug("found existing source directory: $existingSrcDir")
        return existingSrcDir
    }

    companion object {
        var config: Config = Config()
    }

    data class Config(
        var scanPath: String? = null,
        var scanSubDirectories: Boolean = false,
        var order: Order = Order.LATEST_AT_BOTTOM,
        var maximumVersionCount: Int = 10,
        var onlyReleaseVersion: Boolean = false,
    )
}