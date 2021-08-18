#version 400 core

#define MAX_LIGHTS 10
#define MIN_HEIGHT_EDGE -10 // Lowest height of edges
#define DEFAULT_MAX_HEIGHT 32.0

invariant gl_Position;

layout (location = 0) in ivec2 position;

out vec3 surfaceNormal;
out vec3 toLightVector[MAX_LIGHTS];
out vec3 toCameraVector;
out vec4 worldPosition;
out float visibility;
out vec3 pass_pos;

uniform mat4 transformationMatrix;
uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform vec3 lightPosition[MAX_LIGHTS];

uniform float maxHeight = DEFAULT_MAX_HEIGHT;
uniform sampler2DRect heightMap;
uniform ivec2 terrainSize;

uniform vec4 plane;

const float density = 0;
const float gradient = 5.0;

float getHeight(int x, int z) {
    if (x < 0 || z < 0 || x > terrainSize.x - 1 || z > terrainSize.y - 1) {
        return 0;
    }

    vec4 texel = texelFetch(heightMap, ivec2(x, z));

    return texel.r;
}

vec3 calculateNormal(int x, int z) {
    float heightL = getHeight(x - 1, z);
    float heightR = getHeight(x + 1, z);
    float heightD = getHeight(x, z - 1);
    float heightU = getHeight(x, z + 1);
    vec3 normal = vec3(heightL - heightR, 2, heightD - heightU);

    return normalize(normal);
}

void main(void) {
    float height = MIN_HEIGHT_EDGE;
    ivec2 finalPosition = position;
    bool edge = false;
    if (position.x < 0 || position.x > terrainSize.x - 1) {
        // Edge
        edge = true;
        finalPosition.x = clamp(position.x, 0, terrainSize.x - 1);
    }
    if (position.y < 0 || position.y > terrainSize.y - 1) {
        // Edge
        edge = true;
        finalPosition.y = clamp(position.y, 0, 127);
    }

    if(!edge) {
        height = getHeight(position.x, position.y) * maxHeight;
    }

    vec3 pos = vec3(finalPosition.x, height, finalPosition.y);
    worldPosition = transformationMatrix * vec4(pos, 1.0);

    gl_ClipDistance[0] = dot(worldPosition, plane);

    vec4 positionRelativeToCam = viewMatrix * worldPosition;

    gl_Position = projectionMatrix * positionRelativeToCam;

    surfaceNormal = (transformationMatrix * vec4(calculateNormal(finalPosition.x, finalPosition.y), 0.0)).xyz;
    for (int i = 0; i < MAX_LIGHTS; i++) {
        toLightVector[i] = lightPosition[i] - worldPosition.xyz;
    }

    toCameraVector = (inverse(viewMatrix) * vec4(0.0, 0.0, 0.0, 1.0)).xyz - worldPosition.xyz;


    float distance = length(positionRelativeToCam.xyz);
    visibility = exp(-pow((distance * density), gradient));
    visibility = clamp(visibility, 0.0, 1.0);
    pass_pos = pos;
}