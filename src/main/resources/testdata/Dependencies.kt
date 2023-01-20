import testdata.CommentUpdates

/**
 * Some docs.
 */
object Junit {

    // test previous comments.

    // Available versions:
    // 4.13
    // 4.13.1
    // 4.13.2
    @CommentUpdates
    val junit = "junit:junit:4.12"
}

object KotlinX {
    @CommentUpdates
    val coroutTine = "org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4"
}

object JetBrains {
    @CommentUpdates // test some comments after annotation.
    val kotlinGradlePlugin = "org.jetbrains.kotlin:kotlin-gradle-plugin:1.7.2"
}

object AndroidX {

    // test with commented out annotation and existing updates.
    // Available versions:
    // 1.7.3
    // 1.7.2
    // 1.7.1
    // @CheckNewVersions
    const val coreKtx = "androidx.core:core-ktx:1.7.2"

    // nested dependencies
    object Compose {

        object Animation {
            @CommentUpdates("androidx.compose.animation:animation-core")
            const val version = "1.3.0"

            // test with commented out annotation
            // example of using other dependency variable.
            // @CheckNewVersions
            const val animation = "androidx.compose.animation:animation:${version}"
        }
    }

    @CommentUpdates
    val lifecycleViewModelCompose = "androidx.lifecycle:lifecycle-viewmodel-compose:2.4.1"

    // Error: invalid dependency.
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
    // Available versions:
    // 1.7.3
    // 1.7.2
    // 1.7.1
}



