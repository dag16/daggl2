package com.example.daan.daggl2;

import android.content.Context;
import android.opengl.GLSurfaceView;

/**
 * Created by daan on 10.02.2015.
 */
public class MyGLView extends GLSurfaceView{
    private MyGLRenderer renderer;
    public MyGLView(Context context) {
        super(context);
        renderer = new MyGLRenderer();
        setEGLContextClientVersion(2);
        setEGLConfigChooser(8,8,8,8,16,0);
        setRenderer(renderer);
    }
}
