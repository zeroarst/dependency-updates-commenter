package io.github.dependencyupdatescommenter.test

import io.github.zeroarst.dependencyupdatescommenter.utils.Parser
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Assert.assertTrue
import org.junit.Test
class ParserTest2 {

    @Test
    fun testCustomPlugin() {
        // // Java plugin required for IMPLEMENTATION task in the custom plugin
        val project = ProjectBuilder.builder().build()
        project.pluginManager.apply("java")
        project.pluginManager.apply("com.zeroarst.new-versions-checker")
        assertTrue(project.pluginManager.hasPlugin("com.zeroarst.new-versions-checker"))
    }
}