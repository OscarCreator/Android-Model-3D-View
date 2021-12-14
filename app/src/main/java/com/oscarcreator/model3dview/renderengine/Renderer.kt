package com.oscarcreator.model3dview.renderengine

import android.opengl.GLES30
import android.opengl.Matrix
import com.oscarcreator.model3dview.entities.Entity
import com.oscarcreator.model3dview.models.TexturedModel
import com.oscarcreator.model3dview.shaders.StaticShader

class Renderer(
    val width: Int,
    val height: Int,
    val shader: StaticShader
) {

    private val projectionMatrix = FloatArray(16)

    companion object {
        const val FOV: Float = 70f
        const val NEAR_PLANE: Float = 0.1f
        const val FAR_PLANE: Float = 1000f
    }

    init {
        GLES30.glEnable(GLES30.GL_CULL_FACE)
        GLES30.glCullFace(GLES30.GL_BACK)

        createProjectionMatrix()
        shader.useProgram()
        shader.loadProjectionMatrix(projectionMatrix)
        shader.stop()
    }

    fun prepare() {
        GLES30.glEnable(GLES30.GL_DEPTH_TEST)
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT or GLES30.GL_DEPTH_BUFFER_BIT)
        GLES30.glClearColor(0.5f, 1f, 1f, 1f)
    }

    fun render(entity: Entity) {
        prepareModel(entity.texturedModel)

        prepareInstance(entity)
        GLES30.glDrawElements(GLES30.GL_TRIANGLES, entity.texturedModel.model.vertexCount, GLES30.GL_UNSIGNED_INT, 0)

        unbindModel()
    }

    private fun prepareModel(model: TexturedModel) {
        GLES30.glBindVertexArray(model.model.vaoId)
        GLES30.glEnableVertexAttribArray(POSITION_VBO_LOCATION)
        GLES30.glEnableVertexAttribArray(TEXTURE_VBO_LOCATION)
        GLES30.glEnableVertexAttribArray(NORMALS_VBO_LOCATION)

        GLES30.glActiveTexture(GLES30.GL_TEXTURE0)
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, model.texture.textureId)

    }

    private fun unbindModel() {
        GLES30.glDisableVertexAttribArray(POSITION_VBO_LOCATION)
        GLES30.glDisableVertexAttribArray(TEXTURE_VBO_LOCATION)
        GLES30.glDisableVertexAttribArray(NORMALS_VBO_LOCATION)
        GLES30.glBindVertexArray(0)
    }

    private fun prepareInstance(entity: Entity) {

        val transformationMatrix = FloatArray(16)
        Matrix.setIdentityM(transformationMatrix, 0)
        Matrix.translateM(transformationMatrix, 0, entity.position.x, entity.position.y, entity.position.z)
        Matrix.rotateM(transformationMatrix, 0, entity.rotX, 1f, 0f, 0f)
        Matrix.rotateM(transformationMatrix, 0, entity.rotY, 0f, 1f, 0f)
        Matrix.rotateM(transformationMatrix, 0, entity.rotZ, 0f, 0f, 1f)

        shader.loadTransformationMatrix(transformationMatrix)
    }

    private fun createProjectionMatrix() {
        val aspectRatio = width.toFloat().div(height)

        Matrix.perspectiveM(projectionMatrix, 0, FOV, aspectRatio, NEAR_PLANE, FAR_PLANE)
    }
}