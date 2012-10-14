package com.emmett.autobrightness;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.util.Log;
import com.emmett.mytest.R;


public class AutoScreenReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("AutoScreen", "Received: " + intent.getAction());
        Settings.System.putInt(context.getContentResolver(),Settings.System.SCREEN_BRIGHTNESS_MODE,
                Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC);
        Intent main = new Intent(context, MainActivity.class);
        main.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(main);
    }
}
