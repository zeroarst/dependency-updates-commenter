package io.github.zeroarst.dependencyupdatescommenter.executers

import io.github.zeroarst.dependencyupdatescommenter.utils.getDucLogger

data class CommentData(
    val parsedContentDetails: ParsedContentDetails,
    val result: Result<Pair<ResolvedDependencyDetails, List<DependencyUpdate>>>,
    val usingLatestVerComment: String
)

object Commenter {

    private val logger = getDucLogger(this::class.java.simpleName)

    /**
     * Comments based on the result. Updates if successful, or error if failed.
     */
    fun comment(fileOriginalContent: String, data: CommentData): String {

        val parsedDeclarationDetails = data.parsedContentDetails
        val result = data.result

        var newFileContent = fileOriginalContent

        val resultText = parsedDeclarationDetails.resultText
        val indent = parsedDeclarationDetails.indent
        val annotation = parsedDeclarationDetails.annotation
        val lineBreak = parsedDeclarationDetails.lineBreak
        val propertyDeclaration = parsedDeclarationDetails.propertyDeclaration

        val newResultText = StringBuilder()

        result
            .onSuccess { (_, updates) ->
                newResultText
                    .append("$indent// $COMMENT_AVAILABLE_VERSION$lineBreak")
                    .apply {
                        if (updates.isEmpty())
                            append("$indent// ${data.usingLatestVerComment}$lineBreak")
                        else
                            append(updates.joinToString("") {
                                "$indent// ${it.version}$lineBreak"
                            })
                    }
            }
            // any errors from resolve or fetches show them.
            .onFailure {
                newResultText
                    .append("$indent// $COMMENT_ERROR$lineBreak")
                    .append("$indent// $it$lineBreak")
            }

        // append original annotation nd declaration
        newResultText
            .append("$indent$annotation$lineBreak")
            .append("$indent$propertyDeclaration")

        newFileContent = newFileContent.replace(resultText, newResultText.toString())

        return newFileContent
    }

    const val COMMENT_AVAILABLE_VERSION = "Available versions:"
    const val COMMENT_ERROR = "Error:"
}