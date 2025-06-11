package com.intheme.dev.base

import android.app.Application
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import com.intheme.dev.utils.Constants.Companion.ALARM_ALERT
import com.intheme.dev.utils.Constants.Companion.CHANNLE_ID
import com.google.firebase.FirebaseApp
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class BaseApplication:Application(){

    companion object {
        var manager: NotificationManager? = null
    }

    override fun onCreate() {
        super.onCreate()
        ProcessLifecycleOwner.get().lifecycle.addObserver(AppLifecycleObserver());
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        FirebaseApp.initializeApp(this)
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val mNotificationChannel = NotificationChannel(CHANNLE_ID, ALARM_ALERT, NotificationManager.IMPORTANCE_HIGH)
            mNotificationChannel.setShowBadge(true)
            mNotificationChannel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            mNotificationChannel.enableVibration(false)
            mNotificationChannel.enableLights(true)
            manager = getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(mNotificationChannel)
        }
    }

    class AppLifecycleObserver : LifecycleEventObserver {

        override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
            when (event) {
                Lifecycle.Event.ON_START -> {
                    // App is in the foreground
                   // isAppInForeground = true
                }
                Lifecycle.Event.ON_STOP -> {
                    // App is in the background
                   // isAppInForeground = false
                   // isOpenInBackground = true
                }
                else -> {
                    // Handle other lifecycle events if needed
                }
            }
        }
    }
}