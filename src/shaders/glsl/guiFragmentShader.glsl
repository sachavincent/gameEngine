#version 400 core
#define PI 3.1415926538

#define DEFAULT 0
#define DONUT 1
#define PROGRESS_ICON 2

#define MAX_LINES 5// Social classes

const float cornerSmooth = 0.55f;

in vec2 textureCoords;

out vec4 out_Color;

uniform sampler2D guiTexture;

uniform float guiWidth;
uniform float guiHeight;
uniform float radius;

uniform float alpha;

uniform vec3 color;

uniform int type;

// donut stuff
uniform float innerCircleRadius;
uniform float outerCircleRadius;
uniform int nbLines;
uniform vec2 donutLines[MAX_LINES];
uniform vec3 donutColors[MAX_LINES];

// icon stuff
uniform float percentage;

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

void drawDonut() {
    vec2 pos = vec2(textureCoords.x * 2.0 - 1.0, -(textureCoords.y * 2.0 - 1.0));
    vec2 center = vec2(0, 0);
    if (sqrt(distanceSquared(center, pos)) < (innerCircleRadius / outerCircleRadius)) { // Don't render inner ring colors
        // out_Color = vec4(0, 0, 0, 0);
        // return;
        discard;
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

        double angle1 = atan(abs(crossAngle1), dot(normalizedLine1, normalizedVector));
        double angle2 = atan(abs(crossAngle2), dot(normalizedVector, normalizedLine2));

        double areaAngle = atan(abs(crossAreaAngle), dot(normalizedLine1, normalizedLine2));
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

    }

    //    out_Color = vec4(1, 0, 1, 1);
    discard;
}

void drawGui() {
    // If color = (-1,-1,-1) -> texture else color
    if (color.x == -1) {
        out_Color = texture(guiTexture, textureCoords);
    } else {
        out_Color = vec4(color.xyz, calcRoundedCorners());

        if (out_Color.a > 0) {
            out_Color.a = alpha;
        }
    }
}

/**
 * Returns the monochrome luminance of the given color as an intensity
 * between 0.0 and 1.0 using the NTSC formula
 * Y = 0.299*r + 0.587*g + 0.114*b. If the given color is a shade of gray
 * (r = g = b), this method is guaranteed to return the exact grayscale
 * value (an integer with no floating-point roundoff error).
 *
 * @param color the color to convert
 * @return the monochrome luminance (between 0.0 and 1.0)
 */
float intensity(vec4 color) {
    float r = float(color.r);
    float g = float(color.g);
    float b = float(color.b);
    if (r == g && r == b) return r;
    return 0.299 * r + 0.587 * g + 0.114 * b;
}

/**
 * Returns a grayscale version of the given color as a {@code Color} object.
 *
 * @param color the {@code Color} object to convert to grayscale
 * @return a grayscale version of {@code color}
 */
vec4 toGray(vec4 color) {
    float y = intensity(color);
    return vec4(y, y, y, color.a);
}

void drawProgressIcon() {
    out_Color = texture(guiTexture, textureCoords);

    float posY = -(textureCoords.y * 2.0 - 1.0);
    double percentageHeight = percentage * 2 - 1;
    if (posY > percentageHeight) {
        out_Color = toGray(out_Color);
    }
}

void main(void) {
    switch (type) {
        case DEFAULT:
        drawGui();
        break;
        case DONUT:
        drawDonut();
        break;
        case PROGRESS_ICON:
        drawProgressIcon();
        break;
    }
}