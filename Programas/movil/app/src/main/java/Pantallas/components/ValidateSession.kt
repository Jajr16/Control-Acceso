package Pantallas.components

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavController

/**
 * Componente reutilizable para validar la sesión del usuario.
 * Redirige al usuario a la pantalla de login si no tiene una sesión válida.
 *
 * @param navController Controlador de navegación.
 * @param onValidSession Contenido a renderizar si la sesión es válida.
 */
@Composable
fun ValidateSession(navController: NavController, onValidSession: @Composable () -> Unit) {
    val sharedPreferences = navController.context.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
    val userRole = sharedPreferences.getString("userRole", null)

    LaunchedEffect(userRole) {
        if (userRole == null) {
            navController.navigate("login") {
                popUpTo(navController.graph.startDestinationId) { inclusive = true }
            }
        }
    }

    if (userRole != null) {
        onValidSession()
    }
}
