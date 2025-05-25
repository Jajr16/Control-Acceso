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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.prueba3.Views.EtsInfoViewModel
import com.example.prueba3.Views.PersonaViewModel

@Composable

fun EtsACardButton(
    navController: NavController,
    idETS: Int,
    idPeriodo: String,
    Turno: String,
    Fecha: String,
    UnidadAprendizaje: String,
    carrera: String,
    viewModel: EtsInfoViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {

    LaunchedEffect(idETS) {
        viewModel.fetchEtsDetail(idETS)
    }

    val etsDetail by remember { viewModel.etsDetailState }.collectAsState()
    val horaETS = etsDetail?.ets?.hora ?: ""
    val fechaETS = etsDetail?.ets?.fecha ?: ""

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
            Text(text = "$carrera, $idPeriodo", style = MaterialTheme.typography.bodyLarge)
            Text(text = "$UnidadAprendizaje ", style = MaterialTheme.typography.bodyLarge)
            Text(text = "$Turno $Fecha ", style = MaterialTheme.typography.bodyLarge)

        }

    }

}