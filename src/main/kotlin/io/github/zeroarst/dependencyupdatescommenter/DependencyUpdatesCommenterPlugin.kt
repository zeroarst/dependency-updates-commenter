package io.github.zeroarst.dependencyupdatescommenter

import io.github.zeroarst.dependencyupdatescommenter.extensions.main
import io.github.zeroarst.dependencyupdatescommenter.extensions.sourceSets
import org.gradle.api.Plugin
import org.gradle.api.Project
import java.io.File
import java.nio.file.Files

const val EXTENSION_NAME = "DependencyUpdatesCommenter"
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
                task.onlyReleaseVersion.set(extension.onlyReleaseVersion)
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
        val buildDir = project.layout.buildDirectory.asFile.get()
        val generatedSrcDir = File("$buildDir/generated/${packageName.substringAfterLast(".")}/main/kotlin")

        // add annotation.
        val annotationFile = File("${generatedSrcDir.path}/Annotation.kt")
        if (!annotationFile.exists()) {
            Files.createDirectories(generatedSrcDir.toPath())
            Files.createFile(annotationFile.toPath())
        }
        annotationFile.apply {
            writeText("package $packageName\n")
            appendText("annotation class $ANNOTATION_NAME")
        }
        addSrcDir(project, generatedSrcDir)
    }

    private fun addSrcDir(project: Project, srcDir: File) {
        project.sourceSets.main.allJava
            .srcDir(srcDir)
    }

}