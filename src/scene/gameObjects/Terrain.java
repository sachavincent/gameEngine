package scene.gameObjects;

import engineTester.Game;
import models.Model;
import renderEngine.TerrainRenderer;
import renderEngine.structures.AttributeData;
import renderEngine.structures.AttributeData.DataType;
import renderEngine.structures.IndexBufferVao;
import renderEngine.structures.IndexData;
import renderEngine.structures.IndicesAttribute;
import renderEngine.structures.Vao;
import scene.components.HeightMapComponent;
import scene.components.RendererComponent;
import scene.components.SingleModelComponent;
import scene.components.TerrainComponent;
import terrain.HeightMapGenerator;
import terrain.HeightMapSupplier;
import textures.TerrainTexture;
import util.ResourceFile;

public class Terrain extends GameObject {

    public Terrain() {
        ResourceFile heightMap = new ResourceFile("hihi.png");
        long seed = (long) (Math.random() * 100000L);
//                (long) (Math.random() * Long.MAX_VALUE);
        HeightMapSupplier<TerrainTexture, Exception> heightMapSupplier =
//                new HeightMapReader(heightMap);
                new HeightMapGenerator(seed
                        , heightMap
                );
        HeightMapComponent heightMapComponent = new HeightMapComponent(Game.TERRAIN_MAX_HEIGHT,
                Game.TERRAIN_WIDTH, Game.TERRAIN_DEPTH, heightMapSupplier);
        addComponent(heightMapComponent);
        IndexData modelData = generateTerrain();
        IndexBufferVao terrainVao = Vao.createVao(modelData, modelData.getVaoType());
        Model model = new Model(terrainVao);
        addComponent(new TerrainComponent());
        addComponent(new SingleModelComponent(model));

        RendererComponent component = new RendererComponent(TerrainRenderer.getInstance());
        component.setOnFrameRenderedCallback((gameObject, nbFrames) -> {
//            if (nbFrames > 200) {
//                System.out.println("TBD");
//                MousePicker.getInstance().TBD();
//                return true;
//            }
            return false;
        });
        addComponent(component);
    }

    private IndexData generateTerrain() {
        System.out.println("Generating Terrain...");
        int width = Game.TERRAIN_WIDTH + 2;
        int depth = Game.TERRAIN_DEPTH + 2;
        int count = width * depth;
        Integer[] vertices = new Integer[count * 2];
        Integer[] indices = new Integer[6 * (width - 1) * (depth - 1)];
        int vertexPointer = 0;
        System.out.println("Calculating positions...");

        for (int x = 0; x < width; x++) {
            for (int z = 0; z < depth; z++) {
                vertices[vertexPointer * 2] = z - 1;
                vertices[vertexPointer * 2 + 1] = x - 1;

                vertexPointer++;
            }
        }
        int pointer = 0;
        for (int gz = 0; gz < width - 1; gz++) {
            for (int gx = 0; gx < depth - 1; gx++) {
                int topLeft = (gz * width) + gx;
                int topRight = topLeft + 1;
                int bottomLeft = ((gz + 1) * width) + gx;
                int bottomRight = bottomLeft + 1;
                indices[pointer++] = topLeft;
                indices[pointer++] = bottomLeft;
                indices[pointer++] = topRight;
                indices[pointer++] = topRight;
                indices[pointer++] = bottomLeft;
                indices[pointer++] = bottomRight;
            }
        }
        AttributeData<Integer> verticesAttribute = new AttributeData<>(0, 2, vertices, DataType.INT);
        IndicesAttribute indicesAttribute = new IndicesAttribute(indices);
        return IndexData.createData(verticesAttribute, indicesAttribute);
    }
}