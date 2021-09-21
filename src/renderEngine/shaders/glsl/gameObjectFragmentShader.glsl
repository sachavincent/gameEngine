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
in vec3 toLightVectors[MAX_LIGHTS];
in vec3 toCameraVector;
in vec3 surfaceNormal;
in float visibility;

out vec4 out_Color;

uniform Material material;
uniform sampler2D ambientMap;
uniform sampler2D diffuseMap;
uniform sampler2D normalMap;
uniform sampler2D specularMap;


uniform vec3 lightsColor[MAX_LIGHTS];
uniform vec3 attenuations[MAX_LIGHTS];
uniform float shineDamper;
uniform float reflectivity;
uniform vec3 skyColor;
uniform bool useNormalMap;

uniform float transparency;

vec4 emissionValue;
vec4 ambientValue;
vec4 specularValue;
vec4 diffuseValue;
vec3 toCamera;

vec4 calcLightColor(vec3 lightColor, float lightIntensity, vec3 toLightVec, vec3 normal) {
    // Diffuse Light
    float diffuseFactor = max(dot(normal, toLightVec), 0.0);
    vec4 diffuseColor = diffuseValue * vec4(lightColor, 1.0) * lightIntensity * diffuseFactor;
    // Specular Light
    vec3 unitLightVector = normalize(toLightVec);
    vec3 lightDirection = -unitLightVector;
    vec3 reflectedLightDirection = reflect(lightDirection, normal);
    float specularFactor = pow(max(dot(reflectedLightDirection, toCamera), 0.0), 32);
    vec4 specColor = specularValue * lightIntensity * specularFactor * vec4(lightColor, 1.0);

    return (diffuseColor + specColor);
}

void main() {
    vec3 unitNormal;
    if (material.UseNormalMap) {
        vec4 normalMapValue = texture(normalMap, pass_textureCoords);
        unitNormal = normalize(normalMapValue.rgb);
    } else {
        unitNormal = normalize(surfaceNormal);
    }

    emissionValue = vec4(material.Emission, 1.0);

    if (material.UseDiffuseMap) {
        if (material.UseNormalMap) {
            diffuseValue = texture(diffuseMap, pass_textureCoords, -1.0);
        } else {
            diffuseValue = texture(diffuseMap, pass_textureCoords);
        }
    } else {
        diffuseValue = vec4(material.Diffuse, 1.0);
    }

    if (material.UseSpecularMap) {
        vec4 specInfo = texture(specularMap, pass_textureCoords);
        specularValue = vec4(specInfo.r);
        if (specInfo.g > 0.5) {
            diffuseValue = vec4(1.0);
        }
    } else {
        specularValue = vec4(material.Specular, 1.0);
    }

    if (material.UseAmbientMap) {
        ambientValue = texture(ambientMap, pass_textureCoords);
    } else {
        ambientValue = vec4(material.Ambient, 1.0);
    }

    toCamera = normalize(toCameraVector);

    vec3 vectors[4];
    vectors[0] = vec3(30, 0, 0);
    vectors[1] = vec3(0, 0, 30);
    vectors[2] = vec3(0, 0, -30);
    vectors[3] = vec3(-30, 0, 0);

    vec4 diffuseSpecularComp = vec4(0.0);
    for (int i = 0; i < MAX_LIGHTS; i++) {
        diffuseSpecularComp += calcLightColor(lightsColor[i], 1.0, normalize(toLightVectors[i]), unitNormal);
    }

    for (int i = 0; i < 4; i++) {
        diffuseSpecularComp += calcLightColor(vec3(1.0), 1.0, normalize(vectors[i]), unitNormal);
    }

    out_Color = emissionValue + diffuseSpecularComp;//TODO: Ambient

    if (transparency >= 0) { // Set by user
        out_Color.a = transparency;
    }
}