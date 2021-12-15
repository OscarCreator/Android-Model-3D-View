package com.oscarcreator.model3dview.renderengine

import android.content.Context
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import com.oscarcreator.model3dview.R
import com.oscarcreator.model3dview.entities.Entity
import com.oscarcreator.model3dview.entities.Light
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

    private lateinit var light: Light

    // renderer code runs on a separate thread
    @Volatile
    var angle = 0f

    override fun onSurfaceCreated(p0: GL10?, p1: EGLConfig?) {
        loader = Loader()
        val rawModel = loadObjModel(context, R.raw.utah_teapot_sharp_edges, loader)
        val texturedModel = TexturedModel(rawModel, ModelTexture(loader.loadTexture(context, R.drawable.blue), 10f, 0.2f))
        model = Entity(texturedModel, Vector3f(0f, -2f, -10f), 0f, 0f, 0f)
        renderer = MasterRenderer(context, surfaceView.width, surfaceView.height)

        light = Light(Vector3f(10f, 10f, 10f), Vector3f(1f, 1f, 1f))
    }

    // Called when screen size is changed
    override fun onSurfaceChanged(unused: GL10?, width: Int, height: Int) {
        // Set opengl viewport to fill entire surface
        GLES20.glViewport(0,0, width, height)
    }
    override fun onDrawFrame(p0: GL10?) {
        model.rotY += 0.2f
        renderer.render(light, model)
    }

}