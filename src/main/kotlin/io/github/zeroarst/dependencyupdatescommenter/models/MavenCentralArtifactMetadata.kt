package io.github.zeroarst.dependencyupdatescommenter.models

data class MavenCentralArtifactMetadata(
    val response: Response? = null,
    val responseHeader: ResponseHeader? = null
)

data class ResponseHeader(
    val qTime: Int? = null,
    val params: Params? = null,
    val status: Int? = null
)

data class Params(
	val q: String? = null,
	val core: String? = null,
	val indent: String? = null,
	val fl: String? = null,
	val start: String? = null,
	val sort: String? = null,
	val rows: String? = null,
	val wt: String? = null,
	val version: String? = null
)

data class DocsItem(
	val p: String? = null,
	val a: String? = null,
	val v: String? = null,
	val g: String? = null,
	val id: String? = null,
	val ec: List<String?>? = null,
	val timestamp: Long? = null,
	val tags: List<String?>? = null
)

data class Response(
    val docs: List<DocsItem?>? = null,
    val numFound: Int? = null,
    val start: Int? = null
)

