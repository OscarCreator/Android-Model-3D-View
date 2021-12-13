attribute vec3 position;
attribute vec2 textureCoordinate;
attribute vec3 normal;

varying vec3 nColor;

uniform mat4 transformationMatrix;
uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;

void main() {

    nColor = abs(normalize(normal));

    vec4 worldPosition = transformationMatrix * vec4(position, 1.0);

    gl_Position = projectionMatrix * viewMatrix * worldPosition;
}