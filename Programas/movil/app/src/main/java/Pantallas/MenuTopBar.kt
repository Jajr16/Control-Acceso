package Pantallas

import android.content.Context
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController


    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun MenuTopBar(navController: NavController, title: String) {
        var menuExpanded by remember { mutableStateOf(false) }

        val sharedPreferences = navController.context
            .getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        val userRole = sharedPreferences.getString("userRole", null)

        // Barra superior con el color morado
        TopAppBar(
            title = { Text(text = title) },
            actions = {
                IconButton(onClick = { menuExpanded = !menuExpanded }) {
                    Icon(
                        imageVector = Icons.Filled.MoreVert,
                        contentDescription = "Menú",
                        tint = Color.White
                    )
                }
                // Menú desplegable
                DropdownMenu(
                    expanded = menuExpanded,
                    onDismissRequest = { menuExpanded = false }
                ) {

                    when (userRole) {
                        "Alumno" -> DropdownMenuItem(
                            text = { Text("Inicio") },
                            onClick = {
                                menuExpanded = false
                                navController.navigate("Menu Alumno")
                            }
                        )
                        else -> DropdownMenuItem(
                            text = { Text("Inicio") },
                            onClick = {
                                menuExpanded = false
                                navController.navigate("Menu")
                            }
                        )
                    }

                    DropdownMenuItem(
                        text = { Text("Cerrar Sesión") },
                        onClick = {
                            menuExpanded = false

                            val editor = sharedPreferences.edit()
                            editor.clear()
                            editor.apply()

                            navController.navigate("login") {
                                // Limpia el historial de navegación
                                popUpTo(navController.graph.startDestinationId) { inclusive = true }
                                launchSingleTop = true
                            }
                        }
                    )
                }
            },
            // Usamos el nuevo color de TopAppBar
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color(0xFF6c1d45), // Color morado para la barra
                titleContentColor = Color.White // Texto blanco
            )
        )
    }

