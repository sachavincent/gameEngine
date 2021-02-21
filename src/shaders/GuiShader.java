package shaders;

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
    private int location_radius;
    private int location_alpha;
    private int location_color;

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
        location_transformationMatrix = super.getUniformLocation("transformationMatrix");
        location_guiWidth = super.getUniformLocation("guiWidth");
        location_guiHeight = super.getUniformLocation("guiHeight");
        location_radius = super.getUniformLocation("radius");
        location_alpha = super.getUniformLocation("alpha");
        location_color = super.getUniformLocation("color");
        location_type = super.getUniformLocation("type");
        location_innerCircleRadius = super.getUniformLocation("innerCircleRadius");
        location_outerCircleRadius = super.getUniformLocation("outerCircleRadius");
        location_percentage = super.getUniformLocation("percentage");
        location_nbLines = super.getUniformLocation("nbLines");

        location_donutLines = new int[100];
        location_donutColors = new int[100];

        for (int i = 0; i < location_donutLines.length; i++) {
            location_donutLines[i] = super.getUniformLocation("donutLines[" + i + "]");
            location_donutColors[i] = super.getUniformLocation("donutColors[" + i + "]");
        }
    }

    @Override
    protected void bindAttributes() {
        super.bindAttribute(0, "position");
    }

    public void loadTransformation(Matrix4f matrix) {
        super.loadMatrix(location_transformationMatrix, matrix);
    }

    public void loadAlpha(float alpha) {
        super.loadFloat(location_alpha, alpha);
    }

    public void loadRadius(float radius) {
        super.loadFloat(location_radius, radius);
    }

    public void loadWidth(float width) {
        super.loadFloat(location_guiWidth, width * DisplayManager.WIDTH);
    }

    public void loadHeight(float height) {
        super.loadFloat(location_guiHeight, height * DisplayManager.HEIGHT);
    }

    public void loadColor(Vector3f color) {
        super.loadVector(location_color, color == null ? new Vector3f(-1, -1, -1) : color);
    }

    public void loadType(int type) {
        super.loadInt(location_type, type);
    }

    public void loadInnerCircleRadius(float radius) {
        super.loadFloat(location_innerCircleRadius, radius);
    }

    public void loadOuterCircleRadius(float radius) {
        super.loadFloat(location_outerCircleRadius, radius);
    }

    public void loadPercentage(float percentage) {
        super.loadFloat(location_percentage, percentage);
    }

    public void loadDonutLines(List<Vector2f> points) {
        super.loadInt(location_nbLines, points.size());

        int i = 0;
        for (Vector2f point : points) {
            if (i < location_donutLines.length)
                super.load2DVector(location_donutLines[i++], point);
            else
                break;
        }
    }

    public void loadDonutColors(Set<Color> colors) {
        int i = 0;
        for (Color color : colors) {
            if (i < location_donutColors.length)
                super.loadVector(location_donutColors[i++],
                        new Vector3f(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f));
            else
                break;
        }
    }
}