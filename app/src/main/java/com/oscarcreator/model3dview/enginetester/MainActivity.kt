package com.oscarcreator.model3dview.enginetester


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.oscarcreator.model3dview.Model3DView

class MainActivity : AppCompatActivity() {

    private lateinit var glSurfaceView: Model3DView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        glSurfaceView = Model3DView(this)

        setContentView(glSurfaceView)

    }
}