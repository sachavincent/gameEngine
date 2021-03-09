package terrains;

import static entities.Camera.Direction.EAST;
import static entities.Camera.Direction.NORTH;
import static entities.Camera.Direction.SOUTH;
import static entities.Camera.Direction.WEST;

import entities.Camera.Direction;
import items.ConnectableItem;
import items.ConnectableItem.Connections;
import items.Item;
import items.PlaceHolderConnectableItem;
import items.PlaceHolderItem;
import items.abstractItem.AbstractItem;
import items.buildings.BuildingItem;
import items.buildings.houses.HouseItem;
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
import pathfinding.RouteFinder.Route;
import people.SocialClass;
import renderEngine.Loader;
import renderEngine.MasterRenderer;
import services.BuildingRequirementsService;
import services.ServiceManager;
import textures.TerrainTexture;
import textures.TerrainTexturePack;
import util.math.Maths;
import util.math.Vector2f;
import util.math.Vector3f;

public class Terrain {

    public final static int NB_SOCIAL_CLASSES = SocialClass.getNbClasses();

    public static final  int   SIZE             = 1500;
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

    private AbstractItem previewedItem;

    private Set<TerrainPosition> previewItemPositions = new HashSet<>();
    private List<Item>           items                = new ArrayList<>();

    private RoadGraph roadGraph;

    private int numberOfPeople;

    public static Terrain                                     instance;
    private       boolean                                     needUpdatePaths;
    private       List<Route>                                 paths;
    private final ServiceManager<BuildingRequirementsService> serviceManager;

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
        this.serviceManager = new ServiceManager<>();
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

    public Set<TerrainPosition> getPreviewItemPositions() {
        return Collections.unmodifiableSet(this.previewItemPositions);
    }

    public AbstractItem getPreviewedItem() {
        return this.previewedItem;
    }

    public void setPreviewedItem(AbstractItem previewedItem) {
        this.previewedItem = previewedItem;
    }

    /**
     * Add preview item to the terrain
     *
     * @param previewItemPosition position must be available
     */
    public void addPreviewItem(TerrainPosition previewItemPosition) {
        if (this.previewedItem == null || previewItemPosition == null)
            return;

        //Check previewItem = previewedItem TODO

        this.previewItemPositions.add(previewItemPosition);

//        Item item = this.previewedItem.getPreviewItem();
//        if (item instanceof BuildingItem) //TODO Useless ?!
        MasterRenderer.getInstance().getItemRenderer().setUpdateNeeded(true);
    }

