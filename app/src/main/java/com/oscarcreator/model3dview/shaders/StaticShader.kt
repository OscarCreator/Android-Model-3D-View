package com.oscarcreator.model3dview.shaders

import android.content.Context
import com.oscarcreator.model3dview.R
import com.oscarcreator.model3dview.entities.Camera
import com.oscarcreator.model3dview.entities.Light
import com.oscarcreator.model3dview.renderengine.NORMALS_VBO_LOCATION
import com.oscarcreator.model3dview.renderengine.TEXTURE_VBO_LOCATION
import com.oscarcreator.model3dview.util.createViewMatrix

class StaticShader(context: Context) :
    ShaderProgram(context, R.raw.vertex_shader, R.raw.fragment_shader) {

    // location of the uniform
    private var location_transformationMatrix: Int = 0
    private var location_projectionMatrix: Int = 0
    private var location_viewMatrix: Int = 0
    private var location_lightPosition: Int = 0
    private var location_lightColor: Int = 0
    private var location_shineDamper: Int = 0
    private var location_reflectivity: Int = 0



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

        location_lightPosition = super.getUniformLocation("lightPosition")
        location_lightColor = super.getUniformLocation("lightColor")
        location_shineDamper = super.getUniformLocation("shineDamper")
        location_reflectivity = super.getUniformLocation("reflectivity")
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
    fun loadViewMatrix(camera: Camera) {
        val viewMatrix = createViewMatrix(camera)
        super.loadMatrix(location_viewMatrix, viewMatrix)
    }

    fun loadLight(light: Light) {
        super.loadVector(location_lightPosition, light.position)
        super.loadVector(location_lightColor, light.color)
    }

    fun loadShineVariables(damper: Float, reflectivity: Float) {
        super.loadFloat(location_shineDamper, damper)
        super.loadFloat(location_reflectivity, reflectivity)
    }
}