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
import pathfinding.NodeConnection;
import pathfinding.NodeRoad;
import pathfinding.NormalRoad;
import pathfinding.Path;
import pathfinding.PathFinder;
import pathfinding.RoadGraph;
import renderEngine.DisplayManager;
import renderEngine.PathRenderer;
import scene.Scene;
import scene.components.PositionComponent;
import scene.gameObjects.DirtRoad;
import scene.gameObjects.GameObject;
import scene.gameObjects.Insula;
import scene.gameObjects.Market;
import terrains.TerrainPosition;
import util.Utils;

class TerrainTest {

    private final Scene scene = Scene.getInstance();

    @BeforeAll
    public static void init() {
        glfwInit();
        DisplayManager.createDisplayForTests();
        PathRenderer.getInstance();
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
        GameObject.newInstance(DirtRoad.class, center.add(Direction.toRelativeDistance(Direction.WEST)));

        Direction[] directions = scene.getRoadConnections(center);

        assertArrayEquals(new Direction[]{Direction.WEST}, directions);
    }

    @Test
    void testGetRoadConnectionsToRoadGameObject3() {
        TerrainPosition center = new TerrainPosition(50, 50);
        GameObject.newInstance(DirtRoad.class, center);
        GameObject.newInstance(DirtRoad.class, center.add(Direction.toRelativeDistance(Direction.EAST)));


        Direction[] directions = scene.getRoadConnections(center);

        assertArrayEquals(new Direction[]{Direction.EAST}, directions);
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
        GameObject.newInstance(Insula.class, center.add(Direction.toRelativeDistance(Direction.WEST)));

        Direction[] directions = scene.getRoadConnections(center);

        assertArrayEquals(new Direction[]{Direction.NORTH, Direction.SOUTH}, directions);
    }

    @Test
    void testGetRoadConnectionsToRoadGameObject8() {
        TerrainPosition center = new TerrainPosition(50, 50);
        GameObject.newInstance(DirtRoad.class, center);
        GameObject.newInstance(DirtRoad.class, center.add(Direction.toRelativeDistance(Direction.WEST)));
        GameObject.newInstance(DirtRoad.class, center.add(Direction.toRelativeDistance(Direction.EAST)));
        GameObject.newInstance(Insula.class, center.add(Direction.toRelativeDistance(Direction.WEST)));

        Direction[] directions = scene.getRoadConnections(center);

        assertArrayEquals(new Direction[]{Direction.EAST, Direction.WEST}, directions);
    }

