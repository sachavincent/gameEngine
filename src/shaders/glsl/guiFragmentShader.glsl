#version 400 core
#define PI 3.1415926538
const int maxLines = 5;// Social classes
const float cornerSmooth = 0.55f;

in vec2 textureCoords;

out vec4 out_Color;

uniform sampler2D guiTexture;

uniform float guiWidth;
uniform float guiHeight;
uniform float radius;

uniform float alpha;

uniform vec3 color;

uniform bool isDonut;
uniform float innerCircleRadius;
uniform float outerCircleRadius;
uniform vec2 center;
uniform int nbLines;
uniform vec2 donutLines[maxLines];
uniform vec3 donutColors[maxLines];

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


float cross_product(vec2 v1, vec2 v2) {
    return v1.x * v2.y - v1.y * v2.x;
}

void main(void) {
    if (isDonut) {
        vec2 pos = vec2(textureCoords.x * 2.0 - 1.0, -(textureCoords.y * 2.0 - 1.0));
        if (sqrt(distanceSquared(center, pos)) < (innerCircleRadius / outerCircleRadius)) { // Don't render inner ring colors
            return;
        }

        vec2 normalizedVector = normalize(pos);

        if (nbLines == 0) { // Only one color

            out_Color = vec4(donutColors[0], 1);
            return;
        }
        for (int i = 0; i < nbLines - 1; i++) {
            vec2 normalizedLine1 = normalize(donutLines[i]);
            vec2 normalizedLine2 = normalize(donutLines[i + 1]);

            float crossAngle1 = cross_product(normalizedLine1, normalizedVector);
            float crossAngle2 = cross_product(normalizedVector, normalizedLine2);
            float crossAreaAngle = cross_product(normalizedLine1, normalizedLine2);

            float angle1 = atan(abs(crossAngle1), dot(normalizedLine1, normalizedVector));
            float angle2 = atan(abs(crossAngle2), dot(normalizedVector, normalizedLine2));

            float areaAngle = atan(abs(crossAreaAngle), dot(normalizedLine1, normalizedLine2));
            if (crossAngle1 > 0) {
                angle1 = 2 * PI - angle1;
            }

            if (crossAngle2 > 0) {
                angle2 = 2 * PI - angle2;
            }

            if (crossAreaAngle > 0) {
                areaAngle = 2 * PI - areaAngle;
            }

            if (angle1 <= areaAngle && angle2 <= areaAngle) {
                out_Color = vec4(donutColors[i], 1);

                return;
            }

            //            out_Color = vec4(1, 0, 1, 1);
        }
        return;
    }
    // Si color = (-1,-1,-1) -> texture sinon couleur
    if (color.x == -1 && color.y == -1 && color.z == -1) {
        out_Color = texture(guiTexture, textureCoords);
    } else {
        out_Color = vec4(color.x, color.y, color.z, 1);

        out_Color.a = calcRoundedCorners();

        if (out_Color.a > 0) {
            out_Color.a = alpha;
        }
    }
}