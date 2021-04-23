#version 400 core

const float ambientStrength = .34f;

in vec2 pass_textureCoords;
in vec3 surfaceNormal;
in vec3 realNormal;
in vec3 toLightVector[10];
in vec3 toCameraVector;
in float visibility;

out vec4 out_Color;

uniform sampler2D textureSampler;
uniform vec3 lightColor[10];
uniform vec3 attenuation[10];
uniform float shineDamper;
uniform float reflectivity;
uniform vec3 skyColor;
uniform bool directionalColor;

float scaleLinear(float value, vec2 valueDomain) {
    return (value - valueDomain.x) / (valueDomain.y - valueDomain.x);
}

float scaleLinear(float value, vec2 valueDomain, vec2 valueRange) {
    return mix(valueRange.x, valueRange.y, scaleLinear(value, valueDomain));
}

void main() {
    vec3 unitNormal = normalize(surfaceNormal);
    vec3 normal = normalize(realNormal);
    vec3 unitVectorToCamera = normalize(toCameraVector);

    vec3 totalDiffuse = vec3(0.0);
    vec3 totalSpecular = vec3(0.0);

    vec3 ambient = ambientStrength * vec3(1, 1, 1);
    for (int i = 0; i < 10; i++) {
        float distance = length(toLightVector[i]);
        float attFactor = attenuation[i].x + (attenuation[i].y * distance) + (attenuation[i].z * distance * distance);
        vec3 unitLightVector = normalize(toLightVector[i]);
        float nDotl = dot(unitNormal, unitLightVector);
        //        float brightness = max(nDotl, 0.0);
        float brightness = clamp(nDotl, 0.0, 1.0);

        totalDiffuse = totalDiffuse + (brightness * lightColor[i]);
    }

    totalDiffuse = clamp(totalDiffuse, 0.0, 1.0);
    totalDiffuse.x = scaleLinear(totalDiffuse.x, vec2(0, 1), vec2(.45, .7));
    totalDiffuse.y = scaleLinear(totalDiffuse.y, vec2(0, 1), vec2(.45, .7));
    totalDiffuse.z = scaleLinear(totalDiffuse.z, vec2(0, 1), vec2(.45, .7));

    vec4 textureColor = texture(textureSampler, pass_textureCoords);
    if (textureColor.a < 0.5) {
        discard;
    }

    // out_Color = vec4(totalDiffuse, 1.0) * textureColor + vec4(totalSpecular, 1.0);
    out_Color = clamp(vec4(totalDiffuse, 1.0) + vec4(ambient, 1.0), 0.0, 1.0);
    out_Color = out_Color * textureColor;
    // out_Color = mix(vec4(skyColor, 1.0), out_Color, visibility);

    if (directionalColor) {
        const float eps = 0.001;
        const float faceColorDifference = 0.07;
        if (normal.y < eps && normal.y > -eps) {
            float factor = 1;
            if (normal.x < (1 + eps) && normal.x > (1 - eps)) {
                factor += faceColorDifference;
            } else if (normal.x > (-1 - eps) && normal.x < (-1 + eps)) {
                factor -= faceColorDifference;
            }
            if (normal.z < (1 + eps) && normal.z > (1 - eps)) {
                factor += faceColorDifference;
            } else if (normal.z > (-1 - eps) && normal.z < (-1 + eps)) {
                factor -= faceColorDifference;
            } else {
                return;
            }

            //            out_Color.x = clamp(out_Color.x * factor, 0, 1);
            //            out_Color.y = clamp(out_Color.y * factor, 0, 1);
            //            out_Color.z = clamp(out_Color.z * factor, 0, 1);
        }
    }
}