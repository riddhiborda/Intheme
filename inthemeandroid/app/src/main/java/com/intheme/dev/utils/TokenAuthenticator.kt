package com.intheme.dev.utils

import android.content.Context
import android.content.Intent
import com.intheme.dev.BuildConfig
import com.intheme.dev.data.apiservice.ApiService
import com.intheme.dev.data.model.request.RefreshTokenRequest
import com.intheme.dev.data.preference.PrefConstant
import com.intheme.dev.data.preference.SharePreferenceManage
import com.intheme.dev.ui.activity.LoginActivity
import okhttp3.Authenticator
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Inject


class TokenAuthenticator @Inject constructor (
    private val preferenceManager: SharePreferenceManage,
    private val context: Context // Inject application context
) : Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? {
        synchronized(this) {
            val refreshToken = preferenceManager.getPref(PrefConstant.PREF_KEY_REFRESH_TOKEN,"")
            if (refreshToken.isEmpty()) {
                handleLogout() // Force logout
                return null
            }
            try {
                val newTokenResponse = createApiService().refreshToken(RefreshTokenRequest(refreshToken)).execute()
                return if (newTokenResponse.isSuccessful) {
                    val newAccessToken = newTokenResponse.body()?.data?.tokens ?: return null
                    val newRefreshToken = newTokenResponse.body()?.data?.refreshToken ?: return null

                    // Save new tokens
                    preferenceManager.setPref(PrefConstant.PREF_KEY_TOKEN,newAccessToken)
                    preferenceManager.setPref(PrefConstant.PREF_KEY_REFRESH_TOKEN,newRefreshToken)

                    // Retry the failed request with the new token
                    response.request.newBuilder()
                        .header("Authorization", "Bearer $newAccessToken")
                        .build()
                } else {
                    handleLogout() // Logout if refresh token is invalid or expired
                    null
                }
            } catch (e: Exception) {
                e.printStackTrace()
                handleLogout()
                return null
            }
        }
    }

    private fun createApiService(): ApiService {
        val okHttpClient = OkHttpClient.Builder()
            .build()
        val retrofit = Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(BuildConfig.api_base_url)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return retrofit.create(ApiService::class.java)
    }

    private fun handleLogout() {
        preferenceManager.clearPref()
        val intent = Intent(context, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        context.startActivity(intent)
    }
}
