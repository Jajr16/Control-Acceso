package com.example.prueba3.Views

import RetroFit.RetrofitInstance
import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.prueba3.Clases.CalendarDays
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class DiasETSModel() : ViewModel() {
    private val _text = MutableStateFlow<String?>(null)
    val text: StateFlow<String?> = _text

    // ESTADO DE CARGA
    private val _loadingState = MutableStateFlow(false)
    val loadingState: StateFlow<Boolean> = _loadingState

    fun getDays() {
        viewModelScope.launch {
            try {
                _loadingState.value = true
                val response = RetrofitInstance.getDaysETS.getDaysETS()
                _text.value = response.text

            } catch (e: Exception) {
                e.printStackTrace()
                _text.value = null
            } finally {
                _loadingState.value = false
            }
        }
    }

}