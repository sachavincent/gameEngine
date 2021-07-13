package renderEngine.shaders;

import entities.Camera;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import scene.components.AttenuationComponent;
import scene.components.ColorComponent;
import scene.components.PositionComponent;
import scene.gameObjects.GameObject;
import terrains.TerrainPosition;
import util.math.Matrix4f;
import util.math.Vector2f;
import util.math.Vector3f;
import util.math.Vector4f;

public class TerrainShader extends ShaderProgram {

    private static final int MAX_LIGHTS       = 10;
    private static final int MAX_FOCUS_POINTS = 100;

    private static final String VERTEX_FILE   = "terrainVertexShader.glsl";
    private static final String FRAGMENT_FILE = "terrainFragmentShader.glsl";

    private int   location_transformationMatrix;
    private int   location_projectionMatrix;
    private int   location_viewMatrix;
    private int[] location_lightPosition;
    private int[] location_lightColor;
    private int[] location_attenuation;
    private int   location_shineDamper;
    private int   location_reflectivity;
    private int   location_skyColor;
    private int   location_backgroundTexture;
    private int   location_rTexture;
    private int   location_gTexture;
    private int   location_bTexture;
    private int   location_blendMap;
    private int   location_uniformColor;
    private int   location_plane;
    private int   location_terrainSize;
    private int   location_focusBuildingPlacement;
    private int[] location_centerFocus;
    private int[] location_radiusFocus;

    public TerrainShader() {
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
        this.location_skyColor = getUniformLocation("skyColor");

        this.location_backgroundTexture = getUniformLocation("backgroundTexture");
        this.location_rTexture = getUniformLocation("rTexture");
        this.location_gTexture = getUniformLocation("gTexture");
        this.location_bTexture = getUniformLocation("bTexture");
        this.location_blendMap = getUniformLocation("blendMap");
        this.location_uniformColor = getUniformLocation("uniformColor");
        this.location_plane = getUniformLocation("plane");
        this.location_terrainSize = getUniformLocation("terrainSize");
        this.location_focusBuildingPlacement = getUniformLocation("focusBuildingPlacement");

        this.location_centerFocus = new int[MAX_FOCUS_POINTS];
        this.location_radiusFocus = new int[MAX_FOCUS_POINTS];
        for (int i = 0; i < MAX_FOCUS_POINTS; i++) {
            this.location_centerFocus[i] = getUniformLocation("centerFocus[" + i + "]");
            this.location_radiusFocus[i] = getUniformLocation("radiusFocus[" + i + "]");
        }

        this.location_lightPosition = new int[MAX_LIGHTS];
        this.location_lightColor = new int[MAX_LIGHTS];
        this.location_attenuation = new int[MAX_LIGHTS];

        for (int i = 0; i < MAX_LIGHTS; i++) {
            this.location_lightPosition[i] = getUniformLocation("lightPosition[" + i + "]");
            this.location_lightColor[i] = getUniformLocation("lightColor[" + i + "]");
            this.location_attenuation[i] = getUniformLocation("attenuation[" + i + "]");
        }
    }

    public void connectTextureUnits() {
        loadInt(this.location_backgroundTexture, 0);
        loadInt(this.location_rTexture, 1);
        loadInt(this.location_gTexture, 2);
        loadInt(this.location_bTexture, 3);
        loadInt(this.location_blendMap, 4);
    }

    public void loadClipPlane(Vector4f plane) {
        loadVector(this.location_plane, plane);
    }

    public void loadTerrainSize(Vector2f terrainSize) {
        loadVector(this.location_terrainSize, terrainSize);
    }

    public void loadSkyColor(float r, float g, float b) {
        loadVector(this.location_skyColor, new Vector3f(r, g, b));
    }

    public void loadShineVariables(float damper, float reflectivity) {
        loadFloat(this.location_shineDamper, damper);
        loadFloat(this.location_reflectivity, reflectivity);
    }

    public void loadUniformColor(boolean uniformColor) {
        loadBoolean(this.location_uniformColor, uniformColor);
    }

    public void loadTransformationMatrix(Matrix4f matrix) {
        loadMatrix(this.location_transformationMatrix, matrix);
    }

    public void loadLights(Set<GameObject> lights) {
        Iterator<GameObject> iterator = lights.iterator();
        int i = 0;
        while (iterator.hasNext()) {
            GameObject light = iterator.next();
            loadVector(this.location_lightPosition[i], light.getComponent(PositionComponent.class).getPosition());
            loadVector(this.location_lightColor[i], light.getComponent(ColorComponent.class).getColor());
            loadVector(this.location_attenuation[i],
                    light.getComponent(AttenuationComponent.class).getAttenuation());
            i++;
        }
        for (; i < MAX_LIGHTS; i++) {
            loadVector(this.location_lightPosition[i], new Vector3f(0, 0, 0));
            loadVector(this.location_lightColor[i], new Vector3f(0, 0, 0));
            loadVector(this.location_attenuation[i], new Vector3f(1, 0, 0));
        }
    }

    public void loadViewMatrix() {
        Matrix4f viewMatrix = Camera.getInstance().getViewMatrix();
        loadMatrix(this.location_viewMatrix, viewMatrix);
    }

    public void loadProjectionMatrix(Matrix4f projection) {
        loadMatrix(this.location_projectionMatrix, projection);
    }

    public void loadFocusBuildingPlacement(int focusBuildingPlacement) {
        this.loadInt(this.location_focusBuildingPlacement, focusBuildingPlacement);
    }

    public void loadFocusPoints(Map<TerrainPosition, Integer> focusPoints) {
        int i = 0;
        for (Entry<TerrainPosition, Integer> entry : focusPoints.entrySet()) {
            this.loadVector(this.location_centerFocus[i], entry.getKey().toVector3f());
            this.loadInt(this.location_radiusFocus[i++], entry.getValue());
        }
    }
}