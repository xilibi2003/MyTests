package com.emmett.view;

import com.emmett.image3d.R;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.hardware.SensorManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.AnimationUtils;


public class FlipImgView extends View {

    private Context context ;
    private Bitmap showBmp ;
    private Matrix matrix ; //作用矩阵
    private Camera camera ;
    private int deltaX , deltaY ; //翻转角度差值
    private int centerX , centerY ; //图片中心点
    private int deltaBaseX;

    private VelocityTracker mVelocityTracker;
    private int mMinimumVelocity;
    private int mMaximumVelocity;
    private final float mDeceleration;

    private int mTouchSlop;
    int mStartX ;
    int mStartY ;
    int mSrollX;
    int mSrollY;
    int mLastX;
    int mLastY;

    float mVelocityX;
    int mDuration;
    private long mStartTime;
    private boolean mIsFling = false;

    public FlipImgView(Context context) {
        super(context);
        this.context = context ;
        initData();
        float ppi = context.getResources().getDisplayMetrics().density * 160.0f;
        mDeceleration = SensorManager.GRAVITY_EARTH   // g (m/s^2)
                * 39.37f                        // inch/meter
                * ppi                           // pixels per inch
                * ViewConfiguration.getScrollFriction() * 2.5f;   // ＊2.5 by my self 加快减速速度.
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

        mMinimumVelocity = config.getScaledMinimumFlingVelocity();
        mMaximumVelocity = config.getScaledMaximumFlingVelocity();

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();

        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(event);

        int x = (int) event.getX();
        int y = (int) event.getY();
        switch(action) {
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

         case MotionEvent.ACTION_UP:
             mVelocityTracker.computeCurrentVelocity(1000, mMaximumVelocity);
             int xVelocity = (int) mVelocityTracker.getXVelocity();
             if(Math.abs(xVelocity) > mMinimumVelocity ) {
                 // compute the degree by velocity.
                 fling(xVelocity);
             }
         }

        return true;
    }

    public void fling(int velocityX) {
        mIsFling = true;
        deltaBaseX = deltaX;
        mVelocityX = velocityX;
        mDuration = (int) (1000 * velocityX / mDeceleration); // Duration is in milliseconds
        Log.d("xlb", "mDuration: " + mDuration);
        mStartTime = AnimationUtils.currentAnimationTimeMillis();
//        totalDistance = (int) ((mVelocityX * mVelocityX) / (2 * mDeceleration));
//        if(mVelocityX < 0) {
//            totalDistance = -totalDistance;
//        }
        invalidate();
    }

    private void updateAnimation() {
        Log.d("xlb", "updateAnimation: ");
        final long timePassed = AnimationUtils.currentAnimationTimeMillis() - mStartTime;
        if (timePassed >= mDuration) {
            mIsFling = false;
            return;
        }
        // update deltaX   应该是以某种程度的衰减。
        deltaX =  deltaX + (int)(deltaBaseX * 0.05);
        invalidate();
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

        if(mIsFling) {
            updateAnimation();
        }
    }

}
