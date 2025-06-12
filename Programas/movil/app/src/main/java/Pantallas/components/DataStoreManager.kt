package Pantallas.components


import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "settings")

class DataStoreManager(private val context: Context) {
    companion object {
        private val Context.dataStore by preferencesDataStore(name = "settings")
        val PRIVACY_ACCEPTED = booleanPreferencesKey("privacy_accepted")
    }

    val privacyAccepted: Flow<Boolean> = context.dataStore.data
        .map { prefs -> prefs[PRIVACY_ACCEPTED] ?: false }

    suspend fun setPrivacyAccepted(accepted: Boolean) {
        context.dataStore.edit { prefs -> prefs[PRIVACY_ACCEPTED] = accepted }
    }
}