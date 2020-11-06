#version 400 core

in vec2 textureCoords;

out vec4 out_Colour;

uniform sampler2D colourTexture;

vec4 toGray(vec4);

void main(void) {

    out_Colour = texture(colourTexture, textureCoords);

    out_Colour.r *= 255;
    out_Colour.g *= 255;
    out_Colour.b *= 255;
    out_Colour.rgba = toGray(out_Colour);
}

/**
 * Returns the monochrome luminance of the given color as an intensity
 * between 0.0 and 255.0 using the NTSC formula
 * Y = 0.299*r + 0.587*g + 0.114*b. If the given color is a shade of gray
 * (r = g = b), this method is guaranteed to return the exact grayscale
 * value (an integer with no floating-point roundoff error).
 *
 * @param color the color to convert
 * @return the monochrome luminance (between 0.0 and 255.0)
 */
float intensity(vec3 color) {
    int r = int(color.r);
    int g = int(color.g);
    int b = int(color.b);
    if (r == g && r == b) return r;// to avoid floating-point issues
    return 0.299*r + 0.587*g + 0.114*b;
}

/**
 * Returns a grayscale version of the given color as a {@code Color} object.
 *
 * @param color the {@code Color} object to convert to grayscale
 * @return a grayscale version of {@code color}
 */
vec4 toGray(vec4 color) {
    float y = round(intensity(color.rgb))/255;// round to nearest int
    return vec4(y, y, y, color.a);
}