package com.example.prueba3.Views

import RetroFit.RetrofitInstance
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.prueba3.Clases.ListChatResponse
import com.example.prueba3.Clases.ListadoUsuarios
import com.example.prueba3.Clases.Mensaje
import com.example.prueba3.Clases.sendMensaje
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class MensajesViewModel @Inject constructor(): ViewModel() {

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

//    LISTADO DE CHATS CON LOS QUE HA CHATEADO EL USUARIO
    private val _chats = MutableStateFlow<List<ListChatResponse>>(emptyList())
    val chats: StateFlow<List<ListChatResponse>> = _chats

    private val _mensajeError = MutableStateFlow<String?>(null)
    val mensajeError: StateFlow<String?> = _mensajeError

    fun getChats(user: String) {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.getListaUsuariosChat.getChats(user)

                if (response.isSuccessful) {
                    response.body()?.let { body ->
                        if (body.chats.isNullOrEmpty()) {
                            _mensajeError.value = body.mensaje ?: "No has hablado con nadie."
                            _chats.value = emptyList()
                        } else {
                            _chats.value = body.chats
                            _mensajeError.value = null
                        }
                    }
                } else {
                    _mensajeError.value = "Error en la respuesta: ${response.code()}"
                    _chats.value = emptyList()
                }
            } catch (e: Exception) {
                _mensajeError.value = "Error en la conexión: ${e.message}"
                _chats.value = emptyList()
            }
        }
    }

//    MENSAJES DE UN CHAT EN ESPECÍFICO
    private val _mensajes = MutableStateFlow<List<Mensaje>>(emptyList())
    val mensajes: StateFlow<List<Mensaje>> = _mensajes

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> get() = _errorMessage

    fun getMessages(remitente: String, destinatario: String) {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.getListaUsuariosChat.getHistorial(remitente, destinatario)
                if (response.isSuccessful) {
                    _mensajes.value = response.body() ?: emptyList()
                } else {
                    _errorMessage.value = "Error fetching messages: ${response.code()}"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error fetching messages: ${e.message}"
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun sendMessage(remitente: String, destinatario: String, mensaje: String) {
        viewModelScope.launch {
            try {
                val result = RetrofitInstance.getListaUsuariosChat.enviarMensaje(sendMensaje(remitente, destinatario, mensaje))
                if (result.success) {
                    agregarMensaje(remitente, mensaje)
                } else {
                    _errorMessage.value = "Error sending message"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error sending message: ${e.message}"
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun agregarMensaje(remitente: String, mensaje: String) {
        viewModelScope.launch {
            val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
            val nuevoMensaje = Mensaje(
                usuario = remitente,
                mensaje = mensaje,
                fecha = LocalDateTime.now().format(formatter)
            )
            _mensajes.value = _mensajes.value + nuevoMensaje
        }
    }

    fun refreshMessages(remitente: String, destinatario: String) {
        viewModelScope.launch {
            Log.d("MensajesViewModel", "refreshMessages called, remitente: $remitente, destinatario: $destinatario")
            try {
                val response = RetrofitInstance.getListaUsuariosChat.getHistorial(remitente, destinatario)
                if (response.isSuccessful) {
                    val newMessages = response.body() ?: emptyList()
                    Log.d("MensajesViewModel", "New messages fetched: $newMessages")
                    val currentMessages = _mensajes.value
                    _mensajes.value = if (newMessages.isEmpty()) {
                        currentMessages
                    } else {
                        currentMessages + newMessages.filter { !currentMessages.contains(it) }
                    }
                } else {
                    _errorMessage.value = "Error fetching messages: ${response.code()}"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error fetching messages: ${e.message}"
                Log.e("MensajesViewModel", "Error in refreshMessages: ${e.message}", e)
            }
        }
    }

}