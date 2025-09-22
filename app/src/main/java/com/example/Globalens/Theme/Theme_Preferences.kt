package com.example.Globalens.Theme

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

object Theme_Preferences {
    private val Context.dataStore by preferencesDataStore("theme_prefs")
    private val theme_key = stringPreferencesKey("theme_mode")

    suspend fun saveTheme(context: Context,themeMode: ThemeMode) {
        context.dataStore.edit {
            it[theme_key] = themeMode.name
        }

    }
         fun getTheme(context: Context):Flow<ThemeMode>{
            return context.dataStore.data.map {
                val mode = it[theme_key]
                when(mode){
                    ThemeMode.Dark.name -> ThemeMode.Dark
                    ThemeMode.Light.name -> ThemeMode.Light
                    else -> ThemeMode.Default
                }
        }
    }

}