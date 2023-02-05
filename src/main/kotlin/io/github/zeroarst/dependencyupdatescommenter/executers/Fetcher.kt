package io.github.zeroarst.dependencyupdatescommenter.executers

import io.github.zeroarst.dependencyupdatescommenter.models.ComparableVersion
import io.github.zeroarst.dependencyupdatescommenter.repositories.CentralMavenRepository
import io.github.zeroarst.dependencyupdatescommenter.repositories.GoogleRepository
import io.github.zeroarst.dependencyupdatescommenter.utils.getDucLogger

data class DependencyUpdate(
    val version: String,
    val date: String? = null,
) {
    val comparableVersion by lazy { ComparableVersion(version) }
}

object Fetcher {

    private val logger = getDucLogger(this::class.java.simpleName)

    private val repositories = listOf(
        CentralMavenRepository,
        GoogleRepository
    )

    /**
     * Fetches the dependency's updates.
     */
    suspend fun fetch(
        resolvedDependencyDetails: ResolvedDependencyDetails,
    ): Result<List<DependencyUpdate>> {
        repositories.forEach { repo ->
            kotlin
                .runCatching {
                    logger.debug("fetching updates from ${repo.url}. coordinate: ${resolvedDependencyDetails.coordinate}")
                    repo.fetchDependencyUpdates(resolvedDependencyDetails)
                }
                .onSuccess { dependencyUpdates ->
                    return Result.success(dependencyUpdates)
                }
                .onFailure {
                    logger.debug("unsuccessfully fetch updates from ${repo.url}", it)
                }
        }
        return Result.failure(Exception("unsuccessfully fetch dependency updates from repositories."))

    }

}