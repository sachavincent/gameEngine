#version 400 core

in vec2 textureCoords;

out vec4 out_Color;

uniform sampler2D guiTexture;

uniform float guiWidth;
uniform float guiHeight;
uniform float radius;

uniform float alpha;

const float cornerSmooth = 0.55f;

float square(float val) {
    return val * val;
}

float distanceSquared(vec2 p1, vec2 p2) {
    vec2 vector = p2 - p1;
    return vector.x * vector.x + vector.y * vector.y;
}

float calcRoundedCorners() {
    if (radius <= 0.0) {
        return 1.0;
    }

    vec2 pixelPos = textureCoords * vec2(guiWidth, guiHeight);
    vec2 minCorner = vec2(radius, radius);
    vec2 maxCorner = vec2(guiWidth - radius, guiHeight - radius);

    vec2 cornerPoint = clamp(pixelPos, minCorner, maxCorner);
    float lowerBound = square(radius - cornerSmooth);
    float upperBound = square(radius + cornerSmooth);

    return smoothstep(upperBound, lowerBound, distanceSquared(pixelPos, cornerPoint));
}

void main(void){
    out_Color = texture(guiTexture, textureCoords);
    out_Color.a = calcRoundedCorners();

    if (out_Color.a == 1) {
        out_Color.a = alpha;
    }
}