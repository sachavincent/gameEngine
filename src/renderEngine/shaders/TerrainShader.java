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
        this.location_transformationMatrix = super.getUniformLocation("transformationMatrix");
        this.location_projectionMatrix = super.getUniformLocation("projectionMatrix");
        this.location_viewMatrix = super.getUniformLocation("viewMatrix");
        this.location_shineDamper = super.getUniformLocation("shineDamper");
        this.location_reflectivity = super.getUniformLocation("reflectivity");
        this.location_skyColor = super.getUniformLocation("skyColor");

        this.location_backgroundTexture = super.getUniformLocation("backgroundTexture");
        this.location_rTexture = super.getUniformLocation("rTexture");
        this.location_gTexture = super.getUniformLocation("gTexture");
        this.location_bTexture = super.getUniformLocation("bTexture");
        this.location_blendMap = super.getUniformLocation("blendMap");
        this.location_uniformColor = super.getUniformLocation("uniformColor");
        this.location_plane = super.getUniformLocation("plane");
        this.location_terrainSize = super.getUniformLocation("terrainSize");
        this.location_focusBuildingPlacement = super.getUniformLocation("focusBuildingPlacement");

        this.location_centerFocus = new int[MAX_FOCUS_POINTS];
        this.location_radiusFocus = new int[MAX_FOCUS_POINTS];
        for (int i = 0; i < MAX_FOCUS_POINTS; i++) {
            this.location_centerFocus[i] = super.getUniformLocation("centerFocus[" + i + "]");
            this.location_radiusFocus[i] = super.getUniformLocation("radiusFocus[" + i + "]");
        }

        this.location_lightPosition = new int[MAX_LIGHTS];
        this.location_lightColor = new int[MAX_LIGHTS];
        this.location_attenuation = new int[MAX_LIGHTS];

        for (int i = 0; i < MAX_LIGHTS; i++) {
            this.location_lightPosition[i] = super.getUniformLocation("lightPosition[" + i + "]");
            this.location_lightColor[i] = super.getUniformLocation("lightColor[" + i + "]");
            this.location_attenuation[i] = super.getUniformLocation("attenuation[" + i + "]");
        }
    }

    public void connectTextureUnits() {
        super.loadInt(this.location_backgroundTexture, 0);
        super.loadInt(this.location_rTexture, 1);
        super.loadInt(this.location_gTexture, 2);
        super.loadInt(this.location_bTexture, 3);
        super.loadInt(this.location_blendMap, 4);
    }

    public void loadClipPlane(Vector4f plane) {
        super.loadVector(this.location_plane, plane);
    }

    public void loadTerrainSize(Vector2f terrainSize) {
        super.load2DVector(this.location_terrainSize, terrainSize);
    }

    public void loadSkyColor(float r, float g, float b) {
        super.loadVector(this.location_skyColor, new Vector3f(r, g, b));
    }

    public void loadShineVariables(float damper, float reflectivity) {
        super.loadFloat(this.location_shineDamper, damper);
        super.loadFloat(this.location_reflectivity, reflectivity);
    }

    public void loadUniformColor(boolean uniformColor) {
        super.loadBoolean(this.location_uniformColor, uniformColor);
    }

    public void loadTransformationMatrix(Matrix4f matrix) {
        super.loadMatrix(this.location_transformationMatrix, matrix);
    }

    public void loadLights(Set<GameObject> lights) {
        Iterator<GameObject> iterator = lights.iterator();
        int i = 0;
        while (iterator.hasNext()) {
            GameObject light = iterator.next();
            super.loadVector(this.location_lightPosition[i], light.getComponent(PositionComponent.class).getPosition());
            super.loadVector(this.location_lightColor[i], light.getComponent(ColorComponent.class).getColor());
            super.loadVector(this.location_attenuation[i],
                    light.getComponent(AttenuationComponent.class).getAttenuation());
            i++;
        }
        for (; i < MAX_LIGHTS; i++) {
            super.loadVector(this.location_lightPosition[i], new Vector3f(0, 0, 0));
            super.loadVector(this.location_lightColor[i], new Vector3f(0, 0, 0));
            super.loadVector(this.location_attenuation[i], new Vector3f(1, 0, 0));
        }
    }

    public void loadViewMatrix() {
        Matrix4f viewMatrix = Camera.getInstance().getViewMatrix();
        super.loadMatrix(this.location_viewMatrix, viewMatrix);
    }

    public void loadProjectionMatrix(Matrix4f projection) {
        super.loadMatrix(this.location_projectionMatrix, projection);
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