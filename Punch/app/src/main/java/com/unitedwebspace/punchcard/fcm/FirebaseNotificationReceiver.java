package com.unitedwebspace.punchcard.fcm;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Handler;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import com.unitedwebspace.punchcard.R;
import com.unitedwebspace.punchcard.main.MainActivity;

/**
 * Created by sonback123456 on 4/22/2018.
 */

public class FirebaseNotificationReceiver extends WakefulBroadcastReceiver {

    private static final String TAG = "FirebaseNotificationReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {

        Log.d("notification", intent.getExtras().toString());

        if (intent.getExtras() != null) {
            if (intent.hasExtra("title") & intent.hasExtra("body") & intent.hasExtra("sound"))
                sendNotification(context, intent.getExtras().get("title").toString(), intent.getExtras().get("body").toString(), intent.getExtras().get("sound").toString());
        }
    }

    private void sendNotification(Context context, String messageTitle, String messageBody, String sound) {

        Log.d("Title", messageTitle);
        Log.d("Body", messageBody);
        Log.d("Sound", sound);

        final NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        Intent notificationIntent = new Intent(context, MainActivity.class);
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingNotificationIntent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.noti);

        Notification notification = new Notification.Builder(context)
                .setLargeIcon(bitmap)
                .setSmallIcon(R.drawable.noti)
                .setAutoCancel(true)
                .setFullScreenIntent(pendingNotificationIntent, true)
                .setContentTitle(messageTitle)
                .setContentText(messageBody)
                .setLights(Color.BLUE,1,1)
                .setSound(defaultSoundUri).build();

        notificationManager.cancelAll();
        notificationManager.notify(0, notification);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                notificationManager.cancel(0);
            }
        }, 3000);
    }
}