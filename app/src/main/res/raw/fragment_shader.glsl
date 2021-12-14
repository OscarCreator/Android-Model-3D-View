precision mediump float;

varying vec2 passTextureCoordinate;

uniform sampler2D textureSampler;
uniform vec3 color;

void main() {

    gl_FragColor = texture2D(textureSampler, passTextureCoordinate);
}