package io.github.zeroarst.dependencyupdatescommenter.executers

import io.github.zeroarst.dependencyupdatescommenter.ANNOTATION_NAME

data class ParsedContentDetails(
    val resultText: String,
    val comments: String,
    val indent: String,
    val annotation: String,
    val annotationCoordinate: String,
    val propertyDeclaration: String,
    val propertyValue: String,
)

object RegexConfig {
    private val comment = """(.*// *(?:${Commenter.COMMENT_AVAILABLE_VERSION}|${Commenter.COMMENT_ERROR}.*)(?:\s*//.*)*\n)?""".toRegex()
    private val annotation = """([\t ]*)(@$ANNOTATION_NAME(?:\((?:coordinate.*=.*)?"(.*)"\))?).*\n""".toRegex()
    private val coordinate = """.*?((?:const.*)?val.*?(?:\n.*?)*"(.*?)".*)""".toRegex()
    val constituted = "$comment$annotation$coordinate".toRegex()
}

object Parser {

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
                propertyDeclaration = matchResult.groupValues[5],
                propertyValue = matchResult.groupValues[6],
            )
        }.toList()
    }

}
