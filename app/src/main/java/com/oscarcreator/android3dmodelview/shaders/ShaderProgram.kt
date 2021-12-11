package com.oscarcreator.android3dmodelview.shaders

import android.content.Context
import android.opengl.GLES30
import android.util.Log
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.lang.StringBuilder
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.system.exitProcess

/**
 * A class which has the responsibility to create a shader program and
 * load data into variables in the shaders.
 * */
abstract class ShaderProgram(context: Context, vertexShaderResourceId: Int, fragmentShaderResourceId: Int) {

    companion object {
        const val TAG = "ShaderProgram"
    }

    /** The id of the shader program */
    private val programId: Int
    /** The id of the vertex shader which is attached to the shader program */
    private val vertexShaderId: Int
    /** The id of the fragment shader which is attached to the shader program */
    private val fragmentShaderId: Int

    /** Buffer object to load matrix data to uniform */
    private val matrixBuffer = ByteBuffer.allocateDirect(16 shl 2).order(ByteOrder.nativeOrder()).asFloatBuffer()

    init {

        vertexShaderId = loadShader(context, vertexShaderResourceId, GLES30.GL_VERTEX_SHADER)
        fragmentShaderId = loadShader(context, fragmentShaderResourceId, GLES30.GL_FRAGMENT_SHADER)

        // Creates an empty program object and returns it's id. Shader objects can be
        // attached to this program object.
        programId = GLES30.glCreateProgram()

        // attaches shaders to program
        GLES30.glAttachShader(programId, vertexShaderId)
        GLES30.glAttachShader(programId, fragmentShaderId)

        bindAttributes()

        // link program by creating executables for both vertex and fragment shader.
        // All uniform variables will be assigned a id and can be queried with glGetUniformLocation()
        GLES30.glLinkProgram(programId)
        // validates the program and stores information in the program's log
        GLES30.glValidateProgram(programId)

        getAllUniformLocations()
    }

    /**
     * Installs the program to make it available for rendering.
     * */
    fun useProgram() {
        GLES30.glUseProgram(programId)
    }

    /**
     * Uninstalls the program from rendering
     * */
    fun stop() {
        GLES30.glUseProgram(0)
    }

    /**
     * Detaches and deletes shaders. Then deletes the shader program.
     * */
    fun cleanUp() {
        stop()

        // detach shaders from program
        GLES30.glDetachShader(programId, vertexShaderId)
        GLES30.glDetachShader(programId, fragmentShaderId)

        // deletes the shaders. Only possible after detach
        GLES30.glDeleteShader(vertexShaderId)
        GLES30.glDeleteShader(fragmentShaderId)

        // delete program. frees memory and invalidates program
        GLES30.glDeleteProgram(programId)
    }

    protected abstract fun getAllUniformLocations()

    protected abstract fun bindAttributes()

    /**
     * Used to associate a user defined attribute variable with a vertex attribute index.
     *
     * @param variableName is the name of the variable in the shader
     * @param attribute is the index of the attribute to bind to the given variable
     * */
    protected fun bindAttribute(attribute: Int, variableName: String) {
        GLES30.glBindAttribLocation(programId, attribute, variableName)
    }

    /**
     * Looks up the int representing the location of the passed uniform by name.
     *
     * @param uniformName the name of the uniform in the shader
     * @return the int representing the location of the uniform
     * */
    protected fun getUniformLocation(uniformName: String): Int {
        return GLES30.glGetUniformLocation(programId, uniformName)
    }

    /**
     * Modifies the uniform in the passed location to the value in floatArray.
     *
     * @param location the location of the uniform to be modified.
     * @param floatArray the value the uniform will be changed to.
     * */
    protected fun loadMatrix(location: Int, floatArray: FloatArray) {
        matrixBuffer.put(floatArray)
        matrixBuffer.flip()
        // location
        // number of matrices to be modified
        // whether to transpose the matrix
        // value to change to
        GLES30.glUniformMatrix4fv(location, 1, false, matrixBuffer)
    }


}

fun loadShader(context: Context, resourceId: Int, type: Int): Int {
    val shaderCode = StringBuilder()

    // read file and append text to string
    try {
        val inputStream = context.resources.openRawResource(resourceId)
        val inputStreamReader = InputStreamReader(inputStream)

        val bufferedReader = BufferedReader(inputStreamReader)
        var line: String?

        while ((bufferedReader.readLine().also { line = it }) != null) {
            shaderCode.append(line).append("\n")
        }

        bufferedReader.close()
    } catch (e: IOException) {
        e.printStackTrace()
        exitProcess(-1);
    }

    val shaderId = GLES30.glCreateShader(type)

    // upload shader code to shader object
    GLES30.glShaderSource(shaderId, shaderCode.toString())

    // compiles shader code in object to executable
    GLES30.glCompileShader(shaderId)

    // get shader compilation status
    val compileStatus = IntArray(1)
    GLES30.glGetShaderiv(shaderId, GLES30.GL_COMPILE_STATUS, compileStatus, 0)

    Log.d(ShaderProgram.TAG, "Result of compiling source:\n${shaderCode}\n:${GLES30.glGetShaderInfoLog(shaderId)}")

    // verify shader compiled successfully
    if (compileStatus[0] == 0) {
        // delete shader object if compilation failed
        GLES30.glDeleteShader(shaderId)

        Log.w(ShaderProgram.TAG, "Compilation of shader failed")
        return 0
    }

    return shaderId

}