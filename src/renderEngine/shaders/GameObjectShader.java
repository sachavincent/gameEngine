package renderEngine.shaders;

import java.util.Iterator;
import java.util.Set;
import renderEngine.shaders.structs.Material;
import renderEngine.shaders.structs.MaterialStruct;
import renderEngine.shaders.structs.StructLocation;
import scene.Scene;
import scene.components.LightComponent;
import scene.components.PositionComponent;
import scene.gameObjects.GameObject;
import util.math.Matrix4f;
import util.math.Vector3f;
import util.math.Vector4f;

public class GameObjectShader extends ShaderProgram implements IGameObjectShader {

    private static final int MAX_LIGHTS = 10;

    private static final String VERTEX_FILE   = "gameObjectVertexShader.glsl";
    private static final String FRAGMENT_FILE = "gameObjectFragmentShader.glsl";

    protected int            location_transformationMatrix;
    protected int            location_projectionMatrix;
    protected int            location_viewMatrix;
    protected int[]          location_lightPosition;
    protected int[]          location_lightColor;
    protected int[]          location_attenuation;
    protected int            location_useFakeLighting;
    protected int            location_skyColor;
    protected int            location_plane;
    protected int            location_useNormalMap;
    protected int            location_isInstanced;
    protected int            location_transparency;
    protected StructLocation location_material;

    public GameObjectShader() {
        super(VERTEX_FILE, FRAGMENT_FILE);
    }

    public GameObjectShader(String vertexFile, String fragmentFile) {
        super(vertexFile, fragmentFile);
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
        this.location_transformationMatrix = getUniformLocation("transformationMatrix");
        this.location_projectionMatrix = getUniformLocation("projectionMatrix");
        this.location_viewMatrix = getUniformLocation("viewMatrix");
        this.location_useFakeLighting = getUniformLocation("useFakeLighting");
        this.location_skyColor = getUniformLocation("skyColor");
        this.location_plane = getUniformLocation("plane");
        this.location_isInstanced = getUniformLocation("isInstanced");
        this.location_transparency = getUniformLocation("transparency");
        this.location_useNormalMap = getUniformLocation("useNormalMap");

        this.location_lightPosition = new int[MAX_LIGHTS];
        this.location_lightColor = new int[MAX_LIGHTS];
        this.location_attenuation = new int[MAX_LIGHTS];
        for (int i = 0; i < MAX_LIGHTS; i++) {
            this.location_lightPosition[i] = getUniformLocation("lightsPosition[" + i + "]");
            this.location_lightColor[i] = getUniformLocation("lightsColor[" + i + "]");
            this.location_attenuation[i] = getUniformLocation("attenuations[" + i + "]");
        }

        createMaterialLocation();
    }

    protected final void createMaterialLocation() {
        this.location_material = new MaterialStruct(this.programID, "material");
    }

    public void loadClipPlane(Vector4f plane) {
        loadVector(this.location_plane, plane);
    }

    public void loadTransparency(float alpha) {
        loadFloat(this.location_transparency, alpha);
    }

    public void loadMaterial(Material material) {
        this.location_material.load(material);
    }

    public void loadSkyColor(float r, float g, float b) {
        loadVector(this.location_skyColor, new Vector3f(r, g, b));
    }

    public void loadUseFakeLighting(boolean useFakeLighting) {
        loadBoolean(this.location_useFakeLighting, useFakeLighting);
    }

    @Override
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
        this.location_material.connectTextureUnits();
    }

    public void loadLights(boolean useNormalMap, Matrix4f viewMatrix) {
        loadBoolean(this.location_useNormalMap, useNormalMap);
        Set<GameObject> lights = Scene.getInstance().getGameObjectsForComponent(LightComponent.class, false);
        Iterator<GameObject> iterator = lights.iterator();
        int i = 0;
        while (iterator.hasNext()) {
            GameObject light = iterator.next();
            if (useNormalMap)
                loadVector(this.location_lightPosition[i],
                        getEyeSpacePosition(light.getComponent(PositionComponent.class).getPosition(), viewMatrix));
            else
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

    private Vector3f getEyeSpacePosition(Vector3f position, Matrix4f viewMatrix) {
        Vector4f eyeSpacePos = new Vector4f(position.getX(), position.getY(), position.getZ(), 1f);
        Matrix4f.transform(viewMatrix, eyeSpacePos, eyeSpacePos);
        return new Vector3f(eyeSpacePos);
    }

    public void loadViewMatrix(Matrix4f viewMatrix) {
        loadMatrix(this.location_viewMatrix, viewMatrix);
    }
}