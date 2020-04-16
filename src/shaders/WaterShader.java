package shaders;

import entities.Camera;
import entities.Light;
import renderEngine.MasterRenderer;
import util.math.Maths;
import util.math.Matrix4f;

public class WaterShader extends ShaderProgram {

    private final static String VERTEX_FILE   = "waterVertexShader.glsl";
    private final static String FRAGMENT_FILE = "waterFragmentShader.glsl";

    private int location_modelMatrix;
    private int location_viewMatrix;
    private int location_projectionMatrix;
    private int location_reflectionTexture;
    private int location_refractionTexture;
    private int location_dudvMap;
    private int location_moveFactor;
    private int location_cameraPosition;
    private int location_normalMap;
    private int location_depthMap;
    private int location_lightColor;
    private int location_lightPosition;

    private int location_nearPlane;
    private int location_farPlane;

    public WaterShader() {
        super(VERTEX_FILE, FRAGMENT_FILE);
    }

    @Override
    protected void bindAttributes() {
        bindAttribute(0, "position");
    }

    @Override
    protected void getAllUniformLocations() {
        location_projectionMatrix = getUniformLocation("projectionMatrix");
        location_viewMatrix = getUniformLocation("viewMatrix");
        location_modelMatrix = getUniformLocation("modelMatrix");
        location_reflectionTexture = getUniformLocation("reflectionTexture");
        location_refractionTexture = getUniformLocation("refractionTexture");
        location_dudvMap = getUniformLocation("dudvMap");
        location_moveFactor = getUniformLocation("moveFactor");
        location_cameraPosition = getUniformLocation("cameraPosition");
        location_normalMap = getUniformLocation("normalMap");
        location_depthMap = getUniformLocation("depthMap");
        location_lightColor = getUniformLocation("lightColor");
        location_lightPosition = getUniformLocation("lightPosition");
        location_nearPlane = getUniformLocation("nearPlane");
        location_farPlane = getUniformLocation("farPlane");
    }

    public void connectTextureUnits() {
        super.loadInt(location_reflectionTexture, 0);
        super.loadInt(location_refractionTexture, 1);
        super.loadInt(location_dudvMap, 2);
        super.loadInt(location_normalMap, 3);
        super.loadInt(location_depthMap, 4);
    }

    public void loadPlaneValues() {
        super.loadFloat(location_nearPlane, MasterRenderer.NEAR_PLANE);
        super.loadFloat(location_farPlane, MasterRenderer.FAR_PLANE);
    }

    public void loadLight(Light sun) {
        super.loadVector(location_lightColor, sun.getColor());
        super.loadVector(location_lightPosition, sun.getPosition());
    }

    public void loadMoveFactor(float factor) {
        super.loadFloat(location_moveFactor, factor);
    }

    public void loadProjectionMatrix(Matrix4f projection) {
        loadMatrix(location_projectionMatrix, projection);
    }

    public void loadViewMatrix(Camera camera) {
        Matrix4f viewMatrix = Maths.createViewMatrix(camera);
        loadMatrix(location_viewMatrix, viewMatrix);
        super.loadVector(location_cameraPosition, camera.getPosition());
    }

    public void loadModelMatrix(Matrix4f modelMatrix) {
        loadMatrix(location_modelMatrix, modelMatrix);
    }

}
