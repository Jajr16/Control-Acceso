package com.example.prueba3.Views

import RetroFit.RetrofitCamaraInstance
import android.graphics.Bitmap
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.prueba3.Clases.PythonResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody



class CamaraViewModel : ViewModel() {

    private var currentBoleta: String? = null
    private var currentIdETS: String? = null

    private val _pythonResponse = MutableStateFlow<PythonResponse?>(null)
    val pythonResponse: StateFlow<PythonResponse?> = _pythonResponse

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    val imagenBitmap = mutableStateOf<Bitmap?>(null)

    // Propiedad para la precisión mostrada en InformacionAlumno
    val precision = mutableStateOf<Float?>(null)

    // Propiedad para la precisión dentro de PythonResponse
    private var responsePrecision: Float? = null

    fun setImagen(imagen: Bitmap) {
        imagenBitmap.value = imagen
    }

    fun uploadImage(image: MultipartBody.Part, boleta: RequestBody) {
        viewModelScope.launch {
            try {
                val response = RetrofitCamaraInstance.uploadImage(image, boleta)
                if (response.isSuccessful) {
                    _pythonResponse.value = response.body()
                    responsePrecision = response.body()?.precision
                    Log.d("CamaraViewModel", "Precision del servidor: $responsePrecision")
                    setPrecision(responsePrecision)
                } else {
                    _errorMessage.value = "Error en la comunicación con Python: ${response.errorBody()?.string()}"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error de red: ${e.message}"
            }
        }
    }

    fun setPrecision(precision: Float?) {
        Log.d("CamaraViewModel", "Precision establecida: $precision")
        this.precision.value = precision
    }

    fun updateBoletaAndIdETS(boleta: String?, idETS: String?) {
        if (currentBoleta != boleta || currentIdETS != idETS) {
            currentBoleta = boleta
            currentIdETS = idETS
            imagenBitmap.value = null
            precision.value = null // Resetea la precisión mostrada
        }
    }

    fun setPythonResponse(response: PythonResponse?) {
        _pythonResponse.value = response
        // Eliminar la llamada a setPrecision
    }

    fun setErrorMessage(message: String) {
        _errorMessage.value = message
    }
}