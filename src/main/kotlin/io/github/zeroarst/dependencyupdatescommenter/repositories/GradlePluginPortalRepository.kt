package io.github.zeroarst.dependencyupdatescommenter.repositories

object GradlePluginPortalRepository : Maven2Repository() {
    override val name: String = "GradlePluginsPortal"
    override val url: String = "https://plugins.gradle.org/m2/"
}