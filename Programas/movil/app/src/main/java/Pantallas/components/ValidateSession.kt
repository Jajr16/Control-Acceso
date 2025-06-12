package Pantallas.components

import Pantallas.Plantillas.clearSession
import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
    // Obtén las preferencias compartidas
    val sharedPreferences = navController.context.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
    val userRole = sharedPreferences.getString("userRole", null)

    // Lista de pantallas excluidas de la validación
    val excludedScreens = listOf("login")

    // Variable para evitar redirecciones múltiples
    val isRedirecting = remember { mutableStateOf(false) }

    // Efecto lanzado al cambiar el estado del usuario o la navegación
    if (sharedPreferences.getString("userRole", null) == null ||
        sharedPreferences.getString("userRole", null) == "") {
        LaunchedEffect(userRole) {
            val currentRoute = navController.currentDestination?.route
            println("ValidateSession ejecutándose en: $currentRoute")
            println("ValidateSession el userRole es: $userRole")

            if (isRedirecting.value) return@LaunchedEffect

            // Si no hay sesión válida, redirigir a login

            println("Sesión inválida. Redirigiendo a login desde $currentRoute")
            isRedirecting.value = true
            navController.navigate("login")

            clearSession(navController)
        }
    } else if (sharedPreferences.getString("userRole", null) != null ||
        sharedPreferences.getString("userRole", null) != ""){
        println("El userRola es: $userRole")
        onValidSession()
    }


}
