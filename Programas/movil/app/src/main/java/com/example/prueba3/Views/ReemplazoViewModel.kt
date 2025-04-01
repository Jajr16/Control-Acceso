package com.example.prueba3.Views

import RetroFit.RetrofitInstance
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.prueba3.Clases.Reemplazo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ReemplazoViewModel : ViewModel(){

    private val _reemplazoDocente = MutableStateFlow<List<Reemplazo>>(emptyList())
    val reemplazoDocente: StateFlow<List<Reemplazo>> = _reemplazoDocente

    private val _loadingState = MutableStateFlow(false)
    val loadingState: StateFlow<Boolean> = _loadingState

    fun fetchreemplazoDocente(reemplazo: Reemplazo) {
        viewModelScope.launch {
            try {
                _loadingState.value = true
                val responseDocente = RetrofitInstance.getReemplazo.enviarSolicitud(reemplazo)
                _reemplazoDocente.value = responseDocente
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _loadingState.value = false
            }
        }
    }

    fun fetchReemplazoDocente(nuevoReemplazo: Reemplazo) {

    }
}