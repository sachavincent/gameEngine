package tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.lwjgl.glfw.GLFW.glfwInit;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.IntStream;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pathfinding.NormalRoad;
import pathfinding.RoadGraph;
import pathfinding.RoadNode;
import pathfinding.RouteRoad;
import renderEngine.DisplayManager;
import scene.gameObjects.DirtRoad;
import scene.gameObjects.GameObject;
import scene.Scene;
import terrains.TerrainPosition;

public class RoadGraphTest {

    private final static Scene scene = Scene.getInstance();

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
    void testRoadGraph1() {
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

        Set<RoadNode> nodes = roadGraph.getNodes();
        Set<RouteRoad> routes = roadGraph.getRoutes();

        Set<RoadNode> expectedNodes = new HashSet<>();
        Set<RouteRoad> expectedRoutes = new TreeSet<>();

        RoadNode start = new RoadNode(v1);
        expectedNodes.add(start);
        RoadNode end = new RoadNode(v4);
        expectedNodes.add(end);

        RouteRoad route = new RouteRoad(start, end);
        route.addRoad(new NormalRoad(v2));
        route.addRoad(new NormalRoad(v3));
        route.addRoad(end);

        expectedRoutes.add(route);
        expectedRoutes.add(route.invertRoute());

        assertEquals(expectedNodes, nodes);
        assertEquals(expectedRoutes, routes);
    }

    /**
     * 2. Node 2 Node through Node
     */
    @Test
    void testRoadGraph2() {
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

        Set<RoadNode> nodes = roadGraph.getNodes();
        Set<RouteRoad> routes = roadGraph.getRoutes();

        Set<RoadNode> expectedNodes = new HashSet<>();
        Set<RouteRoad> expectedRoutes = new TreeSet<>();

        RoadNode start = new RoadNode(v1);
        expectedNodes.add(start);
        RoadNode middle = new RoadNode(v3);
        expectedNodes.add(middle);
        RoadNode end = new RoadNode(v5);
        expectedNodes.add(end);

        RouteRoad route = new RouteRoad(start, middle);
        route.addRoad(new NormalRoad(v2));
        route.addRoad(middle);

        RouteRoad route2 = new RouteRoad(middle, end);
        route2.addRoad(new NormalRoad(v4));
        route2.addRoad(end);

        expectedRoutes.add(route);
        expectedRoutes.add(route.invertRoute());
        expectedRoutes.add(route2);
        expectedRoutes.add(route2.invertRoute());

        assertEquals(expectedNodes, nodes);
        assertEquals(expectedRoutes, routes);
    }

