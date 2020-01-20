package shaders;

import util.math.Vector2f;
import util.math.Vector3f;

public class FontShader extends ShaderProgram {

    private static final String VERTEX_FILE   = "fontVertexShader.glsl";
    private static final String FRAGMENT_FILE = "fontFragmentShader.glsl";

    private int location_colour;
    private int location_translation;

    public FontShader() {
        super(VERTEX_FILE, FRAGMENT_FILE);
    }

    @Override
    protected void getAllUniformLocations() {
        location_colour = super.getUniformLocation("colour");
        location_translation = super.getUniformLocation("translation");
    }

    @Override
    protected void bindAttributes() {
        super.bindAttribute(0, "position");
        super.bindAttribute(1, "textureCoords");
    }

    public void loadColor(Vector3f color) {
        super.loadVector(location_colour, color);
    }

    public void loadTranslation(Vector2f translation) {
        super.load2DVector(location_translation, translation);
    }
}
