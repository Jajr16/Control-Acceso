package com.example.prueba3.Views

import RetroFit.RetrofitInstance
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.prueba3.Clases.AlumnoEspecifico
import com.example.prueba3.Clases.AlumnosInfo
import com.example.prueba3.Clases.CredencialAlumnos
import com.example.prueba3.Clases.DetalleAlumnos
import com.example.prueba3.Clases.ListaInfor
import com.example.prueba3.Clases.ReporteData
import com.example.prueba3.Clases.UpdateAceptadoRequest
import com.example.prueba3.Clases.regitrarAsistencia
import com.google.gson.Gson
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import okhttp3.ResponseBody
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AlumnosViewModel : ViewModel() {
    private val _alumnosList = MutableStateFlow<List<AlumnosInfo>>(emptyList())
    val alumnosList: StateFlow<List<AlumnosInfo>> = _alumnosList

    private val _alumnosListado = MutableStateFlow<List<ListaInfor>>(emptyList())
    val alumnosListado: StateFlow<List<ListaInfor>> = _alumnosListado

    private val _alumnoEspecifico = MutableStateFlow<AlumnoEspecifico?>(null)
    val alumnoEspecifico: StateFlow<AlumnoEspecifico?> = _alumnoEspecifico

    private val _alumnosDetalle = MutableStateFlow<List<DetalleAlumnos>>(emptyList())
    val alumnosDetalle: StateFlow<List<DetalleAlumnos>> = _alumnosDetalle

    private val _alumnosCredencial = MutableStateFlow<List<CredencialAlumnos>>(emptyList())
    val alumnosCredencial: StateFlow<List<CredencialAlumnos>> = _alumnosCredencial

    private val _loadingState = MutableStateFlow(true)
    val loadingState: StateFlow<Boolean> = _loadingState

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _registroSuccess = MutableStateFlow(false)
    val registroSuccess: StateFlow<Boolean> = _registroSuccess.asStateFlow()

    private val _alumnoRegistro = MutableStateFlow<List<regitrarAsistencia>>(emptyList())
    val alumnoRegistro: StateFlow<List<regitrarAsistencia>> = _alumnoRegistro.asStateFlow()



    private val _fotoAlumno = MutableStateFlow<ByteArray?>(null)
    val fotoAlumno: StateFlow<ByteArray?> = _fotoAlumno


    // ======== Obtener la foto del alumno ============
    fun fetchFotoAlumno(boleta: String) {
        viewModelScope.launch {
            try {
                _loadingState.value = true
                val responseBody = RetrofitInstance.alumnoEspecifico.getFotoAlumno(boleta)

                // Convertimos el contenido de ResponseBody a ByteArray
                val fotoBytes = responseBody.bytes()
                _fotoAlumno.value = fotoBytes
            } catch (e: Exception) {
                println("Error al obtener la foto: ${e.localizedMessage}")
                _fotoAlumno.value = null
            } finally {
                _loadingState.value = false
            }
        }
    }


    // Función para obtener los datos de alumnos
    fun fetchAlumno(ETSid: String) {
        viewModelScope.launch {
            try {
                _loadingState.value = true
                val alumnos = RetrofitInstance.alumnosApi.getAlumnoList(ETSid)
                _alumnosList.value = alumnos // Actualizamos la lista de alumnos
            } catch (e: Exception) {
                _alumnosList.value = emptyList() // Manejo de error
            } finally {
                _loadingState.value = false
            }
        }
    }

    fun fetchListalumnos() {
        viewModelScope.launch {
            try {
                _loadingState.value = true
                val datos = RetrofitInstance.listalumnos.getAlumnoLista()
                System.out.println("Aqui es datos" + datos);
                _alumnosListado.value = datos
            } catch (e: Exception) {
                _alumnosListado.value = emptyList()
            } finally {
                _loadingState.value = false
            }
        }
    }

    // ======= Mostrar Informacion del alumno ===========
    fun fetchDetalleAlumnos(boleta: String) {
        viewModelScope.launch {
            try {
                _loadingState.value = true
                val alumnoState = RetrofitInstance.alumnosDetalle.getalumnosDetalle(boleta)
                _alumnosDetalle.value = alumnoState
            } catch (e: Exception) {
                _alumnosDetalle.value = emptyList()
            } finally {
                _loadingState.value = false
            }
        }
    }

    // ======== Mostrar la credencial del alumno ============
    fun fetchCredencialAlumnos(boleta: String) {
        viewModelScope.launch {
            try {
                _loadingState.value = true
                val estado = RetrofitInstance.alumnosCredencial.getalumnosCredencial(boleta)
                _alumnosCredencial.value = estado
            } catch (e: Exception) {
                _alumnosCredencial.value = emptyList()
            } finally {
                _loadingState.value = false
            }
        }
    }

    //========== Registrar el ingreso a la instalacion del alumno
    fun registrarAsistencia(boleta: String) {
        viewModelScope.launch {
            try {
                _loadingState.value = true
                _errorMessage.value = null

                val fechaActual = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                val horaActual = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())

                val response = RetrofitInstance.alumnosDetalle.registrarAsistencia(
                    boleta = boleta,
                    fecha = fechaActual,
                    hora = horaActual
                )

                if (response.isNotEmpty()) {
                    _alumnoRegistro.value = response
                    _registroSuccess.value = true
                } else {
                    _errorMessage.value = "No se pudo registrar la asistencia"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error: ${e.message}"
            } finally {
                _loadingState.value = false
            }
        }
    }

    fun clearRegistrationState() {
        _registroSuccess.value = false
    }



    fun fetchAlumnoEspecifico(boleta: String) {
        viewModelScope.launch {
            try {
                _loadingState.value = true
                val response = RetrofitInstance.alumnoEspecifico.getAlumnoEspecifico(boleta)
                _alumnoEspecifico.value = response
            } catch (e: Exception) {
                _alumnoEspecifico.value = null
                println("Error al obtener datos específicos: ${e.localizedMessage}")
            } finally {
                _loadingState.value = false
            }
        }
    }


    // Función para actualizar la asistencia de un alumno
    suspend fun updateAsistencia(boleta: String, idETS: Int, aceptado: Int) {
        try {
            val response = RetrofitInstance.aceptadoApi.updateAceptado(
                UpdateAceptadoRequest(
                    boleta,
                    idETS,
                    aceptado
                )
            )

            // Actualizamos solo el alumno modificado en la lista local
            _alumnosList.value = _alumnosList.value.map { alumno ->
                if (alumno.boleta == boleta) {
                    alumno.copy(aceptado = aceptado) // Modificamos solo el alumno con el 'Boleta' correspondiente
                } else {
                    alumno // Dejamos los demás alumnos sin cambios
                }
            }
        } catch (e: Exception) {
            println("Error al actualizar: ${e.localizedMessage}")
        }
    }


        private val _reporte = MutableStateFlow<List<ReporteData>>(emptyList())
        val reporte: StateFlow<List<ReporteData>> = _reporte

        private val _imagenBytes = MutableStateFlow<ByteArray?>(null)
        val imagenBytes: StateFlow<ByteArray?> = _imagenBytes

        fun fetchReporte(idets: Int, boleta: String) {
            viewModelScope.launch {
                try {
                    _loadingState.value = true
                    val responseBody = RetrofitInstance.apiReporteInfo.obtenerReporte(idets, boleta)
                    val response = responseBody.string()
                    val jsonObject = JSONObject(response)

                    val reporteJson = jsonObject.getJSONObject("reporte")

                    val reporte = Gson().fromJson(reporteJson.toString(), ReporteData::class.java)
                    _reporte.value = listOf(reporte)

                    // Obtener la imagen por separado
                    fetchImagenReporte(idets, boleta)

                } catch (e: IOException) {
                    Log.e("ReporteViewModel", "Error de red: ${e.message}")
                    _errorMessage.value = "Error de red: ${e.message}"
                    _reporte.value = emptyList()
                } catch (e: Exception) {
                    Log.e("ReporteViewModel", "Error al obtener el reporte: ${e.message}")
                    _errorMessage.value = "Error al obtener el reporte: ${e.message}"
                    _reporte.value = emptyList()
                } finally {
                    _loadingState.value = false
                }
            }
        }

        fun fetchImagenReporte(idets: Int, boleta: String) {
            viewModelScope.launch {
                try {
                    val imageResponseBody = RetrofitInstance.apiReporteInfo.obtenerImagenReporte(idets, boleta)
                    _imagenBytes.value = imageResponseBody.bytes()
                } catch (e: Exception) {
                    Log.e("ReporteViewModel", "Error al obtener la imagen: ${e.message}")
                    _imagenBytes.value = null
                }
            }
        }

    private val _ingresoResultado = MutableStateFlow<String?>(null)
    val ingresoResultado: StateFlow<String?> = _ingresoResultado



    fun verificarIngresoSalon(idets: Int, boleta: String) {
        viewModelScope.launch {
            try {
                _loadingState.value = true
                val response = RetrofitInstance.ingresoSalonApi.verificarIngreso(idets, boleta)
                if (response.isSuccessful) {
                    val jsonResponse = response.body()
                    _ingresoResultado.value = jsonResponse?.get("resultado")?.asString
                } else {
                    _ingresoResultado.value = "Error en la respuesta del servidor: ${response.code()}"
                }
            } catch (e: Exception) {
                _ingresoResultado.value = "Error de red: ${e.localizedMessage}"
            } finally {
                _loadingState.value = false
            }
        }
    }

    private val _eliminacionExitosa = MutableStateFlow<Boolean?>(null)
    val eliminacionExitosa: StateFlow<Boolean?> = _eliminacionExitosa

    private val _mensajeEliminacion = MutableStateFlow<String?>(null)
    val mensajeEliminacion: StateFlow<String?> = _mensajeEliminacion

    private val _eliminacionCompletada = MutableStateFlow<Boolean?>(null)
    val eliminacionCompletada: StateFlow<Boolean?> = _eliminacionCompletada

    fun eliminarReporte(boleta: String, idETS: Int) {
        viewModelScope.launch {
            try {
                _loadingState.value = true
                _eliminacionExitosa.value = null
                _mensajeEliminacion.value = null
                _eliminacionCompletada.value = null // Resetear el estado de completado

                val response = RetrofitInstance.ingresoSalonApi.eliminarReporte(idETS, boleta)

                if (response.isSuccessful) {
                    val jsonResponse = response.body()
                    _eliminacionExitosa.value = true
                    _mensajeEliminacion.value = jsonResponse?.get("mensaje")?.asString ?: "Eliminación exitosa."
                    // No verificar el ingreso inmediatamente aquí
                } else {
                    _eliminacionExitosa.value = false
                    val errorBody = response.errorBody()?.string()
                    val errorMessage = try {
                        JSONObject(errorBody).getString("mensaje")
                    } catch (e: Exception) {
                        "Error al eliminar el reporte: Código ${response.code()}"
                    }
                    _mensajeEliminacion.value = errorMessage
                }
            } catch (e: Exception) {
                _eliminacionExitosa.value = false
                _mensajeEliminacion.value = "Error de red al eliminar el reporte: ${e.localizedMessage}"
            } finally {
                _loadingState.value = false
            }
        }
    }

    fun clearEliminacionEstado() {
        _eliminacionExitosa.value = null
        _mensajeEliminacion.value = null
        _eliminacionCompletada.value = true // Indica que el proceso de eliminación (y el diálogo) ha finalizado
    }

    fun clearEliminacionCompletada() {
        _eliminacionCompletada.value = null
    }

}
