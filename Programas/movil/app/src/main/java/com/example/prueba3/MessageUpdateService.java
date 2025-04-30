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
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.firebase.crashlytics.FirebaseCrashlytics;

public class MessageUpdateService extends Service {
    private static final String TAG = "MessageUpdateService";
    public static final String ACTION_UPDATE_MESSAGES = "com.example.prueba3.ACTION_UPDATE_MESSAGES";

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
        FirebaseCrashlytics.getInstance().log("UpdateService: onStartCommand llamado con acción = " + intent.getAction());

        if (intent == null) {
            Log.e(TAG, "Intent is null in onStartCommand");
            FirebaseCrashlytics.getInstance().log("UpdateService: Error - Intent es nulo en onStartCommand");
            return START_NOT_STICKY;
        }

        String remitente = intent.getStringExtra("remitente");
        String destinatario = intent.getStringExtra("destinatario");

        Log.d(TAG, "UpdateService: Remitente (Intent) = " + remitente);
        Log.d(TAG, "UpdateService: Destinatario (Intent) = " + destinatario);
        FirebaseCrashlytics.getInstance().log("UpdateService: Remitente (Intent) = " + remitente);
        FirebaseCrashlytics.getInstance().log("UpdateService: Destinatario (Intent) = " + destinatario);

        if (remitente == null || destinatario == null) {
            Log.e(TAG, "Remitente or destinatario is null");
            FirebaseCrashlytics.getInstance().log("UpdateService: Error - Remitente o destinatario es nulo en onStartCommand");
            return START_NOT_STICKY;
        }

        // No necesitamos la notificación de primer plano para la comunicación interna
        // si solo estamos usando LocalBroadcastManager para actualizar la UI.
        // Si aún quieres la notificación (por ejemplo, para mantener el servicio vivo),
        // puedes dejar esta parte.

        // Crear el canal de notificación si la versión de Android es Oreo o superior
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel();
        }

        // Crear y mostrar la notificación de primer plano (incluyendo la categoría)
        Notification notification = createForegroundNotification();
        startForeground(NOTIFICATION_ID, notification);

        // Enviar un Broadcast LOCAL para notificar la actualización de mensajes
        Intent broadcastIntent = new Intent(ACTION_UPDATE_MESSAGES);
        broadcastIntent.putExtra("remitente", remitente);
        broadcastIntent.putExtra("destinatario", destinatario);
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(broadcastIntent);
        Log.d(TAG, "UpdateService: Broadcast LOCAL enviado con remitente = " + remitente + ", destinatario = " + destinatario);
        FirebaseCrashlytics.getInstance().log("UpdateService: Broadcast LOCAL enviado con remitente = " + remitente + ", destinatario = " + destinatario);

        // Detener el servicio después de enviar el Broadcast
        stopSelf();
        FirebaseCrashlytics.getInstance().log("UpdateService: Servicio detenido");
        Log.d(TAG, "UpdateService: Servicio detenido");
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

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "UpdateService: Servicio creado");
        FirebaseCrashlytics.getInstance().log("UpdateService: Servicio creado");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "UpdateService: Servicio destruido");
        FirebaseCrashlytics.getInstance().log("UpdateService: Servicio destruido");
    }
}