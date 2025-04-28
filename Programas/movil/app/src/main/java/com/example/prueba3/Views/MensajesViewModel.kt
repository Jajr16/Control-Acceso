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
import com.google.firebase.crashlytics.FirebaseCrashlytics

@HiltViewModel
class MensajesViewModel @Inject constructor(): ViewModel() {
    private val TAG = "MensajesViewModel"

    private val _listaUsuarios = MutableStateFlow<List<ListadoUsuarios>>(emptyList())
    val listaUsuarios: StateFlow<List<ListadoUsuarios>> = _listaUsuarios

    fun getUsuarios(user: String) {
        FirebaseCrashlytics.getInstance().log("MensajesViewModel: getUsuarios llamado para usuario = $user")
        viewModelScope.launch {
            try {
                val result = RetrofitInstance.getListaUsuariosChat.getPersonasToChat(user)
                _listaUsuarios.emit(result)
                FirebaseCrashlytics.getInstance().log("MensajesViewModel: Resultado de getUsuarios - $_listaUsuarios")
            } catch (e: Exception) {
                _listaUsuarios.emit(emptyList())
                FirebaseCrashlytics.getInstance().log("MensajesViewModel: Error al obtener usuarios: ${e.message}")
                FirebaseCrashlytics.getInstance().recordException(e)
            }
        }
    }

    //    LISTADO DE CHATS CON LOS QUE HA CHATEADO EL USUARIO
    private val _chats = MutableStateFlow<List<ListChatResponse>>(emptyList())
    val chats: StateFlow<List<ListChatResponse>> = _chats

    private val _mensajeError = MutableStateFlow<String?>(null)
    val mensajeError: StateFlow<String?> = _mensajeError

