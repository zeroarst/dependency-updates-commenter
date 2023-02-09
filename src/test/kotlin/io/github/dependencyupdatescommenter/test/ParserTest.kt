package io.github.dependencyupdatescommenter.test

import io.github.zeroarst.dependencyupdatescommenter.ANNOTATION_NAME
import io.github.zeroarst.dependencyupdatescommenter.executers.ParsedContentDetails
import io.github.zeroarst.dependencyupdatescommenter.executers.Parser
import io.github.zeroarst.dependencyupdatescommenter.utils.getResourceAsText
import org.junit.Assert.*
import org.junit.Test

class ParserTest {

    @Test
    fun testContentWithLFFromFile() {
        val content = getResourceAsText("/testdata/DependenciesLF.kt")
        val results = Parser.parse(content!!)
        validateResults(results)
    }

    @Test
    fun testContentWithCRLFFromFile() {
        val content = getResourceAsText("/testdata/DependenciesCRLF.kt")
        val results = Parser.parse(content!!)
        validateResults(results)
    }

    private fun validateResults(results: List<ParsedContentDetails>) {
        assertEquals(12, results.size)
        results[0].let {
            assertEquals("@$ANNOTATION_NAME", it.annotation)
            assertEquals("const val junit = \"junit:junit:4.12\"", it.propertyDeclaration)
            assertEquals("junit:junit:4.12", it.coordinate)
        }
    }
}