package io.github.zeroarst.dependencyupdatescommenter.test

import io.github.zeroarst.dependencyupdatescommenter.CommentDependencyUpdatesTask
import io.github.zeroarst.dependencyupdatescommenter.constants.Order
import io.github.zeroarst.dependencyupdatescommenter.utils.getResource
import kotlinx.coroutines.runBlocking
import org.gradle.testfixtures.ProjectBuilder
import java.io.File

/**
 * To test [CommentDependencyUpdatesTask] in action.
 * Sets generateNewFile to true to see results in generated files in output folder.
 * If set to false the content will be overwritten.
 * If wants to revert overwritten content run clean gradle task and build again.
 */
fun main(): Unit = runBlocking {
    // testCommentDependencyUpdatesTask()
    testCommentDependencyUpdatesTask()
    println("finished")
}

fun testCommentDependencyUpdatesTask() {
    // val path = Paths.get("").toAbsolutePath().toFile()

    val testDataDir = File(getResource("/testdata")!!.file)

    val project = ProjectBuilder.builder().withProjectDir(testDataDir).build()
    val task = project.tasks.register(
        "commentDependencyUpdatesTask",
        CommentDependencyUpdatesTask::class.java
    ) {
        it.scanPath.set("/.") // turn on if want to process subdirectories files.
        it.scanSubDirectories.set(true) // turn on if want to process subdirectories files.
        it.order.set(Order.LATEST_AT_BOTTOM) // turn on if want to change updates order.
        it.onlyReleaseVersion.set(false) // turn on if want to see release versions only.
        it.maximumVersionCount.set(5) // turn on if want to set maximum update version items.
        it.generateNewFile.set(true) // turn on if want to generate files.
    }
    task.get().execute()
}