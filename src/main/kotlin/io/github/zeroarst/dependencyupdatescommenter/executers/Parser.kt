package io.github.zeroarst.dependencyupdatescommenter.executers

import io.github.zeroarst.dependencyupdatescommenter.utils.getDucLogger

data class ParsedContentDetails(
    val resultText: String,
    val comments: String,
    val annotationAndDeclaration: String, // the whole declaration including annotation and property.
    val indent: String,
    val annotation: String,
    val annotationCoordinate: String,
    val lineBreak: String,
    val coordinate: String,
)

object Parser {

    private val logger = getDucLogger(this::class.java.simpleName)

    private val regex =
        """^(.*// *(?:Available versions:|Error:.*)(?:\s*//.*)*\r?\n)?(([\t ]*)(@CommentUpdates(?:\((?:coordinate.*=.*)?"(.*)"\))?).*(\r?\n).*?(?:(?:const.*)?val.*?(?:"(.*?)"|by lazy \{.*?(?:\r?\n.*?)*"(.*?)"(?:\r?\n.*?)*.*?})))""".toRegex(
            RegexOption.MULTILINE
        )

    /**
     * Parse a content via regular expression.
     */
    fun parse(content: String): List<ParsedContentDetails> {
        logger.debug("parsing content")
        val matchResults = regex.findAll(content)
        return matchResults.map { matchResult ->
            ParsedContentDetails(
                resultText = matchResult.groupValues[0],
                comments = matchResult.groupValues[1],
                annotationAndDeclaration = matchResult.groupValues[2],
                indent = matchResult.groupValues[3],
                annotation = matchResult.groupValues[4],
                annotationCoordinate = matchResult.groupValues[5],
                lineBreak = matchResult.groupValues[6],
                coordinate = listOf(
                    matchResult.groupValues[7],
                    matchResult.groupValues[8]
                ).firstOrNull { it.isNotBlank() } ?: "",
            )

        }.toList()
    }

}
