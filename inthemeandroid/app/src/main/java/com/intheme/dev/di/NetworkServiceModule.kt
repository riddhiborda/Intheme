package com.intheme.dev.di

import android.content.Context
import com.intheme.dev.BuildConfig
import com.intheme.dev.data.apiservice.ApiService
import com.intheme.dev.data.preference.PrefConstant
import com.intheme.dev.data.preference.SharePreferenceManage
import com.intheme.dev.utils.AuthInterceptor
import com.intheme.dev.utils.TokenAuthenticator
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkServiceModule {

    @ApiUrl
    @Singleton
    @Provides
    fun provideAPIURL():String = BuildConfig.api_base_url


    @Provides
    @Singleton
    fun providesOkHttp(authInterceptor: AuthInterceptor): OkHttpClient {
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY
        val builder =  OkHttpClient.Builder()
            .connectTimeout(240, TimeUnit.SECONDS)
            .readTimeout(240, TimeUnit.SECONDS)
            .writeTimeout(240, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
        builder.addInterceptor(interceptor)
        builder.addInterceptor(authInterceptor)
        return builder.build()
    }

    @Provides
    @Singleton
    fun providesApiService(client: OkHttpClient, @ApiUrl apiBaseUrl: String): ApiService =
        Retrofit
            .Builder()
            .run {
                baseUrl(apiBaseUrl)
                addConverterFactory(GsonConverterFactory.create())
                client(client)
                build()
            }.create(ApiService::class.java)



}