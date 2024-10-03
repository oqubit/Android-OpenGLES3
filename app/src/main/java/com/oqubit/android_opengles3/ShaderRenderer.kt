package com.oqubit.android_opengles3

import android.content.Context
import android.opengl.GLES31
import android.opengl.GLSurfaceView
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

private const val UNKNOWN_VALUE = -1
private const val FLOAT_SIZE = 4 // in bytes
private const val VERTEX_DATA_STRIDE = FLOAT_SIZE * 5 // 5 floats per vertex, 3 for position and 2 for UV's.
private const val VERTEX_DATA_POS_OFFSET = 0 // position starts from index 0
private const val VERTEX_DATA_UV_OFFSET = 3 // UV's start from index 3

class ShaderRenderer(private val context: Context) : GLSurfaceView.Renderer {
    private var program = UNKNOWN_VALUE
    private var fragShader = UNKNOWN_VALUE
    private var vertShader = UNKNOWN_VALUE
    private val quadVertices: FloatBuffer

    private var touchPos = floatArrayOf(0.0f, 0.0f)
    private var startTime = 0L

    private var inPositionLocation = UNKNOWN_VALUE
    private var inTextureLocation = UNKNOWN_VALUE
    private var resolutionLocation = UNKNOWN_VALUE
    private var touchPosLocation = UNKNOWN_VALUE
    private var timeLocation = UNKNOWN_VALUE

    init {
        val quadVerticesData = floatArrayOf(
            -1.0f, -1.0f, 0f, 0f, 1f, // [x,y,z, U,V]
            1.0f, -1.0f, 0f, 1f, 1f,
            -1.0f, 1.0f, 0f, 0f, 0f,
            1.0f, 1.0f, 0f, 1f, 0f
        )

        quadVertices = ByteBuffer
            .allocateDirect(quadVerticesData.size * FLOAT_SIZE)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
            .apply {
                put(quadVerticesData).position(0)
            }
    }

    override fun onSurfaceCreated(gl: GL10?, eglConfig: EGLConfig?) {
        program = GLES31.glCreateProgram()

        vertShader = compileShader(context, GLES31.GL_VERTEX_SHADER, "vertex.glsl")
        fragShader = compileShader(context, GLES31.GL_FRAGMENT_SHADER, "fragment.glsl")

        GLES31.glAttachShader(program, vertShader)
        GLES31.glAttachShader(program, fragShader)
        linkProgramAndCheck()
        GLES31.glUseProgram(program)

        inPositionLocation = GLES31.glGetAttribLocation(program, "inPosition")
        checkLocation(inPositionLocation, "inPositionLocation")
        inTextureLocation = GLES31.glGetAttribLocation(program, "inTextureCoord")
        checkLocation(inTextureLocation, "inTextureLocation")
        resolutionLocation = GLES31.glGetUniformLocation(program, "resolution")
        checkLocation(resolutionLocation, "resolutionLocation")
        touchPosLocation = GLES31.glGetUniformLocation(program, "touchPos")
        checkLocation(touchPosLocation, "touchPosLocation")
        timeLocation = GLES31.glGetUniformLocation(program, "time")
        checkLocation(timeLocation, "timeLocation")

        startTime = System.currentTimeMillis()
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES31.glViewport(0, 0, width, height)
        GLES31.glUniform2f(resolutionLocation, width.toFloat(), height.toFloat())
        touchPos = floatArrayOf(width.toFloat() / 2f, height.toFloat() / 2f)
    }

    override fun onDrawFrame(gl: GL10?) {
        GLES31.glClear(GLES31.GL_COLOR_BUFFER_BIT)

        setAttribute(inPositionLocation, 3, VERTEX_DATA_POS_OFFSET)
        setAttribute(inTextureLocation, 2, VERTEX_DATA_UV_OFFSET)

        val iTime = (System.currentTimeMillis() - startTime).toFloat() * 0.001f
        GLES31.glUniform1f(timeLocation, iTime)
        GLES31.glUniform2f(touchPosLocation, touchPos[0], touchPos[1])

        GLES31.glBlendFunc(GLES31.GL_SRC_ALPHA, GLES31.GL_ONE_MINUS_SRC_ALPHA)
        GLES31.glEnable(GLES31.GL_BLEND)

        GLES31.glDrawArrays(GLES31.GL_TRIANGLE_STRIP, 0, 4)
    }

    fun setNewPosition(x: Float, y: Float) {
        touchPos = floatArrayOf(x, y)
    }

    fun destroy() {
        if (program == UNKNOWN_VALUE) {
            return
        }
        GLES31.glDeleteProgram(program)
        GLES31.glDeleteShader(fragShader)
        GLES31.glDeleteShader(vertShader)
        program = UNKNOWN_VALUE
    }

    private fun checkLocation(location: Int, name: String) {
        if (location == UNKNOWN_VALUE) {
            throw RuntimeException("Error: could not find [$name]")
        }
    }

    private fun linkProgramAndCheck() {
        GLES31.glLinkProgram(program)
        val linkStatus = IntArray(1)
        GLES31.glGetProgramiv(program, GLES31.GL_LINK_STATUS, linkStatus, 0)
        if (linkStatus[0] != GLES31.GL_TRUE) {
            val infoLog = GLES31.glGetProgramInfoLog(program)
            GLES31.glDeleteProgram(program)
            program = UNKNOWN_VALUE
            throw RuntimeException("Linking error: $infoLog")
        }
    }

    private fun setAttribute(attributeLocation: Int, size: Int, offset: Int) {
        if (attributeLocation == UNKNOWN_VALUE) {
            return
        }
        quadVertices.position(offset)
        GLES31.glVertexAttribPointer(
            attributeLocation,
            size,
            GLES31.GL_FLOAT,
            false,
            VERTEX_DATA_STRIDE,
            quadVertices
        )
        GLES31.glEnableVertexAttribArray(attributeLocation)
    }
}