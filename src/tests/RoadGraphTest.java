package tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.lwjgl.glfw.GLFW.glfwInit;

import display.Display;
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
import pathfinding.NodeConnection;
import pathfinding.NodeRoad;
import pathfinding.NormalRoad;
import pathfinding.RoadGraph;
import renderEngine.PathRenderer;
import scene.Scene;
import scene.gameObjects.DirtRoad;
import scene.gameObjects.GameObject;
import terrain.TerrainPosition;

public class RoadGraphTest {

    private static final Scene scene = Scene.getInstance();

    @BeforeAll
    public static void init() {
        glfwInit();
        Display.createDisplayForTests();
        PathRenderer.getInstance();
    }

    @BeforeEach
    public void resetGameObjects() {
        scene.resetObjects();
        scene.updateBuildingRequirements();
        scene.resetRoadGraph();
    }

    @Test
    void testRoadGraph1() {
        TerrainPosition v1 = new TerrainPosition(50, 0, 50);
        TerrainPosition v2 = new TerrainPosition(51, 0, 50);
        TerrainPosition v3 = new TerrainPosition(52, 0, 50);
        TerrainPosition v4 = new TerrainPosition(53, 0, 50);

        // v1 -> v2 -> v3 -> v4
        GameObject.newInstance(DirtRoad.class, new TerrainPosition(50, 0, 49));
        GameObject.newInstance(DirtRoad.class, new TerrainPosition(50, 0, 51));
        GameObject.newInstance(DirtRoad.class, new TerrainPosition(53, 0, 49));
        GameObject.newInstance(DirtRoad.class, new TerrainPosition(53, 0, 51));
        GameObject.newInstance(DirtRoad.class, v1);
        GameObject.newInstance(DirtRoad.class, v2);
        GameObject.newInstance(DirtRoad.class, v3);
        GameObject.newInstance(DirtRoad.class, v4);

        RoadGraph roadGraph = scene.getRoadGraphCopy();

        Set<NodeRoad> nodes = roadGraph.getNodes();
        Set<NodeConnection> nodeConnections = roadGraph.getNodeConnections();

        Set<NodeRoad> expectedNodes = new HashSet<>();
        Set<NodeConnection> expectedPaths = new TreeSet<>();

        NodeRoad start = new NodeRoad(v1);
        expectedNodes.add(start);
        NodeRoad end = new NodeRoad(v4);
        expectedNodes.add(end);

        NodeConnection nodeConnection = new NodeConnection(start, end);
        nodeConnection.addRoad(new NormalRoad(v2));
        nodeConnection.addRoad(new NormalRoad(v3));
        nodeConnection.addRoad(end);

        expectedPaths.add(nodeConnection);
        expectedPaths.add(nodeConnection.invert());

        assertEquals(expectedNodes, nodes);
        assertEquals(expectedPaths, nodeConnections);
    }

    /**
     * 2. Node 2 Node through Node
     */
    @Test
    void testRoadGraph2() {
        TerrainPosition v1 = new TerrainPosition(50, 0, 50);
        TerrainPosition v2 = new TerrainPosition(51, 0, 50);
        TerrainPosition v3 = new TerrainPosition(52, 0, 50);
        TerrainPosition v4 = new TerrainPosition(53, 0, 50);
        TerrainPosition v5 = new TerrainPosition(54, 0, 50);

        // v1 -> v2 -> v3 -> v4 -> v5
        GameObject.newInstance(DirtRoad.class, new TerrainPosition(49, 0, 50));
        GameObject.newInstance(DirtRoad.class, new TerrainPosition(50, 0, 49));
        GameObject.newInstance(DirtRoad.class, new TerrainPosition(52, 0, 49));
        GameObject.newInstance(DirtRoad.class, new TerrainPosition(54, 0, 49));
        GameObject.newInstance(DirtRoad.class, new TerrainPosition(55, 0, 50));
        GameObject.newInstance(DirtRoad.class, v1);
        GameObject.newInstance(DirtRoad.class, v2);
        GameObject.newInstance(DirtRoad.class, v3);
        GameObject.newInstance(DirtRoad.class, v4);
        GameObject.newInstance(DirtRoad.class, v5);

        RoadGraph roadGraph = scene.getRoadGraphCopy();

        Set<NodeRoad> nodes = roadGraph.getNodes();
        Set<NodeConnection> nodeConnections = roadGraph.getNodeConnections();

        Set<NodeRoad> expectedNodes = new HashSet<>();
        Set<NodeConnection> expectedPaths = new TreeSet<>();

        NodeRoad start = new NodeRoad(v1);
        expectedNodes.add(start);
        NodeRoad middle = new NodeRoad(v3);
        expectedNodes.add(middle);
        NodeRoad end = new NodeRoad(v5);
        expectedNodes.add(end);

        NodeConnection nodeConnection = new NodeConnection(start, middle);
        nodeConnection.addRoad(new NormalRoad(v2));
        nodeConnection.addRoad(middle);

        NodeConnection nodeConnection2 = new NodeConnection(middle, end);
        nodeConnection2.addRoad(new NormalRoad(v4));
        nodeConnection2.addRoad(end);

        expectedPaths.add(nodeConnection);
        expectedPaths.add(nodeConnection.invert());
        expectedPaths.add(nodeConnection2);
        expectedPaths.add(nodeConnection2.invert());

        assertEquals(expectedNodes, nodes);
        assertEquals(expectedPaths, nodeConnections);
    }

    /**
     * 3. Node 2 Node Choice
     */
    @Test
    void testRoadGraph3() {
        TerrainPosition v1 = new TerrainPosition(50, 0, 50);
        TerrainPosition v2 = new TerrainPosition(50, 0, 49);
        TerrainPosition v3 = new TerrainPosition(50, 0, 48);
        TerrainPosition v4 = new TerrainPosition(51, 0, 48);
        TerrainPosition v5 = new TerrainPosition(52, 0, 48);

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

        RoadGraph roadGraph = scene.getRoadGraphCopy();

        Set<NodeRoad> nodes = roadGraph.getNodes();
        Set<NodeConnection> nodeConnections = roadGraph.getNodeConnections();

        Set<NodeRoad> expectedNodes = new HashSet<>();
        Set<NodeConnection> expectedPaths = new TreeSet<>();

        NodeRoad start = new NodeRoad(v1);
        expectedNodes.add(start);
        NodeRoad end = new NodeRoad(v5);
        expectedNodes.add(end);

        NodeConnection nodeConnection = new NodeConnection(start, end);
        nodeConnection.addRoad(new NormalRoad(v2));
        nodeConnection.addRoad(new NormalRoad(v3));
        nodeConnection.addRoad(new NormalRoad(v4));
        nodeConnection.addRoad(end);

        NodeConnection nodeConnection2 = new NodeConnection(start, end);
        IntStream.range(0, 9).forEach(i -> nodeConnection2.addRoad(new NormalRoad(roads[i])));
        nodeConnection2.addRoad(end);

        expectedPaths.add(nodeConnection);
        expectedPaths.add(nodeConnection.invert());
        expectedPaths.add(nodeConnection2);
        expectedPaths.add(nodeConnection2.invert());

        assertEquals(expectedNodes, nodes);
        assertEquals(expectedPaths, nodeConnections);
    }

