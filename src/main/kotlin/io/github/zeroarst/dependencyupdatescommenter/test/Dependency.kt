package io.github.zeroarst.dependencyupdatescommenter.test

import io.github.zeroarst.dependencyupdatescommenter.annotaions.CommentUpdates
/**
 * Some docs.
 */
object Junit {
    @CommentUpdates
    val junit = "junit:junit:4.12"
}

object JetBrains {
    @CommentUpdates // test some comments here.
    val kotlinGradlePlugin = "org.jetbrains.kotlin:kotlin-gradle-plugin:1.7.2"
}

object AndroidX {

    @CommentUpdates
    val composeCompiler = "androidx.compose.compiler:1.3.2" // "test some text with double quotes"

    @CommentUpdates
    val composeCompilerInalid = "androidx.lifecycle-viewmodel-compose:2.4.1"

    @CommentUpdates
    val lifecycleViewModel = "androidx.lifecycle:lifecycle-viewmodel:2.4.1"

    // test with commented out annotation
    // available versions:
    // 1.7.3
    // 1.7.2
    // 1.7.1
    // @CheckNewVersions
    const val core = "androidx.core:core-ktx:1.7.2"

    // test with commented out annotation
    // @CheckNewVersions
    const val core2 = "androidx.core:core-ktx:1.7.2"

    // test lazy property.
    @CommentUpdates
    val core3: String by lazy { "androidx.core:core-ktx:1.7.2" }

    // Some similar comments without annotation.
    // available versions:
    // 1.7.3
    // 1.7.2
    // 1.7.1
}



