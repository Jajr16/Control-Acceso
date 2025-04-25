package com.example.prueba3

import Pantallas.Camara
import Pantallas.CalendarScreen
import Pantallas.ChatScreen
import Pantallas.ConsultarScreen
import Pantallas.CreateAccountScreen
import Pantallas.CredencialDaeScreen
import Pantallas.CredencialScreen
import Pantallas.DetalleAlumnosScreen
import Pantallas.ETSInscriptionProcessScreen
import Pantallas.EtsDetailScreen
import Pantallas.EtsListScreen
import Pantallas.EtsListScreenAlumno
import Pantallas.InformacionAlumno
import Pantallas.ListaAlumnosScreen
import Pantallas.ListadoSolicitudesReemplazo
import Pantallas.LoginScreen
import Pantallas.MensajesScreen
import Pantallas.QRScannerScreen
import Pantallas.WelcomeScreenAlumno
import Pantallas.NotificationsScreen
import Pantallas.Reporte
import Pantallas.ScreenAsignaremplazo
import Pantallas.SolicitarReemplazo
import Pantallas.WelcomeScreen
import Pantallas.WelcomeScreenAcademico
import Pantallas.WelcomeScreenDocente
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.prueba3.Views.LoginViewModel
import com.example.prueba3.Views.AlumnosViewModel
import com.example.prueba3.Views.CamaraViewModel
import com.example.prueba3.Views.DiasETSModel
import com.example.prueba3.Views.InformacionAlumnoViewModel
import com.example.prueba3.Views.MensajesViewModel
import com.example.prueba3.ui.theme.BlueBackground
import com.example.prueba3.ui.theme.Prueba3Theme
import java.lang.Integer.parseInt
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.mutableStateOf
import androidx.compose.material3.MaterialTheme

import androidx.activity.result.contract.ActivityResultContracts
import androidx.localbroadcastmanager.content.LocalBroadcastManager

class MainActivity : ComponentActivity() {
    private val messageHandler = Handler(Looper.getMainLooper())
    private val _showDialog = mutableStateOf(false)
    private val _dialogTitle = mutableStateOf("")
    private val _dialogMessage = mutableStateOf("")

    private fun mostrarMensajeEnUI(title: String?, body: String?) {
        try {
            _dialogTitle.value = title ?: "Notificación"
            _dialogMessage.value = body ?: ""
            _showDialog.value = true
        } catch (e: Exception) {
            Log.e("MainActivity", "Error mostrando mensaje en UI: ${e.message}", e)
        }
    }


