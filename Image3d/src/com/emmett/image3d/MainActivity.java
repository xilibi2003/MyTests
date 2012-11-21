package com.emmett.image3d;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.emmett.view.FlipImgView;

public class MainActivity extends Activity {

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        setContentView(new FlipImgView(this));

        FlipImgView fv = new FlipImgView(this);
//        fv.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
//                ViewGroup.LayoutParams.WRAP_CONTENT));
        fv.setImageResource(R.drawable.per);
        setContentView(fv);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
}
