attribute vec3 position;
attribute vec2 textureCoordinate;
attribute vec3 normal;

varying vec2 passTextureCoordinate;

uniform mat4 transformationMatrix;
uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;

void main() {

    vec4 worldPosition = transformationMatrix * vec4(position, 1.0);
    gl_Position = projectionMatrix * viewMatrix * worldPosition;

    passTextureCoordinate = textureCoordinate;
}