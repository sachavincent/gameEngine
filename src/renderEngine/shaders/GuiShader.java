package renderEngine.shaders;

import java.awt.Color;
import java.util.List;
import java.util.Set;
import renderEngine.DisplayManager;
import util.math.Matrix4f;
import util.math.Vector2f;
import util.math.Vector3f;

public class GuiShader extends ShaderProgram {

    private static final String VERTEX_FILE   = "guiVertexShader.glsl";
    private static final String FRAGMENT_FILE = "guiFragmentShader.glsl";

    private int location_transformationMatrix;
    private int location_guiWidth;
    private int location_guiHeight;
    private int location_cornerRadius;
    private int location_alpha;
    private int location_color;
    private int location_filled;
    private int location_borderColor;
    private int location_borderEnabled;
    private int location_outlineWidth;

    // donut chart stuff
    private int   location_type;
    private int   location_innerCircleRadius;
    private int   location_outerCircleRadius;
    private int   location_percentage;
    private int   location_nbLines;
    private int[] location_donutLines;
    private int[] location_donutColors;

    public GuiShader() {
        super(VERTEX_FILE, FRAGMENT_FILE);
    }

    @Override
    protected void getAllUniformLocations() {
        this.location_transformationMatrix = super.getUniformLocation("transformationMatrix");
        this.location_guiWidth = super.getUniformLocation("guiWidth");
        this.location_guiHeight = super.getUniformLocation("guiHeight");
        this.location_cornerRadius = super.getUniformLocation("cornerRadius");
        this.location_alpha = super.getUniformLocation("alpha");
        this.location_color = super.getUniformLocation("color");
        this.location_borderColor = super.getUniformLocation("borderColor");
        this.location_borderEnabled = super.getUniformLocation("borderEnabled");
        this.location_outlineWidth = super.getUniformLocation("outlineWidth");
        this.location_type = super.getUniformLocation("type");
        this.location_filled = super.getUniformLocation("filled");
        this.location_innerCircleRadius = super.getUniformLocation("innerCircleRadius");
        this.location_outerCircleRadius = super.getUniformLocation("outerCircleRadius");
        this.location_percentage = super.getUniformLocation("percentage");
        this.location_nbLines = super.getUniformLocation("nbLines");

        this.location_donutLines = new int[100];
        this.location_donutColors = new int[100];

        for (int i = 0; i < location_donutLines.length; i++) {
            this.location_donutLines[i] = super.getUniformLocation("donutLines[" + i + "]");
            this.location_donutColors[i] = super.getUniformLocation("donutColors[" + i + "]");
        }
    }

    @Override
    protected void bindAttributes() {
        super.bindAttribute(0, "position");
    }

    public void loadTransformation(Matrix4f matrix) {
        super.loadMatrix(this.location_transformationMatrix, matrix);
    }

    public void loadAlpha(float alpha) {
        super.loadFloat(this.location_alpha, alpha);
    }

    public void loadCornerRadius(float cornerRadius) {
        super.loadFloat(this.location_cornerRadius, cornerRadius);
    }

    public void loadWidth(float width) {
        super.loadFloat(this.location_guiWidth, width * DisplayManager.WIDTH);
    }

    public void loadHeight(float height) {
        super.loadFloat(this.location_guiHeight, height * DisplayManager.HEIGHT);
    }

    public void loadFilled(boolean filled) {
        super.loadBoolean(this.location_filled, filled);
    }

    public void loadBorderEnabled(boolean borderEnabled) {
        super.loadBoolean(this.location_borderEnabled, borderEnabled);
    }

    public void loadBorderColor(Color color) {
        super.loadVector(this.location_borderColor, color == null ? new Vector3f(-1, -1, -1) :
                new Vector3f(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f));
    }

    public void loadColor(Color color) {
        super.loadVector(this.location_color, color == null ? new Vector3f(-1, -1, -1) :
                new Vector3f(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f));
    }

    public void loadType(int type) {
        super.loadInt(location_type, type);
    }

    public void loadInnerCircleRadius(float radius) {
        super.loadFloat(this.location_innerCircleRadius, radius);
    }

    public void loadOuterCircleRadius(float radius) {
        super.loadFloat(this.location_outerCircleRadius, radius);
    }

    public void loadPercentage(float percentage) {
        super.loadFloat(this.location_percentage, percentage);
    }

    public void loadOutlineWidth(int width) {
        super.loadInt(this.location_outlineWidth, width);
    }

    public void loadDonutLines(List<Vector2f> points) {
        super.loadInt(this.location_nbLines, points.size());

        int i = 0;
        for (Vector2f point : points) {
            if (i < this.location_donutLines.length)
                super.load2DVector(this.location_donutLines[i++], point);
            else
                break;
        }
    }

    public void loadDonutColors(Set<Color> colors) {
        int i = 0;
        for (Color color : colors) {
            if (i < this.location_donutColors.length)
                super.loadVector(this.location_donutColors[i++],
                        new Vector3f(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f));
            else
                break;
        }
    }
}