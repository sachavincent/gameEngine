package renderEngine;

import engineTester.Game;
import entities.ModelEntity;
import models.AbstractModel;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL31;
import renderEngine.shaders.TerrainShader;
import renderEngine.shaders.structs.Biome;
import renderEngine.shaders.structs.Material;
import scene.Scene;
import scene.components.HeightMapComponent;
import scene.components.SingleModelComponent;
import scene.components.TerrainComponent;
import scene.gameObjects.GameObject;
import scene.gameObjects.Terrain;
import terrain.TerrainPosition;
import util.MousePicker;
import util.math.Vector2f;
import util.parsing.SimpleMaterialColor;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.lwjgl.opengl.GL11.*;
import static renderEngine.MasterRenderer.*;

public class TerrainRenderer extends GameObjectRenderer<TerrainShader> {

    private static TerrainRenderer instance;

    public static TerrainRenderer getInstance() {
        return instance == null ? (instance = new TerrainRenderer()) : instance;
    }

    private TerrainRenderer() {
        super(new TerrainShader(), s -> {
            s.loadProjectionMatrix(MasterRenderer.getInstance().getProjectionMatrix());
            s.connectTextureUnits();
            s.loadClipPlane(CLIP_PLANE);
            s.loadSkyColor(RED, GREEN, BLUE);
            s.loadTerrainSize(new Vector2f(Game.TERRAIN_WIDTH, Game.TERRAIN_DEPTH), Game.TERRAIN_MAX_HEIGHT);
        });
    }

    @Override
    protected void doPreRender() {
//        GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
//        GL11.glDisable(GL_TEXTURE_2D);
//        MasterRenderer.disableCulling();
//        GL11.glLineWidth(1);
        glEnable(GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        this.shader.loadLights();
        this.shader.loadViewMatrix();
        GL11.glLineWidth(2);
    }

    @Override
    protected void doRender(Set<Map.Entry<AbstractModel, List<ModelEntity>>> entrySet) {
//        TerrainPosition hoveredCell = MousePicker.getInstance().getHoveredCell();
        Terrain terrain = Scene.getInstance().getTerrain();
        if (terrain == null)
            return;

        Vao vao = prepareTerrain(terrain);
        if (vao == null)
            return;
        vao.bind();
//        if (hoveredCell != null) {
            this.shader.loadHoveredCells(MousePicker.getInstance().hoveredCells);
//        } else
//            this.shader.loadHoveredCells(new ArrayList<>());
//
//        this.shader.loadHoveredCells(Camera.getInstance().getPosition(), MousePicker.getInstance().getCurrentRay());
        vao.getIndexVbos().values().stream().findFirst().ifPresent(Vbo::bind);//TEMP TODO
        GL11.glDrawElements(GL_TRIANGLES, vao.getIndexCount(), GL11.GL_UNSIGNED_INT, 0);
        //TODO: Triangle strip
        vao.getIndexVbos().values().stream().findFirst().ifPresent(Vbo::unbind);//TEMP TODO
        vao.unbind();
        unbindTexturedModel();

        MasterRenderer.enableCulling();
        GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);

        GL11.glBindTexture(GL31.GL_TEXTURE_RECTANGLE, 0);
        GL11.glLineWidth(1);
    }

    private Vao prepareTerrain(GameObject terrain) {
        SingleModelComponent singleModelComponent = terrain.getComponent(SingleModelComponent.class);
        if (singleModelComponent == null)
            return null;

        AbstractModel model = singleModelComponent.getModel().getModel();
        if (model == null)
            return null;

        Vao vao = model.getVao();
        vao.bind();

        HeightMapComponent heightMapComponent = terrain.getComponent(HeightMapComponent.class);
        if (heightMapComponent != null) {
            GL13.glActiveTexture(GL13.GL_TEXTURE0);
            GL11.glBindTexture(GL31.GL_TEXTURE_RECTANGLE, heightMapComponent.getTexture().getID());
        }

        prepareInstance(model.toModelEntity());


        Material dirtBiomeMaterial = new Material("DirtBiome");
        dirtBiomeMaterial.setDiffuse(new SimpleMaterialColor(Color.decode("#4E342E")));
        Biome dirtBiome = new Biome(dirtBiomeMaterial, 0, 6.84f);

        Material grassBiomeMaterial = new Material("GrassBiome");
        grassBiomeMaterial.setDiffuse(new SimpleMaterialColor(Color.decode("#4CAF50")));
        Biome grassBiome = new Biome(grassBiomeMaterial, 8.47f, 11.85f);

        Material mountainBiomeMaterial = new Material("MountainBiome");
        mountainBiomeMaterial.setDiffuse(new SimpleMaterialColor(Color.decode("#616161")));
        Biome mountainBiome = new Biome(mountainBiomeMaterial, 12.2f, 24);

        Material snowBiomeMaterial = new Material("SnowBiome");
        snowBiomeMaterial.setDiffuse(new SimpleMaterialColor(new Color(255, 255, 255)));
        Biome snowBiome = new Biome(snowBiomeMaterial, 24, 30);

        List<Biome> biomes = new ArrayList<>() {{
            add(dirtBiome);
            add(grassBiome);
            add(mountainBiome);
            add(snowBiome);
        }};
        this.shader.loadBiomes(biomes);

        TerrainComponent terrainComponent = terrain.getComponent(TerrainComponent.class);
        Map<TerrainPosition, Integer> focusPoints = terrainComponent.getFocusPoints();

        this.shader.loadFocusBuildingPlacement(focusPoints.size());
        this.shader.loadFocusPoints(focusPoints);

        return vao;
    }

    private void unbindTexturedModel() {
        GL11.glBindTexture(GL31.GL_TEXTURE_RECTANGLE, 0);
    }
}