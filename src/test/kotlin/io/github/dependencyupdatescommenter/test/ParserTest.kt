package io.github.dependencyupdatescommenter.test

import io.github.zeroarst.dependencyupdatescommenter.utils.getResourceAsText
import io.github.zeroarst.dependencyupdatescommenter.executers.Parser
import org.junit.Assert.assertTrue
import org.junit.Test

class ParserTest {

    // @Test
    // fun testParse() {
    //     val content = getResourceAsText("/testdata/Dependencies.kt")
    //     val results = Parser.parse(content!!)
    //
    //     // should only have 6 matched results.
    //     assertTrue(results.size == 9)
    //     results[0].let {
    //         assertTrue(it.annotation == "@CommentUpdates")
    //         assertTrue(it.propertyDeclaration == """val junit = "junit:junit:4.12"""")
    //         assertTrue(it.propertyValue == "junit:junit:4.12")
    //     }
    // }
}