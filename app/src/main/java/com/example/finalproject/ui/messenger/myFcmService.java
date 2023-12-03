package com.example.finalproject.ui.messenger;


import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.example.finalproject.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Random;

public class myFcmService extends FirebaseMessagingService {


    private static final String TAG = "FCM_SERVICE_TAG";

    private static final String ADMIN_CHANNEL_ID = "ADMIN_CHANNEL_ID";


    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        String title = "" + remoteMessage.getNotification().getTitle();

        String body = "" + remoteMessage.getNotification().getBody();

        String senderUid = "" + remoteMessage.getData().get("senderUid");

        String notificationType = "" + remoteMessage.getData().get("notificationType");


        Log.d(TAG, "onMessageReceived: title: " + title);
        Log.d(TAG, "onMessageReceived: body: " + body);
        Log.d(TAG, "onMessageReceived: senderUid: " + senderUid);
        Log.d(TAG, "onMessageReceived: notificationType " + notificationType);
        showChatNotification(title,body,senderUid);
    }

    private void showChatNotification(String title, String body, String senderUid) {

        int notificationId = new Random().nextInt(  3000);

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE) ;

        setupNotificationChannel (notificationManager) ;



        Intent intent = new Intent( this, chatActivity.class);
        intent. putExtra("ownerUid", senderUid);
        intent .addFlags (Intent. FLAG_ACTIVITY_CLEAR_TOP) ;

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 6, intent, PendingIntent.FLAG_IMMUTABLE) ;
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, ""+ADMIN_CHANNEL_ID)
                .setSmallIcon(R. drawable.logo)
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel (true)
                .setPriority(NotificationCompat .PRIORITY_HIGH)
                        .setContentIntent(pendingIntent);

        notificationManager.notify(notificationId, notificationBuilder.build());


    }

    @SuppressLint("NewApi")
    private void setupNotificationChannel(NotificationManager notificationManager) {
//Starting in Android 8.6 (API level 26), all notifications must be  assigned to o channel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel notificationChannel = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                notificationChannel = new NotificationChannel(
                        ADMIN_CHANNEL_ID,
                        "CHAT_CHANNEL",
                        NotificationManager.IMPORTANCE_HIGH
                );
            }


            notificationChannel.setDescription("Show Chat Notifications.");
            notificationChannel.enableVibration(true);
            notificationManager.createNotificationChannel(notificationChannel);
        }


    }
}


