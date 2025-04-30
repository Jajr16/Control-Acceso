package com.example.prueba3.Clases

import RetroFit.RetrofitInstance.sendTokenToBack
import android.app.ActivityManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.prueba3.R
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

data class FirebaseResponse(
    val message: String,
    val status: Int
)

class FCMService : FirebaseMessagingService() {
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        val sharedPreferences = getSharedPreferences("user_preferences", Context.MODE_PRIVATE)
        val username = sharedPreferences.getString("username", null)

        username?.let {
            Log.d("FCM", "Actualizando token para usuario: $it")
            sendTokenToBackend(it, token)
        }
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        try {
            Log.d("FCM", "Mensaje recibido: ${remoteMessage.data}")

            remoteMessage.data.let { data ->
                Log.d("FCM", "Datos del mensaje: $data")
            }

            remoteMessage.notification?.let { notification ->
                Log.d("FCM", "Notificación: ${notification.title} - ${notification.body}")

                if (isAppInForeground()) {
                    showInAppMessage(notification)
                } else {
                    showNotification(notification)
                }
            }
        } catch (e: Exception) {
            Log.e("FCM", "Error al procesar mensaje: ${e.message}")
            e.printStackTrace()
        }
    }

    private fun isAppInForeground(): Boolean {
        val activityManager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val appProcesses = activityManager.runningAppProcesses ?: return false
        val packageName = packageName
        for (appProcess in appProcesses) {
            if (appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND
                && appProcess.processName == packageName) {
                return true
            }
        }
        return false
    }

    private fun showInAppMessage(notification: RemoteMessage.Notification) {
        try {
            val intent = Intent("NOTIFICACION_RECIBIDA").apply {
                putExtra("title", notification.title)
                putExtra("body", notification.body)
            }
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
        } catch (e: Exception) {
            Log.e("FCM", "Error mostrando mensaje en app: ${e.message}")
            showNotification(notification)
        }
    }

    private fun showNotification(notification: RemoteMessage.Notification) {
        try {
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(
                    "default",
                    "Canal Principal",
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description = "Canal para notificaciones de la aplicación"
                    enableLights(true)
                    lightColor = Color.BLUE
                    enableVibration(true)
                }
                notificationManager.createNotificationChannel(channel)
            }

            // Intent para abrir la app
            val intent = packageManager.getLaunchIntentForPackage(packageName)?.apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            val pendingIntent = PendingIntent.getActivity(
                this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
            )

            val builder = NotificationCompat.Builder(this, "default")
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle(notification.title)
                .setContentText(notification.body)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)

            notificationManager.notify(System.currentTimeMillis().toInt(), builder.build())
        } catch (e: Exception) {
            Log.e("FCM", "Error mostrando notificación: ${e.message}")
        }
    }
}

private fun sendTokenToBackend(username: String, token: String) {
    GlobalScope.launch {
        try {
            val response = sendTokenToBack.registerToken(username, token)
            if (response.isSuccessful) {
                val apiResponse = response.body()
                apiResponse?.let {
                    Log.d("FCM", "Token ${if (it.status == 200) "registrado" else "actualizado"}: ${it.message}")
                }
            } else {
                Log.e("FCM", "Error al registrar el token: ${response.errorBody()?.string()}")
            }
        } catch (e: Exception) {
            Log.e("FCM", "Error de red: ${e.message}", e)
        }
    }
}

fun getFCMToken(username: String) {
    FirebaseMessaging.getInstance().token
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val token = task.result
                Log.d("FCM", "Token obtenido: $token")
                sendTokenToBackend(username, token)
            } else {
                Log.e("FCM", "Error al obtener el token", task.exception)
            }
        }
        .addOnFailureListener { e ->
            Log.e("FCM", "Error al obtener el token", e)
        }

    FirebaseMessaging.getInstance().setAutoInitEnabled(true)
}