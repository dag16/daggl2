package com.example.daan.daggl2;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;

/**
 * Created by daan on 10.02.2015.
 */
public class MyGLView extends GLSurfaceView{
    private static final float TOUCH_SCALE_FACTOR = 0.5f;
    private MyGLRenderer renderer;
    private float mPreviousX;
    private float mPreviousY;

    public MyGLView(Context context) {
        super(context);
        renderer = new MyGLRenderer();
        setEGLContextClientVersion(3);
        setEGLConfigChooser(8,8,8,8,16,0);
        setRenderer(renderer);
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        float x = e.getX();
        float y = e.getY();
        switch (e.getAction()) {
            case MotionEvent.ACTION_MOVE:
                float dx = x - mPreviousX;
                float dy = y - mPreviousY;
                if (y > getHeight() / 2) {
                    dx *= -1;
                }
                if (x < getWidth() / 2) {
                    dy *= -1;
                }
                renderer.setAngle(renderer.getAngle() + ((dx+dy) * TOUCH_SCALE_FACTOR));
                renderer.setColors(dx, dy);
                requestRender();
        }
        mPreviousX = x;
        mPreviousY = y;
        return true;
    }
}
