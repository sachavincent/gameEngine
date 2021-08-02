package renderEngine;

import entities.ModelEntity;
import models.AbstractModel;
import models.SimpleModel;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import renderEngine.shaders.TerrainShader;
import scene.components.SingleModelComponent;
import scene.components.TerrainComponent;
import scene.components.TexturePackComponent;
import scene.gameObjects.GameObject;
import scene.gameObjects.Terrain;
import terrains.TerrainPosition;
import textures.ModelTexture;
import textures.TerrainTexturePack;
import util.MousePicker;
import util.ResourceFile;
import util.math.Vector2f;
import util.math.Vector3f;
import util.parsing.Material;
import util.parsing.ModelType;
import util.parsing.SimpleMaterialColor;
import util.parsing.colladaParser.dataStructures.MeshData;

import java.awt.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.opengl.GL11.*;
import static renderEngine.MasterRenderer.*;

public class TerrainRenderer extends GameObjectRenderer {
    private static TerrainRenderer instance;
    private ModelEntity modelEntity;

    public static TerrainRenderer getInstance() {
        return instance == null ? (instance = new TerrainRenderer()) : instance;
    }

    private TerrainRenderer() {
        super(new TerrainShader());

        this.shader.start();

        ((TerrainShader) this.shader).loadProjectionMatrix(MasterRenderer.getInstance().getProjectionMatrix());
        ((TerrainShader) this.shader).connectTextureUnits();

        this.shader.stop();
    }

    @Override
    public void render() {
        GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
        GL11.glDisable(GL_TEXTURE_2D);
        MasterRenderer.disableCulling();
//        GL11.glLineWidth(1);
        this.gameObjects.forEach(terrain -> {
            Vao vao = prepareTerrain(terrain);
            if (vao != null) {
                GL11.glLineWidth(2);

                vao.getIndexVbos().values().stream().findFirst().ifPresent(Vbo::bind);//TEMP TODO
                GL11.glDrawElements(GL_TRIANGLES, vao.getIndexCount(), GL11.GL_UNSIGNED_INT, 0);
                //TODO: Triangle strip
                vao.getIndexVbos().values().stream().findFirst().ifPresent(Vbo::unbind);//TEMP TODO
                unbindTexturedModel();
            }
        });

        MasterRenderer.enableCulling();
        GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
//        if (MouseUtils.rightClickPressed) {
//            System.out.println("pdating");
//        Vector3f currentRay = MousePicker.getInstance().getCurrentRay();
//        Vector3f start = new Vector3f(Camera.getInstance().getPosition());
//        Vector3f end = new Vector3f(start);
//        Vector3f tmp = new Vector3f(currentRay.x * 200f, currentRay.y * 200f, currentRay.z * 200f);
//        Vector3f.add(end, tmp, end);
//        Vector3f end = MousePicker.getInstance().getIntersectionPoint();
//        if (end != null) {
//            float[] positions = new float[6];
//            positions[0] = start.x;
//            positions[1] = start.y - 0.1f;
//            positions[2] = start.z;
//            positions[3] = end.x;
//            positions[4] = end.y;
//            positions[5] = end.z;
//            Material material = new Material("MouseRay");
//            material.setDiffuse(new SimpleMaterialColor(Color.RED));
//            Map<Material, int[]> indices = new HashMap<>() {{
//                put(material, new int[]{0, 1});
//            }};
//            MeshData data = new MeshData(positions, new float[]{0, 0, 1, 1}, new float[]{0, 1, 0, 0, 1, 0}, indices);
//            Vao va1o = Vao.createVao(data, ModelType.DEFAULT);
////        Vector3f position = new Vector3f(Camera.getInstance().getPosition());
////        position.setY(0);
//            Vector3f position = new Vector3f();
//            modelEntity = new ModelEntity(position, new Vector3f(), 1, new SimpleModel(va1o));
//        }
//        if (modelEntity != null) {
//            Vao vao = modelEntity.getModel().getVao();
//            vao.bind(0, 1, 2);
//            prepareInstance(modelEntity);
////            ((TerrainShader) this.shader).loadTransformationMatrix(new Matrix4f());
////            ((TerrainShader) this.shader).loadViewMatrix();
//            ModelTexture modelTexture = ModelTexture.createTexture(new ResourceFile("blue.png"));
//
//            GL13.glActiveTexture(GL13.GL_TEXTURE0);
//            GL11.glBindTexture(GL_TEXTURE_2D, modelTexture.getTextureID());
//            GL11.glLineWidth(4);
//            vao.getIndexVbos().values().stream().findFirst().ifPresent(Vbo::bind);//TEMP TODO
//            GL11.glDrawElements(GL11.GL_LINES, 2, GL11.GL_UNSIGNED_INT, 0);
//            vao.getIndexVbos().values().stream().findFirst().ifPresent(Vbo::unbind);//TEMP TODO
//            vao.unbind(0, 1, 2);
//            GL11.glLineWidth(2);
//        }
        TerrainPosition hoveredCell = MousePicker.getInstance().getHoveredCell();
        if (hoveredCell != null) {
            float[] pos = new float[12];
            int x = hoveredCell.getX();
            int z = hoveredCell.getZ();
            pos[0] = x;
            pos[1] = (float) Terrain.heights[x][z];
            pos[2] = z;

            pos[3] = x + 1;
            pos[4] = (float) Terrain.heights[x + 1][z];
            pos[5] = z;

            pos[6] = x + 1;
            pos[7] = (float) Terrain.heights[x + 1][z + 1];
            pos[8] = z + 1;

            pos[9] = x;
            pos[10] = (float) Terrain.heights[x][z + 1];
            pos[11] = z + 1;

            Material mtl = new Material("MouseRayInt");
            mtl.setDiffuse(new SimpleMaterialColor(Color.BLUE.darker()));
            Map<Material, int[]> idx = new HashMap<>() {{
                put(mtl, new int[]{0, 1, 2, 0, 2, 3});
            }};
            MeshData mdata = new MeshData(pos, new float[]{0.5f, 0.5f, 0.5f, 0.5f, 0.5f, 0.5f, 0.5f, 0.5f},
                    new float[]{0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 1, 0}, idx);
            Vao vao = Vao.createVao(mdata, ModelType.DEFAULT);
            ModelEntity entity = new ModelEntity(new Vector3f(), new Vector3f(), 1, new SimpleModel(vao));
            prepareInstance(entity);
            vao.bind(0, 1, 2);
            vao.getIndexVbos().values().stream().findFirst().ifPresent(Vbo::bind);//TEMP TODO
            ModelTexture modelTexture = ModelTexture.createTexture(new ResourceFile("purple.png"));
            GL13.glActiveTexture(GL13.GL_TEXTURE0);
            GL11.glBindTexture(GL_TEXTURE_2D, modelTexture.getTextureID());
            GL11.glDrawElements(GL11.GL_TRIANGLES, vao.getIndexCount(), GL11.GL_UNSIGNED_INT, 0);
            vao.getIndexVbos().values().stream().findFirst().ifPresent(Vbo::unbind);//TEMP TODO
            vao.unbind(0, 1, 2);
        }
        GL11.glBindTexture(GL_TEXTURE_2D, 0);
        this.gameObjects.clear();
        this.shader.stop();
    }

