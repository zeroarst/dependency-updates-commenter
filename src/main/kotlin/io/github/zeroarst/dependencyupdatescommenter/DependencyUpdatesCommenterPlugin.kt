package io.github.zeroarst.dependencyupdatescommenter

import io.github.zeroarst.dependencyupdatescommenter.extensions.main
import io.github.zeroarst.dependencyupdatescommenter.extensions.sourceSets
import io.github.zeroarst.dependencyupdatescommenter.utils.ducLogger
import org.gradle.api.Plugin
import org.gradle.api.Project
import java.io.File
import java.nio.file.Files

const val EXTENSION_NAME = "dependencyUpdatesCommenter"
const val TASK_NAME = "commentDependencyUpdates"
const val ANNOTATION_NAME = "CommentUpdates"


class DependencyUpdatesCommenterPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        val extension = registerConfigurableExtension(project)
        generateRequiredSources(project)
        registerTasks(project, extension)
    }

    private fun registerConfigurableExtension(project: Project): DependencyUpdatesCommenterExtension =
        project.extensions.create(
            EXTENSION_NAME,
            DependencyUpdatesCommenterExtension::class.java
        )

    private fun registerTasks(
        project: Project,
        extension: DependencyUpdatesCommenterExtension
    ) {
        project.tasks.register(TASK_NAME, CommentDependencyUpdatesTask::class.java) { task ->
            try {
                task.scanPath.set(extension.scanPath)
                task.scanSubDirectories.set(extension.scanSubDirectories)
                task.order.set(extension.order)
                task.onlyReleaseVersion.set(extension.onlyReleaseVersion)
                task.maximumVersionCount.set(extension.maximumVersionCount)
                task.usingLatestVerComment.set(extension.usingLatestVerComment)
                task.generateNewFile.set(extension.generateNewFile)
            } catch (e: Exception) {
                ducLogger.error("register task \"${task.name}\" error", e)
            }
        }
    }

    /**
     * Generate sources that required for plugin to work.
     */
    private fun generateRequiredSources(project: Project) {
        val packageName = this::class.java.packageName
        val generatedSrcDir = File("${project.buildDir}/generated/${packageName.substringAfterLast(".")}/main/kotlin")

        // add annotation.
        val annotationFile = File("${generatedSrcDir.path}/Annotation.kt")
        if (!annotationFile.exists()) {
            Files.createDirectories(generatedSrcDir.toPath())
            Files.createFile(annotationFile.toPath())
        }
        annotationFile.apply {
            writeText("package $packageName\n")
            appendText("""annotation class $ANNOTATION_NAME(val coordinate: String = "")""")
        }
        addSrcDir(project, generatedSrcDir)
    }

    private fun addSrcDir(project: Project, srcDir: File) {
        project.sourceSets.main.java.srcDir(srcDir)
    }

}