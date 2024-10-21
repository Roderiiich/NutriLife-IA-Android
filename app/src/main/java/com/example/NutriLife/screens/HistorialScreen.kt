package com.example.NutriLife.screens



import android.icu.text.SimpleDateFormat
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.NutriLife.data.ChatHistoryItem
import com.example.NutriLife.Navigation.AppScreens
import com.example.NutriLife.SharedPreferencesHelper
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistorialScreen(
    navController: NavController
) {
    val chatHistories = remember { mutableStateListOf<ChatHistoryItem>() }

    // Cargar el historial de chats desde SharedPreferences
    LaunchedEffect(Unit) {
        chatHistories.addAll(SharedPreferencesHelper.getChatHistories())
    }

    @Composable
    fun TopBar(modifier: Modifier = Modifier) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(16.dp), // Opcional: para darle un padding
            contentAlignment = Alignment.Center // Para centrar el texto
        ) {
            Text(
                text = "Historial de Chat",
                fontSize = 25.sp,
                color = Color.Black,
                fontWeight = FontWeight.Bold
            )
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopBar()
        }
    )

    { paddingValues ->

        // Establecer el fondo de la pantalla
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White) // Establecer color de fondo
                .padding(paddingValues)
        ) {
            LazyColumn(
                modifier = Modifier.padding(8.dp)
            ) {
                items(chatHistories) { chat ->
                    ChatPreview(chat = chat) {
                        // Navegar al chat específico utilizando su ID
                        navController.navigate("${AppScreens.ChatScreen.route}/${chat.id}")
                    }
                }
            }
        }
    }
}

// Función que trunca el mensaje a las primeras 2 frases
fun truncarmensajeshistorial(message: String): String {
    val frases = message.split(".") // Dividir por punto
    return frases.take(1).joinToString(". ") +
            if (frases.size > 1) "..."
            else ""
}

@Composable
fun ChatPreview(chat: ChatHistoryItem, onClick: () -> Unit) {
    val lastMessage = chat.messages.lastOrNull()?.partBody?.text ?: "No hay mensajes"
    val truncatedMessage = truncarmensajeshistorial(lastMessage)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(2.dp, Color(android.graphics.Color.parseColor("#785A28"))) // Contorno con color #785A28
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth() // Asegúrate de que ocupe todo el ancho
                .background(Color(0xFFC1F66E))// Fondo de la conversación
                .padding(16.dp) // Padding interno'p
        ) {
            Text(text = "Conversación: ${chat.id}", color = Color(android.graphics.Color.parseColor("#3e6108")),fontWeight = FontWeight.Bold) // Color del texto en blanco
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = truncatedMessage, // Mostrar solo el mensaje truncado
                modifier = Modifier.padding(8.dp),
                color = Color.Black // Color del texto en blanco
            )
        }
    }



}




