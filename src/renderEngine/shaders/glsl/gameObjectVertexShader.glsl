#version 400 core

layout (location=0) in vec3 position;
layout (location=1) in vec2 textureCoords;
layout (location=2) in vec3 normal;
layout (location=3) in vec3 tangent;
layout (location=4) in mat4 globalTransformationMatrix;

out vec2 pass_textureCoords;
out vec3 toLightVector[10];
out vec3 toCameraVector;
out float visibility;

uniform mat4 transformationMatrix;
uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform vec3 lightPositionEyeSpace[10];
uniform float useFakeLighting;
uniform bool isInstanced;
uniform bool areTangentsOn;

uniform float numberOfRows;
uniform vec2 offset;

uniform vec4 plane;

const float density = 0;
const float gradient = 5.0;

void main(void) {
    vec4 worldPosition;

    //    if (useFakeLighting > 0.5) {
    //        actualNormal = vec3(0.0, 1.0, 0.0);
    //    }
    mat4 modelViewMatrix;
    if (isInstanced) {
        worldPosition = globalTransformationMatrix * vec4(position, 1.0);
        modelViewMatrix = viewMatrix * globalTransformationMatrix;
    } else {
        worldPosition = transformationMatrix * vec4(position, 1.0);
        modelViewMatrix = viewMatrix * transformationMatrix;
    }
    vec3 surfaceNormal = (modelViewMatrix * vec4(normal, 0.0)).xyz;

    gl_ClipDistance[0] = dot(worldPosition, plane);

    vec4 positionRelativeToCam = modelViewMatrix * vec4(position, 1.0);

    gl_Position = projectionMatrix * positionRelativeToCam;

    pass_textureCoords = (textureCoords / numberOfRows) + offset;

    mat3 toTangentSpace;
    if (areTangentsOn) {
        vec3 norm = normalize(surfaceNormal);
        vec3 tang = normalize((modelViewMatrix * vec4(tangent, 0.0)).xyz);
        vec3 bitang = normalize(cross(norm, tang));
        toTangentSpace = mat3(
        tang.x, bitang.x, norm.x,
        tang.y, bitang.y, norm.y,
        tang.z, bitang.z, norm.z);
    } else {
        toTangentSpace = mat3(
        1, 1, 1,
        1, 1, 1,
        1, 1, 1);
    }
    for (int i = 0; i < 10; i++){
        toLightVector[i] = toTangentSpace * (lightPositionEyeSpace[i] - positionRelativeToCam.xyz);
    }
    toCameraVector = toTangentSpace * (-positionRelativeToCam.xyz);
    //    toCameraVector = (inverse(viewMatrix) * vec4(0.0, 0.0, 0.0, 1.0)).xyz - worldPosition.xyz;

    //    float distance = length(positionRelativeToCam.xyz);
    //    visibility = exp(-pow((distance * density), gradient));
    //    visibility = clamp(visibility, 0.0, 1.0);

    visibility = 1;
}