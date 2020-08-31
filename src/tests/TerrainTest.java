package tests;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFW.glfwSwapInterval;

import abstractItem.AbstractDirtRoadItem;
import abstractItem.AbstractInsula;
import entities.Camera.Direction;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import pathfinding.RouteFinder;
import renderEngine.DisplayManager;
import terrains.Terrain;
import util.math.Vector2f;

class TerrainTest {

    @BeforeAll
    public static void init() {
        glfwInit();
        DisplayManager.createDisplay();
        glfwSwapInterval(1);
        GL.createCapabilities();
        GL11.glEnable(GL13.GL_MULTISAMPLE);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
    }

    @BeforeEach
    public void resetTerrainItems() {
        Terrain.getInstance().getItems().clear();
    }

    @org.junit.jupiter.api.Test
    void testGetRoadConnectionsToRoadItem() {
        Terrain terrain = Terrain.getInstance();

        AbstractDirtRoadItem abstractDirtRoadItem = new AbstractDirtRoadItem();
        abstractDirtRoadItem.place(terrain, new Vector2f(50, 50));


        Direction[] directions = terrain.getConnectionsToRoadItem(new Vector2f(50, 50), true);

        assertArrayEquals(new Direction[0], directions);
    }

    @org.junit.jupiter.api.Test
    void testGetRoadConnectionsToRoadItem2() {
        Terrain terrain = Terrain.getInstance();

        AbstractDirtRoadItem abstractDirtRoadItem = new AbstractDirtRoadItem();
        AbstractDirtRoadItem abstractDirtRoadItem2 = new AbstractDirtRoadItem();
        Vector2f center = new Vector2f(50, 50);
        abstractDirtRoadItem.place(terrain, center);
        abstractDirtRoadItem2.place(terrain, center.add(Direction.toRelativeDistance(Direction.EAST)));


        Direction[] directions = terrain.getConnectionsToRoadItem(center, true);

        assertArrayEquals(new Direction[]{Direction.EAST}, directions);
    }

    @org.junit.jupiter.api.Test
    void testGetRoadConnectionsToRoadItem3() {
        Terrain terrain = Terrain.getInstance();

        AbstractDirtRoadItem abstractDirtRoadItem = new AbstractDirtRoadItem();
        AbstractDirtRoadItem abstractDirtRoadItem2 = new AbstractDirtRoadItem();
        Vector2f center = new Vector2f(50, 50);
        abstractDirtRoadItem.place(terrain, center);
        abstractDirtRoadItem2.place(terrain, center.add(Direction.toRelativeDistance(Direction.WEST)));


        Direction[] directions = terrain.getConnectionsToRoadItem(center, true);

        assertArrayEquals(new Direction[]{Direction.WEST}, directions);
    }

    @org.junit.jupiter.api.Test
    void testGetRoadConnectionsToRoadItem4() {
        Terrain terrain = Terrain.getInstance();

        AbstractDirtRoadItem abstractDirtRoadItem = new AbstractDirtRoadItem();
        AbstractDirtRoadItem abstractDirtRoadItem2 = new AbstractDirtRoadItem();
        Vector2f center = new Vector2f(50, 50);
        abstractDirtRoadItem.place(terrain, center);
        abstractDirtRoadItem2.place(terrain, center.add(Direction.toRelativeDistance(Direction.SOUTH)));


        Direction[] directions = terrain.getConnectionsToRoadItem(center, true);

        assertArrayEquals(new Direction[]{Direction.SOUTH}, directions);
    }

    @org.junit.jupiter.api.Test
    void testGetRoadConnectionsToRoadItem5() {
        Terrain terrain = Terrain.getInstance();

        AbstractDirtRoadItem abstractDirtRoadItem = new AbstractDirtRoadItem();
        AbstractDirtRoadItem abstractDirtRoadItem2 = new AbstractDirtRoadItem();
        Vector2f center = new Vector2f(50, 50);
        abstractDirtRoadItem.place(terrain, center);
        abstractDirtRoadItem2.place(terrain, center.add(Direction.toRelativeDistance(Direction.NORTH)));


        Direction[] directions = terrain.getConnectionsToRoadItem(center, true);

        assertArrayEquals(new Direction[]{Direction.NORTH}, directions);
    }


