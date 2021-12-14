package com.oscarcreator.model3dview.shaders

import android.content.Context
import com.oscarcreator.model3dview.R
import com.oscarcreator.model3dview.renderengine.NORMALS_VBO_LOCATION
import com.oscarcreator.model3dview.renderengine.TEXTURE_VBO_LOCATION

class StaticShader(context: Context) :
    ShaderProgram(context, R.raw.vertex_shader, R.raw.fragment_shader) {

    // location of the uniform
    private var location_transformationMatrix: Int = 0
    private var location_projectionMatrix: Int = 0
    private var location_viewMatrix: Int = 0
    private var location_color: Int = 0


    companion object {
        const val POSITIONS_VBO_LOCATION = 0
    }

    override fun bindAttributes() {
        super.bindAttribute(POSITIONS_VBO_LOCATION, "position")
        super.bindAttribute(TEXTURE_VBO_LOCATION, "textureCoordinate")
        super.bindAttribute(NORMALS_VBO_LOCATION, "normal")

    }

    override fun getAllUniformLocations() {
        location_transformationMatrix = super.getUniformLocation("transformationMatrix")
        location_projectionMatrix = super.getUniformLocation("projectionMatrix")
        location_viewMatrix = super.getUniformLocation("viewMatrix")
    }

    /**
     * Modifies the uniform at [location_transformationMatrix] to the passed data,
     *
     * @param floatArray the new value of uniform at [location_transformationMatrix]
     * */
    fun loadTransformationMatrix(floatArray: FloatArray) {
        super.loadMatrix(location_transformationMatrix, floatArray)
    }

    /**
     * Modifies the uniform at [location_projectionMatrix] to the passed data.
     *
     * @param floatArray the new value of uniform at [location_projectionMatrix]
     * */
    fun loadProjectionMatrix(floatArray: FloatArray) {
        super.loadMatrix(location_projectionMatrix, floatArray)
    }

    /**
     * Modifies the unform at [location_viewMatrix] to the passed data.
     *
     * @param floatArray the new value of uniform at [location_viewMatrix]
     * */
    fun loadViewMatrix(floatArray: FloatArray) {
        super.loadMatrix(location_viewMatrix, floatArray)
    }
}