    /**
     * 4. Node 2 Node through 2 Nodes
     */
    @Test
    void testRoadGraph4() {
        TerrainPosition v1 = new TerrainPosition(50, 0, 50);
        TerrainPosition v2 = new TerrainPosition(51, 0, 50);
        TerrainPosition v3 = new TerrainPosition(52, 0, 50);
        TerrainPosition v4 = new TerrainPosition(53, 0, 50);
        TerrainPosition v5 = new TerrainPosition(54, 0, 50);
        TerrainPosition v6 = new TerrainPosition(55, 0, 50);
        TerrainPosition v7 = new TerrainPosition(55, 0, 49);
        TerrainPosition v8 = new TerrainPosition(56, 0, 49);
        TerrainPosition v9 = new TerrainPosition(57, 0, 49);

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

        RoadGraph roadGraph = scene.getRoadGraphCopy();

        Set<NodeRoad> nodes = roadGraph.getNodes();
        Set<NodeConnection> nodeConnections = roadGraph.getNodeConnections();

        Set<NodeRoad> expectedNodes = new HashSet<>();
        Set<NodeConnection> expectedPaths = new TreeSet<>();

        NodeRoad start = new NodeRoad(v1);
        expectedNodes.add(start);
        NodeRoad middle1 = new NodeRoad(v4);
        expectedNodes.add(middle1);
        NodeRoad middle2 = new NodeRoad(v7);
        expectedNodes.add(middle2);
        NodeRoad end = new NodeRoad(v9);
        expectedNodes.add(end);

        NodeConnection nodeConnection = new NodeConnection(start, middle1);
        nodeConnection.addRoad(new NormalRoad(v2));
        nodeConnection.addRoad(new NormalRoad(v3));
        nodeConnection.addRoad(middle1);

        NodeConnection nodeConnection2 = new NodeConnection(middle1, middle2);
        nodeConnection2.addRoad(new NormalRoad(v5));
        nodeConnection2.addRoad(new NormalRoad(v6));
        nodeConnection2.addRoad(middle2);

        NodeConnection nodeConnection3 = new NodeConnection(middle2, end);
        nodeConnection3.addRoad(new NormalRoad(v8));
        nodeConnection3.addRoad(end);

        expectedPaths.add(nodeConnection);
        expectedPaths.add(nodeConnection.invert());
        expectedPaths.add(nodeConnection2);
        expectedPaths.add(nodeConnection2.invert());
        expectedPaths.add(nodeConnection3);
        expectedPaths.add(nodeConnection3.invert());

        assertEquals(expectedNodes, nodes);
        assertEquals(expectedPaths, nodeConnections);
    }

    /**
     * 5. Node 2 Node Multiple nodes and choices (v1)
     */
    @Test
    void testRoadGraph5() {
        TerrainPosition v1 = new TerrainPosition(50, 0, 50);
        TerrainPosition v2 = new TerrainPosition(51, 0, 50);
        TerrainPosition v3 = new TerrainPosition(52, 0, 50);
        TerrainPosition v4 = new TerrainPosition(53, 0, 50);
        TerrainPosition v5 = new TerrainPosition(50, 0, 49);
        TerrainPosition v6 = new TerrainPosition(50, 0, 48);
        TerrainPosition v7 = new TerrainPosition(51, 0, 48);
        TerrainPosition v8 = new TerrainPosition(52, 0, 48);
        TerrainPosition v9 = new TerrainPosition(53, 0, 48);
        TerrainPosition v10 = new TerrainPosition(53, 0, 49);
        TerrainPosition v11 = new TerrainPosition(54, 0, 48);
        TerrainPosition v12 = new TerrainPosition(55, 0, 48);

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

        RoadGraph roadGraph = scene.getRoadGraphCopy();

        Set<NodeRoad> nodes = roadGraph.getNodes();
        Set<NodeConnection> nodeConnections = roadGraph.getNodeConnections();

        Set<NodeRoad> expectedNodes = new HashSet<>();
        Set<NodeConnection> expectedPaths = new TreeSet<>();

        NodeRoad start = new NodeRoad(v1);
        expectedNodes.add(start);
        NodeRoad middle1 = new NodeRoad(v4);
        expectedNodes.add(middle1);
        NodeRoad middle2 = new NodeRoad(v6);
        expectedNodes.add(middle2);
        NodeRoad middle3 = new NodeRoad(v9);
        expectedNodes.add(middle3);
        NodeRoad end = new NodeRoad(v12);
        expectedNodes.add(end);

        NodeConnection nodeConnection = new NodeConnection(start, middle2);
        nodeConnection.addRoad(new NormalRoad(v5));
        nodeConnection.addRoad(middle2);

        NodeConnection nodeConnection2 = new NodeConnection(start, middle1);
        nodeConnection2.addRoad(new NormalRoad(v2));
        nodeConnection2.addRoad(new NormalRoad(v3));
        nodeConnection2.addRoad(middle1);

        NodeConnection nodeConnection3 = new NodeConnection(middle1, middle3);
        nodeConnection3.addRoad(new NormalRoad(v10));
        nodeConnection3.addRoad(middle3);

        NodeConnection nodeConnection4 = new NodeConnection(middle2, middle3);
        nodeConnection4.addRoad(new NormalRoad(v7));
        nodeConnection4.addRoad(new NormalRoad(v8));
        nodeConnection4.addRoad(middle3);

        NodeConnection nodeConnection5 = new NodeConnection(middle3, end);
        nodeConnection5.addRoad(new NormalRoad(v11));
        nodeConnection5.addRoad(end);

        NodeConnection nodeConnection6 = new NodeConnection(middle2, end);
        IntStream.range(0, 8).forEach(i -> nodeConnection6.addRoad(new NormalRoad(roads[i])));
        nodeConnection6.addRoad(end);

        expectedPaths.add(nodeConnection);
        expectedPaths.add(nodeConnection.invert());
        expectedPaths.add(nodeConnection2);
        expectedPaths.add(nodeConnection2.invert());
        expectedPaths.add(nodeConnection3);
        expectedPaths.add(nodeConnection3.invert());
        expectedPaths.add(nodeConnection4);
        expectedPaths.add(nodeConnection4.invert());
        expectedPaths.add(nodeConnection5);
        expectedPaths.add(nodeConnection5.invert());
        expectedPaths.add(nodeConnection6);
        expectedPaths.add(nodeConnection6.invert());

        assertEquals(expectedNodes, nodes);
        assertEquals(expectedPaths, nodeConnections);
    }

