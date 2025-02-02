package Pantallas

import androidx.compose.foundation.layout.*
import Pantallas.components.MenuBottomBar
import Pantallas.components.ValidateSession
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.prueba3.Views.EtsViewModel
import com.example.prueba3.Views.LoginViewModel

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SolicitarReemplazo(

) {
    var razon by remember { mutableStateOf("") }
    var showMessage by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Solicitar reemplazo",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "ETS")
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.Gray, shape = RoundedCornerShape(8.dp))
                .padding(16.dp)
        ) {
            Text(text = "ETS fijo seleccionado", color = Color.White)
        }
        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "RAZON")
        TextField(
            value = razon,
            onValueChange = { razon = it },
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.Gray, shape = RoundedCornerShape(8.dp)),
            placeholder = { Text("Escriba su razón") },
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = Color.Gray,
                textColor = Color.White,
                placeholderColor = Color.White
            )
        )
        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { showMessage = true },
            modifier = Modifier
                .fillMaxWidth()
                .border(2.dp, Color.Black, RoundedCornerShape(8.dp)),
            colors = ButtonDefaults.buttonColors(backgroundColor = Color.White)
        ) {
            Text(text = "Enviar", color = Color.Black)
        }

        if (showMessage) {
            Toast.makeText(LocalContext.current, "Solicitud enviada", Toast.LENGTH_SHORT).show()
        }
    }
}