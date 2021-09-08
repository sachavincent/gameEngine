#version 400 core

#define MAX_LIGHTS 10

const int MAX_JOINTS = 50;//max joints allowed in a skeleton
const int MAX_WEIGHTS = 3;//max number of joints that can affect a vertex

layout (location=0) in vec3 position;
layout (location=1) in vec2 textureCoords;
layout (location=2) in vec3 normal;
layout (location=3) in vec3 tangent;
layout (location=4) in ivec3 jointIndices;
layout (location=5) in vec3 weights;
layout (location=6) in mat4 globalTransformationMatrix;

out vec2 pass_textureCoords;
out vec3 toLightVectors[MAX_LIGHTS];
out vec3 toCameraVector;
out vec3 surfaceNormal;
out float visibility;

uniform mat4 jointTransforms[MAX_JOINTS];
uniform mat4 transformationMatrix;
uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform vec3 lightsPosition[MAX_LIGHTS];
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
    vec3 actualNormal = normal;
    vec4 in_pos = vec4(position, 1.0);

    {
        vec4 totalLocalPos = vec4(0.0);
        vec4 totalNormal = vec4(0.0);

        for (int i = 0; i < MAX_WEIGHTS; i++) {
            mat4 jointTransform = jointTransforms[jointIndices[i]];
            vec4 posePosition = jointTransform * in_pos;
            totalLocalPos += posePosition * weights[i];

            vec4 worldNormal = jointTransform * vec4(normal, 0.0);
            totalNormal += worldNormal * weights[i];
        }

        if (totalLocalPos == vec4(0.0)) {
            totalLocalPos = in_pos;
        } else {
            actualNormal = totalNormal.xyz;
        }
        //        gl_Position = projectionMatrix * totalLocalPos;
        in_pos = totalLocalPos;
    }
    vec4 worldPosition;
    mat4 modelViewMatrix;
    if (useFakeLighting) {
        surfaceNormal = vec3(0.0, 1.0, 0.0);
    }
    if (isInstanced) {
        worldPosition = globalTransformationMatrix * in_pos;
        modelViewMatrix = viewMatrix * globalTransformationMatrix;
        if (!useFakeLighting) {
            surfaceNormal = (globalTransformationMatrix * vec4(actualNormal, 0.0)).xyz;
        }
    } else {
        worldPosition = transformationMatrix * in_pos;
        modelViewMatrix = viewMatrix * transformationMatrix;
        if (!useFakeLighting) {
            surfaceNormal = (transformationMatrix * vec4(actualNormal, 0.0)).xyz;
        }
    }

    if (useNormalMap && !useFakeLighting) {
        surfaceNormal = (modelViewMatrix * vec4(actualNormal, 0.0)).xyz;
    }
    gl_ClipDistance[0] = dot(worldPosition, plane);

    vec4 positionRelativeToCam;
    if (useNormalMap) {
        positionRelativeToCam = modelViewMatrix * in_pos;
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
            toLightVectors[i] = toTangentSpace * (lightsPosition[i] - positionRelativeToCam.xyz);
        }
        toCameraVector = toTangentSpace * (-positionRelativeToCam.xyz);
    } else {
        for (int i = 0; i < MAX_LIGHTS; i++) {
            toLightVectors[i] = lightsPosition[i] - worldPosition.xyz;
        }
        toCameraVector = (inverse(viewMatrix) * vec4(0.0, 0.0, 0.0, 1.0)).xyz - worldPosition.xyz;
    }

    //    float distance = length(positionRelativeToCam.xyz);
    //    visibility = exp(-pow((distance * density), gradient));
    //    visibility = clamp(visibility, 0.0, 1.0);

    visibility = 1;
}