#version 400 core

layout (location=0) in vec3 position;
layout (location=1) in vec2 textureCoords;
layout (location=2) in vec3 normal;
layout (location=3) in mat4 globalTransformationMatrix;

out vec2 pass_textureCoords;
out vec3 surfaceNormal;
out vec3 realNormal;
out vec3 toLightVector[10];
out vec3 toCameraVector;

out float visibility;

uniform mat4 transformationMatrix;
uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform vec3 lightPosition[10];
uniform float useFakeLighting;
uniform bool isInstanced;

uniform float numberOfRows;
uniform vec2 offset;

uniform vec4 plane;

const float density = 0;
const float gradient = 5.0;


void main(void) {
    vec4 worldPosition = vec4(0, 0, 0, 0);
    realNormal = normal;
    vec3 actualNormal = normal;

    if (useFakeLighting > 0.5) {
        actualNormal = vec3(0.0, 1.0, 0.0);
    }

    if (isInstanced) {
        worldPosition = globalTransformationMatrix * vec4(position, 1.0);
        surfaceNormal = (globalTransformationMatrix * vec4(actualNormal, 0.0)).xyz;
    } else {
        worldPosition = transformationMatrix * vec4(position, 1.0);
        surfaceNormal = (transformationMatrix * vec4(actualNormal, 0.0)).xyz;
    }
    gl_ClipDistance[0] = dot(worldPosition, plane);

    vec4 positionRelativeToCam = viewMatrix * worldPosition;

    gl_Position = projectionMatrix * positionRelativeToCam;

    pass_textureCoords = (textureCoords / numberOfRows) + offset;

    for (int i = 0; i < 10; i++) {
        toLightVector[i] = lightPosition[i] - worldPosition.xyz;
    }
    toCameraVector = (inverse(viewMatrix) * vec4(0.0, 0.0, 0.0, 1.0)).xyz - worldPosition.xyz;

    float distance = length(positionRelativeToCam.xyz);
    visibility = exp(-pow((distance * density), gradient));
    visibility = clamp(visibility, 0.0, 1.0);

    visibility = 1;
}