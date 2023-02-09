package testdata

/**
 * This file for testing Unix and macOS which line break is LF (\n).
 */
object DependenciesLF {

    object Junit {

        // test previous comments.

        // Available versions:
        // 4.13
        // 4.13.1
        // 4.13.2
        @CommentUpdates
        const val junit = "junit:junit:4.12"
    }

    object KotlinX {
        // test no annotation.
        const val coroutine = "org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.0"
    }

    object JetBrains {
        @CommentUpdates // test some comments after annotation.
        const val kotlinGradlePlugin = "org.jetbrains.kotlin:kotlin-gradle-plugin:1.7.2"
    }

    object AndroidX {

        // test with passing coordinate to annotation.
        @CommentUpdates("androidx.activity:activity")
        const val activityVersion = "1.6.1"
        const val activity = "androidx.activity:activity:$activityVersion"
        const val activityKtx = "androidx.activity:activity-ktx:$activityVersion"

        // test with passing coordinate and parameter name to annotation.
        @CommentUpdates(coordinate = "androidx.appcompat:appcompat")
        const val appcompatVersion = "1.6.1"
        const val appcompat = "androidx.appcompat:appcompat:$appcompatVersion"
        const val appcompatResources = "androidx.appcompat:appcompat-resources:$appcompatVersion"

        // test with commented out annotation and existing updates.
        // Available versions:
        // 1.7.3
        // 1.7.2
        // 1.7.1
        // @CommentUpdates
        const val coreKtx = "androidx.core:core-ktx:1.7.2"

        // test nested dependencies
        object Compose {

            object Animation {
                @CommentUpdates("androidx.compose.animation:animation-core")
                const val version = "1.3.0"

                // test with commented out annotation
                // example of using other dependency variable.
                // @CommentUpdates
                const val animation = "androidx.compose.animation:animation:$version"
            }
        }

        @CommentUpdates
        const val lifecycleViewModelCompose = "androidx.lifecycle:lifecycle-viewmodel-compose:2.4.1"

        // Error: invalid dependency.
        // test continue error on second line.
        @CommentUpdates
        const val lifecycleViewModelComposeInalid = "androidx.lifecycle-viewmodel-compose:2.4.1"

        // test using val without const.
        @CommentUpdates
        val lifecycleViewModel = "androidx.lifecycle:lifecycle-viewmodel:2.4.1"

        @CommentUpdates
        const val composeCompiler = "androidx.compose.compiler:1.3.2" // "test some text with double quotes"

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

        // test some similar comments without annotation and declaration.
        // Available versions:
        // 1.7.3
        // 1.7.2
        // 1.7.1
    }


}