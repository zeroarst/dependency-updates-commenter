package io.github.zeroarst.dependencyupdatescommenter.executers

import io.github.zeroarst.dependencyupdatescommenter.CommentDependencyUpdatesTask
import io.github.zeroarst.dependencyupdatescommenter.utils.ducLogger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.withContext
import java.io.File

object Conductor {

    suspend fun scanFilesAndProcess(dir: File, config: CommentDependencyUpdatesTask.Config) {
        ducLogger.debug("scanFilesAndProcess. dir: $dir")
        if (!dir.isDirectory) {
            ducLogger.debug("$dir is not a directory, skipping.")
            return
        }
        dir.listFiles()
            ?.asFlow()
            ?.collect { file ->
                if (file.isDirectory && config.scanSubDirectories)
                    scanFilesAndProcess(file, config)
                else if (file.extension == "kt") {
                    ducLogger.debug("start to process. file: $file")
                    val content = file.readText()
                    val newContent = processContentAndCommentUpdates(
                        content = content,
                        config = config
                    ) ?: return@collect

                    if (config.generateNewFile) {
                        val newFile = File("${file.parent}/${file.nameWithoutExtension}2.kt")
                        val successful = withContext(Dispatchers.IO) {
                            newFile.createNewFile()
                        }
                        if (successful)
                            newFile.writeText(newContent)
                    } else
                        file.writeText(newContent)
                }
            }
    }

    /**
     * Return null if the content has no annotation to process.
     */
    private suspend fun processContentAndCommentUpdates(
        content: String,
        config: CommentDependencyUpdatesTask.Config
    ): String? {
        return content
            .run(Parser::parse)
            .let { it.ifEmpty { return null } } // no annotation found.
            .associate(Resolver::resolve)
            .map { (parsedContentDetails, resolvedDependencyDetailsResult) ->
                // return original throwable result or mapped result.
                parsedContentDetails to resolvedDependencyDetailsResult.mapCatching {
                    it to Fetcher.fetch(
                        resolvedDependencyDetails = it,
                        onlyReleaseVersion = config.onlyReleaseVersion,
                        maximumVersionCount = config.maximumVersionCount,
                        order = config.order
                    ).getOrThrow()
                }
            }
            .map { (parsedContentDetails, resolvedDependencyDetailsResult) ->
                CommentData(
                    parsedContentDetails = parsedContentDetails,
                    result = resolvedDependencyDetailsResult,
                    usingLatestVerComment = config.usingLatestVerComment
                )
            }
            .fold(content, Commenter::comment)
    }
}