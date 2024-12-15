package com.example.prueba3.Views

import com.example.prueba3.Clases.ListadoETS
import RetroFit.RetrofitInstance
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class EtsViewModel : ViewModel() {
    private val _etsList = MutableStateFlow<List<ListadoETS>>(emptyList())
    val etsList: StateFlow<List<ListadoETS>> = _etsList

    private val _etsInscritos = MutableStateFlow<List<ListadoETS>>(emptyList())
    val etsInscritos: StateFlow<List<ListadoETS>> = _etsInscritos

    private val _loadingState = MutableStateFlow(true)
    val loadingState: StateFlow<Boolean> = _loadingState

    init {
        fetchEtsList()
    }

    private fun fetchEtsList() {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.ETSapi.getEtsList()
                _etsList.value = response
            } catch (e: Exception) {
                // Maneja los errores aquí
                e.printStackTrace()
            }
        }
    }

    fun fetchETSInscritos(usuario: String) {
        viewModelScope.launch {
            try {
                _loadingState.value = true
                val response = RetrofitInstance.ETSapi.getEtsInscritos(usuario)
                _etsInscritos.value = response
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _loadingState.value = false
            }
        }
    }
}
