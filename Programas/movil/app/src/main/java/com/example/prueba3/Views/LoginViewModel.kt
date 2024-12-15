package com.example.prueba3.Views

import android.content.SharedPreferences
import com.example.prueba3.Clases.LoginResponse
import RetroFit.RetrofitInstance
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.prueba3.Clases.LoginRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LoginViewModel(private val sharedPreferences: SharedPreferences) : ViewModel() {

    private val _loginResponse = MutableStateFlow<LoginResponse?>(null)
    val loginResponse: StateFlow<LoginResponse?> = _loginResponse

    private val _loginError = MutableStateFlow<String?>(null)
    val loginError: StateFlow<String?> = _loginError

    fun saveUserName(username: String) {
        val editor = sharedPreferences.edit()
        editor.putString("username", username)  // Guardamos el nombre de usuario
        editor.apply()
    }

    fun saveUserRole(role: String) {
        val editor = sharedPreferences.edit()
        editor.putString("userRole", role)
        editor.apply()
    }

    fun login(username: String, password: String) {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.loginApi.login(LoginRequest(username, password))
                if (response.isSuccessful && response.body() != null) {
                    _loginResponse.value = response.body()
                } else {
                    _loginError.value = "Error de autenticación: ${response.code()} ${response.message()}"
                }
            } catch (e: Exception) {
                _loginError.value = "Error de conexión: ${e.message}"
            }
        }
    }
}