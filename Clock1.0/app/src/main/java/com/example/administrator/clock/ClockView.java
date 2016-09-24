package com.example.administrator.clock;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.util.Calendar;


/**
 * Created by Administrator on 2016/9/22.
 */
public class ClockView extends View {

    private int mHeight, mWidth;//屏幕的高度和宽度

    private int mHourColor,mMinColor,mSecondColor;//每个转盘的颜色

    private int mHour,mMin,mSecond;//进度值

    private int mHourWidth,mMinWidth,mSecondWidth;//进度条的宽度
    public ClockView(Context context) {
        this(context, null);
    }

    public ClockView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ClockView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);


        //获取attrs中的属性
        TypedArray a=context.getTheme().obtainStyledAttributes(attrs,R.styleable.ClockView,defStyleAttr,0);
        int n=a.getIndexCount();
        for(int i=0;i<n;i++){
            int attr=a.getIndex(i);
            switch(attr){
                case R.styleable.ClockView_HourColor:
                    mHourColor=a.getColor(attr, Color.BLACK);
                    break;
                case R.styleable.ClockView_MinColor:
                    mMinColor=a.getColor(attr,Color.BLACK);
                    break;
                case R.styleable.ClockView_SecondColor:
                    mSecondColor=a.getColor(attr,Color.BLACK);
                    break;
            }
        }
        a.recycle();

        //开启线程，来获取时间，并进行重绘
        new Thread(){
            @Override
            public void run() {
                //获取系统的时间
             //   Time t=new Time();//报错，不知道为什么
                while (true) {
                    Long time = System.currentTimeMillis();
                    final Calendar mCalendar = Calendar.getInstance();
                    mCalendar.setTimeInMillis(time);
                    mHour = mCalendar.get(Calendar.HOUR_OF_DAY);//获取的是24小时制
               //     Log.i("info",mHour+"");
                    mHour=(int)((mHour*1.0)/(24*1.0)*360);
                    mMin = mCalendar.get(Calendar.MINUTE);
                    mMin=mMin*6;
                    mSecond = mCalendar.get(Calendar.SECOND);
                    mSecond=mSecond*6;
                    postInvalidate();//重绘
                }
            }
        }.start();
    }



    @Override
    protected void onDraw(Canvas canvas) {
        mHourWidth=50;
        mMinWidth=40;
        mSecondWidth=30;

        // 获取宽高参数
        mWidth = getMeasuredWidth();
        mHeight = getMeasuredHeight();
        Paint paintCircle = new Paint();
        paintCircle.setStyle(Paint.Style.STROKE);//画空圆
        paintCircle.setAntiAlias(true);//抗锯齿
        paintCircle.setStrokeWidth(5);//线宽
        //最外圈1
        int rad1=mWidth/2-5;
        canvas.drawCircle(mWidth / 2,
                mHeight / 2, rad1, paintCircle);
        // 画刻度
        Paint painDegree = new Paint();
        paintCircle.setStrokeWidth(3);//线宽

        //秒
        for (int i = 0; i < 60; i++) {
            // 区分整点与非整点
            if (i%5==0) {
                painDegree.setStrokeWidth(5);
                canvas.drawLine(mWidth / 2, mHeight / 2 - rad1,
                        mWidth / 2, mHeight / 2 - rad1 + 60,
                        painDegree);
            } else {
                painDegree.setStrokeWidth(3);
                painDegree.setTextSize(15);
                canvas.drawLine(mWidth / 2, mHeight / 2 - rad1,
                        mWidth / 2, mHeight / 2 - rad1 + 30,
                        painDegree);
                String degree = String.valueOf(i);
                painDegree.setColor(Color.RED);
                canvas.drawText(degree,
                        mWidth / 2 - painDegree.measureText(degree) / 2,
                        mHeight / 2 - rad1 + 50,
                        painDegree);
                painDegree.setColor(Color.BLACK);
            }
            // 旋转画布画线，免得繁琐计算
            canvas.rotate(6, mWidth / 2, mHeight / 2);
        }

        //第二圈,60为之前画表盘消耗的
        canvas.drawCircle(mWidth/2,mHeight/2,rad1-60,paintCircle);
        //第三圈,在二三之间为秒表,其中宽度为mSecondWidth
        canvas.drawCircle(mWidth / 2,
                mHeight / 2, rad1 - mSecondWidth - 60, paintCircle);

        //分
        //第4圈，在三四之间为分表,其中mMinWidth为宽度
        canvas.drawCircle(mWidth / 2,
                mHeight / 2, rad1-mSecondWidth-60-mMinWidth, paintCircle);
        //第5圈,在四五之间为时表，其中mHourWidth为宽度
        canvas.drawCircle(mWidth / 2,
                mHeight / 2, rad1-mSecondWidth-60-mMinWidth-mHourWidth, paintCircle);

        //时盘的半径
        int rad2=rad1-mSecondWidth-60-mMinWidth-mHourWidth;
        //时
        for (int i = 0; i < 24; i++) {

            // 区分整点与非整点
            if ((i)%6==0) {
                painDegree.setStrokeWidth(5);
                painDegree.setTextSize(30);
                canvas.drawLine(mWidth / 2, mHeight / 2 - rad2,
                        mWidth / 2, mHeight / 2 -rad2+60,
                        painDegree);
            } else {
                painDegree.setStrokeWidth(3);
                painDegree.setTextSize(15);
                canvas.drawLine(mWidth / 2, mHeight / 2 - rad2,
                        mWidth / 2, mHeight / 2 - rad2+30,
                        painDegree);
                String degree = String.valueOf(i);
                painDegree.setColor(Color.RED);
                canvas.drawText(degree,
                        mWidth / 2 - painDegree.measureText(degree) / 2,
                        mHeight / 2 - rad2 + 50,
                        painDegree);
                painDegree.setColor(Color.BLACK);
            }
            // 通过旋转画布简化坐标运算
            canvas.rotate(15, mWidth / 2, mHeight / 2);
        }

        //第6圈，五六之间为时表
        canvas.drawCircle(mWidth/2,mHeight/2,rad2-60,paintCircle);

        //画时间转动表
        Paint paint=new Paint();

        //秒
        paint.setStrokeWidth(mSecondWidth-5);
        int radius=rad1-mSecondWidth/2-60;
        paint.setStyle(Paint.Style.STROKE);
        RectF rectF=new RectF(mWidth/2-radius,mHeight/2-radius,mWidth/2+radius,mHeight/2+radius);
        paint.setColor(mSecondColor);
        canvas.drawArc(rectF,270,mSecond,false,paint);

        //分
        paint.setStrokeWidth(mMinWidth-5);
        int radius2=rad1-mSecondWidth-60-mMinWidth/2;
        paint.setStyle(Paint.Style.STROKE);
        RectF rectF2=new RectF(mWidth/2-radius2,mHeight/2-radius2,mWidth/2+radius2,mHeight/2+radius2);
        paint.setColor(mMinColor);
        canvas.drawArc(rectF2,270,mMin,false,paint);

        //时
        paint.setStrokeWidth(mHourWidth-5);
        int radius3=rad1-mSecondWidth-60-mMinWidth-mHourWidth/2;
        paint.setStyle(Paint.Style.STROKE);
        RectF rectF3=new RectF(mWidth/2-radius3,mHeight/2-radius3,mWidth/2+radius3,mHeight/2+radius3);
        paint.setColor(mHourColor);
        canvas.drawArc(rectF3,270,mHour,false,paint);
    }
}
