package io.github.zeroarst.dependencyupdatescommenter.repositories

import io.github.zeroarst.dependencyupdatescommenter.models.ComparableVersion
import io.github.zeroarst.dependencyupdatescommenter.executers.DependencyUpdate
import io.github.zeroarst.dependencyupdatescommenter.executers.ResolvedDependencyDetails
import io.github.zeroarst.dependencyupdatescommenter.models.MavenCentralArtifactMetadata
import io.github.zeroarst.dependencyupdatescommenter.utils.getDucLogger
import retrofit2.Converter
import retrofit2.Response
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

object CentralMavenRepository : Repository<CentralMavenRepository.CentralMavenService>() {

    const val ROWS = 10
    override val name: String = "CentralMaven"
    override val url: String = "https://search.maven.org/solrsearch/"
    override val converterFactory: Converter.Factory = MoshiConverterFactory.create()

    interface CentralMavenService {
        @GET("select")
        suspend fun getArtifacts(
            @Query("q", encoded = true) query: String,
            @Query("rows") rows: Int = ROWS, // max is 200.
            @Query("start") start: Int = 0,
            @Query("wt") wt: String = "json",
            @Query("core") core: String = "gav"
        ): Response<MavenCentralArtifactMetadata>

    }

    override val serviceClass: Class<CentralMavenService> = CentralMavenService::class.java

    private val logger = getDucLogger(this::class.java.simpleName)

    override suspend fun fetchDependencyUpdates(resolvedDependencyDetails: ResolvedDependencyDetails): List<DependencyUpdate> {
        return fetchArtifactMetadataRecursively(resolvedDependencyDetails, mutableListOf(), 0)
    }

    private suspend fun fetchArtifactMetadataRecursively(
        resolvedDependencyDetails: ResolvedDependencyDetails,
        updates: MutableList<DependencyUpdate>,
        start: Int
    ): List<DependencyUpdate> {

        val groupId = resolvedDependencyDetails.groupId
        val artifactId = resolvedDependencyDetails.artifactId
        val version = resolvedDependencyDetails.version

        val query = StringBuilder()
            .append("g:${groupId}")
            .append("+AND+")
            .append("a:${artifactId}")
            .toString()

        val response = service.getArtifacts(query = query, start = start)

        if (!response.isSuccessful) {
            error(
                "unable to get data." +
                        " url: ${response.raw().request().url()}." +
                        " code:${response.code()}." +
                        " message:${response.errorBody()?.string()}"
            )
        }

        val mavenCentralArtifactMetadata = response.body() ?: error("empty response body")

        if ((mavenCentralArtifactMetadata.response?.numFound ?: 0) == 0) error("Unable to find artifact data")

        val docs = mavenCentralArtifactMetadata.response?.docs ?: error("empty docs in response")

        if (docs.isEmpty()) return updates

        val allVersions = docs.mapNotNull { docItem ->
            docItem?.v?.let { DependencyUpdate(it) }
        }

        val givingVersion = ComparableVersion(version)
        val newVersions = allVersions
            .filter { ComparableVersion(it.version) > givingVersion }

        updates += newVersions

        return if (newVersions.size == docs.size) {
            fetchArtifactMetadataRecursively(resolvedDependencyDetails, updates, start + ROWS)
        } else {
            // Long output. Turn it on when only need it.
            // logger.debug("Found new versions: ${updates.map { it.version }}")
            updates
        }

    }

}