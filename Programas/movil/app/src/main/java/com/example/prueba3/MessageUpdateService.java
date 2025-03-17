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

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    static final int NOTIFICATION_ID = 1;
    static final String CHANNEL_ID = "my_channel_id";

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

        // Crear y mostrar la notificación
        Notification notification = createNotification();
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

    private Notification createNotification() {
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);

        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Actualizando mensajes")
                .setContentText("Obteniendo últimos mensajes...")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentIntent(pendingIntent)
                .build();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createNotificationChannel() {
        CharSequence name = "Mi Canal";
        String description = "Canal para notificaciones importantes";
        int importance = NotificationManager.IMPORTANCE_HIGH;
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
        channel.setDescription(description);
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);
    }
}