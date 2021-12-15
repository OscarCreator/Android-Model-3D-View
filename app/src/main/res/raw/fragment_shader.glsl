precision mediump float;

varying vec2 passTextureCoordinate;

varying vec3 surfaceNormal;
varying vec3 toLightVector;
varying vec3 toCameraVector;

uniform sampler2D textureSampler;

uniform vec3 lightColor;
uniform float shineDamper;
uniform float reflectivity;

void main() {

    /* calc color based on light and position */

    // normalize surface normal and light vector
    vec3 unitNormal = normalize(surfaceNormal);
    vec3 unitLightVector = normalize(toLightVector);
    // dot product is a number describing how much of a vector
    // is on the other when projected onto it.
    float nDot1 = dot(unitNormal, unitLightVector);
    // set the brightness according to the dot product.
    // the minimum value is the ambient brightness
    float brightness = max((nDot1 + 1.0) * 0.5, 0.2) * 0.9;
    // the color of the light is changed with the brightness
    vec3 diffuse = brightness * lightColor;

    /** calc specular lighting */

    // normalize vector
    vec3 unitVectorToCamera = normalize(toCameraVector);
    // normalized vector from light to fragment
    vec3 lightDirection = -unitLightVector;
    // reflect light based on fragment normal to see where it would
    // end up if it got reflected by the surface
    vec3 reflectedLightDirection = reflect(lightDirection, unitNormal);
    // a value from -1 to 1 describing how close the reflected light would point at the camera
    float specularFactor = dot(reflectedLightDirection, unitVectorToCamera);
    // remove irrelevant data (90 degrees or more)
    specularFactor = max(specularFactor, 0.0);
    // because light is most intense right point
    float dampedFactor = pow(specularFactor, shineDamper);
    // convert to light
    vec3 finalSpecular = dampedFactor * reflectivity * lightColor;

    gl_FragColor = vec4(diffuse, 1.0) * texture2D(textureSampler, passTextureCoordinate) + vec4(finalSpecular, 1.0);
}