    @org.junit.jupiter.api.Test
    void testGetRoadConnectionsToRoadItem6() {
        Terrain terrain = Terrain.getInstance();

        AbstractDirtRoadItem abstractDirtRoadItem = new AbstractDirtRoadItem();
        AbstractDirtRoadItem abstractDirtRoadItem2 = new AbstractDirtRoadItem();
        AbstractDirtRoadItem abstractDirtRoadItem3 = new AbstractDirtRoadItem();
        Vector2f center = new Vector2f(50, 50);
        abstractDirtRoadItem.place(terrain, center);
        abstractDirtRoadItem2.place(terrain, center.add(Direction.toRelativeDistance(Direction.NORTH)));
        abstractDirtRoadItem3.place(terrain, center.add(Direction.toRelativeDistance(Direction.SOUTH)));


        Direction[] directions = terrain.getConnectionsToRoadItem(center, true);

        assertArrayEquals(new Direction[]{Direction.SOUTH, Direction.NORTH}, directions);
    }

    @org.junit.jupiter.api.Test
    void testGetRoadConnectionsToRoadItem7() {
        Terrain terrain = Terrain.getInstance();

        AbstractDirtRoadItem abstractDirtRoadItem = new AbstractDirtRoadItem();
        AbstractDirtRoadItem abstractDirtRoadItem2 = new AbstractDirtRoadItem();
        AbstractDirtRoadItem abstractDirtRoadItem3 = new AbstractDirtRoadItem();
        AbstractInsula abstractInsula = new AbstractInsula();
        Vector2f center = new Vector2f(50, 50);
        abstractDirtRoadItem.place(terrain, center);
        abstractDirtRoadItem2.place(terrain, center.add(Direction.toRelativeDistance(Direction.NORTH)));
        abstractDirtRoadItem3.place(terrain, center.add(Direction.toRelativeDistance(Direction.SOUTH)));
        abstractInsula.place(terrain, center.add(Direction.toRelativeDistance(Direction.EAST)));


        Direction[] directions = terrain.getConnectionsToRoadItem(center, true);

        assertArrayEquals(new Direction[]{Direction.SOUTH, Direction.NORTH}, directions);
    }

    @org.junit.jupiter.api.Test
    void testGetRoadConnectionsToRoadItem8() {
        Terrain terrain = Terrain.getInstance();

        AbstractDirtRoadItem abstractDirtRoadItem = new AbstractDirtRoadItem();
        AbstractDirtRoadItem abstractDirtRoadItem2 = new AbstractDirtRoadItem();
        AbstractDirtRoadItem abstractDirtRoadItem3 = new AbstractDirtRoadItem();
        AbstractInsula abstractInsula = new AbstractInsula();
        Vector2f center = new Vector2f(50, 50);
        abstractDirtRoadItem.place(terrain, center);
        abstractDirtRoadItem2.place(terrain, center.add(Direction.toRelativeDistance(Direction.EAST)));
        abstractDirtRoadItem3.place(terrain, center.add(Direction.toRelativeDistance(Direction.WEST)));
        abstractInsula.place(terrain, center.add(Direction.toRelativeDistance(Direction.EAST)));


        Direction[] directions = terrain.getConnectionsToRoadItem(center, true);

        assertArrayEquals(new Direction[]{Direction.WEST, Direction.EAST}, directions);
    }

