package com.example.nutrisee.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map




class Preferences private constructor(private val dataStore: DataStore<Preferences>){

    fun getUser(): Flow<User> {
        return dataStore.data.map { preferences ->
            User(
                preferences[ID_TOKEN] ?: "",
                preferences[EMAIL_KEY] ?: ""
            )
        }
    }

    suspend fun saveUser(user: User) {
        dataStore.edit { preferences ->
            preferences[ID_TOKEN] = user.id_token
            preferences[EMAIL_KEY] = user.email
        }
    }

    suspend fun deleteUser() {
        dataStore.edit { preferences ->
            preferences[ID_TOKEN] = ""
            preferences[EMAIL_KEY] = ""
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: com.example.nutrisee.data.local.Preferences? = null

        private val EMAIL_KEY = stringPreferencesKey("email")
        private val ID_TOKEN = stringPreferencesKey("id_token")

        fun getInstance(dataStore: DataStore<Preferences>): com.example.nutrisee.data.local.Preferences {
            return INSTANCE ?: synchronized(this) {
                val instance = Preferences(dataStore)
                INSTANCE = instance
                instance
            }
        }
    }
}