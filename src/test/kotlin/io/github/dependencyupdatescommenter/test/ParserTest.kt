package io.github.dependencyupdatescommenter.test

import io.github.dependencyupdatescommenter.test.extensions.getResourceAsText
import io.github.zeroarst.dependencyupdatescommenter.utils.Parser
import org.junit.Assert.assertTrue
import org.junit.Test

class ParserTest {

    @Test
    fun testParse() {
        val content = getResourceAsText("/Dependency.kt")
        val results = Parser.parse(content!!)

        // should only have 6 matched results.
        assertTrue(results.size == 6)
        results[0].let {
            assertTrue(it.annotation == "@CommentUpdates")
            assertTrue(it.propertyDeclaration == """val junit = "junit:junit:4.12"""")
            assertTrue(it.coordinate == "junit:junit:4.12")
        }
    }
}