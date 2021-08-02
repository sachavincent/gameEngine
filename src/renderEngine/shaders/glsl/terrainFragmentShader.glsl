#version 400 core

#define MAX_LIGHTS 10

in vec2 pass_textureCoords;
in vec3 surfaceNormal;
in vec3 toLightVector[MAX_LIGHTS];
in vec3 toCameraVector;
in float visibility;
in vec4 worldPosition;
in float yLevel;
out vec4 out_Color;

uniform sampler2D backgroundTexture;
uniform sampler2D rTexture;
uniform sampler2D gTexture;
uniform sampler2D bTexture;
uniform sampler2D blendMap;
uniform bool uniformColor;
uniform vec2 terrainSize;

uniform int focusBuildingPlacement;
uniform vec3 centerFocus[100];
uniform int radiusFocus[100];

uniform vec3 lightColor[MAX_LIGHTS];
uniform vec3 attenuation[MAX_LIGHTS];
uniform float shineDamper;
uniform float reflectivity;
uniform vec3 skyColor;
uniform vec2 hoveredCell;

float manhattanDistance(float p1, float p2) {
    float d1 = abs(p1 - p2);
    return d1;
}

float manhattanDistance(vec2 p1, vec2 p2) {
    float d1 = abs(p1.x - p2.x);
    float d2 = abs(p1.y - p2.y);
    return d1 + d2;
}

float manhattanDistance(vec3 p1, vec3 p2) {
    float d1 = abs(p1.x - p2.x);
    float d2 = abs(p1.y - p2.y);
    float d3 = abs(p1.z - p2.z);
    return d1 + d2 + d3;
}
void main() {
    vec3 totalDiffuse = vec3(0.0);
    vec3 totalSpecular = vec3(0.0);
    vec3 unitNormal = surfaceNormal;
    vec3 unitVectorToCamera = normalize(toCameraVector);

    //    vec4 blendMapColor = texture(blendMap, pass_textureCoords);

    //    float backTextureAmount = 1 - (blendMapColor.r + blendMapColor.g + blendMapColor.b);


    //    if (uniformColor) {
    //        out_Color = texture(rTexture, tiledCoords) * blendMapColor.r;
    //    } else {
    //    vec4 backgroundTextureColor = texture(backgroundTexture, tiledCoords) * backTextureAmount;
    //    vec4 rTextureColor = texture(rTexture, tiledCoords) * blendMapColor.r;
    //    vec4 gTextureColor = texture(gTexture, tiledCoords) * blendMapColor.g;
    //    vec4 bTextureColor = texture(bTexture, tiledCoords) * blendMapColor.b;

    //        vec4 totalColor = backgroundTextureColor + rTextureColor + gTextureColor + bTextureColor;
    vec4 totalColor = vec4(0, 1, 0, 1);
    for (int i = 0; i < MAX_LIGHTS; i++) {
        float dist = length(toLightVector[i]);
        float attFactor = attenuation[i].x + (attenuation[i].y * dist) + (attenuation[i].z * dist * dist);

        vec3 unitLightVector = normalize(toLightVector[i]);
        float nDotl = dot(unitNormal, unitLightVector);

        vec3 lightDirection = -unitLightVector;
        vec3 reflectedLightDirection = reflect(lightDirection, unitNormal);

        float specularFactor = dot(reflectedLightDirection, unitVectorToCamera);
        specularFactor = max(specularFactor, 0.0);
        float dampedFactor = pow(specularFactor, shineDamper);

        vec3 color = lightColor[i];
        float brightness = clamp(nDotl, 0.0, 1.0) / 2.0;

        totalDiffuse = totalDiffuse + (brightness * color) / attFactor;
        //            totalSpecular = totalSpecular + (dampedFactor * reflectivity * color) / attFactor;
    }

    totalDiffuse = max(totalDiffuse, 0.1);

    //    out_Color = vec4(totalDiffuse, 1.0) * totalColor/* + vec4(totalSpecular, 1.0)*/;
    //    out_Color = vec4(totalDiffuse, 1.0) * texture(backgroundTexture, pass_textureCoords);
    out_Color = texture(backgroundTexture, pass_textureCoords);
    //        out_Color = mix(vec4(skyColor, 1.0), out_Color, visibility);

    //        out_Color = totalColor;
    //    }

    //    out_Color = vec4(surfaceNormal * 0.5 + vec3(.5), 1.0);


    //        out_Color = mix(vec4(0, 1, 0, 1), vec4(1, 0, 0, 1), yLevel / 32.0);
    //    out_Color = vec4(1, 0, 0, 1);
}