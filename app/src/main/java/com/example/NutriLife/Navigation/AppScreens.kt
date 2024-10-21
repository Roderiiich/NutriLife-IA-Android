package com.example.NutriLife.Navigation

sealed class AppScreens(val route: String) {

    object ChatScreen : AppScreens("ChatScreen")
    object HistorialScreen : AppScreens("HistorialScreen")

}