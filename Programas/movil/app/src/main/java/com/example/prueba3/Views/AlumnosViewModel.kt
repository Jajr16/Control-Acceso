package com.example.prueba3.Views

import RetroFit.RetrofitInstance
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.prueba3.Clases.AlumnoEspecifico
import com.example.prueba3.Clases.AlumnosInfo
import com.example.prueba3.Clases.ComparacionResponse
import com.example.prueba3.Clases.CredencialAlumnos
import com.example.prueba3.Clases.DatosWeb
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
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateMapOf
import com.google.gson.JsonSyntaxException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.ByteArrayOutputStream
import java.net.ConnectException
import java.net.HttpURLConnection
import java.net.URL
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


    private val _fotoAlumno = MutableStateFlow<ByteArray?>(null)
    val fotoAlumno: StateFlow<ByteArray?> = _fotoAlumno

    // ============ Comparacion de datos de la credencial + DAE =================
    private val _comparacionResultado = MutableStateFlow<ComparacionResponse?>(null)
    val comparacionResultado: StateFlow<ComparacionResponse?> = _comparacionResultado

    private val _mostrarDialogoComparacion = MutableStateFlow(false)
    val mostrarDialogoComparacion: StateFlow<Boolean> = _mostrarDialogoComparacion

    private val _loadingFotoAlumno = MutableStateFlow(false) // Private mutable state
    val loadingFotoAlumno: StateFlow<Boolean> = _loadingFotoAlumno // Public read-only state


    fun fetchFotoAlumno(boleta: String, onComplete: () -> Unit) {
        viewModelScope.launch {
            _loadingState.value = true
            try {
                // 1. Obtener la URL de la imagen desde la API
                val imageUrl = try {
                    val response = RetrofitInstance.alumnoEspecifico.getFotoAlumno(boleta)
                    response?.fotoUrl?.trim()?.replace("\"", "")
                } catch (e: HttpException) {
                    println("Error HTTP al obtener la URL: ${e.code()} - ${e.message()}")
                    null
                } catch (e: IOException) {
                    println("Error de red al obtener la URL: ${e.message}")
                    null
                }

                if (imageUrl.isNullOrBlank()) {
                    throw IllegalArgumentException("URL de imagen inválida obtenida del servidor")
                }

                println("URL de la imagen obtenida: $imageUrl")

                // 2. Descargar los bytes desde la URL obtenida
                _fotoAlumno.value = withContext(Dispatchers.IO) {
                    var connection: HttpURLConnection? = null
                    return@withContext try {
                        val url = URL(imageUrl)
                        connection = url.openConnection() as HttpURLConnection
                        connection!!.connectTimeout = 15_000
                        connection!!.readTimeout = 15_000
                        connection!!.doInput = true

                        connection!!.inputStream.use { inputStream ->
                            val buffer = ByteArrayOutputStream()
                            inputStream.copyTo(buffer)
                            buffer.toByteArray().takeIf { it.isNotEmpty() }
                                ?: throw IOException("La imagen está vacía")
                        }
                    } catch (e: Exception) {
                        Log.e("fetchFotoAlumno", "Error al descargar la imagen: ${e.message}", e)
                        null
                    } finally {
                        connection?.disconnect()
                    }
                }

            } catch (e: Exception) {
                println("Error al obtener o descargar la foto: ${e.localizedMessage}")
                _fotoAlumno.value = null
            } finally {
                _loadingState.value = false
                onComplete()
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

    // Agregar esta propiedad
    private val _asistenciasHoy = mutableStateMapOf<String, Boolean>()
    val asistenciasHoy: Map<String, Boolean> get() = _asistenciasHoy

    // Modificar fetchListalumnos
    fun fetchListalumnos() {
        viewModelScope.launch {
            try {
                _loadingState.value = true
                val datos = RetrofitInstance.listalumnos.getAlumnoLista()
                System.out.println("Aqui es datos" + datos)

                // Verificar asistencias para todos los alumnos de una vez
                val boletas = datos.map { it.boleta }
                val fechaActual = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

                val response = RetrofitInstance.alumnosDetalle.verificarAsistencias(
                    boletas = boletas,
                    fecha = fechaActual
                )

                if (response.isSuccessful) {
                    response.body()?.let { asistencias ->
                        _asistenciasHoy.clear()
                        _asistenciasHoy.putAll(asistencias)
                    }
                }

                _alumnosListado.value = datos.map { alumno ->
                    alumno.copy(asistenciaRegistrada = _asistenciasHoy[alumno.boleta] ?: false)
                }
            } catch (e: Exception) {
                _alumnosListado.value = emptyList()
                _errorMessage.value = "Error al obtener la lista: ${e.message}"
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

                // Log para depuración
                Log.d("AlumnosViewModel", "Detalles del alumno recibidos: $alumnoState")

            } catch (e: Exception) {
                _alumnosDetalle.value = emptyList()
                Log.e("AlumnosViewModel", "Error al obtener detalles", e)
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
    private val _registroSuccess = MutableStateFlow(false)
    val registroSuccess: StateFlow<Boolean> = _registroSuccess.asStateFlow()

    private val _asistenciaYaRegistrada = MutableStateFlow(false)
    val asistenciaYaRegistrada: StateFlow<Boolean> = _asistenciaYaRegistrada.asStateFlow()

    private val _alumnoRegistro = MutableStateFlow<List<regitrarAsistencia>>(emptyList())
    val alumnoRegistro: StateFlow<List<regitrarAsistencia>> = _alumnoRegistro.asStateFlow()

    fun gestionarAsistencia(boleta: String, idETS: Int, registrar: Boolean = false) {

        if (boleta.isBlank()) {
            _errorMessage.value = "El número de boleta no puede estar vacío"
            return
        }

        viewModelScope.launch {
            try {
                _loadingState.value = true
                _errorMessage.value = null
                resetAsistenciaFlags()

                val fechaActual = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                val horaActual = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())

                val response = RetrofitInstance.alumnosDetalle.registrarAsistencia(
                    boleta = boleta,
                    fecha = fechaActual,
                    hora = horaActual,
                    idETS = idETS
                )

                if (response.isSuccessful) {
                    response.body()?.let { registros ->
                        _registroSuccess.value = true
                        _asistenciaYaRegistrada.value = true
                    }
                } else {
                    handleAsistenciaError(
                        HttpException(response),
                        registrar
                    )
                }
            } catch (e: Exception) {
                handleAsistenciaError(e, registrar)
            } finally {
                _loadingState.value = false
            }
        }
    }

    private fun handleAsistenciaError(e: Exception, registrar: Boolean) {
        when {
            e is HttpException && e.response()?.errorBody() != null -> {
                try {
                    val errorResponse = e.response()?.errorBody()?.string()
                    if (errorResponse?.contains("asistencia ya fue registrada", ignoreCase = true) == true) {
                        _asistenciaYaRegistrada.value = true
                        _errorMessage.value = "La asistencia ya fue registrada hoy para este ETS"
                    } else {
                        _errorMessage.value = "Error del servidor: ${e.code()}\n$errorResponse"
                    }
                } catch (ex: Exception) {
                    _errorMessage.value = "Error al procesar respuesta del servidor"
                }
            }

            e is IOException -> {
                _errorMessage.value = "Error de conexión. Verifica tu internet."
            }

            e is HttpException -> {
                _errorMessage.value = "Error del servidor: ${e.code()}"
            }

            else -> {
                _errorMessage.value = if (registrar) {
                    "Error al registrar asistencia: ${e.message}"
                } else {
                    "Error al verificar asistencia: ${e.message}"
                }
            }
        }
    }

    fun verificarAsistencia(boleta: String, idETS: Int) {
        viewModelScope.launch {
            try {
                _loadingState.value = true
                val fecha = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                val response = RetrofitInstance.alumnosDetalle.verificarAsistencias(
                    listOf(boleta),
                    fecha
                )
                _asistenciaYaRegistrada.value = response.body()?.get(boleta) ?: false
            } catch (e: Exception) {
                handleAsistenciaError(e, false)
            } finally {
                _loadingState.value = false
            }
        }
    }


    fun registrarAsistencia(boleta: String, idETS: Int) {
        gestionarAsistencia(boleta, idETS, registrar = true)
    }


    fun clearErrorMessage() {
        _errorMessage.value = null
    }

    fun resetAsistenciaFlags() {
        _registroSuccess.value = false
        _asistenciaYaRegistrada.value = false
    }




    // ======================== Funcion para comparar datos credencial + DAE ========================
    fun compararDatos(boleta: String, datosWeb: DatosWeb) {
        viewModelScope.launch {
            try {
                _loadingState.value = true
                val response = RetrofitInstance.alumnosDetalle.compararDatos(boleta, datosWeb)
                _comparacionResultado.value = response
                _mostrarDialogoComparacion.value = true
            } catch (e: Exception) {
                _comparacionResultado.value = ComparacionResponse(
                    false,
                    listOf("Error al comparar datos: ${e.localizedMessage}")
                )
                _mostrarDialogoComparacion.value = true
                Log.e("ComparacionDatos", "Error al comparar datos", e)
            } finally {
                _loadingState.value = false
            }
        }
    }

    fun cerrarDialogoComparacion() {
        _mostrarDialogoComparacion.value = false
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
                    //fetchImagenReporte(idets, boleta)

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



    fun fetchImagenReporte(idets: Int, boleta: String, onComplete: () -> Unit) {
        viewModelScope.launch {
            _loadingState.value = true
            try {
                // 1. Obtener la URL de la imagen desde la API
                val imageUrl = try {
                    val response = RetrofitInstance.apiReporteInfo.obtenerImagenReporte(idets, boleta)
                    response?.imageUrl?.trim()?.replace("\"", "")
                } catch (e: HttpException) {
                    Log.e("ReporteViewModel", "Error HTTP al obtener la URL: ${e.code()} - ${e.message()}")
                    null
                } catch (e: IOException) {
                    Log.e("ReporteViewModel", "Error de red al obtener la URL: ${e.message}")
                    null
                }

                if (imageUrl.isNullOrBlank()) {
                    Log.e("ReporteViewModel", "URL de imagen inválida obtenida del servidor")
                    _imagenBytes.value = null
                    return@launch
                }

                Log.d("ReporteViewModel", "URL de la imagen obtenida: $imageUrl")

                // 2. Descargar los bytes desde la URL obtenida
                _imagenBytes.value = withContext(Dispatchers.IO) {
                    var connection: HttpURLConnection? = null
                    return@withContext try {
                        val url = URL(imageUrl)
                        connection = url.openConnection() as HttpURLConnection
                        connection!!.connectTimeout = 15_000
                        connection!!.readTimeout = 15_000
                        connection!!.doInput = true

                        connection!!.inputStream.use { inputStream ->
                            val buffer = ByteArrayOutputStream()
                            inputStream.copyTo(buffer)
                            buffer.toByteArray().takeIf { it.isNotEmpty() }
                                ?: throw IOException("La imagen del reporte está vacía")
                        }
                    } catch (e: Exception) {
                        Log.e("ReporteViewModel", "Error al descargar la imagen del reporte: ${e.message}", e)
                        null
                    } finally {
                        connection?.disconnect()
                    }
                }

            } catch (e: Exception) {
                Log.e("ReporteViewModel", "Error general al obtener o descargar la imagen del reporte: ${e.localizedMessage}")
                _imagenBytes.value = null
            } finally {
                _loadingState.value = false
                onComplete()
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

    private val _eliminandoReporte = MutableStateFlow(false)
    val eliminandoReporte: StateFlow<Boolean> = _eliminandoReporte

    fun eliminarReporte(boleta: String, idETS: Int) {
        viewModelScope.launch {
            _eliminandoReporte.value = true
            _eliminacionExitosa.value = null
            _mensajeEliminacion.value = null
            _eliminacionCompletada.value = null
            try {


                val response = RetrofitInstance.ingresoSalonApi.eliminarReporte(idETS, boleta)

                if (response.isSuccessful) {
                    val jsonResponse = response.body()
                    _eliminacionExitosa.value = true
                    _mensajeEliminacion.value = jsonResponse?.get("mensaje")?.asString ?: "Eliminación exitosa."
                    _eliminacionCompletada.value = true

                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorMessage = try {
                        errorBody?.let { JSONObject(it).getString("mensaje") }
                            ?: "Ocurrió un fallo en el proceso: Código" // ${response.code()}
                    } catch (e: Exception) {
                        "Ocurrió un fallo en el proceso: Código" // ${response.code()}
                    }
                    _mensajeEliminacion.value = errorMessage
                }
            } catch (e: ConnectException) {
                _eliminacionExitosa.value = false
                _mensajeEliminacion.value = "Error de conexión";
                _eliminacionCompletada.value = false
            } catch (e: HttpException) {
                _eliminacionExitosa.value = false
                _mensajeEliminacion.value = "Ocurrió un fallo en el proceso: Código"; // ${e.code()}
                _eliminacionCompletada.value = false
            } catch (e: Exception) {
                _eliminacionExitosa.value = false
                _mensajeEliminacion.value = "Ocurrió un fallo en el proceso:"; //  ${e.localizedMessage}
                _eliminacionCompletada.value = false
            } finally {
                _eliminandoReporte.value = false
            }
        }
    }

    fun clearEliminacionEstado() {
        _eliminacionExitosa.value = null
        _mensajeEliminacion.value = null

    }

    fun clearEliminacionCompletada() {
        _eliminacionCompletada.value = null
    }



    private val _idETSFlujo = MutableStateFlow<String?>(null)
    val idETSFlujo: StateFlow<String?> = _idETSFlujo

    private val _boletaFlujo = MutableStateFlow<String?>(null)
    val boletaFlujo: StateFlow<String?> = _boletaFlujo

    fun guardarIdETSFlujo(id: String) {
        _idETSFlujo.value = id
    }

    fun guardarBoletaFlujo(boleta: String) {
        _boletaFlujo.value = boleta
    }

    fun limpiarInfoFlujo() {
        _idETSFlujo.value = null
        _boletaFlujo.value = null
    }


    fun resetFotoAlumno() {
        _fotoAlumno.value = null
    }

}
