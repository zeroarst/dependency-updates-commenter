package io.github.zeroarst.dependencyupdatescommenter.utils

import org.gradle.api.logging.Logger
import org.gradle.api.logging.Logging
import java.net.URL


fun getResource(path: String): URL? = object {}.javaClass.getResource(path)

fun getResourceAsText(path: String): String? = getResource(path)?.readText()
