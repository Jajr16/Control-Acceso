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
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.lazy.rememberLazyListState
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.example.prueba3.Clases.Mensaje
import androidx.localbroadcastmanager.content.LocalBroadcastManager

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

        var message by remember { mutableStateOf("") }
        val username = loginViewModel.getUserName()
        val mensajes by mensajesViewModel.mensajes.collectAsState()
        val errorMessage by mensajesViewModel.errorMessage.collectAsState()
        val usuarioActual = loginViewModel.getUserName()

        LaunchedEffect(mensajes) {
            if (mensajes.isNotEmpty()) {
                coroutineScope.launch {
                    lazyListState.animateScrollToItem(mensajes.size - 1)
                }
            }
        }

        val destinatarioActual = remember(destinatario) { destinatario }
        val usuarioActualRemembered = remember(usuarioActual) { usuarioActual }
        val updateMessageReceiver = remember {
            object : BroadcastReceiver() {
                override fun onReceive(context: Context?, intent: Intent?) {
                    FirebaseCrashlytics.getInstance().log("ChatScreen: Broadcast Recibido - Action=${intent?.action}")
                    Log.d("ChatScreen", "Broadcast recibido: ${intent?.action}")

                    if (intent?.action == MessageUpdateService.ACTION_UPDATE_MESSAGES) {

                        val broadcastRemitente = intent?.getStringExtra("remitente")
                        val broadcastDestinatario = intent?.getStringExtra("destinatario")

                        Log.d("ChatScreen", "Broadcast recibido: remitente=$broadcastRemitente, destinatario=$broadcastDestinatario")
                        FirebaseCrashlytics.getInstance().log("ChatScreen: Datos del Broadcast - Remitente=$broadcastRemitente, Destinatario=$broadcastDestinatario, Usuario Actual=$usuarioActualRemembered, Destinatario Actual=$destinatarioActual")
// Refrescar los mensajes si la conversación actual coincide
                        if ((broadcastRemitente == usuarioActualRemembered && broadcastDestinatario == destinatarioActual) ||
                            (broadcastRemitente == destinatarioActual && broadcastDestinatario == usuarioActualRemembered)) {
                            Log.d("ChatScreen", "Si entró")
                            FirebaseCrashlytics.getInstance().log("ChatScreen: Conversación coincidente. Llamando a getMessages()")
                            coroutineScope.launch { // Usar coroutineScope para llamadas suspend
                                mensajesViewModel.getMessages(usuarioActualRemembered ?: "", destinatarioActual)
                            }
                        } else {
                            FirebaseCrashlytics.getInstance().log("ChatScreen: Conversación NO coincidente.")
                        }
                    }
                }
            }
        }

        val filter = IntentFilter(MessageUpdateService.ACTION_UPDATE_MESSAGES)
        val localBroadcastManager = LocalBroadcastManager.getInstance(context)

        DisposableEffect(context, updateMessageReceiver) {
            localBroadcastManager.registerReceiver(updateMessageReceiver, filter)
            onDispose { localBroadcastManager.unregisterReceiver(updateMessageReceiver) }
        }

        LaunchedEffect(destinatarioActual, usuarioActualRemembered) {
            Log.d("ChatScreen", "LaunchedEffect triggered para cargar mensajes con destinatario=$destinatarioActual, usuario=$usuarioActualRemembered")
            FirebaseCrashlytics.getInstance().log("ChatScreen: LaunchedEffect triggered para cargar mensajes con remitente = $usuarioActualRemembered, destinatario = $destinatarioActual")
            if (usuarioActualRemembered != null) {
                coroutineScope.launch {
                    mensajesViewModel.getMessages(usuarioActualRemembered, destinatarioActual)
                }
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
            bottomBar = {}
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(BlueBackground)
                    .padding(padding)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    val nuevoMensajeEnviado by mensajesViewModel.nuevoMensajeEnviado.collectAsState(initial = null)

                    LaunchedEffect(nuevoMensajeEnviado) {
                        if (nuevoMensajeEnviado != null) {
                            Log.d("ChatScreen", "Evento nuevoMensajeEnviado recibido")
                            FirebaseCrashlytics.getInstance().log("ChatScreen: Evento nuevoMensajeEnviado recibido. Recargando mensajes.")
                            if (usuarioActualRemembered != null) {
                                coroutineScope.launch {
                                    mensajesViewModel.getMessages(usuarioActualRemembered, destinatarioActual)
                                }
                            }
                        }
                    }

                    LazyColumn(
                        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                        state = lazyListState,
                        contentPadding = PaddingValues(vertical = 8.dp)
                    ) {
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

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp)
                        .imePadding()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(2.dp, Color.Gray, RoundedCornerShape(16.dp))
                            .background(Color.White, RoundedCornerShape(16.dp))
                            .padding(horizontal = 10.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        BasicTextField(
                            value = message,
                            onValueChange = { newText -> message = newText },
                            modifier = Modifier
                                .weight(1f),
                            textStyle = TextStyle(fontSize = 16.sp, color = Color.Black),
                            maxLines = 4
                        )

                        IconButton(
                            onClick = {
                                if (message.isNotBlank()) {
                                    if (username != null) {
                                        FirebaseCrashlytics.getInstance()
                                            .log("ChatScreen: Intentando enviar mensaje: $message " +
                                                    "a $destinatario desde $username")

                                        coroutineScope.launch {
                                            mensajesViewModel.sendMessage(
                                                username,
                                                destinatario,
                                                message
                                            )
                                        }
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
        }
    }
}

@Composable
fun MensajeItem(mensaje: Mensaje, usuarioActual: String) {
    val esUsuarioActual = mensaje.usuario == usuarioActual
    val modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 4.dp)

    Row(modifier = modifier, horizontalArrangement = if (esUsuarioActual) Arrangement.End else Arrangement.Start) {
        if (esUsuarioActual) {
            MensajeMio(mensaje)
        } else {
            MensajeOtro(mensaje)
        }
    }
}

@Composable
fun MensajeMio(mensaje: Mensaje) {
    Box(
        modifier = Modifier
            .background(Color(0xFFDCF8C6), RoundedCornerShape(16.dp, 16.dp, 0.dp, 16.dp))
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Text(text = mensaje.mensaje, color = Color.Black)
    }
}

@Composable
fun MensajeOtro(mensaje: Mensaje) {
    Box(
        modifier = Modifier
            .background(Color(0xFFEEEEEE), RoundedCornerShape(16.dp, 0.dp, 16.dp, 16.dp))
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Text(text = mensaje.mensaje, color = Color.Black)
    }
}