package io.github.zeroarst.dependencyupdatescommenter.repositories

import com.squareup.moshi.Json
import com.squareup.moshi.Moshi
import io.github.zeroarst.dependencyupdatescommenter.executers.DependencyUpdate
import io.github.zeroarst.dependencyupdatescommenter.executers.ResolvedDependencyDetails
import io.github.zeroarst.dependencyupdatescommenter.utils.getDucLogger
import org.json.XML
import retrofit2.Converter
import retrofit2.Response
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET
import retrofit2.http.Url

abstract class Maven2Repository : Repository<Maven2Repository.Service>() {

    override val converterFactory: Converter.Factory = ScalarsConverterFactory.create()

    interface Service {
        @GET
        suspend fun getXml(
            @Url url: String,
        ): Response<String>

    }

    override val serviceClass: Class<Service> = Service::class.java


    data class MavenMetadata(
        val metadata: Metadata
    )

    data class Metadata(
        @Json(name = "groupId")
        val groupID: String,

        @Json(name = "artifactId")
        val artifactID: String,

        val version: String,
        val versioning: Versioning
    )

    data class Versioning(
        val latest: String,
        val release: String,
        val versions: Versions,
        val lastUpdated: Long
    )

    data class Versions(
        val version: List<String>
    )

    private val logger = getDucLogger(this::class.java.simpleName)

    override suspend fun fetchDependencyUpdates(
        resolvedDependencyDetails: ResolvedDependencyDetails
    ): List<DependencyUpdate> {

        val groupId = resolvedDependencyDetails.groupId
        val artifactId = resolvedDependencyDetails.artifactId.ifBlank { groupId.substringAfterLast(".") }
        val version = resolvedDependencyDetails.version

        val groupIdToPath = groupId.replace(".", "/")

        val response = service.getXml("${groupIdToPath}/${artifactId}/$METADATA_FILE_NAME")

        if (!response.isSuccessful) {
            error(
                "unable to get $METADATA_FILE_NAME." +
                        " url: ${response.raw().request().url()}." +
                        " code:${response.code()}." +
                        " message:${response.errorBody()?.string()}"
            )
        }

        val xmlString = response.body() ?: error("empty response body")


        val jsonString = XML.toJSONObject(xmlString).toString()
        val mavenMetadata = Moshi.Builder()
            .build()
            .adapter(MavenMetadata::class.java)
            .fromJson(jsonString) ?: error("Unable to convert xml string to json data.")


        // Long output. Turn it on when only need it.
        // val jsonPrettyPrintString = rootJsonObj.toString(2)
        // logger.debug(jsonPrettyPrintString)

        // val versioningJsonObj = kotlin.runCatching { jsonString.getJSONObject("versioning") }.getOrNull()
        //     ?: error("cannot find get JSONObject with \"versioning\" key")
        //
        // val versionsJsonObj = kotlin.runCatching { versioningJsonObj.getJSONObject("versions") }.getOrNull()
        //     ?: error("cannot find get JSONObject with \"versions\" key")
        //
        // val versionJsonArray = kotlin.runCatching { versionsJsonObj.getJSONArray("version") }.getOrNull()
        //     ?: error("cannot find get JSONObject with \"version\" key")
        //
        // if (versionJsonArray.isEmpty) {
        //     error("cannot find get JSONObject with \"versions\" key")
        // }
        // Long output. Turn it on when only need it.
        // logger.debug(versionsString)

        return mavenMetadata.metadata.versioning.versions.version
            .map {
                DependencyUpdate(version = it)
            }
    }

    companion object {
        const val METADATA_FILE_NAME = "maven-metadata.xml"
    }

}