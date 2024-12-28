package Pantallas

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.prueba3.Views.LoginViewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import com.example.prueba3.R

@Composable
fun LoginScreen(
    navController: NavController,
    loginViewModel: LoginViewModel
) {
    var boleta by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }

    val loginResponse = loginViewModel.loginResponse.collectAsState().value
    val loginError = loginViewModel.loginError.collectAsState().value

    LaunchedEffect(loginResponse) {
        loginResponse?.let {
            val sharedPreferences = navController.context.getSharedPreferences("MyAppPrefs",
                Context.MODE_PRIVATE)
            val userRole = sharedPreferences.getString("userRole", null)
            val editor = sharedPreferences.edit()
            editor.remove("userRole")

            if (it.Error_code == 0) {
                loginViewModel.saveUserName(it.Usuario)
                loginViewModel.saveUserRole(it.Rol)

                if (userRole != null) {
                    when (userRole) {
                        "Alumno" -> navController.navigate("Menu Alumno") {
                            popUpTo(navController.graph.startDestinationId)
                            launchSingleTop = true
                        }
                        "Personal Seguridad" -> navController.navigate("Menu"){
                            popUpTo(navController.graph.startDestinationId)
                            launchSingleTop = true
                        }
                        "Docente" -> navController.navigate("Menu"){
                            popUpTo(navController.graph.startDestinationId)
                            launchSingleTop = true
                        }

                        else -> errorMessage = "Rol inválido"
                    }
                } else {
                    if (it.Message != "Login exitoso") {
                        errorMessage = it.Message
                    }
                }
            }
        }
    }

    LaunchedEffect(loginError) {
        loginError?.let {
            errorMessage = it
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize() // Hacer que el Box ocupe toda la pantalla
            .padding(16.dp, 0.dp, 16.dp, bottom = 70.dp) // Agregar margen para no pegarse a los bordes
    ) {
        Image(
            painter = painterResource(id = R.drawable.poli), // Reemplaza con el nombre de tu recurso
            contentDescription = "Logo IPN", // Accesibilidad
            modifier = Modifier
                .fillMaxWidth(0.75f)
                //.padding(start = 20.dp, top = 160.dp, end = 20.dp, bottom = 20.dp)
                .padding(start = 20.dp, top = 60.dp, end = 20.dp, bottom = 20.dp)
                .align(Alignment.TopCenter),
            contentScale = ContentScale.Crop
        )

        // Fondo blanco para el contenedor
        Column(
            modifier = Modifier
                .fillMaxWidth() // Asegurarse de que ocupe todo el ancho
                .background(Color.White, shape = RoundedCornerShape(30.dp)) // Fondo blanco
                .padding(start = 20.dp, top = 20.dp, end = 20.dp, bottom = 60.dp)
                .align(Alignment.BottomCenter)

        ) {
            // Título de la pantalla
            Text(
                text = "Iniciar sesión",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier
                    .padding(bottom = 16.dp)
                    .align(Alignment.CenterHorizontally),
                fontWeight = FontWeight.Bold
            )

            // Campo para la boleta
            OutlinedTextField (
                value = boleta,
                onValueChange = { boleta = it },
                label = { Text("Boleta",
                    style = MaterialTheme.typography.labelMedium) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(50.dp),
                colors =  TextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFFF0F0F0),
                    unfocusedContainerColor = Color(0xFFF0F0F0),
                ),
                leadingIcon = {
                    Icon(imageVector = Icons.Default.AccountCircle, contentDescription = "")
                }
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Campo para la contraseña
            OutlinedTextField (
                value = password,
                onValueChange = { password = it },
                label = { Text("Contraseña",
                    style = MaterialTheme.typography.labelMedium) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                shape = RoundedCornerShape(50.dp),
                colors =  TextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFFF0F0F0),
                    unfocusedContainerColor = Color(0xFFF0F0F0),
                    ),
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Lock, contentDescription = "")
                }
            )

            Spacer(modifier = Modifier.height(30.dp))

            // Botón de iniciar sesión
            Button(
                onClick = {
                    if (boleta.isEmpty() || password.isEmpty()) {
                        errorMessage = "Por favor, completa todos los campos."
                    } else {
                        loginViewModel.login(boleta, password)
                    }
                },
                    modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF1414E3),
                    contentColor = Color.White
                )
            ) {
                Text("Entrar")
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "¿Su cuenta está bloqueada?",
                modifier = Modifier.align(Alignment.CenterHorizontally),
            )

            Text (
                text = "Presione aquí para pedir su activación",
                style = TextStyle(
                    color = Color.Blue,  // Color del texto como hipervínculo
                    textDecoration = TextDecoration.Underline,  // Subrayado para parecer un enlace
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.clickable {
                    // Acción de navegación al hacer clic
                    if (navController != null) {
                        navController.navigate("screen2")
                    }
                }
                    .align(Alignment.CenterHorizontally),
            )

            // Mostrar mensaje de error si es necesario
            if (errorMessage.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.fillMaxWidth(), // Hace que el texto ocupe todo el ancho disponible
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}