package renderEngine.shaders;

import java.util.Iterator;
import java.util.List;
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
import util.parsing.objParser.Material;

public class GameObjectShader extends ShaderProgram {

    private static final int MAX_LIGHTS    = 10;
    private static final int MAX_MATERIALS = 20;

    private static final String VERTEX_FILE   = "gameObjectVertexShader.glsl";
    private static final String FRAGMENT_FILE = "gameObjectFragmentShader.glsl";

    private int              location_transformationMatrix;
    private int              location_projectionMatrix;
    private int              location_viewMatrix;
    private int[]            location_lightPosition;
    private int[]            location_lightColor;
    private int[]            location_attenuation;
    private int              location_shineDamper;
    private int              location_reflectivity;
    private int              location_useFakeLighting;
    private int              location_skyColor;
    private int              location_numberOfRows;
    private int              location_offset;
    private int              location_plane;
    private int[]            location_useNormalMaps;
    private int              location_isInstanced;
    private int              location_alpha;
    private StructLocation[] location_materials;
    private int              maxSamplerId;

    public GameObjectShader() {
        super(VERTEX_FILE, FRAGMENT_FILE);
    }

    @Override
    protected void bindAttributes() {
        super.bindAttribute(0, "position");
        super.bindAttribute(1, "textureCoords");
        super.bindAttribute(2, "normal");
        super.bindAttribute(3, "materialIndex");
        super.bindAttribute(4, "tangent");
        super.bindAttribute(5, "globalTransformationMatrix");
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

        this.location_lightPosition = new int[MAX_LIGHTS];
        this.location_lightColor = new int[MAX_LIGHTS];
        this.location_attenuation = new int[MAX_LIGHTS];
        for (int i = 0; i < MAX_LIGHTS; i++) {
            this.location_lightPosition[i] = getUniformLocation("lightPosition[" + i + "]");
            this.location_lightColor[i] = getUniformLocation("lightColor[" + i + "]");
            this.location_attenuation[i] = getUniformLocation("attenuation[" + i + "]");
        }

        this.location_materials = new StructLocation[MAX_MATERIALS];
        this.location_useNormalMaps = new int[MAX_MATERIALS];
        for (int i = 0; i < MAX_MATERIALS; i++) {
            this.location_useNormalMaps[i] = getUniformLocation("useNormalMaps[" + i + "]");
            this.location_materials[i] = new StructLocation(this.programID, "materials[" + i + "]",
                    new Location("Ambient", Vector3f.class),
                    new Location("Diffuse", Vector3f.class),
                    new Location("Specular", Vector3f.class),
                    new Location("Shininess", Float.class),
                    new Location("ambientMaps[" + i + "]", Texture.class),
                    new Location("diffuseMaps[" + i + "]", Texture.class),
                    new Location("normalMaps[" + i + "]", Texture.class),
                    new Location("specularMaps[" + i + "]", Texture.class),
                    new Location("UseAmbientMap", Boolean.class),
                    new Location("UseDiffuseMap", Boolean.class),
                    new Location("UseNormalMap", Boolean.class),
                    new Location("UseSpecularMap", Boolean.class)
            );
        }
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

    public void connectTextureUnits() {
        for (StructLocation material : this.location_materials)
            this.maxSamplerId = material.connectTextureUnits(this.maxSamplerId);
    }

    public void loadMaterials(List<Material> materials) {
        Iterator<Material> iterator = materials.iterator();
        int i = 0;
        while (iterator.hasNext()) {
            Material material = iterator.next();
            this.location_materials[i].load(material.getAmbient(), material.getDiffuse(), material.getSpecular(),
                    material.getShininessExponent(), material.hasAmbientMap(), material.hasDiffuseMap(),
                    material.hasNormalMap(), material.hasSpecularMap());

            this.location_materials[i].loadTextures(material.getAmbientMap(), material.getDiffuseMap(),
                    material.getNormalMap(), material.getSpecularMap());
            loadBoolean(this.location_useNormalMaps[i], material.hasNormalMap());
            i++;
        }
        for (; i < MAX_MATERIALS; i++) {
            this.location_materials[i]
                    .load(new Vector3f(), new Vector3f(), new Vector3f(), 0f, false, false, false, false);
        }
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

    public void loadTransformationMatrix(Matrix4f matrix) {
        loadMatrix(this.location_transformationMatrix, matrix);
    }

    public void loadProjectionMatrix(Matrix4f matrix) {
        loadMatrix(this.location_projectionMatrix, matrix);
    }

    public void loadIsInstanced(boolean isInstanced) {
        loadBoolean(this.location_isInstanced, isInstanced);
    }

    public void loadLights(Set<GameObject> lights, Matrix4f viewMatrix) {
        Iterator<GameObject> iterator = lights.iterator();
        int i = 0;
        while (iterator.hasNext()) {
            GameObject light = iterator.next();
//            if (useNormalMap)
//                loadVector(this.location_lightPosition[i],
//                        getEyeSpacePosition(light.getComponent(PositionComponent.class).getPosition(), viewMatrix));
//            else
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
