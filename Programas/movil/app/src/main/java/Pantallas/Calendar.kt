package Pantallas

import android.graphics.Bitmap
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
@Composable
fun CalendarScreen(navController: NavController) {
    val context = LocalContext.current
    val pdfUrl = "https://www.ipn.mx/assets/files/website/docs/inicio/calendarioipn-escolarizada.pdf"

    // Variable para almacenar el Bitmap del PDF
    var bitmap by remember { mutableStateOf<Bitmap?>(null) }

    LaunchedEffect(pdfUrl) {
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {


    }
}