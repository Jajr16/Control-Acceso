package com.example.prueba3.Views

import com.example.prueba3.Clases.SalonETSResponse
import RetroFit.RetrofitInstance
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class EtsInfoViewModel : ViewModel() {
    private val _etsDetailState = MutableStateFlow<SalonETSResponse?>(null)
    val etsDetailState: StateFlow<SalonETSResponse?> = _etsDetailState

    // ESTADO DE CARGA
    private val _loadingState = MutableStateFlow(true)
    val loadingState: StateFlow<Boolean> = _loadingState

    fun fetchEtsDetail(idETS: Int) {
        viewModelScope.launch {
            try {
                _loadingState.value = true
                val response = RetrofitInstance.ETSListapi.getEtsDetail(idETS)
                if (response?.Salones != null) {
                    _etsDetailState.value = response
                } else {
                    _etsDetailState.value = null // Si no hay salones
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _etsDetailState.value = null // Si hay error en la solicitud
            } finally {
                _loadingState.value = false
            }
        }
    }
}

