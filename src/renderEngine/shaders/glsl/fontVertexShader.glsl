#version 460 core

layout (location = 0) in vec2 position;
layout (location = 1) in vec2 textureCoords;
layout (location = 2) in vec3 color;

out vec3 pass_color;
out vec2 pass_textureCoords;
out vec2 pass_position;

uniform vec2 translation;

void main(void) {
    pass_position = vec2(position + translation * vec2(2.0, -2.0));
    gl_Position = vec4(pass_position, 0.0, 1.0);
    pass_textureCoords = textureCoords;
    pass_color = color;
}