package com.example.prueba3.Views

import RetroFit.RetrofitInstance
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.prueba3.Clases.confirmInscripcion
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {

    private val _StatusInscription = MutableStateFlow<Boolean?>(null)
    val StatusInscripcion: StateFlow<Boolean?> = _StatusInscription

    fun getConfirmationInscription(username: String) {
        viewModelScope.launch {
            try {
                val confirmacion = RetrofitInstance.confirmacionInscripcion.getConfirmInscrip(username)
                _StatusInscription.value = confirmacion.message
            } catch (e: Exception) {
                _StatusInscription.value = null
            }
        }
    }
}