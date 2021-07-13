package renderEngine.shaders;

import java.awt.Color;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import scene.components.AttenuationComponent;
import scene.components.ColorComponent;
import scene.components.PositionComponent;
import scene.gameObjects.GameObject;
import util.math.Matrix4f;
import util.math.Vector2f;
import util.math.Vector3f;
import util.math.Vector4f;

public class AnimatedGameObjectShader extends ShaderProgram {

    private static final int MAX_LIGHTS = 10;
    private static final int MAX_JOINTS = 50;

    private static final String VERTEX_FILE   = "animatedGameObjectVertexShader.glsl";
    private static final String FRAGMENT_FILE = "animatedGameObjectFragmentShader.glsl";

    private int   location_transformationMatrix;
    private int   location_projectionMatrix;
    private int   location_viewMatrix;
    private int[] location_lightPosition;
    private int[] location_lightColor;
    private int[] location_attenuation;
    private int   location_shineDamper;
    private int   location_reflectivity;
    private int   location_useFakeLighting;
    private int   location_useNormalMap;
    private int   location_skyColor;
    private int   location_numberOfRows;
    private int   location_offset;
    private int   location_plane;
    private int   location_modelTexture;
    private int   location_normalMap;
    private int   location_isInstanced;
    private int   location_alpha;
    private int   location_color;
    private int[] location_jointTransforms;

    public AnimatedGameObjectShader() {
        super(VERTEX_FILE, FRAGMENT_FILE);
    }

    @Override
    protected void bindAttributes() {
        super.bindAttribute(0, "position");
        super.bindAttribute(1, "textureCoords");
        super.bindAttribute(2, "normal");
        super.bindAttribute(3, "jointIndices");
        super.bindAttribute(4, "weights");
        super.bindAttribute(5, "tangent");
        super.bindAttribute(6, "globalTransformationMatrix");
    }

    @Override
    protected void getAllUniformLocations() {
        this.location_transformationMatrix = getUniformLocation("transformationMatrix");
        this.location_projectionMatrix = getUniformLocation("projectionMatrix");
        this.location_viewMatrix = getUniformLocation("viewMatrix");
        this.location_shineDamper = getUniformLocation("shineDamper");
        this.location_reflectivity = getUniformLocation("reflectivity");
        this.location_useFakeLighting = getUniformLocation("useFakeLighting");
        this.location_useNormalMap = getUniformLocation("useNormalMap");
        this.location_skyColor = getUniformLocation("skyColor");
        this.location_numberOfRows = getUniformLocation("numberOfRows");
        this.location_offset = getUniformLocation("offset");
        this.location_plane = getUniformLocation("plane");
        this.location_modelTexture = getUniformLocation("modelTexture");
        this.location_normalMap = getUniformLocation("normalMap");
        this.location_isInstanced = getUniformLocation("isInstanced");
        this.location_alpha = getUniformLocation("alpha");
        this.location_color = getUniformLocation("color");

        this.location_jointTransforms = new int[MAX_JOINTS];

        for (int i = 0; i < MAX_JOINTS; i++)
            this.location_jointTransforms[i] = getUniformLocation("jointTransforms[" + i + "]");

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
        loadVector(this.location_plane, plane);
    }

    public void loadAlpha(float alpha) {
        loadFloat(this.location_alpha, alpha);
    }

    public void loadColor(Color color) {
        loadVector(this.location_color, color == null ? new Vector3f(-1, -1, -1) :
                new Vector3f(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f));
    }

    public void loadNumberOfRows(int numberofRows) {
        loadInt(this.location_numberOfRows, numberofRows);
    }

    public void loadOffset(float x, float y) {
        loadVector(this.location_offset, new Vector2f(x, y));
    }

    public void loadSkyColor(float r, float g, float b) {
        loadVector(this.location_skyColor, new Vector3f(r, g, b));
    }

    public void loadJointTransforms(List<Matrix4f> jointTransforms) {
        Iterator<Matrix4f> iterator = jointTransforms.iterator();
        int i = 0;
        while (iterator.hasNext()) {
            loadMatrix(this.location_jointTransforms[i], iterator.next());
            i++;
        }
        for (; i < MAX_JOINTS; i++) {
            loadMatrix(this.location_jointTransforms[i], new Matrix4f());
        }
    }

    public void loadUseFakeLighting(boolean useFakeLighting) {
        loadBoolean(this.location_useFakeLighting, useFakeLighting);
    }

    public void loadUseNormalMap(boolean useNormalMap) {
        loadBoolean(this.location_useNormalMap, useNormalMap);
    }

    public void loadShineVariables(float damper, float reflectivity) {
        loadFloat(this.location_shineDamper, damper);
        loadFloat(this.location_reflectivity, reflectivity);
    }

    public void loadTransformationMatrix(Matrix4f matrix) {
        loadMatrix(this.location_transformationMatrix, matrix);
    }

    public void loadProjectionMatrix(Matrix4f matrix) {
        loadMatrix(this.location_projectionMatrix, matrix);
    }

    public void loadIsInstanced(boolean isInstanced) {
        loadBoolean(this.location_isInstanced, isInstanced);
    }

    public void connectTextureUnits() {
        loadInt(this.location_modelTexture, 0);
        loadInt(this.location_normalMap, 1);
    }

    public void loadLights(boolean useNormalMap, Set<GameObject> lights, Matrix4f viewMatrix) {
        loadBoolean(this.location_useNormalMap, useNormalMap);
        Iterator<GameObject> iterator = lights.iterator();
        int i = 0;
        while (iterator.hasNext()) {
            GameObject light = iterator.next();
            if (useNormalMap)
                loadVector(this.location_lightPosition[i],
                        getEyeSpacePosition(light.getComponent(PositionComponent.class).getPosition(), viewMatrix));
            else
                loadVector(this.location_lightPosition[i],
                        light.getComponent(PositionComponent.class).getPosition());
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

    private Vector3f getEyeSpacePosition(Vector3f position, Matrix4f viewMatrix) {
        Vector4f eyeSpacePos = new Vector4f(position.x, position.y, position.z, 1f);
        Matrix4f.transform(viewMatrix, eyeSpacePos, eyeSpacePos);
        return new Vector3f(eyeSpacePos);
    }

    public void loadViewMatrix(Matrix4f viewMatrix) {
        loadMatrix(this.location_viewMatrix, viewMatrix);
    }
}
