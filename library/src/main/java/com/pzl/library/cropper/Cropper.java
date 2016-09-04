package com.pzl.library.cropper;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.util.FloatMath;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

/**
 * Created by zl.peng on 2016/9/3 08:57.
 */
public class Cropper extends RelativeLayout implements View.OnTouchListener {
    public static final String TAG = "Cropper";

    private Context context;
    private ClipView clipView;
    private ImageView imageView;

    private Matrix matrix = new Matrix();
    private Matrix savedMatrix = new Matrix();

    static final int NONE = 0;
    static final int DRAG = 1;
    static final int ZOOM = 2;

    private int mode = NONE;

    PointF start = new PointF();
    PointF mid = new PointF();

    float oldDist = 1f;


    public Cropper(Context context) {
        this(context, null);
    }

    public Cropper(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public Cropper(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
    }


    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        imageView = new ImageView(context);
        LayoutParams ivLp = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        imageView.setLayoutParams(ivLp);
        imageView.setScaleType(ImageView.ScaleType.MATRIX);

        clipView = new ClipView(context);
        LayoutParams cvLp = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        clipView.setLayoutParams(cvLp);

        addView(imageView);
        addView(clipView);

        imageView.setBackgroundColor(Color.BLACK);
        imageView.setOnTouchListener(this);

        //debugBgColor();
    }

    public void setImageBitmap(Bitmap bitmap) {
        imageView.setImageBitmap(bitmap);
    }

    public void setImageResource(int resId) {
        imageView.setImageResource(resId);
    }

    /**
     * 设置框选padding
     *
     * @param padding
     */
    public void setPadding(int padding) {
        clipView.setPaddingMax(padding);
    }

    /**
     * 设置框选比例
     *
     * @param w
     * @param h
     */
    public void setRatio(float w, float h) {
        clipView.setRatio(w, h);
    }

    //debug 测试 -------------------------------------------


    /**
     * 测试用
     */
    private void debugBgColor() {
        imageView.setBackgroundColor(Color.YELLOW);
        clipView.setBackgroundColor(Color.CYAN);
    }

    public boolean onTouch(View v, MotionEvent event) {
        ImageView view = (ImageView) v;
        // Handle touch events here...
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                savedMatrix.set(matrix);
                // �O�ó�ʼ�cλ��
                start.set(event.getX(), event.getY());
                Log.d(TAG, "mode=DRAG");
                mode = DRAG;
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                oldDist = spacing(event);
                Log.d(TAG, "oldDist=" + oldDist);
                if (oldDist > 10f) {
                    savedMatrix.set(matrix);
                    midPoint(mid, event);
                    mode = ZOOM;
                    Log.d(TAG, "mode=ZOOM");
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                mode = NONE;
                Log.d(TAG, "mode=NONE");
                break;
            case MotionEvent.ACTION_MOVE:
                if (mode == DRAG) {
                    // ...
                    matrix.set(savedMatrix);
                    matrix.postTranslate(event.getX() - start.x, event.getY()
                            - start.y);
                } else if (mode == ZOOM) {
                    float newDist = spacing(event);
                    Log.d(TAG, "newDist=" + newDist);
                    if (newDist > 10f) {
                        matrix.set(savedMatrix);
                        float scale = newDist / oldDist;
                        matrix.postScale(scale, scale, mid.x, mid.y);
                    }
                }
                break;
        }

        view.setImageMatrix(matrix);
        return true; // indicate event was handled
    }

    /**
     * Determine the space between the first two fingers
     */
    private float spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float)Math.sqrt((double)(x * x + y * y));
    }

    /**
     * Calculate the mid point of the first two fingers
     */
    private void midPoint(PointF point, MotionEvent event) {
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        point.set(x / 2, y / 2);
    }

    /**
     * 获得最终裁剪后的bitmap
     *
     * @return
     */
    public Bitmap getClipBitmap() {
        Bitmap screenShoot = takeScreenShot();

        Bitmap finalBitmap = Bitmap.createBitmap(screenShoot,
                clipView.getClipArea()[0], clipView.getClipArea()[1], clipView.getClipArea()[2], clipView.getClipArea()[3]);
        return finalBitmap;
    }

    private Bitmap takeScreenShot() {
       /* View view = this.getWindow().getDecorView();
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        return view.getDrawingCache();*/

        imageView.setDrawingCacheEnabled(true);
        imageView.buildDrawingCache();
        Bitmap result = Bitmap.createBitmap(imageView.getDrawingCache());
        imageView.setDrawingCacheEnabled(false);
        return result;
    }
}
