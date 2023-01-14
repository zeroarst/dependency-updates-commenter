package io.github.zeroarst.dependencyupdatescommenter.utils

import io.github.zeroarst.dependencyupdatescommenter.CommentDependencyUpdatesTask.Companion.config
import io.github.zeroarst.dependencyupdatescommenter.models.ComparableVersion
import io.github.zeroarst.dependencyupdatescommenter.constants.Order
import io.github.zeroarst.dependencyupdatescommenter.ducLogger
import io.github.zeroarst.dependencyupdatescommenter.repositories.CentralMavenRepository
import io.github.zeroarst.dependencyupdatescommenter.repositories.GoogleRepository

data class DependencyUpdate(
    val version: String,
    val date: String? = null,
)

object Fetcher {

    private val repositories = listOf(
        CentralMavenRepository,
        GoogleRepository
    )

    suspend fun fetch(resolvedDependencyDetails: ResolvedDependencyDetails): Result<List<DependencyUpdate>> {
        repositories.forEach { repo ->

            kotlin.runCatching { repo.fetchDependencyUpdates(resolvedDependencyDetails) }
                .onSuccess { dependencyUpdates ->
                    val refinedUpdates = dependencyUpdates
                        .run {
                            if (config.onlyReleaseVersion)
                                filter {
                                    !ComparableVersion(it.version).hasQualifier
                                }
                            else this
                        }
                        .take(config.maximumVersionCount)
                        .run {
                            if (config.order == Order.LATEST_AT_TOP)
                                sortedByDescending { ComparableVersion(it.version) }
                            else
                                sortedBy { ComparableVersion(it.version) }
                        }
                    return Result.success(refinedUpdates)
                }
                .onFailure {
                    ducLogger.debug("Unsuccessfully get details from ${repo.url}", it)
                }
        }
        return Result.failure(Exception("Unsuccessfully get dependency details from repositories."))

    }

}