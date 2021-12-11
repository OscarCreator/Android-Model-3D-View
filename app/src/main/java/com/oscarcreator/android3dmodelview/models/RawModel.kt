package com.oscarcreator.android3dmodelview.models

/**
 * This object stores the Vertex Array Object (VAO) identifier and the vertex count.
 * @param vaoId the identifier to the object in the graphics card.
 * @param vertexCount the amount of vertices which the model has.
 * */
open class RawModel(
    val vaoId: Int,
    val vertexCount: Int
    )