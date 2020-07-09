package com.bottlerocketstudios.brarchitecture.infrastructure

import com.bottlerocketstudios.brarchitecture.data.network.BitbucketService
import com.bottlerocketstudios.brarchitecture.data.network.auth.BitbucketCredentialsRepository
import com.bottlerocketstudios.brarchitecture.data.network.auth.basic.BasicAuthInterceptor
import com.bottlerocketstudios.brarchitecture.data.repository.BitbucketRepository
import com.bottlerocketstudios.brarchitecture.data.repository.BitbucketRepositoryImplementation
import com.bottlerocketstudios.brarchitecture.infrastructure.auth.token.TokenAuthInterceptor
import com.bottlerocketstudios.brarchitecture.infrastructure.auth.token.TokenAuthService
import com.bottlerocketstudios.brarchitecture.infrastructure.coroutine.DispatcherProvider
import com.bottlerocketstudios.brarchitecture.infrastructure.coroutine.DispatcherProviderImpl
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

/** General app configuration (repositories/viewmodels/etc) */
object Data {
    val dataModule = module {
        single<DispatcherProvider> { DispatcherProviderImpl() }
        single<Moshi> { Moshi.Builder().add(KotlinJsonAdapterFactory()).build() }
        single<BitbucketRepository> { BitbucketRepositoryImplementation(get(), get()) }
        single { BitbucketCredentialsRepository(androidContext(), get()) }
    }
}

/** Allows multiple types of network related implementations to co-exist in the koin graph */
private enum class KoinNamedNetwork {
    Unauthenticated, Authenticated
}

/** General network configuration. Always include with either [BasicAuth] or [TokenAuth] */
object NetworkObject {
    val networkModule = module {
        single<OkHttpClient>(named(KoinNamedNetwork.Unauthenticated)) {
            OkHttpClient.Builder()
                .addInterceptor(ChuckerInterceptor(androidContext()))
                .build()
        }
        single<Retrofit>(named(KoinNamedNetwork.Authenticated)) {
            provideAuthenticatedRetrofit(
                get(named(KoinNamedNetwork.Authenticated))
            )
        }
        single<BitbucketService> { provideBitbucketService(get(named(KoinNamedNetwork.Authenticated))) }
    }

    private fun provideAuthenticatedRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://api.bitbucket.org")
            .client(okHttpClient)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
    }

    private fun provideBitbucketService(retrofit: Retrofit): BitbucketService {
        return retrofit.create(BitbucketService::class.java)
    }
}

/** Basic auth only configuration. Use this or [TokenAuth], never both. **/
private object BasicAuth {
    val basicAuthModule = module {
        single { BasicAuthInterceptor(get()) }
        single<OkHttpClient>(named(KoinNamedNetwork.Authenticated)) {
            provideBasicAuthOkHttpClient(
                get(named(KoinNamedNetwork.Unauthenticated)),
                get()
            )
        }
    }

    private fun provideBasicAuthOkHttpClient(okHttpClient: OkHttpClient, basicAuthInterceptor: BasicAuthInterceptor): OkHttpClient {
        return okHttpClient.newBuilder()
            .addInterceptor(basicAuthInterceptor)
            .build()
    }
}

/** Token auth only configuration. Use this or [BasicAuth], never both. **/
object TokenAuth {
    val tokenAuthModule = module {
        single { TokenAuthInterceptor(get(), get()) }
        single<TokenAuthService> { provideTokenAuthService(get(named(KoinNamedNetwork.Unauthenticated))) }
        single<OkHttpClient>(named(KoinNamedNetwork.Authenticated)) {
            provideTokenAuthOkHttpClient(
                get(named(KoinNamedNetwork.Unauthenticated)),
                get()
            )
        }
    }

    private fun provideTokenAuthService(okHttpClient: OkHttpClient): TokenAuthService {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://bitbucket.org/    ")
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
        return retrofit.create(TokenAuthService::class.java)
    }

    private fun provideTokenAuthOkHttpClient(okHttpClient: OkHttpClient, tokenAuthInterceptor: TokenAuthInterceptor): OkHttpClient {
        return okHttpClient.newBuilder()
            .addInterceptor(tokenAuthInterceptor)
            .build()
    }
}
