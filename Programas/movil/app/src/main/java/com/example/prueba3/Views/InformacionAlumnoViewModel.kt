package com.example.prueba3.Views

import RetroFit.ApiReporte
import RetroFit.RetrofitInstance
import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.prueba3.Clases.CreacionReporte
import com.google.android.gms.common.api.Response
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException // Importa retrofit2.HttpException
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.net.ConnectException

class InformacionAlumnoViewModel : ViewModel() {

    private val _envioExitoso = MutableStateFlow<String?>(null)
    val envioExitoso: StateFlow<String?> = _envioExitoso

    private val _errorEnvio = MutableStateFlow<Throwable?>(null)
    val errorEnvio: StateFlow<Throwable?> = _errorEnvio

    private val _cargando = MutableStateFlow(false)
    val cargando: StateFlow<Boolean> = _cargando


    fun enviarDatosAlServidor(
        razon: String?,
        tipo: String,
        boleta: String,
        idETS: String,
        precision: String?,
        hora: String,
        imagenBitmap: Bitmap?,
        context: Context,
        onComplete: () -> Unit, // <---- AÑADE ESTE PARÁMETRO
        onError: (String) -> Unit // <---- AÑADE ESTE PARÁMETRO
    ) {
        viewModelScope.launch {
            _cargando.value = true
            _envioExitoso.value = null // Reiniciar el estado de éxito
            _errorEnvio.value = null   // Reiniciar el estado de error
            try {
                Log.d("RetrofitResponse", "Mande razón: $razon")

                var imagenPart: MultipartBody.Part? = null
                if (imagenBitmap != null) {
                    val imagenFile = bitmapToFile(imagenBitmap, context)
                    val imagenBody = imagenFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
                    imagenPart = MultipartBody.Part.createFormData("imagen", imagenFile.name, imagenBody)
                }

                val razonBody = razon?.toRequestBody("text/plain".toMediaTypeOrNull())
                val tipoBody = tipo.toRequestBody("text/plain".toMediaTypeOrNull())
                val boletaBody = boleta.toRequestBody("text/plain".toMediaTypeOrNull())
                val idETSBody = idETS.toRequestBody("text/plain".toMediaTypeOrNull())
                val precisionBody = precision?.toRequestBody("text/plain".toMediaTypeOrNull())
                val horaBody = hora.toRequestBody("text/plain".toMediaTypeOrNull())

                val response = RetrofitInstance.apiReporte.enviarDatos(
                    razonBody,
                    tipoBody,
                    boletaBody,
                    idETSBody,
                    precisionBody,
                    horaBody,
                    imagenPart
                )

                if (response.isSuccessful) {
                    val respuesta: CreacionReporte? = response.body()
                    val mensaje: String? = respuesta?.mensaje
                    Log.d("RetrofitResponse", "Respuesta del servidor: $mensaje")
                    _envioExitoso.emit(mensaje ?: "Operación exitosa")
                    onComplete() // <---- LLAMA AL CALLBACK DE ÉXITO
                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorMessage = "Ocurrió un fallo en el proceso: ${response.code()}, ${errorBody ?: "Error desconocido"}"
                    Log.e("RetrofitResponse", errorMessage)
                    _errorEnvio.emit(Exception(errorMessage))
                    onError(errorMessage) // <---- LLAMA AL CALLBACK DE ERROR
                }
            } catch (e: ConnectException) {
                Log.e("RetrofitResponse", "Error de conexión: ${e.message}")
                _errorEnvio.emit(e)
                onError("Error de conexión: ${e.message}") // <---- LLAMA AL CALLBACK DE ERROR
            } catch (e: HttpException) {
                val errorMessage = "Ocurrió un fallo en el proceso: ${e.code()} ${e.message()}"
                Log.e("RetrofitResponse", "Error HTTP: $errorMessage")
                _errorEnvio.emit(Exception(errorMessage))
                onError(errorMessage) // <---- LLAMA AL CALLBACK DE ERROR
            } catch (e: Exception) {
                val errorMessage = "Ocurrió un fallo inesperado: ${e.message}"
                Log.e("RetrofitResponse", "Error general: $errorMessage")
                _errorEnvio.emit(Exception(errorMessage))
                onError(errorMessage) // <---- LLAMA AL CALLBACK DE ERROR
            } finally {
                _cargando.value = false
            }
        }
    }

    private fun bitmapToFile(bitmap: Bitmap, context: Context): File {
        val file = File(context.cacheDir, "imagen.jpg")
        try {
            val outputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            outputStream.flush()
            outputStream.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return file
    }

    fun clearEnvioExitoso() {
        viewModelScope.launch {
            _envioExitoso.emit(null)
        }
    }

    fun clearErrorEnvio() {
        viewModelScope.launch {
            _errorEnvio.emit(null)
        }
    }
}