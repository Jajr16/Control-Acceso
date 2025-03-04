package com.example.prueba3.Views

import RetroFit.RetrofitInstance
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.prueba3.Clases.confirmInscripcion
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {

    // Para el alumno
    private val _StatusInscription = MutableStateFlow<Boolean?>(null)
    val StatusInscripcion: StateFlow<Boolean?> = _StatusInscription

    // Para el personal de seguridad y docente
    private val _StatusValidacion = MutableStateFlow<Boolean?>(null)
    val StatusValidacion: StateFlow<Boolean?> = _StatusValidacion

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage




    // Verifica la inscripción del alumno en el ETS
    fun getConfirmationInscription(username: String) {
        viewModelScope.launch {
            try {
                val confirmacion = RetrofitInstance.confirmacionInscripcion.getConfirmInscrip(username)
                _StatusInscription.value = confirmacion.message // true o false según esté inscrito
            } catch (e: Exception) {
                _StatusInscription.value = null // Error en la validación
            }
        }
    }

    fun getConfirmationValidacion(username: String) {
        viewModelScope.launch {
            try {
                val validacion = RetrofitInstance.confirmacionValidacion.getConfirmValid(username)

                // Verifica si el campo 'error' es nulo (sin error) y si el 'tipoUsuario' es válido
                if (validacion.error == null && validacion.tipoUsuario != null) {
                    _StatusValidacion.value = true // Validación exitosa
                    _errorMessage.value = null
                } else {
                    _StatusValidacion.value = false // Error en la validación
                    _errorMessage.value = validacion.error ?: "Error desconocido"
                }
            } catch (e: Exception) {
                _StatusValidacion.value = false // Error en la validación
                _errorMessage.value = "Error en la validación: ${e.message}"
            }
        }
    }

}