    /**
     * 6. Node 2 Node Multiple nodes and choices (v2)
     */
    @Test
    void testRoadGraph6() {
        TerrainPosition v1 = new TerrainPosition(50, 0, 50);
        TerrainPosition v2 = new TerrainPosition(51, 0, 50);
        TerrainPosition v3 = new TerrainPosition(52, 0, 50);
        TerrainPosition v4 = new TerrainPosition(53, 0, 50);
        TerrainPosition v5 = new TerrainPosition(50, 0, 49);
        TerrainPosition v6 = new TerrainPosition(50, 0, 48);
        TerrainPosition v7 = new TerrainPosition(51, 0, 48);
        TerrainPosition v8 = new TerrainPosition(52, 0, 48);
        TerrainPosition v9 = new TerrainPosition(53, 0, 48);
        TerrainPosition v10 = new TerrainPosition(53, 0, 49);
        TerrainPosition v11 = new TerrainPosition(53, 0, 47);
        TerrainPosition v12 = new TerrainPosition(53, 0, 46);
        TerrainPosition v13 = new TerrainPosition(54, 0, 46);

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

        RoadGraph roadGraph = scene.getRoadGraphCopy();

        Set<NodeRoad> nodes = roadGraph.getNodes();
        Set<NodeConnection> nodeConnections = roadGraph.getNodeConnections();

        Set<NodeRoad> expectedNodes = new HashSet<>();
        Set<NodeConnection> expectedPaths = new TreeSet<>();

        NodeRoad start = new NodeRoad(v1);
        expectedNodes.add(start);
        NodeRoad middle1 = new NodeRoad(v4);
        expectedNodes.add(middle1);
        NodeRoad middle2 = new NodeRoad(v6);
        expectedNodes.add(middle2);
        NodeRoad middle3 = new NodeRoad(v9);
        expectedNodes.add(middle3);
        NodeRoad middle4 = new NodeRoad(roads[1]);
        expectedNodes.add(middle4);
        NodeRoad middle5 = new NodeRoad(roads[2]);
        expectedNodes.add(middle5);
        NodeRoad end = new NodeRoad(v13);
        expectedNodes.add(end);

        NodeConnection nodeConnection = new NodeConnection(start, middle2);
        nodeConnection.addRoad(new NormalRoad(v5));
        nodeConnection.addRoad(middle2);

        NodeConnection nodeConnection2 = new NodeConnection(start, middle1);
        nodeConnection2.addRoad(new NormalRoad(v2));
        nodeConnection2.addRoad(new NormalRoad(v3));
        nodeConnection2.addRoad(middle1);

        NodeConnection nodeConnection3 = new NodeConnection(middle1, middle3);
        nodeConnection3.addRoad(new NormalRoad(v10));
        nodeConnection3.addRoad(middle3);

        NodeConnection nodeConnection4 = new NodeConnection(middle2, middle3);
        nodeConnection4.addRoad(new NormalRoad(v7));
        nodeConnection4.addRoad(new NormalRoad(v8));
        nodeConnection4.addRoad(middle3);

        NodeConnection nodeConnection5 = new NodeConnection(middle3, end);
        nodeConnection5.addRoad(new NormalRoad(v11));
        nodeConnection5.addRoad(new NormalRoad(v12));
        nodeConnection5.addRoad(end);

        NodeConnection nodeConnection6 = new NodeConnection(middle1, middle4);
        nodeConnection6.addRoad(new NormalRoad(roads[0]));
        nodeConnection6.addRoad(middle4);

        NodeConnection nodeConnection7 = new NodeConnection(middle4, middle5);
        nodeConnection7.addRoad(middle5);

        NodeConnection nodeConnection8 = new NodeConnection(middle5, end);
        IntStream.range(3, 8).forEach(i -> nodeConnection8.addRoad(new NormalRoad(roads[i])));
        nodeConnection8.addRoad(end);

        expectedPaths.add(nodeConnection);
        expectedPaths.add(nodeConnection.invert());
        expectedPaths.add(nodeConnection2);
        expectedPaths.add(nodeConnection2.invert());
        expectedPaths.add(nodeConnection3);
        expectedPaths.add(nodeConnection3.invert());
        expectedPaths.add(nodeConnection4);
        expectedPaths.add(nodeConnection4.invert());
        expectedPaths.add(nodeConnection5);
        expectedPaths.add(nodeConnection5.invert());
        expectedPaths.add(nodeConnection6);
        expectedPaths.add(nodeConnection6.invert());
        expectedPaths.add(nodeConnection7);
        expectedPaths.add(nodeConnection7.invert());
        expectedPaths.add(nodeConnection8);
        expectedPaths.add(nodeConnection8.invert());

        assertEquals(expectedNodes, nodes);
        assertEquals(expectedPaths, nodeConnections);
    }

    /**
     * 7. Road 2 Node through Node
     */
    @Test
    void testRoadGraph7() {
        TerrainPosition v1 = new TerrainPosition(50, 0, 50);
        TerrainPosition v2 = new TerrainPosition(51, 0, 50);
        TerrainPosition v3 = new TerrainPosition(52, 0, 50);
        TerrainPosition v4 = new TerrainPosition(53, 0, 50);
        TerrainPosition v5 = new TerrainPosition(54, 0, 50);

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

        RoadGraph roadGraph = scene.getRoadGraphCopy();

        Set<NodeRoad> nodes = roadGraph.getNodes();
        Set<NodeConnection> nodeConnections = roadGraph.getNodeConnections();

        Set<NodeRoad> expectedNodes = new HashSet<>();
        Set<NodeConnection> expectedPaths = new TreeSet<>();

        NodeRoad start = new NodeRoad(v2);
        expectedNodes.add(start);
        NodeRoad end = new NodeRoad(v5);
        expectedNodes.add(end);

        NodeConnection nodeConnection = new NodeConnection(start, end);
        nodeConnection.addRoad(new NormalRoad(v3));
        nodeConnection.addRoad(new NormalRoad(v4));
        nodeConnection.addRoad(end);

        expectedPaths.add(nodeConnection);
        expectedPaths.add(nodeConnection.invert());

        assertEquals(expectedNodes, nodes);
        assertEquals(expectedPaths, nodeConnections);
    }

    /**
     * 8. Road 2 Node Choice
     */
    @Test
    void testRoadGraph8() {
        TerrainPosition v1 = new TerrainPosition(50, 0, 50);
        TerrainPosition v2 = new TerrainPosition(51, 0, 50);
        TerrainPosition v3 = new TerrainPosition(52, 0, 50);
        TerrainPosition v4 = new TerrainPosition(53, 0, 50);
        TerrainPosition v5 = new TerrainPosition(53, 0, 49);
        TerrainPosition v6 = new TerrainPosition(53, 0, 48);

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

        RoadGraph roadGraph = scene.getRoadGraphCopy();

        Set<NodeRoad> nodes = roadGraph.getNodes();
        Set<NodeConnection> nodeConnections = roadGraph.getNodeConnections();

        Set<NodeRoad> expectedNodes = new HashSet<>();
        Set<NodeConnection> expectedPaths = new TreeSet<>();

        NodeRoad middle1 = new NodeRoad(v4);
        expectedNodes.add(middle1);
        NodeRoad middle2 = new NodeRoad(roads[0]);
        expectedNodes.add(middle2);
        NodeRoad end = new NodeRoad(v6);
        expectedNodes.add(end);

        NodeConnection nodeConnection = new NodeConnection(middle1, middle2);
        nodeConnection.addRoad(new NormalRoad(v3));
        nodeConnection.addRoad(new NormalRoad(v2));
        nodeConnection.addRoad(new NormalRoad(v1));
        nodeConnection.addRoad(new NormalRoad(roads[1]));
        nodeConnection.addRoad(middle2);

        NodeConnection nodeConnection2 = new NodeConnection(middle1, end);
        nodeConnection2.addRoad(new NormalRoad(v5));
        nodeConnection2.addRoad(end);

        NodeConnection nodeConnection3 = new NodeConnection(middle2, end);
        IntStream.range(2, 8).forEach(i -> nodeConnection3.addRoad(new NormalRoad(roads[i])));
        nodeConnection3.addRoad(end);

        expectedPaths.add(nodeConnection2);
        expectedPaths.add(nodeConnection2.invert());
        expectedPaths.add(nodeConnection3);
        expectedPaths.add(nodeConnection3.invert());
        expectedPaths.add(nodeConnection);
        expectedPaths.add(nodeConnection.invert());

        assertEquals(expectedNodes, nodes);
        assertEquals(expectedPaths, nodeConnections);
    }

