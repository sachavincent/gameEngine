package renderEngine.shaders;

import java.util.Iterator;
import java.util.Set;
import scene.components.AttenuationComponent;
import scene.components.ColorComponent;
import scene.gameObjects.GameObject;
import scene.components.PositionComponent;
import util.math.Maths;
import util.math.Matrix4f;
import util.math.Vector3f;
import util.math.Vector4f;

public class TerrainShader extends ShaderProgram {

    private static final int MAX_LIGHTS = 10;

    private static final String VERTEX_FILE   = "terrainVertexShader.glsl";
    private static final String FRAGMENT_FILE = "terrainFragmentShader.glsl";

    private int location_transformationMatrix;
    private int location_projectionMatrix;
    private int   location_viewMatrix;
    private int[] location_lightPosition;
    private int[] location_lightColor;
    private int[] location_attenuation;
    private int   location_shineDamper;
    private int location_reflectivity;
    private int location_skyColor;
    private int location_backgroundTexture;
    private int location_rTexture;
    private int location_gTexture;
    private int location_bTexture;
    private int location_blendMap;
    private int location_uniformColor;
    private int location_plane;

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
        location_transformationMatrix = super.getUniformLocation("transformationMatrix");
        location_projectionMatrix = super.getUniformLocation("projectionMatrix");
        location_viewMatrix = super.getUniformLocation("viewMatrix");
        location_shineDamper = super.getUniformLocation("shineDamper");
        location_reflectivity = super.getUniformLocation("reflectivity");
        location_skyColor = super.getUniformLocation("skyColor");

        location_backgroundTexture = super.getUniformLocation("backgroundTexture");
        location_rTexture = super.getUniformLocation("rTexture");
        location_gTexture = super.getUniformLocation("gTexture");
        location_bTexture = super.getUniformLocation("bTexture");
        location_blendMap = super.getUniformLocation("blendMap");
        location_uniformColor = super.getUniformLocation("uniformColor");
        location_plane = super.getUniformLocation("plane");

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
        super.loadInt(location_backgroundTexture, 0);
        super.loadInt(location_rTexture, 1);
        super.loadInt(location_gTexture, 2);
        super.loadInt(location_bTexture, 3);
        super.loadInt(location_blendMap, 4);
    }

    public void loadClipPlane(Vector4f plane) {
        super.loadVector(location_plane, plane);
    }

    public void loadSkyColor(float r, float g, float b) {
        super.loadVector(location_skyColor, new Vector3f(r, g, b));
    }

    public void loadShineVariables(float damper, float reflectivity) {
        super.loadFloat(location_shineDamper, damper);
        super.loadFloat(location_reflectivity, reflectivity);
    }

    public void loadUniformColor(boolean uniformColor) {
        super.loadBoolean(location_uniformColor, uniformColor);
    }

    public void loadTransformationMatrix(Matrix4f matrix) {
        super.loadMatrix(location_transformationMatrix, matrix);
    }

    public void loadLights(Set<GameObject> lights) {
        Iterator<GameObject> iterator = lights.iterator();
        int i = 0;
        while (iterator.hasNext()) {
            GameObject light = iterator.next();
            super.loadVector(location_lightPosition[i], light.getComponent(PositionComponent.class).getPosition());
            super.loadVector(location_lightColor[i], light.getComponent(ColorComponent.class).getColor());
            super.loadVector(location_attenuation[i],
                    light.getComponent(AttenuationComponent.class).getAttenuation());
            i++;
        }
        for (; i < MAX_LIGHTS; i++) {
            super.loadVector(location_lightPosition[i], new Vector3f(0, 0, 0));
            super.loadVector(location_lightColor[i], new Vector3f(0, 0, 0));
            super.loadVector(location_attenuation[i], new Vector3f(1, 0, 0));

        }
    }

    public void loadViewMatrix() {
        Matrix4f viewMatrix =  Maths.createViewMatrix();
        super.loadMatrix(location_viewMatrix, viewMatrix);
    }

    public void loadProjectionMatrix(Matrix4f projection) {
        super.loadMatrix(location_projectionMatrix, projection);
    }
}