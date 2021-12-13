package com.oscarcreator.model3dview.renderengine

import android.content.Context
import com.oscarcreator.model3dview.models.RawModel
import com.oscarcreator.model3dview.util.Vector2f
import com.oscarcreator.model3dview.util.Vector3f
import java.io.BufferedReader
import java.io.InputStreamReader

fun loadObjModel(context: Context, resourceId: Int, loader: Loader): RawModel {
    val inputStream = context.resources.openRawResource(resourceId)

    val inputStreamReader = InputStreamReader(inputStream)
    val reader = BufferedReader(inputStreamReader)

    var line: String?

    val vertices = mutableListOf<Vector3f>()
    val textures = mutableListOf<Vector2f>()
    val normals = mutableListOf<Vector3f>()
    val indices = mutableListOf<Int>()

    var verticesArray: FloatArray
    var normalsArray = FloatArray(0)
    var textureArray = FloatArray(0)

    try {
        while(true) {
            line = reader.readLine()
            val currentLine = line.split(" ")
            if (line.startsWith("v ")) {
                val vertex = Vector3f(
                    currentLine[1].toFloat(),
                    currentLine[2].toFloat(),
                    currentLine[3].toFloat()
                )
                vertices.add(vertex)
            } else if (line.startsWith("vt ")) {
                val texture = Vector2f(
                    currentLine[1].toFloat(),
                    currentLine[2].toFloat()
                )
                textures.add(texture)
            } else if(line.startsWith("vn ")) {
                val normal = Vector3f(
                    currentLine[1].toFloat(),
                    currentLine[2].toFloat(),
                    currentLine[3].toFloat()
                )
                normals.add(normal)
            } else if(line.startsWith("f ")) {
                textureArray = FloatArray(vertices.size * 2)
                normalsArray = FloatArray(vertices.size * 3)
                break
            }
        }

        while (line != null) {
            if (!line.startsWith("f")) {
                line = reader.readLine()
                continue
            }
            val currentLine = line.split(" ")
            val vertex1 = currentLine[1].split("/")
            val vertex2 = currentLine[2].split("/")
            val vertex3 = currentLine[3].split("/")

            processVertex(vertex1, indices, textures, normals, textureArray, normalsArray)
            processVertex(vertex2, indices, textures, normals, textureArray, normalsArray)
            processVertex(vertex3, indices, textures, normals, textureArray, normalsArray)
            line = reader.readLine()
        }
        reader.close()

    } catch (e: Exception) {
        e.printStackTrace()
    }

    verticesArray = FloatArray(vertices.size * 3)

    var vertexPointer = 0
    for (vertex in vertices) {
        verticesArray[vertexPointer++] = vertex.x
        verticesArray[vertexPointer++] = vertex.y
        verticesArray[vertexPointer++] = vertex.z
    }

    val indicesArray = indices.toIntArray()

    return loader.loadToVAO(verticesArray, textureArray, normalsArray, indicesArray)
}

private fun processVertex(
    vertexData: List<String>,
    indices: MutableList<Int>,
    textures: List<Vector2f>,
    normals: List<Vector3f>,
    textureArray: FloatArray,
    normalsArray: FloatArray
) {

    val currentVertexPointer = vertexData[0].toInt().minus(1)
    indices.add(currentVertexPointer)

    val currentTex = textures.get(vertexData[1].toInt().minus(1))
    textureArray[currentVertexPointer * 2] = currentTex.x
    textureArray[currentVertexPointer * 2 + 1] = 1 - currentTex.y

    val currentNorm = normals.get(vertexData[2].toInt().minus(1))
    normalsArray[currentVertexPointer * 3] = currentNorm.x
    normalsArray[currentVertexPointer * 3 + 1] = currentNorm.y
    normalsArray[currentVertexPointer * 3 + 2] = currentNorm.z

}