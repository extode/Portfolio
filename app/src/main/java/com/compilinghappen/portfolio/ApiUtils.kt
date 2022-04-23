package com.compilinghappen.portfolio

import android.content.Context

class ApiUtils {
    companion object {
        private const val AUTH_SHARED_PREFS_KEY = "auth_prefs"
        private const val AUTH_SHARED_PREFS_TOKEN_KEY = "token"


        suspend fun loadUserToken(context: Context): Boolean {
            val sharedPrefs =
                context.getSharedPreferences(AUTH_SHARED_PREFS_KEY, Context.MODE_PRIVATE)
            val token = sharedPrefs.getString(AUTH_SHARED_PREFS_TOKEN_KEY, null)
            if (Repository.isTokenAlive(token)) {
                UserToken = token
                return true
            }
            UserToken = null
            return false
        }

        fun saveUserToken(context: Context, token: String) {
            val sharedPrefs =
                context.getSharedPreferences(AUTH_SHARED_PREFS_KEY, Context.MODE_PRIVATE)

            sharedPrefs.edit().putString(AUTH_SHARED_PREFS_TOKEN_KEY, token).apply()
        }
    }
}
