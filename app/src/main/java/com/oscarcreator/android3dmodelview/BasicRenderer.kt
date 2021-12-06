package com.oscarcreator.android3dmodelview

import android.opengl.GLES20
import android.opengl.GLSurfaceView
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class BasicRenderer() : GLSurfaceView.Renderer {

    private lateinit var triangle: Triangle

    override fun onSurfaceCreated(p0: GL10?, p1: EGLConfig?) {
        // set the background frame color
        GLES20.glClearColor(0f, 1f, 0f,1f)

        triangle = Triangle()
    }

    // Called when screen size is changed
    override fun onSurfaceChanged(unused: GL10?, width: Int, height: Int) {
        // Set opengl viewport to fill entire surface
        GLES20.glViewport(0,0, width, height)
    }

    override fun onDrawFrame(p0: GL10?) {
        // redraw background color
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)
        triangle.draw()
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
    0f, 1f, 0f,  // top
    -1f, -0.50f, 0f, // left
    1f, 0f, 0f   // right
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

    private var positionHandle: Int = 0
    private var colorHandle: Int = 0

    private val vertexCount: Int = triangleCoords.size / COORDS_PER_VERTEX
    private val vertexStride: Int = COORDS_PER_VERTEX * 4 // 4 bytes per vertex

    private val vertextShaderCode = """
        attribute vec4 vPosition;
        void main() {
            gl_Position = vPosition;
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

        val vertexShader: Int = loadShader(GLES20.GL_VERTEX_SHADER, vertextShaderCode)
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

    fun draw() {
        GLES20.glUseProgram(program)

        positionHandle = GLES20.glGetAttribLocation(program, "vPosition").also {
            // enable a handle to the triangle vertices
            GLES20.glEnableVertexAttribArray(it)

            // prepare the triangle coordinate data
            GLES20.glVertexAttribPointer(
                it,
                COORDS_PER_VERTEX,
                GLES20.GL_FLOAT,
                false,
                vertexStride,
                vertexBuffer
            )

            // get handle to fragment shader's color memeber
            colorHandle = GLES20.glGetUniformLocation(program, "vColor").also { colorHandle
                // set color for drawing triangle
                GLES20.glUniform4fv(colorHandle, 1, color, 0)

            }

            // draw the triangle
            GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertexCount)

            // disable vertex array
            GLES20.glDisableVertexAttribArray(it)


        }
    }
}