    @org.junit.jupiter.api.Test
    void testGetRoadConnectionsToRoadItem9() {
        Terrain terrain = Terrain.getInstance();

        AbstractDirtRoadItem abstractDirtRoadItem = new AbstractDirtRoadItem();
        AbstractDirtRoadItem abstractDirtRoadItem2 = new AbstractDirtRoadItem();
        AbstractDirtRoadItem abstractDirtRoadItem3 = new AbstractDirtRoadItem();
        AbstractDirtRoadItem abstractDirtRoadItem4 = new AbstractDirtRoadItem();
        AbstractDirtRoadItem abstractDirtRoadItem5 = new AbstractDirtRoadItem();
        Vector2f center = new Vector2f(50, 50);
        abstractDirtRoadItem.place(terrain, center);
        abstractDirtRoadItem2.place(terrain, center.add(Direction.toRelativeDistance(Direction.EAST)));
        abstractDirtRoadItem3.place(terrain, center.add(Direction.toRelativeDistance(Direction.WEST)));
        abstractDirtRoadItem4.place(terrain, center.add(Direction.toRelativeDistance(Direction.NORTH)));
        abstractDirtRoadItem5.place(terrain, center.add(Direction.toRelativeDistance(Direction.SOUTH)));


        Direction[] directions = terrain.getConnectionsToRoadItem(center, true);

        assertArrayEquals(new Direction[]{Direction.SOUTH, Direction.NORTH, Direction.WEST, Direction.EAST},
                directions);
    }
//
//    @org.junit.jupiter.api.Test
//    void testGetRoadGraph() {
//        Terrain terrain = Terrain.getInstance();
//
//        AbstractDirtRoadItem abstractDirtRoadItem = new AbstractDirtRoadItem();
//        AbstractDirtRoadItem abstractDirtRoadItem2 = new AbstractDirtRoadItem();
//        AbstractDirtRoadItem abstractDirtRoadItem3 = new AbstractDirtRoadItem();
//        AbstractDirtRoadItem abstractDirtRoadItem4 = new AbstractDirtRoadItem();
//        AbstractDirtRoadItem abstractDirtRoadItem5 = new AbstractDirtRoadItem();
//        Vector2f center = new Vector2f(50, 50);
//        abstractDirtRoadItem.place(terrain, center);
//        abstractDirtRoadItem2.place(terrain, center.add(Direction.toRelativeDistance(Direction.EAST)));
//        abstractDirtRoadItem3.place(terrain, center.add(Direction.toRelativeDistance(Direction.WEST)));
//        abstractDirtRoadItem4.place(terrain, center.add(Direction.toRelativeDistance(Direction.NORTH)));
//        abstractDirtRoadItem5.place(terrain, center.add(Direction.toRelativeDistance(Direction.SOUTH)));
//
//        Map<RoadNode, Set<RouteRoad>> res = terrain.getRoadGraph();
//
//        RoadNode roadNode = new RoadNode(center);
//        Set<RouteRoad> routes = new HashSet<>();
//        Map<RoadNode, Set<RouteRoad>> expected = new HashMap<>();
//        expected.put(roadNode, routes);
//
//
//        assertEquals(expected, res);
//    }
//
//    @org.junit.jupiter.api.Test
//    void testGetRoadGraph2() {
//        Terrain terrain = Terrain.getInstance();
//
//        AbstractDirtRoadItem abstractDirtRoadItem = new AbstractDirtRoadItem();
//        Vector2f v0 = new Vector2f(49, 50);
//        Vector2f v00 = new Vector2f(50, 51);
//        Vector2f center = new Vector2f(50, 50);
//        Vector2f v1 = new Vector2f(51, 50);
//        Vector2f v2 = new Vector2f(52, 50);
//        Vector2f v3 = new Vector2f(53, 50);
//        Vector2f v4 = new Vector2f(53, 51);
//        Vector2f v5 = new Vector2f(53, 49);
//
//        abstractDirtRoadItem.place(terrain, center);
//        abstractDirtRoadItem.place(terrain, v0);
//        abstractDirtRoadItem.place(terrain, v00);
//        abstractDirtRoadItem.place(terrain, v1);
//        abstractDirtRoadItem.place(terrain, v2);
//        abstractDirtRoadItem.place(terrain, v3);
//        abstractDirtRoadItem.place(terrain, v4);
//        abstractDirtRoadItem.place(terrain, v5);
//
//        Map<RoadNode, Set<RouteRoad>> res = terrain.getRoadGraph();
//
//        RoadNode roadNode = new RoadNode(center);
//        RoadNode roadNode2 = new RoadNode(v3);
//        Set<RouteRoad> routes = new HashSet<>();
//        Set<RouteRoad> routes2 = new HashSet<>();
//        Map<RoadNode, Set<RouteRoad>> expected = new HashMap<>();
//        RouteRoad routeRoad = new RouteRoad(roadNode);
//        routeRoad.setEnd(roadNode2);
//        routes.add(routeRoad);
//        expected.put(roadNode, routes);
//
//
//        assertEquals(expected, res);
//    }

    @org.junit.jupiter.api.Test
    void testFindRoute() {
        Terrain terrain = Terrain.getInstance();

        AbstractDirtRoadItem abstractDirtRoadItem = new AbstractDirtRoadItem();
        Vector2f v0 = new Vector2f(49, 50);
        Vector2f v00 = new Vector2f(50, 51);
        Vector2f center = new Vector2f(50, 50);
        Vector2f v1 = new Vector2f(51, 50);
        Vector2f v2 = new Vector2f(52, 50);
        Vector2f v3 = new Vector2f(53, 50);
        Vector2f v4 = new Vector2f(53, 51);
        Vector2f v5 = new Vector2f(53, 49);

        abstractDirtRoadItem.place(terrain, center);
        abstractDirtRoadItem.place(terrain, v0);
        abstractDirtRoadItem.place(terrain, v00);
        abstractDirtRoadItem.place(terrain, v1);
        abstractDirtRoadItem.place(terrain, v2);
        abstractDirtRoadItem.place(terrain, v3);
        abstractDirtRoadItem.place(terrain, v4);
        abstractDirtRoadItem.place(terrain, v5);

//        Map<RoadNode, Set<RouteRoad>> res = terrain.getRoadGraph();
//
//        RoadNode roadNode = new RoadNode(center);
//        RoadNode roadNode2 = new RoadNode(v3);
//
//
//        RoadGraph roadGraph = new RoadGraph(res);
//        RouteFinder routeFinder = new RouteFinder(roadGraph);
//
//        routeFinder.findRoute(roadNode, roadNode2);
//        Set<RoadNode> expected = new HashSet<>();
//        expected.add(roadNode);
//        expected.add(roadNode2);
//
//        assertEquals(routeFinder.getRouteNodes(), expected);
//        assertEquals(2, routeFinder.getRouteCost());
    }

