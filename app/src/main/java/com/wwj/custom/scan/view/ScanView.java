package com.wwj.custom.scan.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * Created by Administrator on 2017/4/25.
 */

public class ScanView extends View {

    private Paint mPaintCircleOrLine, mPaintArc1, mPaintArc2, mPaintLine;
    private float mWidth, mHeight;
    private RectF mRectF;
    private Matrix matrix;
    //旋转效果起始角度
    private int start = 0;
    private boolean threadRunning = false;
    private ScanThread mThread;

    public ScanView(Context context) {
        super(context);
        initPaint();
    }

    public ScanView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initPaint();
    }

    private void initPaint() {
        mPaintCircleOrLine = new Paint();
        mPaintCircleOrLine.setStrokeWidth(2);
        mPaintCircleOrLine.setAntiAlias(true);
        mPaintCircleOrLine.setStyle(Paint.Style.STROKE);
        mPaintCircleOrLine.setColor(getResources().getColor(R.color.colorCircleOrLine));

        mPaintArc1 = new Paint();
        mPaintArc1.setStyle(Paint.Style.FILL);
        mPaintArc1.setAntiAlias(true);

        mPaintArc2 = new Paint();
        mPaintArc2.setStrokeWidth(4);
        mPaintArc2.setStyle(Paint.Style.STROKE);
        mPaintArc2.setAntiAlias(true);
        mPaintArc2.setColor(getResources().getColor(R.color.colorArcBorder));

        mPaintLine = new Paint();
        mPaintLine.setStrokeWidth(4);
        mPaintLine.setStyle(Paint.Style.STROKE);
        mPaintLine.setAntiAlias(true);
        mPaintLine.setColor(getResources().getColor(R.color.colorLine));

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        mWidth = getWidth();
        mHeight = getHeight();
        mRectF = new RectF(getPaddingLeft(), getPaddingTop(), mWidth-getPaddingRight(), mHeight-getPaddingBottom());
        SweepGradient sweepGradient1 = new SweepGradient(mWidth / 2, mHeight / 2,
                new int[]{Color.TRANSPARENT, getResources().getColor(R.color.colorArc)}, new float[]{0.8f, 1f});
        mPaintArc1.setShader(sweepGradient1);

        SweepGradient sweepGradient2 = new SweepGradient(mWidth / 2, mHeight / 2,
                Color.TRANSPARENT, getResources().getColor(R.color.colorArcBorder));
        mPaintArc2.setShader(sweepGradient2);
        Log.i("wang", String.format("width:%s, height:%s", mWidth, mHeight));

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Log.i("ScanView", "onDraw()");
        canvasCircle(canvas);
        canvasLine(canvas);
        //根据matrix中设定角度，不断绘制shader,呈现出一种扇形扫描效果
        if(threadRunning) {
            canvas.concat(matrix);
            canvasArc(canvas);
        }
    }

    private void canvasCircle(Canvas canvas) {
        canvas.drawCircle(mWidth / 2, mHeight / 2, (mWidth - getPaddingLeft() - getPaddingRight()) / 6, mPaintCircleOrLine);
        canvas.drawCircle(mWidth / 2, mHeight / 2, (mWidth - getPaddingLeft() - getPaddingRight()) / 3, mPaintCircleOrLine);
        canvas.drawCircle(mWidth / 2, mHeight / 2, (mWidth - getPaddingLeft() - getPaddingRight()) / 2, mPaintCircleOrLine);
    }

    private void canvasLine(Canvas canvas) {
        canvas.drawLine(getPaddingLeft(), mHeight/2, mWidth - getPaddingRight(), mHeight/2, mPaintCircleOrLine);
        canvas.drawLine(mWidth/2, getPaddingTop(), mWidth/2, mHeight - getPaddingBottom(), mPaintCircleOrLine);
    }

    private void canvasArc(Canvas canvas) {
        canvas.drawArc(mRectF, 0, 180, true, mPaintArc1);
        canvas.drawArc(mRectF, 0, 180, false, mPaintArc2);
        canvas.drawLine(getPaddingLeft()-2, mHeight/2, mWidth/2, mHeight/2, mPaintLine);
    }

    public void start() {
        mThread = new ScanThread(this);
        mThread.start();
        threadRunning = true;
    }

    public void stop() {
        threadRunning = false;
        invalidate();
    }

    protected class ScanThread extends Thread {

        private ScanView view;

        public ScanThread(ScanView view) {
            // TODO Auto-generated constructor stub
            this.view = view;
        }

        @Override
        public void run() {
            Log.i("ScanView", "run()");
            // TODO Auto-generated method stub
            while (threadRunning) {
                view.post(new Runnable() {
                    public void run() {
                        start = start + 1;
                        matrix = new Matrix();
                        matrix.preRotate(start, mWidth/2, mWidth/2);
                        view.invalidate();

                    }
                });
                try {
                    Thread.sleep(5);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
