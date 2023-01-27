package io.github.zeroarst.dependencyupdatescommenter.repositories

import io.github.zeroarst.dependencyupdatescommenter.executers.DependencyUpdate
import io.github.zeroarst.dependencyupdatescommenter.executers.ResolvedDependencyDetails
import retrofit2.Converter
import retrofit2.Retrofit


abstract class Repository<S> {

    abstract val name: String
    abstract val url: String
    abstract val converterFactory: Converter.Factory

    protected val retrofit: Retrofit by lazy {
        Retrofit.Builder().baseUrl(url)
            .addConverterFactory(converterFactory)
            .build()
    }

    abstract val serviceClass: Class<S>

    val service: S by lazy { retrofit.create(serviceClass) }

    abstract suspend fun fetchDependencyUpdates(resolvedDependencyDetails: ResolvedDependencyDetails): List<DependencyUpdate>

}