package com.example.prueba3.Views

import RetroFit.RetrofitInstance
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.prueba3.Clases.Docente
import com.example.prueba3.Clases.Reemplazo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class ReemplazoViewModel : ViewModel() {
    // ============== Solicitar Reemplazo ==================
    private val _reemplazoState = MutableStateFlow<Reemplazo?>(null)
    val reemplazoState: StateFlow<Reemplazo?> = _reemplazoState

    private val _loadingState = MutableStateFlow(false)
    val loadingState: StateFlow<Boolean> = _loadingState

    private val _errorState = MutableStateFlow<String?>(null)
    val errorState: StateFlow<String?> = _errorState

    // =============== Asignar Reemplazo =================
    private val _solicitudesPendientes = MutableStateFlow<List<Reemplazo>>(emptyList())
    val solicitudesPendientes: StateFlow<List<Reemplazo>> = _solicitudesPendientes

    private val _solicitudDetalle = MutableStateFlow<Reemplazo?>(null)
    val solicitudDetalle: StateFlow<Reemplazo?> = _solicitudDetalle

    private val _docentesDisponibles = MutableStateFlow<List<Docente>>(emptyList())
    val docentesDisponibles: StateFlow<List<Docente>> = _docentesDisponibles

    private val _docenteSeleccionado = MutableStateFlow<Docente?>(null)
    val docenteSeleccionado: StateFlow<Docente?> = _docenteSeleccionado


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

    // ========== ASIGNAR REEMPLAZO ====================
    fun cargarSolicitudesPendientes() {
        viewModelScope.launch {
            try {
                _loadingState.value = true
                _errorState.value = null

                val response = try {
                    RetrofitInstance.reemplazoApi.obtenerSolicitudesPendientes()
                } catch (e: IOException) {
                    throw Exception("Error de conexión: ${e.message}")
                } catch (e: Exception) {
                    throw Exception("Error inesperado: ${e.message}")
                }

                if (response.isNullOrEmpty()) {
                    _solicitudesPendientes.value = emptyList()
                } else {
                    // Validación adicional de datos
                    val solicitudesValidas = response.filter {
                        it.idETS != null &&
                                !it.docenteRFC.isNullOrEmpty() &&
                                !it.motivo.isNullOrEmpty() &&
                                !it.estatus.isNullOrEmpty()
                    }

                    if (solicitudesValidas.isEmpty() && response.isNotEmpty()) {
                        throw Exception("Los datos recibidos no tienen el formato esperado")
                    }

                    _solicitudesPendientes.value = solicitudesValidas
                }
            } catch (e: Exception) {
                _errorState.value = "Error al cargar solicitudes: ${e.message}"
                // Log para depuración
                Log.e("ReemplazoViewModel", "Error: ${e.message}", e)
            } finally {
                _loadingState.value = false
            }
        }
    }

    fun aprobarReemplazo(idETS: Int, docenteRFC: String, docenteReemplazo: String) {
        viewModelScope.launch {
            try {
                _loadingState.value = true
                val response = RetrofitInstance.reemplazoApi.aprobarReemplazo(
                    idETS = idETS,
                    docenteRFC = docenteRFC,
                    docenteReemplazo = docenteReemplazo
                )
                _solicitudesPendientes.value = _solicitudesPendientes.value.map {
                    if (it.idETS == idETS && it.docenteRFC == docenteRFC) {
                        it.copy(estatus = "APROBADO")
                    } else {
                        it
                    }
                }
            } catch (e: Exception) {
                _errorState.value = "Error al aprobar: ${e.message}"
            } finally {
                _loadingState.value = false
            }
        }
    }

    fun rechazarReemplazo(idETS: Int, docenteRFC: String, motivo: String) {
        viewModelScope.launch {
            try {
                _loadingState.value = true
                val response = RetrofitInstance.reemplazoApi.rechazarReemplazo(
                    idETS = idETS,
                    docenteRFC = docenteRFC,
                    motivo = motivo
                )

                // Actualiza la solicitud en la lista en lugar de eliminarla
                _solicitudesPendientes.value = _solicitudesPendientes.value.map { solicitud ->
                    if (solicitud.idETS == idETS && solicitud.docenteRFC == docenteRFC) {
                        solicitud.copy(
                            estatus = "RECHAZADO",
                            motivo = solicitud.motivo + " | Motivo rechazo: $motivo"
                        )
                    } else {
                        solicitud
                    }
                }

                // Opcional: Recargar desde el servidor
                cargarSolicitudesPendientes()

            } catch (e: Exception) {
                _errorState.value = "Error al rechazar reemplazo: ${e.message}"
            } finally {
                _loadingState.value = false
            }
        }
    }

    fun cargarDocentesDisponibles() {
        viewModelScope.launch {
            try {
                _loadingState.value = true
                val response = RetrofitInstance.reemplazoApi.obtenerDocentesDisponibles()
                Log.d("ReemplazoViewModel", "API Response: $response")
                _docentesDisponibles.value = response.mapNotNull {
                    Log.d("ReemplazoViewModel", "Processing docente: $it")
                    if (it.rfcDocente != null && it.nombreDocente != null) {
                        Docente(it.rfcDocente, it.nombreDocente)
                    } else {
                        null
                    }
                }
                Log.d("ReemplazoViewModel", "Mapped docentes: ${_docentesDisponibles.value}")
            } catch (e: Exception) {
                _errorState.value = "Error al cargar docentes: ${e.message}"
            } finally {
                _loadingState.value = false
            }
        }
    }

    fun seleccionarDocente(docente: Docente) {
        _docenteSeleccionado.value = docente
    }

}

