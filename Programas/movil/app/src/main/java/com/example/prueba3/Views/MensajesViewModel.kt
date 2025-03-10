package com.example.prueba3.Views

import RetroFit.RetrofitInstance
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.prueba3.Clases.ListadoUsuarios
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MensajesViewModel : ViewModel() {
    private val _listaUsuarios = MutableStateFlow<List<ListadoUsuarios>>(emptyList())
    val listaUsuarios: StateFlow<List<ListadoUsuarios>> = _listaUsuarios

    fun getUsuarios() {
        viewModelScope.launch {
            try {
                _listaUsuarios.emit(RetrofitInstance.getListaUsuariosChat.getPersonasToChat())
            } catch (e: Exception) {
                _listaUsuarios.emit(emptyList())
            }
        }
    }
}