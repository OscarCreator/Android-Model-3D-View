package com.oscarcreator.model3dview.util

import android.opengl.Matrix
import com.oscarcreator.model3dview.entities.Camera

fun createViewMatrix(camera: Camera): FloatArray {
    val viewMatrix = FloatArray(16)
    Matrix.setIdentityM(viewMatrix, 0)

    Matrix.rotateM(viewMatrix, 0, camera.pitch, 1f, 0f, 0f)
    Matrix.rotateM(viewMatrix, 0, camera.yaw, 0f, 1f, 0f)
    Matrix.rotateM(viewMatrix, 0, camera.roll, 0f, 0f, 1f)
    Matrix.translateM(viewMatrix, 0, -camera.position.x, -camera.position.y, -camera.position.z)
    return viewMatrix
}