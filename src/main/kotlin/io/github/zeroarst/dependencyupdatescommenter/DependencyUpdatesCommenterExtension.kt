package io.github.zeroarst.dependencyupdatescommenter

import io.github.zeroarst.dependencyupdatescommenter.constants.FeatureFlag
import io.github.zeroarst.dependencyupdatescommenter.constants.Order
import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.tasks.Nested
import javax.inject.Inject


abstract class DependencyUpdatesCommenterExtension @Inject constructor(project: Project) {

    private val objects = project.objects
    //
    // // Example of a property that is mandatory. The task will
    // // fail if this property is not set as is annotated with @Optional.
    // val scanPath2: Property<String> = objects.property(String::class.java)
    //
    // // Example of a property that is optional.
    // val order: Property<String> = objects.property(String::class.java)

    // var scanPath: String? = null
    var scanPath: String? = null
    var order: Order? = null
    var onlyReleaseVersion: Boolean? = null

    var test: String = "abc"

    @get:Nested
    abstract val featureFlag: FeatureFlagExtension

    open fun featureFlagExt(action: Action<in FeatureFlagExtension>) {
        action.execute(featureFlag)
    }

}

abstract class FeatureFlagExtension {
    abstract var featureFlag: FeatureFlag
    abstract var featureFlagStr: String
}