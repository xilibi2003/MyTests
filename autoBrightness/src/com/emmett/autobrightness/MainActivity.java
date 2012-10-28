package com.emmett.autobrightness;

import com.emmett.mytest.R;

import android.app.Activity;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.TextView;


public class MainActivity extends Activity {

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TextView tv = new TextView(this);
        tv.setText(R.string.app_uninstall);
        tv.setTextSize(22);
        setContentView(tv);
        Settings.System.putInt(getContentResolver(),Settings.System.SCREEN_BRIGHTNESS_MODE,
                Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC);
    }
}
