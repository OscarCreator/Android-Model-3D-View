package com.oscarcreator.model3dview.renderengine

import android.content.Context
import android.opengl.Matrix
import com.oscarcreator.model3dview.models.RawModel
import com.oscarcreator.model3dview.shaders.StaticShader
import com.oscarcreator.model3dview.util.Vector3f

class MasterRenderer(
    context: Context,
    width: Int,
    height: Int
) {

    private val shader: StaticShader
    private val renderer: Renderer

    init {
        shader = StaticShader(context)
        renderer = Renderer(width, height, shader)
    }

    /**
     * Renders a model
     * */
    fun render(model: RawModel) {
        renderer.prepare()
        shader.useProgram()
        shader.loadColor(Vector3f(0.37f, 0.37f, 0.37f))

        val viewMatrix = FloatArray(16)
        Matrix.setIdentityM(viewMatrix, 0)
        shader.loadViewMatrix(viewMatrix)

        renderer.render(model)

        shader.stop()
    }

    fun cleanUp() {
        shader.cleanUp()
    }
}