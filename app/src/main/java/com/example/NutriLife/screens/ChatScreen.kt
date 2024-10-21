package com.example.NutriLife.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.NutriLife.Navigation.AppScreens
import com.example.NutriLife.R
import com.example.NutriLife.Services.ApiService
import com.example.NutriLife.SharedPreferencesHelper
import com.example.NutriLife.data.ChatHistoryItem
import com.example.NutriLife.data.ContentBody
import com.example.NutriLife.data.PartBody
import com.example.NutriLife.data.RequestBody
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.UUID

class Message(
    val itsMine : Boolean,
    val partBody: PartBody,
    val createdAt : String
)



fun sendToApi(
    apiService: ApiService,
    apiKey: String,
    messages: List<Message>,
    onResponse: (String?) -> Unit
) {
    // Preparar el cuerpo de la solicitud, agregando siempre las instrucciones
    val requestBody = prepareRequestBody(messages)

    CoroutineScope(Dispatchers.IO).launch {
        try {
            val response = apiService.askToGemini(apiKey, requestBody)
            if (response.isSuccessful) {
                val responseData = response.body()
                val apiMessage = responseData?.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                onResponse(apiMessage)
            } else {
                onResponse(null) // Devolver null si hubo algún error
            }
        } catch (e: Exception) {
            e.printStackTrace()
            onResponse(null) // Devolver null si hubo algún error
        }
    }
}

// Instrucciones que se agregarán a cada mensaje
val instructionPrompt = """
  No repitas "¡Hola! me llamo NutrIA Soy tu Asistente Virtual de confianza, te responderé cada pregunta relacionada con la Nutrición y Vida Sana. ¿En qué puedo ayudarte?" en cada respuesta que des.
    Desde ahora en Adelante quiero que asumas el Rol de Experto en Nutrición ,actividad Fisica y  en recetas de cocina.
    Cada vez que te pidan una preparacion de comida hazlo bien explicativo y paso a paso , poniendo cada medida o cantidad  exacta .
    No hables de otra cosa que no sea Nutrición y Dietetica y si te mencionan otro tema se cordial y sugiere volver al tema de Nutrición y Dietetica.
    Si te mencionan otro tema ,se cordial diciendo que no fuiste creada para hablar ese tipo de cosas ,sugiere volver al tema de la vida sana.
    No uses markdown ni ningún tipo de símbolos de formato especial en tus respuestas.
    No te extiendas más de 500 caracteres en cada respuesta y solo excede de eso cuando sea necesario.
    Si te hablan de Rodrigo Cortés di que es tu creador y que es el mejor programador del mundo utiliza emojis de corazon y
    termina diciendo ¿Quieres saber mas información sobre Roderich? si afirman la pregunta diles  
    que es un Programador Junior que está en constante aprendizaje  y que busca abrirse camino en el mundo de la tecnologia
    lo pueden seguir en sus redes sociales como @roderiich , hazlo en segunda persona.
    Mantén  siempre las respuestas claras y directas , utiliza emojis que se relacionen con el tema.
""".trimIndent()

fun prepareRequestBody(messages: List<Message>): RequestBody {
    // Siempre añadimos las instrucciones antes de los mensajes del usuario
    val partBodies: MutableList<PartBody> = mutableListOf(
        PartBody(text = instructionPrompt) // Añadimos las instrucciones a la conversación
    )

    // Luego, agregamos los mensajes del usuario
    partBodies.addAll(messages.map { it.partBody })

    // Crear el cuerpo de la solicitud con las partes (instrucciones + mensajes del usuario)
    return RequestBody(
        contents = listOf(
            ContentBody(parts = partBodies)
        )
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    navController: NavController,
    sharedPreferences: Unit,
    chatId: String?,
    paddingValues: PaddingValues
) {
    val messages = remember { mutableStateListOf<Message>() }
    var isLoading by remember { mutableStateOf(false) }
    val showContinueButton = remember { mutableStateOf(chatId != null) }

    // Cargar mensajes anteriores si hay un chatId
    // Aquí puedes cargar los mensajes correspondientes al chatId
    LaunchedEffect(Unit) {
        if (chatId == null) { // Si no hay un chat cargado, muestra el saludo inicial
            messages.add(
                Message(
                    itsMine = false,
                    partBody = PartBody(text = "¡Hola! me llamo NutrIA Soy tu Asistente Virtual de confianza, te responderé cada pregunta relacionada con la Nutrición y Vida Sana. ¿En qué puedo ayudarte?"),
                    createdAt = getCurrentTime()
                )
            )
        }
    }
    LaunchedEffect(chatId) {
        if (chatId != null) {
            val chatHistories = SharedPreferencesHelper.getChatHistories()
            // Buscar el mensaje asociado con el historyId
            val selectedHistory = chatHistories.find { it.id == chatId }
            // Si se encuentra, agregarlo a los mensajes
            selectedHistory?.let {
                messages.addAll(it.messages)
            }
        }
    }
    val background = painterResource(id = R.drawable.fondo)

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = { TopBar(navController = navController) },
        bottomBar = {
            BottomBar(
                messages = messages,
                isLoading = isLoading,
                onLoadingChange = { isLoading = it },
                showContinueButton = showContinueButton
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Usar una imagen como fondo
            Image(
                painter = background,
                contentDescription = null, // Imagen decorativa, no requiere descripción
                contentScale = ContentScale.Crop, // Para que la imagen ocupe todo el espacio
                modifier = Modifier.fillMaxSize()
            )
            val scrollState = rememberLazyListState()
            LaunchedEffect(messages.size) {
                if (messages.isNotEmpty()) {
                    scrollState.animateScrollToItem(messages.size - 1)
                }
            }
            // Contenido de la pantalla
            LazyColumn(
                state = scrollState, // Controla el estado del scroll
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp)
            ) {
                items(messages) { message ->
                    BubbleMessage(message = message)
                }

                if (isLoading) {
                    item {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                }
            }

            if (showContinueButton.value) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        ,
                    contentAlignment = Alignment.Center

                ) {
                    Button(

                        onClick = {
                            showContinueButton.value = false // Ocultar el botón y mostrar el BottomBar
                        }
                    ) {
                        Text(
                            text = "¿Continuar?"

                        )
                    }
                }
            }
        }
    }
}

