package renderEngine.shaders;

import java.util.Iterator;
import java.util.Set;
import scene.components.AttenuationComponent;
import scene.components.ColorComponent;
import scene.gameObjects.GameObject;
import scene.components.PositionComponent;
import util.math.Maths;
import util.math.Matrix4f;
import util.math.Vector2f;
import util.math.Vector3f;
import util.math.Vector4f;

public class GameObjectShader extends ShaderProgram {

    private static final int MAX_LIGHTS = 10;

    private static final String VERTEX_FILE   = "gameObjectVertexShader.glsl";
    private static final String FRAGMENT_FILE = "gameObjectFragmentShader.glsl";

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
    private int   location_directionalColor;
    private int   location_isInstanced;

    public GameObjectShader() {
        super(VERTEX_FILE, FRAGMENT_FILE);
    }

    @Override
    protected void bindAttributes() {
        super.bindAttribute(0, "position");
        super.bindAttribute(1, "textureCoords");
        super.bindAttribute(2, "normal");
        super.bindAttribute(3, "globalTransformationMatrix");
    }

    @Override
    protected void getAllUniformLocations() {
        this.location_transformationMatrix = super.getUniformLocation("transformationMatrix");
        this.location_projectionMatrix = super.getUniformLocation("projectionMatrix");
        this.location_viewMatrix = super.getUniformLocation("viewMatrix");
        this.location_shineDamper = super.getUniformLocation("shineDamper");
        this.location_reflectivity = super.getUniformLocation("reflectivity");
        this.location_useFakeLighting = super.getUniformLocation("useFakeLighting");
        this.location_skyColor = super.getUniformLocation("skyColor");
        this.location_numberOfRows = super.getUniformLocation("numberOfRows");
        this.location_offset = super.getUniformLocation("offset");
        this.location_plane = super.getUniformLocation("plane");
        this.location_directionalColor = super.getUniformLocation("directionalColor");
        this.location_isInstanced = super.getUniformLocation("isInstanced");

        this.location_lightPosition = new int[MAX_LIGHTS];
        this.location_lightColor = new int[MAX_LIGHTS];
        this.location_attenuation = new int[MAX_LIGHTS];

        for (int i = 0; i < MAX_LIGHTS; i++) {
            this.location_lightPosition[i] = super.getUniformLocation("lightPosition[" + i + "]");
            this.location_lightColor[i] = super.getUniformLocation("lightColor[" + i + "]");
            this.location_attenuation[i] = super.getUniformLocation("attenuation[" + i + "]");
        }
    }

    public void loadClipPlane(Vector4f plane) {
        super.loadVector(location_plane, plane);
    }

    public void loadDirectionalColor(boolean directionalColor) {
        super.loadBoolean(location_directionalColor, directionalColor);
    }

    public void loadNumberOfRows(int numberofRows) {
        super.loadFloat(location_numberOfRows, numberofRows);
    }

    public void loadOffset(float x, float y) {
        super.load2DVector(location_offset, new Vector2f(x, y));
    }

    public void loadSkyColor(float r, float g, float b) {
        super.loadVector(location_skyColor, new Vector3f(r, g, b));
    }

    public void loadFakeLightingVariable(boolean useFakeLighting) {
        super.loadBoolean(location_useFakeLighting, useFakeLighting);
    }

    public void loadShineVariables(float damper, float reflectivity) {
        super.loadFloat(location_shineDamper, damper);
        super.loadFloat(location_reflectivity, reflectivity);
    }

    public void loadTransformationMatrix(Matrix4f matrix) {
        super.loadMatrix(location_transformationMatrix, matrix);
    }

    public void loadProjectionMatrix(Matrix4f matrix) {
        super.loadMatrix(location_projectionMatrix, matrix);
    }

    public void loadIsInstanced(boolean isInstanced) {
        super.loadBoolean(location_isInstanced, isInstanced);
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
        Matrix4f matrix = Maths.createViewMatrix();
        super.loadMatrix(location_viewMatrix, matrix);
    }
}