    private val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            try {
                if (intent?.action == "NOTIFICACION_RECIBIDA") {
                    val title = intent.getStringExtra("title")
                    val body = intent.getStringExtra("body")

                    // Usar Handler para asegurar que estamos en el hilo principal
                    messageHandler.post {
                        handleIncomingMessage(title, body)
                    }
                }
            } catch (e: Exception) {
                Log.e("MainActivity", "Error procesando mensaje: ${e.message}", e)
            }
        }
    }

    private fun handleIncomingMessage(title: String?, body: String?) {
        try {
            if (!isFinishing && !isDestroyed) {
                // Si la app está en primer plano, mostrar en UI
                mostrarMensajeEnUI(title, body)
            } else {
                // Si está en segundo plano, mostrar notificación
                mostrarNotificacion(title, body)
            }
        } catch (e: Exception) {
            Log.e("MainActivity", "Error manejando mensaje: ${e.message}", e)
        }
    }

    private fun mostrarNotificacion(title: String?, body: String?) {
        try {
            // Código para mostrar notificación
        } catch (e: Exception) {
            Log.e("MainActivity", "Error mostrando notificación: ${e.message}", e)
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        or View.SYSTEM_UI_FLAG_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                )

        LocalBroadcastManager.getInstance(this).registerReceiver(
            broadcastReceiver,
            IntentFilter("NOTIFICACION_RECIBIDA")
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
        }

        val sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
        val loginViewModel = LoginViewModel(sharedPreferences)
        val diasETSModel = DiasETSModel()
        val mensajesViewModel = MensajesViewModel()

        enableEdgeToEdge()
        setContent {
            Prueba3Theme {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(BlueBackground)
                ) {
                    val navController = rememberNavController()
                    val alumnosViewModel: AlumnosViewModel = viewModel()
                    val camaraViewModel: CamaraViewModel = viewModel() // Crea el CamaraViewModel aquí
                    val informacionAlumnoViewModel: InformacionAlumnoViewModel = viewModel()

                    NavHost(navController = navController, startDestination = "login") {

                        composable("login") { LoginScreen(navController, loginViewModel) }
                        composable("camara/{boleta}/{idETS}") { backStackEntry ->
                            val boleta = backStackEntry.arguments?.getString("boleta") ?: ""
                            val idETS = backStackEntry.arguments?.getString("idETS") ?: ""
                            Camara(navController, boleta, idETS, loginViewModel = loginViewModel, cameraViewModel = camaraViewModel) // Pasa camaraViewModel
                        }
                        composable("notificaciones") { NotificationsScreen(navController) }
                        composable("Menu Alumno") { WelcomeScreenAlumno(navController, loginViewModel = loginViewModel,cameraViewModel = camaraViewModel) }

                        composable("Menu") { WelcomeScreen(navController = navController, loginViewModel = loginViewModel) }
                        composable("Menu Docente") {
                            WelcomeScreenDocente(
                                navController = navController,
                                loginViewModel = loginViewModel
                            )
                        }

                        composable("Menu Academico") {
                            WelcomeScreenAcademico(
                                navController = navController,
                                loginViewModel = loginViewModel
                            )
                        }

                        composable("credencial/{boleta}") { backStackEntry ->
                            val boleta = backStackEntry.arguments?.getString("boleta") ?: ""
                            CredencialScreen(
                                navController = navController,
                                loginViewModel = loginViewModel,
                                viewModel = alumnosViewModel,
                                boleta = boleta
                            )
                        }

                        composable("detallealumnos/{boleta}") { backStackEntry ->
                            val boleta = backStackEntry.arguments?.getString("boleta") ?: ""

                            DetalleAlumnosScreen(
                                navController = navController,
                                loginViewModel = loginViewModel,
                                viewModel = alumnosViewModel,
                                boleta = boleta
                            )
                        }

                        composable("LETS") { EtsListScreen(navController, loginViewModel = loginViewModel) }

                        composable("LETSA") { EtsListScreenAlumno(navController, loginViewModel = loginViewModel) }
                        composable("scanQr") { QRScannerScreen(navController, loginViewModel = loginViewModel) }
                        composable("info") { ETSInscriptionProcessScreen(navController, loginViewModel) }
                        composable("CrearCuenta") { CreateAccountScreen(navController) }

                        composable(
                            "solicitarReemplazo/{idETS}/{nombreETS}",
                            arguments = listOf(
                                navArgument("idETS") { type = NavType.IntType },
                                navArgument("nombreETS") { type = NavType.StringType }
                            )
                        ) { backStackEntry ->
                            SolicitarReemplazo(
                                navController = navController,
                                loginViewModel = loginViewModel,
                                idETS = backStackEntry.arguments?.getInt("idETS"),
                                nombreETS = backStackEntry.arguments?.getString("nombreETS")
                            )
                        }

                        composable("listadoReemplazos") { ListadoSolicitudesReemplazo(navController = navController, loginViewModel = loginViewModel) }

                        composable(
                            "detalleReemplazo/{idETS}/{docenteRFC}",
                            arguments = listOf(
                                navArgument("idETS") { type = NavType.IntType },
                                navArgument("docenteRFC") { type = NavType.StringType }
                            )
                        ) { backStackEntry ->
                            val idETS = backStackEntry.arguments?.getInt("idETS")
                            val docenteRFC = backStackEntry.arguments?.getString("docenteRFC")

                            ScreenAsignaremplazo(
                                navController = navController,
                                loginViewModel = loginViewModel,
                                idETS = idETS,
                                docenteRFC = docenteRFC
                            )
                        }

                        composable("Calendar") { CalendarScreen(navController, loginViewModel, diasETSModel) }
                        composable(
                            route = "CredencialDAE?url={url}&boleta={boleta}", // Ruta con dos argumentos
                            arguments = listOf(
                                navArgument("url") { // Argumento "url"
                                    type = NavType.StringType // Tipo String
                                    nullable = true // Permite valores nulos
                                },
                                navArgument("boleta") { // Argumento "boleta"
                                    type = NavType.StringType // Tipo String
                                    defaultValue = "" // Valor por defecto
                                }
                            )
                        ) { backStackEntry ->
                            val url = backStackEntry.arguments?.getString("url")
                            val boleta = backStackEntry.arguments?.getString("boleta") ?: ""

                                CredencialDaeScreen(
                                    navController = navController,
                                    loginViewModel = loginViewModel,
                                    url = url,
                                    alumnosViewModel,
                                    boleta = boleta
                                )
                        }
                        composable("ConsultarAlumnos") {
                            ConsultarScreen(navController, viewModel = alumnosViewModel, loginViewModel = loginViewModel)
                        }

                        composable(
                            "Reporte/{idETS}/{boleta}/{aceptado}",
                            arguments = listOf(
                                navArgument("idETS") { type = NavType.StringType },
                                navArgument("boleta") { type = NavType.StringType },
                                navArgument("aceptado") { type = NavType.IntType }
                            )
                        ) { backStackEntry ->
                            val idETS = backStackEntry.arguments?.getString("idETS") ?: ""
                            val boleta = backStackEntry.arguments?.getString("boleta") ?: ""
                            val aceptado = backStackEntry.arguments?.getInt("aceptado") ?: 0
                            Reporte(navController, idETS, boleta, loginViewModel, viewModel = alumnosViewModel, aceptado)
                        }

                        composable(
                            "InfoA/{idETS}/{boleta}",
                            arguments = listOf(
                                navArgument("idETS") { type = NavType.StringType },
                                navArgument("boleta") { type = NavType.StringType },



                                )
                        ) { backStackEntry ->
                            val idETS = backStackEntry.arguments?.getString("idETS") ?: ""
                            val boleta = backStackEntry.arguments?.getString("boleta") ?: ""



                            InformacionAlumno(navController, idETS, boleta, loginViewModel, alumnosViewModel,
                                camaraViewModel,informacionAlumnoViewModel) // Pasa camaraViewModel
                        }

                        composable(
                            "ListaAlumnos/{idETS}",
                            arguments = listOf(navArgument("idETS") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val idETS = backStackEntry.arguments?.getString("idETS") ?: ""
                            ListaAlumnosScreen(navController, idETS, alumnosViewModel, loginViewModel)
                        }

                        composable(
                            route = "unicETSDetail/{idETS}",
                            arguments = listOf(navArgument("idETS") { type = NavType.IntType })
                        ) { backStackEntry ->
                            val idETS = backStackEntry.arguments?.getInt("idETS") ?: 0
                            EtsDetailScreen(navController, idETS, loginViewModel = loginViewModel)
                        }

                        composable(route = "Mensajes/{user}",
                            arguments = listOf(navArgument("user") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val user = backStackEntry.arguments?.getString("user") ?: ""
                            MensajesScreen(navController, user, loginViewModel, mensajesViewModel)
                        }

                        composable(route = "Chat/{destinatario}/{nombre}",
                            arguments = listOf(
                                navArgument("destinatario") { type = NavType.StringType },
                                navArgument("nombre") { type = NavType.StringType }
                            )
                        ) { backStackEntry ->
                            val destinatario = backStackEntry.arguments?.getString("destinatario") ?: ""
                            val nombre = backStackEntry.arguments?.getString("nombre") ?: ""

                            ChatScreen(navController, destinatario, nombre, loginViewModel, mensajesViewModel)
                        }

                        composable(
                            route = "etsDetail/{ETS}/{Periodo}/{Turno}/{Fecha}/{PA}",
                            arguments = listOf(
                                navArgument("ETS") { type = NavType.StringType },
                                navArgument("Periodo") { type = NavType.StringType },
                                navArgument("Turno") { type = NavType.StringType },
                                navArgument("PA") { type = NavType.StringType },
                                navArgument("Fecha") { type = NavType.StringType }
                            )
                        ) { backStackEntry ->
                            val ETS = backStackEntry.arguments?.getString("ETS") ?: ""
                            val parsedETS = try {
                                parseInt(ETS)
                            } catch (e: NumberFormatException) {
                                0 // o algún valor predeterminado
                            }
                            val Periodo = backStackEntry.arguments?.getString("Periodo") ?: ""
                            val Turno = backStackEntry.arguments?.getString("Turno") ?: ""
                            val PA = backStackEntry.arguments?.getString("PA") ?: ""
                            val Fecha = backStackEntry.arguments?.getString("Fecha") ?: ""

//                        EtsCardButton(navController, parsedETS, Periodo,Turno, Fecha,PA)
                        }
                    }
                    if (_showDialog.value) {
                        AlertDialog(
                            onDismissRequest = { _showDialog.value = false },
                            title = {
                                Text(
                                    text = _dialogTitle.value,
                                    style = MaterialTheme.typography.titleMedium
                                )
                            },
                            text = {
                                Text(
                                    text = _dialogMessage.value,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            },
                            confirmButton = {
                                TextButton(
                                    onClick = { _showDialog.value = false }
                                ) {
                                    Text("Aceptar")
                                }
                            }
                        )
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        LocalBroadcastManager.getInstance(this)
            .unregisterReceiver(broadcastReceiver)
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            Log.d("MainActivity", "Permiso de notificaciones concedido")
        } else {
            Log.d("MainActivity", "Permiso de notificaciones denegado")
        }
    }

}