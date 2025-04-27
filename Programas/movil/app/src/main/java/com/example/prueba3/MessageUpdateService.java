package com.example.prueba3;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

public class MessageUpdateService extends Service {
    private static final String TAG = "MessageUpdateService";
    public static final String ACTION_UPDATE_MESSAGES = "com.example.prueba3.UPDATE_MESSAGES";

    static final int NOTIFICATION_ID = 1;
    static final String CHANNEL_ID = "message_update_channel"; // Más descriptivo

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @SuppressLint("ForegroundServiceType")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null) {
            Log.e(TAG, "Intent is null in onStartCommand");
            return START_NOT_STICKY;
        }

        String remitente = intent.getStringExtra("remitente");
        String destinatario = intent.getStringExtra("destinatario");

        if (remitente == null || destinatario == null) {
            Log.e(TAG, "Remitente or destinatario is null");
            return START_NOT_STICKY;
        }

        // Crear el canal de notificación si la versión de Android es Oreo o superior
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel();
        }

        // Crear y mostrar la notificación de primer plano (incluyendo la categoría)
        Notification notification = createForegroundNotification();
        startForeground(NOTIFICATION_ID, notification);

        // Enviar un Broadcast para notificar la actualización de mensajes
        Intent broadcastIntent = new Intent(ACTION_UPDATE_MESSAGES);
        broadcastIntent.putExtra("remitente", remitente);
        broadcastIntent.putExtra("destinatario", destinatario);
        sendBroadcast(broadcastIntent);

        // Detener el servicio después de enviar el Broadcast
        stopSelf();
        return START_NOT_STICKY;
    }

    private Notification createForegroundNotification() {
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Actualizando mensajes")
                .setContentText("Obteniendo últimos mensajes...")
                .setSmallIcon(R.drawable.ic_launcher_foreground) // Reemplaza con tu icono adecuado
                .setContentIntent(pendingIntent)
                .setCategory(NotificationCompat.CATEGORY_SERVICE) // Establece la categoría de servicio
                .setOngoing(true); // Indica que es un servicio en primer plano

        return builder.build();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createNotificationChannel() {
        CharSequence name = "Actualización de Mensajes"; // Nombre más descriptivo
        String description = "Canal para notificaciones de actualización de mensajes en tiempo real."; // Descripción más clara
        int importance = NotificationManager.IMPORTANCE_HIGH; // Considera la importancia según la urgencia
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
        channel.setDescription(description);
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);
    }
}