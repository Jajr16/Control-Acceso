package com.example.prueba3;

import android.app.ActivityManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import androidx.core.app.NotificationCompat;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import java.util.List;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "MyFirebaseMessagingService";
    private static final String CHANNEL_ID = "my_channel_id";
    private static final int NOTIFICATION_ID = 1;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d(TAG, "onMessageReceived llamado");
        FirebaseCrashlytics.getInstance().log("FCMService: onMessageReceived llamado");
        Log.d(TAG, "FCM Data: " + remoteMessage.getData());
        FirebaseCrashlytics.getInstance().log("FCMService: FCM Data = " + remoteMessage.getData());

        String remitente = remoteMessage.getData().get("sender");
        String destinatario = remoteMessage.getData().get("destinatario");
        String mensaje = remoteMessage.getData().get("message");

        FirebaseCrashlytics.getInstance().log("FCMService: Remitente = " + remitente);
        FirebaseCrashlytics.getInstance().log("FCMService: Destinatario = " + destinatario);
        FirebaseCrashlytics.getInstance().log("FCMService: Mensaje = " + mensaje);

        if (remitente == null || destinatario == null) {
            Log.e(TAG, "Remitente o destinatario nulos en el mensaje FCM");
            FirebaseCrashlytics.getInstance().log("FCMService: Error - Remitente o destinatario nulos en el mensaje FCM");
            return;
        }

        if (isAppInForeground()) {
            // Si la app está en primer plano, iniciar el servicio
            Log.d(TAG, "App en primer plano, iniciando servicio");
            FirebaseCrashlytics.getInstance().log("FCMService: App en primer plano, iniciando MessageUpdateService");
            Intent serviceIntent = new Intent(this, MessageUpdateService.class);
            serviceIntent.putExtra("remitente", remitente);
            serviceIntent.putExtra("destinatario", destinatario);

            FirebaseCrashlytics.getInstance().log("FCMService: Iniciando servicio con remitente = " + remitente + ", destinatario = " + destinatario);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(serviceIntent);
            } else {
                startService(serviceIntent);
            }
        } else {
            // Si la app está en segundo plano, mostrar notificación
            Log.d(TAG, "App en segundo plano, mostrando notificación");
            FirebaseCrashlytics.getInstance().log("FCMService: App en segundo plano, mostrando notificación");
            if (remoteMessage.getNotification() != null) {
                String title = remoteMessage.getNotification().getTitle();
                String body = remoteMessage.getNotification().getBody();
                FirebaseCrashlytics.getInstance().log("FCMService: Notificación - Título = " + title + ", Cuerpo = " + body);
                showNotification(title, body);
            }
        }
    }

    private boolean isAppInForeground() {
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        if (activityManager == null) return false;

        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        if (appProcesses == null) return false;

        String packageName = getPackageName();
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND
                    && appProcess.processName.equals(packageName)) {
                return true;
            }
        }
        return false;
    }

    private void showNotification(String title, String body) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this,
                0,
                intent,
                PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT
        );

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(body)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel();
        }

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build());
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Mi Canal";
            String description = "Canal para notificaciones importantes";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    @Override
    public void onNewToken(String token) {
        super.onNewToken(token);
        Log.d(TAG, "Nuevo token FCM: " + token);
        FirebaseCrashlytics.getInstance().log("FCMService: Nuevo token FCM = " + token);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseCrashlytics.getInstance().log("FCMService: Servicio creado");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        FirebaseCrashlytics.getInstance().log("FCMService: Servicio destruido");
    }
}