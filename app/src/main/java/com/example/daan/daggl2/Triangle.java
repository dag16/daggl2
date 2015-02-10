package com.example.daan.daggl2;

import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * Created by daan on 10.02.2015.
 */
public class Triangle {
    private static final int VERTEX_COORDINATES = 3;
    private final FloatBuffer vertexBuffer;
    private final float[] triangleCoords = {
        0.0f, 0.5f, 0.0f,
       -0.5f, 0.0f, 0.0f,
        0.5f, 0.0f, 0.0f,
    };
    private final int program;
    private final String vertexShaderCode =
            "uniform mat4 mvp_matrix;" +
            "attribute vec4 vPosition;" +
            "void main(void) {" +
            " gl_Position = mvp_matrix * vPosition;" +
            "}";
    private final String fragmentShaderCode =
            "precision mediump float;" +
            "uniform vec4 vColor;" +
            "void main(void) {" +
            " gl_FragColor = vColor;" +
            "}";
    private float[] vertexColors = {
            1.0f, 0.0f, 0.0f, 0.0f,
    };

    public Triangle() {
        // set the vertex buffer that will be passed on to the GPU
        vertexBuffer = ByteBuffer.allocateDirect(triangleCoords.length * 4)
                                 .order(ByteOrder.nativeOrder())
                                 .asFloatBuffer();
        vertexBuffer.put(triangleCoords).position(0);

        // set up the vertex and fragment shaders
        int vertexShader = MyGLRenderer.loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
        int fragmentShader = MyGLRenderer.loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);
        program = GLES20.glCreateProgram();
        GLES20.glAttachShader(program, vertexShader);
        GLES20.glAttachShader(program, fragmentShader);
        GLES20.glLinkProgram(program);

    }

    public void draw(float[] mvpMatrix) {
        GLES20.glUseProgram(program);
        int positionId = GLES20.glGetAttribLocation(program, "vPosition");
        int colorId = GLES20.glGetUniformLocation(program, "vColor");
        int mvpMatrixId = GLES20.glGetUniformLocation(program, "mvp_matrix");

        GLES20.glEnableVertexAttribArray(positionId);
        GLES20.glVertexAttribPointer(positionId, VERTEX_COORDINATES, GLES20.GL_FLOAT, false, 0, vertexBuffer);
        GLES20.glUniform4fv(colorId, 1, vertexColors, 0);
        GLES20.glUniformMatrix4fv(mvpMatrixId, 1, false, mvpMatrix, 0);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, VERTEX_COORDINATES);
        GLES20.glDisableVertexAttribArray(positionId);

    }
}