    /**
     * 3. Node 2 Node Choice
     */
    @Test
    void testRoadGraph3() {
        TerrainPosition v1 = new TerrainPosition(50, 50);
        TerrainPosition v2 = new TerrainPosition(50, 49);
        TerrainPosition v3 = new TerrainPosition(50, 48);
        TerrainPosition v4 = new TerrainPosition(51, 48);
        TerrainPosition v5 = new TerrainPosition(52, 48);

        // v1 -> v2 -> v3 -> v4 -> v5
        int[] roadPositions = new int[]{
                51, 50,
                52, 50,
                53, 50,
                54, 50,
                55, 50,
                55, 49,
                55, 48,
                54, 48,
                53, 48,
                49, 50,
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

        Set<RoadNode> nodes = roadGraph.getNodes();
        Set<RouteRoad> routes = roadGraph.getRoutes();

        Set<RoadNode> expectedNodes = new HashSet<>();
        Set<RouteRoad> expectedRoutes = new TreeSet<>();

        RoadNode start = new RoadNode(v1);
        expectedNodes.add(start);
        RoadNode end = new RoadNode(v5);
        expectedNodes.add(end);

        RouteRoad route = new RouteRoad(start, end);
        route.addRoad(new NormalRoad(v2));
        route.addRoad(new NormalRoad(v3));
        route.addRoad(new NormalRoad(v4));
        route.addRoad(end);

        RouteRoad route2 = new RouteRoad(start, end);
        IntStream.range(0, 9).forEach(i -> route2.addRoad(new NormalRoad(roads[i])));
        route2.addRoad(end);

        expectedRoutes.add(route);
        expectedRoutes.add(route.invertRoute());
        expectedRoutes.add(route2);
        expectedRoutes.add(route2.invertRoute());

        assertEquals(expectedNodes, nodes);
        assertEquals(expectedRoutes, routes);
    }

    /**
     * 4. Node 2 Node through 2 Nodes
     */
    @Test
    void testRoadGraph4() {
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

        Set<RoadNode> nodes = roadGraph.getNodes();
        Set<RouteRoad> routes = roadGraph.getRoutes();

        Set<RoadNode> expectedNodes = new HashSet<>();
        Set<RouteRoad> expectedRoutes = new TreeSet<>();

        RoadNode start = new RoadNode(v1);
        expectedNodes.add(start);
        RoadNode middle1 = new RoadNode(v4);
        expectedNodes.add(middle1);
        RoadNode middle2 = new RoadNode(v7);
        expectedNodes.add(middle2);
        RoadNode end = new RoadNode(v9);
        expectedNodes.add(end);

        RouteRoad route = new RouteRoad(start, middle1);
        route.addRoad(new NormalRoad(v2));
        route.addRoad(new NormalRoad(v3));
        route.addRoad(middle1);

        RouteRoad route2 = new RouteRoad(middle1, middle2);
        route2.addRoad(new NormalRoad(v5));
        route2.addRoad(new NormalRoad(v6));
        route2.addRoad(middle2);

        RouteRoad route3 = new RouteRoad(middle2, end);
        route3.addRoad(new NormalRoad(v8));
        route3.addRoad(end);

        expectedRoutes.add(route);
        expectedRoutes.add(route.invertRoute());
        expectedRoutes.add(route2);
        expectedRoutes.add(route2.invertRoute());
        expectedRoutes.add(route3);
        expectedRoutes.add(route3.invertRoute());

        assertEquals(expectedNodes, nodes);
        assertEquals(expectedRoutes, routes);
    }

    /**
     * 5. Node 2 Node Multiple nodes and choices (v1)
     */
    @Test
    void testRoadGraph5() {
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
                50, 47,
                50, 46,
                51, 46,
                52, 46,
                53, 46,
                54, 46,
                55, 46,
                55, 47,
                49, 50,
                54, 50,
                49, 48,
                55, 49,
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

        Set<RoadNode> nodes = roadGraph.getNodes();
        Set<RouteRoad> routes = roadGraph.getRoutes();

        Set<RoadNode> expectedNodes = new HashSet<>();
        Set<RouteRoad> expectedRoutes = new TreeSet<>();

        RoadNode start = new RoadNode(v1);
        expectedNodes.add(start);
        RoadNode middle1 = new RoadNode(v4);
        expectedNodes.add(middle1);
        RoadNode middle2 = new RoadNode(v6);
        expectedNodes.add(middle2);
        RoadNode middle3 = new RoadNode(v9);
        expectedNodes.add(middle3);
        RoadNode end = new RoadNode(v12);
        expectedNodes.add(end);

        RouteRoad route = new RouteRoad(start, middle2);
        route.addRoad(new NormalRoad(v5));
        route.addRoad(middle2);

        RouteRoad route2 = new RouteRoad(start, middle1);
        route2.addRoad(new NormalRoad(v2));
        route2.addRoad(new NormalRoad(v3));
        route2.addRoad(middle1);

        RouteRoad route3 = new RouteRoad(middle1, middle3);
        route3.addRoad(new NormalRoad(v10));
        route3.addRoad(middle3);

        RouteRoad route4 = new RouteRoad(middle2, middle3);
        route4.addRoad(new NormalRoad(v7));
        route4.addRoad(new NormalRoad(v8));
        route4.addRoad(middle3);

        RouteRoad route5 = new RouteRoad(middle3, end);
        route5.addRoad(new NormalRoad(v11));
        route5.addRoad(end);

        RouteRoad route6 = new RouteRoad(middle2, end);
        IntStream.range(0, 8).forEach(i -> route6.addRoad(new NormalRoad(roads[i])));
        route6.addRoad(end);

        expectedRoutes.add(route);
        expectedRoutes.add(route.invertRoute());
        expectedRoutes.add(route2);
        expectedRoutes.add(route2.invertRoute());
        expectedRoutes.add(route3);
        expectedRoutes.add(route3.invertRoute());
        expectedRoutes.add(route4);
        expectedRoutes.add(route4.invertRoute());
        expectedRoutes.add(route5);
        expectedRoutes.add(route5.invertRoute());
        expectedRoutes.add(route6);
        expectedRoutes.add(route6.invertRoute());

        assertEquals(expectedNodes, nodes);
        assertEquals(expectedRoutes, routes);
    }

    /**
     * 6. Node 2 Node Multiple nodes and choices (v2)
     */
    @Test
    void testRoadGraph6() {
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
                54, 50,
                55, 50,
                56, 50,
                56, 49,
                56, 48,
                55, 48,
                55, 47,
                55, 46,
                53, 51,
                55, 51,
                49, 48,
                48, 48,
                50, 47,
                54, 45,
                49, 50,
                57, 50,
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

        Set<RoadNode> nodes = roadGraph.getNodes();
        Set<RouteRoad> routes = roadGraph.getRoutes();

        Set<RoadNode> expectedNodes = new HashSet<>();
        Set<RouteRoad> expectedRoutes = new TreeSet<>();

        RoadNode start = new RoadNode(v1);
        expectedNodes.add(start);
        RoadNode middle1 = new RoadNode(v4);
        expectedNodes.add(middle1);
        RoadNode middle2 = new RoadNode(v6);
        expectedNodes.add(middle2);
        RoadNode middle3 = new RoadNode(v9);
        expectedNodes.add(middle3);
        RoadNode middle4 = new RoadNode(roads[1]);
        expectedNodes.add(middle4);
        RoadNode middle5 = new RoadNode(roads[2]);
        expectedNodes.add(middle5);
        RoadNode end = new RoadNode(v13);
        expectedNodes.add(end);

        RouteRoad route = new RouteRoad(start, middle2);
        route.addRoad(new NormalRoad(v5));
        route.addRoad(middle2);

        RouteRoad route2 = new RouteRoad(start, middle1);
        route2.addRoad(new NormalRoad(v2));
        route2.addRoad(new NormalRoad(v3));
        route2.addRoad(middle1);

        RouteRoad route3 = new RouteRoad(middle1, middle3);
        route3.addRoad(new NormalRoad(v10));
        route3.addRoad(middle3);

        RouteRoad route4 = new RouteRoad(middle2, middle3);
        route4.addRoad(new NormalRoad(v7));
        route4.addRoad(new NormalRoad(v8));
        route4.addRoad(middle3);

        RouteRoad route5 = new RouteRoad(middle3, end);
        route5.addRoad(new NormalRoad(v11));
        route5.addRoad(new NormalRoad(v12));
        route5.addRoad(end);

        RouteRoad route6 = new RouteRoad(middle1, middle4);
        route6.addRoad(new NormalRoad(roads[0]));
        route6.addRoad(middle4);

        RouteRoad route7 = new RouteRoad(middle4, middle5);
        route7.addRoad(middle5);

        RouteRoad route8 = new RouteRoad(middle5, end);
        IntStream.range(3, 8).forEach(i -> route8.addRoad(new NormalRoad(roads[i])));
        route8.addRoad(end);

        expectedRoutes.add(route);
        expectedRoutes.add(route.invertRoute());
        expectedRoutes.add(route2);
        expectedRoutes.add(route2.invertRoute());
        expectedRoutes.add(route3);
        expectedRoutes.add(route3.invertRoute());
        expectedRoutes.add(route4);
        expectedRoutes.add(route4.invertRoute());
        expectedRoutes.add(route5);
        expectedRoutes.add(route5.invertRoute());
        expectedRoutes.add(route6);
        expectedRoutes.add(route6.invertRoute());
        expectedRoutes.add(route7);
        expectedRoutes.add(route7.invertRoute());
        expectedRoutes.add(route8);
        expectedRoutes.add(route8.invertRoute());

        assertEquals(expectedNodes, nodes);
        assertEquals(expectedRoutes, routes);
    }

    /**
     * 7. Road 2 Node through Node
     */
    @Test
    void testRoadGraph7() {
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

        Set<RoadNode> nodes = roadGraph.getNodes();
        Set<RouteRoad> routes = roadGraph.getRoutes();

        Set<RoadNode> expectedNodes = new HashSet<>();
        Set<RouteRoad> expectedRoutes = new TreeSet<>();

        RoadNode start = new RoadNode(v2);
        expectedNodes.add(start);
        RoadNode end = new RoadNode(v5);
        expectedNodes.add(end);

        RouteRoad route = new RouteRoad(start, end);
        route.addRoad(new NormalRoad(v3));
        route.addRoad(new NormalRoad(v4));
        route.addRoad(end);

        expectedRoutes.add(route);
        expectedRoutes.add(route.invertRoute());

        assertEquals(expectedNodes, nodes);
        assertEquals(expectedRoutes, routes);
    }

    /**
     * 8. Road 2 Node Choice
     */
    @Test
    void testRoadGraph8() {
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
                50, 48,
                50, 49,
                50, 47,
                50, 46,
                51, 46,
                52, 46,
                53, 46,
                53, 47,
                54, 50,
                51, 48,
                54, 48,
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

        Set<RoadNode> nodes = roadGraph.getNodes();
        Set<RouteRoad> routes = roadGraph.getRoutes();

        Set<RoadNode> expectedNodes = new HashSet<>();
        Set<RouteRoad> expectedRoutes = new TreeSet<>();

        RoadNode middle1 = new RoadNode(v4);
        expectedNodes.add(middle1);
        RoadNode middle2 = new RoadNode(roads[0]);
        expectedNodes.add(middle2);
        RoadNode end = new RoadNode(v6);
        expectedNodes.add(end);

        RouteRoad route = new RouteRoad(middle1, middle2);
        route.addRoad(new NormalRoad(v3));
        route.addRoad(new NormalRoad(v2));
        route.addRoad(new NormalRoad(v1));
        route.addRoad(new NormalRoad(roads[1]));
        route.addRoad(middle2);

        RouteRoad route2 = new RouteRoad(middle1, end);
        route2.addRoad(new NormalRoad(v5));
        route2.addRoad(end);

        RouteRoad route3 = new RouteRoad(middle2, end);
        IntStream.range(2, 8).forEach(i -> route3.addRoad(new NormalRoad(roads[i])));
        route3.addRoad(end);

        expectedRoutes.add(route2);
        expectedRoutes.add(route2.invertRoute());
        expectedRoutes.add(route3);
        expectedRoutes.add(route3.invertRoute());
        expectedRoutes.add(route);
        expectedRoutes.add(route.invertRoute());

        assertEquals(expectedNodes, nodes);
        assertEquals(expectedRoutes, routes);
    }

    /**
     * 9. Road 2 Road
     */
    @Test
    void testRoadGraph9() {
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

        Set<RoadNode> nodes = roadGraph.getNodes();
        Set<RouteRoad> routes = roadGraph.getRoutes();

        Set<RoadNode> expectedNodes = new HashSet<>();
        Set<RouteRoad> expectedRoutes = new TreeSet<>();

        assertEquals(expectedNodes, nodes);
        assertEquals(expectedRoutes, routes);
    }

    /**
     * 10. Road 2 Node (both ways)
     */
    @Test
    void testRoadGraph10() {
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

        RoadGraph roadGraph = scene.getRoadGraph();

        Set<RoadNode> nodes = roadGraph.getNodes();
        Set<RouteRoad> routes = roadGraph.getRoutes();

        Set<RoadNode> expectedNodes = new HashSet<>();
        Set<RouteRoad> expectedRoutes = new TreeSet<>();

        RoadNode node = new RoadNode(v4);
        expectedNodes.add(node);

        assertEquals(expectedNodes, nodes);
        assertEquals(expectedRoutes, routes);
    }

    /**
     * 11. Road 2 Road (ignore nodes)
     */
    @Test
    void testRoadGraph11() {
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

        Set<RoadNode> nodes = roadGraph.getNodes();
        Set<RouteRoad> routes = roadGraph.getRoutes();

        Set<RoadNode> expectedNodes = new HashSet<>();
        Set<RouteRoad> expectedRoutes = new TreeSet<>();

        RoadNode firstNode = new RoadNode(v2);
        expectedNodes.add(firstNode);
        RoadNode lastNode = new RoadNode(v6);
        expectedNodes.add(lastNode);

        RouteRoad route = new RouteRoad(firstNode, lastNode);
        route.addRoad(new NormalRoad(v3));
        route.addRoad(new NormalRoad(v4));
        route.addRoad(new NormalRoad(v5));
        route.addRoad(lastNode);

        expectedRoutes.add(route);
        expectedRoutes.add(route.invertRoute());

        assertEquals(expectedNodes, nodes);
        assertEquals(expectedRoutes, routes);
    }

    /**
     * 12. Road 2 Road through node
     */
    @Test
    void testRoadGraph12() {
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

        Set<RoadNode> nodes = roadGraph.getNodes();
        Set<RouteRoad> routes = roadGraph.getRoutes();

        Set<RoadNode> expectedNodes = new HashSet<>();
        Set<RouteRoad> expectedRoutes = new TreeSet<>();

        RoadNode node = new RoadNode(v4);
        expectedNodes.add(node);

        assertEquals(expectedNodes, nodes);
        assertEquals(expectedRoutes, routes);
    }

    /**
     * 13. Road 2 Road (disconnected)
     */
    @Test
    void testRoadGraph13() {
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

        Set<RoadNode> nodes = roadGraph.getNodes();
        Set<RouteRoad> routes = roadGraph.getRoutes();

        Set<RoadNode> expectedNodes = new HashSet<>();
        Set<RouteRoad> expectedRoutes = new TreeSet<>();

        assertEquals(expectedNodes, nodes);
        assertEquals(expectedRoutes, routes);
    }

    /**
     * 14. Road 2 Node (disconnected)
     */
    @Test
    void testRoadGraph14() {
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

        Set<RoadNode> nodes = roadGraph.getNodes();
        Set<RouteRoad> routes = roadGraph.getRoutes();

        Set<RoadNode> expectedNodes = new HashSet<>();
        Set<RouteRoad> expectedRoutes = new TreeSet<>();

        RoadNode node = new RoadNode(v5);
        expectedNodes.add(node);

        assertEquals(expectedNodes, nodes);
        assertEquals(expectedRoutes, routes);
    }

    /**
     * 15. Node 2 Node (disconnected)
     */
    @Test
    void testRoadGraph15() {
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

        Set<RoadNode> nodes = roadGraph.getNodes();
        Set<RouteRoad> routes = roadGraph.getRoutes();

        Set<RoadNode> expectedNodes = new HashSet<>();
        Set<RouteRoad> expectedRoutes = new TreeSet<>();

        RoadNode firstNode = new RoadNode(v2);
        expectedNodes.add(firstNode);
        RoadNode lastNode = new RoadNode(v6);
        expectedNodes.add(lastNode);

        assertEquals(expectedNodes, nodes);
        assertEquals(expectedRoutes, routes);
    }

    /**
     * 16. Road 2 Road (close)
     */
    @Test
    void testRoadGraph16() {
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

        Set<RoadNode> nodes = roadGraph.getNodes();
        Set<RouteRoad> routes = roadGraph.getRoutes();

        Set<RoadNode> expectedNodes = new HashSet<>();
        Set<RouteRoad> expectedRoutes = new TreeSet<>();

        assertEquals(expectedNodes, nodes);
        assertEquals(expectedRoutes, routes);
    }

    /**
     * 17. Road 2 Node (close)
     */
    @Test
    void testRoadGraph17() {
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

        Set<RoadNode> nodes = roadGraph.getNodes();
        Set<RouteRoad> routes = roadGraph.getRoutes();

        Set<RoadNode> expectedNodes = new HashSet<>();
        Set<RouteRoad> expectedRoutes = new TreeSet<>();

        RoadNode node = new RoadNode(v2);
        expectedNodes.add(node);

        assertEquals(expectedNodes, nodes);
        assertEquals(expectedRoutes, routes);
    }

    /**
     * 18. Node 2 Node (close)
     */
    @Test
    void testRoadGraph18() {
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

        Set<RoadNode> nodes = roadGraph.getNodes();
        Set<RouteRoad> routes = roadGraph.getRoutes();

        Set<RoadNode> expectedNodes = new HashSet<>();
        Set<RouteRoad> expectedRoutes = new TreeSet<>();

        RoadNode firstNode = new RoadNode(v2);
        expectedNodes.add(firstNode);
        RoadNode lastNode = new RoadNode(v3);
        expectedNodes.add(lastNode);

        RouteRoad route = new RouteRoad(firstNode, lastNode);
        route.addRoad(lastNode);

        expectedRoutes.add(route);
        expectedRoutes.add(route.invertRoute());

        assertEquals(expectedNodes, nodes);
        assertEquals(expectedRoutes, routes);
    }

    /**
     * 19. Node 2 Node (close) Choice
     */
    @Test
    void testRoadGraph19() {
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

        Set<RoadNode> nodes = roadGraph.getNodes();
        Set<RouteRoad> routes = roadGraph.getRoutes();

        Set<RoadNode> expectedNodes = new HashSet<>();
        Set<RouteRoad> expectedRoutes = new TreeSet<>();

        RoadNode firstNode = new RoadNode(v2);
        expectedNodes.add(firstNode);
        RoadNode lastNode = new RoadNode(v3);
        expectedNodes.add(lastNode);

        RouteRoad route = new RouteRoad(firstNode, lastNode);
        route.addRoad(lastNode);

        RouteRoad route2 = new RouteRoad(firstNode, lastNode);
        route2.addRoad(new NormalRoad(v7));
        route2.addRoad(new NormalRoad(v8));
        route2.addRoad(lastNode);

        RouteRoad route3 = new RouteRoad(firstNode, lastNode);
        route3.addRoad(new NormalRoad(v5));
        route3.addRoad(new NormalRoad(v6));
        route3.addRoad(lastNode);

        expectedRoutes.add(route);
        expectedRoutes.add(route.invertRoute());

        expectedRoutes.add(route2);
        expectedRoutes.add(route2.invertRoute());

        expectedRoutes.add(route3);
        expectedRoutes.add(route3.invertRoute());

        assertEquals(expectedNodes, nodes);
        assertEquals(expectedRoutes, routes);
    }

    /**
     * 20. Road 2 Node (close) Choice
     */
    @Test
    void testRoadGraph20() {
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

        Set<RoadNode> nodes = roadGraph.getNodes();
        Set<RouteRoad> routes = roadGraph.getRoutes();

        Set<RoadNode> expectedNodes = new HashSet<>();
        Set<RouteRoad> expectedRoutes = new TreeSet<>();

        RoadNode node = new RoadNode(v2);
        expectedNodes.add(node);

        assertEquals(expectedNodes, nodes);
        assertEquals(expectedRoutes, routes);
    }

    /**
     * 21. Road 2 Road (close) Choice
     */
    @Test
    void testRoadGraph21() {
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

        Set<RoadNode> nodes = roadGraph.getNodes();
        Set<RouteRoad> routes = roadGraph.getRoutes();

        Set<RoadNode> expectedNodes = new HashSet<>();
        Set<RouteRoad> expectedRoutes = new TreeSet<>();

        assertEquals(expectedNodes, nodes);
        assertEquals(expectedRoutes, routes);
    }

    /**
     * 22. Road 2 Road (ignore Nodes) Choice
     */
    @Test
    void testRoadGraph22() {
        TerrainPosition v1 = new TerrainPosition(52, 50);
        TerrainPosition v2 = new TerrainPosition(53, 50);
        TerrainPosition v3 = new TerrainPosition(54, 50);


        /*
            v1 -> v2 -> v3
         */
        int[] roadPositions = new int[]{
                51, 50,
                55, 50,
                50, 50,
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

        Set<RoadNode> nodes = roadGraph.getNodes();
        Set<RouteRoad> routes = roadGraph.getRoutes();

        Set<RoadNode> expectedNodes = new HashSet<>();
        Set<RouteRoad> expectedRoutes = new TreeSet<>();

        RoadNode firstNode = new RoadNode(roads[0]);
        expectedNodes.add(firstNode);
        RoadNode lastNode = new RoadNode(roads[1]);
        expectedNodes.add(lastNode);

        RouteRoad route = new RouteRoad(firstNode, lastNode);
        route.addRoad(new NormalRoad(v1));
        route.addRoad(new NormalRoad(v2));
        route.addRoad(new NormalRoad(v3));
        route.addRoad(lastNode);

        RouteRoad route2 = new RouteRoad(firstNode, lastNode);
        IntStream.range(4, 11).forEach(i -> route2.addRoad(new NormalRoad(roads[i])));
        route2.addRoad(lastNode);

        expectedRoutes.add(route);
        expectedRoutes.add(route.invertRoute());

        expectedRoutes.add(route2);
        expectedRoutes.add(route2.invertRoute());

        assertEquals(expectedNodes, nodes);
        assertEquals(expectedRoutes, routes);
    }

    /**
     * 23. Road 2 Road (ignore Nodes) Choice (v2)
     */
    @Test
    void testRoadGraph23() {
        TerrainPosition v1 = new TerrainPosition(52, 48);
        TerrainPosition v2 = new TerrainPosition(53, 48);
        TerrainPosition v3 = new TerrainPosition(54, 48);


        /*
            v1 -> v2 -> v3
         */
        int[] roadPositions = new int[]{
                51, 50,
                51, 48,
                55, 50,
                55, 48,
                51, 49,
                55, 49,
                52, 50,
                53, 50,
                54, 50,
                v1.getX(), v1.getZ(),
                v2.getX(), v2.getZ(),
                v3.getX(), v3.getZ(),
                56, 48,
                50, 50,
                56, 50,
                50, 48
        };

        TerrainPosition[] roads = TerrainPosition.toPositionArray(roadPositions);

        GameObject.newInstances(DirtRoad.class, roads);

        RoadGraph roadGraph = scene.getRoadGraph();

        Set<RoadNode> nodes = roadGraph.getNodes();
        Set<RouteRoad> routes = roadGraph.getRoutes();

        Set<RoadNode> expectedNodes = new HashSet<>();
        Set<RouteRoad> expectedRoutes = new TreeSet<>();

        RoadNode firstNode = new RoadNode(roads[0]);
        expectedNodes.add(firstNode);
        RoadNode middle1 = new RoadNode(roads[1]);
        expectedNodes.add(middle1);
        RoadNode middle2 = new RoadNode(roads[2]);
        expectedNodes.add(middle2);
        RoadNode lastNode = new RoadNode(roads[3]);
        expectedNodes.add(lastNode);

        RouteRoad route = new RouteRoad(firstNode, middle1);
        route.addRoad(new NormalRoad(roads[4]));
        route.addRoad(middle1);

        RouteRoad route2 = new RouteRoad(firstNode, middle2);
        IntStream.range(6, 9).forEach(i -> route2.addRoad(new NormalRoad(roads[i])));
        route2.addRoad(middle2);

        RouteRoad route3 = new RouteRoad(middle1, lastNode);
        IntStream.range(9, 12).forEach(i -> route3.addRoad(new NormalRoad(roads[i])));
        route3.addRoad(lastNode);

        RouteRoad route4 = new RouteRoad(middle2, lastNode);
        route4.addRoad(new NormalRoad(roads[5]));
        route4.addRoad(lastNode);

        expectedRoutes.add(route);
        expectedRoutes.add(route.invertRoute());

        expectedRoutes.add(route2);
        expectedRoutes.add(route2.invertRoute());

        expectedRoutes.add(route3);
        expectedRoutes.add(route3.invertRoute());

        expectedRoutes.add(route4);
        expectedRoutes.add(route4.invertRoute());

        assertEquals(expectedNodes, nodes);
        assertEquals(expectedRoutes, routes);
    }

    /**
     * 24. Road 2 Road (loop)
     */
    @Test
    void testRoadGraph24() {
        TerrainPosition v1 = new TerrainPosition(50, 50);
        TerrainPosition v2 = new TerrainPosition(51, 50);
        TerrainPosition v3 = new TerrainPosition(52, 50);
        TerrainPosition v4 = new TerrainPosition(53, 50);
        TerrainPosition v5 = new TerrainPosition(54, 50);


        /*
            v1 -> v2 -> v3 -> v4 -> v5
         */
        int[] roadPositions = new int[]{
                50, 48,
                54, 48,
                50, 49,
                51, 48,
                52, 48,
                53, 48,
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

        Set<RoadNode> nodes = roadGraph.getNodes();
        Set<RouteRoad> routes = roadGraph.getRoutes();

        Set<RoadNode> expectedNodes = new HashSet<>();
        Set<RouteRoad> expectedRoutes = new TreeSet<>();

        assertEquals(expectedNodes, nodes);
        assertEquals(expectedRoutes, routes);
    }

    /**
     * 25. Road 2 Road (through node) x 2
     */
    @Test
    void testRoadGraph25() {
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

        Set<RoadNode> nodes = roadGraph.getNodes();
        Set<RouteRoad> routes = roadGraph.getRoutes();

        Set<RoadNode> expectedNodes = new HashSet<>();
        Set<RouteRoad> expectedRoutes = new TreeSet<>();

        RoadNode firstNode = new RoadNode(v3);
        expectedNodes.add(firstNode);
        RoadNode lastNode = new RoadNode(v8);
        expectedNodes.add(lastNode);

        assertEquals(expectedNodes, nodes);
        assertEquals(expectedRoutes, routes);
    }

    /**
     * 26. Road 2 Road x 2
     */
    @Test
    void testRoadGraph26() {
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

        Set<RoadNode> nodes = roadGraph.getNodes();
        Set<RouteRoad> routes = roadGraph.getRoutes();

        Set<RoadNode> expectedNodes = new HashSet<>();
        Set<RouteRoad> expectedRoutes = new TreeSet<>();

        assertEquals(expectedNodes, nodes);
        assertEquals(expectedRoutes, routes);
    }

    /**
     * 27. Node 2 Road x 2
     */
    @Test
    void testRoadGraph27() {
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

        Set<RoadNode> nodes = roadGraph.getNodes();
        Set<RouteRoad> routes = roadGraph.getRoutes();

        Set<RoadNode> expectedNodes = new HashSet<>();
        Set<RouteRoad> expectedRoutes = new TreeSet<>();

        RoadNode firstNode = new RoadNode(v1);
        expectedNodes.add(firstNode);
        RoadNode lastNode = new RoadNode(v4);
        expectedNodes.add(lastNode);

        assertEquals(expectedNodes, nodes);
        assertEquals(expectedRoutes, routes);
    }

    /**
     * 28. Node 2 Node x2
     */
    @Test
    void testRoadGraph28() {
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

        Set<RoadNode> nodes = roadGraph.getNodes();
        Set<RouteRoad> routes = roadGraph.getRoutes();

        Set<RoadNode> expectedNodes = new HashSet<>();
        Set<RouteRoad> expectedRoutes = new TreeSet<>();

        RoadNode firstNode = new RoadNode(v1);
        expectedNodes.add(firstNode);
        RoadNode middle1 = new RoadNode(v3);
        expectedNodes.add(middle1);
        RoadNode middle2 = new RoadNode(v4);
        expectedNodes.add(middle2);
        RoadNode lastNode = new RoadNode(v6);
        expectedNodes.add(lastNode);

        RouteRoad route = new RouteRoad(firstNode, middle1);
        route.addRoad(new NormalRoad(v2));
        route.addRoad(middle1);

        RouteRoad route2 = new RouteRoad(middle2, lastNode);
        route2.addRoad(new NormalRoad(v5));
        route2.addRoad(lastNode);

        expectedRoutes.add(route);
        expectedRoutes.add(route.invertRoute());

        expectedRoutes.add(route2);
        expectedRoutes.add(route2.invertRoute());

        assertEquals(expectedNodes, nodes);
        assertEquals(expectedRoutes, routes);
    }

    /**
     * 29. Road 2 Road (choice found in practice)
     */
    @Test
    void testRoadGraph29() {
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

        Set<RoadNode> nodes = roadGraph.getNodes();
        Set<RouteRoad> routes = roadGraph.getRoutes();

        Set<RoadNode> expectedNodes = new HashSet<>();
        Set<RouteRoad> expectedRoutes = new TreeSet<>();

        RoadNode firstNode = new RoadNode(v3);
        expectedNodes.add(firstNode);
        RoadNode middle1 = new RoadNode(v4);
        expectedNodes.add(middle1);
        RoadNode middle2 = new RoadNode(v5);
        expectedNodes.add(middle2);
        RoadNode lastNode = new RoadNode(roads[1]);
        expectedNodes.add(lastNode);

        RouteRoad route = new RouteRoad(firstNode, middle1);
        route.addRoad(middle1);

        RouteRoad route2 = new RouteRoad(middle1, middle2);
        route2.addRoad(middle2);

        RouteRoad route3 = new RouteRoad(middle1, lastNode);
        route3.addRoad(lastNode);

        RouteRoad route4 = new RouteRoad(firstNode, lastNode);
        route4.addRoad(new NormalRoad(roads[0]));
        route4.addRoad(lastNode);

        RouteRoad route5 = new RouteRoad(middle2, lastNode);
        route5.addRoad(new NormalRoad(roads[2]));
        route5.addRoad(lastNode);

        expectedRoutes.add(route);
        expectedRoutes.add(route.invertRoute());

        expectedRoutes.add(route2);
        expectedRoutes.add(route2.invertRoute());

        expectedRoutes.add(route3);
        expectedRoutes.add(route3.invertRoute());

        expectedRoutes.add(route4);
        expectedRoutes.add(route4.invertRoute());

        expectedRoutes.add(route5);
        expectedRoutes.add(route5.invertRoute());

        assertEquals(expectedNodes, nodes);
        assertEquals(expectedRoutes, routes);
    }

    /**
     * 29b. Road 2 Road (choice found in practice)
     */
    @Test
    void testRoadGraph29b() {
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

        Set<RoadNode> nodes = roadGraph.getNodes();
        Set<RouteRoad> routes = roadGraph.getRoutes();

        Set<RoadNode> expectedNodes = new HashSet<>();
        Set<RouteRoad> expectedRoutes = new TreeSet<>();

        expectedNodes.add(new RoadNode(v5));

        assertEquals(expectedNodes, nodes);
        assertEquals(expectedRoutes, routes);
    }

    /**
     * 30. Road 2 Road (=)
     */
    @Test
    void testRoadGraph30() {
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

        Set<RoadNode> nodes = roadGraph.getNodes();
        Set<RouteRoad> routes = roadGraph.getRoutes();

        Set<RoadNode> expectedNodes = new HashSet<>();
        Set<RouteRoad> expectedRoutes = new TreeSet<>();

        assertEquals(expectedNodes, nodes);
        assertEquals(expectedRoutes, routes);
    }

    /**
     * 31. Node 2 Node (=)
     */
    @Test
    void testRoadGraph31() {
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

        Set<RoadNode> nodes = roadGraph.getNodes();
        Set<RouteRoad> routes = roadGraph.getRoutes();

        Set<RoadNode> expectedNodes = new HashSet<>();
        Set<RouteRoad> expectedRoutes = new TreeSet<>();

        expectedNodes.add(new RoadNode(v2));

        assertEquals(expectedNodes, nodes);
        assertEquals(expectedRoutes, routes);
    }

    /**
     * 32. Found in practice
     */
    @Test
    void testRoadGraph32() {
        TerrainPosition[] positions = new TerrainPosition[15];
        TerrainPosition[] positions2 = new TerrainPosition[14];
        for (int i = 55; i < 70; i++)
            positions[i - 55] = new TerrainPosition(50, i);
        for (int i = 50; i < 64; i++)
            positions2[i - 50] = new TerrainPosition(i, 70);

        GameObject.newInstances(DirtRoad.class, positions);
        GameObject.newInstance(DirtRoad.class, new TerrainPosition(51, 55));
        GameObject.newInstance(DirtRoad.class, new TerrainPosition(49, 55));
        GameObject.newInstance(DirtRoad.class, new TerrainPosition(51, 63));
        GameObject.newInstance(DirtRoad.class, new TerrainPosition(62, 71));

        List<TerrainPosition> pos2 = Arrays.asList(positions2);
        Collections.reverse(pos2);
        positions2 = pos2.toArray(new TerrainPosition[0]);
        GameObject.newInstances(DirtRoad.class, positions2);

        RoadGraph roadGraph = scene.getRoadGraph();

        Set<RoadNode> nodes = roadGraph.getNodes();
        Set<RouteRoad> routes = roadGraph.getRoutes();

        Set<RoadNode> expectedNodes = new HashSet<>();
        Set<RouteRoad> expectedRoutes = new TreeSet<>();

        RoadNode node1 = new RoadNode(new TerrainPosition(50, 63));
        RoadNode node2 = new RoadNode(new TerrainPosition(62, 70));
        RoadNode node3 = new RoadNode(new TerrainPosition(50, 55));
        expectedNodes.add(node1);
        expectedNodes.add(node2);
        expectedNodes.add(node3);

        RouteRoad routeRoad1 = new RouteRoad(node3, node1);
        Arrays.stream(positions, 0, 8).forEach(p -> routeRoad1.addRoad(new NormalRoad(p)));
        routeRoad1.addRoad(node1);
        expectedRoutes.add(routeRoad1);
        expectedRoutes.add(routeRoad1.invertRoute());

        RouteRoad routeRoad2 = new RouteRoad(node2, node1);
        Arrays.stream(positions2, 1, 14).forEach(p -> routeRoad2.addRoad(new NormalRoad(p)));
        List<TerrainPosition> pos = Arrays.asList(positions);
        Collections.reverse(pos);
        Arrays.stream(positions, 0, 6).forEach(p -> routeRoad2.addRoad(new NormalRoad(p)));
        routeRoad2.addRoad(node1);
        expectedRoutes.add(routeRoad2);
        expectedRoutes.add(routeRoad2.invertRoute());

        assertEquals(expectedNodes, nodes);
        assertEquals(expectedRoutes, routes);
    }
}