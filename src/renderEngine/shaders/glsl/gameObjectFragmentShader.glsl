#version 400 core

const float ambientStrength = .34f;

in vec2 pass_textureCoords;
in vec3 toLightVector[10];
in vec3 toCameraVector;
in vec3 surfaceNormal;
in float visibility;

out vec4 out_Color;

uniform sampler2D modelTexture;
uniform sampler2D normalMap;
uniform vec3 lightColor[10];
uniform vec3 attenuation[10];
uniform float shineDamper;
uniform float reflectivity;
uniform vec3 skyColor;
uniform bool useNormalMap;

uniform float alpha;

uniform vec3 color;

float scaleLinear(float value, vec2 valueDomain) {
    return (value - valueDomain.x) / (valueDomain.y - valueDomain.x);
}

float scaleLinear(float value, vec2 valueDomain, vec2 valueRange) {
    return mix(valueRange.x, valueRange.y, scaleLinear(value, valueDomain));
}

void main() {
    // If color = (-1,-1,-1) -> texture else color
    if (color.x < 0) {
        vec3 unitNormal;
        if (useNormalMap) {
            vec4 normalMapValue = 2.0 * texture(normalMap, pass_textureCoords, -1.0) - 1.0;
            unitNormal = normalize(normalMapValue.rgb);
        } else {
            unitNormal = normalize(surfaceNormal);
        }
        vec3 unitVectorToCamera = normalize(toCameraVector);

        vec3 totalDiffuse = vec3(0.0);
        vec3 totalSpecular = vec3(0.0);

        for (int i = 0; i < 10; i++) {
            float distance = length(toLightVector[i]);
            float attFactor = attenuation[i].x + (attenuation[i].y * distance) + (attenuation[i].z * distance * distance);
            vec3 unitLightVector = normalize(toLightVector[i]);
            float nDotl = dot(unitNormal, unitLightVector);
            float brightness = clamp(nDotl, 0.0, 1.0) / 2.0;
            vec3 lightDirection = -unitLightVector;
            vec3 reflectedLightDirection = reflect(lightDirection, unitNormal);
            float specularFactor = dot(reflectedLightDirection, unitVectorToCamera);
            specularFactor = max(specularFactor, 0.0);
            float dampedFactor = pow(specularFactor, shineDamper);
            totalDiffuse = totalDiffuse + (brightness * lightColor[i]) / attFactor;
            totalSpecular = totalSpecular + (dampedFactor * reflectivity * lightColor[i]) / attFactor;
        }
        totalDiffuse = max(totalDiffuse, 0.2);

        if (useNormalMap) {
            out_Color = texture(modelTexture, pass_textureCoords, -1.0);
        } else {
            out_Color = vec4(texture(modelTexture, pass_textureCoords).xyz, 1.0);
        }
        if (out_Color.a < 0.5) {
            discard;
        }
        out_Color = vec4(totalDiffuse, 1.0) * out_Color + vec4(totalSpecular, 1.0);
        out_Color = mix(vec4(skyColor, 1.0), out_Color, visibility);

        out_Color.x = scaleLinear(out_Color.x, vec2(0, 1), vec2(0.1, .5));
        out_Color.y = scaleLinear(out_Color.y, vec2(0, 1), vec2(0.1, .5));
        out_Color.z = scaleLinear(out_Color.z, vec2(0, 1), vec2(0.1, .5));
    } else {
        out_Color = vec4(color, 1);
    }

    if (alpha >= 0) { // Set by user
        out_Color.a = alpha;
    }
}