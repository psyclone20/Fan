package com.psyclone.fan.helpers;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;

import com.psyclone.fan.R;
import com.psyclone.fan.activities.MainActivity;
import java.util.Set;

public class NotificationHelper extends ContextWrapper {
    private NotificationManager manager;
    public static final String CHANNEL_ID = "com.psyclone.fan.myshowsnotification";
    public static final String CHANNEL_NAME = "My shows notification";
    public static final int NOTIFICATION_ID = 2810;

    public NotificationHelper(Context base) {
        super(base);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            createChannels();
    }

    public void showGuideRefreshNotification(Set<String> shows) {
        String notificationTitle = getString(R.string.notif_title);
        String notificationMessage = getString(R.string.notif_no_shows);

        if (shows.size() > 0) {
            notificationMessage = "";
            for (String show : shows)
                notificationMessage += show + ", ";
            notificationMessage = notificationMessage.substring(0, notificationMessage.length() - 2);
        }

        ////////// PendingIntent used to launch MainActivity when the notification is tapped //////////
        Intent launchListActivity = new Intent(this, MainActivity.class);
        launchListActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, launchListActivity, 0);

        ////////// Notification to display on completion //////////
        Notification notification;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Notification.Builder builder = new Notification.Builder(this, CHANNEL_ID)
                    .setContentTitle(notificationTitle)
                    .setContentText(notificationMessage)
                    .setStyle(new Notification.BigTextStyle().bigText(notificationMessage))
                    .setSmallIcon(R.drawable.svg_movie_filter_white_48px)
                    .setColor(ContextCompat.getColor(this, R.color.colorPrimary))
                    .setContentIntent(pendingIntent);
            notification = builder.build();
        } else {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                    .setContentTitle(notificationTitle)
                    .setContentText(notificationMessage)
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(notificationMessage))
                    .setSmallIcon(R.drawable.svg_movie_filter_white_48px)
                    .setColor(ContextCompat.getColor(this, R.color.colorPrimary))
                    .setContentIntent(pendingIntent)
                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                    .setLights(Color.YELLOW, 500, 2000);
            notification = builder.build();
        }
        notification.flags = Notification.FLAG_AUTO_CANCEL; // Notification disappears after it has been used to launch MainActivity
        getManager().notify(NOTIFICATION_ID, notification);
    }

    @TargetApi(Build.VERSION_CODES.O)
    public void createChannels() {
        NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
        notificationChannel.enableLights(true);
        notificationChannel.setLightColor(Color.YELLOW);
        notificationChannel.setShowBadge(true);
        notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        getManager().createNotificationChannel(notificationChannel);
    }

    private NotificationManager getManager() {
        if (manager == null)
            manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        return manager;
    }
}
