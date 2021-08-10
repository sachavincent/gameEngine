#version 400 core

layout (location=0) in vec3 position;

out vec3 pass_textureCoords;

uniform mat4 projectionViewMatrix;

void main(void) {
    gl_Position = projectionViewMatrix * vec4(position, 1.0);
    pass_textureCoords = position;
}