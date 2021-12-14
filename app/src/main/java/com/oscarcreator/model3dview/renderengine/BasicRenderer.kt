package com.oscarcreator.model3dview.renderengine

import android.content.Context
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import com.oscarcreator.model3dview.R
import com.oscarcreator.model3dview.entities.Entity
import com.oscarcreator.model3dview.models.TexturedModel
import com.oscarcreator.model3dview.textures.ModelTexture
import com.oscarcreator.model3dview.util.Vector3f
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class BasicRenderer(
    private val context: Context,
    private val surfaceView: GLSurfaceView
) : GLSurfaceView.Renderer {

    private lateinit var model: Entity
    private lateinit var loader: Loader
    private lateinit var renderer: MasterRenderer

    // renderer code runs on a separate thread
    @Volatile
    var angle = 0f

    override fun onSurfaceCreated(p0: GL10?, p1: EGLConfig?) {
        loader = Loader()
        val rawModel = loadObjModel(context, R.raw.cube, loader)
        val texturedModel = TexturedModel(rawModel, ModelTexture(loader.loadTexture(context, R.drawable.image)))
        model = Entity(texturedModel, Vector3f(0f, 0f, -50f), 30f, 10f, 60f)
        renderer = MasterRenderer(context, surfaceView.width, surfaceView.height)
    }

    // Called when screen size is changed
    override fun onSurfaceChanged(unused: GL10?, width: Int, height: Int) {
        // Set opengl viewport to fill entire surface
        GLES20.glViewport(0,0, width, height)
    }
    override fun onDrawFrame(p0: GL10?) {
        model.rotX += 0.3f
        model.rotY += 0.2f
        model.rotZ += 0.5f
        renderer.render(model)
    }

}