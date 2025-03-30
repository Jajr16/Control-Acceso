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
import com.example.prueba3.Clases.UpdateAceptadoRequest
import com.example.prueba3.Clases.regitrarAsistencia
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

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


    private val _fotoAlumno = MutableStateFlow<ByteArray?>(null)
    val fotoAlumno: StateFlow<ByteArray?> = _fotoAlumno

    private val _alumnoRegistro = MutableStateFlow<List<regitrarAsistencia>>(emptyList())
    val alumnoRegistro: StateFlow<List<regitrarAsistencia>> = _alumnoRegistro

    private val _registroSuccess = mutableStateOf(false)
    val registroSuccess = mutableStateOf(false)

    private val _errorMessage = mutableStateOf<String?>(null)
    val errorMessage = mutableStateOf<String?>(null)


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

    /// ======= Mostrar Informacion del alumno ===========
    fun fetchDetalleAlumnos(boleta: String) {
        viewModelScope.launch {
            try {
                _loadingState.value = true
                val alumnoState = RetrofitInstance.alumnosDetalle.getalumnosDetalle(boleta)
                _alumnosDetalle.value = alumnoState
            }catch (e: Exception) {
                _alumnosDetalle.value = emptyList()
            }finally {
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
            }catch (e: Exception) {
                _alumnosCredencial.value = emptyList()
            }finally {
                _loadingState.value = false
            }
        }
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


    //========== Registrar el ingreso a la instalacion del alumno
    fun registrarAsistencia(boleta: String) {
        viewModelScope.launch {
            try {
                _loadingState.value = true
                _errorMessage.value = null
                val response = RetrofitInstance.alumnosDetalle.getregistrarEntrada(boleta)

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
        _errorMessage.value = null
    }

    // Función para actualizar la asistencia de un alumno
    suspend fun updateAsistencia(boleta: String, idETS: Int, aceptado: Int) {
        try {
            val response = RetrofitInstance.aceptadoApi.updateAceptado(UpdateAceptadoRequest(boleta, idETS, aceptado))

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
}
