package com.example.mazzika.Services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class NotificationReceiver extends BroadcastReceiver {
    public static final String INTENT_ACTION_NAME = "TRACKS_ACTION";
    public static final String BROADCAST_ACTION_NAME = "broadcastAction";
    @Override
    public void onReceive(Context context, Intent intent) {
        context.sendBroadcast(new Intent(INTENT_ACTION_NAME)
                .putExtra(BROADCAST_ACTION_NAME, intent.getAction()));
    }
}
