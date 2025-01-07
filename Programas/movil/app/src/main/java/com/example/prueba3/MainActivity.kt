package com.example.prueba3

import Pantallas.Camara
import Pantallas.CreateAccountScreen
import Pantallas.Calendar
import Pantallas.ETSInscriptionProcessScreen
import Pantallas.EtsCardButton
import Pantallas.EtsDetailScreen
import Pantallas.EtsListScreen
import Pantallas.EtsListScreenAlumno
import Pantallas.ListaAlumnosScreen
import Pantallas.LoginScreen
import Pantallas.QRScannerScreen
import Pantallas.WelcomeScreenDocente
import Pantallas.WelcomeScreenAlumno
import PantallasTT.NotificationsScreen
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
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
import com.example.prueba3.ui.theme.BlueBackground
import com.example.prueba3.ui.theme.Prueba3Theme
import java.lang.Integer.parseInt


class MainActivity : ComponentActivity() {

override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    val sharedPreferences = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
    val loginViewModel = LoginViewModel(sharedPreferences)

    enableEdgeToEdge()
    setContent {
        Prueba3Theme {
            androidx.compose.foundation.layout.Box(
                modifier = androidx.compose.ui.Modifier
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
                    composable("Menu") { WelcomeScreenDocente(navController, loginViewModel) }
                    composable("Menu Alumno") { WelcomeScreenAlumno(navController, loginViewModel) }
                    composable("LETS") { EtsListScreen(navController, loginViewModel = loginViewModel) }
                    composable("LETSA") { EtsListScreenAlumno(navController, loginViewModel = loginViewModel) }
                    composable("scanQr") { QRScannerScreen(navController, loginViewModel = loginViewModel) }
                    composable("info") { ETSInscriptionProcessScreen(navController, loginViewModel) }
                    composable("CrearCuenta") { CreateAccountScreen(navController) }

                    composable("Calendar") { Calendar(navController) }

                    composable("ListaAlumnos/{idETS}") { backStackEntry ->
                        val idETS = backStackEntry.arguments?.getString("idETS") ?: ""
                        ListaAlumnosScreen(navController, idETS, alumnosViewModel, loginViewModel) // Pasar el ViewModel
                    }


                    composable(
                        route = "unicETSDetail/{idETS}",
                        arguments = listOf(navArgument("idETS") { type = NavType.IntType })
                    ) { backStackEntry ->
                        val idETS = backStackEntry.arguments?.getInt("idETS") ?: 0
                        EtsDetailScreen(navController, idETS, loginViewModel = loginViewModel)
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

                        EtsCardButton(navController, parsedETS, Periodo,Turno, Fecha,PA)
                        }
                    }
                }

            }
        }
    }
}