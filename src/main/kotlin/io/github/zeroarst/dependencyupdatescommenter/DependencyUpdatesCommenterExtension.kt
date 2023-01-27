package io.github.zeroarst.dependencyupdatescommenter

import io.github.zeroarst.dependencyupdatescommenter.constants.Order
import org.gradle.api.Project
import javax.inject.Inject

abstract class DependencyUpdatesCommenterExtension @Inject constructor(project: Project) {
    var scanPath: String? = null
    var scanSubDirectories: Boolean = false
    var order: Order? = null
    var onlyReleaseVersion: Boolean? = null
    var maximumVersionCount: Int? = null
    var usingLatestVerComment: String? = null
    var generateNewFile: Boolean? = null
}