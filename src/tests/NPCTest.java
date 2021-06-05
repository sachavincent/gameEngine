package tests;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.lwjgl.glfw.GLFW.glfwInit;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pathfinding.NodeConnection;
import pathfinding.NodeRoad;
import pathfinding.NormalRoad;
import pathfinding.Path;
import pathfinding.PathFinder;
import renderEngine.DisplayManager;
import renderEngine.PathRenderer;
import scene.Scene;
import scene.components.PathComponent;
import scene.components.PathComponent.PathType;
import scene.components.PedestrianComponent;
import scene.components.PedestrianComponent.Behavior;
import scene.gameObjects.DirtRoad;
import scene.gameObjects.GameObject;
import scene.gameObjects.NPC;
import terrains.TerrainPosition;
import util.math.Vector3f;

public class NPCTest {

    private final static Scene scene = Scene.getInstance();

    private static NPC NPC;

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

        NPC = new NPC();
        NPC.addComponent(new PedestrianComponent(Behavior.WALKING));
        NPC.getComponent(PathComponent.class).createPathComponent(NPC, new Vector3f(20, 0, 20), PathType.ROAD_TO_ROAD);
    }

    @Test
    /**
     * See Case1
     */
    void testPath1() {
        TerrainPosition start = new TerrainPosition(10, 14);
        TerrainPosition end = new TerrainPosition(25, 14);
        TerrainPosition npcPos = new TerrainPosition(20, 20);

        GameObject.newInstance(DirtRoad.class, start);
        GameObject.newInstance(DirtRoad.class, end);
        GameObject.newInstance(DirtRoad.class, npcPos);

        for (int x = 11; x < 25; x++) {
            GameObject.newInstance(DirtRoad.class, new TerrainPosition(x, 14));
        }
        for (int z = 11; z < 20; z++) {
            GameObject.newInstance(DirtRoad.class, new TerrainPosition(12, z));
            GameObject.newInstance(DirtRoad.class, new TerrainPosition(20, z));
        }

        PathFinder pathFinder = new PathFinder(scene.getRoadGraph());
        Path givenPath = pathFinder.findBestPath(start, end, 0);
        PathComponent pathComponent = NPC.getComponent(PathComponent.class);
        pathComponent.setPath(givenPath);
        Path foundBestPath = pathComponent.getPath();

        Path expectedBestPath = new Path();
        NodeConnection nodeConnection1 = new NodeConnection(new NormalRoad(npcPos),
                new NodeRoad(new TerrainPosition(20, 14)));
        NodeConnection nodeConnection2 = new NodeConnection(new NodeRoad(new TerrainPosition(20, 14)),
                new NormalRoad(new TerrainPosition(25, 14)));

        for (int z = 19; z > 14; z--)
            nodeConnection1.addRoad(new NormalRoad(new TerrainPosition(20, z)));
        nodeConnection1.addRoad(new NodeRoad(new TerrainPosition(20, 14)));

        for (int x = 21; x < 26; x++)
            nodeConnection2.addRoad(new NormalRoad(new TerrainPosition(x, 14)));

        expectedBestPath.add(nodeConnection1);
        expectedBestPath.add(nodeConnection2);

        assertTrue(foundBestPath.comparePaths(expectedBestPath));
        assertTrue(foundBestPath.compareCost(expectedBestPath));
    }

    @Test
    /**
     * See Case2
     */
    void testPath2() {
        TerrainPosition start = new TerrainPosition(10, 14);
        TerrainPosition end = new TerrainPosition(25, 14);
        TerrainPosition npcPos = new TerrainPosition(20, 20);

        GameObject.newInstance(DirtRoad.class, start);
        GameObject.newInstance(DirtRoad.class, end);
        GameObject.newInstance(DirtRoad.class, npcPos);

        for (int x = 11; x < 25; x++) {
            GameObject.newInstance(DirtRoad.class, new TerrainPosition(x, 14));
        }
        for (int z = 11; z < 20; z++) {
            GameObject.newInstance(DirtRoad.class, new TerrainPosition(12, z));
            GameObject.newInstance(DirtRoad.class, new TerrainPosition(20, z));
        }

        GameObject.newInstance(DirtRoad.class, new TerrainPosition(21, 20)); // Case2
        GameObject.newInstance(DirtRoad.class, new TerrainPosition(19, 20)); // Case2

        PathFinder pathFinder = new PathFinder(scene.getRoadGraph());
        Path givenPath = pathFinder.findBestPath(start, end, 0);
        PathComponent pathComponent = NPC.getComponent(PathComponent.class);
        pathComponent.setPath(givenPath);
        Path foundBestPath = pathComponent.getPath();

        Path expectedBestPath = new Path();
        NodeConnection nodeConnection1 = new NodeConnection(new NodeRoad(npcPos),
                new NodeRoad(new TerrainPosition(20, 14)));
        for (int z = 19; z > 14; z--)
            nodeConnection1.addRoad(new NormalRoad(new TerrainPosition(20, z)));
        nodeConnection1.addRoad(new NodeRoad(new TerrainPosition(20, 14)));

        NodeConnection nodeConnection2 = new NodeConnection(new NodeRoad(new TerrainPosition(20, 14)),
                new NormalRoad(end));
        for (int x = 21; x < 26; x++)
            nodeConnection2.addRoad(new NormalRoad(new TerrainPosition(x, 14)));

        expectedBestPath.add(nodeConnection1);
        expectedBestPath.add(nodeConnection2);

        assertTrue(foundBestPath.comparePaths(expectedBestPath));
        assertTrue(foundBestPath.compareCost(expectedBestPath));
    }

    @Test
    /**
     * See Case3
     */
    void testPath3() {
        TerrainPosition start = new TerrainPosition(10, 14);
        TerrainPosition end = new TerrainPosition(25, 14);
        TerrainPosition npcPos = new TerrainPosition(20, 20);

        GameObject.newInstance(DirtRoad.class, start);
        GameObject.newInstance(DirtRoad.class, end);
        GameObject.newInstance(DirtRoad.class, npcPos);

        for (int x = 11; x < 25; x++) {
            GameObject.newInstance(DirtRoad.class, new TerrainPosition(x, 14));
        }
        for (int z = 11; z < 20; z++) {
            GameObject.newInstance(DirtRoad.class, new TerrainPosition(12, z));
            GameObject.newInstance(DirtRoad.class, new TerrainPosition(20, z));
        }

        GameObject.newInstance(DirtRoad.class, new TerrainPosition(19, 17)); // Case3
        GameObject.newInstance(DirtRoad.class, new TerrainPosition(21, 20));

        PathFinder pathFinder = new PathFinder(scene.getRoadGraph());
        Path givenPath = pathFinder.findBestPath(start, end, 0);
        PathComponent pathComponent = NPC.getComponent(PathComponent.class);
        pathComponent.setPath(givenPath);
        Path foundBestPath = pathComponent.getPath();

        Path expectedBestPath = new Path();
        NodeConnection nodeConnection1 = new NodeConnection(new NormalRoad(npcPos),
                new NodeRoad(new TerrainPosition(20, 17)));
        for (int z = 19; z > 17; z--) {
            nodeConnection1.addRoad(new NormalRoad(new TerrainPosition(20, z)));
        }
        nodeConnection1.addRoad(new NodeRoad(new TerrainPosition(20, 17)));

        NodeConnection nodeConnection2 = new NodeConnection(new NodeRoad(new TerrainPosition(20, 17)),
                new NodeRoad(new TerrainPosition(20, 14)));
        for (int z = 17; z > 14; z--) {
            nodeConnection2.addRoad(new NormalRoad(new TerrainPosition(20, z)));
        }
        nodeConnection2.addRoad(new NodeRoad(new TerrainPosition(20, 14)));

        NodeConnection nodeConnection3 = new NodeConnection(new NodeRoad(new TerrainPosition(20, 14)),
                new NormalRoad(end));
        for (int x = 21; x < 26; x++)
            nodeConnection3.addRoad(new NormalRoad(new TerrainPosition(x, 14)));

        expectedBestPath.add(nodeConnection1);
        expectedBestPath.add(nodeConnection2);
        expectedBestPath.add(nodeConnection3);

        assertTrue(foundBestPath.comparePaths(expectedBestPath));
        assertTrue(foundBestPath.compareCost(expectedBestPath));
    }

    @Test
    /**
     * See Case4
     */
    void testPath4() {
        TerrainPosition start = new TerrainPosition(10, 14);
        TerrainPosition end = new TerrainPosition(25, 14);
        TerrainPosition npcPos = new TerrainPosition(20, 20);

        GameObject.newInstance(DirtRoad.class, start);
        GameObject.newInstance(DirtRoad.class, end);
        GameObject.newInstance(DirtRoad.class, npcPos);

        for (int x = 11; x < 25; x++) {
            GameObject.newInstance(DirtRoad.class, new TerrainPosition(x, 14));
        }
        for (int z = 11; z < 20; z++) {
            GameObject.newInstance(DirtRoad.class, new TerrainPosition(12, z));
            GameObject.newInstance(DirtRoad.class, new TerrainPosition(20, z));
        }

        GameObject.newInstance(DirtRoad.class, new TerrainPosition(19, 17));
        GameObject.newInstance(DirtRoad.class, new TerrainPosition(19, 20)); // Case4
        GameObject.newInstance(DirtRoad.class, new TerrainPosition(21, 20));

        PathFinder pathFinder = new PathFinder(scene.getRoadGraph());
        Path givenPath = pathFinder.findBestPath(start, end, 0);
        PathComponent pathComponent = NPC.getComponent(PathComponent.class);
        pathComponent.setPath(givenPath);
        Path foundBestPath = pathComponent.getPath();

        Path expectedBestPath = new Path();
        NodeConnection nodeConnection1 = new NodeConnection(new NodeRoad(npcPos),
                new NodeRoad(new TerrainPosition(20, 17)));
        for (int z = 19; z > 17; z--) {
            nodeConnection1.addRoad(new NormalRoad(new TerrainPosition(20, z)));
        }
        nodeConnection1.addRoad(new NodeRoad(new TerrainPosition(20, 17)));

        NodeConnection nodeConnection2 = new NodeConnection(new NodeRoad(new TerrainPosition(20, 17)),
                new NodeRoad(new TerrainPosition(20, 14)));
        for (int z = 17; z > 14; z--) {
            nodeConnection2.addRoad(new NormalRoad(new TerrainPosition(20, z)));
        }
        nodeConnection2.addRoad(new NodeRoad(new TerrainPosition(20, 14)));

        NodeConnection nodeConnection3 = new NodeConnection(new NodeRoad(new TerrainPosition(20, 14)),
                new NormalRoad(end));
        for (int x = 21; x < 26; x++)
            nodeConnection3.addRoad(new NormalRoad(new TerrainPosition(x, 14)));

        expectedBestPath.add(nodeConnection1);
        expectedBestPath.add(nodeConnection2);
        expectedBestPath.add(nodeConnection3);

        assertTrue(foundBestPath.comparePaths(expectedBestPath));
        assertTrue(foundBestPath.compareCost(expectedBestPath));
    }

    @Test
    /**
     * See Case5
     */
    void testPath5() {
        TerrainPosition start = new TerrainPosition(10, 14);
        TerrainPosition end = new TerrainPosition(25, 14);
        TerrainPosition npcPos = new TerrainPosition(20, 20);

        GameObject.newInstance(DirtRoad.class, start);
        GameObject.newInstance(DirtRoad.class, end);
        GameObject.newInstance(DirtRoad.class, npcPos);

        for (int x = 11; x < 25; x++) {
            GameObject.newInstance(DirtRoad.class, new TerrainPosition(x, 14));
        }
        for (int z = 11; z < 20; z++) {
            GameObject.newInstance(DirtRoad.class, new TerrainPosition(12, z));
            GameObject.newInstance(DirtRoad.class, new TerrainPosition(20, z));
        }

        GameObject.newInstance(DirtRoad.class, new TerrainPosition(19, 17));
        GameObject.newInstance(DirtRoad.class, new TerrainPosition(19, 20));
        GameObject.newInstance(DirtRoad.class, new TerrainPosition(21, 20));
        GameObject.newInstance(DirtRoad.class, new TerrainPosition(23, 13)); // Case5

        PathFinder pathFinder = new PathFinder(scene.getRoadGraph());
        Path givenPath = pathFinder.findBestPath(start, end, 0);
        PathComponent pathComponent = NPC.getComponent(PathComponent.class);
        pathComponent.setPath(givenPath);
        Path foundBestPath = pathComponent.getPath();

        Path expectedBestPath = new Path();
        NodeConnection nodeConnection1 = new NodeConnection(new NodeRoad(npcPos),
                new NodeRoad(new TerrainPosition(20, 17)));
        for (int z = 19; z > 17; z--) {
            nodeConnection1.addRoad(new NormalRoad(new TerrainPosition(20, z)));
        }
        nodeConnection1.addRoad(new NodeRoad(new TerrainPosition(20, 17)));

        NodeConnection nodeConnection2 = new NodeConnection(new NodeRoad(new TerrainPosition(20, 17)),
                new NodeRoad(new TerrainPosition(20, 14)));
        for (int z = 17; z > 14; z--) {
            nodeConnection2.addRoad(new NormalRoad(new TerrainPosition(20, z)));
        }
        nodeConnection2.addRoad(new NodeRoad(new TerrainPosition(20, 14)));

        NodeConnection nodeConnection3 = new NodeConnection(new NodeRoad(new TerrainPosition(20, 14)),
                new NodeRoad(new TerrainPosition(23, 14)));
        nodeConnection3.addRoad(new NormalRoad(new TerrainPosition(21, 14)));
        nodeConnection3.addRoad(new NormalRoad(new TerrainPosition(22, 14)));
        nodeConnection3.addRoad(new NodeRoad(new TerrainPosition(23, 14)));

        NodeConnection nodeConnection4 = new NodeConnection(new NodeRoad(new TerrainPosition(23, 14)),
                new NormalRoad(end));
        nodeConnection4.addRoad(new NormalRoad(new TerrainPosition(24, 14)));
        nodeConnection4.addRoad(new NormalRoad(end));

        expectedBestPath.add(nodeConnection1);
        expectedBestPath.add(nodeConnection2);
        expectedBestPath.add(nodeConnection3);
        expectedBestPath.add(nodeConnection4);

        assertTrue(foundBestPath.comparePaths(expectedBestPath));
        assertTrue(foundBestPath.compareCost(expectedBestPath));
    }

    @Test
    /**
     * See Case6
     */
    void testPath6() {
        TerrainPosition start = new TerrainPosition(10, 14);
        TerrainPosition end = new TerrainPosition(25, 14);
        TerrainPosition npcPos = new TerrainPosition(20, 20);

        GameObject.newInstance(DirtRoad.class, start);
        GameObject.newInstance(DirtRoad.class, end);
        GameObject.newInstance(DirtRoad.class, npcPos);

        for (int x = 11; x < 25; x++) {
            GameObject.newInstance(DirtRoad.class, new TerrainPosition(x, 14));
        }
        for (int z = 11; z < 20; z++) {
            GameObject.newInstance(DirtRoad.class, new TerrainPosition(12, z));
            GameObject.newInstance(DirtRoad.class, new TerrainPosition(20, z));
        }

        GameObject.newInstance(DirtRoad.class, new TerrainPosition(19, 17));
        GameObject.newInstance(DirtRoad.class, new TerrainPosition(19, 20));
        GameObject.newInstance(DirtRoad.class, new TerrainPosition(21, 20));
        GameObject.newInstance(DirtRoad.class, new TerrainPosition(23, 13));
        GameObject.newInstance(DirtRoad.class, new TerrainPosition(16, 15)); // Case6

        PathFinder pathFinder = new PathFinder(scene.getRoadGraph());
        Path givenPath = pathFinder.findBestPath(start, end, 0);
        PathComponent pathComponent = NPC.getComponent(PathComponent.class);
        pathComponent.setPath(givenPath);
        Path foundBestPath = pathComponent.getPath();

        Path expectedBestPath = new Path();
        NodeConnection nodeConnection1 = new NodeConnection(new NodeRoad(npcPos),
                new NodeRoad(new TerrainPosition(20, 17)));
        for (int z = 19; z > 17; z--) {
            nodeConnection1.addRoad(new NormalRoad(new TerrainPosition(20, z)));
        }
        nodeConnection1.addRoad(new NodeRoad(new TerrainPosition(20, 17)));

        NodeConnection nodeConnection2 = new NodeConnection(new NodeRoad(new TerrainPosition(20, 17)),
                new NodeRoad(new TerrainPosition(20, 14)));
        for (int z = 17; z > 14; z--) {
            nodeConnection2.addRoad(new NormalRoad(new TerrainPosition(20, z)));
        }
        nodeConnection2.addRoad(new NodeRoad(new TerrainPosition(20, 14)));

        NodeConnection nodeConnection3 = new NodeConnection(new NodeRoad(new TerrainPosition(20, 14)),
                new NodeRoad(new TerrainPosition(23, 14)));
        nodeConnection3.addRoad(new NormalRoad(new TerrainPosition(21, 14)));
        nodeConnection3.addRoad(new NormalRoad(new TerrainPosition(22, 14)));
        nodeConnection3.addRoad(new NodeRoad(new TerrainPosition(23, 14)));

        NodeConnection nodeConnection4 = new NodeConnection(new NodeRoad(new TerrainPosition(23, 14)),
                new NormalRoad(end));
        nodeConnection4.addRoad(new NormalRoad(new TerrainPosition(24, 14)));
        nodeConnection4.addRoad(new NormalRoad(end));

        expectedBestPath.add(nodeConnection1);
        expectedBestPath.add(nodeConnection2);
        expectedBestPath.add(nodeConnection3);
        expectedBestPath.add(nodeConnection4);

        assertTrue(foundBestPath.comparePaths(expectedBestPath));
        assertTrue(foundBestPath.compareCost(expectedBestPath));
    }

    @Test
    /**
     * See Case7
     */
    void testPath7() {
        TerrainPosition start = new TerrainPosition(10, 14);
        TerrainPosition end = new TerrainPosition(25, 14);
        TerrainPosition npcPos = new TerrainPosition(20, 20);

        GameObject.newInstance(DirtRoad.class, start);
        GameObject.newInstance(DirtRoad.class, end);
        GameObject.newInstance(DirtRoad.class, npcPos);

        for (int x = 11; x < 25; x++) {
            GameObject.newInstance(DirtRoad.class, new TerrainPosition(x, 14));
        }
        for (int z = 11; z < 20; z++) {
            GameObject.newInstance(DirtRoad.class, new TerrainPosition(12, z));
            GameObject.newInstance(DirtRoad.class, new TerrainPosition(20, z));
        }

        GameObject.newInstance(DirtRoad.class, new TerrainPosition(19, 17));
        GameObject.newInstance(DirtRoad.class, new TerrainPosition(19, 20));
        GameObject.newInstance(DirtRoad.class, new TerrainPosition(21, 20));
        GameObject.newInstance(DirtRoad.class, new TerrainPosition(23, 13));
        GameObject.newInstance(DirtRoad.class, new TerrainPosition(25, 13)); // Case7
        GameObject.newInstance(DirtRoad.class, new TerrainPosition(25, 15)); // Case7
        GameObject.newInstance(DirtRoad.class, new TerrainPosition(16, 15));

        PathFinder pathFinder = new PathFinder(scene.getRoadGraph());
        Path givenPath = pathFinder.findBestPath(start, end, 0);
        PathComponent pathComponent = NPC.getComponent(PathComponent.class);
        pathComponent.setPath(givenPath);
        Path foundBestPath = pathComponent.getPath();

        Path expectedBestPath = new Path();
        NodeConnection nodeConnection1 = new NodeConnection(new NodeRoad(npcPos),
                new NodeRoad(new TerrainPosition(20, 17)));
        for (int z = 19; z > 17; z--) {
            nodeConnection1.addRoad(new NormalRoad(new TerrainPosition(20, z)));
        }
        nodeConnection1.addRoad(new NodeRoad(new TerrainPosition(20, 17)));

        NodeConnection nodeConnection2 = new NodeConnection(new NodeRoad(new TerrainPosition(20, 17)),
                new NodeRoad(new TerrainPosition(20, 14)));
        for (int z = 17; z > 14; z--) {
            nodeConnection2.addRoad(new NormalRoad(new TerrainPosition(20, z)));
        }
        nodeConnection2.addRoad(new NodeRoad(new TerrainPosition(20, 14)));

        NodeConnection nodeConnection3 = new NodeConnection(new NodeRoad(new TerrainPosition(20, 14)),
                new NodeRoad(new TerrainPosition(23, 14)));
        nodeConnection3.addRoad(new NormalRoad(new TerrainPosition(21, 14)));
        nodeConnection3.addRoad(new NormalRoad(new TerrainPosition(22, 14)));
        nodeConnection3.addRoad(new NodeRoad(new TerrainPosition(23, 14)));

        NodeConnection nodeConnection4 = new NodeConnection(new NodeRoad(new TerrainPosition(23, 14)),
                new NodeRoad(end));
        nodeConnection4.addRoad(new NormalRoad(new TerrainPosition(24, 14)));
        nodeConnection4.addRoad(new NodeRoad(end));

        expectedBestPath.add(nodeConnection1);
        expectedBestPath.add(nodeConnection2);
        expectedBestPath.add(nodeConnection3);
        expectedBestPath.add(nodeConnection4);

        assertTrue(foundBestPath.comparePaths(expectedBestPath));
        assertTrue(foundBestPath.compareCost(expectedBestPath));
    }

    @Test
    /**
     * See Case8
     */
    void testPath8() {
        TerrainPosition start = new TerrainPosition(10, 14);
        TerrainPosition end = new TerrainPosition(25, 14);
        TerrainPosition npcPos = new TerrainPosition(20, 20);

        GameObject.newInstance(DirtRoad.class, start);
        GameObject.newInstance(DirtRoad.class, end);
        GameObject.newInstance(DirtRoad.class, npcPos);

        for (int x = 11; x < 25; x++) {
            GameObject.newInstance(DirtRoad.class, new TerrainPosition(x, 14));
        }
        for (int z = 11; z < 20; z++) {
            GameObject.newInstance(DirtRoad.class, new TerrainPosition(12, z));
            GameObject.newInstance(DirtRoad.class, new TerrainPosition(20, z));
        }

        GameObject.newInstance(DirtRoad.class, new TerrainPosition(10, 13)); // Case8
        GameObject.newInstance(DirtRoad.class, new TerrainPosition(10, 15)); // Case8
        GameObject.newInstance(DirtRoad.class, new TerrainPosition(19, 17));
        GameObject.newInstance(DirtRoad.class, new TerrainPosition(19, 20));
        GameObject.newInstance(DirtRoad.class, new TerrainPosition(21, 20));
        GameObject.newInstance(DirtRoad.class, new TerrainPosition(23, 13));
        GameObject.newInstance(DirtRoad.class, new TerrainPosition(25, 13));
        GameObject.newInstance(DirtRoad.class, new TerrainPosition(25, 15));
        GameObject.newInstance(DirtRoad.class, new TerrainPosition(16, 15));

        PathFinder pathFinder = new PathFinder(scene.getRoadGraph());
        Path givenPath = pathFinder.findBestPath(start, end, 0);
        PathComponent pathComponent = NPC.getComponent(PathComponent.class);
        pathComponent.setPath(givenPath);
        Path foundBestPath = pathComponent.getPath();

        Path expectedBestPath = new Path();
        NodeConnection nodeConnection1 = new NodeConnection(new NodeRoad(npcPos),
                new NodeRoad(new TerrainPosition(20, 17)));
        for (int z = 19; z > 17; z--) {
            nodeConnection1.addRoad(new NormalRoad(new TerrainPosition(20, z)));
        }
        nodeConnection1.addRoad(new NodeRoad(new TerrainPosition(20, 17)));

        NodeConnection nodeConnection2 = new NodeConnection(new NodeRoad(new TerrainPosition(20, 17)),
                new NodeRoad(new TerrainPosition(20, 14)));
        for (int z = 17; z > 14; z--) {
            nodeConnection2.addRoad(new NormalRoad(new TerrainPosition(20, z)));
        }
        nodeConnection2.addRoad(new NodeRoad(new TerrainPosition(20, 14)));

        NodeConnection nodeConnection3 = new NodeConnection(new NodeRoad(new TerrainPosition(20, 14)),
                new NodeRoad(new TerrainPosition(23, 14)));
        nodeConnection3.addRoad(new NormalRoad(new TerrainPosition(21, 14)));
        nodeConnection3.addRoad(new NormalRoad(new TerrainPosition(22, 14)));
        nodeConnection3.addRoad(new NodeRoad(new TerrainPosition(23, 14)));

        NodeConnection nodeConnection4 = new NodeConnection(new NodeRoad(new TerrainPosition(23, 14)),
                new NodeRoad(end));
        nodeConnection4.addRoad(new NormalRoad(new TerrainPosition(24, 14)));
        nodeConnection4.addRoad(new NodeRoad(end));

        expectedBestPath.add(nodeConnection1);
        expectedBestPath.add(nodeConnection2);
        expectedBestPath.add(nodeConnection3);
        expectedBestPath.add(nodeConnection4);

        assertTrue(foundBestPath.comparePaths(expectedBestPath));
        assertTrue(foundBestPath.compareCost(expectedBestPath));
    }

    @Test
    /**
     * See Case9
     */
    void testPath9() {
        TerrainPosition start = new TerrainPosition(10, 14);
        TerrainPosition end = new TerrainPosition(25, 14);
        TerrainPosition npcPos = new TerrainPosition(20, 20);

        GameObject.newInstance(DirtRoad.class, start);
        GameObject.newInstance(DirtRoad.class, end);
        GameObject.newInstance(DirtRoad.class, npcPos);

        for (int x = 11; x < 25; x++) {
            GameObject.newInstance(DirtRoad.class, new TerrainPosition(x, 14));
        }
        for (int z = 11; z < 20; z++) {
            GameObject.newInstance(DirtRoad.class, new TerrainPosition(12, z));
            GameObject.newInstance(DirtRoad.class, new TerrainPosition(20, z));
        }

        GameObject.newInstance(DirtRoad.class, new TerrainPosition(10, 13));
        GameObject.newInstance(DirtRoad.class, new TerrainPosition(10, 15));
        GameObject.newInstance(DirtRoad.class, new TerrainPosition(19, 17));
        GameObject.newInstance(DirtRoad.class, new TerrainPosition(19, 20));
        GameObject.newInstance(DirtRoad.class, new TerrainPosition(21, 20));
        GameObject.newInstance(DirtRoad.class, new TerrainPosition(23, 13));
        GameObject.newInstance(DirtRoad.class, new TerrainPosition(16, 15));

        PathFinder pathFinder = new PathFinder(scene.getRoadGraph());
        Path givenPath = pathFinder.findBestPath(start, end, 0);
        PathComponent pathComponent = NPC.getComponent(PathComponent.class);
        pathComponent.setPath(givenPath);
        Path foundBestPath = pathComponent.getPath();

        Path expectedBestPath = new Path();
        NodeConnection nodeConnection1 = new NodeConnection(new NodeRoad(npcPos),
                new NodeRoad(new TerrainPosition(20, 17)));
        for (int z = 19; z > 17; z--) {
            nodeConnection1.addRoad(new NormalRoad(new TerrainPosition(20, z)));
        }
        nodeConnection1.addRoad(new NodeRoad(new TerrainPosition(20, 17)));

        NodeConnection nodeConnection2 = new NodeConnection(new NodeRoad(new TerrainPosition(20, 17)),
                new NodeRoad(new TerrainPosition(20, 14)));
        for (int z = 17; z > 14; z--) {
            nodeConnection2.addRoad(new NormalRoad(new TerrainPosition(20, z)));
        }
        nodeConnection2.addRoad(new NodeRoad(new TerrainPosition(20, 14)));

        NodeConnection nodeConnection3 = new NodeConnection(new NodeRoad(new TerrainPosition(20, 14)),
                new NodeRoad(new TerrainPosition(23, 14)));
        nodeConnection3.addRoad(new NormalRoad(new TerrainPosition(21, 14)));
        nodeConnection3.addRoad(new NormalRoad(new TerrainPosition(22, 14)));
        nodeConnection3.addRoad(new NodeRoad(new TerrainPosition(23, 14)));

        NodeConnection nodeConnection4 = new NodeConnection(new NodeRoad(new TerrainPosition(23, 14)),
                new NormalRoad(end));
        nodeConnection4.addRoad(new NormalRoad(new TerrainPosition(24, 14)));
        nodeConnection4.addRoad(new NormalRoad(end));

        expectedBestPath.add(nodeConnection1);
        expectedBestPath.add(nodeConnection2);
        expectedBestPath.add(nodeConnection3);
        expectedBestPath.add(nodeConnection4);

        assertTrue(foundBestPath.comparePaths(expectedBestPath));
        assertTrue(foundBestPath.compareCost(expectedBestPath));
    }

    @Test
    /**
     * See Case10
     */
    void testPath10() {
        TerrainPosition start = new TerrainPosition(10, 14);
        TerrainPosition end = new TerrainPosition(25, 14);
        TerrainPosition npcPos = new TerrainPosition(20, 20);

        GameObject.newInstance(DirtRoad.class, start);
        GameObject.newInstance(DirtRoad.class, end);
        GameObject.newInstance(DirtRoad.class, npcPos);

        for (int x = 11; x < 25; x++) {
            GameObject.newInstance(DirtRoad.class, new TerrainPosition(x, 14));
        }
        for (int z = 11; z < 20; z++) {
            GameObject.newInstance(DirtRoad.class, new TerrainPosition(12, z));
            GameObject.newInstance(DirtRoad.class, new TerrainPosition(20, z));
        }

        GameObject.newInstance(DirtRoad.class, new TerrainPosition(10, 13));
        GameObject.newInstance(DirtRoad.class, new TerrainPosition(10, 15));
        GameObject.newInstance(DirtRoad.class, new TerrainPosition(19, 17));
        GameObject.newInstance(DirtRoad.class, new TerrainPosition(19, 20));
        GameObject.newInstance(DirtRoad.class, new TerrainPosition(21, 20));
        GameObject.newInstance(DirtRoad.class, new TerrainPosition(23, 13));
        GameObject.newInstance(DirtRoad.class, new TerrainPosition(16, 15));

        { // Case10
            GameObject.newInstance(DirtRoad.class, new TerrainPosition(20, 13));
            GameObject.newInstance(DirtRoad.class, new TerrainPosition(20, 12));
            GameObject.newInstance(DirtRoad.class, new TerrainPosition(20, 11));
            GameObject.newInstance(DirtRoad.class, new TerrainPosition(21, 11));
            GameObject.newInstance(DirtRoad.class, new TerrainPosition(22, 11));
            GameObject.newInstance(DirtRoad.class, new TerrainPosition(23, 11));
            GameObject.newInstance(DirtRoad.class, new TerrainPosition(24, 11));
            GameObject.newInstance(DirtRoad.class, new TerrainPosition(25, 11));
            GameObject.newInstance(DirtRoad.class, new TerrainPosition(25, 12));
            GameObject.newInstance(DirtRoad.class, new TerrainPosition(25, 13));
        }

        PathFinder pathFinder = new PathFinder(scene.getRoadGraph());
        Path givenPath = pathFinder.findBestPath(start, end, 0);
        PathComponent pathComponent = NPC.getComponent(PathComponent.class);
        pathComponent.setPath(givenPath);
        Path foundBestPath = pathComponent.getPath();

        Path expectedBestPath = new Path();
        NodeConnection nodeConnection1 = new NodeConnection(new NodeRoad(npcPos),
                new NodeRoad(new TerrainPosition(20, 17)));
        for (int z = 19; z > 17; z--) {
            nodeConnection1.addRoad(new NormalRoad(new TerrainPosition(20, z)));
        }
        nodeConnection1.addRoad(new NodeRoad(new TerrainPosition(20, 17)));

        NodeConnection nodeConnection2 = new NodeConnection(new NodeRoad(new TerrainPosition(20, 17)),
                new NodeRoad(new TerrainPosition(20, 14)));
        for (int z = 17; z > 14; z--) {
            nodeConnection2.addRoad(new NormalRoad(new TerrainPosition(20, z)));
        }
        nodeConnection2.addRoad(new NodeRoad(new TerrainPosition(20, 14)));

        NodeConnection nodeConnection3 = new NodeConnection(new NodeRoad(new TerrainPosition(20, 14)),
                new NodeRoad(new TerrainPosition(23, 14)));
        nodeConnection3.addRoad(new NormalRoad(new TerrainPosition(21, 14)));
        nodeConnection3.addRoad(new NormalRoad(new TerrainPosition(22, 14)));
        nodeConnection3.addRoad(new NodeRoad(new TerrainPosition(23, 14)));

        NodeConnection nodeConnection4 = new NodeConnection(new NodeRoad(new TerrainPosition(23, 14)),
                new NormalRoad(end));
        nodeConnection4.addRoad(new NormalRoad(new TerrainPosition(24, 14)));
        nodeConnection4.addRoad(new NormalRoad(end));

        expectedBestPath.add(nodeConnection1);
        expectedBestPath.add(nodeConnection2);
        expectedBestPath.add(nodeConnection3);
        expectedBestPath.add(nodeConnection4);

        assertTrue(foundBestPath.comparePaths(expectedBestPath));
        assertTrue(foundBestPath.compareCost(expectedBestPath));
    }

    @Test
    /**
     * See Case11
     */
    void testPath11() {
        TerrainPosition start = new TerrainPosition(10, 14);
        TerrainPosition end = new TerrainPosition(25, 14);
        TerrainPosition npcPos = new TerrainPosition(20, 20);

        GameObject.newInstance(DirtRoad.class, start);
        GameObject.newInstance(DirtRoad.class, end);
        GameObject.newInstance(DirtRoad.class, npcPos);

        for (int x = 11; x < 25; x++) {
            GameObject.newInstance(DirtRoad.class, new TerrainPosition(x, 14));
        }
        for (int z = 11; z < 20; z++) {
            GameObject.newInstance(DirtRoad.class, new TerrainPosition(12, z));
            GameObject.newInstance(DirtRoad.class, new TerrainPosition(20, z));
        }

        GameObject.newInstance(DirtRoad.class, new TerrainPosition(10, 13));
        GameObject.newInstance(DirtRoad.class, new TerrainPosition(10, 15));
        GameObject.newInstance(DirtRoad.class, new TerrainPosition(19, 17));
        GameObject.newInstance(DirtRoad.class, new TerrainPosition(19, 20));
        GameObject.newInstance(DirtRoad.class, new TerrainPosition(21, 20));
        GameObject.newInstance(DirtRoad.class, new TerrainPosition(23, 13));
        GameObject.newInstance(DirtRoad.class, new TerrainPosition(16, 15));
        GameObject.newInstance(DirtRoad.class, new TerrainPosition(26, 14)); // Case11

        {
            GameObject.newInstance(DirtRoad.class, new TerrainPosition(20, 13));
            GameObject.newInstance(DirtRoad.class, new TerrainPosition(20, 12));
            GameObject.newInstance(DirtRoad.class, new TerrainPosition(20, 11));
            GameObject.newInstance(DirtRoad.class, new TerrainPosition(21, 11));
            GameObject.newInstance(DirtRoad.class, new TerrainPosition(22, 11));
            GameObject.newInstance(DirtRoad.class, new TerrainPosition(23, 11));
            GameObject.newInstance(DirtRoad.class, new TerrainPosition(24, 11));
            GameObject.newInstance(DirtRoad.class, new TerrainPosition(25, 11));
            GameObject.newInstance(DirtRoad.class, new TerrainPosition(25, 12));
            GameObject.newInstance(DirtRoad.class, new TerrainPosition(25, 13));
        }

        PathFinder pathFinder = new PathFinder(scene.getRoadGraph());
        Path givenPath = pathFinder.findBestPath(start, end, 0);
        PathComponent pathComponent = NPC.getComponent(PathComponent.class);
        pathComponent.setPath(givenPath);
        Path foundBestPath = pathComponent.getPath();

        Path expectedBestPath = new Path();
        NodeConnection nodeConnection1 = new NodeConnection(new NodeRoad(npcPos),
                new NodeRoad(new TerrainPosition(20, 17)));
        for (int z = 19; z > 17; z--) {
            nodeConnection1.addRoad(new NormalRoad(new TerrainPosition(20, z)));
        }
        nodeConnection1.addRoad(new NodeRoad(new TerrainPosition(20, 17)));

        NodeConnection nodeConnection2 = new NodeConnection(new NodeRoad(new TerrainPosition(20, 17)),
                new NodeRoad(new TerrainPosition(20, 14)));
        for (int z = 17; z > 14; z--) {
            nodeConnection2.addRoad(new NormalRoad(new TerrainPosition(20, z)));
        }
        nodeConnection2.addRoad(new NodeRoad(new TerrainPosition(20, 14)));

        NodeConnection nodeConnection3 = new NodeConnection(new NodeRoad(new TerrainPosition(20, 14)),
                new NodeRoad(new TerrainPosition(23, 14)));
        nodeConnection3.addRoad(new NormalRoad(new TerrainPosition(21, 14)));
        nodeConnection3.addRoad(new NormalRoad(new TerrainPosition(22, 14)));
        nodeConnection3.addRoad(new NodeRoad(new TerrainPosition(23, 14)));

        NodeConnection nodeConnection4 = new NodeConnection(new NodeRoad(new TerrainPosition(23, 14)),
                new NodeRoad(end));
        nodeConnection4.addRoad(new NormalRoad(new TerrainPosition(24, 14)));
        nodeConnection4.addRoad(new NodeRoad(end));

        expectedBestPath.add(nodeConnection1);
        expectedBestPath.add(nodeConnection2);
        expectedBestPath.add(nodeConnection3);
        expectedBestPath.add(nodeConnection4);

        assertTrue(foundBestPath.comparePaths(expectedBestPath));
        assertTrue(foundBestPath.compareCost(expectedBestPath));
    }

    @Test
    /**
     * See Case12
     */
    void testPath12() {
        TerrainPosition start = new TerrainPosition(10, 14);
        TerrainPosition end = new TerrainPosition(25, 14);
        TerrainPosition npcPos = new TerrainPosition(20, 20);

        GameObject.newInstance(DirtRoad.class, start);
        GameObject.newInstance(DirtRoad.class, end);
        GameObject.newInstance(DirtRoad.class, npcPos);

        for (int x = 11; x < 25; x++) {
            GameObject.newInstance(DirtRoad.class, new TerrainPosition(x, 14));
        }
        for (int z = 11; z < 20; z++) {
            GameObject.newInstance(DirtRoad.class, new TerrainPosition(12, z));
            GameObject.newInstance(DirtRoad.class, new TerrainPosition(20, z));
        }

        GameObject.newInstance(DirtRoad.class, new TerrainPosition(10, 13));
        GameObject.newInstance(DirtRoad.class, new TerrainPosition(10, 15));
        GameObject.newInstance(DirtRoad.class, new TerrainPosition(19, 17));
        GameObject.newInstance(DirtRoad.class, new TerrainPosition(19, 20));
        GameObject.newInstance(DirtRoad.class, new TerrainPosition(21, 20));
        GameObject.newInstance(DirtRoad.class, new TerrainPosition(23, 13));
        GameObject.newInstance(DirtRoad.class, new TerrainPosition(16, 15));
        GameObject.newInstance(DirtRoad.class, new TerrainPosition(26, 14));

        {
            GameObject.newInstance(DirtRoad.class, new TerrainPosition(20, 13));
            GameObject.newInstance(DirtRoad.class, new TerrainPosition(20, 12));
            GameObject.newInstance(DirtRoad.class, new TerrainPosition(20, 11));
            GameObject.newInstance(DirtRoad.class, new TerrainPosition(21, 11));
            GameObject.newInstance(DirtRoad.class, new TerrainPosition(22, 11));
            GameObject.newInstance(DirtRoad.class, new TerrainPosition(23, 11));
            GameObject.newInstance(DirtRoad.class, new TerrainPosition(24, 11));
            GameObject.newInstance(DirtRoad.class, new TerrainPosition(25, 11));
            GameObject.newInstance(DirtRoad.class, new TerrainPosition(25, 12));
            GameObject.newInstance(DirtRoad.class, new TerrainPosition(25, 13));
            GameObject.newInstance(DirtRoad.class, new TerrainPosition(20, 10)); // Case12
        }

        PathFinder pathFinder = new PathFinder(scene.getRoadGraph());
        Path givenPath = pathFinder.findBestPath(start, end, 0);
        PathComponent pathComponent = NPC.getComponent(PathComponent.class);
        pathComponent.setPath(givenPath);
        Path foundBestPath = pathComponent.getPath();

        Path expectedBestPath = new Path();
        NodeConnection nodeConnection1 = new NodeConnection(new NodeRoad(npcPos),
                new NodeRoad(new TerrainPosition(20, 17)));
        for (int z = 19; z > 17; z--) {
            nodeConnection1.addRoad(new NormalRoad(new TerrainPosition(20, z)));
        }
        nodeConnection1.addRoad(new NodeRoad(new TerrainPosition(20, 17)));

        NodeConnection nodeConnection2 = new NodeConnection(new NodeRoad(new TerrainPosition(20, 17)),
                new NodeRoad(new TerrainPosition(20, 14)));
        for (int z = 17; z > 14; z--) {
            nodeConnection2.addRoad(new NormalRoad(new TerrainPosition(20, z)));
        }
        nodeConnection2.addRoad(new NodeRoad(new TerrainPosition(20, 14)));

        NodeConnection nodeConnection3 = new NodeConnection(new NodeRoad(new TerrainPosition(20, 14)),
                new NodeRoad(new TerrainPosition(23, 14)));
        nodeConnection3.addRoad(new NormalRoad(new TerrainPosition(21, 14)));
        nodeConnection3.addRoad(new NormalRoad(new TerrainPosition(22, 14)));
        nodeConnection3.addRoad(new NodeRoad(new TerrainPosition(23, 14)));

        NodeConnection nodeConnection4 = new NodeConnection(new NodeRoad(new TerrainPosition(23, 14)),
                new NodeRoad(end));
        nodeConnection4.addRoad(new NormalRoad(new TerrainPosition(24, 14)));
        nodeConnection4.addRoad(new NodeRoad(end));

        expectedBestPath.add(nodeConnection1);
        expectedBestPath.add(nodeConnection2);
        expectedBestPath.add(nodeConnection3);
        expectedBestPath.add(nodeConnection4);

        assertTrue(foundBestPath.comparePaths(expectedBestPath));
        assertTrue(foundBestPath.compareCost(expectedBestPath));
    }

    @Test
    /**
     * See Case13
     * Removed road
     */
    void testPath13() {
        TerrainPosition start = new TerrainPosition(10, 14);
        TerrainPosition end = new TerrainPosition(25, 14);
        TerrainPosition npcPos = new TerrainPosition(20, 20);

        GameObject.newInstance(DirtRoad.class, start);
        GameObject.newInstance(DirtRoad.class, end);
        GameObject.newInstance(DirtRoad.class, npcPos);

        for (int x = 11; x < 25; x++) {
            if (x != 21) // Case13
                GameObject.newInstance(DirtRoad.class, new TerrainPosition(x, 14));
        }
        for (int z = 11; z < 20; z++) {
            GameObject.newInstance(DirtRoad.class, new TerrainPosition(12, z));
            GameObject.newInstance(DirtRoad.class, new TerrainPosition(20, z));
        }

        GameObject.newInstance(DirtRoad.class, new TerrainPosition(10, 13));
        GameObject.newInstance(DirtRoad.class, new TerrainPosition(10, 15));
        GameObject.newInstance(DirtRoad.class, new TerrainPosition(19, 17));
        GameObject.newInstance(DirtRoad.class, new TerrainPosition(19, 20));
        GameObject.newInstance(DirtRoad.class, new TerrainPosition(21, 20));
        GameObject.newInstance(DirtRoad.class, new TerrainPosition(23, 13));
        GameObject.newInstance(DirtRoad.class, new TerrainPosition(16, 15));
        GameObject.newInstance(DirtRoad.class, new TerrainPosition(26, 14));

        {
            GameObject.newInstance(DirtRoad.class, new TerrainPosition(20, 13));
            GameObject.newInstance(DirtRoad.class, new TerrainPosition(20, 12));
            GameObject.newInstance(DirtRoad.class, new TerrainPosition(20, 11));
            GameObject.newInstance(DirtRoad.class, new TerrainPosition(21, 11));
            GameObject.newInstance(DirtRoad.class, new TerrainPosition(22, 11));
            GameObject.newInstance(DirtRoad.class, new TerrainPosition(23, 11));
            GameObject.newInstance(DirtRoad.class, new TerrainPosition(24, 11));
            GameObject.newInstance(DirtRoad.class, new TerrainPosition(25, 11));
            GameObject.newInstance(DirtRoad.class, new TerrainPosition(25, 12));
            GameObject.newInstance(DirtRoad.class, new TerrainPosition(25, 13));
            GameObject.newInstance(DirtRoad.class, new TerrainPosition(20, 10));
        }

        PathFinder pathFinder = new PathFinder(scene.getRoadGraph());
        Path givenPath = pathFinder.findBestPath(start, end, 0);

        PathComponent pathComponent = NPC.getComponent(PathComponent.class);
        pathComponent.setPath(givenPath);
        Path foundBestPath = pathComponent.getPath();

        GameObject.newInstance(DirtRoad.class, new TerrainPosition(21, 14)); // Case13

        Path expectedBestPath = new Path();
        NodeConnection nodeConnection1 = new NodeConnection(new NodeRoad(npcPos),
                new NodeRoad(new TerrainPosition(20, 17)));
        for (int z = 19; z > 17; z--) {
            nodeConnection1.addRoad(new NormalRoad(new TerrainPosition(20, z)));
        }
        nodeConnection1.addRoad(new NodeRoad(new TerrainPosition(20, 17)));

        NodeConnection nodeConnection2 = new NodeConnection(new NodeRoad(new TerrainPosition(20, 17)),
                new NodeRoad(new TerrainPosition(20, 14)));
        for (int z = 17; z > 14; z--) {
            nodeConnection2.addRoad(new NormalRoad(new TerrainPosition(20, z)));
        }
        nodeConnection2.addRoad(new NodeRoad(new TerrainPosition(20, 14)));

        NodeConnection nodeConnection3 = new NodeConnection(new NodeRoad(new TerrainPosition(20, 14)),
                new NodeRoad(new TerrainPosition(20, 11)));
        nodeConnection3.addRoad(new NormalRoad(new TerrainPosition(20, 13)));
        nodeConnection3.addRoad(new NormalRoad(new TerrainPosition(20, 12)));
        nodeConnection3.addRoad(new NodeRoad(new TerrainPosition(20, 11)));

        NodeConnection nodeConnection4 = new NodeConnection(new NodeRoad(new TerrainPosition(20, 11)),
                new NodeRoad(end));
        nodeConnection4.addRoad(new NormalRoad(new TerrainPosition(21, 11)));
        nodeConnection4.addRoad(new NormalRoad(new TerrainPosition(22, 11)));
        nodeConnection4.addRoad(new NormalRoad(new TerrainPosition(23, 11)));
        nodeConnection4.addRoad(new NormalRoad(new TerrainPosition(24, 11)));
        nodeConnection4.addRoad(new NormalRoad(new TerrainPosition(25, 11)));
        nodeConnection4.addRoad(new NormalRoad(new TerrainPosition(25, 12)));
        nodeConnection4.addRoad(new NormalRoad(new TerrainPosition(25, 13)));
        nodeConnection4.addRoad(new NodeRoad(end));

        expectedBestPath.add(nodeConnection1);
        expectedBestPath.add(nodeConnection2);
        expectedBestPath.add(nodeConnection3);
        expectedBestPath.add(nodeConnection4);

        assertTrue(foundBestPath.comparePaths(expectedBestPath));
        assertTrue(foundBestPath.compareCost(expectedBestPath));
    }

    @Test
    /**
     * See Case14
     * Removed road
     */
    void testPath14() {
        TerrainPosition start = new TerrainPosition(10, 14);
        TerrainPosition end = new TerrainPosition(25, 14);
        TerrainPosition npcPos = new TerrainPosition(20, 20);

        GameObject.newInstance(DirtRoad.class, start);
        GameObject.newInstance(DirtRoad.class, end);
        GameObject.newInstance(DirtRoad.class, npcPos);

        for (int x = 11; x < 25; x++) {
            if (x != 21) // Case14
                GameObject.newInstance(DirtRoad.class, new TerrainPosition(x, 14));
        }
        for (int z = 11; z < 20; z++) {
            GameObject.newInstance(DirtRoad.class, new TerrainPosition(12, z));
            GameObject.newInstance(DirtRoad.class, new TerrainPosition(20, z));
        }

        GameObject.newInstance(DirtRoad.class, new TerrainPosition(10, 13));
        GameObject.newInstance(DirtRoad.class, new TerrainPosition(10, 15));
        GameObject.newInstance(DirtRoad.class, new TerrainPosition(19, 17));
        GameObject.newInstance(DirtRoad.class, new TerrainPosition(19, 20));
        GameObject.newInstance(DirtRoad.class, new TerrainPosition(21, 20));
        GameObject.newInstance(DirtRoad.class, new TerrainPosition(23, 13));
        GameObject.newInstance(DirtRoad.class, new TerrainPosition(16, 15));
        GameObject.newInstance(DirtRoad.class, new TerrainPosition(26, 14));

        {
            GameObject.newInstance(DirtRoad.class, new TerrainPosition(20, 13));
            GameObject.newInstance(DirtRoad.class, new TerrainPosition(20, 12));
            GameObject.newInstance(DirtRoad.class, new TerrainPosition(20, 11));
            GameObject.newInstance(DirtRoad.class, new TerrainPosition(21, 11));
            GameObject.newInstance(DirtRoad.class, new TerrainPosition(22, 11));
            GameObject.newInstance(DirtRoad.class, new TerrainPosition(23, 11));
            GameObject.newInstance(DirtRoad.class, new TerrainPosition(24, 11));
            GameObject.newInstance(DirtRoad.class, new TerrainPosition(25, 11));
            GameObject.newInstance(DirtRoad.class, new TerrainPosition(25, 12));
            GameObject.newInstance(DirtRoad.class, new TerrainPosition(25, 13));
            GameObject.newInstance(DirtRoad.class, new TerrainPosition(20, 10));
        }

        PathFinder pathFinder = new PathFinder(scene.getRoadGraph());
        Path givenPath = pathFinder.findBestPath(start, end, 0);

        PathComponent pathComponent = NPC.getComponent(PathComponent.class);
        pathComponent.setPath(givenPath);
        Path foundBestPath = pathComponent.getPath();

        Path expectedBestPath = new Path();
        NodeConnection nodeConnection1 = new NodeConnection(new NodeRoad(npcPos),
                new NodeRoad(new TerrainPosition(20, 17)));
        for (int z = 19; z > 17; z--) {
            nodeConnection1.addRoad(new NormalRoad(new TerrainPosition(20, z)));
        }
        nodeConnection1.addRoad(new NodeRoad(new TerrainPosition(20, 17)));

        NodeConnection nodeConnection2 = new NodeConnection(new NodeRoad(new TerrainPosition(20, 17)),
                new NodeRoad(new TerrainPosition(20, 14)));
        for (int z = 17; z > 14; z--) {
            nodeConnection2.addRoad(new NormalRoad(new TerrainPosition(20, z)));
        }
        nodeConnection2.addRoad(new NodeRoad(new TerrainPosition(20, 14)));

        NodeConnection nodeConnection3 = new NodeConnection(new NodeRoad(new TerrainPosition(20, 14)),
                new NodeRoad(new TerrainPosition(20, 11)));
        nodeConnection3.addRoad(new NormalRoad(new TerrainPosition(20, 13)));
        nodeConnection3.addRoad(new NormalRoad(new TerrainPosition(20, 12)));
        nodeConnection3.addRoad(new NodeRoad(new TerrainPosition(20, 11)));

        NodeConnection nodeConnection4 = new NodeConnection(new NodeRoad(new TerrainPosition(20, 11)),
                new NodeRoad(end));
        nodeConnection4.addRoad(new NormalRoad(new TerrainPosition(21, 11)));
        nodeConnection4.addRoad(new NormalRoad(new TerrainPosition(22, 11)));
        nodeConnection4.addRoad(new NormalRoad(new TerrainPosition(23, 11)));
        nodeConnection4.addRoad(new NormalRoad(new TerrainPosition(24, 11)));
        nodeConnection4.addRoad(new NormalRoad(new TerrainPosition(25, 11)));
        nodeConnection4.addRoad(new NormalRoad(new TerrainPosition(25, 12)));
        nodeConnection4.addRoad(new NormalRoad(new TerrainPosition(25, 13)));
        nodeConnection4.addRoad(new NodeRoad(end));

        expectedBestPath.add(nodeConnection1);
        expectedBestPath.add(nodeConnection2);
        expectedBestPath.add(nodeConnection3);
        expectedBestPath.add(nodeConnection4);

        assertTrue(foundBestPath.comparePaths(expectedBestPath));
        assertTrue(foundBestPath.compareCost(expectedBestPath));
    }

    @Test
    /**
     * See Case15
     */
    void testPath15() {
        TerrainPosition start = new TerrainPosition(20, 14);
        TerrainPosition end = new TerrainPosition(25, 14);
        TerrainPosition npcPos = new TerrainPosition(20, 20);

        GameObject.newInstance(DirtRoad.class, start);
        GameObject.newInstance(DirtRoad.class, end);
        GameObject.newInstance(DirtRoad.class, npcPos);

        for (int x = 21; x < 25; x++)
            GameObject.newInstance(DirtRoad.class, new TerrainPosition(x, 14));
        for (int z = 15; z < 20; z++)
            GameObject.newInstance(DirtRoad.class, new TerrainPosition(20, z));


        GameObject.newInstance(DirtRoad.class, new TerrainPosition(26, 14));

        PathFinder pathFinder = new PathFinder(scene.getRoadGraph());
        Path givenPath = pathFinder.findBestPath(start, end, 0);
        PathComponent pathComponent = NPC.getComponent(PathComponent.class);
        pathComponent.setPath(givenPath);
        Path foundBestPath = pathComponent.getPath();

        Path expectedBestPath = new Path();
        NodeConnection nodeConnection = new NodeConnection(new NormalRoad(npcPos), new NormalRoad(end));
        for (int z = 19; z > 14; z--)
            nodeConnection.addRoad(new NormalRoad(new TerrainPosition(20, z)));
        for (int x = 20; x < 26; x++)
            nodeConnection.addRoad(new NormalRoad(new TerrainPosition(x, 14)));

        expectedBestPath.add(nodeConnection);

        assertTrue(foundBestPath.comparePaths(expectedBestPath));
        assertTrue(foundBestPath.compareCost(expectedBestPath));
    }

    @Test
    /**
     * See Case16
     */
    void testPath16() {
        TerrainPosition start = new TerrainPosition(20, 14);
        TerrainPosition end = new TerrainPosition(25, 14);
        TerrainPosition npcPos = new TerrainPosition(20, 20);

        GameObject.newInstance(DirtRoad.class, start);
        GameObject.newInstance(DirtRoad.class, end);
        GameObject.newInstance(DirtRoad.class, npcPos);

        for (int x = 21; x < 25; x++)
            if (x != 23) // Case16
                GameObject.newInstance(DirtRoad.class, new TerrainPosition(x, 14));

        for (int z = 15; z < 20; z++) {
            GameObject.newInstance(DirtRoad.class, new TerrainPosition(20, z));
            GameObject.newInstance(DirtRoad.class, new TerrainPosition(22, z));
            GameObject.newInstance(DirtRoad.class, new TerrainPosition(25, z));
        }
        GameObject.newInstance(DirtRoad.class, new TerrainPosition(22, 13));
        GameObject.newInstance(DirtRoad.class, new TerrainPosition(22, 20));
        GameObject.newInstance(DirtRoad.class, new TerrainPosition(25, 20));
        GameObject.newInstance(DirtRoad.class, new TerrainPosition(24, 20));
        GameObject.newInstance(DirtRoad.class, new TerrainPosition(23, 20));

        GameObject.newInstance(DirtRoad.class, new TerrainPosition(26, 14));

        PathFinder pathFinder = new PathFinder(scene.getRoadGraph());
        Path givenPath = pathFinder.findBestPath(start, end, 0);

        GameObject.newInstance(DirtRoad.class, new TerrainPosition(23, 14)); // Case16

        PathComponent pathComponent = NPC.getComponent(PathComponent.class);
        pathComponent.setPath(givenPath);
        Path foundBestPath = pathComponent.getPath();

        Path expectedBestPath = new Path();
        NodeConnection nodeConnection1 = new NodeConnection(new NormalRoad(npcPos),
                new NodeRoad(new TerrainPosition(22, 14)));
        for (int z = 19; z >= 14; z--) {
            nodeConnection1.addRoad(new NormalRoad(new TerrainPosition(20, z)));
        }
        nodeConnection1.addRoad(new NormalRoad(new TerrainPosition(21, 14)));
        nodeConnection1.addRoad(new NodeRoad(new TerrainPosition(22, 14)));

        NodeConnection nodeConnection2 = new NodeConnection(new NodeRoad(new TerrainPosition(22, 14)),
                new NodeRoad(end));
        for (int z = 15; z <= 20; z++) {
            nodeConnection2.addRoad(new NormalRoad(new TerrainPosition(22, z)));
        }
        nodeConnection2.addRoad(new NormalRoad(new TerrainPosition(23, 20)));
        nodeConnection2.addRoad(new NormalRoad(new TerrainPosition(24, 20)));

        for (int z = 20; z > 14; z--) {
            nodeConnection2.addRoad(new NormalRoad(new TerrainPosition(25, z)));
        }

        expectedBestPath.add(nodeConnection1);
        expectedBestPath.add(nodeConnection2);

        assertTrue(foundBestPath.comparePaths(expectedBestPath));
        assertTrue(foundBestPath.compareCost(expectedBestPath));
    }

    @Test
    /**
     * See Case17
     */
    void testPath17() {
        TerrainPosition start = new TerrainPosition(20, 14);
        TerrainPosition end = new TerrainPosition(25, 14);
        TerrainPosition npcPos = new TerrainPosition(20, 20);

        GameObject.newInstance(DirtRoad.class, start);
        GameObject.newInstance(DirtRoad.class, end);
        GameObject.newInstance(DirtRoad.class, npcPos);

        for (int x = 21; x < 25; x++)
            GameObject.newInstance(DirtRoad.class, new TerrainPosition(x, 14));

        for (int z = 15; z < 20; z++)
            GameObject.newInstance(DirtRoad.class, new TerrainPosition(20, z));


        GameObject.newInstance(DirtRoad.class, new TerrainPosition(26, 14));
        { // Case17
            GameObject.newInstance(DirtRoad.class, new TerrainPosition(25, 13));
            GameObject.newInstance(DirtRoad.class, new TerrainPosition(20, 13));
            GameObject.newInstance(DirtRoad.class, new TerrainPosition(19, 20));
            GameObject.newInstance(DirtRoad.class, new TerrainPosition(21, 20));
        }

        PathFinder pathFinder = new PathFinder(scene.getRoadGraph());
        Path givenPath = pathFinder.findBestPath(start, end, 0);
        PathComponent pathComponent = NPC.getComponent(PathComponent.class);
        pathComponent.setPath(givenPath);
        Path foundBestPath = pathComponent.getPath();

        Path expectedBestPath = new Path();
        NodeConnection nodeConnection1 = new NodeConnection(new NodeRoad(npcPos),
                new NodeRoad(new TerrainPosition(20, 14)));
        for (int z = 19; z > 14; z--)
            nodeConnection1.addRoad(new NormalRoad(new TerrainPosition(20, z)));
        nodeConnection1.addRoad(new NodeRoad(new TerrainPosition(20, 14)));

        NodeConnection nodeConnection2 = new NodeConnection(new NodeRoad(new TerrainPosition(20, 14)),
                new NodeRoad(end));
        for (int x = 21; x < 25; x++)
            nodeConnection2.addRoad(new NormalRoad(new TerrainPosition(x, 14)));
        nodeConnection2.addRoad(new NodeRoad(end));

        expectedBestPath.add(nodeConnection1);
        expectedBestPath.add(nodeConnection2);

        assertTrue(foundBestPath.comparePaths(expectedBestPath));
        assertTrue(foundBestPath.compareCost(expectedBestPath));
    }


    @Test
    /**
     * See Case18
     */
    void testPath18() {
        TerrainPosition start = new TerrainPosition(20, 14);
        TerrainPosition end = new TerrainPosition(25, 14);
        TerrainPosition npcPos = new TerrainPosition(20, 20);

        GameObject.newInstance(DirtRoad.class, start);
        GameObject.newInstance(DirtRoad.class, end);
        GameObject.newInstance(DirtRoad.class, npcPos);

        for (int x = 21; x < 25; x++)
            if (x != 23) // Case16
                GameObject.newInstance(DirtRoad.class, new TerrainPosition(x, 14));

        for (int z = 15; z < 20; z++) {
            GameObject.newInstance(DirtRoad.class, new TerrainPosition(20, z));
            GameObject.newInstance(DirtRoad.class, new TerrainPosition(22, z));
            GameObject.newInstance(DirtRoad.class, new TerrainPosition(25, z));
        }
        GameObject.newInstance(DirtRoad.class, new TerrainPosition(22, 13));
        GameObject.newInstance(DirtRoad.class, new TerrainPosition(22, 20));
        GameObject.newInstance(DirtRoad.class, new TerrainPosition(25, 20));
        GameObject.newInstance(DirtRoad.class, new TerrainPosition(24, 20));
        GameObject.newInstance(DirtRoad.class, new TerrainPosition(23, 20));

        GameObject.newInstance(DirtRoad.class, new TerrainPosition(26, 14));
        GameObject.newInstance(DirtRoad.class, new TerrainPosition(21, 20)); // Case18

        PathFinder pathFinder = new PathFinder(scene.getRoadGraph());
        Path givenPath = pathFinder.findBestPath(start, end, 0);

        GameObject.newInstance(DirtRoad.class, new TerrainPosition(23, 14)); // Case16

        PathComponent pathComponent = NPC.getComponent(PathComponent.class);
        pathComponent.setPath(givenPath);
        Path foundBestPath = pathComponent.getPath();

        Path expectedBestPath = new Path();
        NodeConnection nodeConnection1 = new NodeConnection(new NormalRoad(npcPos),
                new NodeRoad(new TerrainPosition(22, 20)));
        nodeConnection1.addRoad(new NormalRoad(new TerrainPosition(21, 20)));
        nodeConnection1.addRoad(new NodeRoad(new TerrainPosition(22, 20)));

        NodeConnection nodeConnection2 = new NodeConnection(new NodeRoad(new TerrainPosition(22, 20)),
                new NodeRoad(end));
        nodeConnection2.addRoad(new NormalRoad(new TerrainPosition(23, 20)));
        nodeConnection2.addRoad(new NormalRoad(new TerrainPosition(24, 20)));
        for (int z = 20; z > 14; z--) {
            nodeConnection2.addRoad(new NormalRoad(new TerrainPosition(25, z)));
        }

        expectedBestPath.add(nodeConnection1);
        expectedBestPath.add(nodeConnection2);

        assertTrue(foundBestPath.comparePaths(expectedBestPath));
        assertTrue(foundBestPath.compareCost(expectedBestPath));
    }


    @Test
    /**
     * See Case19
     */
    void testPath19() {
        TerrainPosition start = new TerrainPosition(20, 14);
        TerrainPosition end = new TerrainPosition(25, 14);
        TerrainPosition npcPos = new TerrainPosition(20, 20);

        GameObject.newInstance(DirtRoad.class, start);
        GameObject.newInstance(DirtRoad.class, end);
        GameObject.newInstance(DirtRoad.class, npcPos);

        for (int x = 21; x < 25; x++)
            if (x != 23) // Case19
                GameObject.newInstance(DirtRoad.class, new TerrainPosition(x, 14));

        for (int z = 15; z < 20; z++) {
            GameObject.newInstance(DirtRoad.class, new TerrainPosition(20, z));
            GameObject.newInstance(DirtRoad.class, new TerrainPosition(22, z));
            GameObject.newInstance(DirtRoad.class, new TerrainPosition(25, z));
        }
        GameObject.newInstance(DirtRoad.class, new TerrainPosition(22, 13));
        GameObject.newInstance(DirtRoad.class, new TerrainPosition(22, 20));
        GameObject.newInstance(DirtRoad.class, new TerrainPosition(25, 20));
        GameObject.newInstance(DirtRoad.class, new TerrainPosition(24, 20));
        GameObject.newInstance(DirtRoad.class, new TerrainPosition(23, 20));

        GameObject.newInstance(DirtRoad.class, new TerrainPosition(26, 14));

        { // Case19
            GameObject.newInstance(DirtRoad.class, new TerrainPosition(26, 20));
        }
        PathFinder pathFinder = new PathFinder(scene.getRoadGraph());
        Path givenPath = pathFinder.findBestPath(start, end, 0);

        PathComponent pathComponent = NPC.getComponent(PathComponent.class);
        pathComponent.setPath(givenPath);
        Path foundBestPath = pathComponent.getPath();

        Path expectedBestPath = new Path();
        NodeConnection nodeConnection1 = new NodeConnection(new NormalRoad(npcPos),
                new NodeRoad(new TerrainPosition(22, 14)));
        NodeConnection nodeConnection2 = new NodeConnection(new NodeRoad(new TerrainPosition(22, 14)),
                new NodeRoad(new TerrainPosition(25, 20)));
        NodeConnection nodeConnection3 = new NodeConnection(new NodeRoad(new TerrainPosition(25, 20)),
                new NormalRoad(end));

        for (int z = 19; z >= 14; z--)
            nodeConnection1.addRoad(new NormalRoad(new TerrainPosition(20, z)));

        nodeConnection1.addRoad(new NormalRoad(new TerrainPosition(21, 14)));
        nodeConnection1.addRoad(new NodeRoad(new TerrainPosition(22, 14)));

        for (int z = 15; z <= 20; z++)
            nodeConnection2.addRoad(new NormalRoad(new TerrainPosition(22, z)));

        nodeConnection2.addRoad(new NormalRoad(new TerrainPosition(23, 20)));
        nodeConnection2.addRoad(new NormalRoad(new TerrainPosition(24, 20)));
        nodeConnection2.addRoad(new NodeRoad(new TerrainPosition(25, 20)));

        for (int z = 19; z > 14; z--)
            nodeConnection3.addRoad(new NormalRoad(new TerrainPosition(25, z)));

        nodeConnection3.addRoad(new NodeRoad(new TerrainPosition(25, 14)));

        expectedBestPath.add(nodeConnection1);
        expectedBestPath.add(nodeConnection2);
        expectedBestPath.add(nodeConnection3);

        assertTrue(foundBestPath.comparePaths(expectedBestPath));
        assertTrue(foundBestPath.compareCost(expectedBestPath));
    }


    @Test
    /**
     * See Case20
     */
    void testPath20() {
        TerrainPosition start = new TerrainPosition(20, 14);
        TerrainPosition end = new TerrainPosition(25, 14);
        TerrainPosition npcPos = new TerrainPosition(20, 20);

        GameObject.newInstance(DirtRoad.class, start);
        GameObject.newInstance(DirtRoad.class, end);
        GameObject.newInstance(DirtRoad.class, npcPos);

        for (int x = 21; x < 25; x++)
            if (x != 23) // Case19
                GameObject.newInstance(DirtRoad.class, new TerrainPosition(x, 14));

        for (int z = 15; z < 20; z++) {
            GameObject.newInstance(DirtRoad.class, new TerrainPosition(20, z));
            GameObject.newInstance(DirtRoad.class, new TerrainPosition(22, z));
            GameObject.newInstance(DirtRoad.class, new TerrainPosition(25, z));
        }
        GameObject.newInstance(DirtRoad.class, new TerrainPosition(22, 13));
        GameObject.newInstance(DirtRoad.class, new TerrainPosition(22, 20));
        GameObject.newInstance(DirtRoad.class, new TerrainPosition(25, 20));
        GameObject.newInstance(DirtRoad.class, new TerrainPosition(24, 20));
        GameObject.newInstance(DirtRoad.class, new TerrainPosition(23, 20));

        GameObject.newInstance(DirtRoad.class, new TerrainPosition(26, 14));

        { // Case20
            GameObject.newInstance(DirtRoad.class, new TerrainPosition(26, 20));
            GameObject.newInstance(DirtRoad.class, new TerrainPosition(19, 18));
            GameObject.newInstance(DirtRoad.class, new TerrainPosition(19, 20));
        }
        PathFinder pathFinder = new PathFinder(scene.getRoadGraph());
        Path givenPath = pathFinder.findBestPath(start, end, 0);

        PathComponent pathComponent = NPC.getComponent(PathComponent.class);
        pathComponent.setPath(givenPath);
        Path foundBestPath = pathComponent.getPath();

        Path expectedBestPath = new Path();
        NodeConnection nodeConnection1 = new NodeConnection(new NormalRoad(npcPos),
                new NodeRoad(new TerrainPosition(20, 18)));
        NodeConnection nodeConnection2 = new NodeConnection(new NodeRoad(new TerrainPosition(20, 18)),
                new NodeRoad(new TerrainPosition(22, 14)));
        NodeConnection nodeConnection3 = new NodeConnection(new NodeRoad(new TerrainPosition(22, 14)),
                new NodeRoad(new TerrainPosition(25, 20)));
        NodeConnection nodeConnection4 = new NodeConnection(new NodeRoad(new TerrainPosition(25, 20)),
                new NormalRoad(end));

        nodeConnection1.addRoad(new NormalRoad(new TerrainPosition(20, 19)));
        nodeConnection1.addRoad(new NodeRoad(new TerrainPosition(20, 18)));

        for (int z = 17; z > 13; z--)
            nodeConnection2.addRoad(new NormalRoad(new TerrainPosition(20, z)));
        nodeConnection2.addRoad(new NormalRoad(new TerrainPosition(21, 14)));
        nodeConnection2.addRoad(new NodeRoad(new TerrainPosition(22, 14)));

        for (int z = 15; z <= 20; z++)
            nodeConnection3.addRoad(new NormalRoad(new TerrainPosition(22, z)));

        nodeConnection3.addRoad(new NormalRoad(new TerrainPosition(23, 20)));
        nodeConnection3.addRoad(new NormalRoad(new TerrainPosition(24, 20)));
        nodeConnection3.addRoad(new NodeRoad(new TerrainPosition(25, 20)));

        for (int z = 19; z > 14; z--)
            nodeConnection4.addRoad(new NormalRoad(new TerrainPosition(25, z)));

        nodeConnection4.addRoad(new NodeRoad(new TerrainPosition(25, 14)));

        expectedBestPath.add(nodeConnection1);
        expectedBestPath.add(nodeConnection2);
        expectedBestPath.add(nodeConnection3);
        expectedBestPath.add(nodeConnection4);

        assertTrue(foundBestPath.comparePaths(expectedBestPath));
        assertTrue(foundBestPath.compareCost(expectedBestPath));
    }
}