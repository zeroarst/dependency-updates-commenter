package io.github.zeroarst.dependencyupdatescommenter.executers

import io.github.zeroarst.dependencyupdatescommenter.CommentDependencyUpdatesTask
import io.github.zeroarst.dependencyupdatescommenter.utils.getDucLogger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.withContext
import java.io.File

object Conductor {

    private val logger = getDucLogger(this::class.java.simpleName)

    suspend fun scanFilesAndProcess(fileOrDir: File, task: CommentDependencyUpdatesTask) {
        logger.debug("scanFilesAndProcess. path: $fileOrDir")
        if (!fileOrDir.isDirectory)
            processFile(fileOrDir, task)
        else
            fileOrDir.listFiles()
                ?.asFlow()
                ?.collect { file ->
                    processFile(file, task)
                }
    }

    private suspend fun processFile(
        file: File,
        task: CommentDependencyUpdatesTask
    ) {
        if (file.isDirectory && task.scanSubDirectories.get())
            scanFilesAndProcess(file, task)
        else if (file.extension == "kt") {
            logger.debug("start to process file: $file")
            val content = file.readText()
            val newContent = processContentAndCommentUpdates(
                content = content,
                task = task
            ) ?: return

            if (task.generateNewFile.get()) {
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

    /**
     * Return null if the content has no annotation to process.
     */
    private suspend fun processContentAndCommentUpdates(
        content: String,
        task: CommentDependencyUpdatesTask
    ): String? {
        return content
            .run(Parser::parse)
            .let {
                // no annotation found.
                it.ifEmpty {
                    logger.debug("no parsed result.")
                    return null
                }
            }
            .associate(Resolver::resolve)
            .map { (parsedContentDetails, result) ->
                // return original throwable result or mapped result.
                parsedContentDetails to result.mapCatching {
                    it to Fetcher.fetch(resolvedDependencyDetails = it).getOrThrow()
                }
            }
            .map { (parsedContentDetails, result) ->
                // return original throwable result or mapped result.
                parsedContentDetails to result.mapCatching { (resolvedDependencyDetails, dependencyUpdates) ->
                    resolvedDependencyDetails to Filter.filter(
                        dependencyUpdates,
                        onlyReleaseVersion = task.onlyReleaseVersion.get(),
                        distinctByMajorAndMinorVersion = task.pickLatestGroupedByMajorAndMinor.get(),
                        maximumVersionCount = task.maximumVersionCount.get(),
                        order = task.order.get()
                    ).getOrThrow()
                }
            }
            // map the data to CommentData for Commenter.
            .map { (parsedContentDetails, resolvedDependencyDetailsResult) ->
                CommentData(
                    parsedContentDetails = parsedContentDetails,
                    result = resolvedDependencyDetailsResult,
                    usingLatestVerComment = task.usingLatestVerComment.get()
                )
            }
            .fold(content, Commenter::comment)
    }
}