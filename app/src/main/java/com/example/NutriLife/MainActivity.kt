package com.example.NutriLife

import AppNavigation
import com.example.NutriLife.ui.theme.Trabajo2Theme


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface

// Hacemos una clase para envolver el PartBody y agregar información extra del mensaje,
// como el propietario y hora de creación.

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val sharedPreferences = SharedPreferencesHelper.init(this)
        setContent {
            // Tema de la aplicación
            Trabajo2Theme {
                // Contenedor de la aplicación
                Surface(color = MaterialTheme.colorScheme.background) {
                    // Iniciamos la navegación
                    AppNavigation(sharedPreferences = sharedPreferences)
                }
            }
        }
    }
}