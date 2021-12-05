package com.oscarcreator.android3dmodelview


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity() {

    private lateinit var glSurfaceView: Model3DView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        glSurfaceView = Model3DView(this)

        setContentView(glSurfaceView)

    }
}