    /**
     * 9. Road 2 Road
     */
    @Test
    void testRoadGraph9() {
        TerrainPosition v1 = new TerrainPosition(50, 0, 50);
        TerrainPosition v2 = new TerrainPosition(51, 0, 50);
        TerrainPosition v3 = new TerrainPosition(52, 0, 50);
        TerrainPosition v4 = new TerrainPosition(53, 0, 50);
        TerrainPosition v5 = new TerrainPosition(54, 0, 50);

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

        RoadGraph roadGraph = scene.getRoadGraphCopy();

        Set<NodeRoad> nodes = roadGraph.getNodes();
        Set<NodeConnection> nodeConnections = roadGraph.getNodeConnections();

        Set<NodeRoad> expectedNodes = new HashSet<>();
        Set<NodeConnection> expectedPaths = new TreeSet<>();

        assertEquals(expectedNodes, nodes);
        assertEquals(expectedPaths, nodeConnections);
    }

    /**
     * 10. Road 2 Node (both ways)
     */
    @Test
    void testRoadGraph10() {
        TerrainPosition v1 = new TerrainPosition(50, 0, 50);
        TerrainPosition v2 = new TerrainPosition(51, 0, 50);
        TerrainPosition v3 = new TerrainPosition(52, 0, 50);
        TerrainPosition v4 = new TerrainPosition(53, 0, 50);
        TerrainPosition v5 = new TerrainPosition(54, 0, 50);
        TerrainPosition v6 = new TerrainPosition(53, 0, 49);

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

        RoadGraph roadGraph = scene.getRoadGraphCopy();

        Set<NodeRoad> nodes = roadGraph.getNodes();
        Set<NodeConnection> nodeConnections = roadGraph.getNodeConnections();

        Set<NodeRoad> expectedNodes = new HashSet<>();
        Set<NodeConnection> expectedPaths = new TreeSet<>();

        NodeRoad node = new NodeRoad(v4);
        expectedNodes.add(node);

        assertEquals(expectedNodes, nodes);
        assertEquals(expectedPaths, nodeConnections);
    }

    /**
     * 11. Road 2 Road (ignore nodes)
     */
    @Test
    void testRoadGraph11() {
        TerrainPosition v1 = new TerrainPosition(50, 0, 50);
        TerrainPosition v2 = new TerrainPosition(51, 0, 50);
        TerrainPosition v3 = new TerrainPosition(52, 0, 50);
        TerrainPosition v4 = new TerrainPosition(53, 0, 50);
        TerrainPosition v5 = new TerrainPosition(54, 0, 50);
        TerrainPosition v6 = new TerrainPosition(55, 0, 50);
        TerrainPosition v7 = new TerrainPosition(56, 0, 50);
        TerrainPosition v8 = new TerrainPosition(51, 0, 49);
        TerrainPosition v9 = new TerrainPosition(55, 0, 49);

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

        RoadGraph roadGraph = scene.getRoadGraphCopy();

        Set<NodeRoad> nodes = roadGraph.getNodes();
        Set<NodeConnection> nodeConnections = roadGraph.getNodeConnections();

        Set<NodeRoad> expectedNodes = new HashSet<>();
        Set<NodeConnection> expectedPaths = new TreeSet<>();

        NodeRoad firstNode = new NodeRoad(v2);
        expectedNodes.add(firstNode);
        NodeRoad lastNode = new NodeRoad(v6);
        expectedNodes.add(lastNode);

        NodeConnection nodeConnection = new NodeConnection(firstNode, lastNode);
        nodeConnection.addRoad(new NormalRoad(v3));
        nodeConnection.addRoad(new NormalRoad(v4));
        nodeConnection.addRoad(new NormalRoad(v5));
        nodeConnection.addRoad(lastNode);

        expectedPaths.add(nodeConnection);
        expectedPaths.add(nodeConnection.invert());

        assertEquals(expectedNodes, nodes);
        assertEquals(expectedPaths, nodeConnections);
    }

    /**
     * 12. Road 2 Road through node
     */
    @Test
    void testRoadGraph12() {
        TerrainPosition v1 = new TerrainPosition(50, 0, 50);
        TerrainPosition v2 = new TerrainPosition(51, 0, 50);
        TerrainPosition v3 = new TerrainPosition(52, 0, 50);
        TerrainPosition v4 = new TerrainPosition(53, 0, 50);
        TerrainPosition v5 = new TerrainPosition(54, 0, 50);
        TerrainPosition v6 = new TerrainPosition(55, 0, 50);
        TerrainPosition v7 = new TerrainPosition(50, 0, 49);
        TerrainPosition v8 = new TerrainPosition(53, 0, 49);

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

        RoadGraph roadGraph = scene.getRoadGraphCopy();

        Set<NodeRoad> nodes = roadGraph.getNodes();
        Set<NodeConnection> nodeConnections = roadGraph.getNodeConnections();

        Set<NodeRoad> expectedNodes = new HashSet<>();
        Set<NodeConnection> expectedPaths = new TreeSet<>();

        NodeRoad node = new NodeRoad(v4);
        expectedNodes.add(node);

        assertEquals(expectedNodes, nodes);
        assertEquals(expectedPaths, nodeConnections);
    }

    /**
     * 13. Road 2 Road (disconnected)
     */
    @Test
    void testRoadGraph13() {
        TerrainPosition v1 = new TerrainPosition(50, 0, 50);
        TerrainPosition v2 = new TerrainPosition(51, 0, 50);
        TerrainPosition v3 = new TerrainPosition(52, 0, 50);
        TerrainPosition v4 = new TerrainPosition(54, 0, 50);

        int[] roadPositions = new int[]{
                v1.getX(), v1.getZ(),
                v2.getX(), v2.getZ(),
                v3.getX(), v3.getZ(),
                v4.getX(), v4.getZ()
        };

        TerrainPosition[] roads = TerrainPosition.toPositionArray(roadPositions);

        GameObject.newInstances(DirtRoad.class, roads);

        RoadGraph roadGraph = scene.getRoadGraphCopy();

        Set<NodeRoad> nodes = roadGraph.getNodes();
        Set<NodeConnection> nodeConnections = roadGraph.getNodeConnections();

        Set<NodeRoad> expectedNodes = new HashSet<>();
        Set<NodeConnection> expectedPaths = new TreeSet<>();

        assertEquals(expectedNodes, nodes);
        assertEquals(expectedPaths, nodeConnections);
    }

    /**
     * 14. Road 2 Node (disconnected)
     */
    @Test
    void testRoadGraph14() {
        TerrainPosition v1 = new TerrainPosition(50, 0, 50);
        TerrainPosition v2 = new TerrainPosition(51, 0, 50);
        TerrainPosition v3 = new TerrainPosition(52, 0, 50);

        TerrainPosition v4 = new TerrainPosition(56, 0, 50);
        TerrainPosition v5 = new TerrainPosition(56, 0, 49);
        TerrainPosition v6 = new TerrainPosition(56, 0, 48);
        TerrainPosition v7 = new TerrainPosition(55, 0, 49);
        TerrainPosition v8 = new TerrainPosition(57, 0, 49);

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

        RoadGraph roadGraph = scene.getRoadGraphCopy();

        Set<NodeRoad> nodes = roadGraph.getNodes();
        Set<NodeConnection> nodeConnections = roadGraph.getNodeConnections();

        Set<NodeRoad> expectedNodes = new HashSet<>();
        Set<NodeConnection> expectedPaths = new TreeSet<>();

        NodeRoad node = new NodeRoad(v5);
        expectedNodes.add(node);

        assertEquals(expectedNodes, nodes);
        assertEquals(expectedPaths, nodeConnections);
    }

