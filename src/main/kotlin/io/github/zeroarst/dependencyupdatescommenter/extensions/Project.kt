package io.github.zeroarst.dependencyupdatescommenter.extensions

import org.gradle.api.Project
import org.gradle.api.file.FileCollection
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.SourceSetContainer
import java.io.File

val Project.sourceSets: SourceSetContainer
    get() = extensions
        .getByType(JavaPluginExtension::class.java)
        .sourceSets

val SourceSetContainer.main: SourceSet
    get() = getByName(SourceSet.MAIN_SOURCE_SET_NAME)

val Project.srcDirs: FileCollection
    get() {
        return sourceSets.main.allJava.sourceDirectories
    }

val Project.existingSrcDir: File?
    get() {
        return srcDirs.firstOrNull { file -> file.exists() }
    }
