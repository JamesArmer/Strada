package com.example.strada;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class LocalBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("g53mdp", "LocalBroadcastReceiver onReceive");
        Toast.makeText(context, "Location Service Started", Toast.LENGTH_LONG).show(); //Let the user know that location is being tracked

        context.startService(new Intent(context, LocationService.class));//start service from the broadcast receiver
    }
}
