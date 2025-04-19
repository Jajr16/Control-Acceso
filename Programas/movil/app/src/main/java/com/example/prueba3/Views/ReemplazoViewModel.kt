package com.example.prueba3.Views

import RetroFit.RetrofitInstance
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.prueba3.Clases.Reemplazo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException

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

                val response = RetrofitInstance.reemplazoApi.enviarSolicitud(
                    Reemplazo(
                        idETS = idETS,
                        docenteRFC = docenteRFC,
                        motivo = motivo,
                        estatus = "PENDIENTE"
                    )
                )

                _reemplazoState.value = Reemplazo(
                    idETS = response.idETS,
                    docenteRFC = response.docenteRFC,
                    motivo = response.motivo,
                    estatus = response.estatus
                )
            } catch (e: HttpException) {
                when (e.code()) {
                    409 -> {
                        _errorState.value = "Ya existe una solicitud pendiente para este ETS"
                        _reemplazoState.value = Reemplazo(
                            idETS = idETS,
                            docenteRFC = docenteRFC,
                            motivo = motivo,
                            estatus = "PENDIENTE"
                        )
                    }
                    else -> {
                        _errorState.value = "Error al enviar solicitud: ${e.message()}"
                    }
                }
            } catch (e: Exception) {
                _errorState.value = "Error al enviar solicitud: ${e.message}"
            } finally {
                _loadingState.value = false
            }
        }
    }

    fun verificarSolicitudPendiente(etsId: Int, docenteRFC: String) {
        viewModelScope.launch {
            try {
                _loadingState.value = true
                val response = RetrofitInstance.reemplazoApi.verificarSolicitudPendiente(
                    etsId = etsId,
                    docenteRFC = docenteRFC
                )

                if (response.tieneSolicitudPendiente) {
                    _errorState.value = "Ya existe una solicitud pendiente para este ETS"
                    response.solicitudExistente?.let { solicitud ->
                        _reemplazoState.value = Reemplazo(
                            idETS = solicitud.idETS,
                            docenteRFC = solicitud.docenteRFC,
                            motivo = solicitud.motivo,
                            estatus = solicitud.estatus
                        )
                    }
                }
            } catch (e: Exception) {
                // No mostramos error si falla la verificación
            } finally {
                _loadingState.value = false
            }
        }
    }
}