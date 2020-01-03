package com.zz.foldinganimation.foldlayout;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Shader;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.RequiresApi;


public class FoldLayout extends ViewGroup {

    private static final int NUM_OF_POINT = 8;
    private float mTranslateDis;// the foled sum height
    protected float mFactor = 0.0f;// the ratio folded height and old height
    private int mNumOfFolds = 10;//fold count

    private Bitmap mBitmap;
    private Canvas mCanvas = new Canvas();
    private boolean isReady;

    private Matrix[] mMatrices = new Matrix[mNumOfFolds];
    private Paint mSolidPaint;// black area

    private Paint mShadowPaint;// shadow area
    private Matrix mShadowGradientMatrix;
    private LinearGradient mShadowLinearGradient;

    private float mFlodwidth;//  old area each height
    private float mTrannslateDisPerFlod;// folding each area height

    private float mAnchor = 0;

    public FoldLayout(Context context) {
        this(context, null);
    }
    float[] src = new float[NUM_OF_POINT];
    float[] dst = new float[NUM_OF_POINT];

    /**
     * need to  init ,do not depend on width and height such as paint color matric
     *
     */
    public FoldLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        for (int i = 0; i < mNumOfFolds; i++) {
            mMatrices[i] = new Matrix();
        }
        mSolidPaint = new Paint();
        mShadowPaint = new Paint();
        mShadowPaint.setStyle(Paint.Style.FILL);
        mShadowLinearGradient = new LinearGradient(0, 0f, 0.5f, 0, Color.parseColor("#303030"), Color.parseColor("#505050"), Shader.TileMode.CLAMP);
        mShadowPaint.setShader(mShadowLinearGradient);
        mShadowGradientMatrix = new Matrix();

     /*   this.setWillNotDraw(false);*/
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        View child = getChildAt(0);
        measureChild(child, widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(child.getMeasuredWidth(), child.getMeasuredHeight());


    }

    /**
     * the depended var ,set them after onLayout();
     *
     */
    @Override
    protected void onLayout(boolean b, int i, int i1, int i2, int i3) {

        View child = getChildAt(0);
        child.layout(0, 0, getMeasuredWidth(), getMeasuredHeight());
        if (mBitmap==null){
            mBitmap = Bitmap.createBitmap(getMeasuredWidth(), getMeasuredHeight(), Bitmap.Config.ARGB_8888);
            mCanvas.setBitmap(mBitmap);
        }


        updateFold();
        invalidate();

    }

    /**
     * init the data var
     */
    private void updateFold() {
        float  hhhhh= getMeasuredWidth();
        float wwwww = getMeasuredHeight();
        mTranslateDis = wwwww * mFactor;
        mFlodwidth = wwwww / mNumOfFolds;
        mTrannslateDisPerFlod = mTranslateDis / mNumOfFolds;

        int alpha = (int) (255 * (1-mFactor ));
        //mSolidPaint.setAlpha((int) (alpha * 0.4f));
       // mSolidPaint.setColor(Color.parseColor("#303030"));
        mSolidPaint.setColor(Color.argb((int) (alpha * 0.4f), 0, 0, 0));

        mShadowGradientMatrix.setScale(1, mFlodwidth);
        mShadowLinearGradient.setLocalMatrix(mShadowGradientMatrix);
        mShadowPaint.setAlpha(alpha);

       // float depth = (float) (Math.sqrt(mFlodwidth * mFlodwidth - mTrannslateDisPerFlod * mTrannslateDisPerFlod) );
        float depth = (float) (Math.sqrt(mFlodwidth * mFlodwidth - mTrannslateDisPerFlod * mTrannslateDisPerFlod)/6 );
        float anchorPoint = mAnchor * wwwww;
        float midFold = anchorPoint / mFlodwidth;



        for (int i = 0; i < mNumOfFolds; i++) {
            mMatrices[i].reset();
            src[0] = 0;
            src[1] = i * mFlodwidth;
            src[2] = hhhhh;
            src[3] =  src[1];;
            src[4] = src[2];
            src[5] = src[3] + mFlodwidth;
            src[6] = src[0];
            src[7] = src[5];
                if (src.length>0){
                    boolean isEven = i % 2 == 0;
                    dst[0] = isEven ? 0 : depth;
                    dst[1] = i * mTrannslateDisPerFlod;

                    dst[2] = isEven ? hhhhh : hhhhh - depth;//dst[0] + mTrannslateDisPerFlod
                    //dst[1] = (anchorPoint > i * mFlodwidth) ? anchorPoint + (i - midFold) * mTrannslateDisPerFlod : anchorPoint - (midFold - i) * mTrannslateDisPerFlod   ;
                    dst[3]=dst[1];
                    dst[4] = isEven ? hhhhh - depth : hhhhh;
                    dst[5]=dst[3]+mTrannslateDisPerFlod;

                   //  dst[5] = (anchorPoint > (i + 1) * mFlodwidth) ? anchorPoint + (i + 1 - midFold) * mTrannslateDisPerFlod : anchorPoint - (midFold - i - 1) * mTrannslateDisPerFlod;


                    dst[7] = dst[5];
                    dst[6] = isEven ? depth : 0 ;;
                }


if (dst.length>0){

    for (int y = 0; y < 8; y++) {
        dst[y] = Math.round(dst[y]);
    }
}

            mMatrices[i].setPolyToPoly(src, 0, dst, 0, 4 );
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void dispatchDraw(Canvas canvas) {
        if (mFactor == 0) {//  mFactor is 0 condition ,for full folded
            return;
        }
        if (mFactor == 1) {//mFactor is 1 condition ,just  super.dispatchDraw(canvas);
            super.dispatchDraw(canvas);
            return;
        }
       if (mMatrices.length>0){
           for (int i = 0; i < mNumOfFolds; i++) {
               canvas.save();
             //  canvas.clipRect(0, mFlodwidth * i,getWidth(), mFlodwidth * i + mFlodwidth);
            Matrix matrix=   mMatrices[i];
               canvas.concat(matrix);

               // canvas.clipRect(i*mFlodwidth, 0, mFlodwidth*i+mFlodwidth, mBitmap.getHeight());
               canvas.clipRect(0, mFlodwidth * i,getWidth(), mFlodwidth * i + mFlodwidth);
               if (isReady) {
                   canvas.drawBitmap(mBitmap, 0, 0, null);
               } else {// for the first time to draw
                   super.dispatchDraw(mCanvas);
                   canvas.drawBitmap(mBitmap, 0, 0, null);
                   isReady = true;
               }
               canvas.translate(0, mFlodwidth * i);
               if (i % 2 == 0) {
                   canvas.drawRect(0, 0, getWidth(),mFlodwidth , mSolidPaint);
               } else {
                   canvas.drawRect(0, 0, getWidth(), mFlodwidth, mShadowPaint);
               }
               canvas.restore();
           }
       }

    }

    public float getFactor() {
        return mFactor;
    }

    public void setFactor(float factor) {
        this.mFactor = factor;
        updateFold();
        invalidate();
    }

    public void setAnchor(float anchor) {
        this.mAnchor = anchor;
        updateFold();
        invalidate();
    }
}
