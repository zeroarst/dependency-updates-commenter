package io.github.zeroarst.dependencyupdatescommenter.utils

import io.github.zeroarst.dependencyupdatescommenter.ANNOTATION_NAME

data class ParsedContentDetails(
    val resultText: String,
    val availableVersions: String,
    val indent: String,
    val annotation: String,
    val propertyDeclaration: String,
    val coordinate: String,
)

object RegexConfig {
    private val comment = """(\n[\t ]*// *(?:available versions:|error:)(?:\s*//.*)*\s?)?\n([\t ]*)""".toRegex()
    private val annotation = """(@$ANNOTATION_NAME)\s*\n.*""".toRegex()
    private val coordinate = """(val.*?"(.*?)".*)\n""".toRegex()
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
                matchResult.groupValues[0],
                matchResult.groupValues[1],
                matchResult.groupValues[2],
                matchResult.groupValues[3],
                matchResult.groupValues[4],
                matchResult.groupValues[5],
            )
        }.toList()
    }

}
