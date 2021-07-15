#version 460 core
#define PI 3.1415926538

#define DEFAULT 0
#define DONUT 1
#define PROGRESS_ICON 2
#define CIRCLE 3

#define MAX_LINES 5// Social classes

const float cornerSmooth = 0.55f;

in vec2 textureCoords;

out vec4 out_Color;

uniform sampler2D guiTexture;

uniform float guiWidth;
uniform float guiHeight;
uniform float cornerRadius;

uniform float alpha;
uniform vec3 color;
uniform vec3 borderColor;
uniform int outlineWidth;
uniform int type;
uniform bool filled;
uniform bool borderEnabled;

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
    if (cornerRadius <= 0.0) {
        return 1.0;
    }

    vec2 pixelPos = textureCoords * vec2(guiWidth, guiHeight);
    vec2 minCorner = vec2(cornerRadius, cornerRadius);
    vec2 maxCorner = vec2(guiWidth - cornerRadius, guiHeight - cornerRadius);

    vec2 cornerPoint = clamp(pixelPos, minCorner, maxCorner);
    float lowerBound = square(cornerRadius - cornerSmooth);
    float upperBound = square(cornerRadius + cornerSmooth);

    return smoothstep(upperBound, lowerBound, distanceSquared(pixelPos, cornerPoint));
}


float cross_product(vec2 v1, vec2 v2) {
    return v1.x * v2.y - v1.y * v2.x;
}

void drawDonut() {
    vec2 pos = vec2(textureCoords.x * 2.0 - 1.0, -(textureCoords.y * 2.0 - 1.0));
    vec2 localCenter = vec2(0, 0);
    if (distance(localCenter, pos) < (innerCircleRadius / outerCircleRadius)) { // Don't render inner ring colors
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

    discard;
}

void drawGui() {
    vec2 pixelPos = textureCoords * vec2(guiWidth, guiHeight);
    float width = guiWidth - outlineWidth;
    float height = guiHeight - outlineWidth;
    if (!filled) {
        if(!borderEnabled) {
            discard;
        }
        if (pixelPos.y < height && pixelPos.y > outlineWidth
        && pixelPos.x < width && pixelPos.x > outlineWidth) {
            discard;
        }
        out_Color = vec4(borderColor, 1.0);
    } else {
        // If color = (-1,-1,-1) -> texture else color
        if (color.x == -1) {
            out_Color = texture(guiTexture, textureCoords);
        } else {
            out_Color = vec4(color.xyz, calcRoundedCorners());
            if (out_Color.a > 0) {
                out_Color.a = alpha;
            }
        }
        if ((pixelPos.y >= height || pixelPos.y <= outlineWidth
        || pixelPos.x >= width || pixelPos.x <= outlineWidth) && borderEnabled) {
            out_Color = vec4(borderColor, 1.0);
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

void drawCircle() {
    if (!filled) {
        if (!borderEnabled) {
            discard;
        }
        vec2 pos = vec2(textureCoords.x * 2.0 - 1.0, -(textureCoords.y * 2.0 - 1.0));
        vec2 pixelPos = pos * vec2(guiWidth, guiHeight);

        float d = distance(pixelPos, textureCoords);
        out_Color = vec4(borderColor.rgb, 1.0 - smoothstep(0.0, float(outlineWidth), abs(guiWidth - d)));
    } else {
        if (!borderEnabled) {
            // If color = (-1,-1,-1) -> texture else color
            if (color.x == -1) {
                out_Color = texture(guiTexture, textureCoords);
            } else {
                out_Color = vec4(color.xyz, 1);
            }
        } else {
            vec2 pos = vec2(textureCoords.x * 2.0 - 1.0, -(textureCoords.y * 2.0 - 1.0));
            vec2 pixelPos = pos * vec2(guiWidth, guiHeight);

            float d = distance(pixelPos, textureCoords);
            float t1 = 1.0 - smoothstep(guiWidth - outlineWidth, guiWidth, d);
            float t2 = 1.0 - smoothstep(guiWidth, guiWidth + outlineWidth, d);
            out_Color = vec4(mix(borderColor, color, t1), t2);

            if (out_Color.a > 0) {
                out_Color.a = 1;
            }
        }
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
        case CIRCLE:
        drawCircle();
        break;
    }
}