package renderEngine.shaders;

import java.awt.Color;
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

public class GameObjectShader extends ShaderProgram {

    private static final int MAX_LIGHTS = 10;

    private static final String VERTEX_FILE   = "gameObjectVertexShader.glsl";
    private static final String FRAGMENT_FILE = "gameObjectFragmentShader.glsl";

    private int   location_transformationMatrix;
    private int   location_projectionMatrix;
    private int   location_viewMatrix;
    private int[] location_lightPositionEyeSpace;
    private int[] location_lightColor;
    private int[] location_attenuation;
    private int   location_shineDamper;
    private int   location_reflectivity;
    private int   location_useFakeLighting;
    private int   location_skyColor;
    private int   location_numberOfRows;
    private int   location_offset;
    private int   location_plane;
    private int   location_modelTexture;
    private int   location_normalMap;
    private int   location_isInstanced;
    private int   location_areTangentsOn;
    private int   location_alpha;
    private int   location_color;

    public GameObjectShader() {
        super(VERTEX_FILE, FRAGMENT_FILE);
    }

    @Override
    protected void bindAttributes() {
        super.bindAttribute(0, "position");
        super.bindAttribute(1, "textureCoords");
        super.bindAttribute(2, "normal");
        super.bindAttribute(3, "tangent");
        super.bindAttribute(4, "globalTransformationMatrix");
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
        this.location_modelTexture = super.getUniformLocation("modelTexture");
        this.location_normalMap = super.getUniformLocation("normalMap");
        this.location_isInstanced = super.getUniformLocation("isInstanced");
        this.location_areTangentsOn = super.getUniformLocation("areTangentsOn");
        this.location_alpha = super.getUniformLocation("alpha");
        this.location_color = super.getUniformLocation("color");

        this.location_lightPositionEyeSpace = new int[MAX_LIGHTS];
        this.location_lightColor = new int[MAX_LIGHTS];
        this.location_attenuation = new int[MAX_LIGHTS];
        for (int i = 0; i < MAX_LIGHTS; i++) {
            this.location_lightPositionEyeSpace[i] = super.getUniformLocation("lightPositionEyeSpace[" + i + "]");
            this.location_lightColor[i] = super.getUniformLocation("lightColor[" + i + "]");
            this.location_attenuation[i] = super.getUniformLocation("attenuation[" + i + "]");
        }
    }

    public void loadClipPlane(Vector4f plane) {
        super.loadVector(this.location_plane, plane);
    }

    public void loadAlpha(float alpha) {
        super.loadFloat(this.location_alpha, alpha);
    }

    public void loadColor(Color color) {
        super.loadVector(this.location_color, color == null ? new Vector3f(-1, -1, -1) :
                new Vector3f(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f));
    }

    public void loadNumberOfRows(int numberofRows) {
        super.loadFloat(this.location_numberOfRows, numberofRows);
    }

    public void loadOffset(float x, float y) {
        super.load2DVector(this.location_offset, new Vector2f(x, y));
    }

    public void loadSkyColor(float r, float g, float b) {
        super.loadVector(this.location_skyColor, new Vector3f(r, g, b));
    }

    public void loadFakeLightingVariable(boolean useFakeLighting) {
        super.loadBoolean(this.location_useFakeLighting, useFakeLighting);
    }

    public void loadShineVariables(float damper, float reflectivity) {
        super.loadFloat(this.location_shineDamper, damper);
        super.loadFloat(this.location_reflectivity, reflectivity);
    }

    public void loadTransformationMatrix(Matrix4f matrix) {
        super.loadMatrix(this.location_transformationMatrix, matrix);
    }

    public void loadProjectionMatrix(Matrix4f matrix) {
        super.loadMatrix(this.location_projectionMatrix, matrix);
    }

    public void loadIsInstanced(boolean isInstanced) {
        super.loadBoolean(this.location_isInstanced, isInstanced);
    }

    public void loadAreTangentsOn(boolean areTangentsOn) {
        super.loadBoolean(this.location_areTangentsOn, areTangentsOn);
    }

    public void connectTextureUnits() {
        super.loadInt(this.location_modelTexture, 0);
        super.loadInt(this.location_normalMap, 1);
    }

    public void loadLights(Set<GameObject> lights, Matrix4f viewMatrix) {
        Iterator<GameObject> iterator = lights.iterator();
        int i = 0;
        while (iterator.hasNext()) {
            GameObject light = iterator.next();
            super.loadVector(this.location_lightPositionEyeSpace[i],
                    getEyeSpacePosition(light.getComponent(PositionComponent.class).getPosition(), viewMatrix));
            super.loadVector(this.location_lightColor[i], light.getComponent(ColorComponent.class).getColor());
            super.loadVector(this.location_attenuation[i],
                    light.getComponent(AttenuationComponent.class).getAttenuation());
            i++;
        }
        for (; i < MAX_LIGHTS; i++) {
            super.loadVector(this.location_lightPositionEyeSpace[i], new Vector3f(0, 0, 0));
            super.loadVector(this.location_lightColor[i], new Vector3f(0, 0, 0));
            super.loadVector(this.location_attenuation[i], new Vector3f(1, 0, 0));

        }
    }

    private Vector3f getEyeSpacePosition(Vector3f position, Matrix4f viewMatrix) {
        Vector4f eyeSpacePos = new Vector4f(position.x, position.y, position.z, 1f);
        Matrix4f.transform(viewMatrix, eyeSpacePos, eyeSpacePos);
        return new Vector3f(eyeSpacePos);
    }

    public void loadViewMatrix(Matrix4f viewMatrix) {
        super.loadMatrix(this.location_viewMatrix, viewMatrix);
    }
}
