package renderEngine.shaders;

import entities.Camera;
import java.util.Iterator;
import java.util.Set;
import scene.components.AttenuationComponent;
import scene.components.ColorComponent;
import scene.components.PositionComponent;
import scene.gameObjects.GameObject;
import util.math.Matrix4f;
import util.math.Vector2f;
import util.math.Vector3f;
import util.math.Vector4f;

public class StaticShader extends ShaderProgram {

    private static final int MAX_LIGHTS = 10;

    private static final String VERTEX_FILE   = "vertexShader.glsl";
    private static final String FRAGMENT_FILE = "fragmentShader.glsl";

    private int   location_transformationMatrix;
    private int   location_projectionMatrix;
    private int   location_viewMatrix;
    private int[] location_lightPosition;
    private int[] location_lightColor;
    private int[] location_attenuation;
    private int   location_shineDamper;
    private int   location_reflectivity;
    private int   location_useFakeLighting;
    private int   location_skyColor;
    private int   location_numberOfRows;
    private int   location_offset;
    private int   location_plane;

    public StaticShader() {
        super(VERTEX_FILE, FRAGMENT_FILE);
    }

    @Override
    protected void bindAttributes() {
        super.bindAttribute(0, "position");
        super.bindAttribute(1, "textureCoords");
        super.bindAttribute(2, "normal");
    }

    @Override
    protected void getAllUniformLocations() {
        this.location_transformationMatrix = getUniformLocation("transformationMatrix");
        this.location_projectionMatrix = getUniformLocation("projectionMatrix");
        this.location_viewMatrix = getUniformLocation("viewMatrix");
        this.location_shineDamper = getUniformLocation("shineDamper");
        this.location_reflectivity = getUniformLocation("reflectivity");
        this.location_useFakeLighting = getUniformLocation("useFakeLighting");
        this.location_skyColor = getUniformLocation("skyColor");
        this.location_numberOfRows = getUniformLocation("numberOfRows");
        this.location_offset = getUniformLocation("offset");
        this.location_plane = getUniformLocation("plane");

        this.location_lightPosition = new int[MAX_LIGHTS];
        this.location_lightColor = new int[MAX_LIGHTS];
        this.location_attenuation = new int[MAX_LIGHTS];

        for (int i = 0; i < MAX_LIGHTS; i++) {
            this.location_lightPosition[i] = getUniformLocation("lightPosition[" + i + "]");
            this.location_lightColor[i] = getUniformLocation("lightColor[" + i + "]");
            this.location_attenuation[i] = getUniformLocation("attenuation[" + i + "]");
        }
    }

    public void loadClipPlane(Vector4f plane) {
        loadVector(location_plane, plane);
    }

    public void loadNumberOfRows(int numberofRows) {
        loadInt(location_numberOfRows, numberofRows);
    }

    public void loadOffset(float x, float y) {
        loadVector(location_offset, new Vector2f(x, y));
    }

    public void loadSkyColor(float r, float g, float b) {
        loadVector(location_skyColor, new Vector3f(r, g, b));
    }

    public void loadFakeLightingVariable(boolean useFakeLighting) {
        loadBoolean(location_useFakeLighting, useFakeLighting);
    }

    public void loadShineVariables(float damper, float reflectivity) {
        loadFloat(location_shineDamper, damper);
        loadFloat(location_reflectivity, reflectivity);
    }

    public void loadTransformationMatrix(Matrix4f matrix) {
        loadMatrix(location_transformationMatrix, matrix);
    }

    public void loadProjectionMatrix(Matrix4f matrix) {
        loadMatrix(location_projectionMatrix, matrix);
    }

    public void loadLights(Set<GameObject> lights) {
        Iterator<GameObject> iterator = lights.iterator();
        int i = 0;
        while (iterator.hasNext()) {
            GameObject light = iterator.next();
            loadVector(location_lightPosition[i], light.getComponent(PositionComponent.class).getPosition());
            loadVector(location_lightColor[i], light.getComponent(ColorComponent.class).getColor());
            loadVector(location_attenuation[i],
                    light.getComponent(AttenuationComponent.class).getAttenuation());
            i++;
        }
        for (; i < MAX_LIGHTS; i++) {
            loadVector(location_lightPosition[i], new Vector3f(0, 0, 0));
            loadVector(location_lightColor[i], new Vector3f(0, 0, 0));
            loadVector(location_attenuation[i], new Vector3f(1, 0, 0));

        }
    }

    public void loadViewMatrix() {
        Matrix4f matrix = Camera.getInstance().getViewMatrix();
        loadMatrix(location_viewMatrix, matrix);
    }
}
