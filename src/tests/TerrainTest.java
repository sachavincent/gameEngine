package tests;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFW.glfwSwapInterval;

import abstractItem.AbstractDirtRoadItem;
import abstractItem.AbstractInsula;
import entities.Camera.Direction;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import pathfinding.NormalRoad;
import pathfinding.RoadGraph;
import pathfinding.RoadNode;
import pathfinding.RouteFinder;
import pathfinding.RouteFinder.Route;
import pathfinding.RouteRoad;
import renderEngine.DisplayManager;
import terrains.Terrain;
import terrains.TerrainPosition;

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
        Terrain.getInstance().resetItems();
        Terrain.getInstance().updateRequirements();
        Terrain.getInstance().updateRoadGraph();
    }

    @Test
    void testGetRoadConnectionsToRoadItem() {
        Terrain terrain = Terrain.getInstance();

        AbstractDirtRoadItem abstractDirtRoadItem = new AbstractDirtRoadItem();
        abstractDirtRoadItem.place(new TerrainPosition(50, 50));


        Direction[] directions = terrain.getConnectionsToRoadItem(new TerrainPosition(50, 50), true);

        assertArrayEquals(new Direction[0], directions);
    }

    @Test
    void testGetRoadConnectionsToRoadItem2() {
        Terrain terrain = Terrain.getInstance();

        AbstractDirtRoadItem abstractDirtRoadItem = new AbstractDirtRoadItem();
        AbstractDirtRoadItem abstractDirtRoadItem2 = new AbstractDirtRoadItem();
        TerrainPosition center = new TerrainPosition(50, 50);
        abstractDirtRoadItem.place(center);
        abstractDirtRoadItem2.place(center.add(Direction.toRelativeDistance(Direction.EAST)));


        Direction[] directions = terrain.getConnectionsToRoadItem(center, true);

        assertArrayEquals(new Direction[]{Direction.EAST}, directions);
    }

    @Test
    void testGetRoadConnectionsToRoadItem3() {
        Terrain terrain = Terrain.getInstance();

        AbstractDirtRoadItem abstractDirtRoadItem = new AbstractDirtRoadItem();
        AbstractDirtRoadItem abstractDirtRoadItem2 = new AbstractDirtRoadItem();
        TerrainPosition center = new TerrainPosition(50, 50);
        abstractDirtRoadItem.place(center);
        abstractDirtRoadItem2.place(center.add(Direction.toRelativeDistance(Direction.WEST)));


        Direction[] directions = terrain.getConnectionsToRoadItem(center, true);

        assertArrayEquals(new Direction[]{Direction.WEST}, directions);
    }

    @Test
    void testGetRoadConnectionsToRoadItem4() {
        Terrain terrain = Terrain.getInstance();

        AbstractDirtRoadItem abstractDirtRoadItem = new AbstractDirtRoadItem();
        AbstractDirtRoadItem abstractDirtRoadItem2 = new AbstractDirtRoadItem();
        TerrainPosition center = new TerrainPosition(50, 50);
        abstractDirtRoadItem.place(center);
        abstractDirtRoadItem2.place(center.add(Direction.toRelativeDistance(Direction.SOUTH)));


        Direction[] directions = terrain.getConnectionsToRoadItem(center, true);

        assertArrayEquals(new Direction[]{Direction.SOUTH}, directions);
    }

    @Test
    void testGetRoadConnectionsToRoadItem5() {
        Terrain terrain = Terrain.getInstance();

        AbstractDirtRoadItem abstractDirtRoadItem = new AbstractDirtRoadItem();
        AbstractDirtRoadItem abstractDirtRoadItem2 = new AbstractDirtRoadItem();
        TerrainPosition center = new TerrainPosition(50, 50);
        abstractDirtRoadItem.place(center);
        abstractDirtRoadItem2.place(center.add(Direction.toRelativeDistance(Direction.NORTH)));


        Direction[] directions = terrain.getConnectionsToRoadItem(center, true);

        assertArrayEquals(new Direction[]{Direction.NORTH}, directions);
    }


    @Test
    void testGetRoadConnectionsToRoadItem6() {
        Terrain terrain = Terrain.getInstance();

        AbstractDirtRoadItem abstractDirtRoadItem = new AbstractDirtRoadItem();
        AbstractDirtRoadItem abstractDirtRoadItem2 = new AbstractDirtRoadItem();
        AbstractDirtRoadItem abstractDirtRoadItem3 = new AbstractDirtRoadItem();
        TerrainPosition center = new TerrainPosition(50, 50);
        abstractDirtRoadItem.place(center);
        abstractDirtRoadItem2.place(center.add(Direction.toRelativeDistance(Direction.NORTH)));
        abstractDirtRoadItem3.place(center.add(Direction.toRelativeDistance(Direction.SOUTH)));


        Direction[] directions = terrain.getConnectionsToRoadItem(center, true);

        assertArrayEquals(new Direction[]{Direction.NORTH, Direction.SOUTH}, directions);
    }

    @Test
    void testGetRoadConnectionsToRoadItem7() {
        Terrain terrain = Terrain.getInstance();

        AbstractDirtRoadItem abstractDirtRoadItem = new AbstractDirtRoadItem();
        AbstractDirtRoadItem abstractDirtRoadItem2 = new AbstractDirtRoadItem();
        AbstractDirtRoadItem abstractDirtRoadItem3 = new AbstractDirtRoadItem();
        AbstractInsula abstractInsula = new AbstractInsula();
        TerrainPosition center = new TerrainPosition(50, 50);
        abstractDirtRoadItem.place(center);
        abstractDirtRoadItem2.place(center.add(Direction.toRelativeDistance(Direction.NORTH)));
        abstractDirtRoadItem3.place(center.add(Direction.toRelativeDistance(Direction.SOUTH)));
        abstractInsula.place(center.add(Direction.toRelativeDistance(Direction.EAST)));


        Direction[] directions = terrain.getConnectionsToRoadItem(center, true);

        assertArrayEquals(new Direction[]{Direction.NORTH, Direction.SOUTH}, directions);
    }

    @Test
    void testGetRoadConnectionsToRoadItem8() {
        Terrain terrain = Terrain.getInstance();

        AbstractDirtRoadItem abstractDirtRoadItem = new AbstractDirtRoadItem();
        AbstractDirtRoadItem abstractDirtRoadItem2 = new AbstractDirtRoadItem();
        AbstractDirtRoadItem abstractDirtRoadItem3 = new AbstractDirtRoadItem();
        AbstractInsula abstractInsula = new AbstractInsula();
        TerrainPosition center = new TerrainPosition(50, 50);
        abstractDirtRoadItem.place(center);
        abstractDirtRoadItem2.place(center.add(Direction.toRelativeDistance(Direction.EAST)));
        abstractDirtRoadItem3.place(center.add(Direction.toRelativeDistance(Direction.WEST)));
        abstractInsula.place(center.add(Direction.toRelativeDistance(Direction.EAST)));


        Direction[] directions = terrain.getConnectionsToRoadItem(center, true);

        assertArrayEquals(new Direction[]{Direction.WEST, Direction.EAST}, directions);
    }

    @Test
    void testGetRoadConnectionsToRoadItem9() {
        Terrain terrain = Terrain.getInstance();

        AbstractDirtRoadItem abstractDirtRoadItem = new AbstractDirtRoadItem();
        AbstractDirtRoadItem abstractDirtRoadItem2 = new AbstractDirtRoadItem();
        AbstractDirtRoadItem abstractDirtRoadItem3 = new AbstractDirtRoadItem();
        AbstractDirtRoadItem abstractDirtRoadItem4 = new AbstractDirtRoadItem();
        AbstractDirtRoadItem abstractDirtRoadItem5 = new AbstractDirtRoadItem();
        TerrainPosition center = new TerrainPosition(50, 50);
        abstractDirtRoadItem.place(center);
        abstractDirtRoadItem2.place(center.add(Direction.toRelativeDistance(Direction.EAST)));
        abstractDirtRoadItem3.place(center.add(Direction.toRelativeDistance(Direction.WEST)));
        abstractDirtRoadItem4.place(center.add(Direction.toRelativeDistance(Direction.NORTH)));
        abstractDirtRoadItem5.place(center.add(Direction.toRelativeDistance(Direction.SOUTH)));


        Direction[] directions = terrain.getConnectionsToRoadItem(center, true);
        assertArrayEquals(new Direction[]{Direction.WEST, Direction.NORTH, Direction.EAST, Direction.SOUTH},
                directions);
    }

    //
