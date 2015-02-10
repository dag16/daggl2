package com.example.daan.daggl2;

import android.app.Activity;
import android.os.Bundle;

import com.example.daan.daggl2.MyGLView;

/**
 * Created by daan on 10.02.2015.
 */
public class ActivityOne extends Activity {
    private MyGLView glView;
    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        glView = new MyGLView(this);
        setContentView(glView);
    }
}
