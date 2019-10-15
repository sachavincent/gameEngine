package shaders;

import guis.Gui;
import java.util.List;
import renderEngine.DisplayManager;
import util.vector.Matrix4f;

public class GuiShader extends ShaderProgram {

    private static final String VERTEX_FILE   = "guiVertexShader.glsl";
    private static final String FRAGMENT_FILE = "guiFragmentShader.glsl";

    private int location_transformationMatrix;
    private int location_guiWidth;
    private int location_guiHeight;
    private int location_radius;

    public GuiShader() {
        super(VERTEX_FILE, FRAGMENT_FILE);
    }

    public void loadTransformation(Matrix4f matrix) {
        super.loadMatrix(location_transformationMatrix, matrix);
    }

    public void loadGuis(List<Gui> guis) {
        for (Gui gui : guis) {
            super.loadFloat(location_guiWidth, gui.getWidth() * DisplayManager.WIDTH);
            super.loadFloat(location_guiHeight, gui.getHeight() * DisplayManager.HEIGHT);
            super.loadFloat(location_radius, Gui.CORNER_RADIUS);
        }
    }

    @Override
    protected void getAllUniformLocations() {
        location_transformationMatrix = super.getUniformLocation("transformationMatrix");
        location_guiWidth = super.getUniformLocation("guiWidth");
        location_guiHeight = super.getUniformLocation("guiHeight");
        location_radius = super.getUniformLocation("radius");
    }

    @Override
    protected void bindAttributes() {
        super.bindAttribute(0, "position");
    }
}