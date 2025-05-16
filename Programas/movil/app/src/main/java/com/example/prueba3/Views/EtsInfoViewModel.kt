package com.example.prueba3.Views

import com.example.prueba3.Clases.SalonETSResponse
import RetroFit.RetrofitInstance
import RetroFit.RfcResponse
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.Call

import retrofit2.Callback
import retrofit2.Response

class EtsInfoViewModel : ViewModel() {

    private val _etsDetailState = MutableStateFlow<SalonETSResponse?>(null)
    val etsDetailState: StateFlow<SalonETSResponse?> = _etsDetailState

    private val _salonDetailState = MutableStateFlow(true)
    val salonDetailState: StateFlow<Boolean?> = _salonDetailState

    private val _loadingState = MutableStateFlow(true)
    val loadingState: StateFlow<Boolean> = _loadingState

    private val _rfcDocenteState = MutableStateFlow<String?>(null)
    val rfcDocenteState: StateFlow<String?> = _rfcDocenteState

    private val _loadingRfcState = MutableStateFlow(false)
    val loadingRfcState: StateFlow<Boolean> = _loadingRfcState

    fun fetchEtsDetail(idETS: Int) {
        viewModelScope.launch {
            try {
                _loadingState.value = true
                val response = RetrofitInstance.ETSListapi.getEtsDetail(idETS)
                System.out.println("EL RESPONSE ES: " + response);
                _etsDetailState.value = response
                if (response?.salon?.isNotEmpty() == true) {
                    _salonDetailState.value = false
                } else {
                    _salonDetailState.value = true // Si no hay salones
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _etsDetailState.value = null // Si hay error en la solicitud
            } finally {
                _loadingState.value = false
            }
        }
    }

    fun fetchRfcDocente(idets: Int) {
        viewModelScope.launch {
            try {
                _loadingRfcState.value = true
                val call: Call<RfcResponse> = RetrofitInstance.aplicaApi.obtenerRfcDocente(idets)
                call.enqueue(object : Callback<RfcResponse> {
                    override fun onResponse(
                        call: Call<RfcResponse>,
                        response: Response<RfcResponse>
                    ) {
                        if (response.isSuccessful) {
                            _rfcDocenteState.value = response.body()?.rfc
                        } else {
                            println("Error al obtener el RFC: ${response.code()}")
                            _rfcDocenteState.value = null
                        }
                        _loadingRfcState.value = false
                    }
                    override fun onFailure(call: Call<RfcResponse>, t: Throwable) {
                        println("Fallo en la petición del RFC: ${t.message}")
                        _rfcDocenteState.value = null
                        _loadingRfcState.value = false
                    }
                })
            } catch (e: Exception) {
                e.printStackTrace()
                _rfcDocenteState.value = null
                _loadingRfcState.value = false
            }
        }
    }
}

