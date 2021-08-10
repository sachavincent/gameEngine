#version 400 core

#define MAX_MATERIALS 20
#define MAX_LIGHTS 10

struct Material {
    vec3 Emission;
    vec3 Ambient;
    vec3 Diffuse;
    vec3 Specular;
    float Shininess;

    bool UseAmbientMap;
    bool UseDiffuseMap;
    bool UseNormalMap;
    bool UseSpecularMap;
};

const float ambientStrength = .34f;

in vec2 pass_textureCoords;
in vec3 toLightVector[MAX_LIGHTS];
in vec3 toCameraVector;
in vec3 surfaceNormal;
in float visibility;

out vec4 out_Color;

uniform Material material;
uniform sampler2D ambientMap;
uniform sampler2D diffuseMap;
uniform sampler2D normalMap;
uniform sampler2D specularMap;


uniform vec3 lightColor[MAX_LIGHTS];
uniform vec3 attenuation[MAX_LIGHTS];
uniform float shineDamper;
uniform float reflectivity;
uniform vec3 skyColor;
uniform bool useNormalMap;

uniform float alpha;

float scaleLinear(float value, vec2 valueDomain) {
    return (value - valueDomain.x) / (valueDomain.y - valueDomain.x);
}

float scaleLinear(float value, vec2 valueDomain, vec2 valueRange) {
    return mix(valueRange.x, valueRange.y, scaleLinear(value, valueDomain));
}

void main() {
    vec3 unitNormal;
    if (material.UseNormalMap) {
//        vec4 normalMapValue = texture(normalMap, pass_textureCoords, -1.0);
        vec4 normalMapValue = texture(normalMap, pass_textureCoords);
        unitNormal = normalize(normalMapValue.rgb);
//        out_Color = vec4(unitNormal, 1.0);
    } else {
        unitNormal = normalize(surfaceNormal);
    }
    vec3 unitVectorToCamera = normalize(toCameraVector);

    vec3 totalDiffuse = vec3(0.0);
    vec3 totalSpecular = vec3(0.0);

    for (int i = 0; i < MAX_LIGHTS; i++) {
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
    //    totalDiffuse = max(totalDiffuse, 0.2);
    //    totalDiffuse.a = 1.0;

    if (material.UseDiffuseMap) {
        if (material.UseNormalMap) {
            totalDiffuse *= texture(diffuseMap, pass_textureCoords, -1.0);
        } else {
            totalDiffuse *= texture(diffuseMap, pass_textureCoords);
        }
//        if (totalDiffuse.a < 0.5) {
//            discard;
//        }
    } else {
        totalDiffuse *= vec3(material.Diffuse);
    }

    if (material.UseSpecularMap) {
        vec4 specInfo = texture(specularMap, pass_textureCoords);
        totalSpecular *= specInfo.r;
        if (specInfo.g > 0.5) {
            totalDiffuse = vec3(1.0);
        }
    } else {
        totalSpecular = material.Specular;
    }
    vec3 color = totalDiffuse;
    //    out_Color = totalDiffuse + vec4(totalSpecular, 1.0);
    //    out_Color = mix(vec4(skyColor, 1.0), out_Color, visibility);
    //
    //    out_Color.x = scaleLinear(out_Color.x, vec2(0, 1), vec2(0.1, .5));
    //    out_Color.y = scaleLinear(out_Color.y, vec2(0, 1), vec2(0.1, .5));
    //    out_Color.z = scaleLinear(out_Color.z, vec2(0, 1), vec2(0.1, .5));

    color += material.Emission;
    out_Color = vec4(color.rgb, 1.0);

    if (alpha >= 0) { // Set by user
        out_Color.a = alpha;
    }
}