package com.example.prueba3

import Pantallas.Camara
import Pantallas.EtsDetailScreen
import Pantallas.EtsPeriodScreen
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
                composable("PETS") { EtsPeriodScreen(navController) }
                composable("scanQr") { QRScannerScreen(navController) }

                composable(
                    route = "etsDetail/{tipo}/{unidad}/{docente}/{coordinador}/{periodo}/{fecha}/{horario}/{salon}/{cupo}",
                    arguments = listOf(
                        navArgument("tipo") { type = NavType.StringType },
                        navArgument("unidad") { type = NavType.StringType },
                        navArgument("docente") { type = NavType.StringType },
                        navArgument("coordinador") { type = NavType.StringType },
                        navArgument("periodo") { type = NavType.StringType },
                        navArgument("fecha") { type = NavType.StringType },
                        navArgument("horario") { type = NavType.StringType },
                        navArgument("salon") { type = NavType.StringType },
                        navArgument("cupo") { type = NavType.StringType }

                    )
                ) { backStackEntry ->
                    val tipo = backStackEntry.arguments?.getString("tipo") ?: ""
                    val unidad = backStackEntry.arguments?.getString("unidad") ?: ""
                    val docente = backStackEntry.arguments?.getString("docente") ?: ""
                    val coordinador = backStackEntry.arguments?.getString("coordinador") ?: ""
                    val periodo = backStackEntry.arguments?.getString("periodo") ?: ""
                    val fecha = backStackEntry.arguments?.getString("fecha") ?: ""
                    val horario = backStackEntry.arguments?.getString("horario") ?: ""
                    val salon = backStackEntry.arguments?.getString("salon") ?: ""
                    val cupo = backStackEntry.arguments?.getString("cupo") ?: ""





                    EtsDetailScreen(navController, tipo, unidad,docente,coordinador,periodo,fecha,horario,salon,cupo)
                }
            }
    }
}
}











