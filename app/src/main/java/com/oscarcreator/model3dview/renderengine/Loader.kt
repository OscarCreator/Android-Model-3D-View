package com.oscarcreator.model3dview.renderengine

import android.content.Context
import android.graphics.BitmapFactory
import android.opengl.GLES30
import android.opengl.GLUtils
import android.util.Log
import com.oscarcreator.model3dview.models.RawModel
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.IntBuffer

const val POSITION_VBO_LOCATION = 0
const val TEXTURE_VBO_LOCATION = 1
const val NORMALS_VBO_LOCATION = 2

class Loader {

    companion object {
        const val TAG = "Loader"
    }

    /** All generated VAOs. VAO is object storing one or more VBOs (attribute lists)*/
    private val vaos = mutableListOf<Int>()
    /** All generated VBOs. VBO is a memory buffer stored in the graphics card.*/
    private val vbos = mutableListOf<Int>()
    /** All generated textures.*/
    private val textures = mutableListOf<Int>()


    /**
     * Stores the positions and indices in a VAO.
     *
     * @param positions the points of the triangles
     * @param indices the order of the points to form the triangles
     * @return a [RawModel] containing the VAO associated with the data.
     * */
    fun loadToVAO(positions: FloatArray, textureCoordinates: FloatArray, normals: FloatArray, indices: IntArray): RawModel {
        val vaoId = createVAO()

        bindIndicesBuffer(indices)

        storeDataInAttributeList(POSITION_VBO_LOCATION, 3, positions)
        storeDataInAttributeList(TEXTURE_VBO_LOCATION, 2, textureCoordinates)
        storeDataInAttributeList(NORMALS_VBO_LOCATION, 3, normals)
        unbindVAO()
        return RawModel(vaoId, indices.size)
    }

    /**
     * Creates a texture with mipmap levels from the passed resource id.
     *
     * @return the identifier for the texture
     * */
    fun loadTexture(context: Context, resourceId: Int): Int {
        val textureObjectId = IntArray(1)
        // Generate a texture identifier
        GLES30.glGenTextures(1, textureObjectId, 0)

        if (textureObjectId.first() == 0) {
            Log.i(TAG, "Could not generate a new OpenGL texture object")
            return 0
        }

        val options = BitmapFactory.Options()
        options.inScaled = false
        // decodes the image resource to a bitmap
        val bitmap = BitmapFactory.decodeResource(context.resources, resourceId, options)

        if (bitmap == null) {
            Log.i(TAG, "Resouce ID: $resourceId could not be decoded")
            GLES30.glDeleteTextures(1, textureObjectId, 0)
            return 0
        }

        // binds the texture in TEXTURE_2D target
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, textureObjectId.first())
        // defines how the texture should be minified
        // LINEAR_MIPMAP_LINEAR (trilinear) : weighted average of two mipmaps that most closely
        //  match the size of the pixel begin textured
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MIN_FILTER, GLES30.GL_LINEAR_MIPMAP_LINEAR)
        // defines how the texture should be magnified
        // LINEAR (biliear) : biliear interpolation performs linear interpolation in two directions
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MAG_FILTER, GLES30.GL_LINEAR)
        // tells OpenGL to read in the bitmap data defined by bitmap and copy it
        // over into the texture object that is currently bound
        GLUtils.texImage2D(GLES30.GL_TEXTURE_2D, 0, bitmap, 0)
        // speed up garbage collection by releasing data immediately
        bitmap.recycle()
        // generates mipmaps for all levels for the texture
        GLES30.glGenerateMipmap(GLES30.GL_TEXTURE_2D)
        // unbind texture
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, 0)

        textures.add(textureObjectId.first())
        return textureObjectId.first()

    }

    /**
     * Creates a Vertex Array Object (VAO) and binds it.
     *
     * @return the id of the created VAO
     * */
    private fun createVAO(): Int {
        val vaoId = IntArray(1)
        // Generates a VAO
        GLES30.glGenVertexArrays(1, vaoId, 0)
        vaos.add(vaoId[0])
        // Binds the VAO. This is used to store data in the VBOs in the bound VAO
        GLES30.glBindVertexArray(vaoId[0])
        return vaoId[0]
    }

    /**
     * Unbinds the Vertex Array Object (VAO) by binding invalid id.
     * */
    private fun unbindVAO() {
        GLES30.glBindVertexArray(0)
    }

    /**
     * Stores the indices in the [GLES30.GL_ELEMENT_ARRAY_BUFFER] (indices buffer).
     *
     * @param indices the indices to store in the indices buffer
     * */
    private fun bindIndicesBuffer(indices: IntArray) {
        val vboId = IntArray(1)

        // Generates a VBO identifier to store data in.
        GLES30.glGenBuffers(1, vboId, 0)
        vbos.add(vboId.first())
        // binds the VBO as GL_ELEMENT_ARRAY_BUFFER. This target is used in glDrawElements to know which indices to use together.
        GLES30.glBindBuffer(GLES30.GL_ELEMENT_ARRAY_BUFFER, vboId.first())
        val buffer = storeDataInIntBuffer(indices)
        // stores the indices in the buffer. GL_STATIC_DRAW = STATIC: modified once and used many times,
        // DRAW: data store contents are modified by application and used for GL drawing
        GLES30.glBufferData(GLES30.GL_ELEMENT_ARRAY_BUFFER, indices.size * 4, buffer, GLES30.GL_STATIC_DRAW)
    }

    /**
     * Store data in a [IntBuffer]
     *
     * @param data the data to store in memory
     * @return the buffer object the data is saved in
     * */
    private fun storeDataInIntBuffer(data: IntArray): IntBuffer {
        return ByteBuffer.allocateDirect(data.size shl 2)
            .order(ByteOrder.nativeOrder())
            .asIntBuffer().apply {
                put(data)
                flip()
            }
    }

    /**
     * Store data in a [FloatBuffer]
     *
     * @param data the data to store in memory
     * @return the buffer object the data is stored in
     * */
    private fun storeDataInFloatBuffer(data: FloatArray): FloatBuffer {
        return ByteBuffer.allocateDirect(data.size shl 2)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer().apply {
                put(data)
                flip()
            }
    }

    /**
     * Stores data in the VBO
     *
     * @param attributeNumber the identifier for the VBO
     * @param coordinateSize number of components per attribute
     * @param data the data to store in the VBO
     * */
    private fun storeDataInAttributeList(attributeNumber: Int, coordinateSize: Int, data: FloatArray) {
        val vboId = IntArray(1)
        // Generate Vertex buffer object
        GLES30.glGenBuffers(1, vboId, 0)
        vbos.add(vboId.first())
        // Bind the VBO
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, vboId[0])
        // Store data in memory
        val buffer = storeDataInFloatBuffer(data)

        // New data store is created with data.size * 4 bytes as size with the data buffer
        // GL_ARRAY_BUFFER target does not mean anything in this context but glVertexAttribPointer uses
        // whatever bound to the target
        GLES30.glBufferData(GLES30.GL_ARRAY_BUFFER, data.size * 4, buffer, GLES30.GL_STATIC_DRAW)
        // Specifies location and data format of the array of generic vertex attribute at index attributeNumber
        // coordinateSize: the number of components per attribute (1 to 4)
        GLES30.glVertexAttribPointer(attributeNumber, coordinateSize, GLES30.GL_FLOAT, false, 0, 0)
        // Unbind buffer
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, 0)
    }
}