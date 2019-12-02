package shaders;

import guis.Gui;
import java.util.List;
import renderEngine.DisplayManager;
import util.vector.Matrix4f;
import util.vector.Vector3f;

public class GuiShader extends ShaderProgram {

    private static final String VERTEX_FILE   = "guiVertexShader.glsl";
    private static final String FRAGMENT_FILE = "guiFragmentShader.glsl";

    private int location_transformationMatrix;
    private int location_guiWidth;
    private int location_guiHeight;
    private int location_radius;
    private int location_alpha;
    private int location_color;

    public GuiShader() {
        super(VERTEX_FILE, FRAGMENT_FILE);
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

    @Override
    protected void getAllUniformLocations() {
        location_transformationMatrix = super.getUniformLocation("transformationMatrix");
        location_guiWidth = super.getUniformLocation("guiWidth");
        location_guiHeight = super.getUniformLocation("guiHeight");
        location_radius = super.getUniformLocation("radius");
        location_alpha = super.getUniformLocation("alpha");
        location_color = super.getUniformLocation("color");
    }

    @Override
    protected void bindAttributes() {
        super.bindAttribute(0, "position");
    }
}