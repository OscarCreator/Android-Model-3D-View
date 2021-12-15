package com.oscarcreator.model3dview.renderengine

import android.content.Context
import android.opengl.Matrix
import com.oscarcreator.model3dview.entities.Entity
import com.oscarcreator.model3dview.entities.Light
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
    fun render(light: Light, model: Entity) {
        renderer.prepare()
        shader.useProgram()
        shader.loadLight(light)
        val viewMatrix = FloatArray(16)
        Matrix.setIdentityM(viewMatrix, 0)
        Matrix.translateM(viewMatrix, 0, 0f, -4f, 0f)
        Matrix.rotateM(viewMatrix, 0, 20f, 1f, 0f, 0f)
        shader.loadViewMatrix(viewMatrix)

        renderer.render(model)

        shader.stop()
    }

    fun cleanUp() {
        shader.cleanUp()
    }
}