    /**
     * 15. Node 2 Node (disconnected)
     */
    @Test
    void testRoadGraph15() {
        TerrainPosition v1 = new TerrainPosition(50, 0, 50);
        TerrainPosition v2 = new TerrainPosition(51, 0, 50);
        TerrainPosition v3 = new TerrainPosition(52, 0, 50);
        TerrainPosition v4 = new TerrainPosition(51, 0, 49);

        TerrainPosition v5 = new TerrainPosition(55, 0, 50);
        TerrainPosition v6 = new TerrainPosition(56, 0, 50);
        TerrainPosition v7 = new TerrainPosition(57, 0, 50);
        TerrainPosition v8 = new TerrainPosition(56, 0, 49);

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

        RoadGraph roadGraph = scene.getRoadGraphCopy();

        Set<NodeRoad> nodes = roadGraph.getNodes();
        Set<NodeConnection> nodeConnections = roadGraph.getNodeConnections();

        Set<NodeRoad> expectedNodes = new HashSet<>();
        Set<NodeConnection> expectedPaths = new TreeSet<>();

        NodeRoad firstNode = new NodeRoad(v2);
        expectedNodes.add(firstNode);
        NodeRoad lastNode = new NodeRoad(v6);
        expectedNodes.add(lastNode);

        assertEquals(expectedNodes, nodes);
        assertEquals(expectedPaths, nodeConnections);
    }

    /**
     * 16. Road 2 Road (close)
     */
    @Test
    void testRoadGraph16() {
        TerrainPosition v1 = new TerrainPosition(50, 0, 50);
        TerrainPosition v2 = new TerrainPosition(51, 0, 50);
        TerrainPosition v3 = new TerrainPosition(52, 0, 50);
        TerrainPosition v4 = new TerrainPosition(52, 0, 49);


        int[] roadPositions = new int[]{
                v1.getX(), v1.getZ(),
                v2.getX(), v2.getZ(),
                v3.getX(), v3.getZ(),
                v4.getX(), v4.getZ()
        };

        TerrainPosition[] roads = TerrainPosition.toPositionArray(roadPositions);

        GameObject.newInstances(DirtRoad.class, roads);

        RoadGraph roadGraph = scene.getRoadGraphCopy();

        Set<NodeRoad> nodes = roadGraph.getNodes();
        Set<NodeConnection> nodeConnections = roadGraph.getNodeConnections();

        Set<NodeRoad> expectedNodes = new HashSet<>();
        Set<NodeConnection> expectedPaths = new TreeSet<>();

        assertEquals(expectedNodes, nodes);
        assertEquals(expectedPaths, nodeConnections);
    }

    /**
     * 17. Road 2 Node (close)
     */
    @Test
    void testRoadGraph17() {
        TerrainPosition v1 = new TerrainPosition(50, 0, 50);
        TerrainPosition v2 = new TerrainPosition(51, 0, 50);
        TerrainPosition v3 = new TerrainPosition(52, 0, 50);
        TerrainPosition v4 = new TerrainPosition(51, 0, 49);


        int[] roadPositions = new int[]{
                v1.getX(), v1.getZ(),
                v2.getX(), v2.getZ(),
                v3.getX(), v3.getZ(),
                v4.getX(), v4.getZ()
        };

        TerrainPosition[] roads = TerrainPosition.toPositionArray(roadPositions);

        GameObject.newInstances(DirtRoad.class, roads);

        RoadGraph roadGraph = scene.getRoadGraphCopy();

        Set<NodeRoad> nodes = roadGraph.getNodes();
        Set<NodeConnection> nodeConnections = roadGraph.getNodeConnections();

        Set<NodeRoad> expectedNodes = new HashSet<>();
        Set<NodeConnection> expectedPaths = new TreeSet<>();

        NodeRoad node = new NodeRoad(v2);
        expectedNodes.add(node);

        assertEquals(expectedNodes, nodes);
        assertEquals(expectedPaths, nodeConnections);
    }

    /**
     * 18. Node 2 Node (close)
     */
    @Test
    void testRoadGraph18() {
        TerrainPosition v1 = new TerrainPosition(50, 0, 50);
        TerrainPosition v2 = new TerrainPosition(51, 0, 50);
        TerrainPosition v3 = new TerrainPosition(52, 0, 50);
        TerrainPosition v4 = new TerrainPosition(53, 0, 50);
        TerrainPosition v5 = new TerrainPosition(51, 0, 51);
        TerrainPosition v6 = new TerrainPosition(52, 0, 49);


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

        RoadGraph roadGraph = scene.getRoadGraphCopy();

        Set<NodeRoad> nodes = roadGraph.getNodes();
        Set<NodeConnection> nodeConnections = roadGraph.getNodeConnections();

        Set<NodeRoad> expectedNodes = new HashSet<>();
        Set<NodeConnection> expectedPaths = new TreeSet<>();

        NodeRoad firstNode = new NodeRoad(v2);
        expectedNodes.add(firstNode);
        NodeRoad lastNode = new NodeRoad(v3);
        expectedNodes.add(lastNode);

        NodeConnection nodeConnection = new NodeConnection(firstNode, lastNode);
        nodeConnection.addRoad(lastNode);

        expectedPaths.add(nodeConnection);
        expectedPaths.add(nodeConnection.invert());

        assertEquals(expectedNodes, nodes);
        assertEquals(expectedPaths, nodeConnections);
    }

    /**
     * 19. Node 2 Node (close) Choice
     */
    @Test
    void testRoadGraph19() {
        TerrainPosition v1 = new TerrainPosition(50, 0, 50);
        TerrainPosition v2 = new TerrainPosition(51, 0, 50);
        TerrainPosition v3 = new TerrainPosition(52, 0, 50);
        TerrainPosition v4 = new TerrainPosition(53, 0, 50);

        TerrainPosition v5 = new TerrainPosition(51, 0, 51);
        TerrainPosition v6 = new TerrainPosition(52, 0, 51);

        TerrainPosition v7 = new TerrainPosition(51, 0, 49);
        TerrainPosition v8 = new TerrainPosition(52, 0, 49);


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

        RoadGraph roadGraph = scene.getRoadGraphCopy();

        Set<NodeRoad> nodes = roadGraph.getNodes();
        Set<NodeConnection> nodeConnections = roadGraph.getNodeConnections();

        Set<NodeRoad> expectedNodes = new HashSet<>();
        Set<NodeConnection> expectedPaths = new TreeSet<>();

        NodeRoad firstNode = new NodeRoad(v2);
        expectedNodes.add(firstNode);
        NodeRoad lastNode = new NodeRoad(v3);
        expectedNodes.add(lastNode);

        NodeConnection nodeConnection = new NodeConnection(firstNode, lastNode);
        nodeConnection.addRoad(lastNode);

        NodeConnection nodeConnection2 = new NodeConnection(firstNode, lastNode);
        nodeConnection2.addRoad(new NormalRoad(v7));
        nodeConnection2.addRoad(new NormalRoad(v8));
        nodeConnection2.addRoad(lastNode);

        NodeConnection nodeConnection3 = new NodeConnection(firstNode, lastNode);
        nodeConnection3.addRoad(new NormalRoad(v5));
        nodeConnection3.addRoad(new NormalRoad(v6));
        nodeConnection3.addRoad(lastNode);

        expectedPaths.add(nodeConnection);
        expectedPaths.add(nodeConnection.invert());

        expectedPaths.add(nodeConnection2);
        expectedPaths.add(nodeConnection2.invert());

        expectedPaths.add(nodeConnection3);
        expectedPaths.add(nodeConnection3.invert());

        assertEquals(expectedNodes, nodes);
        assertEquals(expectedPaths, nodeConnections);
    }

