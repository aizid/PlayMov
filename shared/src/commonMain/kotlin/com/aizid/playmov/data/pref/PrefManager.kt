package com.aizid.playmov.data.pref

import eu.anifantakis.lib.ksafe.KSafe

open class PrefManager(private val ksafe: KSafe? = null) {

    companion object {
        private const val PREFS_KEY_DARK_MODE = "PREFS_KEY_DARK_MODE"
        private const val PREFS_KEY_LANGUAGE = "PREFS_KEY_LANGUAGE"
        private const val PREFS_KEY_AUTH_TOKEN = "PREFS_KEY_AUTH_TOKEN"
        private const val PREFS_KEY_ACCOUNT_ID = "PREFS_KEY_ACCOUNT_ID"
        private const val PREFS_KEY_ACCOUNT_NAME = "PREFS_KEY_ACCOUNT_NAME"
        private const val PREFS_KEY_ONBOARDING_COMPLETED = "PREFS_KEY_ONBOARDING_COMPLETED"
    }

    open suspend fun clearPreferences() {
        ksafe?.clearAll()
    }

    open suspend fun getLanguage(): String = ksafe?.get(PREFS_KEY_LANGUAGE, "") ?: ""
    open suspend fun setLanguage(value: String) {
        ksafe?.put(PREFS_KEY_LANGUAGE, value)
    }

    open suspend fun getAuthToken(): String = ksafe?.get(PREFS_KEY_AUTH_TOKEN, "") ?: ""
    open suspend fun setAuthToken(value: String) {
        ksafe?.put(PREFS_KEY_AUTH_TOKEN, value)
    }

    open suspend fun getAccountId(): String = ksafe?.get(PREFS_KEY_ACCOUNT_ID, "") ?: ""
    open suspend fun setAccountId(value: String) {
        ksafe?.put(PREFS_KEY_ACCOUNT_ID, value)
    }

    open suspend fun getAccountName(): String = ksafe?.get(PREFS_KEY_ACCOUNT_NAME, "") ?: ""
    open suspend fun setAccountName(value: String) {
        ksafe?.put(PREFS_KEY_ACCOUNT_NAME, value)
    }

    open suspend fun isOnboardingCompleted(): Boolean = ksafe?.get(PREFS_KEY_ONBOARDING_COMPLETED, false) ?: false
    open suspend fun setOnboardingCompleted(value: Boolean) {
        ksafe?.put(PREFS_KEY_ONBOARDING_COMPLETED, value)
    }

    open suspend fun isLoggedIn(): Boolean = getAuthToken().isNotBlank()

    open suspend fun clearAuth() {
        setAuthToken("")
        setAccountId("")
        setAccountName("")
    }
}