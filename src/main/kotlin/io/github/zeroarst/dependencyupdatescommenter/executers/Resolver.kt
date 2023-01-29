package io.github.zeroarst.dependencyupdatescommenter.executers

import io.github.zeroarst.dependencyupdatescommenter.utils.ducLogger

data class ResolvedDependencyDetails(
    val parsedContentDetails: ParsedContentDetails,
    val coordinate: String,
    val groupId: String,
    val artifactId: String,
    val version: String,
)

object Resolver {

    /**
     * Resolves the content details to group id, artifact id and version.
     */
    fun resolve(parsedContentDetails: ParsedContentDetails): Pair<ParsedContentDetails, Result<ResolvedDependencyDetails>> {

        val coordinate: String = parsedContentDetails.annotationCoordinate.ifBlank {
            parsedContentDetails.propertyValue
        }

        ducLogger.debug("resolving dependency. coordinate: $coordinate")

        if (coordinate.isBlank())
            return parsedContentDetails to Result.failure(Exception("coordinate is blank"))

        val matchResult = """(.+?)(?::(.*))?:(.+)""".toRegex().find(coordinate) ?: kotlin.run {
            return parsedContentDetails to Result.failure(Exception("unable to resolve dependency: $coordinate"))
        }

        val groupId = matchResult.groupValues[1]
        val artifactId = matchResult.groupValues[2]
        val version = matchResult.groupValues[3]

        ducLogger.debug("dependency resolved. groupId=$groupId, artifactId=$artifactId, version=$version")

        return parsedContentDetails to Result.success(
            ResolvedDependencyDetails(
                parsedContentDetails,
                coordinate,
                groupId,
                artifactId,
                version
            )
        )

    }

}