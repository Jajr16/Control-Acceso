package com.example.prueba3

import Pantallas.Camara
import Pantallas.CreateAccountScreen
import Pantallas.ETSInscriptionProcessScreen
import Pantallas.EtsCardButton
import Pantallas.EtsDetailScreen
import Pantallas.EtsListScreen
import Pantallas.LoginScreen
import Pantallas.QRScannerScreen
import Pantallas.WelcomeScreen
import PantallasTT.NotificationsScreen
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import java.lang.Integer.parseInt


class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            NavHost(navController = navController, startDestination = "login"){
                composable("login") { LoginScreen(navController) }
                composable("camara") { Camara(navController)}
                composable("notificaciones") { NotificationsScreen(navController)}
                composable("Menu") { WelcomeScreen(navController) }
                composable("LETS") { EtsListScreen(navController) }
                composable("scanQr") { QRScannerScreen(navController) }
                composable("info") { ETSInscriptionProcessScreen(navController) }
                composable("CrearCuenta") { CreateAccountScreen(navController) }

                composable(
                    route = "unicETSDetail/{idETS}",
                    arguments = listOf(navArgument("idETS" ) { type = NavType.IntType})
                    ) { backStackEntry ->
                    val idETS = backStackEntry.arguments?.getInt("idETS") ?: 0
                    EtsDetailScreen(navController, idETS)
                }

                composable(
                    route = "etsDetail/{ETS}/{Periodo}/{Turno}/{PA}/{Fecha}",
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

                    EtsCardButton(navController, parsedETS, Periodo,Turno,PA,Fecha)
                    }
                }
        }
    }
}