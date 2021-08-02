package renderEngine.shaders;

import java.util.Iterator;
import java.util.Set;

import renderEngine.shaders.StructLocation.Location;
import scene.components.AttenuationComponent;
import scene.components.ColorComponent;
import scene.components.PositionComponent;
import scene.gameObjects.GameObject;
import textures.Texture;
import util.math.Matrix4f;
import util.math.Vector2f;
import util.math.Vector3f;
import util.math.Vector4f;
import util.parsing.Material;
import util.parsing.MaterialColor;

public class GameObjectShader extends ShaderProgram implements IGameObjectShader {

    private static final int MAX_LIGHTS = 10;

    private static final String VERTEX_FILE = "gameObjectVertexShader.glsl";
    private static final String FRAGMENT_FILE = "gameObjectFragmentShader.glsl";

    protected int location_transformationMatrix;
    protected int location_projectionMatrix;
    protected int location_viewMatrix;
    protected int[] location_lightPosition;
    protected int[] location_lightColor;
    protected int[] location_attenuation;
    protected int location_shineDamper;
    protected int location_reflectivity;
    protected int location_useFakeLighting;
    protected int location_skyColor;
    protected int location_numberOfRows;
    protected int location_offset;
    protected int location_plane;
    protected int location_useNormalMap;
    protected int location_isInstanced;
    protected int location_alpha;
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
        this.location_shineDamper = getUniformLocation("shineDamper");
        this.location_reflectivity = getUniformLocation("reflectivity");
        this.location_useFakeLighting = getUniformLocation("useFakeLighting");
        this.location_skyColor = getUniformLocation("skyColor");
        this.location_numberOfRows = getUniformLocation("numberOfRows");
        this.location_offset = getUniformLocation("offset");
        this.location_plane = getUniformLocation("plane");
        this.location_isInstanced = getUniformLocation("isInstanced");
        this.location_alpha = getUniformLocation("alpha");
        this.location_useNormalMap = getUniformLocation("useNormalMap");

        this.location_lightPosition = new int[MAX_LIGHTS];
        this.location_lightColor = new int[MAX_LIGHTS];
        this.location_attenuation = new int[MAX_LIGHTS];
        for (int i = 0; i < MAX_LIGHTS; i++) {
            this.location_lightPosition[i] = getUniformLocation("lightPosition[" + i + "]");
            this.location_lightColor[i] = getUniformLocation("lightColor[" + i + "]");
            this.location_attenuation[i] = getUniformLocation("attenuation[" + i + "]");
        }

        createMaterialLocation();
    }

    protected final void createMaterialLocation() {
        this.location_material = new StructLocation(this.programID, "material",
                new Location("Ambient", MaterialColor.class),
                new Location("Diffuse", MaterialColor.class),
                new Location("Specular", MaterialColor.class),
                new Location("Shininess", Float.class),
                new Location("ambientMap", Texture.class),
                new Location("diffuseMap", Texture.class),
                new Location("normalMap", Texture.class),
                new Location("specularMap", Texture.class),
                new Location("UseAmbientMap", Boolean.class),
                new Location("UseDiffuseMap", Boolean.class),
                new Location("UseNormalMap", Boolean.class),
                new Location("UseSpecularMap", Boolean.class)
        );
    }

    public void loadClipPlane(Vector4f plane) {
        loadVector(this.location_plane, plane);
    }

    public void loadAlpha(float alpha) {
        loadFloat(this.location_alpha, alpha);
    }

    public void loadNumberOfRows(int numberofRows) {
        loadInt(this.location_numberOfRows, numberofRows);
    }

    public void loadMaterial(Material material) {
        this.location_material.load(material.getAmbient(), material.getDiffuse(), material.getSpecular(),
                material.getShininessExponent(), material.hasAmbientMap(), material.hasDiffuseMap(),
                material.hasNormalMap(), material.hasSpecularMap());

        this.location_material.loadTextures(material.getAmbientMap(), material.getDiffuseMap(),
                material.getNormalMap(), material.getSpecularMap());
    }

    public void loadOffset(float x, float y) {
        loadVector(this.location_offset, new Vector2f(x, y));
    }

    public void loadSkyColor(float r, float g, float b) {
        loadVector(this.location_skyColor, new Vector3f(r, g, b));
    }

    public void loadUseFakeLighting(boolean useFakeLighting) {
        loadBoolean(this.location_useFakeLighting, useFakeLighting);
    }

    public void loadShineVariables(float damper, float reflectivity) {
        loadFloat(this.location_shineDamper, damper);
        loadFloat(this.location_reflectivity, reflectivity);
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
