package io.github.zeroarst.dependencyupdatescommenter.utils

data class CommentData(
    val parsedContentDetails: ParsedContentDetails,
    val result: Result<Pair<ResolvedDependencyDetails, List<DependencyUpdate>>>
)

object Commenter {

    fun comment(fileOriginalContent: String, data: CommentData): String {

        val (
            parsedDeclarationDetails,
            result,
        ) = data

        var newFileContent = fileOriginalContent

        val (resultText, indent, annotation, propertyDeclaration, _) = parsedDeclarationDetails

        val newResultText = StringBuilder()

        result
            .onSuccess { (_, updates) ->
                newResultText
                    .append("$indent// available versions:\n")
                    .append(updates.joinToString("") {
                        "$indent// ${it.version}\n"
                    })
            }
            // any errors from resolve or fetches show them.
            .onFailure {
                newResultText
                    .append("$indent// error:\n")
                    .append("$indent// $it\n")
            }

        // append original annotation nd declaration
        newResultText
            .append("$indent$annotation\n")
            .append("$indent$propertyDeclaration")

        newFileContent = newFileContent.replace(resultText, newResultText.toString())

        return newFileContent
    }

}