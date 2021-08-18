#version 330

in vec2 pass_textureCoords;

out vec4 out_Color;

uniform sampler2D sunTexture;

void main(void) {
    out_Color = texture(sunTexture, pass_textureCoords);
}