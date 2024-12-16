package Pantallas

import Pantallas.components.MenuTopBar
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun ETSInscriptionProcessScreen(navController: NavController) {
    Scaffold(
        topBar = { MenuTopBar(navController = navController, title = "Instrucciones para el acceso a los ETS") }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp) // Asegura que haya espacio entre los elementos
        ) {
            item {
                Text(
                    text = "Guía de Inscripción al ETS",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Text(
                    text = "Sigue estos pasos para completar el proceso de inscripción al ETS:",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            // Lista de pasos
            val steps = listOf(
                "Pagar en caja y verificar que estén correctos los siguientes datos: Nombre, Boleta, Carrera y Número de unidades de aprendizaje.",
                "Acudir a ventanilla de gestión escolar para generar créditos en el “SAES”.",
                "Una vez generados los créditos, inscribe las unidades de aprendizaje en la página del “SAES”.",
                "Entregar en ventanilla de gestión escolar el comprobante de inscripción de ETS generado por SAES y el recibo de pago para finalizar la inscripción al ETS.",
                "Acudir el día y la hora establecida en el calendario."
            )

            itemsIndexed(steps) { index, step ->
                StepCard(stepNumber = index + 1, stepDescription = step)
            }
        }
    }
}

@Composable
fun StepCard(stepNumber: Int, stepDescription: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.elevatedCardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(MaterialTheme.colorScheme.primary, shape = CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stepNumber.toString(),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = stepDescription,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

