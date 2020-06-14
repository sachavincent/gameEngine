package terrains;

import static entities.Camera.Direction.EAST;
import static entities.Camera.Direction.NORTH;
import static entities.Camera.Direction.SOUTH;
import static entities.Camera.Direction.WEST;

import abstractItem.AbstractItem;
import entities.Camera.Direction;
import items.ConnectableItem;
import items.Item;
import items.PlaceHolderConnectableItem;
import items.PlaceHolderItem;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javax.imageio.ImageIO;
import models.RawModel;
import renderEngine.Loader;
import textures.TerrainTexture;
import textures.TerrainTexturePack;
import util.math.Maths;
import util.math.Vector2f;
import util.math.Vector3f;

public class Terrain {

    public static final  int   SIZE             = 150;
    private static final float MAX_HEIGHT       = 40;
    private static final float MAX_PIXEL_COLOUR = 256 * 256 * 256;

    private float    x;
    private float    y;
    private float    z;
    private RawModel model, modelGrid;
    private TerrainTexturePack texturePack;
    private TerrainTexturePack redTexturePack;
    private TerrainTexture     blendMap;

    private float[][] heights;

    private Vector2f            previewItemPosition;
    private Map<Vector2f, Item> items = new HashMap<>();

    public static Terrain instance;

    public static Terrain getInstance() {
        return instance == null ? (instance = new Terrain()) : instance;
    }

    private Terrain() {
        TerrainTexture backgroundTexture = new TerrainTexture("blue.png");
        TerrainTexture rTexture = new TerrainTexture("red.png");
        TerrainTexture gTexture = new TerrainTexture("green.png");
        TerrainTexture bTexture = new TerrainTexture("blue.png");
        this.texturePack = new TerrainTexturePack(backgroundTexture, bTexture, gTexture, bTexture);
        this.redTexturePack = new TerrainTexturePack(rTexture, rTexture, rTexture, rTexture);
        this.blendMap = new TerrainTexture("white.png");
        this.x = 0.5f;
        this.z = 0.5f;
        this.model = generateTerrain("black.png");
    }

