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
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MensajesViewModel @Inject constructor(): ViewModel() {
    private val TAG = "MensajesViewModel"

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

    private val _nuevoMensajeEnviado = MutableSharedFlow<Unit>()
    val nuevoMensajeEnviado: SharedFlow<Unit> = _nuevoMensajeEnviado

    @RequiresApi(Build.VERSION_CODES.O)
    fun sendMessage(remitente: String, destinatario: String, mensaje: String) {
        viewModelScope.launch {
            try {
                val result = RetrofitInstance.getListaUsuariosChat.enviarMensaje(
                    sendMensaje(remitente, destinatario, mensaje)
                )
                if (result.success) {
                    // Actualizar mensajes inmediatamente después de enviar
                    getMessages(remitente, destinatario)
                    _nuevoMensajeEnviado.emit(Unit)
                } else {
                    _errorMessage.value = "Error al enviar mensaje"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error sending message: ${e.message}"
            }
        }
    }


    fun refreshMessages(remitente: String, destinatario: String) {
        viewModelScope.launch {
            Log.d(TAG, "refreshMessages called, remitente: $remitente, destinatario: $destinatario")
            try {
                val response = RetrofitInstance.getListaUsuariosChat.getHistorial(remitente, destinatario)
                if (response.isSuccessful) {
                    val newMessages = response.body() ?: emptyList()
                    Log.d(TAG, "New messages fetched: $newMessages")
                    updateMessages(remitente, destinatario, newMessages)
                } else {
                    _errorMessage.value = "Error fetching messages: ${response.code()}"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error fetching messages: ${e.message}"
                Log.e(TAG, "Error in refreshMessages: ${e.message}", e)
            }
        }
    }


    fun updateMessages(remitente: String, destinatario: String, nuevosMensajes: List<Mensaje>) {
        viewModelScope.launch {
            val currentMessages = _mensajes.value
            _mensajes.value = currentMessages + nuevosMensajes.filter { !currentMessages.contains(it) }
        }
    }

}