package com.h2v.messenger.core.network

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "h2v_session")

@Singleton
class TokenManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        private val SESSION_TOKEN = stringPreferencesKey("session_token")
    }

    val tokenFlow: Flow<String?> = context.dataStore.data.map { prefs ->
        prefs[SESSION_TOKEN]
    }

    suspend fun getToken(): String? = tokenFlow.first()

    suspend fun saveToken(token: String) {
        context.dataStore.edit { prefs ->
            prefs[SESSION_TOKEN] = token
        }
    }

    suspend fun clearToken() {
        context.dataStore.edit { prefs ->
            prefs.remove(SESSION_TOKEN)
        }
    }
}
