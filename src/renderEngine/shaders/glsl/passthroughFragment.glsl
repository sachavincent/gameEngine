#version 460 core

in vec2 textureCoords;

out vec4 out_Color;

uniform sampler2D colourTexture;

void main(void) {
    out_Color = texture(colourTexture, textureCoords);
}