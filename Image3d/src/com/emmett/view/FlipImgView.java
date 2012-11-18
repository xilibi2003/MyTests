package com.emmett.view;

import com.emmett.image3d.R;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;


public class FlipImgView extends View {

    private Context context ;
    private Bitmap showBmp ;
    private Matrix matrix ; //作用矩阵
    private Camera camera ;
    private int deltaX , deltaY ; //翻转角度差值
    private int centerX , centerY ; //图片中心点

    private int mTouchSlop;
    int mStartX ;
    int mStartY ;
    int mSrollX;
    int mSrollY;
    int mLastX;
    int mLastY;

    public FlipImgView(Context context) {
        super(context);
        this.context = context ;
        initData();
    }

    private void initData(){
        showBmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.per);
        centerX = showBmp.getWidth()/2 ;
        centerY = showBmp.getHeight()/2 ;
        matrix = new Matrix();
        camera = new Camera();

        final ViewConfiguration config = ViewConfiguration.get(super.getContext());
        mTouchSlop = config.getScaledTouchSlop();
        Log.d("xlb", "mTouchSlop: " + mTouchSlop);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        int x = (int) event.getX();
        int y = (int) event.getY();
        switch(event.getAction()) {
         case MotionEvent.ACTION_DOWN:
             mStartX = x ;
             mLastX = mStartX;
             mStartY = y ;
             mLastY = mStartY;
             break;
         case MotionEvent.ACTION_MOVE:
             int dx = x - mLastX ;
             if(dx > mTouchSlop || dx < -mTouchSlop) {
                 mSrollX = x - mStartX;
                 deltaX = mSrollX;
                 mLastX = x;
                 invalidate();
             }

             int dy = y - mLastY ;
             if(dy > mTouchSlop || dy < -mTouchSlop) {
                 mSrollY = y - mStartY;
                 deltaY += mSrollY ;
                 mLastY = y;
                 invalidate();
             }
             break;
         }

        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Log.d("xlb", "deltaX: " + deltaX);

        camera.save();
        //绕X轴翻转
//        camera.rotateX(-deltaY);
        //绕Y轴翻转
        camera.rotateY(deltaX);
        //设置camera作用矩阵
        camera.getMatrix(matrix);
        camera.restore();
        //设置翻转中心点
        matrix.preTranslate(-this.centerX, -this.centerY);
        matrix.postTranslate(this.centerX, this.centerY);

        canvas.drawBitmap(showBmp, matrix, null);
    }

}
