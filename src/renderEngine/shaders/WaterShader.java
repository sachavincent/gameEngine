package renderEngine.shaders;

import entities.Camera;
import renderEngine.MasterRenderer;
import scene.components.LightComponent;
import scene.components.PositionComponent;
import scene.gameObjects.GameObject;
import util.math.Matrix4f;

public class WaterShader extends ShaderProgram {

    private final static String VERTEX_FILE = "waterVertexShader.glsl";
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
        this.location_projectionMatrix = getUniformLocation("projectionMatrix");
        this.location_viewMatrix = getUniformLocation("viewMatrix");
        this.location_modelMatrix = getUniformLocation("modelMatrix");
        this.location_reflectionTexture = getUniformLocation("reflectionTexture");
        this.location_refractionTexture = getUniformLocation("refractionTexture");
        this.location_dudvMap = getUniformLocation("dudvMap");
        this.location_moveFactor = getUniformLocation("moveFactor");
        this.location_cameraPosition = getUniformLocation("cameraPosition");
        this.location_normalMap = getUniformLocation("normalMap");
        this.location_depthMap = getUniformLocation("depthMap");
        this.location_lightColor = getUniformLocation("lightColor");
        this.location_lightPosition = getUniformLocation("lightPosition");
        this.location_nearPlane = getUniformLocation("nearPlane");
        this.location_farPlane = getUniformLocation("farPlane");
    }

    public void connectTextureUnits() {
        loadInt(this.location_reflectionTexture, 0);
        loadInt(this.location_refractionTexture, 1);
        loadInt(this.location_dudvMap, 2);
        loadInt(this.location_normalMap, 3);
        loadInt(this.location_depthMap, 4);
    }

    public void loadPlaneValues() {
        loadFloat(this.location_nearPlane, MasterRenderer.NEAR_PLANE);
        loadFloat(this.location_farPlane, MasterRenderer.FAR_PLANE);
    }

    public void loadLight(GameObject sun) {
        if (sun == null)
            return;

        loadVector(this.location_lightColor, sun.getComponent(LightComponent.class).getColor());
        loadVector(this.location_lightPosition, sun.getComponent(PositionComponent.class).getPosition());
    }

    public void loadMoveFactor(float factor) {
        loadFloat(this.location_moveFactor, factor);
    }

    public void loadProjectionMatrix(Matrix4f projection) {
        loadMatrix(this.location_projectionMatrix, projection);
    }

    public void loadViewMatrix() {
        Matrix4f viewMatrix = Camera.getInstance().getViewMatrix();
        loadMatrix(this.location_viewMatrix, viewMatrix);
        loadVector(this.location_cameraPosition, Camera.getInstance().getPosition());
    }

    public void loadModelMatrix(Matrix4f modelMatrix) {
        loadMatrix(this.location_modelMatrix, modelMatrix);
    }
}