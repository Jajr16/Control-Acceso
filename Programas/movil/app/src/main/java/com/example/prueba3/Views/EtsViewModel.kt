package com.example.prueba3.Views

import com.example.prueba3.Clases.ListadoETS
import RetroFit.RetrofitInstance
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class EtsViewModel : ViewModel() {
    private val _etsList = MutableStateFlow<List<ListadoETS>>(emptyList())
    val etsList: StateFlow<List<ListadoETS>> = _etsList

    init {
        fetchEtsList()
    }

    private fun fetchEtsList() {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.ETSapi.getEtsList()
                _etsList.value = response
            } catch (e: Exception) {
                // Maneja los errores aquí
                e.printStackTrace()
            }
        }
    }
}
