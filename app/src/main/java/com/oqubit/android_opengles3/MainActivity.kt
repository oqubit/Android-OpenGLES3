package com.oqubit.android_opengles3

import android.annotation.SuppressLint
import android.opengl.GLSurfaceView
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate

class MainActivity : AppCompatActivity() {
    private val renderer by lazy { ShaderRenderer(applicationContext) }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)

        val glSurfaceView = findViewById<GLSurfaceView>(R.id.myGlSurfaceView)
        glSurfaceView.setEGLContextClientVersion(3)
        glSurfaceView.setRenderer(renderer)

        glSurfaceView.setOnTouchListener { view, motionEvent ->
            motionEvent?.let {
                renderer.setNewPosition(it.x, it.y)
            }
            true
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        renderer.destroy()
    }
}