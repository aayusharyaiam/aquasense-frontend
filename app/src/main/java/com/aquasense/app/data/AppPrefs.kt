package com.aquasense.app.data

import android.content.Context

class AppPrefs(context: Context) {
    private val prefs = context.getSharedPreferences("aquasense_preferences", Context.MODE_PRIVATE)

    fun hasSeenOnboarding(): Boolean = prefs.getBoolean(KEY_ONBOARDING, false)

    fun markOnboardingSeen() {
        prefs.edit().putBoolean(KEY_ONBOARDING, true).apply()
    }

    fun saveUser(user: AppUser, token: String? = null) {
        prefs.edit()
            .putString(KEY_NAME, user.displayName)
            .putString(KEY_USER_ID, user.userId)
            .putBoolean(KEY_DEMO, user.demoMode)
            .apply {
                if (token != null) putString(KEY_TOKEN, token)
            }
            .apply()
    }

    fun currentUser(): AppUser? {
        val name = prefs.getString(KEY_NAME, null) ?: return null
        return AppUser(
            displayName = name,
            userId = prefs.getString(KEY_USER_ID, "") ?: "",
            demoMode = prefs.getBoolean(KEY_DEMO, true),
        )
    }

    fun token(): String? = prefs.getString(KEY_TOKEN, null)

    fun clearUser() {
        prefs.edit()
            .remove(KEY_NAME)
            .remove(KEY_USER_ID)
            .remove(KEY_TOKEN)
            .remove(KEY_DEMO)
            .apply()
    }

    private companion object {
        const val KEY_ONBOARDING = "onboarding_seen"
        const val KEY_NAME = "user_name"
        const val KEY_USER_ID = "user_id"
        const val KEY_TOKEN = "access_token"
        const val KEY_DEMO = "demo_mode"
    }
}