    /**
     * 20. Road 2 Node (close) Choice
     */
    @Test
    void testRoadGraph20() {
        TerrainPosition v1 = new TerrainPosition(50, 0, 50);
        TerrainPosition v2 = new TerrainPosition(51, 0, 50);
        TerrainPosition v3 = new TerrainPosition(52, 0, 50);
        TerrainPosition v4 = new TerrainPosition(51, 0, 51);
        TerrainPosition v5 = new TerrainPosition(52, 0, 51);


        int[] roadPositions = new int[]{
                v1.getX(), v1.getZ(),
                v2.getX(), v2.getZ(),
                v3.getX(), v3.getZ(),
                v4.getX(), v4.getZ(),
                v5.getX(), v5.getZ()
        };

        TerrainPosition[] roads = TerrainPosition.toPositionArray(roadPositions);

        GameObject.newInstances(DirtRoad.class, roads);

        RoadGraph roadGraph = scene.getRoadGraphCopy();

        Set<NodeRoad> nodes = roadGraph.getNodes();
        Set<NodeConnection> nodeConnections = roadGraph.getNodeConnections();

        Set<NodeRoad> expectedNodes = new HashSet<>();
        Set<NodeConnection> expectedPaths = new TreeSet<>();

        NodeRoad node = new NodeRoad(v2);
        expectedNodes.add(node);

        assertEquals(expectedNodes, nodes);
        assertEquals(expectedPaths, nodeConnections);
    }

    /**
     * 21. Road 2 Road (close) Choice
     */
    @Test
    void testRoadGraph21() {
        TerrainPosition v1 = new TerrainPosition(50, 0, 50);
        TerrainPosition v2 = new TerrainPosition(51, 0, 50);
        TerrainPosition v3 = new TerrainPosition(50, 0, 49);
        TerrainPosition v4 = new TerrainPosition(51, 0, 49);


        int[] roadPositions = new int[]{
                v1.getX(), v1.getZ(),
                v2.getX(), v2.getZ(),
                v3.getX(), v3.getZ(),
                v4.getX(), v4.getZ()
        };

        TerrainPosition[] roads = TerrainPosition.toPositionArray(roadPositions);

        GameObject.newInstances(DirtRoad.class, roads);

        RoadGraph roadGraph = scene.getRoadGraphCopy();

        Set<NodeRoad> nodes = roadGraph.getNodes();
        Set<NodeConnection> nodeConnections = roadGraph.getNodeConnections();

        Set<NodeRoad> expectedNodes = new HashSet<>();
        Set<NodeConnection> expectedPaths = new TreeSet<>();

        assertEquals(expectedNodes, nodes);
        assertEquals(expectedPaths, nodeConnections);
    }

    /**
     * 22. Road 2 Road (ignore Nodes) Choice
     */
    @Test
    void testRoadGraph22() {
        TerrainPosition v1 = new TerrainPosition(52, 0, 50);
        TerrainPosition v2 = new TerrainPosition(53, 0, 50);
        TerrainPosition v3 = new TerrainPosition(54, 0, 50);


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

        RoadGraph roadGraph = scene.getRoadGraphCopy();

        Set<NodeRoad> nodes = roadGraph.getNodes();
        Set<NodeConnection> nodeConnections = roadGraph.getNodeConnections();

        Set<NodeRoad> expectedNodes = new HashSet<>();
        Set<NodeConnection> expectedPaths = new TreeSet<>();

        NodeRoad firstNode = new NodeRoad(roads[0]);
        expectedNodes.add(firstNode);
        NodeRoad lastNode = new NodeRoad(roads[1]);
        expectedNodes.add(lastNode);

        NodeConnection nodeConnection = new NodeConnection(firstNode, lastNode);
        nodeConnection.addRoad(new NormalRoad(v1));
        nodeConnection.addRoad(new NormalRoad(v2));
        nodeConnection.addRoad(new NormalRoad(v3));
        nodeConnection.addRoad(lastNode);

        NodeConnection nodeConnection2 = new NodeConnection(firstNode, lastNode);
        IntStream.range(4, 11).forEach(i -> nodeConnection2.addRoad(new NormalRoad(roads[i])));
        nodeConnection2.addRoad(lastNode);

        expectedPaths.add(nodeConnection);
        expectedPaths.add(nodeConnection.invert());

        expectedPaths.add(nodeConnection2);
        expectedPaths.add(nodeConnection2.invert());

        assertEquals(expectedNodes, nodes);
        assertEquals(expectedPaths, nodeConnections);
    }

    /**
     * 23. Road 2 Road (ignore Nodes) Choice (v2)
     */
    @Test
    void testRoadGraph23() {
        TerrainPosition v1 = new TerrainPosition(52, 0, 48);
        TerrainPosition v2 = new TerrainPosition(53, 0, 48);
        TerrainPosition v3 = new TerrainPosition(54, 0, 48);


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

        RoadGraph roadGraph = scene.getRoadGraphCopy();

        Set<NodeRoad> nodes = roadGraph.getNodes();
        Set<NodeConnection> nodeConnections = roadGraph.getNodeConnections();

        Set<NodeRoad> expectedNodes = new HashSet<>();
        Set<NodeConnection> expectedPaths = new TreeSet<>();

        NodeRoad firstNode = new NodeRoad(roads[0]);
        expectedNodes.add(firstNode);
        NodeRoad middle1 = new NodeRoad(roads[1]);
        expectedNodes.add(middle1);
        NodeRoad middle2 = new NodeRoad(roads[2]);
        expectedNodes.add(middle2);
        NodeRoad lastNode = new NodeRoad(roads[3]);
        expectedNodes.add(lastNode);

        NodeConnection nodeConnection = new NodeConnection(firstNode, middle1);
        nodeConnection.addRoad(new NormalRoad(roads[4]));
        nodeConnection.addRoad(middle1);

        NodeConnection nodeConnection2 = new NodeConnection(firstNode, middle2);
        IntStream.range(6, 9).forEach(i -> nodeConnection2.addRoad(new NormalRoad(roads[i])));
        nodeConnection2.addRoad(middle2);

        NodeConnection nodeConnection3 = new NodeConnection(middle1, lastNode);
        IntStream.range(9, 12).forEach(i -> nodeConnection3.addRoad(new NormalRoad(roads[i])));
        nodeConnection3.addRoad(lastNode);

        NodeConnection nodeConnection4 = new NodeConnection(middle2, lastNode);
        nodeConnection4.addRoad(new NormalRoad(roads[5]));
        nodeConnection4.addRoad(lastNode);

        expectedPaths.add(nodeConnection);
        expectedPaths.add(nodeConnection.invert());

        expectedPaths.add(nodeConnection2);
        expectedPaths.add(nodeConnection2.invert());

        expectedPaths.add(nodeConnection3);
        expectedPaths.add(nodeConnection3.invert());

        expectedPaths.add(nodeConnection4);
        expectedPaths.add(nodeConnection4.invert());

        assertEquals(expectedNodes, nodes);
        assertEquals(expectedPaths, nodeConnections);
    }

    /**
     * 24. Road 2 Road (loop)
     */
    @Test
    void testRoadGraph24() {
        TerrainPosition v1 = new TerrainPosition(50, 0, 50);
        TerrainPosition v2 = new TerrainPosition(51, 0, 50);
        TerrainPosition v3 = new TerrainPosition(52, 0, 50);
        TerrainPosition v4 = new TerrainPosition(53, 0, 50);
        TerrainPosition v5 = new TerrainPosition(54, 0, 50);


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

        RoadGraph roadGraph = scene.getRoadGraphCopy();

        Set<NodeRoad> nodes = roadGraph.getNodes();
        Set<NodeConnection> nodeConnections = roadGraph.getNodeConnections();

        Set<NodeRoad> expectedNodes = new HashSet<>();
        Set<NodeConnection> expectedPaths = new TreeSet<>();

        assertEquals(expectedNodes, nodes);
        assertEquals(expectedPaths, nodeConnections);
    }