    public void placeItem(AbstractItem item, Vector2f position) {
        try {
            if (position.x < x || position.x > SIZE || position.y < z || position.y > 150)
                throw new IllegalStateException("Clicked out of terrain");

            if (position.equals(previewItemPosition)) {
                items.remove(previewItemPosition);
                resetPreviewItem();
            }

            item.place(this, position);
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }

    public TerrainTexturePack getRedTexturePack() {
        return this.redTexturePack;
    }

    public Vector2f getPreviewItemPosition() {
        return this.previewItemPosition;
    }

    public void setPreviewItem(Vector2f previewItemPosition, AbstractItem previewItem) {
        if ((this.previewItemPosition != null && this.items.get(previewItemPosition) != null) ||
                this.items.get(previewItemPosition) == null) { // Rien dans cet emplacement
            this.items.remove(this.previewItemPosition);
            resetPreviewItem();

            this.previewItemPosition = previewItemPosition;

            this.items.put(previewItemPosition, previewItem.getPreviewItem());
        }
    }

    public void resetPreviewItem() {
        this.previewItemPosition = null;
    }

    public RawModel getModelGrid() {
        return this.modelGrid;
    }

    //    private RawModel generateTerrain(Loader loader) {
//        HeightsGenerator generator = new HeightsGenerator();
//
//        int vertexCount;
//
//        vertexCount = 128;
//        heights = new float[vertexCount][vertexCount];
//
//        float minX = Integer.MAX_VALUE;
//        float minY = Integer.MAX_VALUE;
//        float minZ = Integer.MAX_VALUE;
//        float maxX = 0;
//        float maxY = 0;
//        float maxZ = 0;
//
//        int count = vertexCount * vertexCount;
//        float[] vertices = new float[count * 3];
//        float[] normals = new float[count * 3];
//        float[] textureCoords = new float[count * 2];
//        int[] indices = new int[6 * (vertexCount - 1) * (vertexCount - 1)];
//        int vertexPointer = 0;
//        for (int i = 0; i < vertexCount; i++) {
//            for (int j = 0; j < vertexCount; j++) {
//                float x = (float) j / ((float) vertexCount - 1) * SIZE;
//                float z = (float) i / ((float) vertexCount - 1) * SIZE;
//
//                vertices[vertexPointer * 3] = x;
//                float height = getHeight(j, i, generator);
//                heights[j][i] = height;
//
//
//                vertices[vertexPointer * 3 + 1] = height;
//                vertices[vertexPointer * 3 + 2] = z;
//
//                if (x < minX)
//                    minX = x;
//                else if (x > maxX)
//                    maxX = x;
//
//                if (height < minY)
//                    minY = height;
//                else if (height > maxY)
//                    maxY = height;
//
//                if (z < minZ)
//                    minZ = z;
//                else if (z > maxZ)
//                    maxZ = z;
//
//                Vector3f normal = calculateNormal(j, i, generator);
//                normals[vertexPointer * 3] = normal.x;
//                normals[vertexPointer * 3 + 1] = normal.y;
//                normals[vertexPointer * 3 + 2] = normal.z;
//                textureCoords[vertexPointer * 2] = (float) j / ((float) vertexCount - 1);
//                textureCoords[vertexPointer * 2 + 1] = (float) i / ((float) vertexCount - 1);
//                vertexPointer++;
//            }
//        }
//
//        float width = Math.abs(maxX - minX);
//        float depth = Math.abs(maxZ - minZ);
//        float height = Math.abs(maxY - minY);
//        int pointer = 0;
//        for (int gz = 0; gz < vertexCount - 1; gz++) {
//            for (int gx = 0; gx < vertexCount - 1; gx++) {
//                int topLeft = (gz * vertexCount) + gx;
//                int topRight = topLeft + 1;
//                int bottomLeft = ((gz + 1) * vertexCount) + gx;
//                int bottomRight = bottomLeft + 1;
//                indices[pointer++] = topLeft;
//                indices[pointer++] = bottomLeft;
//                indices[pointer++] = topRight;
//                indices[pointer++] = topRight;
//                indices[pointer++] = bottomLeft;
//                indices[pointer++] = bottomRight;
//            }
//        }
//        return loader.loadToVAO(vertices, textureCoords, normals, indices, width, depth, height);
//    }
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
        heights = new float[VERTEX_COUNT][VERTEX_COUNT];
        float[] vertices = new float[count * 3];
        float[] normals = new float[count * 3];
        float[] textureCoords = new float[count * 2];
        int[] indices = new int[6 * (VERTEX_COUNT - 1) * (VERTEX_COUNT)];
        int vertexPointer = 0;
        for (int i = 0; i < VERTEX_COUNT; i++) {
            for (int j = 0; j < VERTEX_COUNT; j++) {
                vertices[vertexPointer * 3] = (j / (VERTEX_COUNT - 1f) * SIZE);
                float height = getHeight(j, i, image);
                vertices[vertexPointer * 3 + 1] = height;
                heights[j][i] = height;
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

        generateGridTerrain(SIZE, SIZE, textureCoords, image);

        image.getGraphics().dispose();

        return Loader.getInstance().loadToVAO(vertices, textureCoords, normals, indices);
    }

    public float getY() {
        return this.y;
    }

    public void generateGridTerrain(int gridX, int gridZ, float[] textureCoords, BufferedImage image) {
        float[] tab = new float[(gridX + 1) * 6 + (gridZ + 1) * 6];
        int[] indicesTab = new int[(gridX + 1) * (gridZ + 1) * 2];
        float[] normalsTab = new float[(gridX + 1) * 6 + (gridZ + 1) * 6];

        int vertexPointer = 0;
        int normalsPointer = 0;

        int j = 0;

        for (int i = 0; i <= SIZE; i += SIZE / gridX) {
//                float heighzt = getHeight(j, i, image);
            float height = 0f;

            tab[vertexPointer * 3] = 0;
            tab[vertexPointer * 3 + 1] = height;
            tab[vertexPointer * 3 + 2] = i;
            indicesTab[j] = j++;

            Vector3f normal = calculateNormal(0, i, image);
            normalsTab[normalsPointer * 3] = normal.x;
            normalsTab[normalsPointer * 3 + 1] = normal.y;
            normalsTab[normalsPointer * 3 + 2] = normal.z;

            vertexPointer++;
            normalsPointer++;

            tab[vertexPointer * 3] = SIZE;
            tab[vertexPointer * 3 + 1] = height;
            tab[vertexPointer * 3 + 2] = i;
            indicesTab[j] = j++;

            normal = calculateNormal(SIZE, i, image);
            normalsTab[normalsPointer * 3] = normal.x;
            normalsTab[normalsPointer * 3 + 1] = normal.y;
            normalsTab[normalsPointer * 3 + 2] = normal.z;

            vertexPointer++;
            normalsPointer++;
        }

        for (int i = 0; i <= SIZE; i += SIZE / gridZ) {
            float height = 0f;

            tab[vertexPointer * 3] = i;
            tab[vertexPointer * 3 + 1] = height;
            tab[vertexPointer * 3 + 2] = 0;
            indicesTab[j] = j++;

            Vector3f normal = calculateNormal(i, 0, image);
            normalsTab[normalsPointer * 3] = normal.x;
            normalsTab[normalsPointer * 3 + 1] = normal.y;
            normalsTab[normalsPointer * 3 + 2] = normal.z;

            vertexPointer++;
            normalsPointer++;

            tab[vertexPointer * 3] = i;
            tab[vertexPointer * 3 + 1] = height;
            tab[vertexPointer * 3 + 2] = SIZE;
            indicesTab[j] = j++;

            normal = calculateNormal(i, SIZE, image);
            normalsTab[normalsPointer * 3] = normal.x;
            normalsTab[normalsPointer * 3 + 1] = normal.y;
            normalsTab[normalsPointer * 3 + 2] = normal.z;

            vertexPointer++;
            normalsPointer++;
        }

        modelGrid = Loader.getInstance().loadToVAO(tab, textureCoords, normalsTab, indicesTab);
    }

    public void setY(float y) {
        this.y = y;
    }

    //    private Vector3f calculateNormal(int x, int z, HeightsGenerator generator) {
//        float heightL = getHeight(x - 1, z, generator);
//        float heightR = getHeight(x + 1, z, generator);
//        float heightD = getHeight(x, z - 1, generator);
//        float heightU = getHeight(x, z + 1, generator);
//w
//        Vector3f normal = new Vector3f(heightL - heightR, 2f, heightD - heightU);
//        normal.normalise();
//
//        return normal;
//    }

    private Vector3f calculateNormal(int x, int z, BufferedImage image) {
        float heightL = getHeight(x - 1, z, image);
        float heightR = getHeight(x + 1, z, image);
        float heightD = getHeight(x, z - 1, image);
        float heightU = getHeight(x, z + 1, image);
        Vector3f normal = new Vector3f(heightL - heightR, 2f, heightD - heightU);
        normal.normalise();
        return normal;
    }


    public float getHeightOfTerrain(float worldX, float worldZ) {
        float terrainX = worldX - this.x;
        float terrainZ = worldZ - this.z;
        float gridSquareSize = SIZE / ((float) heights.length - 1);
        int gridX = (int) Math.floor(terrainX / gridSquareSize);
        int gridZ = (int) Math.floor(terrainZ / gridSquareSize);

        if (gridX >= heights.length - 1 || gridZ >= heights.length - 1 || gridX < 0 || gridZ < 0) {
            return 0;
        }

        float xCoord = (terrainX % gridSquareSize) / gridSquareSize;
        float zCoord = (terrainZ % gridSquareSize) / gridSquareSize;
        float answer;

        if (xCoord <= (1 - zCoord)) {
            answer = Maths.barryCentric(new Vector3f(0, heights[gridX][gridZ], 0), new Vector3f(1,
                    heights[gridX + 1][gridZ], 0), new Vector3f(0,
                    heights[gridX][gridZ + 1], 1), new Vector2f(xCoord, zCoord));
        } else {
            answer = Maths.barryCentric(new Vector3f(1, heights[gridX + 1][gridZ], 0), new Vector3f(1,
                    heights[gridX + 1][gridZ + 1], 1), new Vector3f(0,
                    heights[gridX][gridZ + 1], 1), new Vector2f(xCoord, zCoord));
        }

        return answer;
    }

//    private float getHeight(int x, int z, HeightsGenerator generator) {
//        return generator.generateHeight(x, z);
//    }

    private float getHeight(int x, int z, BufferedImage image) {
        if (x < 0 || x >= image.getHeight() || z < 0 || z >= image.getHeight()) {
            return 0;
        }
        float height = image.getRGB(x, z);
        height += MAX_PIXEL_COLOUR / 2f;
        height /= MAX_PIXEL_COLOUR / 2f;
        height += 1; //todo +1 enlever
        height *= MAX_HEIGHT;

        return height;
    }

    public TerrainTexture getBlendMap() {
        return this.blendMap;
    }

    public TerrainTexturePack getTexturePack() {
        return this.texturePack;
    }

    public float getX() {
        return x;
    }

    public float getZ() {
        return z;
    }

    public RawModel getModel() {
        return model;
    }


    public void removeItem(Vector2f position) {
        items.remove(position);
    }

    public Map<Vector2f, Item> getItems() {
        return this.items;
    }

    public Set<Item> getSelectedItems() {
        return this.items.values().stream().filter(Item::isSelected).collect(Collectors.toSet());
    }

    public boolean putItemIfSpace(Vector2f position, Item item) {
        int halfXWidth = item.getxWidth() / 2;
        int halfZWidth = item.getzWidth() / 2;

        Map<Vector2f, Item> tempItems = new HashMap<>(items);
        for (int x = (int) (position.x - halfXWidth); x <= position.x + halfXWidth; x++) {
            for (int z = (int) position.y - halfZWidth; z <= position.y + halfZWidth; z++) {
                Vector2f pos = new Vector2f(x, z);
                if (pos.equals(position))
                    continue;

                boolean addedItem = false;

                Vector2f relativePosToItem = new Vector2f(position.x - x, position.y - z);
                if (item instanceof ConnectableItem) {
                    Direction direction = null;
                    if (x == position.x - halfXWidth && z == position.y)
                        direction = SOUTH;
                    else if (x == position.x + halfXWidth && z == position.y)
                        direction = NORTH;
                    else if (z == position.y - halfZWidth && x == position.x)
                        direction = WEST;
                    else if (z == position.y + halfZWidth && x == position.x)
                        direction = EAST;

                    if (direction != null && ((ConnectableItem) item).getAccessPoints()[direction.ordinal()]) {
                        PlaceHolderConnectableItem placeHolderConnectableItem = new PlaceHolderConnectableItem(
                                (ConnectableItem) item, relativePosToItem);
                        placeHolderConnectableItem.connect(direction);
                        if (tempItems.putIfAbsent(pos, placeHolderConnectableItem) != null)
                            return false;

                        addedItem = true;
                    }
                }
                if (!addedItem) {
                    PlaceHolderItem placeHolderItem = new PlaceHolderItem(item, relativePosToItem);
                    if (tempItems.putIfAbsent(pos, placeHolderItem) != null)
                        return false;
                }
            }
        }

        tempItems.put(position, item);
        items = tempItems;

        return true;
    }
}
