package com.example.prueba3.Views

import RetroFit.RetrofitInstance
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.prueba3.Clases.AlumnosInfo
import com.example.prueba3.Clases.UpdateAceptadoRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AlumnosViewModel : ViewModel() {
    private val _alumnosList = MutableStateFlow<List<AlumnosInfo>>(emptyList())
    val alumnosList: StateFlow<List<AlumnosInfo>> = _alumnosList

    // Función para obtener los datos de alumnos
    fun fetchAlumno(ETSid: String) {
        viewModelScope.launch {
            try {
                val alumnos = RetrofitInstance.alumnosApi.getAlumnoList(ETSid)
                _alumnosList.value = alumnos // Actualizamos la lista de alumnos
            } catch (e: Exception) {
                _alumnosList.value = emptyList() // Manejo de error
            }
        }
    }

    // Función para actualizar la asistencia de un alumno
    suspend fun updateAsistencia(boleta: String, idETS: Int, aceptado: Boolean) {
        try {
            val response = RetrofitInstance.aceptadoApi.updateAceptado(UpdateAceptadoRequest(boleta, idETS, aceptado))

            // Actualizamos solo el alumno modificado en la lista local
            _alumnosList.value = _alumnosList.value.map { alumno ->
                if (alumno.Boleta == boleta) {
                    alumno.copy(Aceptado = aceptado) // Modificamos solo el alumno con el 'Boleta' correspondiente
                } else {
                    alumno // Dejamos los demás alumnos sin cambios
                }
            }
        } catch (e: Exception) {
            println("Error al actualizar: ${e.localizedMessage}")
        }
    }
}

