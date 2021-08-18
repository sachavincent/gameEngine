#version 330

layout (location = 0) in vec2 position;

out vec2 pass_textureCoords;

uniform mat4 MVPMatrix;

void main(void) {
    pass_textureCoords = position + vec2(0.5, 0.5);
    pass_textureCoords.y = 1.0 - pass_textureCoords.y;
    gl_Position = MVPMatrix * vec4(position, 0.0, 1.0);
}