package Pantallas.Plantillas

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.prueba3.ui.theme.BlueBackground
import androidx.compose.runtime.*
import java.io.File


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuBottomBar(navController: NavController, userRole: String?) {

    var showDialog by remember { mutableStateOf(false) }
    var showLogoutConfirmationDialog by remember { mutableStateOf(false) }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("En construcción") },
            text = { Text("Esta sección de notificaciones estará disponible próximamente.") },
            confirmButton = {
                Button(onClick = { showDialog = false }) {
                    Text("Aceptar")
                }
            }
        )
    }

    if (showLogoutConfirmationDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutConfirmationDialog = false },
            title = { Text("Cerrar sesión") },
            text = { Text("¿Estás seguro que quieres cerrar la sesión?") },
            confirmButton = {
                Button(onClick = {
                    showLogoutConfirmationDialog = false
                    clearSession(navController)
                }) {
                    Text("Sí")
                }
            },
            dismissButton = {
                Button(onClick = { showLogoutConfirmationDialog = false }) {
                    Text("No")
                }
            }
        )
    }

    // Barra inferior
    BottomAppBar(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.1f) // El recuadro ocupa el 10% de la altura
            .padding(horizontal = 8.dp)
            .clip(RoundedCornerShape(16.dp)),
        actions = {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center // Centra horizontalmente el Row dentro del Box
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(), // El Row ocupa toda la altura del Box (BottomAppBar.actions)
                    horizontalArrangement = Arrangement.SpaceEvenly, // Distribución equitativa horizontal
                    verticalAlignment = Alignment.CenterVertically // Centrado vertical de los íconos
                ) {
                    IconButton(onClick = {
                        val destination = if (userRole == "Alumno") "Menu Alumno" else if (userRole == "Personal Academico" || userRole == "Docente") "Menu Docente" else "Menu"
                        navController.navigate(destination)
                    }) {
                        Icon(
                            imageVector = Icons.Filled.Home,
                            contentDescription = "Inicio",
                            tint = BlueBackground,
                            modifier = Modifier.size(36.dp)
                        )
                    }

                    IconButton(onClick = { showDialog = true }) {
                        Icon(
                            imageVector = Icons.Filled.Notifications,
                            contentDescription = "Notificaciones",
                            tint = BlueBackground,
                            modifier = Modifier.size(36.dp)
                        )
                    }

                    IconButton(onClick = {
                        navController.navigate("Calendar")
                    }) {
                        Icon(
                            imageVector = Icons.Filled.DateRange,
                            contentDescription = "Calendario",
                            tint = BlueBackground,
                            modifier = Modifier.size(36.dp)
                        )
                    }

                    IconButton(onClick = {
                        showLogoutConfirmationDialog = true
                    }) {
                        Icon(
                            imageVector = Icons.Default.ExitToApp,
                            contentDescription = "Cerrar sesión",
                            tint = BlueBackground,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                }
            }
        }
    )
}

fun clearCache(context: Context) {
    try {
        val dir: File = context.cacheDir
        dir.listFiles()?.forEach { file ->
            if (file.isFile) {
                file.delete()
            } else {
                file.deleteRecursively()
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun clearSession(navController: NavController) {
    val context = navController.context
    try {
        clearCache(context)

        val sharedPreferences = context.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)

        val edit = sharedPreferences.edit()

        edit.putString("username", "")
        edit.putString("userRole", "")
        edit.clear()
        edit.apply()

        navController.navigate("login")
    } catch (e: Exception) {
        e.printStackTrace()
    }
}