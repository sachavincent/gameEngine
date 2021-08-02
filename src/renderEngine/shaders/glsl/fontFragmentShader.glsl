#version 400 core

in vec2 pass_textureCoords;
in vec3 pass_color;
in vec2 pass_position;

out vec4 out_Color;

uniform sampler2D fontAtlas;
uniform float charWidth;// 0.5 => larger with larger text
uniform float edgeCharWidth;// 0.1 => smaller with larger text

uniform vec2 topLeftCorner;
uniform vec2 bottomRightCorner;

const float borderWidth = 0.0;// 0.4
const float borderEdge = 0.5;

const vec2 offset = vec2(0, 0);// vec2(0.006, 0.006);

const vec3 outlineColor = vec3(1, 0, 0);

void main(void) {
//    vec2 pos = vec2(pass_textureCoords.x * 2.0 - 1.0, pass_textureCoords.y * 2.0 - 1.0);
    vec2 pos = pass_position;
    if (pos.x > topLeftCorner.x && pos.x < bottomRightCorner.x && pos.y > topLeftCorner.y && pos.y < bottomRightCorner.y) {
        float distance = 1.0 - texture(fontAtlas, pass_textureCoords).a;
        float alpha = 1.0 - smoothstep(charWidth, charWidth + edgeCharWidth, distance);

        float distance2 = 1.0 - texture(fontAtlas, pass_textureCoords + offset).a;
        float outlineAlpha = 1.0 - smoothstep(borderWidth, borderWidth + borderEdge, distance2);

        float overallAlpha = alpha + (1.0 - alpha) * outlineAlpha;
        vec3 overallColor = mix(outlineColor, pass_color, alpha / overallAlpha);
        out_Color = vec4(overallColor, overallAlpha);
    } else {
        discard;
    }
}