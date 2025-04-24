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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.prueba3.MessageUpdateService
import kotlinx.coroutines.launch
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import com.example.prueba3.Clases.Mensaje


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ChatScreen(
    navController: NavHostController,
    destinatario: String,
    nombreDesti: String,
    loginViewModel: LoginViewModel,
    mensajesViewModel: MensajesViewModel = viewModel()
) {

    ValidateSession(navController = navController) {
        val lazyListState = rememberLazyListState()

        val context = LocalContext.current
        val coroutineScope = rememberCoroutineScope()
        val receiver = remember {
            object : BroadcastReceiver() {
                override fun onReceive(context: Context?, intent: Intent?) {
                    val remitente = intent?.getStringExtra("remitente") ?: ""
                    val destinatario = intent?.getStringExtra("destinatario") ?: ""
                    coroutineScope.launch {
                        mensajesViewModel.refreshMessages(remitente, destinatario)
                    }
                }
            }
        }

        DisposableEffect(receiver) {
            val filter = IntentFilter(MessageUpdateService.ACTION_UPDATE_MESSAGES)
            context.registerReceiver(receiver, filter, Context.RECEIVER_NOT_EXPORTED)
            onDispose { context.unregisterReceiver(receiver) }
        }

        var message by remember { mutableStateOf("") }
        val username = loginViewModel.getUserName()
        val mensajes by mensajesViewModel.mensajes.collectAsState()
        val errorMessage by mensajesViewModel.errorMessage.collectAsState()

        val usuarioActual = loginViewModel.getUserName()

        LaunchedEffect(mensajes) { // Scroll to the bottom after messages update
            if (mensajes.isNotEmpty()) {
                lazyListState.animateScrollToItem(mensajes.size -1) // Scroll to last item
            }
        }

        val initialLoad = remember { mutableStateOf(true) }
        val destinatarioActual = remember(destinatario) { destinatario }
        val usuarioActualRemembered = remember(usuarioActual) { usuarioActual }

        LaunchedEffect(destinatarioActual, usuarioActualRemembered, mensajes) {
            Log.d("ChatScreen", "LaunchedEffect triggered")
            if (initialLoad.value && usuarioActualRemembered != null) {
                mensajesViewModel.getMessages(usuarioActualRemembered, destinatarioActual)
                initialLoad.value = false
            }
        }

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
                                        mensajesViewModel.sendMessage(
                                            username,
                                            destinatario,
                                            message
                                        )
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
            ) {
                val nuevoMensajeEnviado = mensajesViewModel.nuevoMensajeEnviado.collectAsState(initial = null)

                LaunchedEffect(nuevoMensajeEnviado.value, message) {
                    if (nuevoMensajeEnviado.value != null) {
                        if (usuarioActualRemembered != null) {
                            mensajesViewModel.getMessages(usuarioActualRemembered, destinatarioActual)
                        }
                    }
                }

                LazyColumn(modifier = Modifier.padding(16.dp), state = lazyListState) {
                    items(mensajes) { mensaje ->
                        Log.d("ChatScreen", "Rendering message: $mensaje")
                        MensajeItem(mensaje, usuarioActualRemembered ?: "")
                    }
                    if (errorMessage != null) {
                        item {
                            Text(text = errorMessage!!, color = Color.Red)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MensajeItem(mensaje: Mensaje, usuarioActual: String) {
    val esUsuarioActual = mensaje.usuario == usuarioActual
    val modifier = Modifier
        .fillMaxWidth()
        .padding(8.dp)

    Row(modifier = modifier, horizontalArrangement = if (esUsuarioActual) Arrangement.End else Arrangement.Start) {
        // Si es el usuario actual:
        if (esUsuarioActual) {
            MensajeMio(mensaje)
        } else { //Si no es el usuario actual
            MensajeOtro(mensaje)
        }
    }
}

@Composable
fun MensajeMio(mensaje: Mensaje) {
    Box(
        modifier = Modifier
            .background(Color(0xFFDCF8C6), RoundedCornerShape(16.dp, 16.dp, 0.dp, 16.dp))
            .padding(16.dp)
    ) {
        Text(text = mensaje.mensaje, color = Color.Black)
    }
}


@Composable
fun MensajeOtro(mensaje: Mensaje) {
    Box(
        modifier = Modifier
            .background(Color(0xFFEEEEEE), RoundedCornerShape(16.dp, 0.dp, 16.dp, 16.dp))
            .padding(16.dp)
    ) {
        Text(text = mensaje.mensaje, color = Color.Black)
    }
}