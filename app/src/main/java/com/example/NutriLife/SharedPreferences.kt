package com.example.NutriLife

import android.content.Context
import android.content.SharedPreferences

import com.example.NutriLife.data.ChatHistoryItem
import com.google.gson.Gson

object SharedPreferencesHelper {
    private const val HISTORY_KEY = "chat_history"
    private const val PREFERENCES_NAME = "MyAppPreferences"

    // Inicialización de SharedPreferences
    private lateinit var preferences: SharedPreferences

    // Método para inicializar las SharedPreferences con el contexto adecuado
    fun init(context: Context) {
        preferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
    }

    fun saveChatHistory(historyItem: ChatHistoryItem) {
        val chatHistories = getChatHistories().toMutableList()


        chatHistories.removeAll { it.id == historyItem.id }
        chatHistories.add(historyItem)

        val json = Gson().toJson(chatHistories)
        preferences.edit().putString(HISTORY_KEY, json).apply()
    }

    fun getChatHistories(): List<ChatHistoryItem> {
        val json = preferences.getString(HISTORY_KEY, null)
        return if (!json.isNullOrEmpty()) {
            Gson().fromJson(json, Array<ChatHistoryItem>::class.java).toList()
        } else {
            emptyList()
        }
    }
}