    fun getChats(user: String) {
        FirebaseCrashlytics.getInstance().log("MensajesViewModel: getChats llamado para usuario = $user")
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.getListaUsuariosChat.getChats(user)

                FirebaseCrashlytics.getInstance().log("MensajesViewModel: Respuesta de getChats - Código = ${response.code()}, Body = ${response.body()}")

                if (response.isSuccessful) {
                    response.body()?.let { body ->
                        if (body.chats.isNullOrEmpty()) {
                            _mensajeError.value = body.mensaje ?: "No has hablado con nadie."
                            _chats.value = emptyList()
                            FirebaseCrashlytics.getInstance().log("MensajesViewModel: No hay chats para mostrar. Mensaje: ${body.mensaje}")
                        } else {
                            _chats.value = body.chats
                            _mensajeError.value = null
                            FirebaseCrashlytics.getInstance().log("MensajesViewModel: Chats cargados - Tamaño = ${_chats.value.size}")
                        }
                    }
                } else {
                    _mensajeError.value = "Error en la respuesta: ${response.code()}"
                    _chats.value = emptyList()
                    FirebaseCrashlytics.getInstance().log("MensajesViewModel: Error en la respuesta de getChats - Código = ${response.code()}")
                }
            } catch (e: Exception) {
                _mensajeError.value = "Error en la conexión: ${e.message}"
                _chats.value = emptyList()
                FirebaseCrashlytics.getInstance().log("MensajesViewModel: Error de conexión al obtener chats: ${e.message}")
                FirebaseCrashlytics.getInstance().recordException(e)
            }
        }
    }

    //    MENSAJES DE UN CHAT EN ESPECÍFICO
    private val _mensajes = MutableStateFlow<List<Mensaje>>(emptyList())
    val mensajes: StateFlow<List<Mensaje>> = _mensajes

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> get() = _errorMessage

    fun getMessages(remitente: String, destinatario: String) {
        Log.d(TAG, "getMessages called with remitente = $remitente, destinatario = $destinatario")
        FirebaseCrashlytics.getInstance().log("MensajesViewModel: getMessages llamado con remitente = $remitente, destinatario = $destinatario")
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.getListaUsuariosChat.getHistorial(remitente, destinatario)
                Log.d(TAG, "Response code: ${response.code()}, Body: ${response.body()}")
                FirebaseCrashlytics.getInstance().log("MensajesViewModel: Respuesta de getHistorial - Código = ${response.code()}, Body = ${response.body()}")
                if (response.isSuccessful) {
                    _mensajes.value = response.body() ?: emptyList()
                    Log.d(TAG, "Mensajes cargados: ${_mensajes.value}")
                    FirebaseCrashlytics.getInstance().log("MensajesViewModel: Mensajes cargados - Tamaño = ${_mensajes.value.size}")
                } else {
                    _errorMessage.value = "Error fetching messages: ${response.code()}"
                    Log.d(TAG, "Error fetching messages: ${response.code()}")
                    FirebaseCrashlytics.getInstance().log("MensajesViewModel: Error al obtener mensajes - Código = ${response.code()}")
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error fetching messages: ${e.message}"
                Log.d(TAG, "Error fetching messages: ${e.message}")
                FirebaseCrashlytics.getInstance().log("MensajesViewModel: Error al obtener mensajes: ${e.message}")
                FirebaseCrashlytics.getInstance().recordException(e)
            }
        }
    }

    private val _nuevoMensajeEnviado = MutableSharedFlow<Unit>()
    val nuevoMensajeEnviado: SharedFlow<Unit> = _nuevoMensajeEnviado

    @RequiresApi(Build.VERSION_CODES.O)
    fun sendMessage(remitente: String, destinatario: String, mensaje: String) {
        Log.d(TAG, "sendMessage called with remitente = $remitente, destinatario = $destinatario, mensaje = $mensaje")
        FirebaseCrashlytics.getInstance().log("MensajesViewModel: sendMessage llamado con remitente = $remitente, destinatario = $destinatario, mensaje = $mensaje")
        viewModelScope.launch {
            try {
                val result = RetrofitInstance.getListaUsuariosChat.enviarMensaje(
                    sendMensaje(remitente, destinatario, mensaje)
                )
                Log.d(TAG, "Resultado de enviarMensaje - Success = ${result.success}")
                FirebaseCrashlytics.getInstance().log("MensajesViewModel: Resultado de enviarMensaje - Success = ${result.success}")
                if (result.success) {
                    // Actualizar mensajes inmediatamente después de enviar
                    getMessages(remitente, destinatario)
                    _nuevoMensajeEnviado.emit(Unit)
                    Log.d(TAG, "Mensaje enviado exitosamente. Recargando mensajes.")
                    FirebaseCrashlytics.getInstance().log("MensajesViewModel: Mensaje enviado exitosamente. Recargando mensajes.")
                } else {
                    _errorMessage.value = "Error al enviar mensaje"
                    Log.d(TAG, "Error al enviar mensaje")
                    FirebaseCrashlytics.getInstance().log("MensajesViewModel: Error al enviar mensaje.")
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error sending message: ${e.message}"
                Log.d(TAG, "Error sending message: ${e.message}")
                FirebaseCrashlytics.getInstance().log("MensajesViewModel: Error al enviar mensaje: ${e.message}")
                FirebaseCrashlytics.getInstance().recordException(e)
            }
        }
    }


    fun refreshMessages(remitente: String, destinatario: String) {
        Log.d(TAG, "refreshMessages called with remitente = $remitente, destinatario = $destinatario")
        FirebaseCrashlytics.getInstance().log("MensajesViewModel: refreshMessages llamado, remitente: $remitente, destinatario: $destinatario")
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.getListaUsuariosChat.getHistorial(remitente, destinatario)
                Log.d(TAG, "Response code: ${response.code()}, Body: ${response.body()}")
                FirebaseCrashlytics.getInstance().log("MensajesViewModel: Respuesta de refreshMessages - Código = ${response.code()}, Body = ${response.body()}")
                if (response.isSuccessful) {
                    val newMessages = response.body() ?: emptyList()
                    Log.d(TAG, "New messages fetched: $newMessages")
                    FirebaseCrashlytics.getInstance().log("MensajesViewModel: Nuevos mensajes obtenidos = $newMessages")
                    updateMessages(remitente, destinatario, newMessages)
                } else {
                    _errorMessage.value = "Error fetching messages: ${response.code()}"
                    FirebaseCrashlytics.getInstance().log("MensajesViewModel: Error al refrescar mensajes: ${response.code()}")
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error fetching messages: ${e.message}"
                Log.e(TAG, "Error in refreshMessages: ${e.message}", e)
                FirebaseCrashlytics.getInstance().log("MensajesViewModel: Error al refrescar mensajes: ${e.message}")
                FirebaseCrashlytics.getInstance().recordException(e)
            }
        }
    }


    fun updateMessages(remitente: String, destinatario: String, nuevosMensajes: List<Mensaje>) {
        Log.d(TAG, "updateMessages called with remitente = $remitente, destinatario = $destinatario, nuevosMensajes = $nuevosMensajes")
        FirebaseCrashlytics.getInstance().log("MensajesViewModel: updateMessages llamado con nuevosMensajes = $nuevosMensajes")
        viewModelScope.launch {
            val currentMessages = _mensajes.value
            _mensajes.value = currentMessages + nuevosMensajes.filter { !currentMessages.contains(it) }
            Log.d(TAG, "Mensajes actualizados: ${_mensajes.value}")
            FirebaseCrashlytics.getInstance().log("MensajesViewModel: Lista de mensajes actualizada - Tamaño = ${_mensajes.value.size}")
        }
    }
}