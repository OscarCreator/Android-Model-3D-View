package com.oscarcreator.android3dmodelview.shaders

import android.content.Context
import android.opengl.GLES20
import android.util.Log
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.lang.StringBuilder
import kotlin.system.exitProcess

abstract class ShaderProgram(context: Context, vertexShaderResourceId: Int, fragmentShaderResourceId: Int) {

    companion object {
        const val TAG = "ShaderProgram"
    }

    val programId: Int
    private val vertexShaderId: Int
    private val fragmentShaderId: Int

    init {

        vertexShaderId = loadShader(context, vertexShaderResourceId, GLES20.GL_VERTEX_SHADER)
        fragmentShaderId = loadShader(context, fragmentShaderResourceId, GLES20.GL_FRAGMENT_SHADER)

        // create programid
        programId = GLES20.glCreateProgram()

        // attach shaders to program
        GLES20.glAttachShader(programId, vertexShaderId)
        GLES20.glAttachShader(programId, fragmentShaderId)


        // link program
        GLES20.glLinkProgram(programId)
        // validates the program and stores information in the program's log
        GLES20.glValidateProgram(programId)

    }


    fun useProgram() {
        GLES20.glUseProgram(programId)
    }

    fun stop() {
        GLES20.glUseProgram(programId)
    }

    fun cleanUp() {
        stop()

        // detach shaders from program
        GLES20.glDetachShader(programId, vertexShaderId)
        GLES20.glDetachShader(programId, fragmentShaderId)

        // deletes the shaders. Only possible after detach
        GLES20.glDeleteShader(vertexShaderId)
        GLES20.glDeleteShader(fragmentShaderId)

        // delete program. frees memory and invalidates program
        GLES20.glDeleteProgram(programId)
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

    val shaderId = GLES20.glCreateShader(type)

    // upload shader code to shader object
    GLES20.glShaderSource(shaderId, shaderCode.toString())

    // compiles shader code in object to executable
    GLES20.glCompileShader(shaderId)

    // get shader compilation status
    val compileStatus = IntArray(1)
    GLES20.glGetShaderiv(shaderId, GLES20.GL_COMPILE_STATUS, compileStatus, 0)

    Log.d(ShaderProgram.TAG, "Result of compiling source:\n${shaderCode}\n:${GLES20.glGetShaderInfoLog(shaderId)}")

    // verify shader compiled successfully
    if (compileStatus[0] == 0) {
        // delete shader object if compilation failed
        GLES20.glDeleteShader(shaderId)

        Log.w(ShaderProgram.TAG, "Compilation of shader failed")
        return 0
    }

    return shaderId

}