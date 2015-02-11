package com.example.daan.daggl2;

import android.opengl.GLES30;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

/**
 * Created by daan on 10.02.2015.
 */
public class Triangle {
    private static final int VERTEX_COORDINATES = 3;
    private final FloatBuffer vertexBuffer;
    private final float[] triangleCoords = {
        0.0f, 0.25f, 0.0f, // CU
       -0.25f, 0.0f, 0.0f, // L
        0.25f, 0.0f, 0.0f, // R
        0.0f,-0.25f, 0.0f, // CD
    };
    private final short[] vertexIndices = {0,1,2,1,2,3};
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
            " gl_FragColor.bgra = vColor.rgba;" +
            "}";

    private final ShortBuffer vertexOrder;
    private float[] vertexColors = {
            1.0f, 0.0f, 0.0f, 0.0f,
    };

    public Triangle() {
        // set the vertex buffer that will be passed on to the GPU
        vertexBuffer = ByteBuffer.allocateDirect(triangleCoords.length * 4)
                                 .order(ByteOrder.nativeOrder())
                                 .asFloatBuffer();
        vertexBuffer.put(triangleCoords).position(0);
        // to draw more triangles the order of the vertices needs to be specified
        vertexOrder = ByteBuffer.allocateDirect(vertexIndices.length*2).order(ByteOrder.nativeOrder()).asShortBuffer();
        vertexOrder.put(vertexIndices).position(0);
        // set up the vertex and fragment shaders
        int vertexShader = MyGLRenderer.loadShader(GLES30.GL_VERTEX_SHADER, vertexShaderCode);
        int fragmentShader = MyGLRenderer.loadShader(GLES30.GL_FRAGMENT_SHADER, fragmentShaderCode);
        program = GLES30.glCreateProgram();
        GLES30.glAttachShader(program, vertexShader);
        GLES30.glAttachShader(program, fragmentShader);
        GLES30.glLinkProgram(program);
        GLES30.glUseProgram(program);
    }

    public void draw(float[] mvpMatrix, float[] color) {
        vertexColors = color;
        int positionId = GLES30.glGetAttribLocation(program, "vPosition");
        int colorId = GLES30.glGetUniformLocation(program, "vColor");
        int mvpMatrixId = GLES30.glGetUniformLocation(program, "mvp_matrix");

        GLES30.glEnableVertexAttribArray(positionId);
        GLES30.glVertexAttribPointer(positionId, VERTEX_COORDINATES, GLES30.GL_FLOAT, false, 0, vertexBuffer);
        GLES30.glUniform4fv(colorId, 1, vertexColors, 0);
        GLES30.glUniformMatrix4fv(mvpMatrixId, 1, false, mvpMatrix, 0);

        //GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, VERTEX_COORDINATES);
        GLES30.glDrawElements(GLES30.GL_TRIANGLES, vertexIndices.length, GLES30.GL_UNSIGNED_SHORT, vertexOrder);
        GLES30.glDisableVertexAttribArray(positionId);

    }
}
