package com.intheme.dev.data.preference

import android.content.SharedPreferences
import javax.inject.Singleton
import kotlin.reflect.KClass

@Suppress("UNCHECKED_CAST")
@Singleton
class SharePreferenceManage (val sharePreference: SharedPreferences) {
    // Extension function to set shared preferences
    fun <T : Any> SharedPreferences.Editor.put(key: String, value: T, clazz: KClass<T>) {
        when (clazz) {
            String::class -> putString(key, value as String)
            Int::class -> putInt(key, value as Int)
            Boolean::class -> putBoolean(key, value as Boolean)
            Float::class -> putFloat(key, value as Float)
            Long::class -> putLong(key, value as Long)
            else -> throw IllegalArgumentException("Unsupported preference type")
        }
    }

    // Extension function to get shared preferences
    fun <T : Any> SharedPreferences.get(key: String, defaultValue: T, clazz: KClass<T>): T {
        return when (clazz) {
            String::class -> getString(key, defaultValue as String) as T
            Int::class -> getInt(key, defaultValue as Int) as T
            Boolean::class -> getBoolean(key, defaultValue as Boolean) as T
            Float::class -> getFloat(key, defaultValue as Float) as T
            Long::class -> getLong(key, defaultValue as Long) as T
            else -> throw IllegalArgumentException("Unsupported preference type")
        }
    }

    // Convenience function to set shared preferences
    inline fun <reified T : Any> setPref(key: String, value: T) {
        val editor = sharePreference.edit()
        editor.put(key, value, T::class)
        editor.apply()
    }

    fun clearPref(){
        val editor = sharePreference.edit()
        editor.clear()
        val isCommit = editor.commit()
        if(isCommit) {
            editor.apply()
        }
    }


    // Convenience function to get shared preferences
    inline fun <reified T : Any> getPref(key: String, defaultValue: T): T {
        return sharePreference.get(key, defaultValue, T::class)
    }

}
