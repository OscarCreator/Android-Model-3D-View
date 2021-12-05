package com.oscarcreator.android3dmodelview

import android.opengl.GLES20
import android.opengl.GLSurfaceView
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class BasicRenderer() : GLSurfaceView.Renderer {

    override fun onSurfaceCreated(p0: GL10?, p1: EGLConfig?) {
        // set the background frame color
        GLES20.glClearColor(0f, 1f, 0f,1f)
    }

    // Called when screen size is changed
    override fun onSurfaceChanged(unused: GL10?, width: Int, height: Int) {
        // Set opengl viewport to fill entire surface
        GLES20.glViewport(0,0, width, height)
    }

    override fun onDrawFrame(p0: GL10?) {
        // redraw background color
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)
    }
}