    /**
     * 25. Road 2 Road (through node) x 2
     */
    @Test
    void testRoadGraph25() {
        TerrainPosition v1 = new TerrainPosition(50, 0, 50);
        TerrainPosition v2 = new TerrainPosition(51, 0, 50);
        TerrainPosition v3 = new TerrainPosition(52, 0, 50);
        TerrainPosition v4 = new TerrainPosition(53, 0, 50);
        TerrainPosition v5 = new TerrainPosition(54, 0, 50);
        TerrainPosition v6 = new TerrainPosition(60, 0, 50);
        TerrainPosition v7 = new TerrainPosition(61, 0, 50);
        TerrainPosition v8 = new TerrainPosition(62, 0, 50);
        TerrainPosition v9 = new TerrainPosition(63, 0, 50);
        TerrainPosition v10 = new TerrainPosition(64, 0, 50);

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

        RoadGraph roadGraph = scene.getRoadGraphCopy();

        Set<NodeRoad> nodes = roadGraph.getNodes();
        Set<NodeConnection> nodeConnections = roadGraph.getNodeConnections();

        Set<NodeRoad> expectedNodes = new HashSet<>();
        Set<NodeConnection> expectedPaths = new TreeSet<>();

        NodeRoad firstNode = new NodeRoad(v3);
        expectedNodes.add(firstNode);
        NodeRoad lastNode = new NodeRoad(v8);
        expectedNodes.add(lastNode);

        assertEquals(expectedNodes, nodes);
        assertEquals(expectedPaths, nodeConnections);
    }

    /**
     * 26. Road 2 Road x 2
     */
    @Test
    void testRoadGraph26() {
        TerrainPosition v1 = new TerrainPosition(50, 0, 50);
        TerrainPosition v2 = new TerrainPosition(51, 0, 50);
        TerrainPosition v3 = new TerrainPosition(52, 0, 50);

        TerrainPosition v4 = new TerrainPosition(56, 0, 49);
        TerrainPosition v5 = new TerrainPosition(56, 0, 50);
        TerrainPosition v6 = new TerrainPosition(56, 0, 51);

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

        RoadGraph roadGraph = scene.getRoadGraphCopy();

        Set<NodeRoad> nodes = roadGraph.getNodes();
        Set<NodeConnection> nodeConnections = roadGraph.getNodeConnections();

        Set<NodeRoad> expectedNodes = new HashSet<>();
        Set<NodeConnection> expectedPaths = new TreeSet<>();

        assertEquals(expectedNodes, nodes);
        assertEquals(expectedPaths, nodeConnections);
    }

    /**
     * 27. Node 2 Road x 2
     */
    @Test
    void testRoadGraph27() {
        TerrainPosition v1 = new TerrainPosition(51, 0, 50);
        TerrainPosition v2 = new TerrainPosition(52, 0, 50);
        TerrainPosition v3 = new TerrainPosition(53, 0, 50);

        TerrainPosition v4 = new TerrainPosition(56, 0, 50);
        TerrainPosition v5 = new TerrainPosition(57, 0, 50);
        TerrainPosition v6 = new TerrainPosition(58, 0, 50);

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

        RoadGraph roadGraph = scene.getRoadGraphCopy();

        Set<NodeRoad> nodes = roadGraph.getNodes();
        Set<NodeConnection> nodeConnections = roadGraph.getNodeConnections();

        Set<NodeRoad> expectedNodes = new HashSet<>();
        Set<NodeConnection> expectedPaths = new TreeSet<>();

        NodeRoad firstNode = new NodeRoad(v1);
        expectedNodes.add(firstNode);
        NodeRoad lastNode = new NodeRoad(v4);
        expectedNodes.add(lastNode);

        assertEquals(expectedNodes, nodes);
        assertEquals(expectedPaths, nodeConnections);
    }

    /**
     * 28. Node 2 Node x2
     */
    @Test
    void testRoadGraph28() {
        TerrainPosition v1 = new TerrainPosition(51, 0, 50);
        TerrainPosition v2 = new TerrainPosition(52, 0, 50);
        TerrainPosition v3 = new TerrainPosition(53, 0, 50);

        TerrainPosition v4 = new TerrainPosition(57, 0, 51);
        TerrainPosition v5 = new TerrainPosition(58, 0, 51);
        TerrainPosition v6 = new TerrainPosition(58, 0, 50);

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

        RoadGraph roadGraph = scene.getRoadGraphCopy();

        Set<NodeRoad> nodes = roadGraph.getNodes();
        Set<NodeConnection> nodeConnections = roadGraph.getNodeConnections();

        Set<NodeRoad> expectedNodes = new HashSet<>();
        Set<NodeConnection> expectedPaths = new TreeSet<>();

        NodeRoad firstNode = new NodeRoad(v1);
        expectedNodes.add(firstNode);
        NodeRoad middle1 = new NodeRoad(v3);
        expectedNodes.add(middle1);
        NodeRoad middle2 = new NodeRoad(v4);
        expectedNodes.add(middle2);
        NodeRoad lastNode = new NodeRoad(v6);
        expectedNodes.add(lastNode);

        NodeConnection nodeConnection = new NodeConnection(firstNode, middle1);
        nodeConnection.addRoad(new NormalRoad(v2));
        nodeConnection.addRoad(middle1);

        NodeConnection nodeConnection2 = new NodeConnection(middle2, lastNode);
        nodeConnection2.addRoad(new NormalRoad(v5));
        nodeConnection2.addRoad(lastNode);

        expectedPaths.add(nodeConnection);
        expectedPaths.add(nodeConnection.invert());

        expectedPaths.add(nodeConnection2);
        expectedPaths.add(nodeConnection2.invert());

        assertEquals(expectedNodes, nodes);
        assertEquals(expectedPaths, nodeConnections);
    }

    /**
     * 29. Road 2 Road (choice found in practice)
     */
    @Test
    void testRoadGraph29() {
        TerrainPosition v1 = new TerrainPosition(50, 0, 50);
        TerrainPosition v2 = new TerrainPosition(51, 0, 50);
        TerrainPosition v3 = new TerrainPosition(52, 0, 50);
        TerrainPosition v4 = new TerrainPosition(53, 0, 50);
        TerrainPosition v5 = new TerrainPosition(54, 0, 50);
        TerrainPosition v6 = new TerrainPosition(55, 0, 50);
        TerrainPosition v7 = new TerrainPosition(56, 0, 50);

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

        RoadGraph roadGraph = scene.getRoadGraphCopy();

        Set<NodeRoad> nodes = roadGraph.getNodes();
        Set<NodeConnection> nodeConnections = roadGraph.getNodeConnections();

        Set<NodeRoad> expectedNodes = new HashSet<>();
        Set<NodeConnection> expectedPaths = new TreeSet<>();

        NodeRoad firstNode = new NodeRoad(v3);
        expectedNodes.add(firstNode);
        NodeRoad middle1 = new NodeRoad(v4);
        expectedNodes.add(middle1);
        NodeRoad middle2 = new NodeRoad(v5);
        expectedNodes.add(middle2);
        NodeRoad lastNode = new NodeRoad(roads[1]);
        expectedNodes.add(lastNode);

        NodeConnection nodeConnection = new NodeConnection(firstNode, middle1);
        nodeConnection.addRoad(middle1);

        NodeConnection nodeConnection2 = new NodeConnection(middle1, middle2);
        nodeConnection2.addRoad(middle2);

        NodeConnection nodeConnection3 = new NodeConnection(middle1, lastNode);
        nodeConnection3.addRoad(lastNode);

        NodeConnection nodeConnection4 = new NodeConnection(firstNode, lastNode);
        nodeConnection4.addRoad(new NormalRoad(roads[0]));
        nodeConnection4.addRoad(lastNode);

        NodeConnection nodeConnection5 = new NodeConnection(middle2, lastNode);
        nodeConnection5.addRoad(new NormalRoad(roads[2]));
        nodeConnection5.addRoad(lastNode);

        expectedPaths.add(nodeConnection);
        expectedPaths.add(nodeConnection.invert());

        expectedPaths.add(nodeConnection2);
        expectedPaths.add(nodeConnection2.invert());

        expectedPaths.add(nodeConnection3);
        expectedPaths.add(nodeConnection3.invert());

        expectedPaths.add(nodeConnection4);
        expectedPaths.add(nodeConnection4.invert());

        expectedPaths.add(nodeConnection5);
        expectedPaths.add(nodeConnection5.invert());

        assertEquals(expectedNodes, nodes);
        assertEquals(expectedPaths, nodeConnections);
    }

