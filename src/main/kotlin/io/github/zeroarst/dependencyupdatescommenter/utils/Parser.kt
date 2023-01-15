package io.github.zeroarst.dependencyupdatescommenter.utils

import io.github.zeroarst.dependencyupdatescommenter.ANNOTATION_NAME

data class ParsedContentDetails(
    val resultText: String,
    val indent: String,
    val annotation: String,
    val propertyDeclaration: String,
    val coordinate: String,
)

object RegexConfig {
    private val comment = """(?:.*// *(?:available versions:|error:.*)(?:\s*//.*)*\n)?""".toRegex()
    private val annotation = """([\t ]*)(@$ANNOTATION_NAME).*\n""".toRegex()
    private val coordinate = """.*((?:const.*)?val.*?(?:\n.*?)*"(.*?)".*)""".toRegex()
    val constituted = "$comment$annotation$coordinate".toRegex()
}

object Parser {

    /**
     * Parse a content by regular expression.
     */
    fun parse(content: String): List<ParsedContentDetails> {
        val regex = RegexConfig.constituted
        // println(regex) // debug
        val matchResults = regex.findAll(content)
        return matchResults.map { matchResult ->
            ParsedContentDetails(
                resultText = matchResult.groupValues[0],
                indent = matchResult.groupValues[1],
                annotation = matchResult.groupValues[2],
                propertyDeclaration = matchResult.groupValues[3],
                coordinate = matchResult.groupValues[4],
            )
        }.toList()
    }

}
