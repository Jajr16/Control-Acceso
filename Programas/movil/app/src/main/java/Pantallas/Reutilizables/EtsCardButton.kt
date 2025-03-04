package Pantallas.Reutilizables

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun EtsACardButton(
    navController: NavController,
    idETS: Int,
    idPeriodo: String,
    Turno: String,
    Fecha: String,
    UnidadAprendizaje: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                navController.navigate("unicETSDetail/$idETS")
            },
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, Color.Black),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFEAEAEA) // Color similar al fondo de la tarjeta en la imagen
        )
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
        ) {
            Text(text = "Unidad de Aprendizaje: $UnidadAprendizaje", style = MaterialTheme.typography.bodyLarge)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Periodo: $idPeriodo", style = MaterialTheme.typography.bodyLarge)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Fecha: $Fecha", style = MaterialTheme.typography.bodyLarge)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Turno: $Turno", style = MaterialTheme.typography.bodyLarge)
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}