    /**
     * 29b. Road 2 Road (choice found in practice)
     */
    @Test
    void testRoadGraph29b() {
        TerrainPosition v1 = new TerrainPosition(50, 0, 50);
        TerrainPosition v2 = new TerrainPosition(51, 0, 50);
        TerrainPosition v3 = new TerrainPosition(52, 0, 50);
        TerrainPosition v4 = new TerrainPosition(53, 0, 50);
        TerrainPosition v5 = new TerrainPosition(54, 0, 50);
        TerrainPosition v6 = new TerrainPosition(55, 0, 50);
        TerrainPosition v6b = new TerrainPosition(54, 0, 51);
        TerrainPosition v7 = new TerrainPosition(55, 0, 51);

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

        RoadGraph roadGraph = scene.getRoadGraphCopy();

        Set<NodeRoad> nodes = roadGraph.getNodes();
        Set<NodeConnection> nodeConnections = roadGraph.getNodeConnections();

        Set<NodeRoad> expectedNodes = new HashSet<>();
        Set<NodeConnection> expectedPaths = new TreeSet<>();

        expectedNodes.add(new NodeRoad(v5));

        assertEquals(expectedNodes, nodes);
        assertEquals(expectedPaths, nodeConnections);
    }

    /**
     * 30. Road 2 Road (=)
     */
    @Test
    void testRoadGraph30() {
        TerrainPosition v1 = new TerrainPosition(50, 0, 50);
        TerrainPosition v2 = new TerrainPosition(51, 0, 50);
        TerrainPosition v3 = new TerrainPosition(51, 0, 51);

        int[] roadPositions = new int[]{
                v1.getX(), v1.getZ(),
                v2.getX(), v2.getZ(),
                v3.getX(), v3.getZ(),
        };

        TerrainPosition[] roads = TerrainPosition.toPositionArray(roadPositions);

        GameObject.newInstances(DirtRoad.class, roads);

        RoadGraph roadGraph = scene.getRoadGraphCopy();

        Set<NodeRoad> nodes = roadGraph.getNodes();
        Set<NodeConnection> nodeConnections = roadGraph.getNodeConnections();

        Set<NodeRoad> expectedNodes = new HashSet<>();
        Set<NodeConnection> expectedPaths = new TreeSet<>();

        assertEquals(expectedNodes, nodes);
        assertEquals(expectedPaths, nodeConnections);
    }

    /**
     * 31. Node 2 Node (=)
     */
    @Test
    void testRoadGraph31() {
        TerrainPosition v1 = new TerrainPosition(50, 0, 50);
        TerrainPosition v2 = new TerrainPosition(50, 0, 49);
        TerrainPosition v3 = new TerrainPosition(51, 0, 49);
        TerrainPosition v4 = new TerrainPosition(50, 0, 48);

        int[] roadPositions = new int[]{
                v1.getX(), v1.getZ(),
                v2.getX(), v2.getZ(),
                v3.getX(), v3.getZ(),
                v4.getX(), v4.getZ(),
        };

        TerrainPosition[] roads = TerrainPosition.toPositionArray(roadPositions);

        GameObject.newInstances(DirtRoad.class, roads);

        RoadGraph roadGraph = scene.getRoadGraphCopy();

        Set<NodeRoad> nodes = roadGraph.getNodes();
        Set<NodeConnection> nodeConnections = roadGraph.getNodeConnections();

        Set<NodeRoad> expectedNodes = new HashSet<>();
        Set<NodeConnection> expectedPaths = new TreeSet<>();

        expectedNodes.add(new NodeRoad(v2));

        assertEquals(expectedNodes, nodes);
        assertEquals(expectedPaths, nodeConnections);
    }

    /**
     * 32. Found in practice
     */
    @Test
    void testRoadGraph32() {
        TerrainPosition[] positions = new TerrainPosition[15];
        TerrainPosition[] positions2 = new TerrainPosition[14];
        for (int i = 55; i < 70; i++)
            positions[i - 55] = new TerrainPosition(50, 0, i);
        for (int i = 50; i < 64; i++)
            positions2[i - 50] = new TerrainPosition(i, 0, 70);

        GameObject.newInstances(DirtRoad.class, positions);
        GameObject.newInstance(DirtRoad.class, new TerrainPosition(51, 0, 55));
        GameObject.newInstance(DirtRoad.class, new TerrainPosition(49, 0, 55));
        GameObject.newInstance(DirtRoad.class, new TerrainPosition(51, 0, 63));
        GameObject.newInstance(DirtRoad.class, new TerrainPosition(62, 0, 71));

        List<TerrainPosition> pos2 = Arrays.asList(positions2);
        Collections.reverse(pos2);
        positions2 = pos2.toArray(new TerrainPosition[0]);
        GameObject.newInstances(DirtRoad.class, positions2);

        RoadGraph roadGraph = scene.getRoadGraphCopy();

        Set<NodeRoad> nodes = roadGraph.getNodes();
        Set<NodeConnection> nodeConnections = roadGraph.getNodeConnections();

        Set<NodeRoad> expectedNodes = new HashSet<>();
        Set<NodeConnection> expectedPaths = new TreeSet<>();

        NodeRoad node1 = new NodeRoad(new TerrainPosition(50, 0, 63));
        NodeRoad node2 = new NodeRoad(new TerrainPosition(62, 0, 70));
        NodeRoad node3 = new NodeRoad(new TerrainPosition(50, 0, 55));
        expectedNodes.add(node1);
        expectedNodes.add(node2);
        expectedNodes.add(node3);

        NodeConnection nodeConnection1 = new NodeConnection(node3, node1);
        Arrays.stream(positions, 0, 8).forEach(p -> nodeConnection1.addRoad(new NormalRoad(p)));
        nodeConnection1.addRoad(node1);
        expectedPaths.add(nodeConnection1);
        expectedPaths.add(nodeConnection1.invert());

        NodeConnection nodeConnection2 = new NodeConnection(node2, node1);
        Arrays.stream(positions2, 1, 14).forEach(p -> nodeConnection2.addRoad(new NormalRoad(p)));
        List<TerrainPosition> pos = Arrays.asList(positions);
        Collections.reverse(pos);
        Arrays.stream(positions, 0, 6).forEach(p -> nodeConnection2.addRoad(new NormalRoad(p)));
        nodeConnection2.addRoad(node1);
        expectedPaths.add(nodeConnection2);
        expectedPaths.add(nodeConnection2.invert());

        assertEquals(expectedNodes, nodes);
        assertEquals(expectedPaths, nodeConnections);
    }
}