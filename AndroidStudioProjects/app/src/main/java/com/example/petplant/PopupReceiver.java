package com.example.petplant;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class PopupReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // Show the popup layout
        Intent popupIntent = new Intent(context, PopupActivity.class);
        popupIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(popupIntent);
    }
}
