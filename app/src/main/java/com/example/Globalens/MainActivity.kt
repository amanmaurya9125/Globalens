package com.example.Globalens

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.collectAsState
import com.example.Globalens.Theme.ThemeMode
import com.example.Globalens.Theme.Theme_Preferences
import com.example.Globalens.ui.theme.TazaKhabarTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {

            val themeFlow = Theme_Preferences.getTheme(this).collectAsState(initial = ThemeMode.Default)
            val darkTheme = when(themeFlow.value){
                ThemeMode.Dark -> true
                ThemeMode.Light -> false
                ThemeMode.Default -> isSystemInDarkTheme()
        }

            TazaKhabarTheme(darkTheme = darkTheme) {
               NavigationGraph()
            }
        }
    }
}

