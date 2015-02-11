package com.example.daan.daggl2;

import android.opengl.GLES20;
import android.opengl.GLES31;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
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
            "attribute vec4 a_v4Position;" +
            "attribute vec4 a_v4FillColor;" +
            "varying vec4 v_v4FillColor;" +
            "void main(void) {" +
            " v_v4FillColor = a_v4FillColor;" +
            " gl_Position = mvp_matrix * a_v4Position;" +
            "}";
    private final String fragmentShaderCode =
            "precision mediump float;" +
            "varying vec4 v_v4FillColor;" +
            "void main(void) {" +
            " gl_FragColor = v_v4FillColor;" +
            "}";
    private final String computeShaderCode =
            "#version 310 es" +
            "\n" +
            "uniform float radius;" +
            "struct Vector3f { float x; float y; float z; float w; };" +
            "struct AttribData { Vector3f v; Vector3f c; };" +
            "layout(std140, binding = 0) buffer destBuffer { AttribData data[]; } outBuffer;" +
            "layout (local_size_x = 8, local_size_y = 8, local_size_z = 1) in;" +
            "void main() {" +
            " ivec2 storePos = ivec2(gl_GlobalInvocationID.xy);" +
            " uint gWidth = gl_WorkGroupSize.x * gl_NumWorkGroups.x;" +
            " uint gHeigth = gl_WorkGroupSize.y * gl_NumWorkGroups.y;" +
            " uint gSize = uint(gWidth) * uint(gHeigth);" +
            " uint offset = uint(storePos.y)*gWidth + uint(storePos.x);" +
            " float alpha = 2.0 * 3.1159265359 * (float(offset) / float(gSize));" +
            " outBuffer.data[offset].v.x = float(sin(alpha)) * float(radius);" +
            " outBuffer.data[offset].v.y = float(cos(alpha)) * float(radius);" +
            " outBuffer.data[offset].v.z = 0.0;" +
            " outBuffer.data[offset].v.w = 1.0;" +
            " outBuffer.data[offset].c.x = float(storePos.x) / float(gWidth);" +
            " outBuffer.data[offset].c.y = 0.0;" +
            " outBuffer.data[offset].c.z = 1.0;" +
            " outBuffer.data[offset].c.w = 1.0;" +
            "}";
    private final ShortBuffer vertexOrder;
    private final int computeProgram;
    private float[] vertexColors = {
            1.0f, 0.0f, 0.0f, 0.0f,
    };
    private int error;
    private int indexBufferBinding;
    private float radius = 1.0f;
    private int[] buffers;
    private int gVBO;

    public Triangle() {
        buffers = new int[1];
        GLES31.glGenBuffers(1, buffers, 0);
        gVBO = buffers[0];
        // set the vertex buffer that will be passed on to the GPU
        vertexBuffer = ByteBuffer.allocateDirect(triangleCoords.length * 4)
                                 .order(ByteOrder.nativeOrder())
                                 .asFloatBuffer();
        vertexBuffer.put(triangleCoords).position(0);
        // to draw more triangles the order of the vertices needs to be specified
        vertexOrder = ByteBuffer.allocateDirect(vertexIndices.length*2).order(ByteOrder.nativeOrder()).asShortBuffer();
        vertexOrder.put(vertexIndices).position(0);
        // set up the vertex and fragment shaders
        int vertexShader = MyGLRenderer.loadShader(GLES31.GL_VERTEX_SHADER, vertexShaderCode);
        int fragmentShader = MyGLRenderer.loadShader(GLES31.GL_FRAGMENT_SHADER, fragmentShaderCode);
        // set up the compute shader
        int computeShader = MyGLRenderer.loadShader(GLES31.GL_COMPUTE_SHADER, computeShaderCode);
        computeProgram = GLES31.glCreateProgram();
        GLES31.glAttachShader(computeProgram, computeShader);
        GLES31.glLinkProgram(computeProgram);
        checkGLError("LinkProgram/computeProgram");
        Log.i("GLLINK", GLES31.glGetProgramInfoLog(computeProgram));
        program = GLES31.glCreateProgram();

        GLES31.glAttachShader(program, vertexShader);
        GLES31.glAttachShader(program, fragmentShader);
        GLES31.glLinkProgram(program);

    }

    public void draw(float[] mvpMatrix, float[] color) {

        GLES31.glUseProgram(computeProgram);
        checkGLError("UseProgram/computeProgram");
        int radiusId = GLES31.glGetUniformLocation(computeProgram, "radius");
        indexBufferBinding = 0;
        GLES31.glUniform1f(radiusId, (float) radius);
        GLES31.glBindBufferBase(GLES31.GL_SHADER_STORAGE_BUFFER, indexBufferBinding, gVBO);
        checkGLError("glBindBuffer/gVBO");
        GLES31.glDispatchCompute(2, 2, 1);
        GLES31.glBindBufferBase(GLES31.GL_SHADER_STORAGE_BUFFER, indexBufferBinding, 0);
        //GLES31.glMemoryBarrier(1); // Where is GL_VERTEX_ATTRIB_ARRAY_BARRIER_BIT?
        checkGLError("glMemoryBarrier/1");
        GLES31.glBindBuffer(GLES31.GL_ARRAY_BUFFER, gVBO);
        checkGLError("glBindBuffer/gVBO");

        GLES31.glUseProgram(program);

        int posId = GLES31.glGetAttribLocation(program, "a_v4Position");
        int fillId = GLES31.glGetAttribLocation(program, "a_v4FillColor");
        int mvpMatrixId = GLES31.glGetUniformLocation(program, "mvp_matrix");

        GLES31.glEnableVertexAttribArray(posId);
        GLES31.glEnableVertexAttribArray(fillId);
        GLES31.glUniformMatrix4fv(mvpMatrixId, 1, false, mvpMatrix, 0);
        GLES31.glDrawArrays(GLES31.GL_POINTS, 0, 3);/*
        */
        /*
        vertexColors = color;
        int positionId = GLES31.glGetAttribLocation(program, "vPosition");
        int colorId = GLES31.glGetUniformLocation(program, "vColor");
        int mvpMatrixId = GLES31.glGetUniformLocation(program, "mvp_matrix");

        GLES31.glEnableVertexAttribArray(positionId);
        GLES31.glVertexAttribPointer(positionId, VERTEX_COORDINATES, GLES31.GL_FLOAT, false, 0, vertexBuffer);
        GLES31.glUniform4fv(colorId, 1, vertexColors, 0);
        GLES31.glUniformMatrix4fv(mvpMatrixId, 1, false, mvpMatrix, 0);

        //GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, VERTEX_COORDINATES);
        GLES31.glDrawElements(GLES31.GL_TRIANGLES, vertexIndices.length, GLES31.GL_UNSIGNED_SHORT, vertexOrder);
        GLES31.glDisableVertexAttribArray(positionId);
        */
    }
    public void checkGLError(String loc) {
        int error = GLES31.glGetError();
        if (error != GLES31.GL_NO_ERROR)
        {
            Log.i("GLError", "Error " + error + " at " + loc);
        }
    }

}
