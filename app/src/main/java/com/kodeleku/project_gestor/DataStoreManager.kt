package com.kodeleku.project_gestor

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Extensión para obtener una instancia de DataStore
private val Context.dataStore by preferencesDataStore(name = "user_preferences")

object DataStoreManager {

    // Claves para los datos que se almacenarán
    private val USERNAME_KEY = stringPreferencesKey("username")
    private val PASSWORD_KEY = stringPreferencesKey("password")
    private val LOGGED_IN_KEY = booleanPreferencesKey("logged_in")

    // Guardar las credenciales del usuario (usuario y contraseña)
    suspend fun saveCredentials(context: Context, username: String, password: String) {
        context.dataStore.edit { preferences ->
            preferences[USERNAME_KEY] = username
            preferences[PASSWORD_KEY] = password
        }
    }

    // Guardar el estado de inicio de sesión
    suspend fun setLoggedIn(context: Context, loggedIn: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[LOGGED_IN_KEY] = loggedIn
        }
    }

    // Obtener el estado de inicio de sesión
    fun isLoggedIn(context: Context): Flow<Boolean> {
        return context.dataStore.data.map { preferences ->
            preferences[LOGGED_IN_KEY] ?: false
        }
    }

    // Obtener el nombre de usuario
    fun getUsername(context: Context): Flow<String?> {
        return context.dataStore.data.map { preferences ->
            preferences[USERNAME_KEY]
        }
    }

    // Obtener la contraseña
    fun getPassword(context: Context): Flow<String?> {
        return context.dataStore.data.map { preferences ->
            preferences[PASSWORD_KEY]
        }
    }
}