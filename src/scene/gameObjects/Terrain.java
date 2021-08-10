package scene.gameObjects;

import engineTester.Game;
import models.Model;
import renderEngine.TerrainMeshData;
import renderEngine.TerrainRenderer;
import renderEngine.Vao;
import scene.components.HeightMapComponent;
import scene.components.RendererComponent;
import scene.components.SingleModelComponent;
import scene.components.TerrainComponent;
import terrain.HeightMapGenerator;
import terrain.HeightMapSupplier;
import textures.TerrainTexture;
import util.ResourceFile;
import util.math.Vector3f;
import util.parsing.ModelType;

import java.util.ArrayList;
import java.util.List;

public class Terrain extends GameObject {

    public Terrain() {
        ResourceFile heightMap = new ResourceFile("hihi.png");
        long seed = 4815162342L;
//                (long) (Math.random() * Long.MAX_VALUE);
        HeightMapSupplier<TerrainTexture, Exception> heightMapSupplier =
//                new HeightMapReader(heightMap);
                new HeightMapGenerator(seed
                        , heightMap
                );
        HeightMapComponent heightMapComponent = new HeightMapComponent(Game.TERRAIN_MAX_HEIGHT,
                Game.TERRAIN_WIDTH, Game.TERRAIN_DEPTH, heightMapSupplier);
        addComponent(heightMapComponent);
        TerrainMeshData modelData = generateTerrain();
        Vao terrainVao = Vao.createVao(modelData, ModelType.DEFAULT);
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

    private TerrainMeshData generateTerrain() {
        System.out.println("Generating Terrain...");
        int width = Game.TERRAIN_WIDTH;
        int depth = Game.TERRAIN_DEPTH;
        int count = width * depth;
        int[] vertices = new int[count * 2];
        int[] indices = new int[6 * (width - 1) * (depth - 1)];
        int vertexPointer = 0;
        System.out.println("Calculating positions...");
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < depth; j++) {
                vertices[vertexPointer * 2] = j;
                vertices[vertexPointer * 2 + 1] = i;
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

        System.out.println("Done with indices!");
        return new TerrainMeshData(vertices, indices);
    }


    private float[] calcNormals(float[] posArr, int width, int height) {
        Vector3f v0 = new Vector3f();
        Vector3f v1 = new Vector3f();
        Vector3f v2 = new Vector3f();
        Vector3f v3 = new Vector3f();
        Vector3f v4 = new Vector3f();
        Vector3f v12 = new Vector3f();
        Vector3f v23 = new Vector3f();
        Vector3f v34 = new Vector3f();
        Vector3f v41 = new Vector3f();
        List<Float> normals = new ArrayList<>();
        Vector3f normal = new Vector3f();
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                if (row > 0 && row < height - 1 && col > 0 && col < width - 1) {
                    int i0 = row * width * 3 + col * 3;
                    v0.x = posArr[i0];
                    v0.y = posArr[i0 + 1];
                    v0.z = posArr[i0 + 2];

                    int i1 = row * width * 3 + (col - 1) * 3;
                    v1.x = posArr[i1];
                    v1.y = posArr[i1 + 1];
                    v1.z = posArr[i1 + 2];
                    v1 = v1.sub(v0);

                    int i2 = (row + 1) * width * 3 + col * 3;
                    v2.x = posArr[i2];
                    v2.y = posArr[i2 + 1];
                    v2.z = posArr[i2 + 2];
                    v2 = v2.sub(v0);

                    int i3 = (row) * width * 3 + (col + 1) * 3;
                    v3.x = posArr[i3];
                    v3.y = posArr[i3 + 1];
                    v3.z = posArr[i3 + 2];
                    v3 = v3.sub(v0);

                    int i4 = (row - 1) * width * 3 + col * 3;
                    v4.x = posArr[i4];
                    v4.y = posArr[i4 + 1];
                    v4.z = posArr[i4 + 2];
                    v4 = v4.sub(v0);

                    Vector3f.cross(v1, v2, v12);
                    v12.normalize();

                    Vector3f.cross(v2, v3, v23);
                    v23.normalize();

                    Vector3f.cross(v3, v4, v34);
                    v34.normalize();

                    Vector3f.cross(v4, v1, v41);
                    v41.normalize();

                    normal = v12.add(v23).add(v34).add(v41);
                    normal.normalize();
                } else {
                    normal.x = 0;
                    normal.y = 1;
                    normal.z = 0;
                }
                normal.normalize();
                normals.add(normal.x);
                normals.add(normal.y);
                normals.add(normal.z);
            }
        }
        float[] normalsArray = new float[normals.size()];
        for (int i = 0; i < normals.size(); i++)
            normalsArray[i] = normals.get(i);
        return normalsArray;
    }
}