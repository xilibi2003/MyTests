package com.emmett.surface;
 
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.DisplayMetrics;
 
/**
 * 球类
 * @author zcl
 *
 */
public class Ball {
          
         /**
          * 球的高
          */
         public static final int HEIGHT = 72;
         /**
          * 球的宽
          */
         public static final int WIDTH = 72;
         private static final int STEPLENGTH = 10;//每次运动的间距
         private static final float REDUCEPERCENTAGE =  0.35F;//递减系数
         private int stepReduce ;//每次反向运动的缩短的距离
        
         private float runX ;//球的位置
         private float runY ;//球的位置
         private boolean upDirection = false;//if true,up direction,or is down direction
         private float maxHeight ;//当前运动最高的高度
         private Paint paint ;
        
         Bitmap ballBitmap ;//球的图片
         SportActivity sa ;
         public Ball(float initX , float initY , BallSurfaceView bsv){
                   this.runX  = initX;
                   this.runY = initY ;
                   maxHeight = initY;
                   this.bsv = bsv;
                   ballBitmap = BitmapFactory.decodeResource(bsv.getResources(), R.drawable.ball);//加载图片
                   paint = new Paint();
                   sa = bsv.sportActivity;
         }
        
         public void onDraw(Canvas canvas) {
                   int c = paint.getColor();//保存颜色，之后还原为之前颜色
                   boundaryTest();
                   if(canvas != null) {
                       canvas.drawBitmap(ballBitmap,runX,runY,paint);
                   }
                   paint.setColor(c);
                   move();
         }
         /**
          * 运动
          */
         private void move() {
                   if(maxHeight >= (sa.screenHeight - HEIGHT)) {
                            return;
                   }
                   if(upDirection){//向上
                            runY = runY + STEPLENGTH ;
                   }else{
                            runY = runY - STEPLENGTH ;
                   }
         }
 
         /**
          * 边界检测，使球不会飞出边界
          */
         private void boundaryTest(){
 
                   if(runY > sa.screenHeight - HEIGHT){//向下运动到头
                            upDirection = !upDirection;//方向置反
                            runY = sa.screenHeight - HEIGHT;
                            stepReduce = (int) (maxHeight * REDUCEPERCENTAGE);
                            maxHeight = maxHeight + stepReduce ;//最大高度递减
                           
                   }
                   if(runY < maxHeight ){//向上运动到头
                            upDirection = !upDirection;//方向置反
                            if(maxHeight >= (sa.screenHeight - HEIGHT)) return;
                            runY = maxHeight ;
                           
                   }
         }
}