package io.github.zeroarst.dependencyupdatescommenter.executers

import io.github.zeroarst.dependencyupdatescommenter.utils.getDucLogger

data class ResolvedDependencyDetails(
    val parsedContentDetails: ParsedContentDetails,
    val coordinate: String,
    val groupId: String,
    val artifactId: String,
    val version: String,
)

object Resolver {

    private val logger = getDucLogger(this::class.java.simpleName)

    /**
     * Resolves the content details to group id, artifact id and version.
     */
    fun resolve(parsedContentDetails: ParsedContentDetails): Pair<ParsedContentDetails, Result<ResolvedDependencyDetails>> {

        val coordinate: String = parsedContentDetails.annotationCoordinate.ifBlank {
            parsedContentDetails.coordinate
        }

        logger.debug("resolving dependency. coordinate: $coordinate")

        if (coordinate.isBlank())
            return parsedContentDetails to Result.failure(Exception("coordinate is blank"))

        val matchResult = """([^:]+):([^:]+)(?::([^:]+))?""".toRegex().find(coordinate) ?: kotlin.run {
            return parsedContentDetails to Result.failure(Exception("unable to resolve dependency: $coordinate"))
        }

        val groupId = matchResult.groupValues[1]
        val artifactId = matchResult.groupValues[2]
        val version = matchResult.groupValues[3]

        val resolvedDependencyDetails = ResolvedDependencyDetails(
            parsedContentDetails,
            coordinate,
            groupId,
            artifactId,
            version
        )

        logger.debug("dependency resolved. groupId: ${resolvedDependencyDetails.groupId}, artifactId: ${resolvedDependencyDetails.artifactId}, version: ${resolvedDependencyDetails.version}")

        return parsedContentDetails to Result.success(resolvedDependencyDetails)

    }

}