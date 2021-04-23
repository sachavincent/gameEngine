package scene.gameObjects;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import models.BoundingBox;
import models.RawModel;
import models.TexturedModel;
import renderEngine.Loader;
import renderEngine.TerrainRenderer;
import scene.components.BoundingBoxComponent;
import scene.components.RendererComponent;
import scene.components.TerrainComponent;
import scene.components.TextureComponent;
import scene.components.TexturePackComponent;
import textures.ModelTexture;
import textures.TerrainTexture;
import textures.TerrainTexturePack;
import util.math.Plane3D;
import util.math.Vector3f;

public class Terrain extends GameObject {

    public static final  int   SIZE             = 500;

    public Terrain() {
        RawModel model = generateTerrain("black.png");
        TexturedModel texturedModel = new TexturedModel(model);
        addComponent(new TerrainComponent());
        addComponent(new TextureComponent(texturedModel));

        Plane3D terrainPlane = new Plane3D(new Vector3f(0, 0, 0), new Vector3f(0, 0, SIZE),
                new Vector3f(SIZE, 0, SIZE), new Vector3f(SIZE, 0, 0));
        BoundingBox boundingBox = new BoundingBox(model);
        boundingBox.addPlane(terrainPlane);
        boundingBox.setModelTexture(ModelTexture.DEFAULT_MODEL);
        addComponent(new BoundingBoxComponent(boundingBox));
        TerrainTexturePack terrainTexturePack = new TerrainTexturePack(new TerrainTexture("blue.png"),
                new TerrainTexture("red.png"), new TerrainTexture("green.png"), new TerrainTexture("blue.png"));
        addComponent(new TexturePackComponent(terrainTexturePack));
        addComponent(new RendererComponent(this, TerrainRenderer.getInstance()));
    }


    private RawModel generateTerrain(String heightMap) {
        BufferedImage image;
        try {
            image = ImageIO.read(new File("res/" + heightMap));
        } catch (IOException e) {
            e.printStackTrace();

            return null;
        }
        int VERTEX_COUNT = image.getHeight();

        int count = VERTEX_COUNT * VERTEX_COUNT;
        float[] vertices = new float[count * 3];
        float[] normals = new float[count * 3];
        float[] textureCoords = new float[count * 2];
        int[] indices = new int[6 * (VERTEX_COUNT - 1) * (VERTEX_COUNT)];
        int vertexPointer = 0;
        for (int i = 0; i < VERTEX_COUNT; i++) {
            for (int j = 0; j < VERTEX_COUNT; j++) {
                vertices[vertexPointer * 3] = (j / (VERTEX_COUNT - 1f) * SIZE);
                float height = 0;
                vertices[vertexPointer * 3 + 1] = height;
                vertices[vertexPointer * 3 + 2] = i / (VERTEX_COUNT - 1f) * SIZE;
                Vector3f normal = calculateNormal(j, i, image);
                normals[vertexPointer * 3] = normal.x;
                normals[vertexPointer * 3 + 1] = normal.y;
                normals[vertexPointer * 3 + 2] = normal.z;
                textureCoords[vertexPointer * 2] = (float) j / ((float) VERTEX_COUNT - 1);
                textureCoords[vertexPointer * 2 + 1] = (float) i / ((float) VERTEX_COUNT - 1);
                vertexPointer++;
            }
        }

        int pointer = 0;
        for (int gz = 0; gz < VERTEX_COUNT - 1; gz++) {
            for (int gx = 0; gx < VERTEX_COUNT - 1; gx++) {
                int topLeft = (gz * VERTEX_COUNT) + gx;
                int topRight = topLeft + 1;
                int bottomLeft = ((gz + 1) * VERTEX_COUNT) + gx;
                int bottomRight = bottomLeft + 1;
                indices[pointer++] = topLeft;
                indices[pointer++] = bottomLeft;
                indices[pointer++] = topRight;
                indices[pointer++] = topRight;
                indices[pointer++] = bottomLeft;
                indices[pointer++] = bottomRight;
            }
        }

        image.getGraphics().dispose();

        return Loader.getInstance().loadToVAO(vertices, textureCoords, normals, indices);
    }

    private Vector3f calculateNormal(int x, int z, BufferedImage image) {
        Vector3f normal = new Vector3f(0, 2f, 0);
        normal.normalise();
        return normal;
    }

}