package renderEngine.shaders;

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
    private int location_topLeftCorner;
    //    private int location_topRightCorner;
//    private int location_bottomLeftCorner;
    private int location_bottomRightCorner;

    public FontShader() {
        super(VERTEX_FILE, FRAGMENT_FILE);
    }

    @Override
    protected void getAllUniformLocations() {
        this.location_color = getUniformLocation("color");
        this.location_translation = getUniformLocation("translation");
        this.location_charWidth = getUniformLocation("charWidth");
        this.location_edgeCharWidth = getUniformLocation("edgeCharWidth");
        this.location_topLeftCorner = getUniformLocation("topLeftCorner");
//        this.location_topRightCorner = getUniformLocation("topRightCorner");
//        this.location_bottomLeftCorner = getUniformLocation("bottomLeftCorner");
        this.location_bottomRightCorner = getUniformLocation("bottomRightCorner");
    }

    @Override
    protected void bindAttributes() {
        super.bindAttribute(0, "position");
        super.bindAttribute(1, "textureCoords");
        super.bindAttribute(2, "color");
    }

    public void loadColor(Color color) {
        loadVector(this.location_color,
                new Vector3f(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f));
    }
//
//    public void loadGuiTextCorners(Vector2f topLeftCorner, Vector2f topRightCorner, Vector2f bottomLeftCorner,
//            Vector2f bottomRightCorner) {
//        load2DVector(this.location_topLeftCorner, topLeftCorner);
//        load2DVector(this.location_topRightCorner, topRightCorner);
//        load2DVector(this.location_bottomLeftCorner, bottomLeftCorner);
//        load2DVector(this.location_bottomRightCorner, bottomRightCorner);
//    }

    public void loadGuiTextCorners(Vector2f topLeftCorner, Vector2f bottomRightCorner) {
        loadVector(this.location_topLeftCorner, topLeftCorner);
        loadVector(this.location_bottomRightCorner, bottomRightCorner);
    }

    public void loadTranslation(Vector2f translation) {
        loadVector(this.location_translation, translation);
    }

    public void loadCharWidths(float charWidth, float edgeCharWidth) {
        loadFloat(this.location_charWidth, charWidth);
        loadFloat(this.location_edgeCharWidth, edgeCharWidth);
    }
}
