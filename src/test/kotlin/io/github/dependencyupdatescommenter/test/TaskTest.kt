package io.github.dependencyupdatescommenter.test

import io.github.zeroarst.dependencyupdatescommenter.*
import io.github.zeroarst.dependencyupdatescommenter.executers.ParsedContentDetails
import io.github.zeroarst.dependencyupdatescommenter.executers.Parser
import io.github.zeroarst.dependencyupdatescommenter.utils.getResource
import kotlinx.coroutines.runBlocking
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File

class TaskTest {

    companion object {
        const val TEST_DATA_FOLDER = "/testdata"
        const val TASK_NAME = "commentDependencyUpdates"
    }

    @get:Rule
    val testFolder = TemporaryFolder()

    // @Test
    // fun `test scan including subDirectories`() {
    //     val project = ProjectBuilder.builder().build()
    //     val ext = registerDependencyUpdatesCommenterExtension(project)
    //     ext.scanSubDirectories = true
    //     commentDependencyUpdatesTest(ext)
    // }
    //
    // @Test
    // fun `test scan excluding subDirectories`() {
    //     val project = ProjectBuilder.builder().build()
    //     val ext = registerDependencyUpdatesCommenterExtension(project)
    //     ext.scanSubDirectories = false
    //     commentDependencyUpdatesTest(ext)
    // }
    //
    // private fun registerDependencyUpdatesCommenterExtension(project: Project): DependencyUpdatesCommenterExtension {
    //     return project.extensions.create(
    //         EXTENSION_NAME,
    //         DependencyUpdatesCommenterExtension::class.java
    //     )
    // }
    //
    // private fun commentDependencyUpdatesTest(extension: DependencyUpdatesCommenterExtension) = runBlocking {
    //
    //     val url = getResource(TEST_DATA_FOLDER)
    //     requireNotNull(url)
    //     val dir = File(url.file)
    //
    //     val beforeResults = parseFileRecursively(
    //         file = dir,
    //         pairResults = mutableListOf()
    //     )
    //
    //     // copy resource file into temp dir.
    //     val tempTestDataDir = testFolder.newFolder(TEST_DATA_FOLDER)
    //     dir.copyRecursively(tempTestDataDir, true)
    //
    //     // create project and register the task.
    //     val project = ProjectBuilder.builder().build()
    //     val task = project.tasks.register(
    //         TASK_NAME,
    //         CommentDependencyUpdatesTask::class.java
    //     ) {
    //         it.scanPath.set(tempTestDataDir.absolutePath)
    //     }
    //     task.get().execute()
    //
    //     val afterResults = parseFileRecursively(
    //         file = tempTestDataDir,
    //         pairResults = mutableListOf()
    //     )
    //
    //     for ((i, pair) in beforeResults.withIndex()) {
    //         val (dirLevel, _, beforePCDList) = pair
    //         val (_, _, afterPCDList) = afterResults[i]
    //         for ((k, beforePCD) in beforePCDList.withIndex()) {
    //             val afterPCD = afterPCDList[k]
    //             assert(beforePCD.annotation == afterPCD.annotation)
    //             assert(beforePCD.propertyValue == afterPCD.propertyValue)
    //             assert(beforePCD.indent == afterPCD.indent)
    //             assert(beforePCD.propertyDeclaration == afterPCD.propertyDeclaration)
    //             if (dirLevel == 0 || dirLevel > 1 && extension.scanSubDirectories)
    //                 assert(afterPCD.comments.isNotBlank()) { "comment is blank. propertyDeclaration=${afterPCD.propertyDeclaration}" }
    //         }
    //     }
    // }
    //
    // /**
    //  * @return Triple<DirLevel, KotlinFile, List<ParsedContentDetails>>
    //  */
    // private fun parseFileRecursively(
    //     dirLevel: Int = 1,
    //     file: File,
    //     pairResults: MutableList<Triple<Int, File, List<ParsedContentDetails>>>
    // ): List<Triple<Int, File, List<ParsedContentDetails>>> {
    //     when {
    //         file.extension == "kt" -> pairResults.add(Triple(dirLevel, file, Parser.parse(file.readText())))
    //         file.isDirectory -> file.listFiles().forEach {
    //             parseFileRecursively(dirLevel, it, pairResults)
    //         }
    //     }
    //     return pairResults
    // }
}