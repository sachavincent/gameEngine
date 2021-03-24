package shaders;

import java.awt.Color;
import util.math.Vector2f;
import util.math.Vector3f;

public class FontShader extends ShaderProgram {

    private static final String VERTEX_FILE   = "fontVertexShader.glsl";
    private static final String FRAGMENT_FILE = "fontFragmentShader.glsl";

    private int location_color;
    private int location_translation;
    private int location_charWidth;
    private int location_edgeCharWidth;

    public FontShader() {
        super(VERTEX_FILE, FRAGMENT_FILE);
    }

    @Override
    protected void getAllUniformLocations() {
        this.location_color = super.getUniformLocation("color");
        this.location_translation = super.getUniformLocation("translation");
        this.location_charWidth = super.getUniformLocation("charWidth");
        this.location_edgeCharWidth = super.getUniformLocation("edgeCharWidth");
    }

    @Override
    protected void bindAttributes() {
        super.bindAttribute(0, "position");
        super.bindAttribute(1, "textureCoords");
    }

    public void loadColor(Color color) {
        super.loadVector(this.location_color,
                new Vector3f(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f));
    }

    public void loadTranslation(Vector2f translation) {
        super.load2DVector(this.location_translation, translation);
    }

    public void loadCharWidths(float charWidth, float edgeCharWidth) {
        super.loadFloat(this.location_charWidth, charWidth);
        super.loadFloat(this.location_edgeCharWidth, edgeCharWidth);
    }
}
