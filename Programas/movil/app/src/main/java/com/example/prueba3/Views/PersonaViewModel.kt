package com.example.prueba3.Views

import com.example.prueba3.Clases.DataPersona
import RetroFit.RetrofitInstance
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PersonaViewModel : ViewModel() {

    private val _datosPersona = MutableStateFlow<List<DataPersona>>(emptyList())
    val datosPersona: StateFlow<List<DataPersona>> = _datosPersona

    private val _loadingState = MutableStateFlow(false)
    val loadingState: StateFlow<Boolean> = _loadingState

    fun obtenerDatos(usuario: String) {
        viewModelScope.launch {
            try {
                _loadingState.value = true
                val response = RetrofitInstance.getdatospersona.getdatospersona(usuario)
                _datosPersona.value = response
            } catch (e: Exception) {
                e.printStackTrace() // Manejar error
            } finally {
                _loadingState.value = false
            }
        }
    }
}
