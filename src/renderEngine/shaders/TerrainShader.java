package renderEngine.shaders;

import entities.Camera;
import renderEngine.shaders.structs.Biome;
import renderEngine.shaders.structs.BiomeStruct;
import renderEngine.shaders.structs.Material;
import renderEngine.shaders.structs.StructLocation;
import scene.Scene;
import scene.components.LightComponent;
import scene.components.PositionComponent;
import scene.gameObjects.GameObject;
import terrain.TerrainPosition;
import util.math.Matrix4f;
import util.math.Vector2f;
import util.math.Vector3f;
import util.math.Vector4f;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class TerrainShader extends ShaderProgram implements IGameObjectShader {

    private static final int MAX_LIGHTS = 10;
    private static final int MAX_BIOMES = 30;
    private static final int MAX_FOCUS_POINTS = 100;

    private static final String VERTEX_FILE = "terrainVertexShader.glsl";
    private static final String FRAGMENT_FILE = "terrainFragmentShader.glsl";

    private int nbBiomes;

    private int location_transformationMatrix;
    private int location_projectionMatrix;
    private int location_viewMatrix;
    private int[] location_lightPosition;
    private int[] location_lightColor;
    private int[] location_attenuation;
    private int location_shineDamper;
    private int location_reflectivity;
    private int location_skyColor;
    private int location_plane;
    private int location_terrainSize;
    private int location_focusBuildingPlacement;
    private int location_hoveredCell;
    private int location_heightMap;
    private int location_maxHeight;
    private int[] location_centerFocus;
    private int[] location_radiusFocus;
    protected StructLocation[] location_biomes;

    public TerrainShader() {
        super(VERTEX_FILE, FRAGMENT_FILE);
    }

    @Override
    protected void bindAttributes() {
        super.bindAttribute(0, "position");
    }

    @Override
    protected void getAllUniformLocations() {
        this.location_transformationMatrix = getUniformLocation("transformationMatrix");
        this.location_projectionMatrix = getUniformLocation("projectionMatrix");
        this.location_viewMatrix = getUniformLocation("viewMatrix");
        this.location_shineDamper = getUniformLocation("shineDamper");
        this.location_reflectivity = getUniformLocation("reflectivity");
        this.location_skyColor = getUniformLocation("skyColor");
        this.location_hoveredCell = getUniformLocation("hoveredCell");
        this.location_heightMap = getUniformLocation("heightMap");
        this.location_maxHeight = getUniformLocation("maxHeight");

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
        this.location_biomes = new StructLocation[MAX_BIOMES];

        for (int i = 0; i < MAX_BIOMES; i++) {
            this.location_biomes[i] = new BiomeStruct(this.programID, "biomes[" + i + "]");
        }
    }

    public void connectTextureUnits() {
        loadInt(this.location_heightMap, 0);
    }

    public void loadHoveredCell(TerrainPosition cell) {
        loadVector(this.location_hoveredCell, new Vector2f(cell.getX(), cell.getZ()));
    }

    public void loadClipPlane(Vector4f plane) {
        loadVector(this.location_plane, plane);
    }

    public void loadTerrainSize(Vector2f terrainSize, float maxHeight) {
        loadVector(this.location_terrainSize, terrainSize);
        loadFloat(this.location_maxHeight, maxHeight);
    }

    public void loadSkyColor(float r, float g, float b) {
        loadVector(this.location_skyColor, new Vector3f(r, g, b));
    }

    public void loadShineVariables(float damper, float reflectivity) {
        loadFloat(this.location_shineDamper, damper);
        loadFloat(this.location_reflectivity, reflectivity);
    }

    @Override
    public void loadTransformationMatrix(Matrix4f matrix) {
        loadMatrix(this.location_transformationMatrix, matrix);
    }

    public void loadLights() {
        Set<GameObject> lights = Scene.getInstance().getGameObjectsForComponent(LightComponent.class, false);
        Iterator<GameObject> iterator = lights.iterator();
        int i = 0;
        while (iterator.hasNext()) {
            GameObject light = iterator.next();
            loadVector(this.location_lightPosition[i], light.getComponent(PositionComponent.class).getPosition());
            LightComponent lightComponent = light.getComponent(LightComponent.class);
            loadVector(this.location_lightColor[i], lightComponent.getColor());
            loadVector(this.location_attenuation[i], lightComponent.getAttenuation());
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

    public void loadBiomes(List<Biome> biomes) {
        Iterator<Biome> iterator = biomes.iterator();
        int i = 0;
        while (iterator.hasNext()) {
            Biome biome = iterator.next();
            this.location_biomes[i++].load(biome);
        }
        for (; i < MAX_BIOMES; i++) {
            this.location_biomes[i++].load(Material.DEFAULT, -1.0f, -1.0f);
        }
    }

    public void loadProjectionMatrix(Matrix4f projection) {
        loadMatrix(this.location_projectionMatrix, projection);
    }

    public void loadFocusBuildingPlacement(int focusBuildingPlacement) {
        loadInt(this.location_focusBuildingPlacement, focusBuildingPlacement);
    }

    public void loadFocusPoints(Map<TerrainPosition, Integer> focusPoints) {
        int i = 0;
        for (Entry<TerrainPosition, Integer> entry : focusPoints.entrySet()) {
            loadVector(this.location_centerFocus[i], entry.getKey().toVector3f());
            loadInt(this.location_radiusFocus[i++], entry.getValue());
        }
    }
}