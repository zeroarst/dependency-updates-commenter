package io.github.zeroarst.dependencyupdatescommenter.repositories

import io.github.zeroarst.dependencyupdatescommenter.executers.DependencyUpdate
import io.github.zeroarst.dependencyupdatescommenter.executers.ResolvedDependencyDetails
import io.github.zeroarst.dependencyupdatescommenter.utils.getDucLogger
import org.json.XML
import retrofit2.Converter
import retrofit2.Response
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET
import retrofit2.http.Url


object GoogleRepository : Repository<GoogleRepository.GoogleMavenService>() {

    override val name: String = "Google"
    override val url: String = "https://maven.google.com/"
    override val converterFactory: Converter.Factory = ScalarsConverterFactory.create()

    interface GoogleMavenService {
        @GET
        suspend fun getXml(
            @Url url: String,
        ): Response<String>

    }

    override val serviceClass: Class<GoogleMavenService> = GoogleMavenService::class.java

    private val logger = getDucLogger(this::class.java.simpleName)

    override suspend fun fetchDependencyUpdates(
        resolvedDependencyDetails: ResolvedDependencyDetails
    ): List<DependencyUpdate> {

        val groupId = resolvedDependencyDetails.groupId
        val artifactId = resolvedDependencyDetails.artifactId
        val version = resolvedDependencyDetails.version

        val groupIdToPath = groupId.replace(".", "/")

        val nonEmptyArtifact = artifactId.ifBlank { groupId.substringAfterLast(".") }

        val response = service.getXml("${groupIdToPath}/group-index.xml")

        if (!response.isSuccessful) {
            error(
                "unable to get group-index.xml." +
                        " url: ${response.raw().request().url()}." +
                        " code:${response.code()}." +
                        " message:${response.errorBody()?.string()}"
            )
        }

        val xmlString = response.body() ?: error("empty response body")

        val rootJsonObj = XML.toJSONObject(xmlString)
        val jsonPrettyPrintString = rootJsonObj.toString(2)
        // Long output. Turn it on when only need it.
        // logger.debug(jsonPrettyPrintString)

        val groupJsonObj = kotlin.runCatching { rootJsonObj.getJSONObject(groupId) }.getOrNull()
            ?: error("cannot find get JSONObject with groupId $groupId")

        val artifactJsonObj = kotlin.runCatching { groupJsonObj.getJSONObject(nonEmptyArtifact) }.getOrNull()
            ?: error("cannot find get JSONObject with artifactId: $nonEmptyArtifact")

        val versionsString = kotlin.runCatching { artifactJsonObj.getString("versions") }.getOrNull()
        if (versionsString.isNullOrBlank()) {
            error("cannot find get JSONObject with \"versions\" key")
        }
        // Long output. Turn it on when only need it.
        // logger.debug(versionsString)

        return versionsString
            .split(",")
            .let { versions ->
                versions.subList(versions.indexOf(version) + 1, versions.size)
                    .map { DependencyUpdate(version = it) }
            }
    }

}