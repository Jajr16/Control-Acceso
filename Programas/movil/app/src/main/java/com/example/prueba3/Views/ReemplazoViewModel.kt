package com.example.prueba3.Views

import RetroFit.RetrofitInstance
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.prueba3.Clases.Reemplazo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ReemplazoViewModel : ViewModel() {
    private val _reemplazoState = MutableStateFlow<Reemplazo?>(null)
    val reemplazoState: StateFlow<Reemplazo?> = _reemplazoState

    private val _loadingState = MutableStateFlow(false)
    val loadingState: StateFlow<Boolean> = _loadingState

    private val _errorState = MutableStateFlow<String?>(null)
    val errorState: StateFlow<String?> = _errorState

    fun enviarSolicitudReemplazo(idETS: Int, docenteRFC: String, motivo: String) {
        viewModelScope.launch {
            try {
                _loadingState.value = true
                _errorState.value = null

                val solicitud = Reemplazo(
                    idETS = idETS,
                    docenteRFC = docenteRFC,
                    motivo = motivo,
                    estatus = "PENDIENTE" // El backend lo manejará como 0
                )

                val response = RetrofitInstance.reemplazoApi.enviarSolicitud(solicitud)
                _reemplazoState.value = response
            } catch (e: Exception) {
                _errorState.value = "Error al enviar solicitud: ${e.message}"
            } finally {
                _loadingState.value = false
            }
        }
    }
}