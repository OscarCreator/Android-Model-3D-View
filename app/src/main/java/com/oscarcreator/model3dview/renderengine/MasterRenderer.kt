package com.oscarcreator.model3dview.renderengine

import android.content.Context
import android.opengl.Matrix
import com.oscarcreator.model3dview.entities.Camera
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
    fun render(light: Light, camera: Camera, model: Entity) {
        renderer.prepare()
        shader.useProgram()
        shader.loadLight(light)
        shader.loadViewMatrix(camera)

        renderer.render(model)

        shader.stop()
    }

    fun cleanUp() {
        shader.cleanUp()
    }
}