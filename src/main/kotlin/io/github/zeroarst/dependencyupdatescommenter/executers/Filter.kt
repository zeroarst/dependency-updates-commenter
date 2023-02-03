package io.github.zeroarst.dependencyupdatescommenter.executers

import io.github.zeroarst.dependencyupdatescommenter.models.ComparableVersion
import io.github.zeroarst.dependencyupdatescommenter.constants.Order
import io.github.zeroarst.dependencyupdatescommenter.utils.getDucLogger

object Filter {

    private val logger = getDucLogger(this::class.java.simpleName)

    /**
     * Filter out results by applying task's properties.
     */
    fun filter(
        dependencyUpdates: List<DependencyUpdate>,
        onlyReleaseVersion: Boolean,
        distinctByMajorAndMinorVersion: Boolean,
        maximumVersionCount: Int,
        order: Order,
    ): Result<List<DependencyUpdate>> = kotlin.runCatching {
        dependencyUpdates
            .run {
                if (onlyReleaseVersion)
                    filter {
                        !it.comparableVersion.hasQualifier
                    }
                else this
            }
            .run {
                if (order == Order.LATEST_AT_TOP) sortedByDescending { it.comparableVersion }
                else sortedBy { it.comparableVersion }
            }
            .run {
                if (distinctByMajorAndMinorVersion)
                    distinctBy { update ->
                        update.comparableVersion.items?.mapNotNull { it as? ComparableVersion.IntItem }
                            ?.let {
                                if (it.size >= 2) "${it.first().value}.${it[1].value}"
                                else it.first().value
                            }

                    }
                else this
            }
            .run {
                if (order == Order.LATEST_AT_TOP) take(maximumVersionCount)
                else takeLast(maximumVersionCount)
            }
    }

}