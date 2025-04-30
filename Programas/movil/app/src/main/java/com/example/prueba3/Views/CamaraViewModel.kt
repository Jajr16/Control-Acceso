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
import java.io.File


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

    fun setImagen(imagen: Bitmap) {
        imagenBitmap.value = imagen
    }

    fun uploadImage(imageFile: File, boleta: String) { // Changed parameters
        viewModelScope.launch {
            try {
                RetrofitCamaraInstance.uploadImage(boleta, imageFile) // Call the new Retrofit function
                    .onSuccess { pythonResponseData ->
                        _pythonResponse.value = pythonResponseData
                        pythonResponseData?.let {
                            val distance = it.distance.toFloat() // Directly convert Double to Float
                            val threshold = 0.4f

                            if (distance > threshold) {
                                setPrecision(0.0f)
                                Log.d("CamaraViewModel", "Distancia ($distance) mayor que el umbral ($threshold), precisión establecida en 0.")
                            } else {
                                // Transformación lineal: 0 -> 1.0, 0.4 -> 0.6
                                val normalizedDistance = distance / threshold
                                val calculatedPrecision = 1.0f - (0.4f * normalizedDistance)
                                setPrecision(calculatedPrecision)
                                Log.d("CamaraViewModel", "Distancia: $distance, Precisión calculada: $calculatedPrecision")
                            }
                            // The 'else' block for distance being null is no longer strictly necessary
                            // since 'it.distance' is a Double, it will always have a value (unless the JSON is malformed).
                            // However, you might want to keep it for robustness in case of unexpected data.
                        }
                    }
                    .onFailure { error ->
                        _errorMessage.value = "Error en la comunicación con Python: ${error.message}"
                        setPrecision(null)
                    }
            } catch (e: Exception) {
                _errorMessage.value = "Error inesperado: ${e.message}"
                setPrecision(null)
            }
        }
    }

    fun setPrecision(precisionValue: Float?) {
        Log.d("CamaraViewModel", "Precisión establecida: $precisionValue")
        this.precision.value = precisionValue
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
        // La lógica de la precisión ahora está en uploadImage
    }

    fun setErrorMessage(message: String) {
        _errorMessage.value = message
    }
}