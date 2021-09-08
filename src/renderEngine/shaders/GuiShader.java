package renderEngine.shaders;

import display.Display;
import java.awt.Color;
import java.util.List;
import java.util.Set;
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
        this.location_transformationMatrix = getUniformLocation("transformationMatrix");
        this.location_guiWidth = getUniformLocation("guiWidth");
        this.location_guiHeight = getUniformLocation("guiHeight");
        this.location_cornerRadius = getUniformLocation("cornerRadius");
        this.location_alpha = getUniformLocation("alpha");
        this.location_color = getUniformLocation("color");
        this.location_borderColor = getUniformLocation("borderColor");
        this.location_borderEnabled = getUniformLocation("borderEnabled");
        this.location_outlineWidth = getUniformLocation("outlineWidth");
        this.location_type = getUniformLocation("type");
        this.location_filled = getUniformLocation("filled");
        this.location_innerCircleRadius = getUniformLocation("innerCircleRadius");
        this.location_outerCircleRadius = getUniformLocation("outerCircleRadius");
        this.location_percentage = getUniformLocation("percentage");
        this.location_nbLines = getUniformLocation("nbLines");

        this.location_donutLines = new int[100];
        this.location_donutColors = new int[100];

        for (int i = 0; i < location_donutLines.length; i++) {
            this.location_donutLines[i] = getUniformLocation("donutLines[" + i + "]");
            this.location_donutColors[i] = getUniformLocation("donutColors[" + i + "]");
        }
    }

    @Override
    protected void bindAttributes() {
        super.bindAttribute(0, "position");
    }

    public void loadTransformationMatrix(Matrix4f matrix) {
        loadMatrix(this.location_transformationMatrix, matrix);
    }

    public void loadAlpha(float alpha) {
        loadFloat(this.location_alpha, alpha);
    }

    public void loadCornerRadius(float cornerRadius) {
        loadFloat(this.location_cornerRadius, cornerRadius);
    }

    public void loadWidth(float width) {
        loadFloat(this.location_guiWidth, width * Display.getWindow().getWidth());
    }

    public void loadHeight(float height) {
        loadFloat(this.location_guiHeight, height * Display.getWindow().getHeight());
    }

    public void loadFilled(boolean filled) {
        loadBoolean(this.location_filled, filled);
    }

    public void loadBorderEnabled(boolean borderEnabled) {
        loadBoolean(this.location_borderEnabled, borderEnabled);
    }

    public void loadBorderColor(Color color) {
        loadVector(this.location_borderColor, color == null ? new Vector3f(-1, -1, -1) :
                new Vector3f(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f));
    }

    public void loadColor(Color color) {
        loadVector(this.location_color, color == null ? new Vector3f(-1, -1, -1) :
                new Vector3f(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f));
    }

    public void loadType(int type) {
        loadInt(location_type, type);
    }

    public void loadInnerCircleRadius(float radius) {
        loadFloat(this.location_innerCircleRadius, radius);
    }

    public void loadOuterCircleRadius(float radius) {
        loadFloat(this.location_outerCircleRadius, radius);
    }

    public void loadPercentage(float percentage) {
        loadFloat(this.location_percentage, percentage);
    }

    public void loadOutlineWidth(int width) {
        loadInt(this.location_outlineWidth, width);
    }

    public void loadDonutLines(List<Vector2f> points) {
        loadInt(this.location_nbLines, points.size());

        int i = 0;
        for (Vector2f point : points) {
            if (i < this.location_donutLines.length)
                loadVector(this.location_donutLines[i++], point);
            else
                break;
        }
    }

    public void loadDonutColors(Set<Color> colors) {
        int i = 0;
        for (Color color : colors) {
            if (i < this.location_donutColors.length)
                loadVector(this.location_donutColors[i++],
                        new Vector3f(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f));
            else
                break;
        }
    }
}