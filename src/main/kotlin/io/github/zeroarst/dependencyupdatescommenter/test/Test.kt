package io.github.zeroarst.dependencyupdatescommenter.test

import io.github.zeroarst.dependencyupdatescommenter.CommentDependencyUpdatesTask
import io.github.zeroarst.dependencyupdatescommenter.DependencyUpdatesCommenterPlugin
import io.github.zeroarst.dependencyupdatescommenter.models.ComparableVersion
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.gradle.testfixtures.ProjectBuilder
import java.io.File
import java.nio.file.Paths


fun main(): Unit = runBlocking {
    // val path = Paths.get("").toAbsolutePath().toFile()
    // val project = ProjectBuilder.builder().withProjectDir(path).build()
    // val file = File("$path/src/main/kotlin/com/zeroarst/dependencyupdatescommenter/testdata/Dependency.kt")

    // launch {
    //     val newContent = Commenter.comment(file.readText())
    //     file.writeText(newContent)
    // }.join()
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

// suspend fun checkNewVersions(project: Project, coordinate: String) {
//     NewVersionsCheckerPlugin().checkNewVersions(project, coordinate)
//         ?.let { artifactMetadata ->
//             println(artifactMetadata.coordinate)
//             println(artifactMetadata.exception)
//             artifactMetadata.newVersions.map { it.version }.forEach(::println)
//             println()
//         }
// }

private val VERSIONS = listOf(
    ComparableVersion("NotAVersionSting"),
    ComparableVersion("1.0a1-SNAPSHOT"),
    ComparableVersion("1.0-alpha1"),
    ComparableVersion("1.0beta1-SNAPSHOT"),
    ComparableVersion("1.0-b2"),
    ComparableVersion("1.0-beta3.SNAPSHOT"),
    ComparableVersion("1.0-beta3"),
    ComparableVersion("1.0-milestone1-SNAPSHOT"),
    ComparableVersion("1.0-m2"),
    ComparableVersion("1.0-rc1-SNAPSHOT"),
    ComparableVersion("1.0-cr1"),
    ComparableVersion("1.0-SNAPSHOT"),
    ComparableVersion("1.0"),
    ComparableVersion("1.0-sp"),
    ComparableVersion("1.0-a"),
    ComparableVersion("1.0-RELEASE"),
    ComparableVersion("1.0-whatever"),
    ComparableVersion("1.0.z"),
    ComparableVersion("1.0.1"),
    ComparableVersion("1.0.1.0.0.0.0.0.0.0.0.0.0.0.1")
)


fun parseVersions() {
    println(VERSIONS.sorted().joinToString("\n"))
}