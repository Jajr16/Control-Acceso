package Pantallas.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment

@Composable
fun <T> BuscadorConLista(
    lista: List<T>,
    filtro: (T, String) -> Boolean,
    onItemClick: (T) -> Unit,
    itemContent: @Composable (T) -> Unit,
    placeholder: String,
    additionalContent: @Composable (() -> Unit)? = null
) {
    var searchQuery by remember { mutableStateOf("") }
    val filteredList = lista.filter { filtro(it, searchQuery) }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Buscador(
            searchQuery = searchQuery,
            onSearchQueryChanged = { searchQuery = it },
            placeholder = placeholder
        )

        additionalContent?.let {
            it() // Ejecutar el contenido adicional
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (filteredList.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f), // Agregar weight para evitar el problema de altura infinita
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No hay resultados.",
                    color = Color.White,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        } else {
            Box(modifier = Modifier.weight(1f)) { // Envolver LazyColumn en un Box con weight
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(8.dp)
                ) {
                    items(filteredList) { item ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clickable { onItemClick(item) },
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(4.dp)
                        ) {
                            itemContent(item)
                        }
                    }
                }
            }
        }
    }
}

