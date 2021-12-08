package com.oscarcreator.android3dmodelview.enginetester


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.oscarcreator.android3dmodelview.Model3DView

class MainActivity : AppCompatActivity() {

    private lateinit var glSurfaceView: Model3DView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        glSurfaceView = Model3DView(this)

        setContentView(glSurfaceView)

    }
}