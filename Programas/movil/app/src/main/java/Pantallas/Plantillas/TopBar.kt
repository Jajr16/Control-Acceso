package Pantallas.Plantillas

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
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
    Component: @Composable (() -> Unit)? = null
) {
    val username = loginViewModel.getUserName()

    TopAppBar(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp)
            .height(48.dp), // Establecemos una altura más pequeña
        title = {
            if (Component != null) {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Component()
                }
            }
        },
        navigationIcon = {
            if (showBackButton) {
                IconButton(
                    onClick = { navController.navigateUp() },
                    modifier = Modifier.size(36.dp)
                        .padding(start = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Regresar",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        },
        actions = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                if (showBackButton) {
                    Spacer(modifier = Modifier.width(0.dp))
                } else {
                    Spacer(modifier = Modifier.weight(1f))
                }


                if (Component != null) {
                    Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                        Component()
                    }
                } else {
                    Spacer(modifier = Modifier.weight(1f))
                }


                if (showMsgButton && (loginViewModel.getUserRole() == "Alumno"
                            || loginViewModel.getUserRole() == "Docente")) {
                    IconButton(onClick = { navController.navigate("Mensajes/${username}") }) {
                        Icon(
                            painter = painterResource(id = R.drawable.chat),
                            contentDescription = "Mensajes",
                            modifier = Modifier.size(24.dp),
                            tint = Color.White
                        )
                    }
                } else {
                    Spacer(modifier = Modifier.width(0.dp))
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

