package terrains;

import static entities.Camera.Direction.EAST;
import static entities.Camera.Direction.NORTH;
import static entities.Camera.Direction.SOUTH;
import static entities.Camera.Direction.WEST;

import abstractItem.AbstractItem;
import entities.Camera.Direction;
import items.ConnectableItem;
import items.EmptyItem;
import items.Item;
import items.PlaceHolderConnectableItem;
import items.PlaceHolderItem;
import items.buildings.BuildingItem;
import items.buildings.houses.HouseItem;
import items.buildings.houses.RequireBuilding;
import items.roads.RoadItem;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import javax.imageio.ImageIO;
import models.RawModel;
import pathfinding.NormalRoad;
import pathfinding.Road;
import pathfinding.RoadGraph;
import pathfinding.RoadNode;
import pathfinding.RouteFinder;
import pathfinding.RouteFinder.Route;
import pathfinding.RouteRoad;
import people.SocialClass;
import renderEngine.Loader;
import renderEngine.MasterRenderer;
import textures.TerrainTexture;
import textures.TerrainTexturePack;
import util.math.Maths;
import util.math.Vector2f;
import util.math.Vector3f;

public class Terrain {

    public final static int NB_SOCIAL_CLASSES = SocialClass.getNbClasses();

    public static final  int   SIZE             = 150;
    private static final float MAX_HEIGHT       = 40;
    private static final float MAX_PIXEL_COLOUR = 256 * 256 * 256;

    private final float              x;
    private       float              y;
    private final float              z;
    private final RawModel           model;
    private       RawModel           modelGrid;
    private final TerrainTexturePack texturePack;
    private final TerrainTexturePack redTexturePack;
    private final TerrainTexturePack blueTexturePack;
    private final TerrainTexture     blendMap;

    private float[][] heights;

    private AbstractItem        previewedItem;
    private Set<Vector2f>       previewItemPositions = new HashSet<>();
    private Map<Vector2f, Item> items                = new HashMap<>();

    private RoadGraph roadGraph;

    private int numberOfPeople;

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
        this.blueTexturePack = new TerrainTexturePack(bTexture, bTexture, bTexture, bTexture);
        this.blendMap = new TerrainTexture("white.png");
        this.x = 0.5f;
        this.z = 0.5f;
        this.model = generateTerrain("black.png");
        //TODO: add items from save
        this.roadGraph = createRoadGraph();

        for (int x = 0; x < SIZE; x++) {
            for (int y = 0; y < SIZE; y++) {
                items.put(new Vector2f(x, y), new EmptyItem());
            }
        }
    }

