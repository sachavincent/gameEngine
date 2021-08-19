#version 400 core

#define MAX_LIGHTS 10

layout (location=0) in vec3 position;
layout (location=1) in vec2 textureCoords;
layout (location=2) in vec3 normal;
layout (location=3) in vec3 tangent;
layout (location=6) in mat4 globalTransformationMatrix;

out vec2 pass_textureCoords;
out vec3 toLightVector[MAX_LIGHTS];
out vec3 toCameraVector;
out vec3 surfaceNormal;
out float visibility;

uniform mat4 transformationMatrix;
uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform vec3 lightPosition[MAX_LIGHTS];
uniform bool useFakeLighting;
uniform bool useNormalMap;
uniform bool isInstanced;
uniform bool areTangentsOn;

uniform int numberOfRows;
uniform vec2 offset;

uniform vec4 plane;

const float density = 0;
const float gradient = 5.0;

void main(void) {
    vec4 worldPosition;
    mat4 modelViewMatrix;
    vec3 actualNormal = normal;
    if (useFakeLighting) {
        surfaceNormal = vec3(0.0, 1.0, 0.0);
    }
    if (isInstanced) {
        worldPosition = globalTransformationMatrix * vec4(position, 1.0);
        modelViewMatrix = viewMatrix * globalTransformationMatrix;
        surfaceNormal = (globalTransformationMatrix * vec4(actualNormal, 0.0)).xyz;
    } else {
        worldPosition = transformationMatrix * vec4(position, 1.0);
        modelViewMatrix = viewMatrix * transformationMatrix;
        surfaceNormal = (transformationMatrix * vec4(actualNormal, 0.0)).xyz;
    }

    if (useNormalMap) {
        surfaceNormal = (modelViewMatrix * vec4(actualNormal, 0.0)).xyz;
    }
    gl_ClipDistance[0] = dot(worldPosition, plane);

    vec4 positionRelativeToCam;
    if (useNormalMap) {
        positionRelativeToCam = modelViewMatrix * vec4(position, 1.0);
    } else {
        positionRelativeToCam = viewMatrix * worldPosition;
    }
    gl_Position = projectionMatrix * positionRelativeToCam;

    pass_textureCoords = (textureCoords / numberOfRows) + offset;

    if (useNormalMap) {
        vec3 norm = normalize(surfaceNormal);
        vec3 tang = normalize((modelViewMatrix * vec4(tangent, 0.0)).xyz);
        vec3 bitang = normalize(cross(norm, tang));
        mat3 toTangentSpace = mat3(
        tang.x, bitang.x, norm.x,
        tang.y, bitang.y, norm.y,
        tang.z, bitang.z, norm.z);

        for (int i = 0; i < MAX_LIGHTS; i++){
            toLightVector[i] = toTangentSpace * (lightPosition[i] - positionRelativeToCam.xyz);
        }
        toCameraVector = toTangentSpace * (-positionRelativeToCam.xyz);
    } else {
        for (int i = 0; i < MAX_LIGHTS; i++) {
            toLightVector[i] = lightPosition[i] - worldPosition.xyz;
        }
        toCameraVector = (inverse(viewMatrix) * vec4(0.0, 0.0, 0.0, 1.0)).xyz - worldPosition.xyz;
    }

    //    float distance = length(positionRelativeToCam.xyz);
    //    visibility = exp(-pow((distance * density), gradient));
    //    visibility = clamp(visibility, 0.0, 1.0);

    visibility = 1;
}