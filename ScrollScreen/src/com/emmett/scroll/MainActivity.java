package com.emmett.scroll;

import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;

public class MainActivity extends Activity implements OnClickListener {

    private Button mScrollLeftBtn;
    private Button mScrollRightBtn;
    private MultiViewGroup mMulTiViewGroup  ;

    public static int sScreenWidth  ;  // 屏幕宽度
    public static int sScrrenHeight ;  //屏幕高度

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //获得屏幕分辨率大小
        DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);
        sScreenWidth = metric.widthPixels ;
        sScrrenHeight = metric.heightPixels;

        setContentView(R.layout.multiview);
 
        mMulTiViewGroup = (MultiViewGroup)findViewById(R.id.mymultiViewGroup);

        mScrollLeftBtn = (Button) findViewById(R.id.bt_scrollLeft);
        mScrollRightBtn = (Button) findViewById(R.id.bt_scrollRight);

        mScrollLeftBtn.setOnClickListener(this);
        mScrollRightBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.bt_scrollLeft:
            mMulTiViewGroup.startMove() ;
            break;
        case R.id.bt_scrollRight:
            mMulTiViewGroup.stopMove() ;
            break;
        }
    }

}
