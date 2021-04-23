package tests;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.lwjgl.glfw.GLFW.glfwInit;

import entities.Camera.Direction;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pathfinding.NormalRoad;
import pathfinding.RoadGraph;
import pathfinding.RoadNode;
import pathfinding.RouteFinder;
import pathfinding.RouteFinder.Route;
import pathfinding.RouteRoad;
import renderEngine.DisplayManager;
import scene.gameObjects.DirtRoad;
import scene.gameObjects.GameObject;
import scene.gameObjects.Insula;
import scene.gameObjects.Market;
import scene.components.PositionComponent;
import scene.Scene;
import terrains.TerrainPosition;
import util.Utils;

class TerrainTest {

    private final Scene scene = Scene.getInstance();

    @BeforeAll
    public static void init() {
        glfwInit();
        DisplayManager.createDisplayForTests();
    }

    @BeforeEach
    public void resetGameObjects() {
        scene.resetObjects();
        scene.updateRequirements();
        scene.resetRoadGraph();
    }


    @Test
    void testGetRoadConnectionsToRoadGameObject() {
        GameObject.newInstance(DirtRoad.class, new TerrainPosition(50, 50));

        Direction[] directions = scene.getRoadConnections(new TerrainPosition(50, 50));

        assertArrayEquals(new Direction[0], directions);
    }

    @Test
    void testGetRoadConnectionsToRoadGameObject2() {
        TerrainPosition center = new TerrainPosition(50, 50);
        GameObject.newInstance(DirtRoad.class, center);
        GameObject.newInstance(DirtRoad.class, center.add(Direction.toRelativeDistance(Direction.EAST)));

        Direction[] directions = scene.getRoadConnections(center);

        assertArrayEquals(new Direction[]{Direction.EAST}, directions);
    }

    @Test
    void testGetRoadConnectionsToRoadGameObject3() {
        TerrainPosition center = new TerrainPosition(50, 50);
        GameObject.newInstance(DirtRoad.class, center);
        GameObject.newInstance(DirtRoad.class, center.add(Direction.toRelativeDistance(Direction.WEST)));


        Direction[] directions = scene.getRoadConnections(center);

        assertArrayEquals(new Direction[]{Direction.WEST}, directions);
    }

    @Test
    void testGetRoadConnectionsToRoadGameObject4() {
        TerrainPosition center = new TerrainPosition(50, 50);
        GameObject.newInstance(DirtRoad.class, center);
        GameObject.newInstance(DirtRoad.class, center.add(Direction.toRelativeDistance(Direction.SOUTH)));

        Direction[] directions = scene.getRoadConnections(center);

        assertArrayEquals(new Direction[]{Direction.SOUTH}, directions);
    }

    @Test
    void testGetRoadConnectionsToRoadGameObject5() {
        TerrainPosition center = new TerrainPosition(50, 50);
        GameObject.newInstance(DirtRoad.class, center);
        GameObject.newInstance(DirtRoad.class, center.add(Direction.toRelativeDistance(Direction.NORTH)));

        Direction[] directions = scene.getRoadConnections(center);

        assertArrayEquals(new Direction[]{Direction.NORTH}, directions);
    }


    @Test
    void testGetRoadConnectionsToRoadGameObject6() {
        TerrainPosition center = new TerrainPosition(50, 50);
        GameObject.newInstance(DirtRoad.class, center);
        GameObject.newInstance(DirtRoad.class, center.add(Direction.toRelativeDistance(Direction.NORTH)));
        GameObject.newInstance(DirtRoad.class, center.add(Direction.toRelativeDistance(Direction.SOUTH)));

        Direction[] directions = scene.getRoadConnections(center);

        assertArrayEquals(new Direction[]{Direction.NORTH, Direction.SOUTH}, directions);
    }

    @Test
    void testGetRoadConnectionsToRoadGameObject7() {
        TerrainPosition center = new TerrainPosition(50, 50);
        GameObject.newInstance(DirtRoad.class, center);
        GameObject.newInstance(DirtRoad.class, center.add(Direction.toRelativeDistance(Direction.NORTH)));
        GameObject.newInstance(DirtRoad.class, center.add(Direction.toRelativeDistance(Direction.SOUTH)));
        GameObject.newInstance(Insula.class, center.add(Direction.toRelativeDistance(Direction.EAST)));

        Direction[] directions = scene.getRoadConnections(center);

        assertArrayEquals(new Direction[]{Direction.NORTH, Direction.SOUTH}, directions);
    }

    @Test
    void testGetRoadConnectionsToRoadGameObject8() {
        TerrainPosition center = new TerrainPosition(50, 50);
        GameObject.newInstance(DirtRoad.class, center);
        GameObject.newInstance(DirtRoad.class, center.add(Direction.toRelativeDistance(Direction.EAST)));
        GameObject.newInstance(DirtRoad.class, center.add(Direction.toRelativeDistance(Direction.WEST)));
        GameObject.newInstance(Insula.class, center.add(Direction.toRelativeDistance(Direction.EAST)));

        Direction[] directions = scene.getRoadConnections(center);

        assertArrayEquals(new Direction[]{Direction.WEST, Direction.EAST}, directions);
    }

    @Test
    void testGetRoadConnectionsToRoadGameObject9() {
        TerrainPosition center = new TerrainPosition(50, 50);
        GameObject.newInstance(DirtRoad.class, center);
        GameObject.newInstance(DirtRoad.class, center.add(Direction.toRelativeDistance(Direction.EAST)));
        GameObject.newInstance(DirtRoad.class, center.add(Direction.toRelativeDistance(Direction.WEST)));
        GameObject.newInstance(DirtRoad.class, center.add(Direction.toRelativeDistance(Direction.NORTH)));
        GameObject.newInstance(DirtRoad.class, center.add(Direction.toRelativeDistance(Direction.SOUTH)));

        Direction[] directions = scene.getRoadConnections(center);
        assertArrayEquals(new Direction[]{Direction.WEST, Direction.NORTH, Direction.EAST, Direction.SOUTH},
                directions);
    }

    //
//    @Test
//    void testGetRoadGraph() {
//
//
//        AbstractDirtRoadGameObject abstractDirtRoadGameObject = AbstractDirtRoadGameObject.getInstance();
//        AbstractDirtRoadGameObject  abstractDirtRoadGameObject = AbstractDirtRoadGameObject.getInstance();
//
//        AbstractDirtRoadGameObject abstractDirtRoadGameObject = AbstractDirtRoadGameObject.getInstance();
//        AbstractDirtRoadGameObject abstractDirtRoadGameObject = AbstractDirtRoadGameObject.getInstance();
//        TerrainPosition center = new TerrainPosition(50, 50);
//        GameObject.newInstance(DirtRoad.class, center);
//         GameObject.newInstance(DirtRoad.class, center.add(Direction.toRelativeDistance(Direction.EAST)));
//        GameObject.newInstance(DirtRoad.class, center.add(Direction.toRelativeDistance(Direction.WEST)));
//        GameObject.newInstance(DirtRoad.class, center.add(Direction.toRelativeDistance(Direction.NORTH)));
//        GameObject.newInstance(DirtRoad.class, center.add(Direction.toRelativeDistance(Direction.SOUTH)));
//
//        Map<RoadNode, Set<RouteRoad>> res = scene.getRoadGraph();
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
//
//
//        AbstractDirtRoadGameObject abstractDirtRoadGameObject = AbstractDirtRoadGameObject.getInstance();
//        TerrainPosition v0 = new TerrainPosition(49, 50);
//        TerrainPosition v00 = new TerrainPosition(50, 51);
//        TerrainPosition center = new TerrainPosition(50, 50);
//        TerrainPosition v1 = new TerrainPosition(51, 50);
//        TerrainPosition v2 = new TerrainPosition(52, 50);
//        TerrainPosition v3 = new TerrainPosition(53, 50);
//        TerrainPosition v4 = new TerrainPosition(53, 51);
//        TerrainPosition v5 = new TerrainPosition(53, 49);
//
//        GameObject.newInstance(DirtRoad.class, center);
//        GameObject.newInstance(DirtRoad.class, v0);
//        GameObject.newInstance(DirtRoad.class, v00);
//        GameObject.newInstance(DirtRoad.class, v1);
//        GameObject.newInstance(DirtRoad.class, v2);
//        GameObject.newInstance(DirtRoad.class, v3);
//        GameObject.newInstance(DirtRoad.class, v4);
//        GameObject.newInstance(DirtRoad.class, v5);
//
//        Map<RoadNode, Set<RouteRoad>> res = scene.getRoadGraph();
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

