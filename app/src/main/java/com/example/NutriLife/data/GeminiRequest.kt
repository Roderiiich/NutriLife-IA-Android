package com.example.NutriLife.data

import com.example.NutriLife.screens.Message

data class RequestBody(
    val contents: List<ContentBody>
)

data class ContentBody(
    val parts: List<PartBody>
)

data class PartBody(
    val text: String

)

data class ChatHistoryItem(
    val id: String,
    val messages: List<Message>

)