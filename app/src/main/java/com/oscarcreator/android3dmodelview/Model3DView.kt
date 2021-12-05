package com.oscarcreator.android3dmodelview

import android.content.Context
import android.opengl.GLSurfaceView

class Model3DView(context: Context): GLSurfaceView(context) {

    private val renderer: BasicRenderer

    init {
        setEGLContextClientVersion(3)

        //Only draw the view when drawing data is changed
        renderer = BasicRenderer()

        setRenderer(renderer)

        renderMode = RENDERMODE_WHEN_DIRTY
    }
}