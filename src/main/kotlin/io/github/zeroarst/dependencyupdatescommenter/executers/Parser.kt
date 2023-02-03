package io.github.zeroarst.dependencyupdatescommenter.executers

import io.github.zeroarst.dependencyupdatescommenter.ANNOTATION_NAME
import io.github.zeroarst.dependencyupdatescommenter.utils.getDucLogger

data class ParsedContentDetails(
    val resultText: String,
    val comments: String,
    val indent: String,
    val annotation: String,
    val annotationCoordinate: String,
    val lineBreak: String,
    val propertyDeclaration: String,
    val propertyValue: String,
)

object RegexConfig {
    private val lineBreak = """\r?\n""".toRegex()
    private val comment = """(.*// *(?:${Commenter.COMMENT_AVAILABLE_VERSION}|${Commenter.COMMENT_ERROR}.*)(?:\s*//.*)*$lineBreak)?""".toRegex()
    private val annotation = """([\t ]*)(@$ANNOTATION_NAME(?:\((?:coordinate.*=.*)?"(.*)"\))?).*($lineBreak).*?""".toRegex()
    private val coordinate = """((?:const.*)?val.*?(?:"(.*?)"|by lazy \{.*\r?\n?.*?"(.*?)"\r?\n?.*?}))""".toRegex()
    val constituted = "$comment$annotation$coordinate".toRegex()
}

object Parser {

    private val logger = getDucLogger(this::class.java.simpleName)

    /**
     * Parse a content via regular expression.
     */
    fun parse(content: String): List<ParsedContentDetails> {
        val regex = RegexConfig.constituted
        val matchResults = regex.findAll(content)
        return matchResults.map { matchResult ->
            ParsedContentDetails(
                resultText = matchResult.groupValues[0],
                comments = matchResult.groupValues[1],
                indent = matchResult.groupValues[2],
                annotation = matchResult.groupValues[3],
                annotationCoordinate = matchResult.groupValues[4],
                lineBreak = matchResult.groupValues[5],
                propertyDeclaration = matchResult.groupValues[6],
                propertyValue = listOf(matchResult.groupValues[7], matchResult.groupValues[8]).first { it.isNotBlank() },
            )

        }.toList()
    }

}
