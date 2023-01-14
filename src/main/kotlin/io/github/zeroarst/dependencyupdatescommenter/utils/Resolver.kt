package io.github.zeroarst.dependencyupdatescommenter.utils

import io.github.zeroarst.dependencyupdatescommenter.ducLogger

data class ResolvedDependencyDetails(
    val coordinate: String,
    val groupId: String,
    val artifactId: String,
    val version: String,
)

object Resolver {

    fun resolve(parsedContentDetails: ParsedContentDetails): Result<ResolvedDependencyDetails> {
        val coordinate = parsedContentDetails.coordinate

        ducLogger.debug("resolving dependency. coordinate: $coordinate")

        if (coordinate.isBlank()) {
            return Result.failure(Exception("coordinate is blank"))
        }

        val matchResult = """(.+?)(?::(.*))?:(.+)""".toRegex().find(coordinate) ?: kotlin.run {
            return Result.failure(Exception("unable to resolve dependency: $coordinate"))
        }

        val groupId = matchResult.groupValues[1]
        val artifactId = matchResult.groupValues[2]
        val version = matchResult.groupValues[3]

        ducLogger.debug("dependency resolved. groupId=$groupId, artifactId=$artifactId, version=$version")

        return Result.success(
            ResolvedDependencyDetails(
                coordinate,
                groupId,
                artifactId,
                version
            )
        )

    }

}