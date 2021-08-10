#version 400 core

#define MAX_LIGHTS 10
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

uniform vec4 plane;

const float density = 0;
const float gradient = 5.0;

float getHeight(int x, int z) {
    if (x < 0 || z < 0 || x > 127 || z > 127) {
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
    float height = texture2DRect(heightMap, position).r;
    vec3 pos = vec3(position.x, height * maxHeight, position.y);
    worldPosition = transformationMatrix * vec4(pos, 1.0);

    gl_ClipDistance[0] = dot(worldPosition, plane);

    vec4 positionRelativeToCam = viewMatrix * worldPosition;

    gl_Position = projectionMatrix * positionRelativeToCam;

    surfaceNormal = (transformationMatrix * vec4(calculateNormal(position.x, position.y), 0.0)).xyz;
    for (int i = 0; i < MAX_LIGHTS; i++) {
        toLightVector[i] = lightPosition[i] - worldPosition.xyz;
    }

    toCameraVector = (inverse(viewMatrix) * vec4(0.0, 0.0, 0.0, 1.0)).xyz - worldPosition.xyz;


    float distance = length(positionRelativeToCam.xyz);
    visibility = exp(-pow((distance * density), gradient));
    visibility = clamp(visibility, 0.0, 1.0);
    pass_pos = pos;
}