    @Test
    void testGetRoadConnectionsToRoadGameObject9() {
        TerrainPosition center = new TerrainPosition(50, 50);
        GameObject.newInstance(DirtRoad.class, center);
        GameObject.newInstance(DirtRoad.class, center.add(Direction.toRelativeDistance(Direction.WEST)));
        GameObject.newInstance(DirtRoad.class, center.add(Direction.toRelativeDistance(Direction.EAST)));
        GameObject.newInstance(DirtRoad.class, center.add(Direction.toRelativeDistance(Direction.NORTH)));
        GameObject.newInstance(DirtRoad.class, center.add(Direction.toRelativeDistance(Direction.SOUTH)));

        Direction[] directions = scene.getRoadConnections(center);
        assertArrayEquals(new Direction[]{Direction.EAST, Direction.NORTH, Direction.WEST, Direction.SOUTH},
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
    void testInvertPath1() {
        TerrainPosition v1 = new TerrainPosition(50, 50);

        Path path = new Path();
        NodeConnection nodeConnection = new NodeConnection(new NodeRoad(v1));
        path.add(nodeConnection);

        Path foundPath = path.invertPath();

        Path expectedPath = new Path();
        expectedPath.add(nodeConnection);

        assertTrue(foundPath.comparePaths(expectedPath));
        assertTrue(foundPath.compareCost(expectedPath));
    }

    @Test
    void testInvertPath2() {
        TerrainPosition v1 = new TerrainPosition(50, 50);
        TerrainPosition v2 = new TerrainPosition(51, 50);

        Path path = new Path();
        NodeConnection nodeConnection = new NodeConnection(new NodeRoad(v1));
        nodeConnection.setEnd(new NodeRoad(v2));
        path.add(nodeConnection);

        Path foundPath = path.invertPath();

        Path expectedPath = new Path();
        NodeConnection expectedNodeConnection = new NodeConnection(new NodeRoad(v2));
        expectedNodeConnection.setEnd(new NodeRoad(v1));
        expectedPath.add(expectedNodeConnection);

        assertTrue(foundPath.comparePaths(expectedPath));
        assertTrue(foundPath.compareCost(expectedPath));
    }

    @Test
    void testInvertPath3() {
        TerrainPosition v1 = new TerrainPosition(50, 50);
        TerrainPosition v2 = new TerrainPosition(51, 50);
        TerrainPosition v3 = new TerrainPosition(52, 50);
        TerrainPosition v4 = new TerrainPosition(53, 50);

        Path path = new Path();
        // Expected individual routes
        NodeConnection nodeConnection = new NodeConnection(new NodeRoad(v1));
        nodeConnection.setEnd(new NodeRoad(v4));
        nodeConnection.addRoad(new NormalRoad(v2));
        nodeConnection.addRoad(new NormalRoad(v3));
        path.add(nodeConnection);

        Path foundPath = path.invertPath();

        Path expectedPath = new Path();
        // Expected individual routes
        NodeConnection expectedNodeConnection = new NodeConnection(new NodeRoad(v4));
        expectedNodeConnection.setEnd(new NodeRoad(v1));
        expectedNodeConnection.addRoad(new NormalRoad(v3));
        expectedNodeConnection.addRoad(new NormalRoad(v2));
        expectedPath.add(expectedNodeConnection);

        assertTrue(foundPath.comparePaths(expectedPath));
        assertTrue(foundPath.compareCost(expectedPath));
    }

    @Test
    void testInvertPath4() {
        TerrainPosition v1 = new TerrainPosition(50, 50);
        TerrainPosition v2 = new TerrainPosition(51, 50);
        TerrainPosition v3 = new TerrainPosition(52, 50);
        TerrainPosition v4 = new TerrainPosition(53, 50);
        TerrainPosition v5 = new TerrainPosition(54, 50);
        TerrainPosition v6 = new TerrainPosition(55, 50);
        TerrainPosition v7 = new TerrainPosition(56, 50);

        Path path = new Path();

        NodeConnection nodeConnection = new NodeConnection(new NodeRoad(v1));
        nodeConnection.setEnd(new NodeRoad(v4));
        nodeConnection.addRoad(new NormalRoad(v2));
        nodeConnection.addRoad(new NormalRoad(v3));
        NodeConnection nodeConnection2 = new NodeConnection(new NodeRoad(v4));
        nodeConnection2.setEnd(new NodeRoad(v7));
        nodeConnection2.addRoad(new NormalRoad(v5));
        nodeConnection2.addRoad(new NormalRoad(v6));
        path.add(nodeConnection);
        path.add(nodeConnection2);

        Path foundPath = path.invertPath();

        Path expectedPath = new Path();

        NodeConnection expectedNodeConnection = new NodeConnection(new NodeRoad(v4));
        expectedNodeConnection.setEnd(new NodeRoad(v1));
        expectedNodeConnection.addRoad(new NormalRoad(v3));
        expectedNodeConnection.addRoad(new NormalRoad(v2));
        NodeConnection expectedNodeConnection2 = new NodeConnection(new NodeRoad(v7));
        expectedNodeConnection2.setEnd(new NodeRoad(v4));
        expectedNodeConnection2.addRoad(new NormalRoad(v6));
        expectedNodeConnection2.addRoad(new NormalRoad(v5));
        expectedPath.add(expectedNodeConnection2);
        expectedPath.add(expectedNodeConnection);

        assertTrue(foundPath.comparePaths(expectedPath));
        assertTrue(foundPath.compareCost(expectedPath));
    }

    @Test
    void testInvertPath5() {
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

        Path path = new Path();

        NodeConnection nodeConnection = new NodeConnection(new NodeRoad(v1));
        nodeConnection.setEnd(new NodeRoad(v4));
        nodeConnection.addRoad(new NormalRoad(v2));
        nodeConnection.addRoad(new NormalRoad(v3));
        NodeConnection nodeConnection2 = new NodeConnection(new NodeRoad(v4));
        nodeConnection2.setEnd(new NodeRoad(v7));
        nodeConnection2.addRoad(new NormalRoad(v5));
        nodeConnection2.addRoad(new NormalRoad(v6));
        NodeConnection nodeConnection3 = new NodeConnection(new NodeRoad(v7));
        nodeConnection3.setEnd(new NodeRoad(v10));
        nodeConnection3.addRoad(new NormalRoad(v8));
        nodeConnection3.addRoad(new NormalRoad(v9));
        path.add(nodeConnection);
        path.add(nodeConnection2);
        path.add(nodeConnection3);

        Path foundPath = path.invertPath();

        Path expectedPath = new Path();

        NodeConnection expectedNodeConnection = new NodeConnection(new NodeRoad(v4));
        expectedNodeConnection.setEnd(new NodeRoad(v1));
        expectedNodeConnection.addRoad(new NormalRoad(v3));
        expectedNodeConnection.addRoad(new NormalRoad(v2));
        NodeConnection expectedNodeConnection2 = new NodeConnection(new NodeRoad(v7));
        expectedNodeConnection2.setEnd(new NodeRoad(v4));
        expectedNodeConnection2.addRoad(new NormalRoad(v6));
        expectedNodeConnection2.addRoad(new NormalRoad(v5));
        NodeConnection expectedNodeConnection3 = new NodeConnection(new NodeRoad(v10));
        expectedNodeConnection3.setEnd(new NodeRoad(v7));
        expectedNodeConnection3.addRoad(new NormalRoad(v9));
        expectedNodeConnection3.addRoad(new NormalRoad(v8));
        expectedPath.add(expectedNodeConnection3);
        expectedPath.add(expectedNodeConnection2);
        expectedPath.add(expectedNodeConnection);

        assertTrue(foundPath.comparePaths(expectedPath));
        assertTrue(foundPath.compareCost(expectedPath));
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
        PathFinder pathFinder = new PathFinder(roadGraph);

        Path foundPath = pathFinder.findBestPath(v1, v4, 0);

        // Expected global route
        Path expectedPath = new Path();
        // Expected individual routes
        NodeConnection expectedNodeConnection = new NodeConnection(new NodeRoad(v1));
        expectedNodeConnection.setEnd(new NodeRoad(v4));
        expectedNodeConnection.addRoad(new NormalRoad(v2));
        expectedNodeConnection.addRoad(new NormalRoad(v3));
        expectedPath.add(expectedNodeConnection);

        assertTrue(foundPath.comparePaths(expectedPath));
        assertTrue(foundPath.compareCost(expectedPath));
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
        PathFinder pathFinder = new PathFinder(roadGraph);

        // Found route
        Path foundPath = pathFinder.findBestPath(v1, v5, 0);

        // Expected global route
        Path expectedPath = new Path();
        // Expected individual routes
        NodeConnection expectedNodeConnection = new NodeConnection(new NodeRoad(v1));
        expectedNodeConnection.setEnd(new NodeRoad(v3));
        expectedNodeConnection.addRoad(new NormalRoad(v2));
        NodeConnection expectedNodeConnection2 = new NodeConnection(new NodeRoad(v3));
        expectedNodeConnection2.setEnd(new NodeRoad(v5));
        expectedNodeConnection2.addRoad(new NormalRoad(v4));

        expectedPath.add(expectedNodeConnection);
        expectedPath.add(expectedNodeConnection2);

        assertTrue(foundPath.comparePaths(expectedPath));
        assertTrue(foundPath.compareCost(expectedPath));
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

        PathFinder pathFinder = new PathFinder(roadGraph);

        // Found route
        Path foundPath = pathFinder.findBestPath(v1, v5, 0);

        // Expected global route
        Path expectedPath = new Path();
        // Expected individual routes
        NodeConnection expectedNodeConnection = new NodeConnection(new NodeRoad(v1));
        expectedNodeConnection.setEnd(new NodeRoad(v5));
        expectedNodeConnection.addRoad(new NormalRoad(v2));
        expectedNodeConnection.addRoad(new NormalRoad(v3));
        expectedNodeConnection.addRoad(new NormalRoad(v4));
        expectedPath.add(expectedNodeConnection);

        assertTrue(foundPath.comparePaths(expectedPath));
        assertTrue(foundPath.compareCost(expectedPath));
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

        PathFinder pathFinder = new PathFinder(roadGraph);

        // Found route
        Path foundPath = pathFinder.findBestPath(v1, v9, 0);

        // Expected global route
        Path expectedPath = new Path();
        // Expected individual routes
        NodeConnection expectedNodeConnection = new NodeConnection(new NodeRoad(v1));
        expectedNodeConnection.setEnd(new NodeRoad(v4));
        expectedNodeConnection.addRoad(new NormalRoad(v2));
        expectedNodeConnection.addRoad(new NormalRoad(v3));
        NodeConnection expectedNodeConnection2 = new NodeConnection(new NodeRoad(v4));
        expectedNodeConnection2.setEnd(new NodeRoad(v7));
        expectedNodeConnection2.addRoad(new NormalRoad(v5));
        expectedNodeConnection2.addRoad(new NormalRoad(v6));
        NodeConnection expectedNodeConnection3 = new NodeConnection(new NodeRoad(v7));
        expectedNodeConnection3.setEnd(new NodeRoad(v9));
        expectedNodeConnection3.addRoad(new NormalRoad(v8));
        expectedPath.add(expectedNodeConnection);
        expectedPath.add(expectedNodeConnection2);
        expectedPath.add(expectedNodeConnection3);


        assertTrue(foundPath.comparePaths(expectedPath));
        assertTrue(foundPath.compareCost(expectedPath));
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
        PathFinder pathFinder = new PathFinder(roadGraph);
        // Found route
        Path foundPath = pathFinder.findBestPath(v1, v12, 0);

        // Expected global routes
        Path expectedPath = new Path();
        Path expectedPath2 = new Path();

        // Expected individual routes
        NodeConnection expectedNodeConnection = new NodeConnection(new NodeRoad(v1));
        expectedNodeConnection.setEnd(new NodeRoad(v4));
        expectedNodeConnection.addRoad(new NormalRoad(v2));
        expectedNodeConnection.addRoad(new NormalRoad(v3));
        NodeConnection expectedNodeConnection2 = new NodeConnection(new NodeRoad(v4));
        expectedNodeConnection2.setEnd(new NodeRoad(v9));
        expectedNodeConnection2.addRoad(new NormalRoad(v10));
        NodeConnection expectedNodeConnection3 = new NodeConnection(new NodeRoad(v9));
        expectedNodeConnection3.setEnd(new NodeRoad(v12));
        expectedNodeConnection3.addRoad(new NormalRoad(v11));
        expectedPath.add(expectedNodeConnection);
        expectedPath.add(expectedNodeConnection2);
        expectedPath.add(expectedNodeConnection3);

        expectedNodeConnection = new NodeConnection(new NodeRoad(v1));
        expectedNodeConnection.setEnd(new NodeRoad(v6));
        expectedNodeConnection.addRoad(new NormalRoad(v5));
        expectedNodeConnection2 = new NodeConnection(new NodeRoad(v6));
        expectedNodeConnection2.setEnd(new NodeRoad(v9));
        expectedNodeConnection2.addRoad(new NormalRoad(v7));
        expectedNodeConnection2.addRoad(new NormalRoad(v8));
        expectedPath2.add(expectedNodeConnection);
        expectedPath2.add(expectedNodeConnection2);
        expectedPath2.add(expectedNodeConnection3);

        assertTrue(foundPath.comparePaths(expectedPath) || foundPath.comparePaths(expectedPath2));
        assertTrue(foundPath.compareCost(expectedPath));
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
        PathFinder pathFinder = new PathFinder(roadGraph);
        // Found route
        Path foundPath = pathFinder.findBestPath(v1, v13, 0);

        // Expected global routes
        Path expectedPath = new Path();
        Path expectedPath2 = new Path();

        // Expected individual routes
        NodeConnection expectedNodeConnection = new NodeConnection(new NodeRoad(v1));
        expectedNodeConnection.setEnd(new NodeRoad(v4));
        expectedNodeConnection.addRoad(new NormalRoad(v2));
        expectedNodeConnection.addRoad(new NormalRoad(v3));
        NodeConnection expectedNodeConnection2 = new NodeConnection(new NodeRoad(v4));
        expectedNodeConnection2.setEnd(new NodeRoad(v9));
        expectedNodeConnection2.addRoad(new NormalRoad(v10));
        NodeConnection expectedNodeConnection3 = new NodeConnection(new NodeRoad(v9));
        expectedNodeConnection3.setEnd(new NodeRoad(v13));
        expectedNodeConnection3.addRoad(new NormalRoad(v11));
        expectedNodeConnection3.addRoad(new NormalRoad(v12));
        expectedPath.add(expectedNodeConnection);
        expectedPath.add(expectedNodeConnection2);
        expectedPath.add(expectedNodeConnection3);

        expectedNodeConnection = new NodeConnection(new NodeRoad(v1));
        expectedNodeConnection.setEnd(new NodeRoad(v6));
        expectedNodeConnection.addRoad(new NormalRoad(v5));
        expectedNodeConnection2 = new NodeConnection(new NodeRoad(v6));
        expectedNodeConnection2.setEnd(new NodeRoad(v9));
        expectedNodeConnection2.addRoad(new NormalRoad(v7));
        expectedNodeConnection2.addRoad(new NormalRoad(v8));
        expectedPath2.add(expectedNodeConnection);
        expectedPath2.add(expectedNodeConnection2);
        expectedPath2.add(expectedNodeConnection3);

        assertTrue(foundPath.comparePaths(expectedPath) || foundPath.comparePaths(expectedPath2));
        assertTrue(foundPath.compareCost(expectedPath));
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
        PathFinder pathFinder = new PathFinder(roadGraph);
        // Found route
        Path foundPath = pathFinder.findBestPath(v1, v5, 0);

        // Expected global routes
        Path expectedPath = new Path();

        // Expected individual routes
        NodeConnection expectedNodeConnection = new NodeConnection(new NormalRoad(v1));
        expectedNodeConnection.setEnd(new NodeRoad(v2));
        NodeConnection expectedNodeConnection2 = new NodeConnection(new NodeRoad(v2));
        expectedNodeConnection2.setEnd(new NodeRoad(v5));
        expectedNodeConnection2.addRoad(new NormalRoad(v3));
        expectedNodeConnection2.addRoad(new NormalRoad(v4));
        expectedPath.add(expectedNodeConnection);
        expectedPath.add(expectedNodeConnection2);

        assertTrue(foundPath.comparePaths(expectedPath));
        assertTrue(foundPath.compareCost(expectedPath));
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
        PathFinder pathFinder = new PathFinder(roadGraph);

        // Found route
        Path foundPath = pathFinder.findBestPath(v1, v6, 0);
        pathFinder.reset();

        Path foundPath2 = pathFinder.findBestPath(v6, v1, 0);

        // Expected global routes
        Path expectedPath = new Path();

        // Expected individual routes
        NodeConnection expectedNodeConnection = new NodeConnection(new NormalRoad(v1));
        expectedNodeConnection.setEnd(new NodeRoad(v4));
        expectedNodeConnection.addRoad(new NormalRoad(v2));
        expectedNodeConnection.addRoad(new NormalRoad(v3));
        NodeConnection expectedNodeConnection2 = new NodeConnection(new NodeRoad(v4));
        expectedNodeConnection2.setEnd(new NodeRoad(v6));
        expectedNodeConnection2.addRoad(new NormalRoad(v5));
        expectedPath.add(expectedNodeConnection);
        expectedPath.add(expectedNodeConnection2);

        Path expectedPath2 = expectedPath.invertPath();

        assertTrue(foundPath.comparePaths(expectedPath));
        assertTrue(foundPath.compareCost(expectedPath));
        assertTrue(foundPath2.comparePaths(expectedPath2));
        assertTrue(foundPath2.compareCost(expectedPath2));
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
        PathFinder pathFinder = new PathFinder(roadGraph);
        // Found route
        Path foundPath = pathFinder.findBestPath(v1, v5, 0);

        // Expected global routes
        Path expectedPath = new Path();

        // Expected individual routes
        NodeConnection expectedNodeConnection = new NodeConnection(new NormalRoad(v1));
        expectedNodeConnection.setEnd(new NormalRoad(v5));
        expectedNodeConnection.addRoad(new NormalRoad(v2));
        expectedNodeConnection.addRoad(new NormalRoad(v3));
        expectedNodeConnection.addRoad(new NormalRoad(v4));
        expectedPath.add(expectedNodeConnection);

        assertTrue(foundPath.comparePaths(expectedPath));
        assertTrue(foundPath.compareCost(expectedPath));
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

        PathFinder pathFinder = new PathFinder(scene.getRoadGraph());
        // Expected global routes
        Path expectedPath1 = new Path();
        Path expectedPath2;

        // Expected individual routes
        NodeConnection expectedNodeConnection = new NodeConnection(new NormalRoad(v2));
        expectedNodeConnection.setEnd(new NodeRoad(v4));
        expectedNodeConnection.addRoad(new NormalRoad(v3));
        expectedPath1.add(expectedNodeConnection);

        expectedPath2 = expectedPath1.invertPath();

        Path actualPath1 = pathFinder.findBestPath(v2, v4, 0);
        assertTrue(actualPath1.comparePaths(expectedPath1));
        assertTrue(actualPath1.compareCost(expectedPath1));

        pathFinder.reset();

        Path actualPath2 = pathFinder.findBestPath(v4, v2, 0);
        assertTrue(actualPath2.comparePaths(expectedPath2));
        assertTrue(actualPath2.compareCost(expectedPath2));
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
        PathFinder pathFinder = new PathFinder(roadGraph);
        // Found route
        Path foundPath = pathFinder.findBestPath(v3, v5, 0);

        // Expected global routes
        Path expectedPath = new Path();

        // Expected individual routes
        NodeConnection expectedNodeConnection = new NodeConnection(new NormalRoad(v3));
        expectedNodeConnection.setEnd(new NormalRoad(v5));
        expectedNodeConnection.addRoad(new NormalRoad(v4));
        expectedPath.add(expectedNodeConnection);


        assertTrue(foundPath.comparePaths(expectedPath));
        assertTrue(foundPath.compareCost(expectedPath));
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
        PathFinder pathFinder = new PathFinder(roadGraph);
        // Found route
        Path foundPath1 = pathFinder.findBestPath(v2, v6, 0);
        pathFinder.reset();

        Path foundPath2 = pathFinder.findBestPath(v6, v2, 0);

        // Expected global routes
        Path expectedPath1 = new Path();
        Path expectedPath2 = new Path();

        // Expected individual routes
        NodeConnection expectedNodeConnection1 = new NodeConnection(new NormalRoad(v2));
        expectedNodeConnection1.setEnd(new NodeRoad(v4));
        expectedNodeConnection1.addRoad(new NormalRoad(v3));
        NodeConnection expectedNodeConnection2 = new NodeConnection(new NodeRoad(v4));
        expectedNodeConnection2.setEnd(new NormalRoad(v6));
        expectedNodeConnection2.addRoad(new NormalRoad(v5));
        expectedPath1.add(expectedNodeConnection1);
        expectedPath1.add(expectedNodeConnection2);

        NodeConnection expectedNodeConnection3 = new NodeConnection(new NormalRoad(v6));
        expectedNodeConnection3.setEnd(new NodeRoad(v4));
        expectedNodeConnection3.addRoad(new NormalRoad(v5));
        NodeConnection expectedNodeConnection4 = new NodeConnection(new NodeRoad(v4));
        expectedNodeConnection4.setEnd(new NormalRoad(v2));
        expectedNodeConnection4.addRoad(new NormalRoad(v3));
        expectedPath2.add(expectedNodeConnection3);
        expectedPath2.add(expectedNodeConnection4);


        assertTrue(foundPath1.comparePaths(expectedPath1));
        assertTrue(foundPath1.compareCost(expectedPath1));
        assertTrue(foundPath2.comparePaths(expectedPath2));
        assertTrue(foundPath2.compareCost(expectedPath2));
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
        PathFinder pathFinder = new PathFinder(roadGraph);
        // Found route
        Path foundPath = pathFinder.findBestPath(v1, v4, 0);

        // Expected global routes
        Path expectedPath = new Path();

        assertTrue(foundPath.comparePaths(expectedPath));
        assertTrue(foundPath.compareCost(expectedPath));
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
        PathFinder pathFinder = new PathFinder(roadGraph);
        // Found route
        Path foundPath1 = pathFinder.findBestPath(v1, v5, 0);
        pathFinder.reset();

        Path foundPath2 = pathFinder.findBestPath(v5, v1, 0);

        // Expected global routes
        Path expectedPath = new Path();

        assertTrue(foundPath1.comparePaths(expectedPath));
        assertTrue(foundPath1.compareCost(expectedPath));

        assertTrue(foundPath2.comparePaths(expectedPath));
        assertTrue(foundPath2.compareCost(expectedPath));
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
        PathFinder pathFinder = new PathFinder(roadGraph);
        // Found route
        Path foundPath = pathFinder.findBestPath(v1, v6, 0);

        // Expected global routes
        Path expectedPath = new Path();

        assertTrue(foundPath.comparePaths(expectedPath));
        assertTrue(foundPath.compareCost(expectedPath));
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
        PathFinder pathFinder = new PathFinder(roadGraph);
        // Found route
        Path foundPath = pathFinder.findBestPath(v2, v3, 0);

        // Expected global routes
        Path expectedPath = new Path();

        // Expected individual routes
        NodeConnection expectedNodeConnection = new NodeConnection(new NormalRoad(v2));
        expectedNodeConnection.setEnd(new NormalRoad(v3));
        expectedPath.add(expectedNodeConnection);

        assertTrue(foundPath.comparePaths(expectedPath));
        assertTrue(foundPath.compareCost(expectedPath));
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
        PathFinder pathFinder = new PathFinder(roadGraph);

        // Found route
        Path foundPath1 = pathFinder.findBestPath(v2, v3, 0);
        pathFinder.reset();

        Path foundPath2 = pathFinder.findBestPath(v3, v2, 0);

        // Expected global routes
        Path expectedPath1 = new Path();
        Path expectedPath2;

        // Expected individual routes
        NodeConnection expectedNodeConnection1 = new NodeConnection(new NodeRoad(v2));
        expectedNodeConnection1.setEnd(new NormalRoad(v3));
        expectedPath1.add(expectedNodeConnection1);

        expectedPath2 = expectedPath1.invertPath();

        assertTrue(foundPath1.comparePaths(expectedPath1));
        assertTrue(foundPath1.compareCost(expectedPath1));

        assertTrue(foundPath2.comparePaths(expectedPath2));
        assertTrue(foundPath2.compareCost(expectedPath2));
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
        PathFinder pathFinder = new PathFinder(roadGraph);

        // Found route
        Path foundPath = pathFinder.findBestPath(v2, v3, 0);

        // Expected global routes
        Path expectedPath = new Path();

        // Expected individual routes
        NodeConnection expectedNodeConnection = new NodeConnection(new NodeRoad(v2));
        expectedNodeConnection.setEnd(new NodeRoad(v3));
        expectedPath.add(expectedNodeConnection);

        assertTrue(foundPath.comparePaths(expectedPath));
        assertTrue(foundPath.compareCost(expectedPath));
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
        PathFinder pathFinder = new PathFinder(roadGraph);

        // Found route
        Path foundPath = pathFinder.findBestPath(v2, v3, 0);

        // Expected global routes
        Path expectedPath = new Path();

        // Expected individual routes
        NodeConnection expectedNodeConnection = new NodeConnection(new NodeRoad(v2));
        expectedNodeConnection.setEnd(new NodeRoad(v3));
        expectedPath.add(expectedNodeConnection);

        assertTrue(foundPath.comparePaths(expectedPath));
        assertTrue(foundPath.compareCost(expectedPath));
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
        PathFinder pathFinder = new PathFinder(roadGraph);

        // Found route
        Path foundPath = pathFinder.findBestPath(v2, v3, 0);

        // Expected global routes
        Path expectedPath = new Path();

        // Expected individual routes
        NodeConnection expectedNodeConnection = new NodeConnection(new NodeRoad(v2));
        expectedNodeConnection.setEnd(new NormalRoad(v3));
        expectedPath.add(expectedNodeConnection);

        assertTrue(foundPath.comparePaths(expectedPath));
        assertTrue(foundPath.compareCost(expectedPath));
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
        PathFinder pathFinder = new PathFinder(roadGraph);

        // Found route
        Path foundPath = pathFinder.findBestPath(v1, v3, 0);

        // Expected global routes
        Path expectedPath = new Path();

        // Expected individual routes
        NodeConnection expectedNodeConnection = new NodeConnection(new NormalRoad(v1));
        expectedNodeConnection.setEnd(new NormalRoad(v3));
        expectedPath.add(expectedNodeConnection);

        assertTrue(foundPath.comparePaths(expectedPath));
        assertTrue(foundPath.compareCost(expectedPath));
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
        PathFinder pathFinder = new PathFinder(roadGraph);

        // Found route
        Path foundPath = pathFinder.findBestPath(v1, v3, 0);

        // Expected global routes
        Path expectedPath = new Path();

        // Expected individual routes
        NodeConnection expectedNodeConnection = new NodeConnection(new NormalRoad(v1));
        expectedNodeConnection.setEnd(new NormalRoad(v3));
        expectedNodeConnection.addRoad(new NormalRoad(v2));
        expectedPath.add(expectedNodeConnection);

        assertTrue(foundPath.comparePaths(expectedPath));
        assertTrue(foundPath.compareCost(expectedPath));
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
        PathFinder pathFinder = new PathFinder(roadGraph);

        // Found route
        Path foundPath = pathFinder.findBestPath(v1, v3, 0);

        // Expected global routes
        Path expectedPath = new Path();

        // Expected individual routes
        NodeConnection expectedNodeConnection = new NodeConnection(new NormalRoad(v1));
        expectedNodeConnection.setEnd(new NormalRoad(v3));
        expectedNodeConnection.addRoad(new NormalRoad(v2));
        expectedPath.add(expectedNodeConnection);

        assertTrue(foundPath.comparePaths(expectedPath));
        assertTrue(foundPath.compareCost(expectedPath));
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
        PathFinder pathFinder = new PathFinder(roadGraph);

        // Found route
        Path foundPath = pathFinder.findBestPath(v1, v5, 0);

        // Expected global routes
        Path expectedPath = new Path();

        // Expected individual routes
        NodeConnection expectedNodeConnection = new NodeConnection(new NormalRoad(v1));
        expectedNodeConnection.setEnd(new NormalRoad(v5));
        expectedNodeConnection.addRoad(new NormalRoad(v2));
        expectedNodeConnection.addRoad(new NormalRoad(v3));
        expectedNodeConnection.addRoad(new NormalRoad(v4));
        expectedPath.add(expectedNodeConnection);

        assertTrue(foundPath.comparePaths(expectedPath));
        assertTrue(foundPath.compareCost(expectedPath));
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
        PathFinder pathFinder = new PathFinder(roadGraph);

        // Found route
        Path foundPath1 = pathFinder.findBestPath(v1, v5, 0);
        pathFinder.reset();

        Path foundPath2 = pathFinder.findBestPath(v6, v10, 0);

        // Expected global routes
        Path expectedPath1 = new Path();
        Path expectedPath2 = new Path();

        // Expected individual routes

        // v1
        NodeConnection expectedNodeConnection1 = new NodeConnection(new NormalRoad(v1));
        expectedNodeConnection1.setEnd(new NodeRoad(v3));
        expectedNodeConnection1.addRoad(new NormalRoad(v2));
        NodeConnection expectedNodeConnection2 = new NodeConnection(new NodeRoad(v3));
        expectedNodeConnection2.setEnd(new NormalRoad(v5));
        expectedNodeConnection2.addRoad(new NormalRoad(v4));

        expectedPath1.add(expectedNodeConnection1);
        expectedPath1.add(expectedNodeConnection2);

        // v2
        NodeConnection expectedNodeConnection3 = new NodeConnection(new NormalRoad(v6));
        expectedNodeConnection3.setEnd(new NodeRoad(v8));
        expectedNodeConnection3.addRoad(new NormalRoad(v7));
        NodeConnection expectedNodeConnection4 = new NodeConnection(new NodeRoad(v8));
        expectedNodeConnection4.setEnd(new NormalRoad(v10));
        expectedNodeConnection4.addRoad(new NormalRoad(v9));

        expectedPath2.add(expectedNodeConnection3);
        expectedPath2.add(expectedNodeConnection4);

        assertTrue(foundPath1.comparePaths(expectedPath1));
        assertTrue(foundPath1.compareCost(expectedPath1));

        assertTrue(foundPath2.comparePaths(expectedPath2));
        assertTrue(foundPath2.compareCost(expectedPath2));
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
        PathFinder pathFinder = new PathFinder(roadGraph);

        // Found route
        Path foundPath1 = pathFinder.findBestPath(v1, v3, 0);
        pathFinder.reset();

        Path foundPath2 = pathFinder.findBestPath(v4, v6, 0);

        // Expected global routes
        Path expectedPath1 = new Path();
        Path expectedPath2 = new Path();

        // Expected individual routes

        // v1
        NodeConnection expectedNodeConnection1 = new NodeConnection(new NormalRoad(v1));
        expectedNodeConnection1.setEnd(new NormalRoad(v3));
        expectedNodeConnection1.addRoad(new NormalRoad(v2));

        expectedPath1.add(expectedNodeConnection1);

        // v2
        NodeConnection expectedNodeConnection2 = new NodeConnection(new NormalRoad(v4));
        expectedNodeConnection2.setEnd(new NormalRoad(v6));
        expectedNodeConnection2.addRoad(new NormalRoad(v5));

        expectedPath2.add(expectedNodeConnection2);

        assertTrue(foundPath1.comparePaths(expectedPath1));
        assertTrue(foundPath1.compareCost(expectedPath1));

        assertTrue(foundPath2.comparePaths(expectedPath2));
        assertTrue(foundPath2.compareCost(expectedPath2));
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
        PathFinder pathFinder = new PathFinder(roadGraph);

        // Found route
        Path foundPath1 = pathFinder.findBestPath(v1, v3, 0);
        pathFinder.reset();

        Path foundPath2 = pathFinder.findBestPath(v3, v1, 0);
        pathFinder.reset();

        Path foundPath3 = pathFinder.findBestPath(v4, v6, 0);
        pathFinder.reset();

        Path foundPath4 = pathFinder.findBestPath(v6, v4, 0);

        // Expected global routes
        Path expectedPath1 = new Path();
        Path expectedPath2 = new Path();
        Path expectedPath3 = new Path();
        Path expectedPath4 = new Path();

        // Expected individual routes

        // v1
        NodeConnection expectedNodeConnection1 = new NodeConnection(new NodeRoad(v1));
        expectedNodeConnection1.setEnd(new NormalRoad(v3));
        expectedNodeConnection1.addRoad(new NormalRoad(v2));

        expectedPath1.add(expectedNodeConnection1);

        NodeConnection expectedNodeConnection2 = new NodeConnection(new NormalRoad(v3));
        expectedNodeConnection2.setEnd(new NodeRoad(v1));
        expectedNodeConnection2.addRoad(new NormalRoad(v2));

        expectedPath2.add(expectedNodeConnection2);

        // v2
        NodeConnection expectedNodeConnection3 = new NodeConnection(new NodeRoad(v4));
        expectedNodeConnection3.setEnd(new NormalRoad(v6));
        expectedNodeConnection3.addRoad(new NormalRoad(v5));

        expectedPath3.add(expectedNodeConnection3);

        NodeConnection expectedNodeConnection4 = new NodeConnection(new NormalRoad(v6));
        expectedNodeConnection4.setEnd(new NodeRoad(v4));
        expectedNodeConnection4.addRoad(new NormalRoad(v5));

        expectedPath4.add(expectedNodeConnection4);

        assertTrue(foundPath1.comparePaths(expectedPath1));
        assertTrue(foundPath1.compareCost(expectedPath1));

        assertTrue(foundPath2.comparePaths(expectedPath2));
        assertTrue(foundPath2.compareCost(expectedPath2));

        assertTrue(foundPath3.comparePaths(expectedPath3));
        assertTrue(foundPath3.compareCost(expectedPath3));

        assertTrue(foundPath4.comparePaths(expectedPath4));
        assertTrue(foundPath4.compareCost(expectedPath4));
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
        PathFinder pathFinder = new PathFinder(roadGraph);

        // Found route
        Path foundPath1 = pathFinder.findBestPath(v1, v3, 0);
        pathFinder.reset();

        Path foundPath2 = pathFinder.findBestPath(v4, v6, 0);


        // Expected global routes
        Path expectedPath1 = new Path();
        Path expectedPath2 = new Path();

        // Expected individual routes

        // v1
        NodeConnection expectedNodeConnection1 = new NodeConnection(new NodeRoad(v1));
        expectedNodeConnection1.setEnd(new NodeRoad(v3));
        expectedNodeConnection1.addRoad(new NormalRoad(v2));

        expectedPath1.add(expectedNodeConnection1);

        // v2
        NodeConnection expectedNodeConnection2 = new NodeConnection(new NodeRoad(v4));
        expectedNodeConnection2.setEnd(new NodeRoad(v6));
        expectedNodeConnection2.addRoad(new NormalRoad(v5));

        expectedPath2.add(expectedNodeConnection2);

        assertTrue(foundPath1.comparePaths(expectedPath1));
        assertTrue(foundPath1.compareCost(expectedPath1));

        assertTrue(foundPath2.comparePaths(expectedPath2));
        assertTrue(foundPath2.compareCost(expectedPath2));
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
        PathFinder pathFinder = new PathFinder(roadGraph);

        // Found route
        Path foundPath1 = pathFinder.findBestPath(v1, v7, 0);
        pathFinder.reset();

        Path foundPath2 = pathFinder.findBestPath(v7, v1, 0);


        // Expected global routes
        Path expectedPath1 = new Path();
        Path expectedPath2 = new Path();

        // Expected individual routes

        // v1
        NodeConnection expectedNodeConnection1 = new NodeConnection(new NormalRoad(v1));
        expectedNodeConnection1.setEnd(new NodeRoad(v3));
        expectedNodeConnection1.addRoad(new NormalRoad(v2));
        NodeConnection expectedNodeConnection2 = new NodeConnection(new NodeRoad(v3));
        expectedNodeConnection2.setEnd(new NodeRoad(v4));
        NodeConnection expectedNodeConnection3 = new NodeConnection(new NodeRoad(v4));
        expectedNodeConnection3.setEnd(new NodeRoad(v5));
        NodeConnection expectedNodeConnection4 = new NodeConnection(new NodeRoad(v5));
        expectedNodeConnection4.setEnd(new NormalRoad(v7));
        expectedNodeConnection4.addRoad(new NormalRoad(v6));

        expectedPath1.add(expectedNodeConnection1);
        expectedPath1.add(expectedNodeConnection2);
        expectedPath1.add(expectedNodeConnection3);
        expectedPath1.add(expectedNodeConnection4);

        // v2
        NodeConnection expectedNodeConnection5 = new NodeConnection(new NormalRoad(v7));
        expectedNodeConnection5.setEnd(new NodeRoad(v5));
        expectedNodeConnection5.addRoad(new NormalRoad(v6));
        NodeConnection expectedNodeConnection6 = new NodeConnection(new NodeRoad(v5));
        expectedNodeConnection6.setEnd(new NodeRoad(v4));
        NodeConnection expectedNodeConnection7 = new NodeConnection(new NodeRoad(v4));
        expectedNodeConnection7.setEnd(new NodeRoad(v3));
        NodeConnection expectedNodeConnection8 = new NodeConnection(new NodeRoad(v3));
        expectedNodeConnection8.setEnd(new NormalRoad(v1));
        expectedNodeConnection8.addRoad(new NormalRoad(v2));

        expectedPath2.add(expectedNodeConnection5);
        expectedPath2.add(expectedNodeConnection6);
        expectedPath2.add(expectedNodeConnection7);
        expectedPath2.add(expectedNodeConnection8);

        assertTrue(foundPath1.comparePaths(expectedPath1));
        assertTrue(foundPath1.compareCost(expectedPath1));

        assertTrue(foundPath2.comparePaths(expectedPath2));
        assertTrue(foundPath2.compareCost(expectedPath2));
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
        PathFinder pathFinder = new PathFinder(roadGraph);

        // Found route
        Path foundPath1 = pathFinder.findBestPath(v1, v7, 0);
        pathFinder.reset();

        Path foundPath2 = pathFinder.findBestPath(v7, v1, 0);


        // Expected global routes
        Path expectedPath1V1 = new Path();
        Path expectedPath2V1 = new Path();
        Path expectedPath1V2 = new Path();
        Path expectedPath2V2 = new Path();

        // Expected individual routes

        // v1
        NodeConnection expectedNodeConnection1V1 = new NodeConnection(new NormalRoad(v1));
        expectedNodeConnection1V1.setEnd(new NodeRoad(v5));
        expectedNodeConnection1V1.addRoad(new NormalRoad(v2));
        expectedNodeConnection1V1.addRoad(new NormalRoad(v3));
        expectedNodeConnection1V1.addRoad(new NormalRoad(v4));
        NodeConnection expectedNodeConnection2V1 = new NodeConnection(new NodeRoad(v5));
        expectedNodeConnection2V1.setEnd(new NormalRoad(v7));
        expectedNodeConnection2V1.addRoad(new NormalRoad(v6));

        expectedPath1V1.add(expectedNodeConnection1V1);
        expectedPath1V1.add(expectedNodeConnection2V1);

        NodeConnection expectedNodeConnection3V1 = new NodeConnection(new NormalRoad(v1));
        expectedNodeConnection3V1.setEnd(new NodeRoad(v5));
        expectedNodeConnection3V1.addRoad(new NormalRoad(v2));
        expectedNodeConnection3V1.addRoad(new NormalRoad(v3));
        expectedNodeConnection3V1.addRoad(new NormalRoad(v4));
        NodeConnection expectedNodeConnection4V1 = new NodeConnection(new NodeRoad(v5));
        expectedNodeConnection4V1.setEnd(new NormalRoad(v7));
        expectedNodeConnection4V1.addRoad(new NormalRoad(v6b));

        expectedPath2V1.add(expectedNodeConnection3V1);
        expectedPath2V1.add(expectedNodeConnection4V1);

        // v2
        NodeConnection expectedNodeConnection1V2 = new NodeConnection(new NormalRoad(v7));
        expectedNodeConnection1V2.setEnd(new NodeRoad(v5));
        expectedNodeConnection1V2.addRoad(new NormalRoad(v6));
        NodeConnection expectedNodeConnection2V2 = new NodeConnection(new NodeRoad(v5));
        expectedNodeConnection2V2.setEnd(new NormalRoad(v1));
        expectedNodeConnection2V2.addRoad(new NormalRoad(v4));
        expectedNodeConnection2V2.addRoad(new NormalRoad(v3));
        expectedNodeConnection2V2.addRoad(new NormalRoad(v2));

        expectedPath1V2.add(expectedNodeConnection1V2);
        expectedPath1V2.add(expectedNodeConnection2V2);

        NodeConnection expectedNodeConnection3V2 = new NodeConnection(new NormalRoad(v7));
        expectedNodeConnection3V2.setEnd(new NodeRoad(v5));
        expectedNodeConnection3V2.addRoad(new NormalRoad(v6b));
        NodeConnection expectedNodeConnection4V2 = new NodeConnection(new NodeRoad(v5));
        expectedNodeConnection4V2.setEnd(new NormalRoad(v1));
        expectedNodeConnection4V2.addRoad(new NormalRoad(v4));
        expectedNodeConnection4V2.addRoad(new NormalRoad(v3));
        expectedNodeConnection4V2.addRoad(new NormalRoad(v2));

        expectedPath2V2.add(expectedNodeConnection3V2);
        expectedPath2V2.add(expectedNodeConnection4V2);

        assertTrue(foundPath1.comparePaths(expectedPath1V1) || foundPath1.comparePaths(expectedPath2V1));
        assertTrue(foundPath1.compareCost(expectedPath1V1) || foundPath1.compareCost(expectedPath2V1));

        assertTrue(foundPath2.comparePaths(expectedPath1V2) || foundPath2.comparePaths(expectedPath2V2));
        assertTrue(foundPath2.compareCost(expectedPath1V2) || foundPath2.compareCost(expectedPath2V2));
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
        PathFinder pathFinder = new PathFinder(roadGraph);

        // Found route
        Path foundPath = pathFinder.findBestPath(v2, v2, 0);

        // Expected global routes
        Path expectedPath = new Path();

        // Expected individual routes
        NormalRoad start_and_end = new NormalRoad(v2);
        NodeConnection expectedNodeConnection1 = new NodeConnection(start_and_end, start_and_end);
        expectedNodeConnection1.addRoad(start_and_end);

        expectedPath.add(expectedNodeConnection1);

        assertTrue(foundPath.comparePaths(expectedPath));
        assertTrue(foundPath.compareCost(expectedPath));
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
        PathFinder pathFinder = new PathFinder(roadGraph);

        // Found route
        Path foundPath = pathFinder.findBestPath(v2, v2, 0);

        // Expected global routes
        Path expectedPath = new Path();

        // Expected individual routes
        NodeRoad start_and_end = new NodeRoad(v2);
        NodeConnection expectedNodeConnection1 = new NodeConnection(start_and_end, start_and_end);
        expectedNodeConnection1.addRoad(start_and_end);

        expectedPath.add(expectedNodeConnection1);

        assertTrue(foundPath.comparePaths(expectedPath));
        assertTrue(foundPath.compareCost(expectedPath));
    }

    /**
     * 32. Found in practice
     */
    @Test
    void testPathfinding32() {
        TerrainPosition v1 = new TerrainPosition(0, 0);
        TerrainPosition v2 = new TerrainPosition(0, 2);
        TerrainPosition v3 = new TerrainPosition(5, 0);
        TerrainPosition v4 = new TerrainPosition(5, 2);

        int[] roadPositions = new int[]{
                v1.getX(), v1.getZ(),
                v2.getX(), v2.getZ(),
                1, 0,
                2, 0,
                3, 0,
                4, 0,
                v3.getX(), v3.getZ(),
                6, 0,
                5, 1,
                v4.getX(), v4.getZ(),
                5, 3,
                4, 2,
                3, 2,
                2, 2,
                1, 2
        };

        TerrainPosition[] roads = TerrainPosition.toPositionArray(roadPositions);

        GameObject.newInstances(DirtRoad.class, roads);

        RoadGraph roadGraph = scene.getRoadGraph();
        PathFinder pathFinder = new PathFinder(roadGraph);

        // Found route
        Path foundPath = pathFinder.findBestPath(v1, v2, 0);

        // Expected global routes
        Path expectedPath = new Path();

        // Expected individual routes
        NodeConnection expectedNodeConnection1 = new NodeConnection(new NormalRoad(v1), new NodeRoad(v3));
        expectedNodeConnection1.addRoad(new NormalRoad(new TerrainPosition(1, 0)));
        expectedNodeConnection1.addRoad(new NormalRoad(new TerrainPosition(2, 0)));
        expectedNodeConnection1.addRoad(new NormalRoad(new TerrainPosition(3, 0)));
        expectedNodeConnection1.addRoad(new NormalRoad(new TerrainPosition(4, 0)));
        expectedNodeConnection1.addRoad(new NodeRoad(v3));
        NodeConnection expectedNodeConnection2 = new NodeConnection(new NodeRoad(v3), new NodeRoad(v4));
        expectedNodeConnection2.addRoad(new NormalRoad(new TerrainPosition(5, 1)));
        expectedNodeConnection2.addRoad(new NodeRoad(v4));
        NodeConnection expectedNodeConnection3 = new NodeConnection(new NodeRoad(v4), new NormalRoad(v2));
        expectedNodeConnection3.addRoad(new NormalRoad(new TerrainPosition(4, 2)));
        expectedNodeConnection3.addRoad(new NormalRoad(new TerrainPosition(3, 2)));
        expectedNodeConnection3.addRoad(new NormalRoad(new TerrainPosition(2, 2)));
        expectedNodeConnection3.addRoad(new NormalRoad(new TerrainPosition(1, 2)));
        expectedNodeConnection3.addRoad(new NormalRoad(v2));

        expectedPath.add(expectedNodeConnection1);
        expectedPath.add(expectedNodeConnection2);
        expectedPath.add(expectedNodeConnection3);

        assertTrue(foundPath.comparePaths(expectedPath));
        assertTrue(foundPath.compareCost(expectedPath));
    }

    /**
     * 33. Found in practice with NPC
     */
    @Test
    void testPathfinding33() {
        /*
        Placing at TerrainPosition{x=12, z=0}
Placing at TerrainPosition{x=13, z=0}
Placing at TerrainPosition{x=14, z=0}
Placing at TerrainPosition{x=15, z=0}
Placing at TerrainPosition{x=16, z=0}
Placing at TerrainPosition{x=17, z=0}
Placing at TerrainPosition{x=18, z=0}
Placing at TerrainPosition{x=19, z=0}
Placing at TerrainPosition{x=20, z=0}
Placing at TerrainPosition{x=21, z=0}
Placing at TerrainPosition{x=22, z=0}
Placing at TerrainPosition{x=23, z=0}
Placing at TerrainPosition{x=24, z=0}
Placing at TerrainPosition{x=25, z=0}
Placing at TerrainPosition{x=26, z=0}
Placing at TerrainPosition{x=27, z=0}
Placing at TerrainPosition{x=28, z=0}
Placing at TerrainPosition{x=29, z=0}
Placing at TerrainPosition{x=30, z=0}
Placing at TerrainPosition{x=31, z=0}
Placing at TerrainPosition{x=32, z=0}
Placing at TerrainPosition{x=33, z=0}
Placing at TerrainPosition{x=34, z=0}
Placing at TerrainPosition{x=35, z=0}
Placing at TerrainPosition{x=0, z=0}
Placing at TerrainPosition{x=1, z=0}
Placing at TerrainPosition{x=2, z=0}
Placing at TerrainPosition{x=3, z=0}
Placing at TerrainPosition{x=4, z=0}
Placing at TerrainPosition{x=5, z=0}
Placing at TerrainPosition{x=6, z=0}
Placing at TerrainPosition{x=7, z=0}
Placing at TerrainPosition{x=8, z=0}
Placing at TerrainPosition{x=9, z=0}
Placing at TerrainPosition{x=10, z=0}
Placing at TerrainPosition{x=11, z=0}
Placing at TerrainPosition{x=36, z=0}
Placing at TerrainPosition{x=36, z=1}
Placing at TerrainPosition{x=36, z=2}
Placing at TerrainPosition{x=36, z=3}
Placing at TerrainPosition{x=36, z=4}
Placing at TerrainPosition{x=36, z=5}
Placing at TerrainPosition{x=36, z=6}
Placing at TerrainPosition{x=36, z=7}
Placing at TerrainPosition{x=36, z=8}
Placing at TerrainPosition{x=36, z=9}
Placing at TerrainPosition{x=36, z=10}
Placing at TerrainPosition{x=36, z=11}
Placing at TerrainPosition{x=36, z=12}
Placing at TerrainPosition{x=36, z=13}
Placing at TerrainPosition{x=36, z=14}
Placing at TerrainPosition{x=36, z=15}
Placing at TerrainPosition{x=36, z=16}
Placing at TerrainPosition{x=37, z=10}
Placing at TerrainPosition{x=38, z=10}
Placing at TerrainPosition{x=39, z=10}
Placing at TerrainPosition{x=40, z=10}
Placing at TerrainPosition{x=41, z=10}
Placing at TerrainPosition{x=42, z=10}
Placing at TerrainPosition{x=43, z=10}
Placing at TerrainPosition{x=44, z=10}
Placing at TerrainPosition{x=45, z=10}
Placing at TerrainPosition{x=46, z=10}
Placing at TerrainPosition{x=47, z=10}
Placing at TerrainPosition{x=47, z=17}
Placing at TerrainPosition{x=47, z=18}
Placing at TerrainPosition{x=47, z=19}
Placing at TerrainPosition{x=47, z=20}
Placing at TerrainPosition{x=47, z=21}
Placing at TerrainPosition{x=47, z=22}
Placing at TerrainPosition{x=47, z=11}
Placing at TerrainPosition{x=47, z=12}
Placing at TerrainPosition{x=47, z=13}
Placing at TerrainPosition{x=47, z=14}
Placing at TerrainPosition{x=47, z=15}
Placing at TerrainPosition{x=47, z=16}
Placing at TerrainPosition{x=46, z=16}
Placing at TerrainPosition{x=45, z=16}
Placing at TerrainPosition{x=44, z=16}
Placing at TerrainPosition{x=43, z=16}
Placing at TerrainPosition{x=42, z=16}
Placing at TerrainPosition{x=41, z=16}
Placing at TerrainPosition{x=40, z=16}
Placing at TerrainPosition{x=39, z=16}
Placing at TerrainPosition{x=38, z=16}
Placing at TerrainPosition{x=37, z=16}
Placing at TerrainPosition{x=37, z=2}
Placing at TerrainPosition{x=38, z=2}
Placing at TerrainPosition{x=39, z=2}
Placing at TerrainPosition{x=40, z=2}
Placing at TerrainPosition{x=41, z=2}
Placing at TerrainPosition{x=42, z=2}
Placing at TerrainPosition{x=43, z=2}
Placing at TerrainPosition{x=44, z=2}
Placing at TerrainPosition{x=45, z=2}
Placing at TerrainPosition{x=46, z=2}
Placing at TerrainPosition{x=45, z=9}
Placing at TerrainPosition{x=45, z=8}
Placing at TerrainPosition{x=45, z=7}
Placing at TerrainPosition{x=45, z=6}
Placing at TerrainPosition{x=45, z=5}
Placing at TerrainPosition{x=45, z=4}
Placing at TerrainPosition{x=45, z=3}
Placing at TerrainPosition{x=43, z=6}
Placing at TerrainPosition{x=43, z=7}
Placing at TerrainPosition{x=43, z=8}
Placing at TerrainPosition{x=43, z=9}
Placing at TerrainPosition{x=43, z=3}
Placing at TerrainPosition{x=43, z=4}
Placing at TerrainPosition{x=43, z=5}
Placing at TerrainPosition{x=42, z=7}
Placing at TerrainPosition{x=41, z=7}
Placing at TerrainPosition{x=40, z=7}
Placing at TerrainPosition{x=39, z=7}
Placing at TerrainPosition{x=38, z=7}
Placing at TerrainPosition{x=37, z=7}
Placing at TerrainPosition{x=37, z=0}
Placing at TerrainPosition{x=38, z=0}
Placing at TerrainPosition{x=38, z=1}
Placing at TerrainPosition{x=37, z=1}
Placing at TerrainPosition{x=35, z=8}
Placing at TerrainPosition{x=34, z=8}
Placing at TerrainPosition{x=33, z=8}
Placing at TerrainPosition{x=33, z=7}
Placing at TerrainPosition{x=33, z=6}
Placing at TerrainPosition{x=33, z=5}
Placing at TerrainPosition{x=33, z=4}
Placing at TerrainPosition{x=33, z=3}
Placing at TerrainPosition{x=33, z=2}
Placing at TerrainPosition{x=33, z=1}
Placing at TerrainPosition{x=28, z=16}
Placing at TerrainPosition{x=27, z=16}
Placing at TerrainPosition{x=26, z=16}
Placing at TerrainPosition{x=25, z=16}
Placing at TerrainPosition{x=24, z=16}
Placing at TerrainPosition{x=23, z=16}
Placing at TerrainPosition{x=22, z=16}
Placing at TerrainPosition{x=21, z=16}
Placing at TerrainPosition{x=35, z=16}
Placing at TerrainPosition{x=34, z=16}
Placing at TerrainPosition{x=33, z=16}
Placing at TerrainPosition{x=32, z=16}
Placing at TerrainPosition{x=31, z=16}
Placing at TerrainPosition{x=30, z=16}
Placing at TerrainPosition{x=29, z=16}
Placing at TerrainPosition{x=22, z=2}
Placing at TerrainPosition{x=22, z=1}
Placing at TerrainPosition{x=22, z=15}
Placing at TerrainPosition{x=22, z=14}
Placing at TerrainPosition{x=22, z=13}
Placing at TerrainPosition{x=22, z=12}
Placing at TerrainPosition{x=22, z=11}
Placing at TerrainPosition{x=22, z=10}
Placing at TerrainPosition{x=22, z=9}
Placing at TerrainPosition{x=22, z=8}
Placing at TerrainPosition{x=22, z=7}
Placing at TerrainPosition{x=22, z=6}
Placing at TerrainPosition{x=22, z=5}
Placing at TerrainPosition{x=22, z=4}
Placing at TerrainPosition{x=22, z=3}
Placing at TerrainPosition{x=23, z=5}
Placing at TerrainPosition{x=24, z=5}
Placing at TerrainPosition{x=25, z=5}
Placing at TerrainPosition{x=26, z=5}
Placing at TerrainPosition{x=23, z=9}
Placing at TerrainPosition{x=23, z=13}
Placing at TerrainPosition{x=20, z=12}
Placing at TerrainPosition{x=16, z=1}
Placing at TerrainPosition{x=24, z=1}
Placing at TerrainPosition{x=30, z=1}
Placing at TerrainPosition{x=40, z=6}
Placing at TerrainPosition{x=43, z=14}
Placing at TerrainPosition{x=42, z=15}
Placing at TerrainPosition{x=42, z=14}
Placing at TerrainPosition{x=48, z=19}
Placing at TerrainPosition{x=49, z=19}
Placing at TerrainPosition{x=50, z=19}
Placing at TerrainPosition{x=51, z=19}
Placing at TerrainPosition{x=48, z=16}
Placing at TerrainPosition{x=52, z=16}
Placing at TerrainPosition{x=51, z=16}
Placing at TerrainPosition{x=50, z=16}
Placing at TerrainPosition{x=49, z=16}
Insula selected
Placing at TerrainPosition{x=52, z=23}
Market selected
Placing at TerrainPosition{x=11, z=5}
DirtRoad selected
Placing at TerrainPosition{x=21, z=17}
Placing at TerrainPosition{x=21, z=18}
Placing at TerrainPosition{x=21, z=19}
Placing at TerrainPosition{x=21, z=20}
Placing at TerrainPosition{x=21, z=21}
Placing at TerrainPosition{x=21, z=22}
Placing at TerrainPosition{x=21, z=23}
Placing at TerrainPosition{x=21, z=24}
Placing at TerrainPosition{x=21, z=25}
Placing at TerrainPosition{x=21, z=26}
Placing at TerrainPosition{x=21, z=27}
Placing at TerrainPosition{x=21, z=28}
Placing at TerrainPosition{x=21, z=29}
Placing at TerrainPosition{x=21, z=30}
Placing at TerrainPosition{x=21, z=31}
Placing at TerrainPosition{x=21, z=32}
Placing at TerrainPosition{x=21, z=33}
Placing at TerrainPosition{x=21, z=34}
Placing at TerrainPosition{x=20, z=34}
Placing at TerrainPosition{x=19, z=34}
Placing at TerrainPosition{x=18, z=34}
Placing at TerrainPosition{x=17, z=34}
Placing at TerrainPosition{x=16, z=34}
Placing at TerrainPosition{x=15, z=34}
Placing at TerrainPosition{x=14, z=34}
Placing at TerrainPosition{x=13, z=34}
Placing at TerrainPosition{x=12, z=34}
Placing at TerrainPosition{x=11, z=34}
Placing at TerrainPosition{x=10, z=34}
Placing at TerrainPosition{x=9, z=34}
Placing at TerrainPosition{x=8, z=34}
Placing at TerrainPosition{x=7, z=34}
Placing at TerrainPosition{x=7, z=33}
Placing at TerrainPosition{x=7, z=32}
Placing at TerrainPosition{x=7, z=31}
Placing at TerrainPosition{x=7, z=30}
Placing at TerrainPosition{x=7, z=29}
Placing at TerrainPosition{x=7, z=28}
Placing at TerrainPosition{x=7, z=27}
Placing at TerrainPosition{x=7, z=26}
Placing at TerrainPosition{x=7, z=25}
Placing at TerrainPosition{x=7, z=24}
Placing at TerrainPosition{x=7, z=23}
Placing at TerrainPosition{x=7, z=22}
Placing at TerrainPosition{x=7, z=21}
Placing at TerrainPosition{x=7, z=20}
Placing at TerrainPosition{x=7, z=19}
Placing at TerrainPosition{x=7, z=18}
Placing at TerrainPosition{x=7, z=17}
Placing at TerrainPosition{x=7, z=16}
Placing at TerrainPosition{x=7, z=15}
Placing at TerrainPosition{x=7, z=14}
Placing at TerrainPosition{x=7, z=13}
Placing at TerrainPosition{x=8, z=13}
Placing at TerrainPosition{x=9, z=13}
Placing at TerrainPosition{x=10, z=13}
Placing at TerrainPosition{x=10, z=14}
Placing at TerrainPosition{x=10, z=15}
Placing at TerrainPosition{x=10, z=16}
Placing at TerrainPosition{x=10, z=17}
Placing at TerrainPosition{x=10, z=18}
Placing at TerrainPosition{x=10, z=19}
Placing at TerrainPosition{x=10, z=20}
Placing at TerrainPosition{x=10, z=21}
Placing at TerrainPosition{x=10, z=22}
Placing at TerrainPosition{x=10, z=23}
Placing at TerrainPosition{x=10, z=24}
Placing at TerrainPosition{x=10, z=25}
Placing at TerrainPosition{x=10, z=26}
Placing at TerrainPosition{x=10, z=27}
Placing at TerrainPosition{x=10, z=28}
Placing at TerrainPosition{x=10, z=29}
Placing at TerrainPosition{x=10, z=30}
Placing at TerrainPosition{x=10, z=31}
Placing at TerrainPosition{x=10, z=32}
Placing at TerrainPosition{x=11, z=32}
Placing at TerrainPosition{x=12, z=32}
Placing at TerrainPosition{x=13, z=32}
Placing at TerrainPosition{x=13, z=18}
Placing at TerrainPosition{x=13, z=17}
Placing at TerrainPosition{x=13, z=16}
Placing at TerrainPosition{x=13, z=15}
Placing at TerrainPosition{x=13, z=14}
Placing at TerrainPosition{x=13, z=13}
Placing at TerrainPosition{x=13, z=12}
Placing at TerrainPosition{x=13, z=11}
Placing at TerrainPosition{x=13, z=10}
Placing at TerrainPosition{x=13, z=31}
Placing at TerrainPosition{x=13, z=30}
Placing at TerrainPosition{x=13, z=29}
Placing at TerrainPosition{x=13, z=28}
Placing at TerrainPosition{x=13, z=27}
Placing at TerrainPosition{x=13, z=26}
Placing at TerrainPosition{x=13, z=25}
Placing at TerrainPosition{x=13, z=24}
Placing at TerrainPosition{x=13, z=23}
Placing at TerrainPosition{x=13, z=22}
Placing at TerrainPosition{x=13, z=21}
Placing at TerrainPosition{x=13, z=20}
Placing at TerrainPosition{x=13, z=19}
Placing at TerrainPosition{x=12, z=10}
Placing at TerrainPosition{x=11, z=10}
Placing at TerrainPosition{x=10, z=10}
Placing at TerrainPosition{x=9, z=10}
Placing at TerrainPosition{x=8, z=10}
Placing at TerrainPosition{x=7, z=10}
Placing at TerrainPosition{x=6, z=10}
Placing at TerrainPosition{x=5, z=10}
Placing at TerrainPosition{x=4, z=10}
Placing at TerrainPosition{x=4, z=9}
Placing at TerrainPosition{x=4, z=8}
Placing at TerrainPosition{x=4, z=7}
Placing at TerrainPosition{x=4, z=6}
Placing at TerrainPosition{x=4, z=5}
Placing at TerrainPosition{x=4, z=4}
Placing at TerrainPosition{x=4, z=3}
Placing at TerrainPosition{x=4, z=2}
Placing at TerrainPosition{x=3, z=2}
Placing at TerrainPosition{x=2, z=2}
Placing at TerrainPosition{x=1, z=2}
Placing at TerrainPosition{x=0, z=2}
Placing at TerrainPosition{x=9, z=17}
Placing at TerrainPosition{x=12, z=17}
Placing at TerrainPosition{x=19, z=12}
Placing at TerrainPosition{x=18, z=12}
Placing at TerrainPosition{x=17, z=12}
Placing at TerrainPosition{x=16, z=12}
Placing at TerrainPosition{x=15, z=12}
Placing at TerrainPosition{x=14, z=12}
Adding person
Placing at TerrainPosition{x=14, z=10}
Placing at TerrainPosition{x=15, z=10}
Placing at TerrainPosition{x=16, z=10}
Placing at TerrainPosition{x=16, z=9}
Placing at TerrainPosition{x=16, z=8}
Placing at TerrainPosition{x=16, z=7}
Placing at TerrainPosition{x=16, z=6}
Placing at TerrainPosition{x=16, z=5}
Placing at TerrainPosition{x=16, z=4}
Placing at TerrainPosition{x=17, z=4}
Placing at TerrainPosition{x=18, z=4}
Placing at TerrainPosition{x=19, z=4}
Placing at TerrainPosition{x=20, z=4}
Placing at TerrainPosition{x=20, z=5}
Placing at TerrainPosition{x=20, z=6}
Placing at TerrainPosition{x=20, z=7}
Placing at TerrainPosition{x=20, z=8}
Placing at TerrainPosition{x=20, z=9}
Placing at TerrainPosition{x=20, z=10}
Placing at TerrainPosition{x=19, z=10}
Placing at TerrainPosition{x=19, z=18}
Placing at TerrainPosition{x=18, z=18}
Placing at TerrainPosition{x=17, z=18}
Placing at TerrainPosition{x=16, z=18}
Placing at TerrainPosition{x=15, z=18}
Placing at TerrainPosition{x=14, z=18}
Placing at TerrainPosition{x=17, z=19}
Placing at TerrainPosition{x=17, z=20}
Placing at TerrainPosition{x=17, z=21}
Placing at TerrainPosition{x=17, z=22}
Placing at TerrainPosition{x=17, z=23}
Placing at TerrainPosition{x=17, z=24}
Placing at TerrainPosition{x=17, z=25}
Placing at TerrainPosition{x=17, z=26}
Placing at TerrainPosition{x=17, z=27}
Placing at TerrainPosition{x=17, z=28}
Placing at TerrainPosition{x=17, z=29}
Placing at TerrainPosition{x=17, z=30}
Placing at TerrainPosition{x=17, z=31}
Placing at TerrainPosition{x=16, z=31}
Placing at TerrainPosition{x=16, z=30}
Placing at TerrainPosition{x=16, z=29}
Placing at TerrainPosition{x=15, z=29}
Placing at TerrainPosition{x=15, z=30}
Placing at TerrainPosition{x=15, z=31}
Placing at TerrainPosition{x=16, z=28}
Placing at TerrainPosition{x=15, z=28}
Placing at TerrainPosition{x=18, z=28}
Placing at TerrainPosition{x=18, z=29}
Placing at TerrainPosition{x=18, z=30}
Placing at TerrainPosition{x=18, z=31}
Placing at TerrainPosition{x=19, z=31}
Placing at TerrainPosition{x=19, z=30}
Placing at TerrainPosition{x=19, z=29}
Placing at TerrainPosition{x=19, z=28}
Placing at TerrainPosition{x=6, z=26}
Placing at TerrainPosition{x=6, z=27}
Placing at TerrainPosition{x=6, z=28}
Placing at TerrainPosition{x=6, z=29}
Placing at TerrainPosition{x=6, z=30}
Placing at TerrainPosition{x=6, z=31}
Placing at TerrainPosition{x=6, z=32}
Placing at TerrainPosition{x=6, z=33}
Placing at TerrainPosition{x=5, z=33}
Placing at TerrainPosition{x=5, z=32}
Placing at TerrainPosition{x=5, z=31}
Placing at TerrainPosition{x=5, z=30}
Placing at TerrainPosition{x=5, z=29}
Placing at TerrainPosition{x=5, z=28}
Placing at TerrainPosition{x=5, z=27}
Placing at TerrainPosition{x=5, z=26}
Placing at TerrainPosition{x=6, z=19}
Placing at TerrainPosition{x=5, z=19}
Placing at TerrainPosition{x=4, z=19}
Placing at TerrainPosition{x=3, z=19}
Placing at TerrainPosition{x=3, z=18}
Placing at TerrainPosition{x=3, z=17}
Placing at TerrainPosition{x=3, z=16}
Placing at TerrainPosition{x=3, z=15}
Placing at TerrainPosition{x=3, z=14}
Placing at TerrainPosition{x=3, z=13}
Placing at TerrainPosition{x=3, z=12}
Placing at TerrainPosition{x=3, z=11}
Placing at TerrainPosition{x=2, z=11}
Placing at TerrainPosition{x=1, z=11}
Placing at TerrainPosition{x=0, z=11}
Adding person
Placing at TerrainPosition{x=0, z=10}
Placing at TerrainPosition{x=0, z=9}
Placing at TerrainPosition{x=0, z=8}
Placing at TerrainPosition{x=0, z=7}
Placing at TerrainPosition{x=0, z=6}
Placing at TerrainPosition{x=0, z=5}
Placing at TerrainPosition{x=0, z=4}
Placing at TerrainPosition{x=1, z=4}
Placing at TerrainPosition{x=2, z=4}
Placing at TerrainPosition{x=2, z=5}
Placing at TerrainPosition{x=2, z=6}
Placing at TerrainPosition{x=2, z=7}
Placing at TerrainPosition{x=2, z=8}
Placing at TerrainPosition{x=2, z=9}
Placing at TerrainPosition{x=2, z=10}



Destination = 0,2 => not working
         */

        TerrainPosition v1 = new TerrainPosition(0, 0);
        TerrainPosition v2 = new TerrainPosition(0, 2);
        TerrainPosition v3 = new TerrainPosition(5, 0);
        TerrainPosition v4 = new TerrainPosition(5, 2);

        int[] roadPositions = new int[]{
                v1.getX(), v1.getZ(),
                v2.getX(), v2.getZ(),
                1, 0,
                2, 0,
                3, 0,
                4, 0,
                v3.getX(), v3.getZ(),
                6, 0,
                5, 1,
                v4.getX(), v4.getZ(),
                5, 3,
                4, 2,
                3, 2,
                2, 2,
                1, 2
        };

        TerrainPosition[] roads = TerrainPosition.toPositionArray(roadPositions);

        GameObject.newInstances(DirtRoad.class, roads);

        RoadGraph roadGraph = scene.getRoadGraph();
        PathFinder pathFinder = new PathFinder(roadGraph);

        // Found route
        Path foundPath = pathFinder.findBestPath(v1, v2, 0);

        // Expected global routes
        Path expectedPath = new Path();

        // Expected individual routes
        NodeConnection expectedNodeConnection1 = new NodeConnection(new NormalRoad(v1), new NodeRoad(v3));
        expectedNodeConnection1.addRoad(new NormalRoad(new TerrainPosition(1, 0)));
        expectedNodeConnection1.addRoad(new NormalRoad(new TerrainPosition(2, 0)));
        expectedNodeConnection1.addRoad(new NormalRoad(new TerrainPosition(3, 0)));
        expectedNodeConnection1.addRoad(new NormalRoad(new TerrainPosition(4, 0)));
        expectedNodeConnection1.addRoad(new NodeRoad(v3));
        NodeConnection expectedNodeConnection2 = new NodeConnection(new NodeRoad(v3), new NodeRoad(v4));
        expectedNodeConnection2.addRoad(new NormalRoad(new TerrainPosition(5, 1)));
        expectedNodeConnection2.addRoad(new NodeRoad(v4));
        NodeConnection expectedNodeConnection3 = new NodeConnection(new NodeRoad(v4), new NormalRoad(v2));
        expectedNodeConnection3.addRoad(new NormalRoad(new TerrainPosition(4, 2)));
        expectedNodeConnection3.addRoad(new NormalRoad(new TerrainPosition(3, 2)));
        expectedNodeConnection3.addRoad(new NormalRoad(new TerrainPosition(2, 2)));
        expectedNodeConnection3.addRoad(new NormalRoad(new TerrainPosition(1, 2)));
        expectedNodeConnection3.addRoad(new NormalRoad(v2));

        expectedPath.add(expectedNodeConnection1);
        expectedPath.add(expectedNodeConnection2);
        expectedPath.add(expectedNodeConnection3);

        assertTrue(foundPath.comparePaths(expectedPath));
        assertTrue(foundPath.compareCost(expectedPath));
    }

    @Test
    void testUnobstrusivePath1() {
        RoadGraph roadGraph = scene.getRoadGraph();
        PathFinder pathFinder = new PathFinder(roadGraph);

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
        Path foundPath = pathFinder.findUnobstructedPathV1(v1, v2);
        // Expected global routes
        Path expectedPath = new Path();

        // Expected individual routes
        NormalRoad end = new NormalRoad(v2);
        NodeConnection expectedNodeConnection = new NodeConnection(new NormalRoad(v1), end);

        for (TerrainPosition pos : positions)
            expectedNodeConnection.addRoad(new NormalRoad(pos));

        expectedNodeConnection.addRoad(end);
        expectedPath.add(expectedNodeConnection);

        assertTrue(foundPath.comparePaths(expectedPath));
        assertTrue(foundPath.compareCost(expectedPath));
    }

    @Test
    void testUnobstrusivePath2() {
        RoadGraph roadGraph = scene.getRoadGraph();
        PathFinder pathFinder = new PathFinder(roadGraph);

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

        Path foundPath = pathFinder.findUnobstructedPathV1(v1, v3);

        // Expected global routes
        Path expectedPath = new Path();

        // Expected individual routes
        NormalRoad middle = new NormalRoad(v2);
        NormalRoad end = new NormalRoad(v3);
        NodeConnection expectedNodeConnection = new NodeConnection(new NormalRoad(v1), middle);
        NodeConnection expectedNodeConnection2 = new NodeConnection(middle, end);

        for (TerrainPosition pos : positions)
            expectedNodeConnection.addRoad(new NormalRoad(pos));
        expectedNodeConnection.addRoad(middle);

        for (TerrainPosition pos : positions2)
            expectedNodeConnection2.addRoad(new NormalRoad(pos));
        expectedNodeConnection2.addRoad(end);

        expectedPath.add(expectedNodeConnection);
        expectedPath.add(expectedNodeConnection2);

        assertTrue(foundPath.comparePaths(expectedPath));
        assertTrue(foundPath.compareCost(expectedPath));
    }

    @Test
    void testRoadsConnectedToItem() {
        TerrainPosition v1 = new TerrainPosition(50, 50);
        TerrainPosition v2 = new TerrainPosition(50, 60);

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
        }

        assertTrue(Utils.listContentEquals(scene.getRoadsConnectedToGameObject(market), expectedRoads));
    }

    @Test
    void testRoadsConnectedToItem2() {
        List<GameObject> expectedRoads = new ArrayList<>();

        int x = 35;
        int z = 50;

        Insula insula = new Insula();
        insula.addComponent(new PositionComponent(new TerrainPosition(x, z)));

        GameObject.newInstances(DirtRoad.class, new TerrainPosition[]{
                new TerrainPosition(x - Insula.X_NEGATIVE_OFFSET, z - Insula.Z_NEGATIVE_OFFSET - 1),
                new TerrainPosition(x - Insula.X_NEGATIVE_OFFSET - 1, z - Insula.Z_NEGATIVE_OFFSET),
                new TerrainPosition(x - Insula.X_NEGATIVE_OFFSET - 1, z + Insula.Z_POSITIVE_OFFSET),
                new TerrainPosition(x - Insula.X_NEGATIVE_OFFSET - 1, z + Insula.Z_POSITIVE_OFFSET + 1),
                new TerrainPosition(x - Insula.X_NEGATIVE_OFFSET - 1, z + Insula.Z_POSITIVE_OFFSET + 2),
                new TerrainPosition(x - Insula.X_NEGATIVE_OFFSET - 1, z + Insula.Z_POSITIVE_OFFSET + 3),
                new TerrainPosition(x - 1, z + Insula.Z_POSITIVE_OFFSET + 1),
                new TerrainPosition(x - 1, z + Insula.Z_POSITIVE_OFFSET + 2),
                new TerrainPosition(x - 1, z + Insula.Z_POSITIVE_OFFSET + 3),
                new TerrainPosition(x + Insula.X_POSITIVE_OFFSET + 1, z),
                new TerrainPosition(x + Insula.X_POSITIVE_OFFSET + 2, z)
        });

        TerrainPosition[] connectedRoads = new TerrainPosition[]{
                new TerrainPosition(x - 1, z + Insula.Z_POSITIVE_OFFSET + 1),
                new TerrainPosition(x - Insula.X_NEGATIVE_OFFSET - 1, z + Insula.Z_POSITIVE_OFFSET),
                new TerrainPosition(x - Insula.X_NEGATIVE_OFFSET, z - Insula.Z_NEGATIVE_OFFSET - 1),
                new TerrainPosition(x - Insula.X_NEGATIVE_OFFSET - 1, z - Insula.Z_NEGATIVE_OFFSET),
                new TerrainPosition(x + Insula.X_POSITIVE_OFFSET + 1, z)
        };

        for (TerrainPosition roadPos : connectedRoads) {
            GameObject.newInstance(DirtRoad.class, roadPos);
            expectedRoads.add(scene.getGameObjectAtPosition(roadPos));
        }
        assertTrue(Utils.listContentEquals(scene.getRoadsConnectedToGameObject(insula), expectedRoads));
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

        Path bestPath = PathFinder.findBestPath(insula, market, 0);

        Path expectedPath = new Path();
        NodeConnection nodeConnection = new NodeConnection(new NormalRoad(positions[0]),
                new NormalRoad(positions[positions.length - 1]));

        Arrays.stream(positions).skip(1).forEach(pos -> nodeConnection.addRoad(new NormalRoad(pos)));
        expectedPath.add(nodeConnection);
        assertEquals(expectedPath, bestPath);
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

        Path bestPath = PathFinder.findBestPath(insula, market, 0);

        Path expectedPath = new Path();
        NodeConnection nodeConnection = new NodeConnection(new NormalRoad(positions[0]),
                new NormalRoad(positions[positions.length - 1]));

        Arrays.stream(positions).skip(1).forEach(pos -> nodeConnection.addRoad(new NormalRoad(pos)));
        expectedPath.add(nodeConnection);
        assertEquals(expectedPath, bestPath);
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

        Path bestPath = PathFinder.findBestPath(insula, market, 0);

        Path expectedPath = new Path();
        NodeConnection nodeConnection = new NodeConnection(new NormalRoad(positions[0]),
                new NodeRoad(new TerrainPosition(50, 75)));

        Arrays.stream(positions).skip(1).forEach(pos -> nodeConnection.addRoad(new NormalRoad(pos)));
        nodeConnection.addRoad(new NodeRoad(new TerrainPosition(50, 75)));
        expectedPath.add(nodeConnection);
        assertEquals(expectedPath, bestPath);
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

        Path bestPath = PathFinder.findBestPath(insula, market, 0);
        Path expectedPath = new Path();
        NodeConnection nodeConnection = new NodeConnection(new NodeRoad(new TerrainPosition(33, 53)),
                new NodeRoad(new TerrainPosition(33, 54)));
        nodeConnection.addRoad(new NodeRoad(new TerrainPosition(33, 54)));

        NodeConnection nodeConnection2 = new NodeConnection(new NodeRoad(new TerrainPosition(33, 54)),
                new NodeRoad(new TerrainPosition(33, 55)));
        nodeConnection2.addRoad(new NodeRoad(new TerrainPosition(33, 55)));

        NodeConnection nodeConnection3 = new NodeConnection(new NodeRoad(new TerrainPosition(33, 55)),
                new NodeRoad(new TerrainPosition(33, 56)));
        nodeConnection3.addRoad(new NodeRoad(new TerrainPosition(33, 56)));

        Path expectedPath2 = new Path();
        NodeConnection nodeConnection21 = new NodeConnection(new NodeRoad(new TerrainPosition(34, 53)),
                new NodeRoad(new TerrainPosition(34, 54)));
        nodeConnection21.addRoad(new NodeRoad(new TerrainPosition(34, 54)));

        NodeConnection nodeConnection22 = new NodeConnection(new NodeRoad(new TerrainPosition(34, 54)),
                new NodeRoad(new TerrainPosition(34, 55)));
        nodeConnection22.addRoad(new NodeRoad(new TerrainPosition(34, 55)));

        NodeConnection nodeConnection23 = new NodeConnection(new NodeRoad(new TerrainPosition(34, 55)),
                new NormalRoad(new TerrainPosition(34, 56)));
        nodeConnection23.addRoad(new NormalRoad(new TerrainPosition(34, 56)));

        expectedPath.add(nodeConnection);
        expectedPath.add(nodeConnection2);
        expectedPath.add(nodeConnection3);

        expectedPath2.add(nodeConnection21);
        expectedPath2.add(nodeConnection22);
        expectedPath2.add(nodeConnection23);

        assertTrue(expectedPath.comparePaths(bestPath) || expectedPath2.comparePaths(bestPath));
        assertTrue(expectedPath.compareCost(bestPath) && expectedPath2.compareCost(bestPath));
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

        Path bestPath = PathFinder.findBestPath(insula, market, 0);
        Path expectedPath = new Path();
        NodeConnection nodeConnection = new NodeConnection(new NodeRoad(new TerrainPosition(33, 53)),
                new NodeRoad(new TerrainPosition(33, 54)));
        nodeConnection.addRoad(new NodeRoad(new TerrainPosition(33, 54)));

        NodeConnection nodeConnection2 = new NodeConnection(new NodeRoad(new TerrainPosition(33, 54)),
                new NodeRoad(new TerrainPosition(33, 55)));
        nodeConnection2.addRoad(new NodeRoad(new TerrainPosition(33, 55)));

        NodeConnection nodeConnection3 = new NodeConnection(new NodeRoad(new TerrainPosition(33, 55)),
                new NodeRoad(new TerrainPosition(33, 56)));
        nodeConnection3.addRoad(new NodeRoad(new TerrainPosition(33, 56)));

        expectedPath.add(nodeConnection);
        expectedPath.add(nodeConnection2);
        expectedPath.add(nodeConnection3);
        assertEquals(expectedPath, bestPath);
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

        Path bestPath = PathFinder.findBestPath(insula, market, 0);

        Path expectedPath = new Path();
        NodeConnection nodeConnection = new NodeConnection(new NormalRoad(positions[0]),
                new NormalRoad(positions[positions.length - 1]));

        Arrays.stream(positions).skip(1).forEach(pos -> nodeConnection.addRoad(new NormalRoad(pos)));
        expectedPath.add(nodeConnection);
        assertEquals(expectedPath, bestPath);
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

        Path bestPath = PathFinder.findBestPath(insula, market, 0);

        Path expectedPath = new Path();
        NodeConnection nodeConnection = new NodeConnection(new NormalRoad(positions[0]),
                new NormalRoad(positions[positions.length - 1]));

        Arrays.stream(positions).skip(1).forEach(pos -> nodeConnection.addRoad(new NormalRoad(pos)));
        expectedPath.add(nodeConnection);
        assertEquals(expectedPath, bestPath);
    }

    @Test
    void testPathfindingMarketToInsula8() {
        Market market = new Market();
        market.addComponent(new PositionComponent(new TerrainPosition(50, 50)));

        TerrainPosition[] positions = new TerrainPosition[15];
        for (int i = 55; i < 70; i++)
            positions[i - 55] = new TerrainPosition(50, i);

        Insula insula = new Insula();
        insula.addComponent(new PositionComponent(new TerrainPosition(47, 60)));

        GameObject.newInstances(DirtRoad.class, positions);
        GameObject.newInstance(DirtRoad.class, new TerrainPosition(51, 68));

        Path bestPath = PathFinder.findBestPath(insula, market, 0);

        Path expectedPath = new Path();

        NormalRoad end = new NormalRoad(new TerrainPosition(50, 57));
        NodeConnection nodeConnection = new NodeConnection(new NormalRoad(new TerrainPosition(50, 55)), end);

        Arrays.stream(positions, 0, 3).forEach(pos -> nodeConnection.addRoad(new NormalRoad(pos)));
        expectedPath.add(nodeConnection.invert());
        assertEquals(expectedPath, bestPath);
    }

    @Test
    void testPathfindingMarketToInsula9() {
        Market market = new Market();
        market.addComponent(new PositionComponent(new TerrainPosition(50, 50)));

        TerrainPosition[] positions = new TerrainPosition[15];
        for (int i = 55; i < 70; i++)
            positions[i - 55] = new TerrainPosition(50, i);

        Insula insula = new Insula();
        insula.addComponent(new PositionComponent(new TerrainPosition(47, 61)));

        GameObject.newInstances(DirtRoad.class, positions);
        GameObject.newInstance(DirtRoad.class, new TerrainPosition(51, 68));
        GameObject.newInstance(DirtRoad.class, new TerrainPosition(51, 55));
        GameObject.newInstance(DirtRoad.class, new TerrainPosition(49, 55));

        Path bestPath = PathFinder.findBestPath(insula, market, 0);
        Path expectedPath = new Path();

        NormalRoad end = new NormalRoad(new TerrainPosition(50, 58));
        NodeConnection nodeConnection = new NodeConnection(new NodeRoad(new TerrainPosition(50, 55)), end);

        Arrays.stream(positions, 1, 4).forEach(pos -> nodeConnection.addRoad(new NormalRoad(pos)));
        expectedPath.add(nodeConnection.invert());
        assertEquals(expectedPath, bestPath);
    }

    @Test
    void testPathfindingMarketToInsula10() {
        Market market = new Market();
        market.addComponent(new PositionComponent(new TerrainPosition(50, 50)));

        TerrainPosition[] positions = new TerrainPosition[15];
        for (int i = 55; i < 70; i++)
            positions[i - 55] = new TerrainPosition(50, i);

        Insula insula = new Insula();
        insula.addComponent(new PositionComponent(new TerrainPosition(47, 61)));

        GameObject.newInstances(DirtRoad.class, positions);
        GameObject.newInstance(DirtRoad.class, new TerrainPosition(51, 68));
        GameObject.newInstance(DirtRoad.class, new TerrainPosition(51, 55));
        GameObject.newInstance(DirtRoad.class, new TerrainPosition(49, 55));
        GameObject.newInstance(DirtRoad.class, new TerrainPosition(49, 65));

        Path bestPath = PathFinder.findBestPath(insula, market, 0);
        Path expectedPath = new Path();

        NormalRoad end = new NormalRoad(new TerrainPosition(50, 58));
        NodeConnection nodeConnection = new NodeConnection(new NodeRoad(new TerrainPosition(50, 55)), end);

        Arrays.stream(positions, 1, 4).forEach(pos -> nodeConnection.addRoad(new NormalRoad(pos)));
        expectedPath.add(nodeConnection.invert());
        assertEquals(expectedPath, bestPath);
    }

    @Test
    void testPathfindingMarketToInsula11() {
        Market market = new Market();
        market.addComponent(new PositionComponent(new TerrainPosition(50, 50)));

        TerrainPosition[] positions = new TerrainPosition[15];
        for (int i = 55; i < 70; i++)
            positions[i - 55] = new TerrainPosition(50, i);

        Insula insula = new Insula();
        insula.addComponent(new PositionComponent(new TerrainPosition(54, 61)));

        GameObject.newInstances(DirtRoad.class, positions);
        GameObject.newInstance(DirtRoad.class, new TerrainPosition(51, 55));
        GameObject.newInstance(DirtRoad.class, new TerrainPosition(49, 55));
        GameObject.newInstance(DirtRoad.class, new TerrainPosition(51, 63));

        Path bestPath = PathFinder.findBestPath(insula, market, 0);
        Path expectedPath = new Path();

        NormalRoad end = new NormalRoad(new TerrainPosition(50, 58));
        NodeConnection nodeConnection = new NodeConnection(new NodeRoad(new TerrainPosition(50, 55)), end);

        Arrays.stream(positions, 1, 4).forEach(pos -> nodeConnection.addRoad(new NormalRoad(pos)));
        expectedPath.add(nodeConnection.invert());
        assertEquals(expectedPath, bestPath);
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
        insula.addComponent(new PositionComponent(new TerrainPosition(63, 75)));

        GameObject.newInstances(DirtRoad.class, positions);
        GameObject.newInstance(DirtRoad.class, new TerrainPosition(51, 55));
        GameObject.newInstance(DirtRoad.class, new TerrainPosition(49, 55));
        GameObject.newInstance(DirtRoad.class, new TerrainPosition(51, 63));
        GameObject.newInstance(DirtRoad.class, new TerrainPosition(62, 71));

        List<TerrainPosition> pos2 = Arrays.asList(positions2);
        Collections.reverse(pos2);
        positions2 = pos2.toArray(new TerrainPosition[0]);
        GameObject.newInstances(DirtRoad.class, positions2);

        Path bestPath = PathFinder.findBestPath(insula, market, 0);

        Path expectedPath = new Path();

        NodeRoad node1 = new NodeRoad(new TerrainPosition(50, 63));
        NodeRoad node2 = new NodeRoad(new TerrainPosition(62, 70));
        NodeRoad node3 = new NodeRoad(new TerrainPosition(50, 55));

        NodeConnection nodeConnection1 = new NodeConnection(new NormalRoad(new TerrainPosition(62, 71)), node2);
        nodeConnection1.addRoad(node2);
        expectedPath.add(nodeConnection1);

        NodeConnection nodeConnection2 = new NodeConnection(node2, node1);
        Arrays.stream(positions2, 2, 14).forEach(p -> nodeConnection2.addRoad(new NormalRoad(p)));
        List<TerrainPosition> pos = Arrays.asList(positions);
        Collections.reverse(pos);
        IntStream.range(0, 6).mapToObj(pos::get).forEach(p -> nodeConnection2.addRoad(new NormalRoad(p)));
        nodeConnection2.addRoad(node1);
        expectedPath.add(nodeConnection2);

        NodeConnection nodeConnection3 = new NodeConnection(node3, node1);
        Collections.reverse(pos);
        IntStream.range(1, 8).mapToObj(pos::get).forEach(p -> nodeConnection3.addRoad(new NormalRoad(p)));
        nodeConnection3.addRoad(node1);
        expectedPath.add(nodeConnection3.invert());
        assertEquals(expectedPath, bestPath);
    }

    @Test
    void testPathfindingMarketToInsula13() {
        Insula insula = new Insula();
        insula.addComponent(new PositionComponent(new TerrainPosition(40, 47)));
        Insula insula2 = new Insula();
        insula2.addComponent(new PositionComponent(new TerrainPosition(50, 50)));
        Market market = new Market();
        market.addComponent(new PositionComponent(new TerrainPosition(50, 80)));
        for (int x = 0; x < 21; x++)
            GameObject.newInstance(DirtRoad.class, new TerrainPosition(x, 0));

        for (int x = 20; x < 41; x++)
            GameObject.newInstance(DirtRoad.class, new TerrainPosition(x, 40));
        GameObject.newInstance(DirtRoad.class, new TerrainPosition(40, 41));
        GameObject.newInstance(DirtRoad.class, new TerrainPosition(40, 42));
        GameObject.newInstance(DirtRoad.class, new TerrainPosition(40, 43));
        GameObject.newInstance(DirtRoad.class, new TerrainPosition(49, 54));
        GameObject.newInstance(DirtRoad.class, new TerrainPosition(35, 39));

        for (int i = 53; i < 76; i++)
            GameObject.newInstance(DirtRoad.class, new TerrainPosition(50, i));

        GameObject.newInstance(Insula.class, new TerrainPosition(32, 37));
        Insula extraInsula = GameObject.newInstance(Insula.class, new TerrainPosition(39, 37));

        Path bestPath = PathFinder.findBestPath(extraInsula, market, 0);
        Path expectedPath = new Path();

        assertEquals(expectedPath, bestPath);
    }

    @Test
    void testPathfindingMarketToInsula14() {
        Insula insula = GameObject.newInstance(Insula.class, new TerrainPosition(36, 75));
        Market market = GameObject.newInstance(Market.class, new TerrainPosition(50, 80));

        for (int i = 55; i < 76; i++)
            GameObject.newInstance(DirtRoad.class, new TerrainPosition(50, i));
        for (int i = 32; i < 50; i++)
            GameObject.newInstance(DirtRoad.class, new TerrainPosition(i, 55));
        for (int i = 56; i < 73; i++)
            GameObject.newInstance(DirtRoad.class, new TerrainPosition(32, i));
        GameObject.newInstance(DirtRoad.class, new TerrainPosition(31, 58));

        Path bestPath = PathFinder.findBestPath(insula, market, 0);

        Path expectedPath = new Path();

        NodeRoad node = new NodeRoad(new TerrainPosition(32, 58));
        NodeConnection nodeConnection1 = new NodeConnection(node, new NormalRoad(new TerrainPosition(32, 72)));
        IntStream.rangeClosed(59, 72)
                .forEach(z -> nodeConnection1.addRoad(new NormalRoad(new TerrainPosition(32, z))));
        expectedPath.add(nodeConnection1.invert());

        NodeConnection nodeConnection2 = new NodeConnection(node, new NormalRoad(new TerrainPosition(50, 75)));
        nodeConnection2.addRoad(new NormalRoad(new TerrainPosition(32, 57)));
        nodeConnection2.addRoad(new NormalRoad(new TerrainPosition(32, 56)));
        IntStream.rangeClosed(32, 50).forEach(x -> nodeConnection2.addRoad(new NormalRoad(new TerrainPosition(x, 55))));
        IntStream.rangeClosed(56, 75).forEach(z -> nodeConnection2.addRoad(new NormalRoad(new TerrainPosition(50, z))));

        expectedPath.add(nodeConnection2);

        assertEquals(expectedPath, bestPath);
    }

    @Test
    void testPathfindingMarketToInsula15() {
        Insula insula = GameObject.newInstance(Insula.class, new TerrainPosition(36, 75));
        Market market = GameObject.newInstance(Market.class, new TerrainPosition(50, 80));

        for (int i = 55; i < 76; i++)
            GameObject.newInstance(DirtRoad.class, new TerrainPosition(50, i));
        for (int i = 32; i < 50; i++)
            GameObject.newInstance(DirtRoad.class, new TerrainPosition(i, 55));
        for (int i = 56; i < 73; i++)
            GameObject.newInstance(DirtRoad.class, new TerrainPosition(32, i));
        GameObject.newInstance(DirtRoad.class, new TerrainPosition(31, 58));
        for (int i = 77; i > 66; i--)
            GameObject.newInstance(DirtRoad.class, new TerrainPosition(39, i));
        for (int i = 40; i < 45; i++)
            GameObject.newInstance(DirtRoad.class, new TerrainPosition(i, 67));
        GameObject.newInstance(DirtRoad.class, new TerrainPosition(43, 66));
        GameObject.newInstance(DirtRoad.class, new TerrainPosition(43, 65));

        Path bestPath = PathFinder.findBestPath(insula, market, 0);

        Path expectedPath = new Path();

        NodeRoad node = new NodeRoad(new TerrainPosition(32, 58));
        NodeConnection nodeConnection1 = new NodeConnection(node, new NormalRoad(new TerrainPosition(32, 72)));
        IntStream.rangeClosed(59, 72)
                .forEach(z -> nodeConnection1.addRoad(new NormalRoad(new TerrainPosition(32, z))));
        expectedPath.add(nodeConnection1.invert());

        NodeConnection nodeConnection2 = new NodeConnection(node, new NormalRoad(new TerrainPosition(50, 75)));
        nodeConnection2.addRoad(new NormalRoad(new TerrainPosition(32, 57)));
        nodeConnection2.addRoad(new NormalRoad(new TerrainPosition(32, 56)));
        IntStream.rangeClosed(32, 50).forEach(x -> nodeConnection2.addRoad(new NormalRoad(new TerrainPosition(x, 55))));
        IntStream.rangeClosed(56, 75).forEach(z -> nodeConnection2.addRoad(new NormalRoad(new TerrainPosition(50, z))));

        expectedPath.add(nodeConnection2);

        assertEquals(expectedPath, bestPath);
    }

    @Test
    void testPathfindingMarketToInsula16() {
        Insula insula = GameObject.newInstance(Insula.class, new TerrainPosition(52, 69));
        Market market = GameObject.newInstance(Market.class, new TerrainPosition(50, 80));

        GameObject.newInstance(DirtRoad.class, new TerrainPosition(44, 75));
        GameObject.newInstance(DirtRoad.class, new TerrainPosition(45, 76));
        GameObject.newInstance(DirtRoad.class, new TerrainPosition(45, 75));
        GameObject.newInstance(DirtRoad.class, new TerrainPosition(46, 75));
        GameObject.newInstance(DirtRoad.class, new TerrainPosition(47, 75));
        GameObject.newInstance(DirtRoad.class, new TerrainPosition(48, 75));
        GameObject.newInstance(DirtRoad.class, new TerrainPosition(48, 70));
        GameObject.newInstance(DirtRoad.class, new TerrainPosition(48, 74));
        GameObject.newInstance(DirtRoad.class, new TerrainPosition(48, 73));
        GameObject.newInstance(DirtRoad.class, new TerrainPosition(48, 72));
        GameObject.newInstance(DirtRoad.class, new TerrainPosition(48, 71));
        GameObject.newInstance(DirtRoad.class, new TerrainPosition(47, 71));

        Path bestPath = PathFinder.findBestPath(insula, market, 0);

        Path expectedPath = new Path();
        NodeConnection nodeConnection = new NodeConnection(new NodeRoad(new TerrainPosition(48, 71)),
                new NormalRoad(new TerrainPosition(48, 75)));
        nodeConnection.addRoad(new NormalRoad(new TerrainPosition(48, 72)));
        nodeConnection.addRoad(new NormalRoad(new TerrainPosition(48, 73)));
        nodeConnection.addRoad(new NormalRoad(new TerrainPosition(48, 74)));
        nodeConnection.addRoad(new NormalRoad(new TerrainPosition(48, 75)));
        expectedPath.add(nodeConnection);

        assertEquals(expectedPath, bestPath);
    }

    @Test
    void testPathfindingMarketToInsula17() {
        Insula insula = GameObject.newInstance(Insula.class, new TerrainPosition(42, 79));
        Market market = GameObject.newInstance(Market.class, new TerrainPosition(50, 80));

        GameObject.newInstance(DirtRoad.class, new TerrainPosition(44, 75));
        GameObject.newInstance(DirtRoad.class, new TerrainPosition(45, 76));
        GameObject.newInstance(DirtRoad.class, new TerrainPosition(45, 75));
        GameObject.newInstance(DirtRoad.class, new TerrainPosition(46, 75));
        GameObject.newInstance(DirtRoad.class, new TerrainPosition(47, 75));
        GameObject.newInstance(DirtRoad.class, new TerrainPosition(48, 75));
        GameObject.newInstance(DirtRoad.class, new TerrainPosition(48, 70));
        GameObject.newInstance(DirtRoad.class, new TerrainPosition(48, 74));
        GameObject.newInstance(DirtRoad.class, new TerrainPosition(48, 73));
        GameObject.newInstance(DirtRoad.class, new TerrainPosition(48, 72));
        GameObject.newInstance(DirtRoad.class, new TerrainPosition(48, 71));
        GameObject.newInstance(DirtRoad.class, new TerrainPosition(47, 71));

        Path bestPath = PathFinder.findBestPath(insula, market, 0);
        Path expectedPath = new Path();
        NodeConnection nodeConnection = new NodeConnection(new NormalRoad(new TerrainPosition(45, 76)),
                new NormalRoad(new TerrainPosition(45, 76)));
        expectedPath.add(nodeConnection);

        assertEquals(expectedPath, bestPath);
    }

    @Test
    void testPathfindingMarketToInsula18() {
        Insula insula = GameObject.newInstance(Insula.class, new TerrainPosition(54, 73));
        Market market = GameObject.newInstance(Market.class, new TerrainPosition(50, 80));

        GameObject.newInstance(DirtRoad.class, new TerrainPosition(50, 74));
        GameObject.newInstance(DirtRoad.class, new TerrainPosition(50, 75));
        GameObject.newInstance(DirtRoad.class, new TerrainPosition(49, 75));

        Path bestPath = PathFinder.findBestPath(insula, market, 0);
        Path expectedPath = new Path();
        NodeConnection nodeConnection = new NodeConnection(new NormalRoad(new TerrainPosition(50, 75)),
                new NormalRoad(new TerrainPosition(50, 75)));
        expectedPath.add(nodeConnection);

        assertEquals(expectedPath, bestPath);
    }

    @Test
    void testPathfindingMarketToInsula19() {
        Market market = GameObject.newInstance(Market.class, new TerrainPosition(50, 80));
        Insula insula = GameObject.newInstance(Insula.class, new TerrainPosition(49, 88));
        for (int z = 75; z < 86; z++)
            GameObject.newInstance(DirtRoad.class, new TerrainPosition(45, z));
        GameObject.newInstance(DirtRoad.class, new TerrainPosition(44, 75));
        GameObject.newInstance(DirtRoad.class, new TerrainPosition(46, 75));

        Path bestPath = PathFinder.findBestPath(insula, market, 0);
        Path expectedPath = new Path();
        NodeConnection nodeConnection = new NodeConnection(
                new NormalRoad(new TerrainPosition(45, 85)), new NormalRoad(new TerrainPosition(45, 84)));
        nodeConnection.addRoad(new NormalRoad(new TerrainPosition(45, 84)));
        expectedPath.add(nodeConnection);

        assertEquals(expectedPath, bestPath);
    }
}
