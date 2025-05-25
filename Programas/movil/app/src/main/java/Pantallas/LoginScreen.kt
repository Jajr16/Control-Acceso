package Pantallas

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import com.example.prueba3.Clases.getFCMToken
import com.example.prueba3.R
import androidx.compose.foundation.layout.imePadding // Importar imePadding

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
    val scrollState = rememberScrollState()

    LaunchedEffect(Unit) {
        loginViewModel.getUserRole()?.let { savedRole ->
            if (savedRole != null || savedRole != "") {
                val savedUsername = loginViewModel.getUserName()

                if (savedRole in listOf("Alumno", "Personal Academico", "Docente") && savedUsername != null) {
                    getFCMToken(savedUsername)
                }

                when (savedRole) {
                    "Alumno" -> navController.navigate("Menu Alumno") {
                        popUpTo("login") { inclusive = true } }
                    "Personal Seguridad" -> navController.navigate("Menu") {
                        popUpTo("login") { inclusive = true } }
                    "Docente" -> navController.navigate("Menu Docente") {
                        popUpTo("login") { inclusive = true } }
                    "Jefe Departamento" -> navController.navigate("Menu Academico") {
                        popUpTo("login") { inclusive = true } }
                    "Presidente de academia" -> navController.navigate("Menu Academico") {
                        popUpTo("login") { inclusive = true } }
                }
            }
        }
    }

    LaunchedEffect(loginResponse) {
        loginResponse?.let {
            if (it.error_code == 0) {
                loginViewModel.saveUserName(it.usuario)
                loginViewModel.saveUserRole(it.rol)

                when (it.rol) {
                    "Alumno", "Personal Academico", "Docente" -> {
                        getFCMToken(it.usuario)
                    }
                }

                when (it.rol) {
                    "Alumno" -> navController.navigate("Menu Alumno") {
                        popUpTo(navController.graph.startDestinationId) { inclusive = true } }
                    "Personal Seguridad" -> navController.navigate("scanQr") {
                        popUpTo("login") { inclusive = true } }
                    "Docente" -> navController.navigate("Menu Docente") {
                        popUpTo("login") { inclusive = true } }
                    "Jefe Departamento" -> navController.navigate("Menu Academico") {
                        popUpTo("login") { inclusive = true } }
                }

                it.rol = ""
                it.usuario= ""
                it.message = ""
            }
        }
    }

    LaunchedEffect(loginError) {
        loginError?.let {
            errorMessage = it
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 30.dp, vertical = 30.dp)
            .imePadding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.escom),
            contentDescription = "Logo IPN",
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .padding(top = 90.dp, bottom = 40.dp),
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White, shape = RoundedCornerShape(30.dp))
                .padding(horizontal = 20.dp, vertical = 20.dp)
                .imePadding(),
        ) {
            Text(
                text = "Iniciar sesión",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier
                    .padding(bottom = 16.dp)
                    .align(Alignment.CenterHorizontally),
                fontWeight = FontWeight.Bold
            )

            OutlinedTextField (
                value = boleta,
                onValueChange = { boleta = it },
                label = { Text("Usuario", style = MaterialTheme.typography.labelMedium) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(50.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFFF0F0F0),
                    unfocusedContainerColor = Color(0xFFF0F0F0),
                ),
                leadingIcon = {
                    Icon(imageVector = Icons.Default.AccountCircle, contentDescription = "")
                }
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField (
                value = password,
                onValueChange = { password = it },
                label = { Text("Contraseña", style = MaterialTheme.typography.labelMedium) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                shape = RoundedCornerShape(50.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFFF0F0F0),
                    unfocusedContainerColor = Color(0xFFF0F0F0),
                ),
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Lock, contentDescription = "")
                }
            )

            Spacer(modifier = Modifier.height(30.dp))

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

            if (errorMessage.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                val mensajeAMostrar = if (errorMessage.contains("401", ignoreCase = true)) {
                    "Error al intentar iniciar sesión, contraseña y/o usuario incorrectos"
                } else {
                    errorMessage
                }
                Text(
                    text = mensajeAMostrar,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }
            // Mantenemos este Spacer para darle un "colchón" extra al final del contenido,
            // pero imePadding() será el principal controlador del desplazamiento del teclado.
            Spacer(modifier = Modifier.height(60.dp))
        }
    }
}