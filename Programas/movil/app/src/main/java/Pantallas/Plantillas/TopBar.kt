package Pantallas.Plantillas

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.prueba3.R
import com.example.prueba3.Views.LoginViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuTopBar(
    showBackButton: Boolean,
    showMsgButton: Boolean,
    loginViewModel: LoginViewModel,
    navController: NavController,
    searchBar: @Composable (() -> Unit)? = null // Pasamos el SearchBar como parámetro
) {
    val username = loginViewModel.getUserName()

    TopAppBar(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 27.dp, start = 10.dp, end = 10.dp)
            .height(if (searchBar != null) 60.dp else 40.dp),
        title = {}, // El título ahora es vacío, ya que no lo necesitamos
        navigationIcon = {
            if (showBackButton) {
                IconButton(onClick = { navController.navigateUp() },
                    modifier = Modifier.size(60.dp)) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Regresar",
                        tint = Color.White
                    )
                }
            }
        },
        actions = {
            if (showMsgButton) {
                IconButton(onClick = { navController.navigate("Mensajes/${username}") }) {
                    Icon(
                        painter = painterResource(id = R.drawable.chat),
                        contentDescription = "Mensajes",
                        modifier = Modifier.size(24.dp),
                        tint = Color.White
                    )
                }
            }
            if (searchBar != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = if (showBackButton) 56.dp else 0.dp)
                ) {
                    searchBar()
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent,
            navigationIconContentColor = Color.White,
            actionIconContentColor = Color.White
        )
    )
}

