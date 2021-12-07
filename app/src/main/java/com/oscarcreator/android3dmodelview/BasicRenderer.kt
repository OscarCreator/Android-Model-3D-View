package com.oscarcreator.android3dmodelview

import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import android.os.SystemClock
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class BasicRenderer() : GLSurfaceView.Renderer {

    private lateinit var triangle: Triangle

    private val modelViewProjectionMatrix = FloatArray(16)
    private val projectionMatrix = FloatArray(16)
    private val viewMatrix = FloatArray(16)
    private val rotationMatrix = FloatArray(16)

    // renderer code runs on a separate thread
    @Volatile
    var angle = 0f

    override fun onSurfaceCreated(p0: GL10?, p1: EGLConfig?) {
        // set the background frame color
        GLES20.glClearColor(0f, 1f, 0f,1f)

        triangle = Triangle()
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

        // draw shape
        triangle.draw(scratch)
    }
}

fun loadShader(type: Int, shaderCode: String): Int {
    // vertex shader type: GLES20.GL_VERTEX_SHADER
    // vertex shader type: GLES20.GL_FRAGMENT_SHADER
    return GLES20.glCreateShader(type).also { shader ->
        // add source code to shade and compile it
        GLES20.glShaderSource(shader, shaderCode)
        GLES20.glCompileShader(shader)
    }
}

const val COORDS_PER_VERTEX = 3
var triangleCoords = floatArrayOf(
    0f, 0.622f, 0f,  // top
    -0.5f, -0.311f, 0f, // left
    0.5f, -0.311f, 0f   // right
)

class Triangle {

    // r, g, b, alpha
    val color = floatArrayOf(0.34567f, 0.4567854f, 0.289348f, 1f)

    private var vertexBuffer: FloatBuffer =
        // 1 float = 4 bytes
        ByteBuffer.allocateDirect(triangleCoords.size * 4).run {
            // use device hardware's native byte order
            order(ByteOrder.nativeOrder())

            asFloatBuffer().apply {
                // add triangle to buffer
                put(triangleCoords)
                // set the buffer to read the first coordinate
                position(0)
            }

        }

    private var program: Int

    private var vPositionHandle: Int = 0
    private var vColorHandle: Int = 0
    private var vPMatrixHandle: Int = 0

    private val vertexCount: Int = triangleCoords.size / COORDS_PER_VERTEX
    private val vertexStride: Int = COORDS_PER_VERTEX * 4 // 4 bytes per vertex

    private val vertexShaderCode = """
        uniform mat4 uMVPMatrix;
        attribute vec4 vPosition;
        void main() {
            gl_Position = uMVPMatrix * vPosition;
        }
    """.trimIndent()

    private val fragmentShaderCode = """
        precision mediump float;
        uniform vec4 vColor;
        void main() {
            gl_FragColor = vColor;
        }
    """.trimIndent()

    init {

        val vertexShader: Int = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode)
        val fragmentShader: Int = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode)
        program = GLES20.glCreateProgram().also {
            // add vertex shader to program
            GLES20.glAttachShader(it, vertexShader)
            // add fragment shader to program
            GLES20.glAttachShader(it, fragmentShader)
            // creates opengl program executables
            GLES20.glLinkProgram(it)
        }
    }

    fun draw(modelViewProjectionMatrix: FloatArray) {
        GLES20.glUseProgram(program)

        vPositionHandle = GLES20.glGetAttribLocation(program, "vPosition").also { positionHandle ->
            // enable a handle to the triangle vertices
            GLES20.glEnableVertexAttribArray(positionHandle)

            // prepare the triangle coordinate data
            GLES20.glVertexAttribPointer(
                positionHandle,
                COORDS_PER_VERTEX,
                GLES20.GL_FLOAT,
                false,
                vertexStride,
                vertexBuffer
            )

            // get handle to fragment shader's color memeber
            vColorHandle = GLES20.glGetUniformLocation(program, "vColor").also { vColorHandle ->
                // set color for drawing triangle
                GLES20.glUniform4fv(vColorHandle, 1, color, 0)

            }

            // get handle to shape's transformation matrix
            vPMatrixHandle = GLES20.glGetUniformLocation(program, "uMVPMatrix")

            // pass the projection and view transformation to the shader
            GLES20.glUniformMatrix4fv(vPMatrixHandle, 1, false, modelViewProjectionMatrix, 0)

            // draw the triangle
            GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertexCount)

            // disable vertex array
            GLES20.glDisableVertexAttribArray(positionHandle)


        }
    }
}