    public void prepareRender(GameObject gameObject) {
        if (!this.shader.isStarted()) {
            glEnable(GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
            this.shader.start();
            ((TerrainShader) this.shader).loadClipPlane(MasterRenderer.getClipPlane());
            ((TerrainShader) this.shader).loadSkyColor(RED, GREEN, BLUE);
            ((TerrainShader) this.shader).loadTerrainSize(new Vector2f(Terrain.SIZE, Terrain.SIZE));
            ((TerrainShader) this.shader).loadLights(LightRenderer.getInstance().getGameObjects());
            ((TerrainShader) this.shader).loadViewMatrix();
        }

        this.gameObjects.add(gameObject);
    }

    private Vao prepareTerrain(GameObject terrain) {
        SingleModelComponent singleModelComponent = terrain.getComponent(SingleModelComponent.class);
        if (singleModelComponent == null)
            return null;

        AbstractModel model = singleModelComponent.getModel().getModel();
        if (model == null)
            return null;

        Vao vao = model.getVao();
        GL30.glBindVertexArray(vao.getId());
        GL20.glEnableVertexAttribArray(0);
        GL20.glEnableVertexAttribArray(1);
        GL20.glEnableVertexAttribArray(2);
        TexturePackComponent texturePackComponent = terrain.getComponent(TexturePackComponent.class);
        if (texturePackComponent == null)
            return null;

//        bindTextures(terrain);
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL_TEXTURE_2D, Terrain.TEST);

        prepareInstance(model.toModelEntity());
        ((TerrainShader) this.shader).loadShineVariables(1, 0);


        TerrainComponent terrainComponent = terrain.getComponent(TerrainComponent.class);
        Map<TerrainPosition, Integer> focusPoints = terrainComponent.getFocusPoints();

        ((TerrainShader) this.shader).loadFocusBuildingPlacement(focusPoints.size());
        ((TerrainShader) this.shader).loadFocusPoints(focusPoints);

        return vao;
    }

    private void bindTextures(GameObject terrain) {
        GL13.glActiveTexture(GL13.GL_TEXTURE0);

        TexturePackComponent texturePackComponent = terrain.getComponent(TexturePackComponent.class);
        if (texturePackComponent == null)
            return;
        TerrainTexturePack texturePack = texturePackComponent.getTerrainTexturePack();

        if (texturePack.getBackgroundTexture() != null) {
            GL11.glBindTexture(GL_TEXTURE_2D, texturePack.getBackgroundTexture().getTextureID());
            GL13.glActiveTexture(GL13.GL_TEXTURE1);
        }

        if (texturePack.getRedTexture() != null) {
            GL11.glBindTexture(GL_TEXTURE_2D, texturePack.getRedTexture().getTextureID());
            GL13.glActiveTexture(GL13.GL_TEXTURE2);
        }

        if (texturePack.getGreenTexture() != null) {
            GL11.glBindTexture(GL_TEXTURE_2D, texturePack.getGreenTexture().getTextureID());
            GL13.glActiveTexture(GL13.GL_TEXTURE3);
        }

        if (texturePack.getBlueTexture() != null) {
            GL11.glBindTexture(GL_TEXTURE_2D, texturePack.getBlueTexture().getTextureID());
            GL13.glActiveTexture(GL13.GL_TEXTURE4);
        }

//        if (terrain.getBlendMap() != null && texturePack.equals(terrain.getTexturePack()))
//            GL11.glBindTexture(GL11.GL_TEXTURE_2D, terrain.getBlendMap().getTextureID());
    }

    private void unbindTexturedModel() {
        GL20.glDisableVertexAttribArray(0);
        GL20.glDisableVertexAttribArray(1);
        GL20.glDisableVertexAttribArray(2);
        GL11.glBindTexture(GL_TEXTURE_2D, 0);
        GL30.glBindVertexArray(0);
    }
}