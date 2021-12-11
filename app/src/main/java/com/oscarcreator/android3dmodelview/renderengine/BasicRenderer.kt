package com.oscarcreator.android3dmodelview.renderengine

import android.content.Context
import android.opengl.GLES20
import android.opengl.GLES30
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import com.oscarcreator.android3dmodelview.models.RawModel
import com.oscarcreator.android3dmodelview.shaders.StaticShader
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class BasicRenderer(private val context: Context) : GLSurfaceView.Renderer {

    private val modelViewProjectionMatrix = FloatArray(16)
    private val projectionMatrix = FloatArray(16)
    private val viewMatrix = FloatArray(16)
    private val rotationMatrix = FloatArray(16)


    private lateinit var shader: StaticShader
    private lateinit var model: RawModel
    private lateinit var loader: Loader

    // renderer code runs on a separate thread
    @Volatile
    var angle = 0f

    override fun onSurfaceCreated(p0: GL10?, p1: EGLConfig?) {
        // set the background frame color
        GLES20.glClearColor(0f, 1f, 0f,1f)

        shader = StaticShader(context)
        loader = Loader()
        model = loader.loadToVAO(triangleCoords, intArrayOf(0, 1, 2))
    }

    // Called when screen size is changed
    override fun onSurfaceChanged(unused: GL10?, width: Int, height: Int) {
        // Set opengl viewport to fill entire surface
        GLES20.glViewport(0,0, width, height)

        val ratio: Float = width.toFloat() / height.toFloat()

        // this projection matrix is applied to object coordinates
        // in the onDrawFrame() method
        Matrix.frustumM(projectionMatrix, 0, -ratio, ratio, -1f, 1f, 3f, 7f)
    }

    override fun onDrawFrame(p0: GL10?) {
        // redraw background color
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)

        // set the camera position: View matrix
        Matrix.setLookAtM(viewMatrix, 0, 0f, 0f, 3f, 0f, 0f, 0f, 0f, 1f, 0f)

        // calculate the projection and view transformation
        Matrix.multiplyMM(modelViewProjectionMatrix, 0, projectionMatrix, 0, viewMatrix, 0)

        val scratch = FloatArray(16)

        // create a rotation transformation for the triangle
        //val time = SystemClock.uptimeMillis() % 4000L
        //val angle = 0.090f * time.toInt()
        Matrix.setRotateM(rotationMatrix, 0, angle, 0f, 0f, -1f)

        // combine the rotation matrix with projection and camera view
        Matrix.multiplyMM(scratch, 0, modelViewProjectionMatrix, 0, rotationMatrix, 0)

        shader.useProgram()

        shader.loadViewModelProjectionMatrix(scratch)

        prepareModel(model)

        GLES20.glDrawElements(GLES20.GL_TRIANGLES, model.vertexCount, GLES20.GL_UNSIGNED_INT, 0)

        unbindModel()

        shader.stop()
    }

    private fun prepareModel(model: RawModel) {
        GLES30.glBindVertexArray(model.vaoId)
        GLES30.glEnableVertexAttribArray(POSITION_VBO_LOCATION)
    }

    private fun unbindModel() {
        GLES30.glDisableVertexAttribArray(POSITION_VBO_LOCATION)

        GLES30.glBindVertexArray(0)
    }
}

var triangleCoords = floatArrayOf(
    0f, 0.622f, 0f,  // top
    -0.5f, -0.311f, 0f, // left
    0.5f, -0.311f, 0f   // right
)