    public void resetPreviewItems() {
        this.previewItemPositions = new HashSet<>();

        MasterRenderer.getInstance().getItemRenderer().setUpdateNeeded(true);
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

    public void removeItem(TerrainPosition position) {
        items.remove(getItemAtPosition(position));
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
        return this.items.stream().filter(HouseItem.class::isInstance)
                .mapToInt(item -> ((HouseItem) item).getMaxPeopleCapacity()).sum();
    }


    public void removePerson() {
        if (numberOfPeople <= 0)
            return;

        numberOfPeople--;
    }

    public void removeItems(Collection<TerrainPosition> positions) {
        for (TerrainPosition position : positions) {
            removeItem(position);
        }
    }

    public boolean addItem(Item item) {
        MasterRenderer.getInstance().getItemRenderer().setUpdateNeeded(true);

        return items.add(item);
    }

    public List<Item> getItems() {
        return this.items;
    }

    public List<RoadItem> getRoads() {
        return this.items.stream().filter(RoadItem.class::isInstance).map(RoadItem.class::cast)
                .collect(Collectors.toList());
    }

    public List<BuildingItem> getBuildings() {
        return this.items.stream().filter(BuildingItem.class::isInstance).map(BuildingItem.class::cast)
                .collect(Collectors.toList());
    }

    public Set<TerrainPosition> getRoadPositions() {
        return this.items.stream().filter(RoadItem.class::isInstance).map(Item::getPosition)
                .collect(Collectors.toSet());
    }

    public Set<Item> getSelectedItems() {
        return this.items.stream().filter(Item::isSelected).collect(Collectors.toSet());
    }

    public List<RoadItem> getRoadsConnectedToItem(BuildingItem buildingItem) {
        List<RoadItem> roads = new ArrayList<>();
        TerrainPosition fromPosition = buildingItem.getPosition();
        for (int i = 0; i < 4; i++) {
            Connections connection = buildingItem.getConnected()[i];
            if (connection == Connections.ROAD) {
                Direction direction = Direction.values()[i];

                TerrainPosition roadPosition = fromPosition
                        .add(Direction.toRelativeDistance(direction, buildingItem.getOffsets()[i] + 1));
                int offset = buildingItem.getOffsets()[(i + 1) % 4];
                Direction newDirection = Direction.values()[(i + 1) % 4];
                roadPosition = roadPosition.add(Direction.toRelativeDistance(newDirection, offset));
                for (int j = 0; j <= offset + buildingItem.getOffsets()[(i + 3) % 4]; j++) {
                    TerrainPosition distance = roadPosition
                            .add(Direction.toRelativeDistance(newDirection.getOppositeDirection(), j));
                    Item itemAtPosition = getItemAtPosition(distance);
                    if (itemAtPosition instanceof RoadItem)
                        roads.add((RoadItem) itemAtPosition);
                }
            }
        }
        return roads;
    }

    public Item putItemIfSpace(AbstractItem abstractItem, TerrainPosition position) {
        Item item = abstractItem.newInstance(position);

        int xNegativeOffset = item.getxNegativeOffset();
        int xPositiveOffset = item.getxPositiveOffset();
        int zNegativeOffset = item.getzNegativeOffset();
        int zPositiveOffset = item.getzPositiveOffset();

        List<Item> tempItems = new ArrayList<>();
        if (item instanceof ConnectableItem) {
            ConnectableItem connectableItem = (ConnectableItem) item;
            boolean[] accessPoints = connectableItem.getAccessPoints();

            for (int x = position.getX() - xNegativeOffset; x <= position.getX() + xPositiveOffset; x++) {
                for (int z = position.getZ() - zNegativeOffset; z <= position.getZ() + zPositiveOffset; z++) {
                    TerrainPosition pos = new TerrainPosition(x, z);
                    if (isPositionOccupied(pos))
                        return null;

                    if (pos.equals(position))
                        continue;

                    TerrainPosition relativePosToItem = new TerrainPosition(x - position.getX(), z - position.getZ());

                    Set<Direction> connectableDirections = new HashSet<>();
                    if (x == position.getX() - xNegativeOffset)
                        connectableDirections.add(SOUTH);
                    else if (x == position.getX() + xPositiveOffset)
                        connectableDirections.add(NORTH);

                    if (z == position.getZ() - zNegativeOffset)
                        connectableDirections.add(WEST);
                    else if (z == position.getZ() + zPositiveOffset)
                        connectableDirections.add(EAST);

                    PlaceHolderItem placeHolderItem;
                    if (connectableDirections.stream().map(Direction::ordinal).anyMatch(
                            index -> accessPoints[index])) { // pos is on a border and on a connectable side
                        placeHolderItem = new PlaceHolderConnectableItem(connectableItem, relativePosToItem);
                        connectableDirections.forEach(direction -> {
                            TerrainPosition newPos = pos.add(Direction.toRelativeDistance(direction));
                            Item itemAtPosition = getItemAtPosition(newPos);
                            if (itemAtPosition instanceof RoadItem) {
                                ((PlaceHolderConnectableItem) placeHolderItem).connect(direction, Connections.ROAD);
                                //TODO ROAD ET BUILDING? Normalement pas
                            }
                        });
                    } else { // Inside the perimeter or not on connectable side => not connectable
                        placeHolderItem = new PlaceHolderItem(item, relativePosToItem);
                    }
                    tempItems.add(placeHolderItem);
                }
            }
        }

        tempItems.add(item);

        tempItems.forEach(this::addItem);

        return item;
    }

    // Check everything for requirements
    // TODO: Optimize
    public void updateRequirements() {
        BuildingRequirementsService service = new BuildingRequirementsService(true, r -> {
            this.paths = r;
            needUpdatePaths = true;
        });

        this.serviceManager.addService(service);
        this.serviceManager.execute();
//        List<Route> paths = new ArrayList<>();
//        getBuildings().stream()
//                .filter(RequireBuilding.class::isInstance)
//                .filter(buildingItem -> !((RequireBuilding) buildingItem).doesMeetAllRequirements())
//                .forEach(item -> {
//                    final RequireBuilding requiringBuilding = (RequireBuilding) item;
//
//                    Set<Route> foundRoutes = new TreeSet<>(Comparator.comparingInt(Route::getCost));
//
//                    if (((ConnectableItem) requiringBuilding).isConnected()) {
//                        requiringBuilding.getRequirements().forEach((neededBuilding, maxLength) -> {
//                            Route route = RouteFinder.findRoute(item, neededBuilding, maxLength);
//                            if (!route.isEmpty()) {
//                                foundRoutes.add(route);
//
//                                requiringBuilding.meetRequirement(neededBuilding, route);
//                            }
//                        });
//                    }
////                    System.out.println(bestRoute);
//                    if (!foundRoutes.isEmpty()) { // Route found
//                        paths.addAll(foundRoutes);
//                    }
//                });
//
//        setHightlightedPaths(paths);
    }

    public Direction[] getConnectionsToRoadItem(TerrainPosition itemPosition, boolean onlyRoad) {
        Item item = getItemAtPosition(itemPosition);

        if (item == null && onlyRoad || (!(item instanceof RoadItem) && !onlyRoad))
            return new Direction[0];

        if (!(item instanceof ConnectableItem))
            return new Direction[0];

        Terrain terrain = Terrain.getInstance();

        Set<Direction> directions = new TreeSet<>();
        ConnectableItem connectableItem = (ConnectableItem) item;
        boolean[] accessPoints = connectableItem.getAccessPoints();

        for (Direction direction : Direction.values()) {
            if (accessPoints[direction.ordinal()]) {
                TerrainPosition connectedItemPosition = new TerrainPosition(itemPosition)
                        .add(Direction.toRelativeDistance(direction));
                Item connectedItem = terrain.getItemAtPosition(connectedItemPosition);
                if (connectedItem == null)
                    continue;

                if (connectedItem instanceof RoadItem || !onlyRoad)
                    directions.add(direction);
            }
        }

        return directions.toArray(new Direction[0]);
    }

    public void setHightlightedPaths(List<Route> routeList) {
        List<RawModel> paths = new ArrayList<>();
        if (!routeList.isEmpty()) {
//            System.out.println("Highlighted paths: ");

            for (Route routes : routeList) {
//                System.out.println("\tPath: " + routes);
//                System.out.println();
//                System.out.println();
                if (routes.size() == 0)
                    continue;

                Set<Vector2f> positions = new LinkedHashSet<>();
                routes.forEach(routeRoad -> {
                    List<Road> roads = new ArrayList<>(routeRoad.getRoute());

                    if (roads.size() == 1) {
                        TerrainPosition position = roads.get(0).getPosition();

                        RoadItem item = (RoadItem) getItemAtPosition(position);

                        for (Direction dir : Direction.values()) {
                            if (item.isConnected(dir)) {
                                TerrainPosition pos = position.add(Direction.toRelativeDistance(dir));
                                positions.add(new Vector2f(pos.getX() - .5f, pos.getZ() - .5f));
                                if (positions.size() == 2) // if too many connections then visuals fuck up
                                    break;
                            }
                        }
                    } else
                        roads.stream().map(Road::getPosition)
                                .forEach(pos -> positions.add(new Vector2f(pos.getX() - .5f, pos.getZ() - .5f)));
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

    /**
     * Testing purposes
     */
    public void resetRoadGraph() {
        this.roadGraph = new RoadGraph();
    }
//    public Direction[] getRoadDirections(Vector2f position) {
//        return getConnectionsToRoadItem(position, false);
//    }

    private RoadGraph createRoadGraph() {
        final RoadGraph roadGraph = new RoadGraph();

        Map<RoadNode, Direction[]> nodes = new HashMap<>();

        getRoads().forEach(road -> {
            TerrainPosition pos = road.getPosition();
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

        return roadGraph;
    }

    public Road getRoad(TerrainPosition roadPosition) {
        Direction[] roadDirections = getConnectionsToRoadItem(roadPosition, true);

//        if (roadDirections.length == 0)
//            return null;

        return roadDirections.length > 2 ? new RoadNode(roadPosition) : new NormalRoad(roadPosition);
    }

    /**
     * Method created for testing purposes
     */
    public void resetItems() {
        items = new ArrayList<>();
    }

    public void placePreviewItems() {
        if (this.previewedItem == null)
            return;

        TerrainPosition[] positions = this.previewItemPositions.toArray(new TerrainPosition[0]);
        if (positions.length == 1)
            this.previewedItem.place(positions[0]);
        else
            this.previewedItem.place(positions);


        MasterRenderer.getInstance().getItemRenderer().setUpdateNeeded(true);
    }

    public boolean canItemBePlaced(AbstractItem abstractItem, TerrainPosition terrainPosition) {
        List<TerrainPosition> relativePositions = Arrays.asList(abstractItem.getPreviewItem().getOffsetPositions());
        List<TerrainPosition> positions = relativePositions.stream().map(p -> p.add(terrainPosition))
                .collect(Collectors.toList());
        return this.items.stream().allMatch(item -> !positions.contains(item.getPosition()));
    }

    public boolean isPositionAvailable(TerrainPosition position) {
        return this.items.stream().noneMatch(item -> item.getPosition().equals(position));
    }

    public boolean isPositionOccupied(TerrainPosition position) {
        return this.items.stream().anyMatch(item -> item.getPosition().equals(position));
    }

    public Item getItemAtPosition(TerrainPosition position) {
        return this.items.stream().filter(item -> item.getPosition().equals(position)).findFirst().orElse(null);
    }

    public Set<Item> getItemsOfType(AbstractItem abstractItem) {
        return this.items.stream().filter(item -> item.getClass() == abstractItem.getPreviewItem().getClass())
                .collect(Collectors.toSet());
    }

    public void updateHighlightedPaths() {
        if (needUpdatePaths) {
            setHightlightedPaths(paths);

            needUpdatePaths = false;
        }
    }
}
