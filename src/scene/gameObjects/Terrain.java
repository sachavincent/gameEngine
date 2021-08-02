package scene.gameObjects;

import models.BoundingBox;
import models.Model;
import renderEngine.TerrainRenderer;
import renderEngine.Vao;
import scene.components.*;
import textures.ModelTexture;
import textures.TerrainTexture;
import textures.TerrainTexturePack;
import util.ResourceFile;
import util.math.Plane3D;
import util.math.Vector3f;
import util.parsing.ModelType;
import util.parsing.colladaParser.dataStructures.MeshData;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static util.Utils.RES_PATH;

public class Terrain extends GameObject {

    public static final int SIZE = 128;
    public static final int MAX_HEIGHT = 32;
    public static final int TEST;

    public static double[][] heights;

    static {
        ModelTexture modelTexture = ModelTexture.createTexture(new ResourceFile("dirt.png"));
        TEST = modelTexture.getTextureID();
    }

    public Terrain() {
        MeshData modelData = generateTerrain("test.png");
        Vao terrainVao = Vao.createVao(modelData, ModelType.DEFAULT);
        Model model = new Model(terrainVao);
        addComponent(new TerrainComponent());
        addComponent(new SingleModelComponent(model));

        Plane3D terrainPlane = new Plane3D(new Vector3f(0, 0, 0), new Vector3f(0, 0, SIZE),
                new Vector3f(SIZE, 0, SIZE), new Vector3f(SIZE, 0, 0));
        BoundingBox boundingBox = new BoundingBox(terrainVao);
        boundingBox.addPlane(terrainPlane);
        boundingBox.setModelTexture(ModelTexture.DEFAULT_MODEL);
        addComponent(new BoundingBoxComponent(boundingBox));
        TerrainTexturePack terrainTexturePack = new TerrainTexturePack(
                new TerrainTexture(new ResourceFile("blue.png")),
                new TerrainTexture(new ResourceFile("red.png")),
                new TerrainTexture(new ResourceFile("green.png")),
                new TerrainTexture(new ResourceFile("blue.png")));
        addComponent(new TexturePackComponent(terrainTexturePack));
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


    private MeshData generateTerrain(String heightMap) {
        System.out.println("Generating Terrain...");
        BufferedImage image;
        try {
            image = ImageIO.read(new File(RES_PATH + "/" + heightMap));
        } catch (IOException e) {
            e.printStackTrace();

            return null;
        }

        int VERTEX_COUNT = SIZE;

        int count = VERTEX_COUNT * VERTEX_COUNT;
        float[] vertices = new float[count * 3];
        float[] normals = new float[count * 3];
        float[] textureCoords = new float[count * 2];
        int[] indices = new int[6 * (VERTEX_COUNT - 1) * (VERTEX_COUNT - 1)];
        int vertexPointer = 0;
        Raster data = image.getData();
        System.out.println("Calculating positions...");
        int jOffset = image.getWidth() / SIZE;
        int iOffset = image.getHeight() / SIZE;
        heights = new double[SIZE][SIZE];
        for (int i = 0; i < VERTEX_COUNT; i++) {
            for (int j = 0; j < VERTEX_COUNT; j++) {
//                vertices[vertexPointer * 3] = (j / (VERTEX_COUNT - 1f) * SIZE);
                vertices[vertexPointer * 3] = j;
                heights[j][i] = getHeight(j * jOffset, i * iOffset, image, data) * MAX_HEIGHT;
                vertices[vertexPointer * 3 + 1] = (float) heights[j][i];
//                vertices[vertexPointer * 3 + 2] = i / (VERTEX_COUNT - 1f) * SIZE;
                vertices[vertexPointer * 3 + 2] = i;
//                Vector3f normal = calculateNormal(j, i, image, data);
//                normals[vertexPointer * 3] = normal.x;
//                normals[vertexPointer * 3 + 1] = normal.y;
//                normals[vertexPointer * 3 + 2] = normal.z;
                textureCoords[vertexPointer * 2] = (float) j / ((float) VERTEX_COUNT - 1);
                textureCoords[vertexPointer * 2 + 1] = (float) i / ((float) VERTEX_COUNT - 1);
                vertexPointer++;
            }
        }
//        System.out.println("Calculating normals...");
        normals = calcNormals(vertices, VERTEX_COUNT, VERTEX_COUNT);
//        System.out.println("Done with normals!");
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

        System.out.println("Max height: " + Height);
        System.out.println("Done with indices!");
        image.getGraphics().dispose();

        return new MeshData(vertices, textureCoords, normals, indices);
    }

    float Height = Float.MIN_VALUE;

    private double getHeight(int x, int z, BufferedImage image, Raster data) {
        if (x < 0 || x >= image.getHeight() || z < 0 || z >= image.getHeight()) return 0;
        float height = data.getSample(x, z, 0);
//        float height = image.getRGB(x, z);
//        height += MAX_PIXEL_COLOR / 2f;
//        height /= MAX_PIXEL_COLOR / 2f;
//        height *= MAX_HEIGHT;
        if (height > Height)
            Height = height;
        height /= 256.0;
//        height *= MAX_HEIGHT;
        return height;
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

    private Vector3f calculateNormal(int x, int z, BufferedImage image, Raster data) {
        double heightL = getHeight(x - 1, z, image, data);
        double heightR = getHeight(x + 1, z, image, data);
        double heightD = getHeight(x, z - 1, image, data);
        double heightU = getHeight(x, z + 1, image, data);
        Vector3f normal = new Vector3f(heightL - heightR, 2, heightD - heightU);

        normal.normalise();
        return normal;
    }

}