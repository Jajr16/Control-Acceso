package Pantallas

import Pantallas.Plantillas.MenuBottomBar
import Pantallas.Plantillas.MenuTopBar
import Pantallas.components.ValidateSession
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.prueba3.Views.LoginViewModel
import com.example.prueba3.Views.MensajesViewModel
import com.example.prueba3.ui.theme.BlueBackground

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MensajesScreen(
    navController: NavController,
    user: String,
    loginViewModel: LoginViewModel,
    mensajesViewModel: MensajesViewModel
) {
    ValidateSession(navController = navController) {

        val listaPersonasToChat by remember {mensajesViewModel.listaUsuarios}.collectAsState()

        val userRole = loginViewModel.getUserRole()

        LaunchedEffect(true) {
            mensajesViewModel.getUsuarios()
        }

        var active by remember { mutableStateOf(false) }
        var query by remember { mutableStateOf("") }

        if (active) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(BlueBackground)
            ) {
                SearchBar(
                    query = query,
                    onQueryChange = { query = it },
                    onSearch = { active = false },
                    active = active,
                    onActiveChange = { active = it },
                    placeholder = { Text("Buscar...", fontSize = 16.sp) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Buscar") },
                    trailingIcon = {
                        if (query.isNotEmpty()) {
                            IconButton(onClick = { query = "" }) {
                                Icon(Icons.Default.Clear, contentDescription = "Borrar")
                            }
                        }
                    },
                    modifier = Modifier.wrapContentHeight(),
                ) {
                    val filteredPeopleToChat =
                        listaPersonasToChat.filter { it.nombre.contains(query, true) }

                    LazyColumn {
                        items(filteredPeopleToChat) {
                            Text(
                                text = it.nombre,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                                    .clickable {
                                        active = false
                                        query =
                                            it.nombre // (Opcional) Llena el query con el nombre seleccionado
                                    },
                                fontSize = 16.sp
                            )
                        }
                    }
                }
            }
        } else {
            androidx.compose.material3.Scaffold(
                topBar = {
                    MenuTopBar(true, false, loginViewModel, navController,
                        searchBar = {
                            SearchBar(
                                query = query,
                                onQueryChange = { query = it },
                                onSearch = { active = false },
                                active = active,
                                onActiveChange = { active = it },
                                placeholder = { Text("Buscar...", fontSize = 16.sp) },
                                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Buscar") },
                                trailingIcon = {
                                    if (query.isNotEmpty()) {
                                        IconButton(onClick = { query = "" }) {
                                            Icon(Icons.Default.Clear, contentDescription = "Borrar")
                                        }
                                    }
                                },
                                modifier = Modifier.fillMaxWidth()
                                    .height(50.dp),
                            ) {}
                        })
                },
                bottomBar = { MenuBottomBar(navController = navController, userRole) }
            ) { padding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(BlueBackground)
                        .padding(padding)
                ) {
                }
            }
        }
    }
}
