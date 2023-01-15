package io.github.zeroarst.dependencyupdatescommenter.test

import io.github.zeroarst.dependencyupdatescommenter.CommentDependencyUpdatesTask
import kotlinx.coroutines.runBlocking
import org.gradle.testfixtures.ProjectBuilder
import java.io.File
import java.nio.file.Paths

fun main(): Unit = runBlocking {
    testCommentDependencyUpdatesTask()
    println("finished")
}

suspend fun testCommentDependencyUpdatesTask() {
    val path = Paths.get("").toAbsolutePath().toFile()
    val project = ProjectBuilder.builder().build()
    val file = File("$path/src/main/kotlin/io/github/zeroarst/dependencyupdatescommenter/test")
    require(file.exists())
    val task = project.tasks.register("test", CommentDependencyUpdatesTask::class.java)
    task.get().scanFilesAndProcess(file)
}