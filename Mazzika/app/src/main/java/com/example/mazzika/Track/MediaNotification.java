package com.example.mazzika.Track;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.session.MediaSession;
import android.os.Build;
import android.support.v4.media.session.MediaSessionCompat;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.mazzika.R;
import com.example.mazzika.Services.NotificationReceiver;
import com.example.mazzika.Track.Song;

public class MediaNotification {

    public static final String CHANNEL_ID = "Channel 1";
    public static final String CHANNEL_NAME = "Media Player Channel";
    public static final String ACTION_PREVIOUS = "actionPrevious";
    public static final String ACTION_NEXT = "actionNext";
    public static final String ACTION_PLAY = "actionPlay";
    public static final String SESSION_TAG = "sessionTag";
    public static final int NOTIFICATION_ID = 1;

    public static Notification notification;

    public static void createNotification(Context context, Song song, int playButton) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
            MediaSessionCompat mediaSessionCompat = new MediaSessionCompat(context, SESSION_TAG);

            PendingIntent pendingIntentPrevious;
            Intent intentPrevious = new Intent(context, NotificationReceiver.class)
                    .setAction(ACTION_PREVIOUS);
            pendingIntentPrevious = PendingIntent.getBroadcast(context, 0,
                    intentPrevious, PendingIntent.FLAG_UPDATE_CURRENT);

            Intent intentPlay = new Intent(context, NotificationReceiver.class)
                    .setAction(ACTION_PLAY);
            PendingIntent pendingIntentPlay = PendingIntent.getBroadcast(context, 0,
                    intentPlay, PendingIntent.FLAG_UPDATE_CURRENT);
            int playDrawable = (playButton == R.drawable.ic_pause_arrow) ?
                    R.drawable.ic_pause_arrow : R.drawable.ic_play_arrow;

            PendingIntent pendingIntentNext;
            Intent intentNext = new Intent(context, NotificationReceiver.class)
                    .setAction(ACTION_NEXT);
            pendingIntentNext = PendingIntent.getBroadcast(context, 0,
                    intentNext, PendingIntent.FLAG_UPDATE_CURRENT);

            // CREATE THE NOTIFICATION
            Bitmap mediaBackgroundIcon = BitmapFactory.
                    decodeResource(context.getResources(), R.drawable.album_poster_2);
            notification = new NotificationCompat.Builder(context, CHANNEL_ID)
                    .setSmallIcon(R.mipmap.ic_launcher) // mandatory
                    .setContentTitle(song.getName())
                    .setContentText(song.getAlbum())
                    .setLargeIcon(mediaBackgroundIcon) // optional
                    .setOnlyAlertOnce(true) // show notification for only first time
                    .setShowWhen(false)
                    .addAction(R.drawable.ic_skip_previous_arrow, "Previous", pendingIntentPrevious)
                    .addAction(playDrawable, "Play", pendingIntentPlay)
                    .addAction(R.drawable.ic_skip_next_arrow, "Next", pendingIntentNext)
                    .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                            .setShowActionsInCompactView(0, 1, 2)
                            .setMediaSession(mediaSessionCompat.getSessionToken()))
                    .setPriority(NotificationCompat.PRIORITY_LOW)
                    .build();
            notificationManagerCompat.notify(NOTIFICATION_ID, notification);
        }
    }
}
