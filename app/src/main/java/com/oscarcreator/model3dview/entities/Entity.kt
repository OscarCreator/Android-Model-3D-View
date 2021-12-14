package com.oscarcreator.model3dview.entities

import com.oscarcreator.model3dview.models.TexturedModel
import com.oscarcreator.model3dview.util.Vector3f

class Entity(
    val texturedModel: TexturedModel,
    var position: Vector3f = Vector3f(0f, 0f, 0f),
    var rotX: Float = 0f,
    var rotY: Float = 0f,
    var rotZ: Float = 0f,
    var scale: Float = 0f
) {
}