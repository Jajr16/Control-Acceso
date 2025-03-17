package Pantallas

import Pantallas.Plantillas.MenuTopBar
import Pantallas.components.ValidateSession
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.prueba3.Views.LoginViewModel
import com.example.prueba3.Views.MensajesViewModel
import com.example.prueba3.ui.theme.BlueBackground
import android.content.IntentFilter
import android.util.Log
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.prueba3.MessageUpdateService
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ChatScreen(
    navController: NavHostController,
    destinatario: String,
    nombreDesti: String,
    loginViewModel: LoginViewModel,
    mensajesViewModel: MensajesViewModel = viewModel()
) {

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val receiver = remember { object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val remitente = intent?.getStringExtra("remitente") ?: ""
            val destinatario = intent?.getStringExtra("destinatario") ?: ""
            coroutineScope.launch {
                mensajesViewModel.refreshMessages(remitente, destinatario)
            }
        }
    } }

    DisposableEffect(receiver) {
        val filter = IntentFilter(MessageUpdateService.ACTION_UPDATE_MESSAGES)
        context.registerReceiver(receiver, filter, Context.RECEIVER_NOT_EXPORTED)
        onDispose { context.unregisterReceiver(receiver) }
    }

    var message by remember { mutableStateOf("") }
    val username = loginViewModel.getUserName()
    val mensajes by remember { mensajesViewModel.mensajes }.collectAsState()
    val errorMessage by mensajesViewModel.errorMessage.collectAsState()

    val usuarioActual = loginViewModel.getUserName()

    Scaffold(
        topBar = {
            MenuTopBar(true, false, loginViewModel, navController,
                Component = {
                    Text(
                        text = nombreDesti,
                        fontSize = 23.sp,
                        modifier = Modifier.padding(top = 15.dp)
                    )
                }
            )
        },
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 10.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(2.dp, Color.Gray, RoundedCornerShape(16.dp))
                        .background(Color.White, RoundedCornerShape(16.dp))
                        .padding(horizontal = 10.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    BasicTextField(
                        value = message,
                        onValueChange = { newText -> message = newText },
                        modifier = Modifier
                            .weight(1f)
                            .padding(8.dp),
                        textStyle = TextStyle(fontSize = 16.sp, color = Color.Black),
                        maxLines = 2
                    )
                    IconButton(
                        onClick = {
                            if (message.isNotBlank()) {
                                if (username != null) {
                                    mensajesViewModel.sendMessage(username, destinatario, message)
                                }
                                message = ""
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Send,
                            contentDescription = "Enviar",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(BlueBackground)
                .padding(padding)
                .imePadding()
        ) {
            ValidateSession(navController = navController) {

                LaunchedEffect(destinatario, usuarioActual, mensajes) {
                    if (usuarioActual != null) {
                        mensajesViewModel.getMessages(usuarioActual, destinatario)
                    }
                }

                LazyColumn(modifier = Modifier.padding(16.dp)) {
                    items(mensajes) { mensaje ->
                        Log.d("ChatScreen", "Rendering message: $mensaje")
                        Text(text = "De: ${mensaje.usuario} - ${mensaje.fecha}: ${mensaje.mensaje}")
                    }
                    if (errorMessage != null) {
                        item {
                            Text(text = errorMessage!!)
                        }
                    }
                }
            }
        }
    }
}