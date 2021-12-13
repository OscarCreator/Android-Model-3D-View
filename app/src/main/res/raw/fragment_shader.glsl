precision mediump float;

varying vec3 nColor;

uniform vec3 color;

void main() {
    gl_FragColor = vec4(nColor, 1.0);
}