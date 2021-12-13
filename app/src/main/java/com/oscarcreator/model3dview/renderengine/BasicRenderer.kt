package com.oscarcreator.model3dview.renderengine

import android.content.Context
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import com.oscarcreator.model3dview.models.RawModel
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class BasicRenderer(
    private val context: Context,
    private val surfaceView: GLSurfaceView
) : GLSurfaceView.Renderer {

    private lateinit var model: RawModel
    private lateinit var loader: Loader
    private lateinit var renderer: MasterRenderer

    // renderer code runs on a separate thread
    @Volatile
    var angle = 0f

    override fun onSurfaceCreated(p0: GL10?, p1: EGLConfig?) {
        loader = Loader()
        model = loader.loadToVAO(triangleCoords, intArrayOf(0, 1, 2, 1, 3, 2))
        renderer = MasterRenderer(context, surfaceView.width, surfaceView.height)
    }

    // Called when screen size is changed
    override fun onSurfaceChanged(unused: GL10?, width: Int, height: Int) {
        // Set opengl viewport to fill entire surface
        GLES20.glViewport(0,0, width, height)
    }
    override fun onDrawFrame(p0: GL10?) {
        renderer.render(model)
    }

}

var triangleCoords = floatArrayOf(
    -0.5f, 0.5f, -2.7f,
    -0.5f, -0.5f, -2.7f,
    0.5f, 0.5f, -2.7f,
    0.5f, -0.5f, -2.7f
)