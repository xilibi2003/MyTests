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

    private static final int MAX_DEGREE_ONCE = 30;
    private static final int NEGATIVE_MAX_DEGREE_ONCE = -30;

    private static final int FULL_CIRCLE_DEGREE      = 360;
    private static final int HALF_CIRCLE_DEGREE      = 180;
    private static final int QUARTER_CIRCLE_DEGREE   = 90;

    private Context context ;
    private Bitmap showBmp ;
    private Matrix matrix ; //作用矩阵
    private Camera camera ;
    private int deltaX , deltaY ; //翻转角度差值
    private int centerX , centerY ; //图片中心点

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
    private boolean mIsRollBack = false;
    private long timePassed;
    private int passDegree = 0;
    private boolean mRollBackDirection;     // true   继续向前      false   向后回走
    private boolean mIsFront;

    public FlipImgView(Context context) {
        super(context);
        this.context = context ;
        float ppi = context.getResources().getDisplayMetrics().density * 160.0f;
        mDeceleration = SensorManager.GRAVITY_EARTH   // g (m/s^2)
                * 39.37f                        // inch/meter
                * ppi                           // pixels per inch
                * ViewConfiguration.getScrollFriction() * 3f;
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

        mMinimumVelocity = config.getScaledMinimumFlingVelocity();
        mMaximumVelocity = config.getScaledMaximumFlingVelocity();
        Log.d("xlb", "mTouchSlop: " + mTouchSlop + ", mMaximumVelocity:" + mMaximumVelocity + ", mMinimumVelocity:" + mMinimumVelocity + ", mDeceleration:" + mDeceleration);

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
                 Log.d("xlb", "mIsFront " + mIsFront + ", mSrollX : " + mSrollX );
                 if(!mIsFront) {
                         x += HALF_CIRCLE_DEGREE; 
                 }
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
             mVelocityTracker.computeCurrentVelocity(500, mMaximumVelocity);
             int xVelocity = (int) mVelocityTracker.getXVelocity();
             if(Math.abs(xVelocity) > mMinimumVelocity ) {
                 // compute the degree by velocity.
                 fling(xVelocity);
             } else {
                 rollback();
             }
         }

        return true;
    }

    public void fling(int velocityX) {
        mIsFling = true;
        mVelocityX = velocityX;
        passDegree = 0;
        mDuration = (int) (1000 * velocityX / mDeceleration); // Duration is in milliseconds   减速到0所要的时间  v=gt ;  t = v/g
        mDuration = Math.abs(mDuration);
        Log.d("xlb", "mDuration: " + mDuration + " , mVelocityX: " + mVelocityX);
        mStartTime = AnimationUtils.currentAnimationTimeMillis();
        invalidate();
    }

    private void updateAnimation() {
        timePassed = AnimationUtils.currentAnimationTimeMillis() - mStartTime;

        if (timePassed >= mDuration) {
                mIsFling = false;
                rollback();
            return;
        } 

        float timeSec = timePassed / 1000f;
        int passedDegree = 0;
        if(mVelocityX >= 0) {
            passedDegree = (int)((mVelocityX * timeSec) - ((mDeceleration * timeSec * timeSec) / 2));   // s = vt - (0.5* gt^2)   以重力加速度逐减产生的位移 当为角度
        } else {
            passedDegree = (int)((mVelocityX * timeSec) + ((mDeceleration * timeSec * timeSec) / 2));   // s = vt - (0.5* gt^2)   以重力加速度逐减产生的位移 当为角度
        }
        Log.d("xlb", "timeSec " + timeSec +  ",  passedDegree: " + passedDegree);
        int diffDegree = passedDegree - passDegree;

        if(diffDegree > MAX_DEGREE_ONCE) {
            diffDegree = MAX_DEGREE_ONCE;
        } else if(diffDegree < NEGATIVE_MAX_DEGREE_ONCE) {
            diffDegree = NEGATIVE_MAX_DEGREE_ONCE;
        }

        // update deltaX   应该是以某种程度的衰减。
        deltaX =  deltaX + (diffDegree);
        Log.d("xlb", "updateAnimation  deltaX " + deltaX + ", diffDegree: " + diffDegree);

        passDegree = passedDegree;

        invalidate();
    }


    public void rollback() {
        mIsRollBack = true;
        int left = (deltaX % QUARTER_CIRCLE_DEGREE);
        if(deltaX % HALF_CIRCLE_DEGREE >= QUARTER_CIRCLE_DEGREE) {
            left = QUARTER_CIRCLE_DEGREE - left;
            mRollBackDirection = true;
        } else {
            mRollBackDirection = false;
        }

        passDegree = 0;
        mDuration = (int) (1000 * Math.sqrt((2*Math.abs(left)) / mDeceleration)); //    相当于从left掉落要花的时间  x=0.5 * g * t^2 求t
        mVelocityX = 0;
        Log.d("xlb", "rollback mDuration: " + mDuration + " , left: " + left);
        mStartTime = AnimationUtils.currentAnimationTimeMillis();
        invalidate();
    }

    private void updateRollbackAnimation() {
        timePassed = AnimationUtils.currentAnimationTimeMillis() - mStartTime;

        if (timePassed >= mDuration) {
            mIsRollBack = false;

            int left = deltaX % HALF_CIRCLE_DEGREE ;
            if(left < QUARTER_CIRCLE_DEGREE) {
                deltaX = (deltaX / HALF_CIRCLE_DEGREE) * HALF_CIRCLE_DEGREE;
            } else {
                deltaX = (deltaX / HALF_CIRCLE_DEGREE + 1) * HALF_CIRCLE_DEGREE;
            }
            invalidate();
            int  cylinderNumber =  deltaX / HALF_CIRCLE_DEGREE ;
            if(cylinderNumber % 2 == 0) {        // 180 的偶数为 正面
                mIsFront = true;
            } else {
                mIsFront = false;
            }
            Log.d("xlb", "mIsFront : " + mIsFront);
            return ;
        }

        float timeSec = timePassed / 1000f;
        int passedDegree =  (int) (0 - ((mDeceleration * timeSec * timeSec) / 2));
        if(deltaX < 0) {
            passedDegree = -passedDegree;
        }


        Log.d("xlb", "timeSec " + timeSec +  ",  passedDegree: " + passedDegree);
        int diffDegree = passedDegree - passDegree;

        if(mRollBackDirection) {
            diffDegree = -diffDegree;
        }

        if(diffDegree > MAX_DEGREE_ONCE) {
            diffDegree = MAX_DEGREE_ONCE;
        } else if(diffDegree < NEGATIVE_MAX_DEGREE_ONCE) {
            diffDegree = NEGATIVE_MAX_DEGREE_ONCE;
        }

        // update deltaX   应该是以某种程度的衰减。
        deltaX =  deltaX + (diffDegree);
        Log.d("xlb", "updateRollbackAnimation  deltaX " + deltaX + ", diffDegree: " + diffDegree);

        passDegree = passedDegree;

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

        if(mIsFling ) {
            updateAnimation();
        } else  if( mIsRollBack) {
            updateRollbackAnimation();
        }
    }

}
