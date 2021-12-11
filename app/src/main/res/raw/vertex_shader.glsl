uniform mat4 uMVPMatrix;
attribute vec3 position;

void main() {
    gl_Position = uMVPMatrix * vec4(position, 1.0);
}