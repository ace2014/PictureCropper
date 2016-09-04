package com.pzl.library.cropper;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.util.Arrays;

class ClipView extends View {
    private String TAG = "ClipView";

    private int width;
    private int height;
    private Paint paintShade;
    private Paint paintPath;
    private float ratio = 1;

    private int clipX;
    private int clipY;
    private int clipWidth;
    private int clipHeight;

    private int paddingMax;
    private int[] area;
    private Path pathLine;
    private int pathColor = Color.WHITE;


    public ClipView(Context context) {
        this(context, null);
    }

    public ClipView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }


    public ClipView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        area = new int[4];

        paintShade = new Paint();
        paintShade.setAntiAlias(true);
        paintShade.setColor(0xaa000000);

        paintPath = new Paint();
        paintPath.setAntiAlias(true);
        paintPath.setStyle(Paint.Style.STROKE);
        paintPath.setStrokeWidth(2);
        paintPath.setColor(pathColor);

        pathLine = new Path();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        width = getWidth();
        height = getHeight();

        clipCalc(ratio);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        // width = getWidth();
        // height = getHeight();
        // clipCalc();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //top
        canvas.drawRect(0, 0, width, clipY, paintShade);
        //left
        canvas.drawRect(0, clipY, clipX, clipY + clipHeight, paintShade);
        //right
        canvas.drawRect(clipX + clipWidth, clipY, width, clipY + clipHeight, paintShade);
        //bottom
        canvas.drawRect(0, clipY + clipHeight, width, height, paintShade);

        canvas.drawPath(pathLine, paintPath);
    }

    public void setRatio(float w, float h) {
        ratio = w / h;
        clipCalc(ratio);
        invalidate();
    }

    private void clipCalc(float ratio) {

        if (ratio < 1f) {
            clipY = paddingMax;
            clipHeight = height - clipY * 2;
            clipWidth = (int) (ratio * clipHeight);
            clipX = (width - clipWidth) / 2;
        } else if (ratio >= 1f) {
            clipX = paddingMax;
            clipWidth = width - clipX * 2;
            clipHeight = (int) (clipWidth / ratio);
            clipY = (height - clipHeight) / 2;
        }

        area[0] = clipX;
        area[1] = clipY;
        area[2] = clipWidth;
        area[3] = clipHeight;

        pathLine.reset();
        pathLine.moveTo(clipX, clipY);
        pathLine.lineTo(clipX + clipWidth, clipY);
        pathLine.lineTo(clipX + clipWidth, clipY + clipHeight);
        pathLine.lineTo(clipX, clipY + clipHeight);
        pathLine.close();

        Log.d(TAG, "clipX=" + clipX + ",clipY=" + clipY + ",clipWidth=" + clipWidth + ",clipHeight=" + clipHeight + ",paddingMax=" + paddingMax);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        int padding[] = new int[4];
        padding[0] = getPaddingBottom();
        padding[1] = getPaddingTop();
        padding[2] = getPaddingLeft();
        padding[3] = getPaddingRight();
        Arrays.sort(padding);
        paddingMax = padding[3];
    }

    public int[] getClipArea() {
        return area;
    }

    public void setPaddingMax(int padding) {
        paddingMax = padding;
        clipCalc(ratio);
        invalidate();
    }
}
