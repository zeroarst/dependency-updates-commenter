package io.github.zeroarst.dependencyupdatescommenter.executers

import io.github.zeroarst.dependencyupdatescommenter.models.ComparableVersion
import io.github.zeroarst.dependencyupdatescommenter.constants.Order
import io.github.zeroarst.dependencyupdatescommenter.repositories.CentralMavenRepository
import io.github.zeroarst.dependencyupdatescommenter.repositories.GoogleRepository
import io.github.zeroarst.dependencyupdatescommenter.utils.ducLogger

data class DependencyUpdate(
    val version: String,
    val date: String? = null,
)

object Fetcher {

    private val repositories = listOf(
        CentralMavenRepository,
        GoogleRepository
    )

    /**
     * Fetches the dependency's updates.
     */
    suspend fun fetch(
        resolvedDependencyDetails: ResolvedDependencyDetails,
        onlyReleaseVersion: Boolean,
        maximumVersionCount: Int,
        order: Order,
    ): Result<List<DependencyUpdate>> {
        repositories.forEach { repo ->
            kotlin
                .runCatching {
                    repo.fetchDependencyUpdates(resolvedDependencyDetails)
                }
                .onSuccess { dependencyUpdates ->
                    val refinedUpdates = dependencyUpdates
                        .run {
                            if (onlyReleaseVersion)
                                filter {
                                    !ComparableVersion(it.version).hasQualifier
                                }
                            else this
                        }
                        .run {
                            if (order == Order.LATEST_AT_TOP) {
                                sortedByDescending { ComparableVersion(it.version) }
                                    .take(maximumVersionCount)
                            }
                            else {
                                sortedBy { ComparableVersion(it.version) }
                                    .takeLast(maximumVersionCount)
                            }
                        }
                    return Result.success(refinedUpdates)
                }
                .onFailure {
                    ducLogger.debug("Unsuccessfully fetch updates from ${repo.url}", it)
                }
        }
        return Result.failure(Exception("Unsuccessfully fetch dependency updates from repositories."))

    }

}