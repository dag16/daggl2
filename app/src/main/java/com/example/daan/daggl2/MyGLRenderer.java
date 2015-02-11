package com.example.daan.daggl2;

import android.opengl.GLES31;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Log;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by daan on 10.02.2015.
 */
public class MyGLRenderer implements GLSurfaceView.Renderer{

    // model, view, and projection matrices
    private float[] projectionMatrix = new float[16];
    private float[] viewMatrix = new float[16];
    private float[] mvpMatrix = new float[16];

    // objects to render
    private Triangle t;
    private float angle;
    private float[] rotationMatrix = new float[16];
    private float[] colors = {1.0f, 1.0f, 1.0f, 1.0f};

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        GLES31.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        GLES31.glCullFace(GLES31.GL_FRONT_AND_BACK);
        Log.i("VERSION", GLES31.glGetString(GLES31.GL_VERSION));
        t = new Triangle();
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES31.glViewport(0,0,width, height);
        float ratio = (float) width / height;
        Matrix.frustumM(projectionMatrix, 0, -ratio, ratio, -1, 1, 3, 7);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        float[] scratch = new float[16];
        Matrix.setRotateM(rotationMatrix, 0, angle, 0, 0, 1.0f);
        GLES31.glClear(GLES31.GL_COLOR_BUFFER_BIT | GLES31.GL_DEPTH_BUFFER_BIT);
        Matrix.setLookAtM(viewMatrix, 0, 0, 0, -3, 0f, 0f, 0f, 0f, 1.0f, 0f);
        Matrix.multiplyMM(mvpMatrix, 0, projectionMatrix, 0, viewMatrix, 0);
        Matrix.multiplyMM(scratch, 0, mvpMatrix, 0, rotationMatrix, 0);
        t.draw(scratch, colors);
    }

    public static int loadShader(int shaderType, String shaderCode) {
        int shader = GLES31.glCreateShader(shaderType);
        GLES31.glShaderSource(shader, shaderCode);
        GLES31.glCompileShader(shader);
        return shader;
    }

    public float getAngle() {
        return angle;
    }

    public void setAngle(float angle) {
        this.angle = angle;
    }

    public float[] getColors() {
        return colors;
    }

    public void setColors(float dx, float dy) {
        colors[0] = dx / 100.0f;
        colors[1] = dy / 100.0f;
    }

}