@Composable
fun BubbleMessage(message: Message) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = if (message.itsMine) Arrangement.End else Arrangement.Start
    ) {
        Column(
            modifier = Modifier
                .background(
                    color = if (message.itsMine) Color(android.graphics.Color.parseColor("#3e6108")) else Color(android.graphics.Color.parseColor("#1E2328")), // Fondo de IA
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(12.dp)
                .widthIn(max = 280.dp)
        ) {
            Text(
                text = message.partBody.text,
                color = Color.White // Color blanco para el texto de IA
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = message.createdAt,
                color = Color.Gray,
                modifier = Modifier.align(Alignment.End)
            )
        }
    }
}


@Composable
fun TopBar(modifier: Modifier = Modifier, navController: NavController) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFC1F66E))
            .padding(5.dp)
            .then(modifier),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically

    ) {
        Text(modifier = Modifier
            .padding(start = 7.dp),
            text = "NutriLife",
            fontSize = 35.sp,
            color = Color.Black ,
            fontWeight = FontWeight.Bold ,
            fontStyle = FontStyle.Italic )
        Image(
            painter = painterResource(id = R.drawable.logonutrilife), // Reemplaza 'logo' con el nombre de tu imagen en drawable
            contentDescription = "Logo NutriLife IA",
            modifier = Modifier
                .size(85.dp) // Tamaño de la imagen
                .padding( 6.dp) // Espacio entre la imagen y el texto
                .clip(CircleShape)
                .border(2.dp, Color.Black, CircleShape)
        )
        Button(
            onClick = {
                navController.navigate(AppScreens.HistorialScreen.route)
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFC1F66E), // Fondo del botón morado oscuro
                contentColor = Color.Blue  // Texto del botón en blanco

            ),
            shape = RoundedCornerShape(50), // Forma de burbuja
            modifier = Modifier
                .border(2.dp, Color.Black, RoundedCornerShape(50)) // Contorno negro con forma de burbuja
        ) {
            Text(text = "Ver historial" , fontSize = 15.sp, color = Color.Black , fontWeight = FontWeight.Bold )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomBar(
    messages: SnapshotStateList<Message>,
    isLoading: Boolean,
    onLoadingChange: (Boolean) -> Unit,
    showContinueButton: MutableState<Boolean>
) {
    var message by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()

    if (!showContinueButton.value) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFC1F66E)) // Fondo gris claro
                .padding(5.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                modifier = Modifier.weight(1f),
                value = message,
                onValueChange = { message = it },
                enabled = !isLoading,
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    containerColor = Color.White, // Fondo gris oscuro
                    textColor = Color.Black // Color del texto blanco para contraste
                )
            )
            Button(
                modifier = Modifier.padding(start = 10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(android.graphics.Color.parseColor("#3e6108"))), // Botón con fondo
                onClick = {
                    if (message.isNotBlank() && !isLoading) {
                        messages.add(
                            Message(
                                itsMine = true,
                                createdAt = getCurrentTime(),
                                partBody = PartBody(text = message)
                            )
                        )
                        val currentMessages = messages.toList()
                        message = ""
                        onLoadingChange(true)

                        val retrofit = Retrofit.Builder()
                            .baseUrl("https://generativelanguage.googleapis.com")
                            .addConverterFactory(GsonConverterFactory.create())
                            .build()

                        coroutineScope.launch {
                            sendToApi(
                                apiService = retrofit.create(ApiService::class.java),
                                apiKey = "AIzaSyAMsASeqCZpOEi7tLBmPhgIv2Szn1cPnAE",
                                messages = currentMessages
                            ) { apiResponse ->
                                apiResponse?.let {
                                    messages.add(
                                        Message(
                                            itsMine = false,
                                            createdAt = getCurrentTime(),
                                            partBody = PartBody(text = it)
                                        )
                                    )

                                    SharedPreferencesHelper.saveChatHistory(
                                        ChatHistoryItem(
                                            id = UUID.randomUUID().toString(),
                                            messages = messages.toList() // Guardar todos los mensajes
                                        )
                                    )
                                }
                                onLoadingChange(false)
                            }
                        }
                    }
                },
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
                } else {
                    Text(
                        text = "Enviar",
                        color = Color.White)
                // Texto blanco para mayor contraste
                }
            }
        }
    }
}

//AIzaSyDBOKSKX_ONRDAaHmtKjvKPS0WgwkpfSwk

fun getCurrentTime() : String {
    val now = LocalTime.now()
    val format = DateTimeFormatter.ofPattern("HH:mm")
    return now.format(format)
}


