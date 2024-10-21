import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.NutriLife.Navigation.AppScreens
import com.example.NutriLife.screens.ChatScreen
import com.example.NutriLife.screens.HistorialScreen

 @OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation(sharedPreferences: Unit) {
     // Creamos el controlador de navegación
     val navController: NavHostController = rememberNavController()

     // Scaffold para estructurar la UI, aquí podrías agregar una barra inferior si la necesitas
     Scaffold(
         bottomBar = {
             // En caso de que necesites una barra de navegación inferior
             val currentRoute = navController.currentBackStackEntry?.destination?.route
             // Puedes agregar aquí tu barra de navegación
         }
     ) { paddingValues -> // paddingValues se usan para evitar solapamiento con la barra

         // Definimos el NavHost que maneja la navegación entre pantallas

         NavHost(
             navController = navController,
             startDestination = AppScreens.ChatScreen.route
         ) {
             // Ruta sin chatId
             composable(AppScreens.ChatScreen.route) {
                 ChatScreen(navController, sharedPreferences, chatId = null, paddingValues)
             }

             // Ruta con chatId
             composable(AppScreens.ChatScreen.route + "/{chatId}") { backStackEntry ->
                 val chatId = backStackEntry.arguments?.getString("chatId")
                 ChatScreen(navController, sharedPreferences, chatId, paddingValues)
             }

             composable(AppScreens.HistorialScreen.route) {
                 HistorialScreen(navController)
             }
         }
     }
 }