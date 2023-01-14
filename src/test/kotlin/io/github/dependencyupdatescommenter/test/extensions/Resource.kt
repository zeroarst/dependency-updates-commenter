package io.github.dependencyupdatescommenter.test.extensions

fun getResourceAsText(path: String): String? =
    object {}.javaClass.getResource(path)?.readText()