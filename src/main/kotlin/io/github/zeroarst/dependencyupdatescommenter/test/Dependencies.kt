package io.github.zeroarst.dependencyupdatescommenter.test

import io.github.zeroarst.dependencyupdatescommenter.annotaions.CommentUpdates

/**
 * Some docs.
 */
object Junit {

    // test previous comments.

    // available versions:
    // 4.13
    // 4.13.1
    // 4.13.2
    @CommentUpdates
    val junit = "junit:junit:4.12"
}

object JetBrains {
    @CommentUpdates // test some comments after annotation.
    val kotlinGradlePlugin = "org.jetbrains.kotlin:kotlin-gradle-plugin:1.7.2"
}

object AndroidX {

    // test with commented out annotation
    // available versions:
    // 1.7.3
    // 1.7.2
    // 1.7.1
    // @CheckNewVersions
    const val coreKtx = "androidx.core:core-ktx:1.7.2"

    // test with commented out annotation
    // @CheckNewVersions
    const val coreAnimation = "androidx.core:core-animation:1.7.2"

    @CommentUpdates
    val lifecycleViewModelCompose = "androidx.lifecycle:lifecycle-viewmodel-compose:2.4.1"

    // error: invalid dependency.
    // continue error on second line.
    @CommentUpdates
    val lifecycleViewModelComposeInalid = "androidx.lifecycle-viewmodel-compose:2.4.1"

    @CommentUpdates
    val lifecycleViewModel = "androidx.lifecycle:lifecycle-viewmodel:2.4.1"

    @CommentUpdates
    val composeCompiler = "androidx.compose.compiler:1.3.2" // "test some text with double quotes"

    // test lazy property.
    @CommentUpdates
    val composeUI: String by lazy { "androidx.compose.ui:ui:1.2.0" }

    // test lazy property with multiple lines.
    @CommentUpdates
    val composeMaterial: String by lazy {
        "androidx.compose.material:material:1.1.1"
    }

    // test lazy property with multiple lines.
    @CommentUpdates
    val composeMaterial3: String by lazy {


        "androidx.compose.material3:material3:1.1.1"


    }

    // Some similar comments without annotation.
    // available versions:
    // 1.7.3
    // 1.7.2
    // 1.7.1
}



