package com.intheme.dev.di


import android.content.Context
import android.content.SharedPreferences
import com.intheme.dev.data.preference.PrefConstant.Companion.PREF_NAME
import com.intheme.dev.data.preference.SharePreferenceManage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PreferenceModule {

    @Provides
    @Singleton
    @PrefName
    fun providePrefName():String = PREF_NAME

    @Provides
    @Singleton
    fun providePrefMode():Int = Context.MODE_PRIVATE

    @Provides
    @Singleton
    fun provideSharePreference(context: Context,@PrefName strPref:String, mode:Int): SharedPreferences {
        return context.getSharedPreferences(strPref,mode)
    }

    @Provides
    @Singleton
    fun providePreference(sharePreference: SharedPreferences): SharePreferenceManage {
        return SharePreferenceManage(sharePreference)
    }

}