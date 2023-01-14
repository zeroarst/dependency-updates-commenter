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

        val (resultText, _, indent, annotation, propertyDeclaration, _) = parsedDeclarationDetails

        val newResultText = StringBuilder()
            .append("\n")

        result
            .onSuccess { (_, updates) ->
                newResultText
                    .append("$indent// available versions:\n")
                    .append(updates.joinToString("") {
                        "$indent// ${it.version}\n"
                    })
            }
            .onFailure {
                newResultText
                    .append("$indent// error:\n")
                    .append("$indent// $it\n")
            }

        // append original annotation nd declaration
        newResultText
            .append("$indent$annotation\n")
            .append("$indent$propertyDeclaration\n")

        newFileContent = newFileContent.replace(resultText, newResultText.toString())

        return newFileContent
    }

}