package com.oscarcreator.model3dview.renderengine

import android.content.Context
import android.opengl.Matrix
import com.oscarcreator.model3dview.entities.Entity
import com.oscarcreator.model3dview.shaders.StaticShader

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
    fun render(model: Entity) {
        renderer.prepare()
        shader.useProgram()

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