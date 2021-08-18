package renderEngine.shaders;

import entities.Camera;
import util.math.Matrix4f;

public class SkyboxShader extends ShaderProgram {

    private static final String VERTEX_FILE = "skyboxVertexShader.glsl";
    private static final String FRAGMENT_FILE = "skyboxFragmentShader.glsl";

    private int location_projectionViewMatrix;

    public SkyboxShader() {
        super(VERTEX_FILE, FRAGMENT_FILE);
    }

    @Override
    protected void getAllUniformLocations() {
        this.location_projectionViewMatrix = getUniformLocation("projectionViewMatrix");
    }

    @Override
    protected void bindAttributes() {
        super.bindAttribute(0, "position");
    }

    public void loadProjectionMatrix(Matrix4f projectionMatrix) {
        Matrix4f projectionViewMatrix = new Matrix4f();
        Matrix4f.mul(projectionMatrix, getViewMatrix(), projectionViewMatrix);
        loadMatrix(this.location_projectionViewMatrix, projectionViewMatrix);
    }

    private Matrix4f getViewMatrix() {
        Matrix4f matrix = Camera.getInstance().getViewMatrix();
        matrix.m30 = 0;
        matrix.m31 = 0;
        matrix.m32 = 0;
        return matrix;
    }
}