//    @Test
//    void testGetRoadGraph() {
//        Terrain terrain = Terrain.getInstance();
//
//        AbstractDirtRoadItem abstractDirtRoadItem = new AbstractDirtRoadItem();
//        AbstractDirtRoadItem abstractDirtRoadItem2 = new AbstractDirtRoadItem();
//        AbstractDirtRoadItem abstractDirtRoadItem3 = new AbstractDirtRoadItem();
//        AbstractDirtRoadItem abstractDirtRoadItem4 = new AbstractDirtRoadItem();
//        AbstractDirtRoadItem abstractDirtRoadItem5 = new AbstractDirtRoadItem();
//        TerrainPosition center = new TerrainPosition(50, 50);
//        abstractDirtRoadItem.place(center);
//        abstractDirtRoadItem2.place(center.add(Direction.toRelativeDistance(Direction.EAST)));
//        abstractDirtRoadItem3.place(center.add(Direction.toRelativeDistance(Direction.WEST)));
//        abstractDirtRoadItem4.place(center.add(Direction.toRelativeDistance(Direction.NORTH)));
//        abstractDirtRoadItem5.place(center.add(Direction.toRelativeDistance(Direction.SOUTH)));
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
//    @Test
//    void testGetRoadGraph2() {
//        Terrain terrain = Terrain.getInstance();
//
//        AbstractDirtRoadItem abstractDirtRoadItem = new AbstractDirtRoadItem();
//        TerrainPosition v0 = new TerrainPosition(49, 50);
//        TerrainPosition v00 = new TerrainPosition(50, 51);
//        TerrainPosition center = new TerrainPosition(50, 50);
//        TerrainPosition v1 = new TerrainPosition(51, 50);
//        TerrainPosition v2 = new TerrainPosition(52, 50);
//        TerrainPosition v3 = new TerrainPosition(53, 50);
//        TerrainPosition v4 = new TerrainPosition(53, 51);
//        TerrainPosition v5 = new TerrainPosition(53, 49);
//
//        abstractDirtRoadItem.place(center);
//        abstractDirtRoadItem.place(v0);
//        abstractDirtRoadItem.place(v00);
//        abstractDirtRoadItem.place(v1);
//        abstractDirtRoadItem.place(v2);
//        abstractDirtRoadItem.place(v3);
//        abstractDirtRoadItem.place(v4);
//        abstractDirtRoadItem.place(v5);
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
    @Test
    void testInvertRoute1() {
        TerrainPosition v1 = new TerrainPosition(50, 50);

        RouteFinder.Route<RouteRoad> route = new Route<>();
        RouteRoad routeRoad = new RouteRoad(new RoadNode(v1));
        route.add(routeRoad);

        RouteFinder.Route<RouteRoad> foundRoute = route.invertRoute();

        RouteFinder.Route<RouteRoad> expectedRoute = new Route<>();
        expectedRoute.add(routeRoad);

        assertTrue(foundRoute.compareRoutes(expectedRoute));
        assertTrue(foundRoute.compareCost(expectedRoute));
    }

    @Test
    void testInvertRoute2() {
        TerrainPosition v1 = new TerrainPosition(50, 50);
        TerrainPosition v2 = new TerrainPosition(51, 50);

        RouteFinder.Route<RouteRoad> route = new Route<>();
        RouteRoad routeRoad = new RouteRoad(new RoadNode(v1));
        routeRoad.setEnd(new RoadNode(v2));
        route.add(routeRoad);

        RouteFinder.Route<RouteRoad> foundRoute = route.invertRoute();

        RouteFinder.Route<RouteRoad> expectedRoute = new Route<>();
        RouteRoad expectedRouteRoad = new RouteRoad(new RoadNode(v2));
        expectedRouteRoad.setEnd(new RoadNode(v1));
        expectedRoute.add(expectedRouteRoad);

        assertTrue(foundRoute.compareRoutes(expectedRoute));
        assertTrue(foundRoute.compareCost(expectedRoute));
    }

    @Test
    void testInvertRoute3() {
        TerrainPosition v1 = new TerrainPosition(50, 50);
        TerrainPosition v2 = new TerrainPosition(51, 50);
        TerrainPosition v3 = new TerrainPosition(52, 50);
        TerrainPosition v4 = new TerrainPosition(53, 50);

        RouteFinder.Route<RouteRoad> route = new Route<>();
        // Expected individual routes
        RouteRoad routeRoad = new RouteRoad(new RoadNode(v1));
        routeRoad.setEnd(new RoadNode(v4));
        routeRoad.addRoad(new NormalRoad(v2));
        routeRoad.addRoad(new NormalRoad(v3));
        route.add(routeRoad);

        RouteFinder.Route<RouteRoad> foundRoute = route.invertRoute();

        RouteFinder.Route<RouteRoad> expectedRoute = new Route<>();
        // Expected individual routes
        RouteRoad expectedRouteRoad = new RouteRoad(new RoadNode(v4));
        expectedRouteRoad.setEnd(new RoadNode(v1));
        expectedRouteRoad.addRoad(new NormalRoad(v3));
        expectedRouteRoad.addRoad(new NormalRoad(v2));
        expectedRoute.add(expectedRouteRoad);

        assertTrue(foundRoute.compareRoutes(expectedRoute));
        assertTrue(foundRoute.compareCost(expectedRoute));
    }

    @Test
    void testInvertRoute4() {
        TerrainPosition v1 = new TerrainPosition(50, 50);
        TerrainPosition v2 = new TerrainPosition(51, 50);
        TerrainPosition v3 = new TerrainPosition(52, 50);
        TerrainPosition v4 = new TerrainPosition(53, 50);
        TerrainPosition v5 = new TerrainPosition(54, 50);
        TerrainPosition v6 = new TerrainPosition(55, 50);
        TerrainPosition v7 = new TerrainPosition(56, 50);

        RouteFinder.Route<RouteRoad> route = new Route<>();

        RouteRoad routeRoad = new RouteRoad(new RoadNode(v1));
        routeRoad.setEnd(new RoadNode(v4));
        routeRoad.addRoad(new NormalRoad(v2));
        routeRoad.addRoad(new NormalRoad(v3));
        RouteRoad routeRoad2 = new RouteRoad(new RoadNode(v4));
        routeRoad2.setEnd(new RoadNode(v7));
        routeRoad2.addRoad(new NormalRoad(v5));
        routeRoad2.addRoad(new NormalRoad(v6));
        route.add(routeRoad);
        route.add(routeRoad2);

        RouteFinder.Route<RouteRoad> foundRoute = route.invertRoute();

        RouteFinder.Route<RouteRoad> expectedRoute = new Route<>();

        RouteRoad expectedRouteRoad = new RouteRoad(new RoadNode(v4));
        expectedRouteRoad.setEnd(new RoadNode(v1));
        expectedRouteRoad.addRoad(new NormalRoad(v3));
        expectedRouteRoad.addRoad(new NormalRoad(v2));
        RouteRoad expectedRouteRoad2 = new RouteRoad(new RoadNode(v7));
        expectedRouteRoad2.setEnd(new RoadNode(v4));
        expectedRouteRoad2.addRoad(new NormalRoad(v6));
        expectedRouteRoad2.addRoad(new NormalRoad(v5));
        expectedRoute.add(expectedRouteRoad2);
        expectedRoute.add(expectedRouteRoad);

        assertTrue(foundRoute.compareRoutes(expectedRoute));
        assertTrue(foundRoute.compareCost(expectedRoute));
    }

    @Test
    void testInvertRoute5() {
        TerrainPosition v1 = new TerrainPosition(50, 50);
        TerrainPosition v2 = new TerrainPosition(51, 50);
        TerrainPosition v3 = new TerrainPosition(52, 50);
        TerrainPosition v4 = new TerrainPosition(53, 50);
        TerrainPosition v5 = new TerrainPosition(54, 50);
        TerrainPosition v6 = new TerrainPosition(55, 50);
        TerrainPosition v7 = new TerrainPosition(56, 50);
        TerrainPosition v8 = new TerrainPosition(57, 50);
        TerrainPosition v9 = new TerrainPosition(58, 50);
        TerrainPosition v10 = new TerrainPosition(59, 50);

        RouteFinder.Route<RouteRoad> route = new Route<>();

        RouteRoad routeRoad = new RouteRoad(new RoadNode(v1));
        routeRoad.setEnd(new RoadNode(v4));
        routeRoad.addRoad(new NormalRoad(v2));
        routeRoad.addRoad(new NormalRoad(v3));
        RouteRoad routeRoad2 = new RouteRoad(new RoadNode(v4));
        routeRoad2.setEnd(new RoadNode(v7));
        routeRoad2.addRoad(new NormalRoad(v5));
        routeRoad2.addRoad(new NormalRoad(v6));
        RouteRoad routeRoad3 = new RouteRoad(new RoadNode(v7));
        routeRoad3.setEnd(new RoadNode(v10));
        routeRoad3.addRoad(new NormalRoad(v8));
        routeRoad3.addRoad(new NormalRoad(v9));
        route.add(routeRoad);
        route.add(routeRoad2);
        route.add(routeRoad3);

        RouteFinder.Route<RouteRoad> foundRoute = route.invertRoute();

        RouteFinder.Route<RouteRoad> expectedRoute = new Route<>();

        RouteRoad expectedRouteRoad = new RouteRoad(new RoadNode(v4));
        expectedRouteRoad.setEnd(new RoadNode(v1));
        expectedRouteRoad.addRoad(new NormalRoad(v3));
        expectedRouteRoad.addRoad(new NormalRoad(v2));
        RouteRoad expectedRouteRoad2 = new RouteRoad(new RoadNode(v7));
        expectedRouteRoad2.setEnd(new RoadNode(v4));
        expectedRouteRoad2.addRoad(new NormalRoad(v6));
        expectedRouteRoad2.addRoad(new NormalRoad(v5));
        RouteRoad expectedRouteRoad3 = new RouteRoad(new RoadNode(v10));
        expectedRouteRoad3.setEnd(new RoadNode(v7));
        expectedRouteRoad3.addRoad(new NormalRoad(v9));
        expectedRouteRoad3.addRoad(new NormalRoad(v8));
        expectedRoute.add(expectedRouteRoad3);
        expectedRoute.add(expectedRouteRoad2);
        expectedRoute.add(expectedRouteRoad);

        assertTrue(foundRoute.compareRoutes(expectedRoute));
        assertTrue(foundRoute.compareCost(expectedRoute));
    }

    /**
     * 1. Node 2 Node
     */
    @Test
    void testPathfinding1() {
        Terrain terrain = Terrain.getInstance();

        AbstractDirtRoadItem abstractDirtRoadItem = new AbstractDirtRoadItem();
        TerrainPosition v1 = new TerrainPosition(50, 50);
        TerrainPosition v2 = new TerrainPosition(51, 50);
        TerrainPosition v3 = new TerrainPosition(52, 50);
        TerrainPosition v4 = new TerrainPosition(53, 50);

        // v1 -> v2 -> v3 -> v4
        abstractDirtRoadItem.place(new TerrainPosition(50, 49));
        abstractDirtRoadItem.place(new TerrainPosition(50, 51));
        abstractDirtRoadItem.place(new TerrainPosition(53, 49));
        abstractDirtRoadItem.place(new TerrainPosition(53, 51));
        abstractDirtRoadItem.place(v1);
        abstractDirtRoadItem.place(v2);
        abstractDirtRoadItem.place(v3);
        abstractDirtRoadItem.place(v4);

        RoadGraph roadGraph = terrain.getRoadGraph();
        RouteFinder routeFinder = new RouteFinder(roadGraph);

        // Found route
        RouteFinder.Route<RouteRoad> foundRoute = routeFinder.findRoute(v1, v4, 0);

        // Expected global route
        RouteFinder.Route<RouteRoad> expectedRoute = new Route<>();
        // Expected individual routes
        RouteRoad expectedRouteRoad = new RouteRoad(new RoadNode(v1));
        expectedRouteRoad.setEnd(new RoadNode(v4));
        expectedRouteRoad.addRoad(new NormalRoad(v2));
        expectedRouteRoad.addRoad(new NormalRoad(v3));
        expectedRoute.add(expectedRouteRoad);

        assertTrue(foundRoute.compareRoutes(expectedRoute));
        assertTrue(foundRoute.compareCost(expectedRoute));
    }

    /**
     * 2. Node 2 Node through Node
     */
    @Test
    void testPathfinding2() {
        Terrain terrain = Terrain.getInstance();

        AbstractDirtRoadItem abstractDirtRoadItem = new AbstractDirtRoadItem();
        TerrainPosition v1 = new TerrainPosition(50, 50);
        TerrainPosition v2 = new TerrainPosition(51, 50);
        TerrainPosition v3 = new TerrainPosition(52, 50);
        TerrainPosition v4 = new TerrainPosition(53, 50);
        TerrainPosition v5 = new TerrainPosition(54, 50);

        // v1 -> v2 -> v3 -> v4 -> v5
        abstractDirtRoadItem.place(new TerrainPosition(49, 50));
        abstractDirtRoadItem.place(new TerrainPosition(50, 49));
        abstractDirtRoadItem.place(new TerrainPosition(52, 49));
        abstractDirtRoadItem.place(new TerrainPosition(54, 49));
        abstractDirtRoadItem.place(new TerrainPosition(55, 50));
        abstractDirtRoadItem.place(v1);
        abstractDirtRoadItem.place(v2);
        abstractDirtRoadItem.place(v3);
        abstractDirtRoadItem.place(v4);
        abstractDirtRoadItem.place(v5);

        RoadGraph roadGraph = terrain.getRoadGraph();
        RouteFinder routeFinder = new RouteFinder(roadGraph);

        // Found route
        RouteFinder.Route<RouteRoad> foundRoute = routeFinder.findRoute(v1, v5, 0);

        // Expected global route
        RouteFinder.Route<RouteRoad> expectedRoute = new Route<>();
        // Expected individual routes
        RouteRoad expectedRouteRoad = new RouteRoad(new RoadNode(v1));
        expectedRouteRoad.setEnd(new RoadNode(v3));
        expectedRouteRoad.addRoad(new NormalRoad(v2));
        RouteRoad expectedRouteRoad2 = new RouteRoad(new RoadNode(v3));
        expectedRouteRoad2.setEnd(new RoadNode(v5));
        expectedRouteRoad2.addRoad(new NormalRoad(v4));

        expectedRoute.add(expectedRouteRoad);
        expectedRoute.add(expectedRouteRoad2);

        assertTrue(foundRoute.compareRoutes(expectedRoute));
        assertTrue(foundRoute.compareCost(expectedRoute));
    }

    /**
     * 3. Node 2 Node Choice
     */
    @Test
    void testPathfinding3() {
        Terrain terrain = Terrain.getInstance();

        AbstractDirtRoadItem abstractDirtRoadItem = new AbstractDirtRoadItem();
        TerrainPosition v1 = new TerrainPosition(50, 50);
        TerrainPosition v2 = new TerrainPosition(50, 49);
        TerrainPosition v3 = new TerrainPosition(50, 48);
        TerrainPosition v4 = new TerrainPosition(51, 48);
        TerrainPosition v5 = new TerrainPosition(52, 48);

        // v1 -> v2 -> v3 -> v4 -> v5
        int[] roadPositions = new int[]{
                49, 50,
                51, 50,
                52, 50,
                53, 50,
                54, 50,
                55, 50,
                53, 48,
                54, 48,
                55, 48,
                55, 49,
                52, 47,
                v1.getX(), v1.getZ(),
                v2.getX(), v2.getZ(),
                v3.getX(), v3.getZ(),
                v4.getX(), v4.getZ(),
                v5.getX(), v5.getZ(),
        };

        TerrainPosition[] roads = TerrainPosition.toVectorArray(roadPositions);

        abstractDirtRoadItem.place(roads);

        RoadGraph roadGraph = terrain.getRoadGraph();

        RouteFinder routeFinder = new RouteFinder(roadGraph);

        // Found route
        RouteFinder.Route<RouteRoad> foundRoute = routeFinder.findRoute(v1, v5, 0);

        // Expected global route
        RouteFinder.Route<RouteRoad> expectedRoute = new Route<>();
        // Expected individual routes
        RouteRoad expectedRouteRoad = new RouteRoad(new RoadNode(v1));
        expectedRouteRoad.setEnd(new RoadNode(v5));
        expectedRouteRoad.addRoad(new NormalRoad(v2));
        expectedRouteRoad.addRoad(new NormalRoad(v3));
        expectedRouteRoad.addRoad(new NormalRoad(v4));
        expectedRoute.add(expectedRouteRoad);

        assertTrue(foundRoute.compareRoutes(expectedRoute));
        assertTrue(foundRoute.compareCost(expectedRoute));
    }

    /**
     * 4. Node 2 Node through 2 Nodes
     */
    @Test
    void testPathfinding4() {
        Terrain terrain = Terrain.getInstance();

        AbstractDirtRoadItem abstractDirtRoadItem = new AbstractDirtRoadItem();
        TerrainPosition v1 = new TerrainPosition(50, 50);
        TerrainPosition v2 = new TerrainPosition(51, 50);
        TerrainPosition v3 = new TerrainPosition(52, 50);
        TerrainPosition v4 = new TerrainPosition(53, 50);
        TerrainPosition v5 = new TerrainPosition(54, 50);
        TerrainPosition v6 = new TerrainPosition(55, 50);
        TerrainPosition v7 = new TerrainPosition(55, 49);
        TerrainPosition v8 = new TerrainPosition(56, 49);
        TerrainPosition v9 = new TerrainPosition(57, 49);

        // v1 -> v2 -> v3 -> v4 -> v5 -> v6 -> v7 -> v8 -> v9
        int[] roadPositions = new int[]{
                50, 49,
                49, 50,
                53, 49,
                55, 48,
                58, 49,
                57, 50,
                57, 48,
                v1.getX(), v1.getZ(),
                v2.getX(), v2.getZ(),
                v3.getX(), v3.getZ(),
                v4.getX(), v4.getZ(),
                v5.getX(), v5.getZ(),
                v6.getX(), v6.getZ(),
                v7.getX(), v7.getZ(),
                v8.getX(), v8.getZ(),
                v9.getX(), v9.getZ(),
        };

        TerrainPosition[] roads = TerrainPosition.toVectorArray(roadPositions);

        abstractDirtRoadItem.place(roads);

        RoadGraph roadGraph = terrain.getRoadGraph();

        RouteFinder routeFinder = new RouteFinder(roadGraph);

        // Found route
        RouteFinder.Route<RouteRoad> foundRoute = routeFinder.findRoute(v1, v9, 0);

        // Expected global route
        RouteFinder.Route<RouteRoad> expectedRoute = new Route<>();
        // Expected individual routes
        RouteRoad expectedRouteRoad = new RouteRoad(new RoadNode(v1));
        expectedRouteRoad.setEnd(new RoadNode(v4));
        expectedRouteRoad.addRoad(new NormalRoad(v2));
        expectedRouteRoad.addRoad(new NormalRoad(v3));
        RouteRoad expectedRouteRoad2 = new RouteRoad(new RoadNode(v4));
        expectedRouteRoad2.setEnd(new RoadNode(v7));
        expectedRouteRoad2.addRoad(new NormalRoad(v5));
        expectedRouteRoad2.addRoad(new NormalRoad(v6));
        RouteRoad expectedRouteRoad3 = new RouteRoad(new RoadNode(v7));
        expectedRouteRoad3.setEnd(new RoadNode(v9));
        expectedRouteRoad3.addRoad(new NormalRoad(v8));
        expectedRoute.add(expectedRouteRoad);
        expectedRoute.add(expectedRouteRoad2);
        expectedRoute.add(expectedRouteRoad3);


        assertTrue(foundRoute.compareRoutes(expectedRoute));
        assertTrue(foundRoute.compareCost(expectedRoute));
    }

    /**
     * 5. Node 2 Node Multiple nodes and choices (v1)
     */
    @Test
    void testPathfinding5() {
        Terrain terrain = Terrain.getInstance();

        AbstractDirtRoadItem abstractDirtRoadItem = new AbstractDirtRoadItem();
        TerrainPosition v1 = new TerrainPosition(50, 50);
        TerrainPosition v2 = new TerrainPosition(51, 50);
        TerrainPosition v3 = new TerrainPosition(52, 50);
        TerrainPosition v4 = new TerrainPosition(53, 50);
        TerrainPosition v5 = new TerrainPosition(50, 49);
        TerrainPosition v6 = new TerrainPosition(50, 48);
        TerrainPosition v7 = new TerrainPosition(51, 48);
        TerrainPosition v8 = new TerrainPosition(52, 48);
        TerrainPosition v9 = new TerrainPosition(53, 48);
        TerrainPosition v10 = new TerrainPosition(53, 49);
        TerrainPosition v11 = new TerrainPosition(54, 48);
        TerrainPosition v12 = new TerrainPosition(55, 48);

        /*
        v1 -> v2 -> v3 -> v4
        ↓                 ↓
        v5               v10
        ↓                 ↓
        v6 -> v7 -> v8 -> v9 -> v11 -> v12
         */
        int[] roadPositions = new int[]{
                49, 50,
                54, 50,
                49, 48,
                55, 49,
                50, 47,
                50, 46,
                51, 46,
                52, 46,
                53, 46,
                54, 46,
                55, 46,
                55, 47,
                v1.getX(), v1.getZ(),
                v2.getX(), v2.getZ(),
                v3.getX(), v3.getZ(),
                v4.getX(), v4.getZ(),
                v5.getX(), v5.getZ(),
                v6.getX(), v6.getZ(),
                v7.getX(), v7.getZ(),
                v8.getX(), v8.getZ(),
                v9.getX(), v9.getZ(),
                v10.getX(), v10.getZ(),
                v11.getX(), v11.getZ(),
                v12.getX(), v12.getZ()
        };

        TerrainPosition[] roads = TerrainPosition.toVectorArray(roadPositions);

        abstractDirtRoadItem.place(roads);

        RoadGraph roadGraph = terrain.getRoadGraph();
        RouteFinder routeFinder = new RouteFinder(roadGraph);
        // Found route
        RouteFinder.Route<RouteRoad> foundRoute = routeFinder.findRoute(v1, v12, 0);

        // Expected global routes
        RouteFinder.Route<RouteRoad> expectedRoute = new Route<>();
        RouteFinder.Route<RouteRoad> expectedRoute2 = new Route<>();

        // Expected individual routes
        RouteRoad expectedRouteRoad = new RouteRoad(new RoadNode(v1));
        expectedRouteRoad.setEnd(new RoadNode(v4));
        expectedRouteRoad.addRoad(new NormalRoad(v2));
        expectedRouteRoad.addRoad(new NormalRoad(v3));
        RouteRoad expectedRouteRoad2 = new RouteRoad(new RoadNode(v4));
        expectedRouteRoad2.setEnd(new RoadNode(v9));
        expectedRouteRoad2.addRoad(new NormalRoad(v10));
        RouteRoad expectedRouteRoad3 = new RouteRoad(new RoadNode(v9));
        expectedRouteRoad3.setEnd(new RoadNode(v12));
        expectedRouteRoad3.addRoad(new NormalRoad(v11));
        expectedRoute.add(expectedRouteRoad);
        expectedRoute.add(expectedRouteRoad2);
        expectedRoute.add(expectedRouteRoad3);

        expectedRouteRoad = new RouteRoad(new RoadNode(v1));
        expectedRouteRoad.setEnd(new RoadNode(v6));
        expectedRouteRoad.addRoad(new NormalRoad(v5));
        expectedRouteRoad2 = new RouteRoad(new RoadNode(v6));
        expectedRouteRoad2.setEnd(new RoadNode(v9));
        expectedRouteRoad2.addRoad(new NormalRoad(v7));
        expectedRouteRoad2.addRoad(new NormalRoad(v8));
        expectedRoute2.add(expectedRouteRoad);
        expectedRoute2.add(expectedRouteRoad2);
        expectedRoute2.add(expectedRouteRoad3);

        assertTrue(foundRoute.compareRoutes(expectedRoute) || foundRoute.compareRoutes(expectedRoute2));
        assertTrue(foundRoute.compareCost(expectedRoute));
    }

    /**
     * 6. Node 2 Node Multiple nodes and choices (v2)
     */
    @Test
    void testPathfinding6() {
        Terrain terrain = Terrain.getInstance();

        AbstractDirtRoadItem abstractDirtRoadItem = new AbstractDirtRoadItem();
        TerrainPosition v1 = new TerrainPosition(50, 50);
        TerrainPosition v2 = new TerrainPosition(51, 50);
        TerrainPosition v3 = new TerrainPosition(52, 50);
        TerrainPosition v4 = new TerrainPosition(53, 50);
        TerrainPosition v5 = new TerrainPosition(50, 49);
        TerrainPosition v6 = new TerrainPosition(50, 48);
        TerrainPosition v7 = new TerrainPosition(51, 48);
        TerrainPosition v8 = new TerrainPosition(52, 48);
        TerrainPosition v9 = new TerrainPosition(53, 48);
        TerrainPosition v10 = new TerrainPosition(53, 49);
        TerrainPosition v11 = new TerrainPosition(53, 47);
        TerrainPosition v12 = new TerrainPosition(53, 46);
        TerrainPosition v13 = new TerrainPosition(54, 46);

        /*
        v1 -> v2 -> v3 -> v4
        ↓                 ↓
        v5               v10
        ↓                 ↓
        v6 -> v7 -> v8 -> v9
                          ↓
                         v11
                          ↓
                         v12 -> v13
         */
        int[] roadPositions = new int[]{
                49, 50,
                54, 50,
                55, 50,
                56, 50,
                57, 50,
                53, 51,
                55, 51,
                49, 48,
                48, 48,
                50, 47,
                56, 49,
                56, 48,
                55, 48,
                55, 47,
                55, 46,
                54, 45,
                v1.getX(), v1.getZ(),
                v2.getX(), v2.getZ(),
                v3.getX(), v3.getZ(),
                v4.getX(), v4.getZ(),
                v5.getX(), v5.getZ(),
                v6.getX(), v6.getZ(),
                v7.getX(), v7.getZ(),
                v8.getX(), v8.getZ(),
                v9.getX(), v9.getZ(),
                v10.getX(), v10.getZ(),
                v11.getX(), v11.getZ(),
                v12.getX(), v12.getZ(),
                v13.getX(), v13.getZ()
        };

        TerrainPosition[] roads = TerrainPosition.toVectorArray(roadPositions);

        abstractDirtRoadItem.place(roads);

        RoadGraph roadGraph = terrain.getRoadGraph();
        RouteFinder routeFinder = new RouteFinder(roadGraph);
        // Found route
        RouteFinder.Route<RouteRoad> foundRoute = routeFinder.findRoute(v1, v13, 0);

        // Expected global routes
        RouteFinder.Route<RouteRoad> expectedRoute = new Route<>();
        RouteFinder.Route<RouteRoad> expectedRoute2 = new Route<>();

        // Expected individual routes
        RouteRoad expectedRouteRoad = new RouteRoad(new RoadNode(v1));
        expectedRouteRoad.setEnd(new RoadNode(v4));
        expectedRouteRoad.addRoad(new NormalRoad(v2));
        expectedRouteRoad.addRoad(new NormalRoad(v3));
        RouteRoad expectedRouteRoad2 = new RouteRoad(new RoadNode(v4));
        expectedRouteRoad2.setEnd(new RoadNode(v9));
        expectedRouteRoad2.addRoad(new NormalRoad(v10));
        RouteRoad expectedRouteRoad3 = new RouteRoad(new RoadNode(v9));
        expectedRouteRoad3.setEnd(new RoadNode(v13));
        expectedRouteRoad3.addRoad(new NormalRoad(v11));
        expectedRouteRoad3.addRoad(new NormalRoad(v12));
        expectedRoute.add(expectedRouteRoad);
        expectedRoute.add(expectedRouteRoad2);
        expectedRoute.add(expectedRouteRoad3);

        expectedRouteRoad = new RouteRoad(new RoadNode(v1));
        expectedRouteRoad.setEnd(new RoadNode(v6));
        expectedRouteRoad.addRoad(new NormalRoad(v5));
        expectedRouteRoad2 = new RouteRoad(new RoadNode(v6));
        expectedRouteRoad2.setEnd(new RoadNode(v9));
        expectedRouteRoad2.addRoad(new NormalRoad(v7));
        expectedRouteRoad2.addRoad(new NormalRoad(v8));
        expectedRoute2.add(expectedRouteRoad);
        expectedRoute2.add(expectedRouteRoad2);
        expectedRoute2.add(expectedRouteRoad3);

        assertTrue(foundRoute.compareRoutes(expectedRoute) || foundRoute.compareRoutes(expectedRoute2));
        assertTrue(foundRoute.compareCost(expectedRoute));
    }

    /**
     * 7. Road 2 Node through Node
     */
    @Test
    void testPathfinding7() {
        Terrain terrain = Terrain.getInstance();

        AbstractDirtRoadItem abstractDirtRoadItem = new AbstractDirtRoadItem();
        TerrainPosition v1 = new TerrainPosition(50, 50);
        TerrainPosition v2 = new TerrainPosition(51, 50);
        TerrainPosition v3 = new TerrainPosition(52, 50);
        TerrainPosition v4 = new TerrainPosition(53, 50);
        TerrainPosition v5 = new TerrainPosition(54, 50);

        /*
        v1 -> v2 -> v3 -> v4 -> v5
         */
        int[] roadPositions = new int[]{
                51, 49,
                54, 49,
                55, 50,
                v1.getX(), v1.getZ(),
                v2.getX(), v2.getZ(),
                v3.getX(), v3.getZ(),
                v4.getX(), v4.getZ(),
                v5.getX(), v5.getZ(),
        };

        TerrainPosition[] roads = TerrainPosition.toVectorArray(roadPositions);

        abstractDirtRoadItem.place(roads);

        RoadGraph roadGraph = terrain.getRoadGraph();
        RouteFinder routeFinder = new RouteFinder(roadGraph);
        // Found route
        RouteFinder.Route<RouteRoad> foundRoute = routeFinder.findRoute(v1, v5, 0);

        // Expected global routes
        RouteFinder.Route<RouteRoad> expectedRoute = new Route<>();

        // Expected individual routes
        RouteRoad expectedRouteRoad = new RouteRoad(new NormalRoad(v1));
        expectedRouteRoad.setEnd(new RoadNode(v2));
        RouteRoad expectedRouteRoad2 = new RouteRoad(new RoadNode(v2));
        expectedRouteRoad2.setEnd(new RoadNode(v5));
        expectedRouteRoad2.addRoad(new NormalRoad(v3));
        expectedRouteRoad2.addRoad(new NormalRoad(v4));
        expectedRoute.add(expectedRouteRoad);
        expectedRoute.add(expectedRouteRoad2);

        assertTrue(foundRoute.compareRoutes(expectedRoute));
        assertTrue(foundRoute.compareCost(expectedRoute));
    }

    /**
     * 8. Road 2 Node Choice
     */
    @Test
    void testPathfinding8() {
        Terrain terrain = Terrain.getInstance();

        AbstractDirtRoadItem abstractDirtRoadItem = new AbstractDirtRoadItem();
        TerrainPosition v1 = new TerrainPosition(50, 50);
        TerrainPosition v2 = new TerrainPosition(51, 50);
        TerrainPosition v3 = new TerrainPosition(52, 50);
        TerrainPosition v4 = new TerrainPosition(53, 50);
        TerrainPosition v5 = new TerrainPosition(53, 49);
        TerrainPosition v6 = new TerrainPosition(53, 48);

        /*
        v1 -> v2 -> v3 -> v4
                          ↓
                          v5
                          ↓
                          v6
         */
        int[] roadPositions = new int[]{
                54, 50,
                50, 49,
                50, 48,
                51, 48,
                54, 48,
                50, 47,
                53, 47,
                50, 46,
                51, 46,
                52, 46,
                53, 46,
                v1.getX(), v1.getZ(),
                v2.getX(), v2.getZ(),
                v3.getX(), v3.getZ(),
                v4.getX(), v4.getZ(),
                v5.getX(), v5.getZ(),
                v6.getX(), v6.getZ(),
        };

        TerrainPosition[] roads = TerrainPosition.toVectorArray(roadPositions);

        abstractDirtRoadItem.place(roads);

        RoadGraph roadGraph = terrain.getRoadGraph();
        RouteFinder routeFinder = new RouteFinder(roadGraph);

        // Found route
        RouteFinder.Route<RouteRoad> foundRoute = routeFinder.findRoute(v1, v6, 0);
        routeFinder.reset();

        RouteFinder.Route<RouteRoad> foundRoute2 = routeFinder.findRoute(v6, v1, 0);

        // Expected global routes
        RouteFinder.Route<RouteRoad> expectedRoute = new Route<>();

        // Expected individual routes
        RouteRoad expectedRouteRoad = new RouteRoad(new NormalRoad(v1));
        expectedRouteRoad.setEnd(new RoadNode(v4));
        expectedRouteRoad.addRoad(new NormalRoad(v2));
        expectedRouteRoad.addRoad(new NormalRoad(v3));
        RouteRoad expectedRouteRoad2 = new RouteRoad(new RoadNode(v4));
        expectedRouteRoad2.setEnd(new RoadNode(v6));
        expectedRouteRoad2.addRoad(new NormalRoad(v5));
        expectedRoute.add(expectedRouteRoad);
        expectedRoute.add(expectedRouteRoad2);

        Route<RouteRoad> expectedRoute2 = expectedRoute.invertRoute();

        System.out.println(expectedRoute);
        System.out.println(foundRoute);
        assertTrue(foundRoute.compareRoutes(expectedRoute));
        assertTrue(foundRoute.compareCost(expectedRoute));
        assertTrue(foundRoute2.compareRoutes(expectedRoute2));
        assertTrue(foundRoute2.compareCost(expectedRoute2));
    }

    /**
     * 9. Road 2 Road
     */
    @Test
    void testPathfinding9() {
        Terrain terrain = Terrain.getInstance();

        AbstractDirtRoadItem abstractDirtRoadItem = new AbstractDirtRoadItem();
        TerrainPosition v1 = new TerrainPosition(50, 50);
        TerrainPosition v2 = new TerrainPosition(51, 50);
        TerrainPosition v3 = new TerrainPosition(52, 50);
        TerrainPosition v4 = new TerrainPosition(53, 50);
        TerrainPosition v5 = new TerrainPosition(54, 50);

        /*
        v1 -> v2 -> v3 -> v4 -> v5
         */
        int[] roadPositions = new int[]{
                //    49,50,
                //    55,50,
                v1.getX(), v1.getZ(),
                v2.getX(), v2.getZ(),
                v3.getX(), v3.getZ(),
                v4.getX(), v4.getZ(),
                v5.getX(), v5.getZ(),
        };

        TerrainPosition[] roads = TerrainPosition.toVectorArray(roadPositions);

        abstractDirtRoadItem.place(roads);

        RoadGraph roadGraph = terrain.getRoadGraph();
        RouteFinder routeFinder = new RouteFinder(roadGraph);
        // Found route
        RouteFinder.Route<RouteRoad> foundRoute = routeFinder.findRoute(v1, v5, 0);

        // Expected global routes
        RouteFinder.Route<RouteRoad> expectedRoute = new Route<>();

        // Expected individual routes
        RouteRoad expectedRouteRoad = new RouteRoad(new NormalRoad(v1));
        expectedRouteRoad.setEnd(new NormalRoad(v5));
        expectedRouteRoad.addRoad(new NormalRoad(v2));
        expectedRouteRoad.addRoad(new NormalRoad(v3));
        expectedRouteRoad.addRoad(new NormalRoad(v4));
        expectedRoute.add(expectedRouteRoad);

        assertTrue(foundRoute.compareRoutes(expectedRoute));
        assertTrue(foundRoute.compareCost(expectedRoute));
    }

    /**
     * 10. Road 2 Node (both ways)
     */
    @Test
    void testPathfinding10() {
        Terrain terrain = Terrain.getInstance();

        AbstractDirtRoadItem abstractDirtRoadItem = new AbstractDirtRoadItem();
        TerrainPosition v1 = new TerrainPosition(50, 50);
        TerrainPosition v2 = new TerrainPosition(51, 50);
        TerrainPosition v3 = new TerrainPosition(52, 50);
        TerrainPosition v4 = new TerrainPosition(53, 50);
        TerrainPosition v5 = new TerrainPosition(54, 50);
        TerrainPosition v6 = new TerrainPosition(53, 49);

        /*
        v2 -> v3 -> v4
         */
        int[] roadPositions = new int[]{
                v1.getX(), v1.getZ(),
                v2.getX(), v2.getZ(),
                v3.getX(), v3.getZ(),
                v4.getX(), v4.getZ(),
                v5.getX(), v5.getZ(),
                v6.getX(), v6.getZ(),
        };

        TerrainPosition[] roads = TerrainPosition.toVectorArray(roadPositions);

        abstractDirtRoadItem.place(roads);

        RoadGraph roadGraph = terrain.getRoadGraph();
        RouteFinder routeFinder = new RouteFinder(roadGraph);
        // Found route
        RouteFinder.Route<RouteRoad> foundRoute1 = routeFinder.findRoute(v2, v4, 0);
        routeFinder.reset();

        RouteFinder.Route<RouteRoad> foundRoute2 = routeFinder.findRoute(v4, v2, 0);

        // Expected global routes
        RouteFinder.Route<RouteRoad> expectedRoute1 = new Route<>();
        RouteFinder.Route<RouteRoad> expectedRoute2;

        // Expected individual routes
        RouteRoad expectedRouteRoad = new RouteRoad(new NormalRoad(v2));
        expectedRouteRoad.setEnd(new RoadNode(v4));
        expectedRouteRoad.addRoad(new NormalRoad(v3));
        expectedRoute1.add(expectedRouteRoad);

        expectedRoute2 = expectedRoute1.invertRoute();

        assertTrue(foundRoute1.compareRoutes(expectedRoute1));
        assertTrue(foundRoute1.compareCost(expectedRoute1));

        assertTrue(foundRoute2.compareRoutes(expectedRoute2));
        assertTrue(foundRoute2.compareCost(expectedRoute2));
    }

    /**
     * 11. Road 2 Road (ignore nodes)
     */
    @Test
    void testPathfinding11() {
        Terrain terrain = Terrain.getInstance();

        AbstractDirtRoadItem abstractDirtRoadItem = new AbstractDirtRoadItem();
        TerrainPosition v1 = new TerrainPosition(50, 50);
        TerrainPosition v2 = new TerrainPosition(51, 50);
        TerrainPosition v3 = new TerrainPosition(52, 50);
        TerrainPosition v4 = new TerrainPosition(53, 50);
        TerrainPosition v5 = new TerrainPosition(54, 50);
        TerrainPosition v6 = new TerrainPosition(55, 50);
        TerrainPosition v7 = new TerrainPosition(56, 50);
        TerrainPosition v8 = new TerrainPosition(51, 49);
        TerrainPosition v9 = new TerrainPosition(55, 49);

        /*
        v3 -> v4 -> v5
         */
        int[] roadPositions = new int[]{
                v1.getX(), v1.getZ(),
                v2.getX(), v2.getZ(),
                v3.getX(), v3.getZ(),
                v4.getX(), v4.getZ(),
                v5.getX(), v5.getZ(),
                v6.getX(), v6.getZ(),
                v7.getX(), v7.getZ(),
                v8.getX(), v8.getZ(),
                v9.getX(), v9.getZ(),
        };

        TerrainPosition[] roads = TerrainPosition.toVectorArray(roadPositions);

        abstractDirtRoadItem.place(roads);

        RoadGraph roadGraph = terrain.getRoadGraph();
        RouteFinder routeFinder = new RouteFinder(roadGraph);
        // Found route
        RouteFinder.Route<RouteRoad> foundRoute = routeFinder.findRoute(v3, v5, 0);

        // Expected global routes
        RouteFinder.Route<RouteRoad> expectedRoute = new Route<>();

        // Expected individual routes
        RouteRoad expectedRouteRoad = new RouteRoad(new NormalRoad(v3));
        expectedRouteRoad.setEnd(new NormalRoad(v5));
        expectedRouteRoad.addRoad(new NormalRoad(v4));
        expectedRoute.add(expectedRouteRoad);


        assertTrue(foundRoute.compareRoutes(expectedRoute));
        assertTrue(foundRoute.compareCost(expectedRoute));
    }

    /**
     * 12. Road 2 Road through node
     */
    @Test
    void testPathfinding12() {
        Terrain terrain = Terrain.getInstance();

        AbstractDirtRoadItem abstractDirtRoadItem = new AbstractDirtRoadItem();
        TerrainPosition v1 = new TerrainPosition(50, 50);
        TerrainPosition v2 = new TerrainPosition(51, 50);
        TerrainPosition v3 = new TerrainPosition(52, 50);
        TerrainPosition v4 = new TerrainPosition(53, 50);
        TerrainPosition v5 = new TerrainPosition(54, 50);
        TerrainPosition v6 = new TerrainPosition(55, 50);
        TerrainPosition v7 = new TerrainPosition(50, 49);
        TerrainPosition v8 = new TerrainPosition(53, 49);

        /*
        v2 -> v3 -> v4 -> v5 -> v6
         */
        int[] roadPositions = new int[]{
                v1.getX(), v1.getZ(),
                v2.getX(), v2.getZ(),
                v3.getX(), v3.getZ(),
                v4.getX(), v4.getZ(),
                v5.getX(), v5.getZ(),
                v6.getX(), v6.getZ(),
                v7.getX(), v7.getZ(),
                v8.getX(), v8.getZ()
        };

        TerrainPosition[] roads = TerrainPosition.toVectorArray(roadPositions);

        abstractDirtRoadItem.place(roads);

        RoadGraph roadGraph = terrain.getRoadGraph();
        RouteFinder routeFinder = new RouteFinder(roadGraph);
        // Found route
        RouteFinder.Route<RouteRoad> foundRoute1 = routeFinder.findRoute(v2, v6, 0);
        routeFinder.reset();

        RouteFinder.Route<RouteRoad> foundRoute2 = routeFinder.findRoute(v6, v2, 0);

        // Expected global routes
        RouteFinder.Route<RouteRoad> expectedRoute1 = new Route<>();
        RouteFinder.Route<RouteRoad> expectedRoute2 = new Route<>();

        // Expected individual routes
        RouteRoad expectedRouteRoad1 = new RouteRoad(new NormalRoad(v2));
        expectedRouteRoad1.setEnd(new RoadNode(v4));
        expectedRouteRoad1.addRoad(new NormalRoad(v3));
        RouteRoad expectedRouteRoad2 = new RouteRoad(new RoadNode(v4));
        expectedRouteRoad2.setEnd(new NormalRoad(v6));
        expectedRouteRoad2.addRoad(new NormalRoad(v5));
        expectedRoute1.add(expectedRouteRoad1);
        expectedRoute1.add(expectedRouteRoad2);

        RouteRoad expectedRouteRoad3 = new RouteRoad(new NormalRoad(v6));
        expectedRouteRoad3.setEnd(new RoadNode(v4));
        expectedRouteRoad3.addRoad(new NormalRoad(v5));
        RouteRoad expectedRouteRoad4 = new RouteRoad(new RoadNode(v4));
        expectedRouteRoad4.setEnd(new NormalRoad(v2));
        expectedRouteRoad4.addRoad(new NormalRoad(v3));
        expectedRoute2.add(expectedRouteRoad3);
        expectedRoute2.add(expectedRouteRoad4);


        assertTrue(foundRoute1.compareRoutes(expectedRoute1));
        assertTrue(foundRoute1.compareCost(expectedRoute1));
        assertTrue(foundRoute2.compareRoutes(expectedRoute2));
        assertTrue(foundRoute2.compareCost(expectedRoute2));
    }

    /**
     * 13. Road 2 Road (disconnected)
     */
    @Test
    void testPathfinding13() {
        Terrain terrain = Terrain.getInstance();

        AbstractDirtRoadItem abstractDirtRoadItem = new AbstractDirtRoadItem();
        TerrainPosition v1 = new TerrainPosition(50, 50);
        TerrainPosition v2 = new TerrainPosition(51, 50);
        TerrainPosition v3 = new TerrainPosition(52, 50);
        TerrainPosition v4 = new TerrainPosition(54, 50);

        int[] roadPositions = new int[]{
                v1.getX(), v1.getZ(),
                v2.getX(), v2.getZ(),
                v3.getX(), v3.getZ(),
                v4.getX(), v4.getZ()
        };

        TerrainPosition[] roads = TerrainPosition.toVectorArray(roadPositions);

        abstractDirtRoadItem.place(roads);

        RoadGraph roadGraph = terrain.getRoadGraph();
        RouteFinder routeFinder = new RouteFinder(roadGraph);
        // Found route
        RouteFinder.Route<RouteRoad> foundRoute = routeFinder.findRoute(v1, v4, 0);

        // Expected global routes
        RouteFinder.Route<RouteRoad> expectedRoute = new Route<>();

        assertTrue(foundRoute.compareRoutes(expectedRoute));
        assertTrue(foundRoute.compareCost(expectedRoute));
    }

    /**
     * 14. Road 2 Node (disconnected)
     */
    @Test
    void testPathfinding14() {
        Terrain terrain = Terrain.getInstance();

        AbstractDirtRoadItem abstractDirtRoadItem = new AbstractDirtRoadItem();
        TerrainPosition v1 = new TerrainPosition(50, 50);
        TerrainPosition v2 = new TerrainPosition(51, 50);
        TerrainPosition v3 = new TerrainPosition(52, 50);

        TerrainPosition v4 = new TerrainPosition(56, 50);
        TerrainPosition v5 = new TerrainPosition(56, 49);
        TerrainPosition v6 = new TerrainPosition(56, 48);
        TerrainPosition v7 = new TerrainPosition(55, 49);
        TerrainPosition v8 = new TerrainPosition(57, 49);

        int[] roadPositions = new int[]{
                v1.getX(), v1.getZ(),
                v2.getX(), v2.getZ(),
                v3.getX(), v3.getZ(),
                v4.getX(), v4.getZ(),
                v5.getX(), v5.getZ(),
                v6.getX(), v6.getZ(),
                v7.getX(), v7.getZ(),
                v8.getX(), v8.getZ()
        };

        TerrainPosition[] roads = TerrainPosition.toVectorArray(roadPositions);

        abstractDirtRoadItem.place(roads);

        RoadGraph roadGraph = terrain.getRoadGraph();
        RouteFinder routeFinder = new RouteFinder(roadGraph);
        // Found route
        RouteFinder.Route<RouteRoad> foundRoute1 = routeFinder.findRoute(v1, v5, 0);
        routeFinder.reset();

        RouteFinder.Route<RouteRoad> foundRoute2 = routeFinder.findRoute(v5, v1, 0);

        // Expected global routes
        RouteFinder.Route<RouteRoad> expectedRoute = new Route<>();

        assertTrue(foundRoute1.compareRoutes(expectedRoute));
        assertTrue(foundRoute1.compareCost(expectedRoute));

        assertTrue(foundRoute2.compareRoutes(expectedRoute));
        assertTrue(foundRoute2.compareCost(expectedRoute));
    }

    /**
     * 15. Node 2 Node (disconnected)
     */
    @Test
    void testPathfinding15() {
        Terrain terrain = Terrain.getInstance();

        AbstractDirtRoadItem abstractDirtRoadItem = new AbstractDirtRoadItem();
        TerrainPosition v1 = new TerrainPosition(50, 50);
        TerrainPosition v2 = new TerrainPosition(51, 50);
        TerrainPosition v3 = new TerrainPosition(52, 50);
        TerrainPosition v4 = new TerrainPosition(51, 49);

        TerrainPosition v5 = new TerrainPosition(55, 50);
        TerrainPosition v6 = new TerrainPosition(56, 50);
        TerrainPosition v7 = new TerrainPosition(57, 50);
        TerrainPosition v8 = new TerrainPosition(56, 49);

        int[] roadPositions = new int[]{
                v1.getX(), v1.getZ(),
                v2.getX(), v2.getZ(),
                v3.getX(), v3.getZ(),
                v4.getX(), v4.getZ(),
                v5.getX(), v5.getZ(),
                v6.getX(), v6.getZ(),
                v7.getX(), v7.getZ(),
                v8.getX(), v8.getZ()
        };

        TerrainPosition[] roads = TerrainPosition.toVectorArray(roadPositions);

        abstractDirtRoadItem.place(roads);

        RoadGraph roadGraph = terrain.getRoadGraph();
        RouteFinder routeFinder = new RouteFinder(roadGraph);
        // Found route
        RouteFinder.Route<RouteRoad> foundRoute = routeFinder.findRoute(v1, v6, 0);

        // Expected global routes
        RouteFinder.Route<RouteRoad> expectedRoute = new Route<>();

        assertTrue(foundRoute.compareRoutes(expectedRoute));
        assertTrue(foundRoute.compareCost(expectedRoute));
    }

    /**
     * 16. Road 2 Road (close)
     */
    @Test
    void testPathfinding16() {
        Terrain terrain = Terrain.getInstance();

        AbstractDirtRoadItem abstractDirtRoadItem = new AbstractDirtRoadItem();
        TerrainPosition v1 = new TerrainPosition(50, 50);
        TerrainPosition v2 = new TerrainPosition(51, 50);
        TerrainPosition v3 = new TerrainPosition(52, 50);
        TerrainPosition v4 = new TerrainPosition(52, 49);


        int[] roadPositions = new int[]{
                v1.getX(), v1.getZ(),
                v2.getX(), v2.getZ(),
                v3.getX(), v3.getZ(),
                v4.getX(), v4.getZ()
        };

        TerrainPosition[] roads = TerrainPosition.toVectorArray(roadPositions);

        abstractDirtRoadItem.place(roads);

        RoadGraph roadGraph = terrain.getRoadGraph();
        RouteFinder routeFinder = new RouteFinder(roadGraph);
        // Found route
        RouteFinder.Route<RouteRoad> foundRoute = routeFinder.findRoute(v2, v3, 0);

        // Expected global routes
        RouteFinder.Route<RouteRoad> expectedRoute = new Route<>();

        // Expected individual routes
        RouteRoad expectedRouteRoad = new RouteRoad(new NormalRoad(v2));
        expectedRouteRoad.setEnd(new NormalRoad(v3));
        expectedRoute.add(expectedRouteRoad);

        assertTrue(foundRoute.compareRoutes(expectedRoute));
        assertTrue(foundRoute.compareCost(expectedRoute));
    }

    /**
     * 17. Road 2 Node (close)
     */
    @Test
    void testPathfinding17() {
        Terrain terrain = Terrain.getInstance();

        AbstractDirtRoadItem abstractDirtRoadItem = new AbstractDirtRoadItem();
        TerrainPosition v1 = new TerrainPosition(50, 50);
        TerrainPosition v2 = new TerrainPosition(51, 50);
        TerrainPosition v3 = new TerrainPosition(52, 50);
        TerrainPosition v4 = new TerrainPosition(51, 49);


        int[] roadPositions = new int[]{
                v1.getX(), v1.getZ(),
                v2.getX(), v2.getZ(),
                v3.getX(), v3.getZ(),
                v4.getX(), v4.getZ()
        };

        TerrainPosition[] roads = TerrainPosition.toVectorArray(roadPositions);

        abstractDirtRoadItem.place(roads);

        RoadGraph roadGraph = terrain.getRoadGraph();
        RouteFinder routeFinder = new RouteFinder(roadGraph);

        // Found route
        RouteFinder.Route<RouteRoad> foundRoute1 = routeFinder.findRoute(v2, v3, 0);
        routeFinder.reset();

        RouteFinder.Route<RouteRoad> foundRoute2 = routeFinder.findRoute(v3, v2, 0);

        // Expected global routes
        RouteFinder.Route<RouteRoad> expectedRoute1 = new Route<>();
        RouteFinder.Route<RouteRoad> expectedRoute2;

        // Expected individual routes
        RouteRoad expectedRouteRoad1 = new RouteRoad(new RoadNode(v2));
        expectedRouteRoad1.setEnd(new NormalRoad(v3));
        expectedRoute1.add(expectedRouteRoad1);

        expectedRoute2 = expectedRoute1.invertRoute();

        assertTrue(foundRoute1.compareRoutes(expectedRoute1));
        assertTrue(foundRoute1.compareCost(expectedRoute1));

        assertTrue(foundRoute2.compareRoutes(expectedRoute2));
        assertTrue(foundRoute2.compareCost(expectedRoute2));
    }

    /**
     * 18. Node 2 Node (close)
     */
    @Test
    void testPathfinding18() {
        Terrain terrain = Terrain.getInstance();

        AbstractDirtRoadItem abstractDirtRoadItem = new AbstractDirtRoadItem();
        TerrainPosition v1 = new TerrainPosition(50, 50);
        TerrainPosition v2 = new TerrainPosition(51, 50);
        TerrainPosition v3 = new TerrainPosition(52, 50);
        TerrainPosition v4 = new TerrainPosition(53, 50);
        TerrainPosition v5 = new TerrainPosition(51, 51);
        TerrainPosition v6 = new TerrainPosition(52, 49);


        int[] roadPositions = new int[]{
                v1.getX(), v1.getZ(),
                v2.getX(), v2.getZ(),
                v3.getX(), v3.getZ(),
                v4.getX(), v4.getZ(),
                v5.getX(), v5.getZ(),
                v6.getX(), v6.getZ()
        };

        TerrainPosition[] roads = TerrainPosition.toVectorArray(roadPositions);

        abstractDirtRoadItem.place(roads);

        RoadGraph roadGraph = terrain.getRoadGraph();
        RouteFinder routeFinder = new RouteFinder(roadGraph);

        // Found route
        RouteFinder.Route<RouteRoad> foundRoute = routeFinder.findRoute(v2, v3, 0);

        // Expected global routes
        RouteFinder.Route<RouteRoad> expectedRoute = new Route<>();

        // Expected individual routes
        RouteRoad expectedRouteRoad = new RouteRoad(new RoadNode(v2));
        expectedRouteRoad.setEnd(new RoadNode(v3));
        expectedRoute.add(expectedRouteRoad);

        assertTrue(foundRoute.compareRoutes(expectedRoute));
        assertTrue(foundRoute.compareCost(expectedRoute));
    }

    /**
     * 19. Node 2 Node (close) Choice
     */
    @Test
    void testPathfinding19() {
        Terrain terrain = Terrain.getInstance();

        AbstractDirtRoadItem abstractDirtRoadItem = new AbstractDirtRoadItem();
        TerrainPosition v1 = new TerrainPosition(50, 50);
        TerrainPosition v2 = new TerrainPosition(51, 50);
        TerrainPosition v3 = new TerrainPosition(52, 50);
        TerrainPosition v4 = new TerrainPosition(53, 50);

        TerrainPosition v5 = new TerrainPosition(51, 51);
        TerrainPosition v6 = new TerrainPosition(52, 51);

        TerrainPosition v7 = new TerrainPosition(51, 49);
        TerrainPosition v8 = new TerrainPosition(52, 49);


        int[] roadPositions = new int[]{
                v1.getX(), v1.getZ(),
                v2.getX(), v2.getZ(),
                v3.getX(), v3.getZ(),
                v4.getX(), v4.getZ(),
                v5.getX(), v5.getZ(),
                v6.getX(), v6.getZ(),
                v7.getX(), v7.getZ(),
                v8.getX(), v8.getZ()
        };

        TerrainPosition[] roads = TerrainPosition.toVectorArray(roadPositions);

        abstractDirtRoadItem.place(roads);

        RoadGraph roadGraph = terrain.getRoadGraph();
        RouteFinder routeFinder = new RouteFinder(roadGraph);

        // Found route
        RouteFinder.Route<RouteRoad> foundRoute = routeFinder.findRoute(v2, v3, 0);

        // Expected global routes
        RouteFinder.Route<RouteRoad> expectedRoute = new Route<>();

        // Expected individual routes
        RouteRoad expectedRouteRoad = new RouteRoad(new RoadNode(v2));
        expectedRouteRoad.setEnd(new RoadNode(v3));
        expectedRoute.add(expectedRouteRoad);

        assertTrue(foundRoute.compareRoutes(expectedRoute));
        assertTrue(foundRoute.compareCost(expectedRoute));
    }

    /**
     * 20. Road 2 Node (close) Choice
     */
    @Test
    void testPathfinding20() {
        Terrain terrain = Terrain.getInstance();

        AbstractDirtRoadItem abstractDirtRoadItem = new AbstractDirtRoadItem();
        TerrainPosition v1 = new TerrainPosition(50, 50);
        TerrainPosition v2 = new TerrainPosition(51, 50);
        TerrainPosition v3 = new TerrainPosition(52, 50);
        TerrainPosition v4 = new TerrainPosition(51, 51);
        TerrainPosition v5 = new TerrainPosition(52, 51);


        int[] roadPositions = new int[]{
                v1.getX(), v1.getZ(),
                v2.getX(), v2.getZ(),
                v3.getX(), v3.getZ(),
                v4.getX(), v4.getZ(),
                v5.getX(), v5.getZ()
        };

        TerrainPosition[] roads = TerrainPosition.toVectorArray(roadPositions);

        abstractDirtRoadItem.place(roads);

        RoadGraph roadGraph = terrain.getRoadGraph();
        RouteFinder routeFinder = new RouteFinder(roadGraph);

        // Found route
        RouteFinder.Route<RouteRoad> foundRoute = routeFinder.findRoute(v2, v3, 0);

        // Expected global routes
        RouteFinder.Route<RouteRoad> expectedRoute = new Route<>();

        // Expected individual routes
        RouteRoad expectedRouteRoad = new RouteRoad(new RoadNode(v2));
        expectedRouteRoad.setEnd(new NormalRoad(v3));
        expectedRoute.add(expectedRouteRoad);

        assertTrue(foundRoute.compareRoutes(expectedRoute));
        assertTrue(foundRoute.compareCost(expectedRoute));
    }

    /**
     * 21. Road 2 Road (close) Choice
     */
    @Test
    void testPathfinding21() {
        Terrain terrain = Terrain.getInstance();

        AbstractDirtRoadItem abstractDirtRoadItem = new AbstractDirtRoadItem();
        TerrainPosition v1 = new TerrainPosition(50, 50);
        TerrainPosition v2 = new TerrainPosition(51, 50);
        TerrainPosition v3 = new TerrainPosition(50, 49);
        TerrainPosition v4 = new TerrainPosition(51, 49);


        int[] roadPositions = new int[]{
                v1.getX(), v1.getZ(),
                v2.getX(), v2.getZ(),
                v3.getX(), v3.getZ(),
                v4.getX(), v4.getZ()
        };

        TerrainPosition[] roads = TerrainPosition.toVectorArray(roadPositions);

        abstractDirtRoadItem.place(roads);

        RoadGraph roadGraph = terrain.getRoadGraph();
        RouteFinder routeFinder = new RouteFinder(roadGraph);

        // Found route
        RouteFinder.Route<RouteRoad> foundRoute = routeFinder.findRoute(v1, v3, 0);

        // Expected global routes
        RouteFinder.Route<RouteRoad> expectedRoute = new Route<>();

        // Expected individual routes
        RouteRoad expectedRouteRoad = new RouteRoad(new NormalRoad(v1));
        expectedRouteRoad.setEnd(new NormalRoad(v3));
        expectedRoute.add(expectedRouteRoad);

        assertTrue(foundRoute.compareRoutes(expectedRoute));
        assertTrue(foundRoute.compareCost(expectedRoute));
    }

    /**
     * 22. Road 2 Road (ignore Nodes) Choice
     */
    @Test
    void testPathfinding22() {
        Terrain terrain = Terrain.getInstance();

        AbstractDirtRoadItem abstractDirtRoadItem = new AbstractDirtRoadItem();
        TerrainPosition v1 = new TerrainPosition(52, 50);
        TerrainPosition v2 = new TerrainPosition(53, 50);
        TerrainPosition v3 = new TerrainPosition(54, 50);


        /*
            v1 -> v2 -> v3
         */
        int[] roadPositions = new int[]{
                50, 50,
                51, 50,
                55, 50,
                56, 50,
                51, 49,
                51, 48,
                52, 48,
                53, 48,
                54, 48,
                55, 48,
                55, 49,
                v1.getX(), v1.getZ(),
                v2.getX(), v2.getZ(),
                v3.getX(), v3.getZ()
        };

        TerrainPosition[] roads = TerrainPosition.toVectorArray(roadPositions);

        abstractDirtRoadItem.place(roads);

        RoadGraph roadGraph = terrain.getRoadGraph();
        RouteFinder routeFinder = new RouteFinder(roadGraph);

        // Found route
        RouteFinder.Route<RouteRoad> foundRoute = routeFinder.findRoute(v1, v3, 0);

        // Expected global routes
        RouteFinder.Route<RouteRoad> expectedRoute = new Route<>();

        // Expected individual routes
        RouteRoad expectedRouteRoad = new RouteRoad(new NormalRoad(v1));
        expectedRouteRoad.setEnd(new NormalRoad(v3));
        expectedRouteRoad.addRoad(new NormalRoad(v2));
        expectedRoute.add(expectedRouteRoad);

        assertTrue(foundRoute.compareRoutes(expectedRoute));
        assertTrue(foundRoute.compareCost(expectedRoute));
    }

    /**
     * 23. Road 2 Road (ignore Nodes) Choice (v2)
     */
    @Test
    void testPathfinding23() {
        Terrain terrain = Terrain.getInstance();

        AbstractDirtRoadItem abstractDirtRoadItem = new AbstractDirtRoadItem();
        TerrainPosition v1 = new TerrainPosition(52, 48);
        TerrainPosition v2 = new TerrainPosition(53, 48);
        TerrainPosition v3 = new TerrainPosition(54, 48);


        /*
            v1 -> v2 -> v3
         */
        int[] roadPositions = new int[]{
                50, 50,
                51, 50,
                55, 50,
                56, 50,
                51, 49,
                51, 48,
                52, 50,
                53, 50,
                54, 50,
                55, 48,
                55, 49,
                50, 48,
                56, 48,
                v1.getX(), v1.getZ(),
                v2.getX(), v2.getZ(),
                v3.getX(), v3.getZ()
        };

        TerrainPosition[] roads = TerrainPosition.toVectorArray(roadPositions);

        abstractDirtRoadItem.place(roads);

        RoadGraph roadGraph = terrain.getRoadGraph();
        RouteFinder routeFinder = new RouteFinder(roadGraph);

        // Found route
        RouteFinder.Route<RouteRoad> foundRoute = routeFinder.findRoute(v1, v3, 0);

        // Expected global routes
        RouteFinder.Route<RouteRoad> expectedRoute = new Route<>();

        // Expected individual routes
        RouteRoad expectedRouteRoad = new RouteRoad(new NormalRoad(v1));
        expectedRouteRoad.setEnd(new NormalRoad(v3));
        expectedRouteRoad.addRoad(new NormalRoad(v2));
        expectedRoute.add(expectedRouteRoad);

        assertTrue(foundRoute.compareRoutes(expectedRoute));
        assertTrue(foundRoute.compareCost(expectedRoute));
    }

    /**
     * 24. Road 2 Road (loop)
     */
    @Test
    void testPathfinding24() {
        Terrain terrain = Terrain.getInstance();

        AbstractDirtRoadItem abstractDirtRoadItem = new AbstractDirtRoadItem();
        TerrainPosition v1 = new TerrainPosition(50, 50);
        TerrainPosition v2 = new TerrainPosition(51, 50);
        TerrainPosition v3 = new TerrainPosition(52, 50);
        TerrainPosition v4 = new TerrainPosition(53, 50);
        TerrainPosition v5 = new TerrainPosition(54, 50);


        /*
            v1 -> v2 -> v3 -> v4 -> v5
         */
        int[] roadPositions = new int[]{
                50, 49,
                50, 48,
                51, 48,
                52, 48,
                53, 48,
                54, 48,
                54, 49,
                v1.getX(), v1.getZ(),
                v2.getX(), v2.getZ(),
                v3.getX(), v3.getZ(),
                v4.getX(), v4.getZ(),
                v5.getX(), v5.getZ(),
        };

        TerrainPosition[] roads = TerrainPosition.toVectorArray(roadPositions);

        abstractDirtRoadItem.place(roads);

        RoadGraph roadGraph = terrain.getRoadGraph();
        RouteFinder routeFinder = new RouteFinder(roadGraph);

        // Found route
        RouteFinder.Route<RouteRoad> foundRoute = routeFinder.findRoute(v1, v5, 0);

        // Expected global routes
        RouteFinder.Route<RouteRoad> expectedRoute = new Route<>();

        // Expected individual routes
        RouteRoad expectedRouteRoad = new RouteRoad(new NormalRoad(v1));
        expectedRouteRoad.setEnd(new NormalRoad(v5));
        expectedRouteRoad.addRoad(new NormalRoad(v2));
        expectedRouteRoad.addRoad(new NormalRoad(v3));
        expectedRouteRoad.addRoad(new NormalRoad(v4));
        expectedRoute.add(expectedRouteRoad);

        assertTrue(foundRoute.compareRoutes(expectedRoute));
        assertTrue(foundRoute.compareCost(expectedRoute));
    }

    /**
     * 25. Road 2 Road (through node) x 2
     */
    @Test
    void testPathfinding25() {
        Terrain terrain = Terrain.getInstance();

        AbstractDirtRoadItem abstractDirtRoadItem = new AbstractDirtRoadItem();
        TerrainPosition v1 = new TerrainPosition(50, 50);
        TerrainPosition v2 = new TerrainPosition(51, 50);
        TerrainPosition v3 = new TerrainPosition(52, 50);
        TerrainPosition v4 = new TerrainPosition(53, 50);
        TerrainPosition v5 = new TerrainPosition(54, 50);
        TerrainPosition v6 = new TerrainPosition(60, 50);
        TerrainPosition v7 = new TerrainPosition(61, 50);
        TerrainPosition v8 = new TerrainPosition(62, 50);
        TerrainPosition v9 = new TerrainPosition(63, 50);
        TerrainPosition v10 = new TerrainPosition(64, 50);

        /*
            v1 -> v2 -> v3 -> v4 -> v5
                        ||
            v6 -> v7 -> v8 -> v9 -> v10
         */
        int[] roadPositions = new int[]{
                52, 49,
                52, 51,
                62, 49,
                62, 51,
                v1.getX(), v1.getZ(),
                v2.getX(), v2.getZ(),
                v3.getX(), v3.getZ(),
                v4.getX(), v4.getZ(),
                v5.getX(), v5.getZ(),
                v6.getX(), v6.getZ(),
                v7.getX(), v7.getZ(),
                v8.getX(), v8.getZ(),
                v9.getX(), v9.getZ(),
                v10.getX(), v10.getZ()
        };

        TerrainPosition[] roads = TerrainPosition.toVectorArray(roadPositions);

        abstractDirtRoadItem.place(roads);

        RoadGraph roadGraph = terrain.getRoadGraph();
        RouteFinder routeFinder = new RouteFinder(roadGraph);

        // Found route
        RouteFinder.Route<RouteRoad> foundRoute1 = routeFinder.findRoute(v1, v5, 0);
        routeFinder.reset();

        RouteFinder.Route<RouteRoad> foundRoute2 = routeFinder.findRoute(v6, v10, 0);

        // Expected global routes
        RouteFinder.Route<RouteRoad> expectedRoute1 = new Route<>();
        RouteFinder.Route<RouteRoad> expectedRoute2 = new Route<>();

        // Expected individual routes

        // v1
        RouteRoad expectedRouteRoad1 = new RouteRoad(new NormalRoad(v1));
        expectedRouteRoad1.setEnd(new RoadNode(v3));
        expectedRouteRoad1.addRoad(new NormalRoad(v2));
        RouteRoad expectedRouteRoad2 = new RouteRoad(new RoadNode(v3));
        expectedRouteRoad2.setEnd(new NormalRoad(v5));
        expectedRouteRoad2.addRoad(new NormalRoad(v4));

        expectedRoute1.add(expectedRouteRoad1);
        expectedRoute1.add(expectedRouteRoad2);

        // v2
        RouteRoad expectedRouteRoad3 = new RouteRoad(new NormalRoad(v6));
        expectedRouteRoad3.setEnd(new RoadNode(v8));
        expectedRouteRoad3.addRoad(new NormalRoad(v7));
        RouteRoad expectedRouteRoad4 = new RouteRoad(new RoadNode(v8));
        expectedRouteRoad4.setEnd(new NormalRoad(v10));
        expectedRouteRoad4.addRoad(new NormalRoad(v9));

        expectedRoute2.add(expectedRouteRoad3);
        expectedRoute2.add(expectedRouteRoad4);

        assertTrue(foundRoute1.compareRoutes(expectedRoute1));
        assertTrue(foundRoute1.compareCost(expectedRoute1));

        assertTrue(foundRoute2.compareRoutes(expectedRoute2));
        assertTrue(foundRoute2.compareCost(expectedRoute2));
    }

    /**
     * 26. Road 2 Road x 2
     */
    @Test
    void testPathfinding26() {
        Terrain terrain = Terrain.getInstance();

        AbstractDirtRoadItem abstractDirtRoadItem = new AbstractDirtRoadItem();
        TerrainPosition v1 = new TerrainPosition(50, 50);
        TerrainPosition v2 = new TerrainPosition(51, 50);
        TerrainPosition v3 = new TerrainPosition(52, 50);

        TerrainPosition v4 = new TerrainPosition(56, 49);
        TerrainPosition v5 = new TerrainPosition(56, 50);
        TerrainPosition v6 = new TerrainPosition(56, 51);

        /*
                                v4
                                ↓
            v1 -> v2 -> v3  ||  v5
                                ↓
                                v6
         */
        int[] roadPositions = new int[]{
                v1.getX(), v1.getZ(),
                v2.getX(), v2.getZ(),
                v3.getX(), v3.getZ(),
                v4.getX(), v4.getZ(),
                v5.getX(), v5.getZ(),
                v6.getX(), v6.getZ()
        };

        TerrainPosition[] roads = TerrainPosition.toVectorArray(roadPositions);

        abstractDirtRoadItem.place(roads);

        RoadGraph roadGraph = terrain.getRoadGraph();
        RouteFinder routeFinder = new RouteFinder(roadGraph);

        // Found route
        RouteFinder.Route<RouteRoad> foundRoute1 = routeFinder.findRoute(v1, v3, 0);
        routeFinder.reset();

        RouteFinder.Route<RouteRoad> foundRoute2 = routeFinder.findRoute(v4, v6, 0);

        // Expected global routes
        RouteFinder.Route<RouteRoad> expectedRoute1 = new Route<>();
        RouteFinder.Route<RouteRoad> expectedRoute2 = new Route<>();

        // Expected individual routes

        // v1
        RouteRoad expectedRouteRoad1 = new RouteRoad(new NormalRoad(v1));
        expectedRouteRoad1.setEnd(new NormalRoad(v3));
        expectedRouteRoad1.addRoad(new NormalRoad(v2));

        expectedRoute1.add(expectedRouteRoad1);

        // v2
        RouteRoad expectedRouteRoad2 = new RouteRoad(new NormalRoad(v4));
        expectedRouteRoad2.setEnd(new NormalRoad(v6));
        expectedRouteRoad2.addRoad(new NormalRoad(v5));

        expectedRoute2.add(expectedRouteRoad2);

        assertTrue(foundRoute1.compareRoutes(expectedRoute1));
        assertTrue(foundRoute1.compareCost(expectedRoute1));

        assertTrue(foundRoute2.compareRoutes(expectedRoute2));
        assertTrue(foundRoute2.compareCost(expectedRoute2));
    }

    /**
     * 27. Node 2 Road x 2
     */
    @Test
    void testPathfinding27() {
        Terrain terrain = Terrain.getInstance();

        AbstractDirtRoadItem abstractDirtRoadItem = new AbstractDirtRoadItem();
        TerrainPosition v1 = new TerrainPosition(51, 50);
        TerrainPosition v2 = new TerrainPosition(52, 50);
        TerrainPosition v3 = new TerrainPosition(53, 50);

        TerrainPosition v4 = new TerrainPosition(56, 50);
        TerrainPosition v5 = new TerrainPosition(57, 50);
        TerrainPosition v6 = new TerrainPosition(58, 50);

        /*
            v1 -> v2 -> v3  ||  v4 -> v5 -> v6
         */
        int[] roadPositions = new int[]{
                50, 50,
                51, 49,
                55, 50,
                56, 51,
                56, 49,
                v1.getX(), v1.getZ(),
                v2.getX(), v2.getZ(),
                v3.getX(), v3.getZ(),
                v4.getX(), v4.getZ(),
                v5.getX(), v5.getZ(),
                v6.getX(), v6.getZ()
        };

        TerrainPosition[] roads = TerrainPosition.toVectorArray(roadPositions);

        abstractDirtRoadItem.place(roads);

        RoadGraph roadGraph = terrain.getRoadGraph();
        RouteFinder routeFinder = new RouteFinder(roadGraph);

        // Found route
        RouteFinder.Route<RouteRoad> foundRoute1 = routeFinder.findRoute(v1, v3, 0);
        routeFinder.reset();

        RouteFinder.Route<RouteRoad> foundRoute2 = routeFinder.findRoute(v3, v1, 0);
        routeFinder.reset();

        RouteFinder.Route<RouteRoad> foundRoute3 = routeFinder.findRoute(v4, v6, 0);
        routeFinder.reset();

        RouteFinder.Route<RouteRoad> foundRoute4 = routeFinder.findRoute(v6, v4, 0);

        // Expected global routes
        RouteFinder.Route<RouteRoad> expectedRoute1 = new Route<>();
        RouteFinder.Route<RouteRoad> expectedRoute2 = new Route<>();
        RouteFinder.Route<RouteRoad> expectedRoute3 = new Route<>();
        RouteFinder.Route<RouteRoad> expectedRoute4 = new Route<>();

        // Expected individual routes

        // v1
        RouteRoad expectedRouteRoad1 = new RouteRoad(new RoadNode(v1));
        expectedRouteRoad1.setEnd(new NormalRoad(v3));
        expectedRouteRoad1.addRoad(new NormalRoad(v2));

        expectedRoute1.add(expectedRouteRoad1);

        RouteRoad expectedRouteRoad2 = new RouteRoad(new NormalRoad(v3));
        expectedRouteRoad2.setEnd(new RoadNode(v1));
        expectedRouteRoad2.addRoad(new NormalRoad(v2));

        expectedRoute2.add(expectedRouteRoad2);

        // v2
        RouteRoad expectedRouteRoad3 = new RouteRoad(new RoadNode(v4));
        expectedRouteRoad3.setEnd(new NormalRoad(v6));
        expectedRouteRoad3.addRoad(new NormalRoad(v5));

        expectedRoute3.add(expectedRouteRoad3);

        RouteRoad expectedRouteRoad4 = new RouteRoad(new NormalRoad(v6));
        expectedRouteRoad4.setEnd(new RoadNode(v4));
        expectedRouteRoad4.addRoad(new NormalRoad(v5));

        expectedRoute4.add(expectedRouteRoad4);

        assertTrue(foundRoute1.compareRoutes(expectedRoute1));
        assertTrue(foundRoute1.compareCost(expectedRoute1));

        assertTrue(foundRoute2.compareRoutes(expectedRoute2));
        assertTrue(foundRoute2.compareCost(expectedRoute2));

        assertTrue(foundRoute3.compareRoutes(expectedRoute3));
        assertTrue(foundRoute3.compareCost(expectedRoute3));

        assertTrue(foundRoute4.compareRoutes(expectedRoute4));
        assertTrue(foundRoute4.compareCost(expectedRoute4));
    }

    /**
     * 28. Node 2 Node x2
     */
    @Test
    void testPathfinding28() {
        Terrain terrain = Terrain.getInstance();

        AbstractDirtRoadItem abstractDirtRoadItem = new AbstractDirtRoadItem();
        TerrainPosition v1 = new TerrainPosition(51, 50);
        TerrainPosition v2 = new TerrainPosition(52, 50);
        TerrainPosition v3 = new TerrainPosition(53, 50);

        TerrainPosition v4 = new TerrainPosition(57, 51);
        TerrainPosition v5 = new TerrainPosition(58, 51);
        TerrainPosition v6 = new TerrainPosition(58, 50);

        /*
            v1 -> v2 -> v3  ||  v4 -> v5
                                      ↓
                                      v6
         */
        int[] roadPositions = new int[]{
                50, 50,
                51, 49,
                53, 49,
                54, 50,
                56, 51,
                57, 52,
                58, 49,
                59, 50,
                v1.getX(), v1.getZ(),
                v2.getX(), v2.getZ(),
                v3.getX(), v3.getZ(),
                v4.getX(), v4.getZ(),
                v5.getX(), v5.getZ(),
                v6.getX(), v6.getZ()
        };

        TerrainPosition[] roads = TerrainPosition.toVectorArray(roadPositions);

        abstractDirtRoadItem.place(roads);

        RoadGraph roadGraph = terrain.getRoadGraph();
        RouteFinder routeFinder = new RouteFinder(roadGraph);

        // Found route
        RouteFinder.Route<RouteRoad> foundRoute1 = routeFinder.findRoute(v1, v3, 0);
        routeFinder.reset();

        RouteFinder.Route<RouteRoad> foundRoute2 = routeFinder.findRoute(v4, v6, 0);


        // Expected global routes
        RouteFinder.Route<RouteRoad> expectedRoute1 = new Route<>();
        RouteFinder.Route<RouteRoad> expectedRoute2 = new Route<>();

        // Expected individual routes

        // v1
        RouteRoad expectedRouteRoad1 = new RouteRoad(new RoadNode(v1));
        expectedRouteRoad1.setEnd(new RoadNode(v3));
        expectedRouteRoad1.addRoad(new NormalRoad(v2));

        expectedRoute1.add(expectedRouteRoad1);

        // v2
        RouteRoad expectedRouteRoad2 = new RouteRoad(new RoadNode(v4));
        expectedRouteRoad2.setEnd(new RoadNode(v6));
        expectedRouteRoad2.addRoad(new NormalRoad(v5));

        expectedRoute2.add(expectedRouteRoad2);

        assertTrue(foundRoute1.compareRoutes(expectedRoute1));
        assertTrue(foundRoute1.compareCost(expectedRoute1));

        assertTrue(foundRoute2.compareRoutes(expectedRoute2));
        assertTrue(foundRoute2.compareCost(expectedRoute2));
    }

    /**
     * 29. Road 2 Road (choice found in practice)
     */
    @Test
    void testPathfinding29() {
        Terrain terrain = Terrain.getInstance();

        AbstractDirtRoadItem abstractDirtRoadItem = new AbstractDirtRoadItem();
        TerrainPosition v1 = new TerrainPosition(50, 50);
        TerrainPosition v2 = new TerrainPosition(51, 50);
        TerrainPosition v3 = new TerrainPosition(52, 50);
        TerrainPosition v4 = new TerrainPosition(53, 50);
        TerrainPosition v5 = new TerrainPosition(54, 50);
        TerrainPosition v6 = new TerrainPosition(55, 50);
        TerrainPosition v7 = new TerrainPosition(56, 50);

        /*
            v1 -> v2 -> v3 -> v4 -> v5 -> v6 -> v7
         */
        int[] roadPositions = new int[]{
                52, 51,
                53, 51,
                54, 51,
                v1.getX(), v1.getZ(),
                v2.getX(), v2.getZ(),
                v3.getX(), v3.getZ(),
                v4.getX(), v4.getZ(),
                v5.getX(), v5.getZ(),
                v6.getX(), v6.getZ(),
                v7.getX(), v7.getZ()
        };

        TerrainPosition[] roads = TerrainPosition.toVectorArray(roadPositions);

        abstractDirtRoadItem.place(roads);

        RoadGraph roadGraph = terrain.getRoadGraph();
        RouteFinder routeFinder = new RouteFinder(roadGraph);

        // Found route
        RouteFinder.Route<RouteRoad> foundRoute1 = routeFinder.findRoute(v1, v7, 0);
        routeFinder.reset();

        RouteFinder.Route<RouteRoad> foundRoute2 = routeFinder.findRoute(v7, v1, 0);


        // Expected global routes
        RouteFinder.Route<RouteRoad> expectedRoute1 = new Route<>();
        RouteFinder.Route<RouteRoad> expectedRoute2 = new Route<>();

        // Expected individual routes

        // v1
        RouteRoad expectedRouteRoad1 = new RouteRoad(new NormalRoad(v1));
        expectedRouteRoad1.setEnd(new RoadNode(v3));
        expectedRouteRoad1.addRoad(new NormalRoad(v2));
        RouteRoad expectedRouteRoad2 = new RouteRoad(new RoadNode(v3));
        expectedRouteRoad2.setEnd(new RoadNode(v4));
        RouteRoad expectedRouteRoad3 = new RouteRoad(new RoadNode(v4));
        expectedRouteRoad3.setEnd(new RoadNode(v5));
        RouteRoad expectedRouteRoad4 = new RouteRoad(new RoadNode(v5));
        expectedRouteRoad4.setEnd(new NormalRoad(v7));
        expectedRouteRoad4.addRoad(new NormalRoad(v6));

        expectedRoute1.add(expectedRouteRoad1);
        expectedRoute1.add(expectedRouteRoad2);
        expectedRoute1.add(expectedRouteRoad3);
        expectedRoute1.add(expectedRouteRoad4);

        // v2
        RouteRoad expectedRouteRoad5 = new RouteRoad(new NormalRoad(v7));
        expectedRouteRoad5.setEnd(new RoadNode(v5));
        expectedRouteRoad5.addRoad(new NormalRoad(v6));
        RouteRoad expectedRouteRoad6 = new RouteRoad(new RoadNode(v5));
        expectedRouteRoad6.setEnd(new RoadNode(v4));
        RouteRoad expectedRouteRoad7 = new RouteRoad(new RoadNode(v4));
        expectedRouteRoad7.setEnd(new RoadNode(v3));
        RouteRoad expectedRouteRoad8 = new RouteRoad(new RoadNode(v3));
        expectedRouteRoad8.setEnd(new NormalRoad(v1));
        expectedRouteRoad8.addRoad(new NormalRoad(v2));

        expectedRoute2.add(expectedRouteRoad5);
        expectedRoute2.add(expectedRouteRoad6);
        expectedRoute2.add(expectedRouteRoad7);
        expectedRoute2.add(expectedRouteRoad8);

        assertTrue(foundRoute1.compareRoutes(expectedRoute1));
        assertTrue(foundRoute1.compareCost(expectedRoute1));

        assertTrue(foundRoute2.compareRoutes(expectedRoute2));
        assertTrue(foundRoute2.compareCost(expectedRoute2));
    }

    /**
     * 30. Road 2 Road (choice found in practice)
     */
    @Test
    void testPathfinding30() {
        Terrain terrain = Terrain.getInstance();

        AbstractDirtRoadItem abstractDirtRoadItem = new AbstractDirtRoadItem();
        TerrainPosition v1 = new TerrainPosition(50, 50);
        TerrainPosition v2 = new TerrainPosition(51, 50);
        TerrainPosition v3 = new TerrainPosition(52, 50);
        TerrainPosition v4 = new TerrainPosition(53, 50);
        TerrainPosition v5 = new TerrainPosition(54, 50);
        TerrainPosition v6 = new TerrainPosition(55, 50);
        TerrainPosition v6b = new TerrainPosition(54, 51);
        TerrainPosition v7 = new TerrainPosition(55, 51);

        /*
                                    v6b -> v7
                                     ↑     ↑
            v1 -> v2 -> v3 -> v4 -> v5  -> v6
         */
        int[] roadPositions = new int[]{
                v1.getX(), v1.getZ(),
                v2.getX(), v2.getZ(),
                v3.getX(), v3.getZ(),
                v4.getX(), v4.getZ(),
                v5.getX(), v5.getZ(),
                v6.getX(), v6.getZ(),
                v6b.getX(), v6b.getZ(),
                v7.getX(), v7.getZ()
        };

        TerrainPosition[] roads = TerrainPosition.toVectorArray(roadPositions);

        abstractDirtRoadItem.place(roads);

        RoadGraph roadGraph = terrain.getRoadGraph();
        RouteFinder routeFinder = new RouteFinder(roadGraph);

        // Found route
        RouteFinder.Route<RouteRoad> foundRoute1 = routeFinder.findRoute(v1, v7, 0);
        routeFinder.reset();

        RouteFinder.Route<RouteRoad> foundRoute2 = routeFinder.findRoute(v7, v1, 0);


        // Expected global routes
        RouteFinder.Route<RouteRoad> expectedRoute1v1 = new Route<>();
        RouteFinder.Route<RouteRoad> expectedRoute2v1 = new Route<>();
        RouteFinder.Route<RouteRoad> expectedRoute1v2 = new Route<>();
        RouteFinder.Route<RouteRoad> expectedRoute2v2 = new Route<>();

        // Expected individual routes

        // v1
        RouteRoad expectedRouteRoad1v1 = new RouteRoad(new NormalRoad(v1));
        expectedRouteRoad1v1.setEnd(new RoadNode(v5));
        expectedRouteRoad1v1.addRoad(new NormalRoad(v2));
        expectedRouteRoad1v1.addRoad(new NormalRoad(v3));
        expectedRouteRoad1v1.addRoad(new NormalRoad(v4));
        RouteRoad expectedRouteRoad2v1 = new RouteRoad(new RoadNode(v5));
        expectedRouteRoad2v1.setEnd(new NormalRoad(v7));
        expectedRouteRoad2v1.addRoad(new NormalRoad(v6));

        expectedRoute1v1.add(expectedRouteRoad1v1);
        expectedRoute1v1.add(expectedRouteRoad2v1);

        RouteRoad expectedRouteRoad3v1 = new RouteRoad(new NormalRoad(v1));
        expectedRouteRoad3v1.setEnd(new RoadNode(v5));
        expectedRouteRoad3v1.addRoad(new NormalRoad(v2));
        expectedRouteRoad3v1.addRoad(new NormalRoad(v3));
        expectedRouteRoad3v1.addRoad(new NormalRoad(v4));
        RouteRoad expectedRouteRoad4v1 = new RouteRoad(new RoadNode(v5));
        expectedRouteRoad4v1.setEnd(new NormalRoad(v7));
        expectedRouteRoad4v1.addRoad(new NormalRoad(v6b));

        expectedRoute2v1.add(expectedRouteRoad3v1);
        expectedRoute2v1.add(expectedRouteRoad4v1);

        // v2
        RouteRoad expectedRouteRoad1v2 = new RouteRoad(new NormalRoad(v7));
        expectedRouteRoad1v2.setEnd(new RoadNode(v5));
        expectedRouteRoad1v2.addRoad(new NormalRoad(v6));
        RouteRoad expectedRouteRoad2v2 = new RouteRoad(new RoadNode(v5));
        expectedRouteRoad2v2.setEnd(new NormalRoad(v1));
        expectedRouteRoad2v2.addRoad(new NormalRoad(v4));
        expectedRouteRoad2v2.addRoad(new NormalRoad(v3));
        expectedRouteRoad2v2.addRoad(new NormalRoad(v2));

        expectedRoute1v2.add(expectedRouteRoad1v2);
        expectedRoute1v2.add(expectedRouteRoad2v2);

        RouteRoad expectedRouteRoad3v2 = new RouteRoad(new NormalRoad(v7));
        expectedRouteRoad3v2.setEnd(new RoadNode(v5));
        expectedRouteRoad3v2.addRoad(new NormalRoad(v6b));
        RouteRoad expectedRouteRoad4v2 = new RouteRoad(new RoadNode(v5));
        expectedRouteRoad4v2.setEnd(new NormalRoad(v1));
        expectedRouteRoad4v2.addRoad(new NormalRoad(v4));
        expectedRouteRoad4v2.addRoad(new NormalRoad(v3));
        expectedRouteRoad4v2.addRoad(new NormalRoad(v2));

        expectedRoute2v2.add(expectedRouteRoad3v2);
        expectedRoute2v2.add(expectedRouteRoad4v2);

        assertTrue(foundRoute1.compareRoutes(expectedRoute1v1) || foundRoute1.compareRoutes(expectedRoute2v1));
        assertTrue(foundRoute1.compareCost(expectedRoute1v1) || foundRoute1.compareCost(expectedRoute2v1));

        assertTrue(foundRoute2.compareRoutes(expectedRoute1v2) || foundRoute2.compareRoutes(expectedRoute2v2));
        assertTrue(foundRoute2.compareCost(expectedRoute1v2) || foundRoute2.compareCost(expectedRoute2v2));
    }


    @Test
    void testUnobstrusiveRoute1() {
        Terrain terrain = Terrain.getInstance();

        RoadGraph roadGraph = terrain.getRoadGraph();
        RouteFinder routeFinder = new RouteFinder(roadGraph);

        TerrainPosition v1 = new TerrainPosition(50, 50);
        TerrainPosition v2 = new TerrainPosition(60, 50);

        TerrainPosition[] positions = new TerrainPosition[]{
                new TerrainPosition(51, 50),
                new TerrainPosition(52, 50),
                new TerrainPosition(53, 50),
                new TerrainPosition(54, 50),
                new TerrainPosition(55, 50),
                new TerrainPosition(56, 50),
                new TerrainPosition(57, 50),
                new TerrainPosition(58, 50),
                new TerrainPosition(59, 50),
        };
        Route<RouteRoad> foundRoute = routeFinder.findUnobstructedRouteV1(v1, v2);
        // Expected global routes
        RouteFinder.Route<RouteRoad> expectedRoute = new Route<>();

        // Expected individual routes
        RouteRoad expectedRouteRoad = new RouteRoad(new NormalRoad(v1));
        NormalRoad end = new NormalRoad(v2);
        expectedRouteRoad.setEnd(end);
        for (TerrainPosition pos : positions)
            expectedRouteRoad.addRoad(new NormalRoad(pos));

        expectedRouteRoad.addRoad(end);
        expectedRoute.add(expectedRouteRoad);

        assertTrue(foundRoute.compareRoutes(expectedRoute));
        assertTrue(foundRoute.compareCost(expectedRoute));
    }

    @Test
    void testUnobstrusiveRoute2() {
        Terrain terrain = Terrain.getInstance();

        RoadGraph roadGraph = terrain.getRoadGraph();
        RouteFinder routeFinder = new RouteFinder(roadGraph);

        TerrainPosition v1 = new TerrainPosition(49, 50);
        TerrainPosition v2 = new TerrainPosition(55, 60);

        TerrainPosition[] positions = new TerrainPosition[]{
                new TerrainPosition(51, 50),
                new TerrainPosition(52, 50),
                new TerrainPosition(53, 50),
                new TerrainPosition(54, 50),
                new TerrainPosition(55, 50),
                new TerrainPosition(56, 50),
                new TerrainPosition(57, 50),
                new TerrainPosition(58, 50),
                new TerrainPosition(59, 50),
        };
        Route<RouteRoad> foundRoute = routeFinder.findUnobstructedRouteV1(v1, v2);
        System.out.println(foundRoute);
        // Expected global routes
        RouteFinder.Route<RouteRoad> expectedRoute = new Route<>();

        // Expected individual routes
        RouteRoad expectedRouteRoad = new RouteRoad(new NormalRoad(v1));
        NormalRoad end = new NormalRoad(v2);
        expectedRouteRoad.setEnd(end);
        for (TerrainPosition pos : positions)
            expectedRouteRoad.addRoad(new NormalRoad(pos));


        expectedRouteRoad.addRoad(end);
        expectedRoute.add(expectedRouteRoad);

        assertTrue(foundRoute.compareRoutes(expectedRoute));
        assertTrue(foundRoute.compareCost(expectedRoute));
    }
}
