package com.oscarcreator.model3dview.entities

import com.oscarcreator.model3dview.util.Vector3f

class Camera(
    val position: Vector3f = Vector3f(0f,0f,0f),
    var pitch: Float = 0f,
    var yaw: Float = 0f,
    var roll: Float = 0f
) {
}