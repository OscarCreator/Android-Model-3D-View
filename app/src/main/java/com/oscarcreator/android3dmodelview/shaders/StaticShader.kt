package com.oscarcreator.android3dmodelview.shaders

import android.content.Context
import com.oscarcreator.android3dmodelview.R

class StaticShader(context: Context) :
    ShaderProgram(context, R.raw.vertex_shader, R.raw.fragment_shader) {

    // location of the uniform
    private var location_modelViewProjectionMatrix: Int = 0


    companion object {
        const val POSITIONS_VBO_LOCATION = 0
    }

    override fun bindAttributes() {
        super.bindAttribute(POSITIONS_VBO_LOCATION, "position")
    }

    override fun getAllUniformLocations() {
        //location_color = super.getUniformLocation("vColor")
        location_modelViewProjectionMatrix = super.getUniformLocation("uMVPMatrix")
    }

    /**
     * Modifies the uniform at [location_modelViewProjectionMatrix] to the passed data.
     *
     * @param floatArray the new value of uniform at [location_modelViewProjectionMatrix]
     * */
    fun loadViewModelProjectionMatrix(floatArray: FloatArray) {
        super.loadMatrix(location_modelViewProjectionMatrix, floatArray)
    }
}