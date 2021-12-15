package com.oscarcreator.model3dview

import android.annotation.SuppressLint
import android.content.Context
import android.opengl.GLSurfaceView
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.core.content.withStyledAttributes
import com.oscarcreator.model3dview.renderengine.BasicRenderer

class Model3DView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
): GLSurfaceView(context, attrs) {

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

        context.withStyledAttributes(attrs, R.styleable.Model3DView) {
            val modelId = getResourceId(R.styleable.Model3DView_model, 0)
            val textureId = getResourceId(R.styleable.Model3DView_texture, 0)
            if (modelId != 0 && textureId != 0) {
                renderer.setModel(modelId, textureId)
            }

        }

        setRenderer(renderer)
        //requestRender()

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

                //renderer.angle += (dx + dy) * TOUCH_SCALE_FACTOR
                // tells the renderer that it's time to render next frame
                //requestRender()
            }
        }

        previousX = x
        previousY = y
        return true
    }


}