        RouteFinder.Route route = new Route();
        RouteRoad routeRoad = new RouteRoad(new RoadNode(v1));
        route.add(routeRoad);

        RouteFinder.Route foundRoute = route.invertRoute();

        RouteFinder.Route expectedRoute = new Route();
        expectedRoute.add(routeRoad);

        assertTrue(foundRoute.compareRoutes(expectedRoute));
        assertTrue(foundRoute.compareCost(expectedRoute));
    }

    @Test
    void testInvertRoute2() {
        TerrainPosition v1 = new TerrainPosition(50, 50);
        TerrainPosition v2 = new TerrainPosition(51, 50);

        RouteFinder.Route route = new Route();
        RouteRoad routeRoad = new RouteRoad(new RoadNode(v1));
        routeRoad.setEnd(new RoadNode(v2));
        route.add(routeRoad);

        RouteFinder.Route foundRoute = route.invertRoute();

        RouteFinder.Route expectedRoute = new Route();
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

        RouteFinder.Route route = new Route();
        // Expected individual routes
        RouteRoad routeRoad = new RouteRoad(new RoadNode(v1));
        routeRoad.setEnd(new RoadNode(v4));
        routeRoad.addRoad(new NormalRoad(v2));
        routeRoad.addRoad(new NormalRoad(v3));
        route.add(routeRoad);

        RouteFinder.Route foundRoute = route.invertRoute();

        RouteFinder.Route expectedRoute = new Route();
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

        RouteFinder.Route route = new Route();

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

        RouteFinder.Route foundRoute = route.invertRoute();

        RouteFinder.Route expectedRoute = new Route();

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

        RouteFinder.Route route = new Route();

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

        RouteFinder.Route foundRoute = route.invertRoute();

        RouteFinder.Route expectedRoute = new Route();

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
        TerrainPosition v1 = new TerrainPosition(50, 50);
        TerrainPosition v2 = new TerrainPosition(51, 50);
        TerrainPosition v3 = new TerrainPosition(52, 50);
        TerrainPosition v4 = new TerrainPosition(53, 50);

        // v1 -> v2 -> v3 -> v4

        GameObject.newInstance(DirtRoad.class, new TerrainPosition(50, 49));
        GameObject.newInstance(DirtRoad.class, new TerrainPosition(50, 51));
        GameObject.newInstance(DirtRoad.class, new TerrainPosition(53, 49));
        GameObject.newInstance(DirtRoad.class, new TerrainPosition(53, 51));
        GameObject.newInstance(DirtRoad.class, v1);
        GameObject.newInstance(DirtRoad.class, v2);
        GameObject.newInstance(DirtRoad.class, v3);
        GameObject.newInstance(DirtRoad.class, v4);

        RoadGraph roadGraph = scene.getRoadGraph();
        RouteFinder routeFinder = new RouteFinder(roadGraph);

        RouteFinder.Route foundRoute = routeFinder.findBestRoute(v1, v4, 0);

        // Expected global route
        RouteFinder.Route expectedRoute = new Route();
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
        TerrainPosition v1 = new TerrainPosition(50, 50);
        TerrainPosition v2 = new TerrainPosition(51, 50);
        TerrainPosition v3 = new TerrainPosition(52, 50);
        TerrainPosition v4 = new TerrainPosition(53, 50);
        TerrainPosition v5 = new TerrainPosition(54, 50);

        // v1 -> v2 -> v3 -> v4 -> v5
        GameObject.newInstance(DirtRoad.class, new TerrainPosition(49, 50));
        GameObject.newInstance(DirtRoad.class, new TerrainPosition(50, 49));
        GameObject.newInstance(DirtRoad.class, new TerrainPosition(52, 49));
        GameObject.newInstance(DirtRoad.class, new TerrainPosition(54, 49));
        GameObject.newInstance(DirtRoad.class, new TerrainPosition(55, 50));
        GameObject.newInstance(DirtRoad.class, v1);
        GameObject.newInstance(DirtRoad.class, v2);
        GameObject.newInstance(DirtRoad.class, v3);
        GameObject.newInstance(DirtRoad.class, v4);
        GameObject.newInstance(DirtRoad.class, v5);

        RoadGraph roadGraph = scene.getRoadGraph();
        RouteFinder routeFinder = new RouteFinder(roadGraph);

        // Found route
        RouteFinder.Route foundRoute = routeFinder.findBestRoute(v1, v5, 0);

        // Expected global route
        RouteFinder.Route expectedRoute = new Route();
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

        TerrainPosition[] roads = TerrainPosition.toPositionArray(roadPositions);

        GameObject.newInstances(DirtRoad.class, roads);

        RoadGraph roadGraph = scene.getRoadGraph();

        RouteFinder routeFinder = new RouteFinder(roadGraph);

        // Found route
        RouteFinder.Route foundRoute = routeFinder.findBestRoute(v1, v5, 0);

        // Expected global route
        RouteFinder.Route expectedRoute = new Route();
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

        TerrainPosition[] roads = TerrainPosition.toPositionArray(roadPositions);

        GameObject.newInstances(DirtRoad.class, roads);

        RoadGraph roadGraph = scene.getRoadGraph();

        RouteFinder routeFinder = new RouteFinder(roadGraph);

        // Found route
        RouteFinder.Route foundRoute = routeFinder.findBestRoute(v1, v9, 0);

        // Expected global route
        RouteFinder.Route expectedRoute = new Route();
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

        TerrainPosition[] roads = TerrainPosition.toPositionArray(roadPositions);

        GameObject.newInstances(DirtRoad.class, roads);

        RoadGraph roadGraph = scene.getRoadGraph();
        RouteFinder routeFinder = new RouteFinder(roadGraph);
        // Found route
        RouteFinder.Route foundRoute = routeFinder.findBestRoute(v1, v12, 0);

        // Expected global routes
        RouteFinder.Route expectedRoute = new Route();
        RouteFinder.Route expectedRoute2 = new Route();

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

        TerrainPosition[] roads = TerrainPosition.toPositionArray(roadPositions);

        GameObject.newInstances(DirtRoad.class, roads);

        RoadGraph roadGraph = scene.getRoadGraph();
        RouteFinder routeFinder = new RouteFinder(roadGraph);
        // Found route
        RouteFinder.Route foundRoute = routeFinder.findBestRoute(v1, v13, 0);

        // Expected global routes
        RouteFinder.Route expectedRoute = new Route();
        RouteFinder.Route expectedRoute2 = new Route();

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

        TerrainPosition[] roads = TerrainPosition.toPositionArray(roadPositions);

        GameObject.newInstances(DirtRoad.class, roads);

        RoadGraph roadGraph = scene.getRoadGraph();
        RouteFinder routeFinder = new RouteFinder(roadGraph);
        // Found route
        RouteFinder.Route foundRoute = routeFinder.findBestRoute(v1, v5, 0);

        // Expected global routes
        RouteFinder.Route expectedRoute = new Route();

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

        TerrainPosition[] roads = TerrainPosition.toPositionArray(roadPositions);

        GameObject.newInstances(DirtRoad.class, roads);

        RoadGraph roadGraph = scene.getRoadGraph();
        RouteFinder routeFinder = new RouteFinder(roadGraph);

        // Found route
        RouteFinder.Route foundRoute = routeFinder.findBestRoute(v1, v6, 0);
        routeFinder.reset();

        RouteFinder.Route foundRoute2 = routeFinder.findBestRoute(v6, v1, 0);

        // Expected global routes
        RouteFinder.Route expectedRoute = new Route();

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

        Route expectedRoute2 = expectedRoute.invertRoute();

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

        TerrainPosition[] roads = TerrainPosition.toPositionArray(roadPositions);

        GameObject.newInstances(DirtRoad.class, roads);

        RoadGraph roadGraph = scene.getRoadGraph();
        RouteFinder routeFinder = new RouteFinder(roadGraph);
        // Found route
        RouteFinder.Route foundRoute = routeFinder.findBestRoute(v1, v5, 0);

        // Expected global routes
        RouteFinder.Route expectedRoute = new Route();

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

        TerrainPosition[] roads = TerrainPosition.toPositionArray(roadPositions);

        GameObject.newInstances(DirtRoad.class, roads);

        RouteFinder routeFinder = new RouteFinder(scene.getRoadGraph());
        // Expected global routes
        Route expectedRoute1 = new Route();
        Route expectedRoute2;

        // Expected individual routes
        RouteRoad expectedRouteRoad = new RouteRoad(new NormalRoad(v2));
        expectedRouteRoad.setEnd(new RoadNode(v4));
        expectedRouteRoad.addRoad(new NormalRoad(v3));
        expectedRoute1.add(expectedRouteRoad);

        expectedRoute2 = expectedRoute1.invertRoute();

        Route actualRoute1 = routeFinder.findBestRoute(v2, v4, 0);
        assertTrue(actualRoute1.compareRoutes(expectedRoute1));
        assertTrue(actualRoute1.compareCost(expectedRoute1));

        routeFinder.reset();

        Route actualRoute2 = routeFinder.findBestRoute(v4, v2, 0);
        assertTrue(actualRoute2.compareRoutes(expectedRoute2));
        assertTrue(actualRoute2.compareCost(expectedRoute2));
    }

    /**
     * 11. Road 2 Road (ignore nodes)
     */
    @Test
    void testPathfinding11() {
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

        TerrainPosition[] roads = TerrainPosition.toPositionArray(roadPositions);

        GameObject.newInstances(DirtRoad.class, roads);

        RoadGraph roadGraph = scene.getRoadGraph();
        RouteFinder routeFinder = new RouteFinder(roadGraph);
        // Found route
        RouteFinder.Route foundRoute = routeFinder.findBestRoute(v3, v5, 0);

        // Expected global routes
        RouteFinder.Route expectedRoute = new Route();

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

        TerrainPosition[] roads = TerrainPosition.toPositionArray(roadPositions);

        GameObject.newInstances(DirtRoad.class, roads);

        RoadGraph roadGraph = scene.getRoadGraph();
        RouteFinder routeFinder = new RouteFinder(roadGraph);
        // Found route
        RouteFinder.Route foundRoute1 = routeFinder.findBestRoute(v2, v6, 0);
        routeFinder.reset();

        RouteFinder.Route foundRoute2 = routeFinder.findBestRoute(v6, v2, 0);

        // Expected global routes
        RouteFinder.Route expectedRoute1 = new Route();
        RouteFinder.Route expectedRoute2 = new Route();

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

        TerrainPosition[] roads = TerrainPosition.toPositionArray(roadPositions);

        GameObject.newInstances(DirtRoad.class, roads);

        RoadGraph roadGraph = scene.getRoadGraph();
        RouteFinder routeFinder = new RouteFinder(roadGraph);
        // Found route
        RouteFinder.Route foundRoute = routeFinder.findBestRoute(v1, v4, 0);

        // Expected global routes
        RouteFinder.Route expectedRoute = new Route();

        assertTrue(foundRoute.compareRoutes(expectedRoute));
        assertTrue(foundRoute.compareCost(expectedRoute));
    }

    /**
     * 14. Road 2 Node (disconnected)
     */
    @Test
    void testPathfinding14() {
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

        TerrainPosition[] roads = TerrainPosition.toPositionArray(roadPositions);

        GameObject.newInstances(DirtRoad.class, roads);

        RoadGraph roadGraph = scene.getRoadGraph();
        RouteFinder routeFinder = new RouteFinder(roadGraph);
        // Found route
        RouteFinder.Route foundRoute1 = routeFinder.findBestRoute(v1, v5, 0);
        routeFinder.reset();

        RouteFinder.Route foundRoute2 = routeFinder.findBestRoute(v5, v1, 0);

        // Expected global routes
        RouteFinder.Route expectedRoute = new Route();

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

        TerrainPosition[] roads = TerrainPosition.toPositionArray(roadPositions);

        GameObject.newInstances(DirtRoad.class, roads);

        RoadGraph roadGraph = scene.getRoadGraph();
        RouteFinder routeFinder = new RouteFinder(roadGraph);
        // Found route
        RouteFinder.Route foundRoute = routeFinder.findBestRoute(v1, v6, 0);

        // Expected global routes
        RouteFinder.Route expectedRoute = new Route();

        assertTrue(foundRoute.compareRoutes(expectedRoute));
        assertTrue(foundRoute.compareCost(expectedRoute));
    }

    /**
     * 16. Road 2 Road (close)
     */
    @Test
    void testPathfinding16() {
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

        TerrainPosition[] roads = TerrainPosition.toPositionArray(roadPositions);

        GameObject.newInstances(DirtRoad.class, roads);

        RoadGraph roadGraph = scene.getRoadGraph();
        RouteFinder routeFinder = new RouteFinder(roadGraph);
        // Found route
        RouteFinder.Route foundRoute = routeFinder.findBestRoute(v2, v3, 0);

        // Expected global routes
        RouteFinder.Route expectedRoute = new Route();

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

        TerrainPosition[] roads = TerrainPosition.toPositionArray(roadPositions);

        GameObject.newInstances(DirtRoad.class, roads);

        RoadGraph roadGraph = scene.getRoadGraph();
        RouteFinder routeFinder = new RouteFinder(roadGraph);

        // Found route
        RouteFinder.Route foundRoute1 = routeFinder.findBestRoute(v2, v3, 0);
        routeFinder.reset();

        RouteFinder.Route foundRoute2 = routeFinder.findBestRoute(v3, v2, 0);

        // Expected global routes
        RouteFinder.Route expectedRoute1 = new Route();
        RouteFinder.Route expectedRoute2;

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

        TerrainPosition[] roads = TerrainPosition.toPositionArray(roadPositions);

        GameObject.newInstances(DirtRoad.class, roads);

        RoadGraph roadGraph = scene.getRoadGraph();
        RouteFinder routeFinder = new RouteFinder(roadGraph);

        // Found route
        RouteFinder.Route foundRoute = routeFinder.findBestRoute(v2, v3, 0);

        // Expected global routes
        RouteFinder.Route expectedRoute = new Route();

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

        TerrainPosition[] roads = TerrainPosition.toPositionArray(roadPositions);

        GameObject.newInstances(DirtRoad.class, roads);

        RoadGraph roadGraph = scene.getRoadGraph();
        RouteFinder routeFinder = new RouteFinder(roadGraph);

        // Found route
        RouteFinder.Route foundRoute = routeFinder.findBestRoute(v2, v3, 0);

        // Expected global routes
        RouteFinder.Route expectedRoute = new Route();

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

        TerrainPosition[] roads = TerrainPosition.toPositionArray(roadPositions);

        GameObject.newInstances(DirtRoad.class, roads);

        RoadGraph roadGraph = scene.getRoadGraph();
        RouteFinder routeFinder = new RouteFinder(roadGraph);

        // Found route
        RouteFinder.Route foundRoute = routeFinder.findBestRoute(v2, v3, 0);

        // Expected global routes
        RouteFinder.Route expectedRoute = new Route();

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

        TerrainPosition[] roads = TerrainPosition.toPositionArray(roadPositions);

        GameObject.newInstances(DirtRoad.class, roads);

        RoadGraph roadGraph = scene.getRoadGraph();
        RouteFinder routeFinder = new RouteFinder(roadGraph);

        // Found route
        RouteFinder.Route foundRoute = routeFinder.findBestRoute(v1, v3, 0);

        // Expected global routes
        RouteFinder.Route expectedRoute = new Route();

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

        TerrainPosition[] roads = TerrainPosition.toPositionArray(roadPositions);

        GameObject.newInstances(DirtRoad.class, roads);

        RoadGraph roadGraph = scene.getRoadGraph();
        RouteFinder routeFinder = new RouteFinder(roadGraph);

        // Found route
        RouteFinder.Route foundRoute = routeFinder.findBestRoute(v1, v3, 0);

        // Expected global routes
        RouteFinder.Route expectedRoute = new Route();

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

        TerrainPosition[] roads = TerrainPosition.toPositionArray(roadPositions);

        GameObject.newInstances(DirtRoad.class, roads);

        RoadGraph roadGraph = scene.getRoadGraph();
        RouteFinder routeFinder = new RouteFinder(roadGraph);

        // Found route
        RouteFinder.Route foundRoute = routeFinder.findBestRoute(v1, v3, 0);

        // Expected global routes
        RouteFinder.Route expectedRoute = new Route();

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

        TerrainPosition[] roads = TerrainPosition.toPositionArray(roadPositions);

        GameObject.newInstances(DirtRoad.class, roads);

        RoadGraph roadGraph = scene.getRoadGraph();
        RouteFinder routeFinder = new RouteFinder(roadGraph);

        // Found route
        RouteFinder.Route foundRoute = routeFinder.findBestRoute(v1, v5, 0);

        // Expected global routes
        RouteFinder.Route expectedRoute = new Route();

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

        TerrainPosition[] roads = TerrainPosition.toPositionArray(roadPositions);

        GameObject.newInstances(DirtRoad.class, roads);

        RoadGraph roadGraph = scene.getRoadGraph();
        RouteFinder routeFinder = new RouteFinder(roadGraph);

        // Found route
        RouteFinder.Route foundRoute1 = routeFinder.findBestRoute(v1, v5, 0);
        routeFinder.reset();

        RouteFinder.Route foundRoute2 = routeFinder.findBestRoute(v6, v10, 0);

        // Expected global routes
        RouteFinder.Route expectedRoute1 = new Route();
        RouteFinder.Route expectedRoute2 = new Route();

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

        TerrainPosition[] roads = TerrainPosition.toPositionArray(roadPositions);

        GameObject.newInstances(DirtRoad.class, roads);

        RoadGraph roadGraph = scene.getRoadGraph();
        RouteFinder routeFinder = new RouteFinder(roadGraph);

        // Found route
        RouteFinder.Route foundRoute1 = routeFinder.findBestRoute(v1, v3, 0);
        routeFinder.reset();

        RouteFinder.Route foundRoute2 = routeFinder.findBestRoute(v4, v6, 0);

        // Expected global routes
        RouteFinder.Route expectedRoute1 = new Route();
        RouteFinder.Route expectedRoute2 = new Route();

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

        TerrainPosition[] roads = TerrainPosition.toPositionArray(roadPositions);

        GameObject.newInstances(DirtRoad.class, roads);

        RoadGraph roadGraph = scene.getRoadGraph();
        RouteFinder routeFinder = new RouteFinder(roadGraph);

        // Found route
        RouteFinder.Route foundRoute1 = routeFinder.findBestRoute(v1, v3, 0);
        routeFinder.reset();

        RouteFinder.Route foundRoute2 = routeFinder.findBestRoute(v3, v1, 0);
        routeFinder.reset();

        RouteFinder.Route foundRoute3 = routeFinder.findBestRoute(v4, v6, 0);
        routeFinder.reset();

        RouteFinder.Route foundRoute4 = routeFinder.findBestRoute(v6, v4, 0);

        // Expected global routes
        RouteFinder.Route expectedRoute1 = new Route();
        RouteFinder.Route expectedRoute2 = new Route();
        RouteFinder.Route expectedRoute3 = new Route();
        RouteFinder.Route expectedRoute4 = new Route();

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

        TerrainPosition[] roads = TerrainPosition.toPositionArray(roadPositions);

        GameObject.newInstances(DirtRoad.class, roads);

        RoadGraph roadGraph = scene.getRoadGraph();
        RouteFinder routeFinder = new RouteFinder(roadGraph);

        // Found route
        RouteFinder.Route foundRoute1 = routeFinder.findBestRoute(v1, v3, 0);
        routeFinder.reset();

        RouteFinder.Route foundRoute2 = routeFinder.findBestRoute(v4, v6, 0);


        // Expected global routes
        RouteFinder.Route expectedRoute1 = new Route();
        RouteFinder.Route expectedRoute2 = new Route();

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

        TerrainPosition[] roads = TerrainPosition.toPositionArray(roadPositions);

        GameObject.newInstances(DirtRoad.class, roads);

        RoadGraph roadGraph = scene.getRoadGraph();
        RouteFinder routeFinder = new RouteFinder(roadGraph);

        // Found route
        RouteFinder.Route foundRoute1 = routeFinder.findBestRoute(v1, v7, 0);
        routeFinder.reset();

        RouteFinder.Route foundRoute2 = routeFinder.findBestRoute(v7, v1, 0);


        // Expected global routes
        RouteFinder.Route expectedRoute1 = new Route();
        RouteFinder.Route expectedRoute2 = new Route();

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
     * 29b. Road 2 Road (choice found in practice)
     */
    @Test
    void testPathfinding29b() {
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

        TerrainPosition[] roads = TerrainPosition.toPositionArray(roadPositions);

        GameObject.newInstances(DirtRoad.class, roads);

        RoadGraph roadGraph = scene.getRoadGraph();
        RouteFinder routeFinder = new RouteFinder(roadGraph);

        // Found route
        RouteFinder.Route foundRoute1 = routeFinder.findBestRoute(v1, v7, 0);
        routeFinder.reset();

        RouteFinder.Route foundRoute2 = routeFinder.findBestRoute(v7, v1, 0);


        // Expected global routes
        RouteFinder.Route expectedRoute1v1 = new Route();
        RouteFinder.Route expectedRoute2v1 = new Route();
        RouteFinder.Route expectedRoute1v2 = new Route();
        RouteFinder.Route expectedRoute2v2 = new Route();

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

    /**
     * 30. Road 2 Road (=)
     */
    @Test
    void testPathfinding30() {
        TerrainPosition v1 = new TerrainPosition(50, 50);
        TerrainPosition v2 = new TerrainPosition(51, 50);
        TerrainPosition v3 = new TerrainPosition(51, 51);

        int[] roadPositions = new int[]{
                v1.getX(), v1.getZ(),
                v2.getX(), v2.getZ(),
                v3.getX(), v3.getZ(),
        };

        TerrainPosition[] roads = TerrainPosition.toPositionArray(roadPositions);

        GameObject.newInstances(DirtRoad.class, roads);

        RoadGraph roadGraph = scene.getRoadGraph();
        RouteFinder routeFinder = new RouteFinder(roadGraph);

        // Found route
        RouteFinder.Route foundRoute = routeFinder.findBestRoute(v2, v2, 0);

        // Expected global routes
        RouteFinder.Route expectedRoute = new Route();

        // Expected individual routes
        NormalRoad start_and_end = new NormalRoad(v2);
        RouteRoad expectedRouteRoad1 = new RouteRoad(start_and_end, start_and_end);
        expectedRouteRoad1.addRoad(start_and_end);

        expectedRoute.add(expectedRouteRoad1);

        assertTrue(foundRoute.compareRoutes(expectedRoute));
        assertTrue(foundRoute.compareCost(expectedRoute));
    }

    /**
     * 31. Node 2 Node (=)
     */
    @Test
    void testPathfinding31() {
        TerrainPosition v1 = new TerrainPosition(50, 50);
        TerrainPosition v2 = new TerrainPosition(50, 49);
        TerrainPosition v3 = new TerrainPosition(51, 49);
        TerrainPosition v4 = new TerrainPosition(50, 48);

        int[] roadPositions = new int[]{
                v1.getX(), v1.getZ(),
                v2.getX(), v2.getZ(),
                v3.getX(), v3.getZ(),
                v4.getX(), v4.getZ(),
        };

        TerrainPosition[] roads = TerrainPosition.toPositionArray(roadPositions);

        GameObject.newInstances(DirtRoad.class, roads);

        RoadGraph roadGraph = scene.getRoadGraph();
        RouteFinder routeFinder = new RouteFinder(roadGraph);

        // Found route
        RouteFinder.Route foundRoute = routeFinder.findBestRoute(v2, v2, 0);

        // Expected global routes
        RouteFinder.Route expectedRoute = new Route();

        // Expected individual routes
        RoadNode start_and_end = new RoadNode(v2);
        RouteRoad expectedRouteRoad1 = new RouteRoad(start_and_end, start_and_end);
        expectedRouteRoad1.addRoad(start_and_end);

        expectedRoute.add(expectedRouteRoad1);

        assertTrue(foundRoute.compareRoutes(expectedRoute));
        assertTrue(foundRoute.compareCost(expectedRoute));
    }

    @Test
    void testUnobstrusiveRoute1() {
        RoadGraph roadGraph = scene.getRoadGraph();
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
        Route foundRoute = routeFinder.findUnobstructedRouteV1(v1, v2);
        // Expected global routes
        RouteFinder.Route expectedRoute = new Route();

        // Expected individual routes
        NormalRoad end = new NormalRoad(v2);
        RouteRoad expectedRouteRoad = new RouteRoad(new NormalRoad(v1), end);

        for (TerrainPosition pos : positions)
            expectedRouteRoad.addRoad(new NormalRoad(pos));

        expectedRouteRoad.addRoad(end);
        expectedRoute.add(expectedRouteRoad);

        assertTrue(foundRoute.compareRoutes(expectedRoute));
        assertTrue(foundRoute.compareCost(expectedRoute));
    }

    @Test
    void testUnobstrusiveRoute2() {
        RoadGraph roadGraph = scene.getRoadGraph();
        RouteFinder routeFinder = new RouteFinder(roadGraph);

        TerrainPosition v1 = new TerrainPosition(50, 50);
        TerrainPosition v2 = new TerrainPosition(55, 50);
        TerrainPosition v3 = new TerrainPosition(55, 60);

        TerrainPosition[] positions = new TerrainPosition[]{
                new TerrainPosition(51, 50),
                new TerrainPosition(52, 50),
                new TerrainPosition(53, 50),
                new TerrainPosition(54, 50)
        };
        TerrainPosition[] positions2 = new TerrainPosition[]{
                new TerrainPosition(55, 51),
                new TerrainPosition(55, 52),
                new TerrainPosition(55, 53),
                new TerrainPosition(55, 54),
                new TerrainPosition(55, 55),
                new TerrainPosition(55, 56),
                new TerrainPosition(55, 57),
                new TerrainPosition(55, 58),
                new TerrainPosition(55, 59)
        };

        Route foundRoute = routeFinder.findUnobstructedRouteV1(v1, v3);

        // Expected global routes
        RouteFinder.Route expectedRoute = new Route();

        // Expected individual routes
        NormalRoad middle = new NormalRoad(v2);
        NormalRoad end = new NormalRoad(v3);
        RouteRoad expectedRouteRoad = new RouteRoad(new NormalRoad(v1), middle);
        RouteRoad expectedRouteRoad2 = new RouteRoad(middle, end);

        for (TerrainPosition pos : positions)
            expectedRouteRoad.addRoad(new NormalRoad(pos));
        expectedRouteRoad.addRoad(middle);

        for (TerrainPosition pos : positions2)
            expectedRouteRoad2.addRoad(new NormalRoad(pos));
        expectedRouteRoad2.addRoad(end);

        expectedRoute.add(expectedRouteRoad);
        expectedRoute.add(expectedRouteRoad2);

        assertTrue(foundRoute.compareRoutes(expectedRoute));
        assertTrue(foundRoute.compareCost(expectedRoute));
    }

    @Test
    void testRoadsConnectedToItem() {
        TerrainPosition v1 = new TerrainPosition(50, 50);
        TerrainPosition v2 = new TerrainPosition(50, 60);

//        AbstractInsula abstractInsula = AbstractInsula.getInstance();
//        abstractInsula.place(v1);

//        AbstractMarket abstractMarket = AbstractMarket.getInstance();
//        Market market = (Market) abstractMarket.place(v2);

        Insula insula = new Insula();
        insula.addComponent(new PositionComponent(v1));
        Market market = new Market();
        market.addComponent(new PositionComponent(v2));

        TerrainPosition[] positions = new TerrainPosition[]{
                new TerrainPosition(50, 53),
                new TerrainPosition(50, 54),
                new TerrainPosition(45, 55),
                new TerrainPosition(55, 55),
                new TerrainPosition(45, 65),
                new TerrainPosition(55, 65),
        };
        GameObject.newInstances(DirtRoad.class, positions);

        TerrainPosition[] connectedRoads = new TerrainPosition[9 * 4];
        for (int i = 0; i < connectedRoads.length / 4; i++) {
            connectedRoads[i * 4] = new TerrainPosition(i + 46, 55);
            connectedRoads[i * 4 + 1] = new TerrainPosition(i + 46, 65);
            connectedRoads[i * 4 + 2] = new TerrainPosition(45, i + 56);
            connectedRoads[i * 4 + 3] = new TerrainPosition(55, i + 56);
        }

        List<GameObject> expectedRoads = new ArrayList<>();

        for (TerrainPosition roadPos : connectedRoads) {
            GameObject.newInstance(DirtRoad.class, roadPos);
            expectedRoads.add(scene.getGameObjectAtPosition(roadPos));

            assertTrue(Utils.listContentEquals(scene.getRoadsConnectedToGameObject(market), expectedRoads));
        }
    }

    @Test
    void testRoadsConnectedToItem2() {
        List<GameObject> expectedRoads = new ArrayList<>();

        Insula insula = new Insula();
        insula.addComponent(new PositionComponent(new TerrainPosition(35, 50)));

        GameObject.newInstances(DirtRoad.class, new TerrainPosition[]{
                new TerrainPosition(31, 48),
                new TerrainPosition(32, 47),
                new TerrainPosition(32, 53),
                new TerrainPosition(32, 54),
                new TerrainPosition(32, 55),
                new TerrainPosition(32, 56),
                new TerrainPosition(34, 54),
                new TerrainPosition(34, 55),
                new TerrainPosition(34, 56),
        });

        TerrainPosition[] connectedRoads = new TerrainPosition[]{
                new TerrainPosition(33, 47),
                new TerrainPosition(32, 48),
                new TerrainPosition(32, 49),
                new TerrainPosition(32, 50),
                new TerrainPosition(32, 51),
                new TerrainPosition(32, 52),
                new TerrainPosition(34, 53),
        };

        for (TerrainPosition roadPos : connectedRoads) {
            GameObject.newInstance(DirtRoad.class, roadPos);
            expectedRoads.add(scene.getGameObjectAtPosition(roadPos));

            assertTrue(Utils.listContentEquals(scene.getRoadsConnectedToGameObject(insula), expectedRoads));
        }
    }

    @Test
    void testPathfindingMarketToInsula1() {
        Insula insula = new Insula();
        insula.addComponent(new PositionComponent(new TerrainPosition(50, 50)));
        Market market = new Market();
        market.addComponent(new PositionComponent(new TerrainPosition(50, 80)));

        TerrainPosition[] positions = new TerrainPosition[23];
        for (int i = 53; i < 76; i++)
            positions[i - 53] = new TerrainPosition(50, i);

        GameObject.newInstances(DirtRoad.class, positions);

        Route bestRoute = RouteFinder.findBestRoute(insula, market, 0);

        Route expectedRoute = new Route();
        RouteRoad routeRoad = new RouteRoad(new NormalRoad(positions[0]),
                new NormalRoad(positions[positions.length - 1]));

        Arrays.stream(positions).skip(1).forEach(pos -> routeRoad.addRoad(new NormalRoad(pos)));
        expectedRoute.add(routeRoad);
        assertEquals(expectedRoute, bestRoute);
    }

    @Test
    void testPathfindingMarketToInsula2() {
        Insula insula = new Insula();
        insula.addComponent(new PositionComponent(new TerrainPosition(50, 50)));
        Market market = new Market();
        market.addComponent(new PositionComponent(new TerrainPosition(50, 80)));

        TerrainPosition[] positions = new TerrainPosition[29];
        for (int i = 53; i < 75; i++)
            positions[i - 53] = new TerrainPosition(50, i);

        positions[22] = new TerrainPosition(51, 74);
        positions[23] = new TerrainPosition(52, 74);
        positions[24] = new TerrainPosition(53, 74);
        positions[25] = new TerrainPosition(54, 74);
        positions[26] = new TerrainPosition(55, 74);
        positions[27] = new TerrainPosition(55, 75);
        positions[28] = new TerrainPosition(55, 76);

        GameObject.newInstances(DirtRoad.class, positions);
        GameObject.newInstance(DirtRoad.class, new TerrainPosition(55, 77));
        GameObject.newInstance(DirtRoad.class, new TerrainPosition(55, 78));
        GameObject.newInstance(DirtRoad.class, new TerrainPosition(55, 79));
        GameObject.newInstance(DirtRoad.class, new TerrainPosition(55, 80));
        GameObject.newInstance(DirtRoad.class, new TerrainPosition(55, 81));
        GameObject.newInstance(DirtRoad.class, new TerrainPosition(55, 82));
        GameObject.newInstance(DirtRoad.class, new TerrainPosition(55, 83));
        GameObject.newInstance(DirtRoad.class, new TerrainPosition(55, 84));
        GameObject.newInstance(DirtRoad.class, new TerrainPosition(55, 85));

        Route bestRoute = RouteFinder.findBestRoute(insula, market, 0);

        Route expectedRoute = new Route();
        RouteRoad routeRoad = new RouteRoad(new NormalRoad(positions[0]),
                new NormalRoad(positions[positions.length - 1]));

        Arrays.stream(positions).skip(1).forEach(pos -> routeRoad.addRoad(new NormalRoad(pos)));
        expectedRoute.add(routeRoad);
        assertEquals(expectedRoute, bestRoute);
    }

    @Test
    void testPathfindingMarketToInsula3() {
        Insula insula = new Insula();
        insula.addComponent(new PositionComponent(new TerrainPosition(50, 50)));
        Market market = new Market();
        market.addComponent(new PositionComponent(new TerrainPosition(50, 80)));

        TerrainPosition[] positions = new TerrainPosition[22];
        for (int i = 53; i < 75; i++)
            positions[i - 53] = new TerrainPosition(50, i);

        GameObject.newInstances(DirtRoad.class, positions);
        TerrainPosition[] connectedRoads = new TerrainPosition[9 * 4];
        for (int i = 0; i < connectedRoads.length / 4; i++) {
            connectedRoads[i * 4] = new TerrainPosition(i + 46, 75);
            connectedRoads[i * 4 + 1] = new TerrainPosition(i + 46, 85);
            connectedRoads[i * 4 + 2] = new TerrainPosition(45, i + 76);
            connectedRoads[i * 4 + 3] = new TerrainPosition(55, i + 76);
        }
        GameObject.newInstances(DirtRoad.class, connectedRoads);

        Route bestRoute = RouteFinder.findBestRoute(insula, market, 0);

        Route expectedRoute = new Route();
        RouteRoad routeRoad = new RouteRoad(new NormalRoad(positions[0]), new NormalRoad(new TerrainPosition(50, 75)));

        Arrays.stream(positions).skip(1).forEach(pos -> routeRoad.addRoad(new NormalRoad(pos)));
        expectedRoute.add(routeRoad);
        assertEquals(expectedRoute, bestRoute);
    }

    @Test
    void testPathfindingMarketToInsula4() {
        Insula insula = new Insula();
        insula.addComponent(new PositionComponent(new TerrainPosition(35, 50)));
        Market market = new Market();
        market.addComponent(new PositionComponent(new TerrainPosition(36, 61)));

        TerrainPosition[] positions = new TerrainPosition[]{
                new TerrainPosition(33, 47),
                new TerrainPosition(31, 48),
                new TerrainPosition(32, 47),
                new TerrainPosition(32, 48),
                new TerrainPosition(32, 49),
                new TerrainPosition(32, 50),
                new TerrainPosition(32, 51),
                new TerrainPosition(32, 52),
                new TerrainPosition(32, 53),
                new TerrainPosition(32, 54),
                new TerrainPosition(32, 55),
                new TerrainPosition(32, 56),
                new TerrainPosition(34, 53),
                new TerrainPosition(34, 54),
                new TerrainPosition(34, 55),
                new TerrainPosition(34, 56),
                new TerrainPosition(35, 53),
                new TerrainPosition(36, 53),
                new TerrainPosition(37, 53),
                new TerrainPosition(33, 54),
                new TerrainPosition(33, 55),
                new TerrainPosition(33, 53),
                new TerrainPosition(33, 56),
        };

        GameObject.newInstances(DirtRoad.class, positions);

        Route bestRoute = RouteFinder.findBestRoute(insula, market, 0);
        Route expectedRoute = new Route();
        RouteRoad routeRoad = new RouteRoad(new RoadNode(new TerrainPosition(33, 53)),
                new RoadNode(new TerrainPosition(33, 54)));
        routeRoad.addRoad(new RoadNode(new TerrainPosition(33, 54)));

        RouteRoad routeRoad2 = new RouteRoad(new RoadNode(new TerrainPosition(33, 54)),
                new RoadNode(new TerrainPosition(33, 55)));
        routeRoad2.addRoad(new RoadNode(new TerrainPosition(33, 55)));

        RouteRoad routeRoad3 = new RouteRoad(new RoadNode(new TerrainPosition(33, 55)),
                new RoadNode(new TerrainPosition(33, 56)));
        routeRoad3.addRoad(new RoadNode(new TerrainPosition(33, 56)));

        Route expectedRoute2 = new Route();
        RouteRoad routeRoad21 = new RouteRoad(new RoadNode(new TerrainPosition(34, 53)),
                new RoadNode(new TerrainPosition(34, 54)));
        routeRoad21.addRoad(new RoadNode(new TerrainPosition(34, 54)));

        RouteRoad routeRoad22 = new RouteRoad(new RoadNode(new TerrainPosition(34, 54)),
                new RoadNode(new TerrainPosition(34, 55)));
        routeRoad22.addRoad(new RoadNode(new TerrainPosition(34, 55)));

        RouteRoad routeRoad23 = new RouteRoad(new RoadNode(new TerrainPosition(34, 55)),
                new NormalRoad(new TerrainPosition(34, 56)));
        routeRoad23.addRoad(new NormalRoad(new TerrainPosition(34, 56)));

        expectedRoute.add(routeRoad);
        expectedRoute.add(routeRoad2);
        expectedRoute.add(routeRoad3);

        expectedRoute2.add(routeRoad21);
        expectedRoute2.add(routeRoad22);
        expectedRoute2.add(routeRoad23);

        assertEquals(expectedRoute2, bestRoute);
        assertTrue(expectedRoute.compareRoutes(bestRoute) || expectedRoute2.compareRoutes(bestRoute));
        assertTrue(expectedRoute.compareCost(bestRoute) && expectedRoute2.compareCost(bestRoute));
    }

    @Test
    void testPathfindingMarketToInsula5() {
        Insula insula = new Insula();
        insula.addComponent(new PositionComponent(new TerrainPosition(35, 50)));
        Market market = new Market();
        market.addComponent(new PositionComponent(new TerrainPosition(36, 61)));

        TerrainPosition[] positions = new TerrainPosition[]{
                new TerrainPosition(33, 47),
                new TerrainPosition(31, 48),
                new TerrainPosition(32, 47),
                new TerrainPosition(32, 48),
                new TerrainPosition(32, 49),
                new TerrainPosition(32, 50),
                new TerrainPosition(32, 51),
                new TerrainPosition(32, 52),
                new TerrainPosition(32, 53),
                new TerrainPosition(32, 54),
                new TerrainPosition(32, 55),
                new TerrainPosition(32, 56),
                new TerrainPosition(34, 53),
                new TerrainPosition(34, 54),
                new TerrainPosition(34, 55),
                new TerrainPosition(34, 56),
                new TerrainPosition(35, 53),
                new TerrainPosition(36, 53),
                new TerrainPosition(37, 53),
                new TerrainPosition(33, 54),
                new TerrainPosition(33, 55),
                new TerrainPosition(33, 53),
                new TerrainPosition(33, 56),
        };

        GameObject.newInstances(DirtRoad.class, positions);

        Route bestRoute = RouteFinder.findBestRoute(insula, market, 0);
        Route expectedRoute = new Route();
        RouteRoad routeRoad = new RouteRoad(new RoadNode(new TerrainPosition(33, 53)),
                new RoadNode(new TerrainPosition(33, 54)));
        routeRoad.addRoad(new RoadNode(new TerrainPosition(33, 54)));

        RouteRoad routeRoad2 = new RouteRoad(new RoadNode(new TerrainPosition(33, 54)),
                new RoadNode(new TerrainPosition(33, 55)));
        routeRoad2.addRoad(new RoadNode(new TerrainPosition(33, 55)));

        RouteRoad routeRoad3 = new RouteRoad(new RoadNode(new TerrainPosition(33, 55)),
                new RoadNode(new TerrainPosition(33, 56)));
        routeRoad3.addRoad(new RoadNode(new TerrainPosition(33, 56)));

        expectedRoute.add(routeRoad);
        expectedRoute.add(routeRoad2);
        expectedRoute.add(routeRoad3);
        assertEquals(expectedRoute, bestRoute);
    }

    @Test
    void testPathfindingMarketToInsula6() {
        Market market = new Market();
        market.addComponent(new PositionComponent(new TerrainPosition(50, 80)));

        TerrainPosition[] positions = new TerrainPosition[23];
        for (int i = 53; i < 76; i++)
            positions[i - 53] = new TerrainPosition(50, i);

        GameObject.newInstances(DirtRoad.class, positions);

        Insula insula = new Insula();
        insula.addComponent(new PositionComponent(new TerrainPosition(50, 50)));

        Route bestRoute = RouteFinder.findBestRoute(insula, market, 0);

        Route expectedRoute = new Route();
        RouteRoad routeRoad = new RouteRoad(new NormalRoad(positions[0]),
                new NormalRoad(positions[positions.length - 1]));

        Arrays.stream(positions).skip(1).forEach(pos -> routeRoad.addRoad(new NormalRoad(pos)));
        expectedRoute.add(routeRoad);
        assertEquals(expectedRoute, bestRoute);
    }

    @Test
    void testPathfindingMarketToInsula7() {
        Insula insula = new Insula();
        insula.addComponent(new PositionComponent(new TerrainPosition(50, 50)));

        TerrainPosition[] positions = new TerrainPosition[23];
        for (int i = 53; i < 76; i++)
            positions[i - 53] = new TerrainPosition(50, i);

        GameObject.newInstances(DirtRoad.class, positions);

        Market market = new Market();
        market.addComponent(new PositionComponent(new TerrainPosition(50, 80)));

        Route bestRoute = RouteFinder.findBestRoute(insula, market, 0);

        Route expectedRoute = new Route();
        RouteRoad routeRoad = new RouteRoad(new NormalRoad(positions[0]),
                new NormalRoad(positions[positions.length - 1]));

        Arrays.stream(positions).skip(1).forEach(pos -> routeRoad.addRoad(new NormalRoad(pos)));
        expectedRoute.add(routeRoad);
        assertEquals(expectedRoute, bestRoute);
    }

    @Test
    void testPathfindingMarketToInsula8() {
        Market market = new Market();
        market.addComponent(new PositionComponent(new TerrainPosition(50, 50)));

        TerrainPosition[] positions = new TerrainPosition[15];
        for (int i = 55; i < 70; i++)
            positions[i - 55] = new TerrainPosition(50, i);

        Insula insula = new Insula();
        insula.addComponent(new PositionComponent(new TerrainPosition(46, 60)));

        GameObject.newInstances(DirtRoad.class, positions);
        GameObject.newInstance(DirtRoad.class, new TerrainPosition(51, 68));

        Route bestRoute = RouteFinder.findBestRoute(insula, market, 0);

        Route expectedRoute = new Route();

        NormalRoad end = new NormalRoad(new TerrainPosition(50, 58));
        RouteRoad routeRoad = new RouteRoad(new NormalRoad(new TerrainPosition(50, 55)), end);

        Arrays.stream(positions, 0, 4).forEach(pos -> routeRoad.addRoad(new NormalRoad(pos)));
        expectedRoute.add(routeRoad.invertRoute());
        assertEquals(expectedRoute, bestRoute);
    }

    @Test
    void testPathfindingMarketToInsula9() {
        Market market = new Market();
        market.addComponent(new PositionComponent(new TerrainPosition(50, 50)));

        TerrainPosition[] positions = new TerrainPosition[15];
        for (int i = 55; i < 70; i++)
            positions[i - 55] = new TerrainPosition(50, i);

        Insula insula = new Insula();
        insula.addComponent(new PositionComponent(new TerrainPosition(46, 60)));

        GameObject.newInstances(DirtRoad.class, positions);
        GameObject.newInstance(DirtRoad.class, new TerrainPosition(51, 68));
        GameObject.newInstance(DirtRoad.class, new TerrainPosition(51, 55));
        GameObject.newInstance(DirtRoad.class, new TerrainPosition(49, 55));

        Route bestRoute = RouteFinder.findBestRoute(insula, market, 0);
        Route expectedRoute = new Route();

        NormalRoad end = new NormalRoad(new TerrainPosition(50, 58));
        RouteRoad routeRoad = new RouteRoad(new NormalRoad(new TerrainPosition(50, 55)), end);

        Arrays.stream(positions, 0, 4).forEach(pos -> routeRoad.addRoad(new NormalRoad(pos)));
        expectedRoute.add(routeRoad.invertRoute());
        assertEquals(expectedRoute, bestRoute);
    }

    @Test
    void testPathfindingMarketToInsula10() {
        Market market = new Market();
        market.addComponent(new PositionComponent(new TerrainPosition(50, 50)));

        TerrainPosition[] positions = new TerrainPosition[15];
        for (int i = 55; i < 70; i++)
            positions[i - 55] = new TerrainPosition(50, i);

        Insula insula = new Insula();
        insula.addComponent(new PositionComponent(new TerrainPosition(46, 60)));

        GameObject.newInstances(DirtRoad.class, positions);
        GameObject.newInstance(DirtRoad.class, new TerrainPosition(51, 68));
        GameObject.newInstance(DirtRoad.class, new TerrainPosition(51, 55));
        GameObject.newInstance(DirtRoad.class, new TerrainPosition(49, 55));
        GameObject.newInstance(DirtRoad.class, new TerrainPosition(49, 65));

        Route bestRoute = RouteFinder.findBestRoute(insula, market, 0);
        Route expectedRoute = new Route();

        NormalRoad end = new NormalRoad(new TerrainPosition(50, 58));
        RouteRoad routeRoad = new RouteRoad(new NormalRoad(new TerrainPosition(50, 55)), end);

        Arrays.stream(positions, 0, 4).forEach(pos -> routeRoad.addRoad(new NormalRoad(pos)));
        expectedRoute.add(routeRoad.invertRoute());
        assertEquals(expectedRoute, bestRoute);
    }

    @Test
    void testPathfindingMarketToInsula11() {
        Market market = new Market();
        market.addComponent(new PositionComponent(new TerrainPosition(50, 50)));

        TerrainPosition[] positions = new TerrainPosition[15];
        for (int i = 55; i < 70; i++)
            positions[i - 55] = new TerrainPosition(50, i);

        Insula insula = new Insula();
        insula.addComponent(new PositionComponent(new TerrainPosition(53, 60)));

        GameObject.newInstances(DirtRoad.class, positions);
        GameObject.newInstance(DirtRoad.class, new TerrainPosition(51, 55));
        GameObject.newInstance(DirtRoad.class, new TerrainPosition(49, 55));
        GameObject.newInstance(DirtRoad.class, new TerrainPosition(51, 63));

        Route bestRoute = RouteFinder.findBestRoute(insula, market, 0);
        Route expectedRoute = new Route();

        NormalRoad end = new NormalRoad(new TerrainPosition(50, 58));
        RouteRoad routeRoad = new RouteRoad(new NormalRoad(new TerrainPosition(50, 55)), end);

        Arrays.stream(positions, 0, 4).forEach(pos -> routeRoad.addRoad(new NormalRoad(pos)));
        expectedRoute.add(routeRoad.invertRoute());
        assertEquals(expectedRoute, bestRoute);
    }

    @Test
    void testPathfindingMarketToInsula12() {
        Market market = new Market();
        market.addComponent(new PositionComponent(new TerrainPosition(50, 50)));
        Market market2 = new Market();
        market2.addComponent(new PositionComponent(new TerrainPosition(50, 50)));

        TerrainPosition[] positions = new TerrainPosition[15];
        TerrainPosition[] positions2 = new TerrainPosition[14];
        for (int i = 55; i < 70; i++)
            positions[i - 55] = new TerrainPosition(50, i);
        for (int i = 50; i < 64; i++)
            positions2[i - 50] = new TerrainPosition(i, 70);

        Insula insula = new Insula();
        insula.addComponent(new PositionComponent(new TerrainPosition(62, 74)));

        GameObject.newInstances(DirtRoad.class, positions);
        GameObject.newInstance(DirtRoad.class, new TerrainPosition(51, 55));
        GameObject.newInstance(DirtRoad.class, new TerrainPosition(49, 55));
        GameObject.newInstance(DirtRoad.class, new TerrainPosition(51, 63));
        GameObject.newInstance(DirtRoad.class, new TerrainPosition(62, 71));

        List<TerrainPosition> pos2 = Arrays.asList(positions2);
        Collections.reverse(pos2);
        positions2 = pos2.toArray(new TerrainPosition[0]);
        GameObject.newInstances(DirtRoad.class, positions2);

        Route bestRoute = RouteFinder.findBestRoute(insula, market, 0);

        Route expectedRoute = new Route();

        RoadNode node1 = new RoadNode(new TerrainPosition(50, 63));
        RoadNode node2 = new RoadNode(new TerrainPosition(62, 70));
        RoadNode node3 = new RoadNode(new TerrainPosition(50, 55));

        RouteRoad routeRoad1 = new RouteRoad(new NormalRoad(new TerrainPosition(62, 71)), node2);
        routeRoad1.addRoad(node2);
        expectedRoute.add(routeRoad1);

        RouteRoad routeRoad2 = new RouteRoad(node2, node1);
        Arrays.stream(positions2, 1, 14).forEach(p -> routeRoad2.addRoad(new NormalRoad(p)));
        List<TerrainPosition> pos = Arrays.asList(positions);
        Collections.reverse(pos);
        IntStream.range(0, 6).mapToObj(pos::get).forEach(p -> routeRoad2.addRoad(new NormalRoad(p)));
        routeRoad2.addRoad(node1);
        expectedRoute.add(routeRoad2);

        RouteRoad routeRoad3 = new RouteRoad(node3, node1);
        Collections.reverse(pos);
        IntStream.range(0, 8).mapToObj(pos::get).forEach(p -> routeRoad3.addRoad(new NormalRoad(p)));
        routeRoad3.addRoad(node1);
        expectedRoute.add(routeRoad3.invertRoute());
        assertEquals(expectedRoute, bestRoute);
    }
}
