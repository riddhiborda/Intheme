package com.intheme.dev.di

import com.intheme.dev.data.apiservice.ApiService
import com.intheme.dev.data.preference.SharePreferenceManage
import com.intheme.dev.data.repository.AuthRepository
import com.intheme.dev.data.repository.BranchAdminRepository
import com.intheme.dev.data.repository.BranchRepository
import com.intheme.dev.data.repository.CustomerRepository
import com.intheme.dev.data.repository.ServiceManRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideAuthRepository(apiService: ApiService, preferences: SharePreferenceManage): AuthRepository = AuthRepository(apiService = apiService, sharePreferenceManage = preferences)

    @Provides
    @Singleton
    fun provideBranchRepository(apiService: ApiService, preferences: SharePreferenceManage): BranchRepository = BranchRepository(apiService = apiService, sharePreferenceManage = preferences)

    @Provides
    @Singleton
    fun provideBranchAdminRepository(apiService: ApiService, preferences: SharePreferenceManage): BranchAdminRepository = BranchAdminRepository(apiService = apiService, sharePreferenceManage = preferences)

    @Provides
    @Singleton
    fun provideServiceManRepository(apiService: ApiService, preferences: SharePreferenceManage): ServiceManRepository = ServiceManRepository(apiService = apiService, sharePreferenceManage = preferences)

    @Provides
    @Singleton
    fun provideCustomerRepository(apiService: ApiService, preferences: SharePreferenceManage): CustomerRepository = CustomerRepository(apiService = apiService, sharePreferenceManage = preferences)

}