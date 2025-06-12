package Pantallas

import Pantallas.Plantillas.WelcomeScreenBase
import Pantallas.components.DataStoreManager
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.prueba3.R
import com.example.prueba3.Views.CamaraViewModel
import com.example.prueba3.Views.LoginViewModel
import com.example.prueba3.Views.PersonaViewModel
import com.example.prueba3.ui.theme.BlueBackground
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun WelcomeScreenAlumno(navController: NavController, loginViewModel: LoginViewModel, cameraViewModel: CamaraViewModel, viewModel: PersonaViewModel = androidx.lifecycle.viewmodel.compose.viewModel()) {

    MostrarVentanaPrivacidad()

    val sharedPreferences = navController.context
        .getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
    val username = sharedPreferences.getString("username", "") ?: ""

    LaunchedEffect(username) {
        viewModel.obtenerDatos(username)
    }

    LaunchedEffect(Unit) {
        cameraViewModel.setPythonResponse(null)
    }

    val datos by viewModel.datosPersona.collectAsState()
    val nombreUsuario = datos.firstOrNull()?.nombre ?: ""
    val appellidoP = datos.firstOrNull()?.apellidoP ?: ""
    val appellidoM = datos.firstOrNull()?.apellidoM ?: ""
    val mensaje = if (nombreUsuario.isNotEmpty()) "Bienvenido $nombreUsuario $appellidoP $appellidoM " else "Bienvenido Alumno"

    WelcomeScreenBase(navController, loginViewModel, mensaje) {
        Spacer(modifier = Modifier.height(2.dp))

        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            OptionButton(
                title = "Listado de ETS",
                icon = ImageVector.vectorResource(id = R.drawable.exam),
                onClick = { navController.navigate("LETSA") },
                modifier = Modifier.weight(1f)
            )

            OptionButton(
                title = "Información de acceso",
                icon = Icons.Default.Info,
                onClick = { navController.navigate("info") },
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            OptionButton(
                title = "Probar reconocimiento facial",
                icon = ImageVector.vectorResource(id = R.drawable.icon_camara),
                onClick = { navController.navigate("camara/$username/Valor") },
                modifier = Modifier.weight(2f)
            )
        }
    }
}

@Composable
fun OptionButton(
    title: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0xFFF5F5F5))
            .clickable(onClick = onClick)
            .padding(16.dp)
            .fillMaxWidth()
            .heightIn(min = 100.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                modifier = Modifier.size(48.dp),
                tint = BlueBackground
            )
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

val BlueBackground = Color(0xFF00395D)

@Composable
fun MostrarVentanaPrivacidad() {
    val context = LocalContext.current
    val dataStore = remember { DataStoreManager(context) }

    val accepted by dataStore.privacyAccepted.collectAsState(initial = false)
    var showDialog by rememberSaveable { mutableStateOf(false) }

    // Solo mostramos el diálogo una vez cuando se sabe que no ha aceptado
    LaunchedEffect(accepted) {
        if (!accepted) {
            showDialog = true
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = {}, // Evita que se cierre al tocar afuera
            title = { Text("Política de privacidad") },
            text = {
                Text(
                        "Con el objetivo de brindarte una experiencia más segura y eficiente dentro de esta aplicación, se te informa que como parte del proceso de autenticación, se tomará una fotografía tuya con fines exclusivos de reconocimiento facial.\n" +
                        "\n" +
                        "La imagen capturada será utilizada únicamente para validar tu identidad durante el uso de la aplicación. Esta imagen:\n" +
                        "\n" +
                        "- Se almacenará de manera segura en servidores protegidos.\n" +
                        "\n" +
                        "- No será compartida, difundida ni utilizada para ningún otro propósito distinto al aquí mencionado.\n" +
                        "\n" +
                        "- No será usada con fines publicitarios, comerciales ni maliciosos.\n" +
                        "\n" +
                        "Al presionar el botón \"Aceptar\", estás dando tu consentimiento explícito para que la aplicación capture tu fotografía y la utilice con el fin mencionado." +
                        ".")
            },
            confirmButton = {
                Button(onClick = {
                    CoroutineScope(Dispatchers.IO).launch {
                        dataStore.setPrivacyAccepted(true)
                    }
                    showDialog = false
                }) {
                    Text("Aceptar")
                }
            }
        )
    }
}
