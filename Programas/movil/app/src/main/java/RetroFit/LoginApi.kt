    package com.example.prueba3
    import com.example.prueba3.Clases.LoginResponse
    import retrofit2.http.Body
    import retrofit2.http.POST
    import retrofit2.Response

    interface LoginApi {
        @POST("login/")
        suspend fun login(@Body loginData: LoginResponse): Response<LoginResponse>
    }
