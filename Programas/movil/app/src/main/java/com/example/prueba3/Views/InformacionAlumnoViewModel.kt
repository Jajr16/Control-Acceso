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

class InformacionAlumnoViewModel : ViewModel() {

    private val _envioExitoso = MutableStateFlow<String?>(null)
    val envioExitoso: StateFlow<String?> = _envioExitoso

    private val _errorEnvio = MutableStateFlow<String?>(null)
    val errorEnvio: StateFlow<String?> = _errorEnvio

    fun enviarDatosAlServidor(
        razon: String?,
        tipo: String,
        boleta: String,
        idETS: String,
        precision: String?,
        hora: String,
        imagenBitmap: Bitmap?,
        context: Context
    ) {
        viewModelScope.launch {
            try {
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
                    _envioExitoso.emit(mensaje)
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e("RetrofitResponse", "Error del servidor: ${response.code()}, ${errorBody}")
                    _errorEnvio.emit("Error ${response.code()}: ${errorBody ?: "Error desconocido"}")
                }
            } catch (e: HttpException) {
                Log.e("RetrofitResponse", "Error HTTP: ${e.code()} ${e.message()}")
                _errorEnvio.emit("Error HTTP: ${e.code()} ${e.message()}")
            } catch (e: Exception) {
                Log.e("RetrofitResponse", "Error de red: ${e.message}")
                _errorEnvio.emit("Error de red: ${e.message}")
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