package PantallasTT

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun NotificationsScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Flecha de retroceso
        Icon(
            imageVector = Icons.Filled.ArrowBack,
            contentDescription = "Volver",
            modifier = Modifier
                .size(32.dp)
                .clickable { navController.popBackStack() } // Navegar hacia atrás
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Título
        Text(
            text = "Notificaciones",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Barra de búsqueda
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .background(Color.Gray.copy(alpha = 0.1f), MaterialTheme.shapes.medium),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Buscar...",
                modifier = Modifier
                    .padding(start = 16.dp)
                    .weight(1f),
                color = Color.Gray
            )
            Icon(
                imageVector = Icons.Filled.Search,
                contentDescription = "Buscar",
                modifier = Modifier.padding(end = 16.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Tarjetas de notificación
        NotificationCard()
        NotificationCard()

        Spacer(modifier = Modifier.weight(1f)) // Espaciador flexible

        // Barra de menú inferior
        BottomNavigationBar(navController)
    }
}

@Composable
fun NotificationCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(text = "Descripción:", style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "Fecha: 2024-12-01", style = MaterialTheme.typography.bodySmall)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "Estado: Pendiente", style = MaterialTheme.typography.bodySmall)
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Revisar:", style = MaterialTheme.typography.bodySmall)
                Icon(
                    imageVector = Icons.Filled.CheckCircle,
                    contentDescription = "Aprobado",
                    modifier = Modifier.size(24.dp),
                    tint = Color.Green
                )
            }
        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavController) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .background(Color.LightGray),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Filled.Home,
            contentDescription = "Inicio",
            modifier = Modifier
                .size(32.dp)
                .clickable { navController.navigate("login") }
        )
        Icon(
            imageVector = Icons.Filled.DateRange,
            contentDescription = "Calendario",
            modifier = Modifier
                .size(32.dp)
                .clickable { navController.navigate("table") }
        )
        Icon(
            imageVector = Icons.Filled.Notifications,
            contentDescription = "Notificaciones",
            modifier = Modifier
                .size(32.dp)
                .clickable { navController.navigate("calculator") }
        )
    }
}