    @org.junit.jupiter.api.Test
    void testFindRoute2() {
        Terrain terrain = Terrain.getInstance();
        AbstractDirtRoadItem abstractDirtRoadItem = new AbstractDirtRoadItem();

        List<Vector2f> roadList = new ArrayList<>();
//        roadList.add(new Vector2f(53, 50));
//        roadList.add(new Vector2f(52, 50));
//        roadList.add(new Vector2f(51, 50));
//        roadList.add(new Vector2f(50, 50));
//        roadList.add(new Vector2f(49, 50));
//        roadList.add(new Vector2f(48, 50));
//        roadList.add(new Vector2f(47, 50));
//        roadList.add(new Vector2f(46, 50));
//        roadList.add(new Vector2f(45, 50));
//
//        roadList.add(new Vector2f(53, 49));
//        roadList.add(new Vector2f(53, 48));
//        roadList.add(new Vector2f(53, 47));
//        roadList.add(new Vector2f(53, 46));
//        roadList.add(new Vector2f(53, 45));
//        roadList.add(new Vector2f(53, 44));
//
//        roadList.add(new Vector2f(52, 44));
//        roadList.add(new Vector2f(51, 44));
//        roadList.add(new Vector2f(50, 44));
//        roadList.add(new Vector2f(49, 44));
//
//        roadList.add(new Vector2f(49, 45));
//        roadList.add(new Vector2f(49, 46));
//        roadList.add(new Vector2f(49, 47));
//
//        roadList.add(new Vector2f(48, 47));
//        roadList.add(new Vector2f(47, 47));
//
//        roadList.add(new Vector2f(47, 46));
//
//        roadList.add(new Vector2f(46, 46));
//        roadList.add(new Vector2f(45, 46));
//
//        roadList.add(new Vector2f(45, 47));
//        roadList.add(new Vector2f(45, 48));
//        roadList.add(new Vector2f(45, 49));
//
//        roadList.add(new Vector2f(51, 49));
//        roadList.add(new Vector2f(51, 48));
//        roadList.add(new Vector2f(51, 47));
//        roadList.add(new Vector2f(51, 46));
//        roadList.add(new Vector2f(51, 45));
//
//        roadList.add(new Vector2f(51, 51));
//        roadList.add(new Vector2f(51, 52));
//        roadList.add(new Vector2f(51, 53));
//        roadList.add(new Vector2f(51, 54));
//        roadList.add(new Vector2f(51, 55));
//
//        roadList.add(new Vector2f(52, 48));
//
//        roadList.add(new Vector2f(48, 51));
////        roadList.add(new Vector2f(48, 52));
//        roadList.add(new Vector2f(48, 53));
//        roadList.add(new Vector2f(48, 54));
//        //tmp
//        roadList.add(new Vector2f(47, 53));
//        roadList.add(new Vector2f(47, 52));
//        roadList.add(new Vector2f(47, 51));
//// fin tmp
//        roadList.add(new Vector2f(49, 54));
//        roadList.add(new Vector2f(50, 54));
//        roadList.add(new Vector2f(52, 54));
        roadList.add(new Vector2f(60, 60));
        roadList.add(new Vector2f(60, 61));
        roadList.add(new Vector2f(60, 62));

        roadList.forEach(pos -> terrain.placeItem(abstractDirtRoadItem, pos));
        terrain.updateRoadGraph();

//        System.out.println(roadGraph);

        RouteFinder routeFinder = new RouteFinder(terrain.getRoadGraph());

        routeFinder.findRoute(new Vector2f(60, 60), new Vector2f(60, 62), 0);
        System.out.println(routeFinder.getRoute());
    }

}
