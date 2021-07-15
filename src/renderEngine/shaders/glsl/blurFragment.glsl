#version 460 core

in vec2 blurTextureCoords[11];

out vec4 out_Color;

uniform sampler2D originalTexture;

void main(void) {
    out_Color = vec4(0.0);

    float blurValues[11];

    blurValues[0] = 0.066414;
    blurValues[1] = 0.079465;
    blurValues[2] = 0.091364;
    blurValues[3] = 0.100939;
    blurValues[4] =	0.107159;
    blurValues[5] = 0.109317;

    for (int i = 0; i < 5; i++)
        out_Color += texture(originalTexture, blurTextureCoords[i]) * blurValues[i];

    out_Color += texture(originalTexture, blurTextureCoords[5]) * blurValues[5];

    for (int i = 0; i < 5; i++)
        out_Color += texture(originalTexture, blurTextureCoords[6 + i]) * blurValues[4 - i];
}