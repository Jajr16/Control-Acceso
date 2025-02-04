package com.example.prueba3

import Pantallas.Camara
import Pantallas.CalendarScreen
import Pantallas.CreateAccountScreen
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
import Pantallas.NotificationsScreen
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.prueba3.Views.LoginViewModel
import com.example.prueba3.Views.AlumnosViewModel
import com.example.prueba3.Views.DiasETSModel
import com.example.prueba3.ui.theme.BlueBackground
import com.example.prueba3.ui.theme.Prueba3Theme
import java.lang.Integer.parseInt

class MainActivity : ComponentActivity() {

override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    window.decorView.systemUiVisibility = (
            android.view.View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    or android.view.View.SYSTEM_UI_FLAG_FULLSCREEN
                    or android.view.View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    or android.view.View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    or android.view.View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            )


    val sharedPreferences = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
    val loginViewModel = LoginViewModel(sharedPreferences)
    val DiasETSModel = DiasETSModel()

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

                    composable("Calendar") { CalendarScreen(navController, loginViewModel, DiasETSModel) }
//                    composable("Calendar") {
//                        val intent = Intent(
//                            Intent.ACTION_VIEW,
//                            Uri.parse("https://www.ipn.mx/assets/files/website/docs/inicio/calendarioipn-escolarizada.pdf"))
//                        startActivity(intent)
//
//                        LaunchedEffect(Unit) {
//                            loginViewModel.getUserRole()?.let { savedRole ->
//                                if (savedRole != null || savedRole != "") {
//                                    when (savedRole) {
//                                        "Alumno" -> navController.navigate("Menu Alumno") {
//                                            popUpTo("login") { inclusive = true } }
//                                        "Personal Seguridad", "Docente" -> navController.navigate("Menu") {
//                                            popUpTo("login") { inclusive = true } }
//                                    }
//                                }
//                            }
//                        }
//                    }

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