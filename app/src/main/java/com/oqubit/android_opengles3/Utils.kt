package com.oqubit.android_opengles3

import android.content.Context
import android.opengl.GLES31
import java.io.IOException

fun compileShader(context: Context, type: Int, fileName: String): Int {
    val shader = GLES31.glCreateShader(type)
    GLES31.glShaderSource(shader, context.readTextFromAssets(fileName))
    GLES31.glCompileShader(shader)

    val compileStatus = IntArray(1)
    GLES31.glGetShaderiv(shader, GLES31.GL_COMPILE_STATUS, compileStatus, 0)
    if (compileStatus[0] == 0) {
        val infoLog = GLES31.glGetShaderInfoLog(shader)
        GLES31.glDeleteShader(shader)
        throw RuntimeException("Shader compile error: $infoLog")
    }

    return shader
}

private fun Context.readTextFromAssets(fileName: String): String {
    try {
        return assets.open(fileName).bufferedReader().use { it.readText() }
    } catch (e: IOException) {
        throw RuntimeException(e)
    }
}