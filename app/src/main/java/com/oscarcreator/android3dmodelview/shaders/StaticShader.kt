package com.oscarcreator.android3dmodelview.shaders

import android.content.Context
import com.oscarcreator.android3dmodelview.R
import com.oscarcreator.android3dmodelview.util.Vector3f

class StaticShader(context: Context) :
    ShaderProgram(context, R.raw.vertex_shader, R.raw.fragment_shader) {

    // location of the uniform
    private var location_modelViewProjectionMatrix: Int = 0
    private var location_color: Int = 0


    companion object {
        const val POSITIONS_VBO_LOCATION = 0
    }

    override fun bindAttributes() {
        super.bindAttribute(POSITIONS_VBO_LOCATION, "position")
    }

    override fun getAllUniformLocations() {
        //location_color = super.getUniformLocation("vColor")
        location_modelViewProjectionMatrix = super.getUniformLocation("uMVPMatrix")
        location_color = super.getUniformLocation("color")
    }

    /**
     * Modifies the uniform at [location_modelViewProjectionMatrix] to the passed data.
     *
     * @param floatArray the new value of uniform at [location_modelViewProjectionMatrix]
     * */
    fun loadViewModelProjectionMatrix(floatArray: FloatArray) {
        super.loadMatrix(location_modelViewProjectionMatrix, floatArray)
    }

    /**
     * Modifies the uniform at [location_color] to the passed data.
     *
     * @param color the new color value of the model
     * */
    fun loadColor(color: Vector3f) {
        super.loadVector(location_color, color)
    }
}