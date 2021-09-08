#version 400 core

#define MAX_LIGHTS 10
#define MAX_BIOMES 30
#define DEFAULT_MAX_HEIGHT 32.0

struct Material {
    vec3 Ambient;
    vec3 Diffuse;
    vec3 Specular;
    float Shininess;

    bool UseAmbientMap;
    bool UseDiffuseMap;
    bool UseNormalMap;
    bool UseSpecularMap;
};

struct Biome {
    Material Material;
    float MinHeight;
    float MaxHeight;
};

in vec3 surfaceNormal;
in vec3 toLightVector[MAX_LIGHTS];
in vec3 toCameraVector;
in vec4 worldPosition;
in float visibility;
in vec3 pass_pos;

out vec4 out_Color;

uniform int focusBuildingPlacement;
uniform vec3 centerFocus[100];
uniform int radiusFocus[100];

uniform vec3 lightColor[MAX_LIGHTS];
uniform vec3 attenuation[MAX_LIGHTS];
uniform Biome biomes[MAX_BIOMES];
uniform float maxHeight = DEFAULT_MAX_HEIGHT;
uniform vec3 skyColor;
uniform vec2 hoveredCell;

bool isPosInCell(vec2 cell, vec2 position) {
    return position.x >= cell.x && position.x < cell.x + 1 && position.y >= cell.y && position.y < cell.y + 1;
}

float far = 400.0;
float near = 1.0;

float LinearizeDepth(float depth) {
    float z = depth * 2.0 - 1.0;// back to NDC
    return (2.0 * near * far) / (far + near - z * (far - near));
}
void main() {
    //    pass_pos.y = clamp(pass_pos.y, 0, maxHeight); // Correcting for bad interpolation between shaders
    vec3 totalDiffuse = vec3(0.0);
    vec3 totalSpecular = vec3(0.0);
    vec3 unitNormal = normalize(surfaceNormal);
    vec3 unitVectorToCamera = normalize(toCameraVector);

    vec4 totalColor = vec4(0, 1, 0, 1);
    for (int i = 0; i < MAX_LIGHTS; i++) {
        float dist = length(toLightVector[i]);
        float attFactor = attenuation[i].x + (attenuation[i].y * dist) + (attenuation[i].z * dist * dist);

        vec3 unitLightVector = normalize(toLightVector[i]);
        float nDotl = dot(unitNormal, unitLightVector);

        //        vec3 lightDirection = -unitLightVector;
        //        vec3 reflectedL ightDirection = reflect(lightDirection, unitNormal);

        //        float specularFactor = dot(reflectedLightDirection, unitVectorToCamera);
        //        specularFactor = max(specularFactor, 0.0);
        //        float dampedFactor = pow(specularFactor, shineDamper);

        vec3 color = lightColor[i];
        float brightness = clamp(nDotl, 0.0, 1.0) / 2.0;

        totalDiffuse = totalDiffuse + (brightness * color) / attFactor;
        //            totalSpecular = totalSpecular + (dampedFactor * reflectivity * color) / attFactor;
    }

    totalDiffuse = max(totalDiffuse, 0.3);


    //    out_Color = vec4(totalDiffuse, 1.0) * totalColor/* + vec4(totalSpecular, 1.0)*/;
    //    out_Color = vec4(totalDiffuse, 1.0) * texture(backgroundTexture, pass_textureCoords);
    //      out_Color = texture(backgroundTexture, vec2(pass_pos.x / 128.0, pass_pos.z / 128.0));
    //        out_Color = mix(vec4(skyColor, 1.0), out_Color, visibility);

    //        out_Color = totalColor;
    //    }

    //    out_Color = vec4(surfaceNormal * 0.5 + vec3(.5), 1.0);


    //        out_Color = mix(vec4(0, 1, 0, 1), vec4(1, 0, 0, 1), yLevel / 32.0);
    //    out_Color = vec4(1, 0, 0, 1);

//    if (pass_pos.x >= hoveredCell.x && pass_pos.x < hoveredCell.x + 1
//    && pass_pos.z >= hoveredCell.y && pass_pos.z < hoveredCell.y + 1) {
//        out_Color = vec4(1, 0, 1, 1);
//        return;
//    }
    Biome biomeBelow;
    biomeBelow.MinHeight = -1;
    Biome biomeAbove;
    biomeAbove.MinHeight = maxHeight + 1;
    for (int i = 0; i < MAX_BIOMES; i++) {
        Biome biome = biomes[i];
        if (biome.MinHeight < 0) { // End of the list of biomes
            break;
        }

        if (pass_pos.y >= biome.MinHeight && pass_pos.y < biome.MaxHeight) { // Fully in biome
            out_Color = vec4(biome.Material.Diffuse.rgb * totalDiffuse, 1.0);
            return;
        }

        if (pass_pos.y < biome.MinHeight && biome.MinHeight < biomeAbove.MinHeight) {
            biomeAbove = biome;
        }
        if (pass_pos.y >= biome.MaxHeight && biome.MaxHeight > biomeBelow.MaxHeight) {
            biomeBelow = biome;
        }
    }
    vec3 colorBelow = biomeBelow.Material.Diffuse * totalDiffuse;
    vec3 colorAbove = biomeAbove.Material.Diffuse * totalDiffuse;
    vec3 color;
    if (biomeAbove.MinHeight < 0 && biomeBelow.MinHeight < 0) { // No biome
        color = vec3(1, 0, 0);
    } else if (biomeAbove.MinHeight < 0) { // No biome above
        color = colorBelow;
        //            color = vec3(0, 1, 0);
    } else if (biomeBelow.MinHeight < 0) { // No biome below
        color = colorAbove;
        //            color = vec3(0, 0, 1);
    } else {
        //            if (abs(biomeAbove.MinHeight - biomeBelow.MaxHeight) < 1) {
        //                color = biomeAbove.Material.Diffuse;
        //            } else {
        color = mix(colorBelow, colorAbove,
        (pass_pos.y - biomeBelow.MaxHeight) / (biomeAbove.MinHeight - biomeBelow.MaxHeight));
        //            }
    }

    //        if (pass_pos.y < 5)
    //        color = vec3(1, 0, 0);
    //        else if (pass_pos.y < 10)
    //        color = vec3(0, 1, 0);
    //        else if (pass_pos.y < 15)
    //        color = vec3(0, 0, 1);
    //        else if (pass_pos.y < 20)
    //        color = vec3(1, 0, 1);
    //        else
    //        color = vec3(1, 1, 1);
    //        color=  totalDiffuse;
    out_Color = vec4(color, 1.0);
    //    }
}