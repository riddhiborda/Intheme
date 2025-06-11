package com.intheme.dev.utils

import com.intheme.dev.data.apiservice.ApiConstant.Companion.AUTHORIZATION
import com.intheme.dev.data.apiservice.ApiConstant.Companion.KEY_BEARER
import com.intheme.dev.data.preference.PrefConstant
import com.intheme.dev.data.preference.SharePreferenceManage
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject


class AuthInterceptor @Inject constructor(private val pref: SharePreferenceManage) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()
        val accessToken = pref.getPref(PrefConstant.PREF_KEY_TOKEN,"")
        if (accessToken.isNotEmpty()) {
            request = request.newBuilder()
                .header(AUTHORIZATION, "$KEY_BEARER $accessToken")
                .build()
        }
        val response = chain.proceed(request)
        return response
    }
}
