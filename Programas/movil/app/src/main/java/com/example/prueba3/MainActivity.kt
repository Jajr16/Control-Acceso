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
import com.google.firebase.FirebaseApp
import com.google.firebase.ktx.Firebase
import Pantallas.EtsDetailScreen
import Pantallas.EtsListScreen
import Pantallas.EtsListScreenAlumno
import Pantallas.InformacionAlumno
import Pantallas.ListaAlumnosScreen
import Pantallas.LoginScreen
import Pantallas.MensajesScreen
import Pantallas.QRScannerScreen
import Pantallas.WelcomeScreenAlumno
import Pantallas.NotificationsScreen
import Pantallas.Reporte
import Pantallas.WelcomeScreen
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
import com.example.prueba3.Views.DiasETSModel
import com.example.prueba3.Views.MensajesViewModel
import com.example.prueba3.ui.theme.BlueBackground
import com.example.prueba3.ui.theme.Prueba3Theme
import dagger.hilt.android.AndroidEntryPoint
import java.lang.Integer.parseInt

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

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


    val sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
    val loginViewModel = LoginViewModel(sharedPreferences)
    val DiasETSModel = DiasETSModel()
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

                NavHost(navController = navController, startDestination = "login") {

                    composable("login") { LoginScreen(navController, loginViewModel) }
                    composable("camara/{boleta}/{idETS}") { backStackEntry ->
                        val boleta = backStackEntry.arguments?.getString("boleta") ?: ""
                        val idETS = backStackEntry.arguments?.getString("idETS") ?: ""
                        Camara(navController, boleta, idETS)
                    }
                    composable("notificaciones") { NotificationsScreen(navController) }
                    composable("Menu Alumno") { WelcomeScreenAlumno(navController, loginViewModel = loginViewModel) }

                    composable("Menu") { WelcomeScreen(navController = navController, loginViewModel = loginViewModel) }
                    composable("Menu Docente") { WelcomeScreenDocente(
                        navController = navController,
                        loginViewModel = loginViewModel

                    ) }

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


                    composable("Calendar") { CalendarScreen(navController, loginViewModel, DiasETSModel) }
                    composable(
                        route = "CredencialDAE?url={url}&boleta={boleta}", // Define la ruta con dos argumentos
                        arguments = listOf(
                            navArgument("url") { // Define el argumento "url"
                                type = NavType.StringType // Especifica que es de tipo String
                            },
                            navArgument("boleta") { // Define el argumento "boleta"
                                type = NavType.StringType // Especifica que es de tipo String
                            }
                        )
                    ) { backStackEntry ->
                        val url = backStackEntry.arguments?.getString("url")
                        val boleta = backStackEntry.arguments?.getString("boleta") ?: ""
                        CredencialDaeScreen(navController, loginViewModel, url, viewModel = alumnosViewModel, boleta)
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
                        Reporte(navController, idETS, boleta, loginViewModel, viewModel = alumnosViewModel,aceptado)
                    }



                    composable(
                        "InfoA/{idETS}/{boleta}",
                        arguments = listOf(
                            navArgument("idETS") { type = NavType.StringType },
                            navArgument("boleta") { type = NavType.StringType }
                        )
                    ) { backStackEntry ->
                        val idETS = backStackEntry.arguments?.getString("idETS") ?: ""
                        val boleta = backStackEntry.arguments?.getString("boleta") ?: ""
                        InformacionAlumno(navController, idETS, boleta, loginViewModel,viewModel = alumnosViewModel)
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
                    ) {
                        backStackEntry ->
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
                }

            }
        }
    }
}