//    public void placeItem(AbstractItem item, Vector2f position) {
//        if (item == null)
//            return;
//
//        try {
//            if (position.x < x || position.x > SIZE || position.y < z || position.y > 150)
//                throw new IllegalStateException("Clicked out of terrain");
//
//            item.place(position);
//        } catch (IllegalStateException e) {
//            e.printStackTrace();
//        }
//    }

    public TerrainTexturePack getRedTexturePack() {
        return this.redTexturePack;
    }

    public Set<Vector2f> getPreviewItemPositions() {
        return this.previewItemPositions;
    }

    public AbstractItem getPreviewedItem() {
        return this.previewedItem;
    }

    public void setPreviewedItem(AbstractItem previewedItem) {
        this.previewedItem = previewedItem;
    }

    public void addPreviewItem(Vector2f previewItemPosition, AbstractItem previewItem) {
        if (previewItem == null)
            return;

        if (this.previewedItem == null)
            setPreviewedItem(previewItem);

//Check previewItem = previewedItem TODO
        if (this.items.get(previewItemPosition) instanceof EmptyItem) { // Rien dans cet emplacement
//            removeItem(this.previewItemPosition);
//            resetPreviewItem();

            this.previewItemPositions.add(previewItemPosition);

            addItem(previewItemPosition, previewItem.getPreviewItem());
        }
    }

    public void replacePreviewItems(List<Vector2f> positions, AbstractItem selectedItem) {
        List<Vector2f> toBeRemoved = this.previewItemPositions.stream().filter(pos -> !positions.contains(pos))
                .collect(Collectors.toList());
        this.previewItemPositions.removeAll(toBeRemoved);
        toBeRemoved.forEach(this::removeItem);

        for (Vector2f position : positions)
            addPreviewItem(position, selectedItem);
    }

    public void resetPreviewItems(boolean safeDelete) {
        if (safeDelete)
            removeItems(this.previewItemPositions);

        this.previewItemPositions = new HashSet<>();
        this.previewedItem = null;
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

    public TerrainTexturePack getBlueTexturePack() {
        return this.blueTexturePack;
    }

    private void generateGridTerrain(int gridX, int gridZ, float[] textureCoords, BufferedImage image) {
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
        if (!(items.get(position) instanceof EmptyItem)) {
            items.replace(position, new EmptyItem());
        }
    }

    public int getNumberOfPeople() {
        return this.numberOfPeople;
    }

    public boolean addPerson() {
        if (numberOfPeople >= getMaxPeopleCapacity())
            return false;

        numberOfPeople++;

        return true;
    }

    public int getMaxPeopleCapacity() {
        return this.items.values().stream().filter(HouseItem.class::isInstance)
                .mapToInt(item -> ((HouseItem) item).getMaxPeopleCapacity()).sum();
    }


    public void removePerson() {
        if (numberOfPeople <= 0)
            return;

        numberOfPeople--;
    }

    public void removeItems(Collection<Vector2f> positions) {
        for (Vector2f position : positions) {
//            if (!(items.get(position) instanceof EmptyItem)) {
            items.replace(position, new EmptyItem());
//            }
        }
    }

    public boolean addItem(Vector2f position, Item item) {
        if (item instanceof BuildingItem)
            MasterRenderer.getInstance().getBuildingRenderer().setUpdateNeeded(true);

        if (items.get(position) instanceof EmptyItem)
            return items.replace(position, item) != null;

        return false;
    }

    public boolean isPositionAvailable(Vector2f position) {
        return items.get(position) instanceof EmptyItem;
    }

    public Map<Vector2f, Item> getItems() {
        return this.items.entrySet().stream().filter(entry -> !(entry.getValue() instanceof EmptyItem))
                .collect(Collectors.toMap(Entry::getKey, Entry::getValue));
    }

    public Map<Vector2f, Item> getRoads() {
        return this.items.entrySet().stream().filter(entry -> entry.getValue() instanceof RoadItem)
                .collect(Collectors.toMap(Entry::getKey, Entry::getValue));
    }

    public Map<Vector2f, BuildingItem> getBuildings() {
        return this.items.entrySet().stream().filter(entry -> entry.getValue() instanceof BuildingItem)
                .map(entry -> new AbstractMap.SimpleEntry<>(entry.getKey(), (BuildingItem) entry.getValue()))
                .collect(Collectors.toMap(Entry::getKey, Entry::getValue));
    }

    public Set<Vector2f> getRoadPositions() {
        return this.items.entrySet().stream().filter(entry -> entry.getValue() instanceof RoadItem)
                .map(Entry::getKey).collect(Collectors.toSet());
    }

    public Set<Vector2f> getEmptyPositions() {
        return this.items.entrySet().stream().filter(entry -> entry.getValue() instanceof EmptyItem)
                .map(Entry::getKey).collect(Collectors.toSet());
    }

    public Set<Item> getSelectedItems() {
        return this.items.values().stream().filter(Item::isSelected).collect(Collectors.toSet());
    }

    public boolean putItemIfSpace(Vector2f position, Item item) {
        int xNegativeOffset = item.getxNegativeOffset();
        int xPositiveOffset = item.getxPositiveOffset();
        int zNegativeOffset = item.getzNegativeOffset();
        int zPositiveOffset = item.getzPositiveOffset();

        Map<Vector2f, Item> tempItems = new HashMap<>();
        for (int x = (int) (position.x - xNegativeOffset); x <= position.x + xPositiveOffset; x++) {
            for (int z = (int) position.y - zNegativeOffset; z <= position.y + zPositiveOffset; z++) {
                Vector2f pos = new Vector2f(x, z);
                if (pos.equals(position))
                    continue;

                boolean addedItem = false;

                Vector2f relativePosToItem = new Vector2f(position.x - x, z - position.y);
                if (item instanceof ConnectableItem) {
                    Set<Direction> directions = new HashSet<>();
                    if (x == position.x - xNegativeOffset)
                        directions.add(SOUTH);
                    else if (x == position.x + xPositiveOffset)
                        directions.add(NORTH);

                    if (z == position.y - zNegativeOffset)
                        directions.add(EAST);
                    else if (z == position.y + zPositiveOffset)
                        directions.add(WEST);

                    if (!directions.isEmpty()) {
                        PlaceHolderConnectableItem placeHolderConnectableItem = new PlaceHolderConnectableItem(
                                (ConnectableItem) item, relativePosToItem);
//                        directions.forEach(direction -> {
//                            if (((ConnectableItem) item).isConnected(direction))
//                                placeHolderConnectableItem
//                                        .connect(direction.getOppositeDirection(),
//                                                Connections.ROAD);//TODO ROAD ET BUILDING? Normalement pas
//                        });
                        if (isPositionAvailable(pos)) {
                            tempItems.put(pos, placeHolderConnectableItem);
                            addedItem = true;
                        }
                    }
                }
                if (!addedItem) { // Inside the perimeter, not connectable
                    PlaceHolderItem placeHolderItem = new PlaceHolderItem(item, relativePosToItem);
                    if (isPositionAvailable(pos))
                        tempItems.put(pos, placeHolderItem);
                    else
                        return false;
                }
            }
        }


        tempItems.put(position, item);

        tempItems.forEach(this::addItem);

        return true;
    }

    // Check everything for requirements
    // TODO: Optimize (no need to check everything?)
    public void updateRequirements() {
        List<Route<RouteRoad>> paths = new ArrayList<>();

        getItems().entrySet().stream().filter(entry -> entry.getValue() instanceof RequireBuilding &&
                entry.getValue() instanceof ConnectableItem).forEach(entry -> {
            final RequireBuilding requiringBuilding = (RequireBuilding) entry.getValue();

            Set<Route<RouteRoad>> foundRoutes = new TreeSet<>(Comparator.comparingInt(Route::getCost));

            for (Direction direction : Direction.values()) {
                if (((ConnectableItem) requiringBuilding).isConnected(direction)) {
                    requiringBuilding.getRequirements().forEach((neededBuilding, maxLength) -> {
                        Vector2f roadPos = entry.getKey().add(entry.getValue().getOffset(direction)
                                .add(Direction.toRelativeDistance(direction)));
                        Route<RouteRoad> route = RouteFinder.findRoute(roadPos, neededBuilding, maxLength);
                        if (route != null && !route.isEmpty())
                            foundRoutes.add(route);
                    });
                }
            }
            Route<RouteRoad> bestRoute = foundRoutes.stream().findFirst().orElse(new Route<>());
//            System.out.println(foundRoutes);
            if (!bestRoute.isEmpty()) { // Route found
                paths.add(bestRoute);
                requiringBuilding.meetRequirements();
//                foundRoutes.stream().filter(routeRoads -> routeRoads.getCost() == bestRoute.getCost()).forEach(paths::add);
            }
        });

        setHightlightedPaths(paths);
    }

    public Direction[] getConnectionsToRoadItem(Vector2f itemPosition, boolean onlyRoad) {
        Item item = getItems().get(itemPosition);

        if (item == null && onlyRoad || (!(item instanceof RoadItem) && !onlyRoad))
            return new Direction[0];

        if (!(item instanceof ConnectableItem))
            return new Direction[0];

        Set<Direction> directions = new TreeSet<>();
        ConnectableItem connectableItem = (ConnectableItem) item;
        boolean[] accessPoints = connectableItem.getAccessPoints();

        for (Direction direction : Direction.values()) {
            if (accessPoints[direction.ordinal()]) {
                Vector2f connectedItemPosition = new Vector2f(itemPosition)
                        .add(Direction.toRelativeDistance(direction));
                Item connectedItem = getItems().get(connectedItemPosition);
                if (connectedItem == null)
                    continue;

                if (connectedItem instanceof RoadItem || !onlyRoad)
                    directions.add(direction);
            }
        }

        return directions.toArray(new Direction[0]);
    }

    public void updateRoadGraph() {
        this.roadGraph = createRoadGraph();
    }

    public void setHightlightedPaths(List<Route<RouteRoad>> routeList) {
        List<RawModel> paths = new ArrayList<>();
        if (!routeList.isEmpty()) {
//            System.out.println("Highlighted paths: ");

            for (Route<RouteRoad> routes : routeList) {
//                System.out.println("\tPath: " + routes);
//                System.out.println();
//                System.out.println();
                if (routes.size() == 0)
                    continue;

                Set<Vector2f> positions = new LinkedHashSet<>();
                routes.forEach(routeRoad -> {
                    List<Road> roads = new ArrayList<>(routeRoad.getRoute());
                    roads.stream().map(Road::getPosition)
                            .forEach(pos -> {
                                positions.add(new Vector2f(pos.x - .5f, pos.y - .5f));
                            });
                });

                float[] positionsFloat = new float[positions.size() * 3];
                int i = 0;
                for (Vector2f pos : positions) {
                    positionsFloat[i++] = pos.x;
                    positionsFloat[i++] = .5f;
                    positionsFloat[i++] = pos.y;
                }
                int[] indicesTab = new int[positions.size() * 2 - 2];
                int j = 0;
                for (i = 0; i < indicesTab.length; i++) {
                    indicesTab[i++] = j++;
                    indicesTab[i] = j;
                }

                RawModel path = Loader.getInstance()
                        .loadToVAO(positionsFloat, new float[]{0}, new float[]{0, 1, 0}, indicesTab);
                paths.add(path);
            }
        }
        MasterRenderer.getInstance().getTerrainRenderer().setPaths(paths);
    }

    public RoadGraph getRoadGraph() {
        return this.roadGraph;
    }

//    public Direction[] getRoadDirections(Vector2f position) {
//        return getConnectionsToRoadItem(position, false);
//    }

    private RoadGraph createRoadGraph() {
        final RoadGraph roadGraph = new RoadGraph();

        Thread t = new Thread(() -> {
            Map<RoadNode, Direction[]> nodes = new HashMap<>();

            getRoads().keySet().forEach(pos -> {
//                    Direction[] directions = getRoadDirections(pos);
                Direction[] directions = getConnectionsToRoadItem(pos, true);
                if (directions.length >= 3)
                    nodes.put(new RoadNode(pos), directions);
            });

            for (Entry<RoadNode, Direction[]> node : nodes.entrySet()) {
                RoadNode roadNode = node.getKey();
                if (!roadGraph.getNodes().contains(roadNode))
                    roadGraph.searchForNextNode(roadNode.getPosition(), node.getValue(), null);
            }
        });

        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return roadGraph;
    }

    public Road getRoad(Vector2f roadPosition) {
        Direction[] roadDirections = getConnectionsToRoadItem(roadPosition, true);

        if (roadDirections.length == 0)
            return null;

        return roadDirections.length > 2 ? new RoadNode(roadPosition) : new NormalRoad(roadPosition);
    }

    /**
     * Method created for testing purposes
     */
    public void resetItems() {
        items = new HashMap<>();
        for (int x = 0; x < SIZE; x++) {
            for (int y = 0; y < SIZE; y++) {
                items.put(new Vector2f(x, y), new EmptyItem());
            }
        }
    }

    public void placePreviewItems() {
        if (this.previewedItem == null)
            return;

        removeItems(this.previewItemPositions);
        Vector2f[] pos = this.previewItemPositions.toArray(new Vector2f[0]);

        this.previewedItem.place(pos);
    }
}
