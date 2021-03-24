#version 400 core

in vec2 pass_textureCoords;

out vec4 out_Color;

uniform vec3 color;
uniform sampler2D fontAtlas;
uniform float charWidth; // 0.5 => larger with larger text
uniform float edgeCharWidth; // 0.1 => smaller with larger text

const float borderWidth = 0.0; // 0.4
const float borderEdge = 0.5;

const vec2 offset = vec2(0, 0); // vec2(0.006, 0.006);

const vec3 outlineColor = vec3(1, 0, 0);

void main(void) {
	float distance = 1.0 - texture(fontAtlas, pass_textureCoords).a;
	float alpha = 1.0 - smoothstep(charWidth, charWidth + edgeCharWidth, distance);

	float distance2 = 1.0 - texture(fontAtlas, pass_textureCoords + offset).a;
	float outlineAlpha = 1.0 - smoothstep(borderWidth, borderWidth + borderEdge, distance2);

	float overallAlpha = alpha + (1.0 - alpha) * outlineAlpha;
	vec3 overallColor = mix(outlineColor, color, alpha / overallAlpha);
	out_Color = vec4(overallColor, overallAlpha);
}