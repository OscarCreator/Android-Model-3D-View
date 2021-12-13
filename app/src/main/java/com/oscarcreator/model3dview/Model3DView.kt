package com.oscarcreator.model3dview

import android.annotation.SuppressLint
import android.content.Context
import android.opengl.GLSurfaceView
import android.view.MotionEvent
import com.oscarcreator.model3dview.renderengine.BasicRenderer

class Model3DView(context: Context): GLSurfaceView(context) {

    private val renderer: BasicRenderer

    companion object {
        private const val TOUCH_SCALE_FACTOR: Float = 180f / 360f
    }

    private var previousX: Float = 0f
    private var previousY: Float = 0f

    init {
        setEGLContextClientVersion(3)

        //Only draw the view when drawing data is changed
        renderer = BasicRenderer(context, this)

        setRenderer(renderer)

        renderMode = RENDERMODE_CONTINUOUSLY//RENDERMODE_WHEN_DIRTY
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {

        val x: Float = event.x
        val y: Float = event.y

        when (event.action) {
            MotionEvent.ACTION_MOVE -> {
                var dx = x - previousX
                var dy = y - previousY

                // reverse direction
                if (y > height / 2) {
                    dx *= -1
                }

                if (x < width / 2) {
                    dy *= -1
                }

                renderer.angle += (dx + dy) * TOUCH_SCALE_FACTOR
                // tells the renderer that it's time to render next frame
                requestRender()
            }
        }

        previousX = x
        previousY = y
        return true
    }


}