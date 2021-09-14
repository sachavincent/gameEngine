package util;

import display.Display;
import engineTester.Game;
import entities.Camera;
import inputs.MouseUtils;
import java.util.AbstractMap.SimpleEntry;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import models.BoundingBox;
import renderEngine.MasterRenderer;
import scene.Scene;
import scene.components.BoundingBoxComponent;
import scene.components.HeightMapComponent;
import scene.components.OffsetComponent;
import scene.components.PositionComponent;
import scene.gameObjects.GameObject;
import scene.gameObjects.Terrain;
import terrain.TerrainPosition;
import util.math.Matrix4f;
import util.math.Plane3D;
import util.math.Vector2f;
import util.math.Vector3f;
import util.math.Vector4f;

public class MousePicker {

    private Vector3f currentRay = new Vector3f();

    private Matrix4f viewMatrix;

    private Vector3f intersectionPoint;

    private static MousePicker instance;

    private GameObject gameObject;

    private MousePicker() {
        this.viewMatrix = Camera.getInstance().getViewMatrix();
    }

    public static MousePicker getInstance() {
        return instance == null ? (instance = new MousePicker()) : instance;
    }

    public Vector3f getIntersectionPoint() {
        return this.intersectionPoint;
    }

    public boolean isPointOnTerrain() {
        return this.intersectionPoint != null;
    }

    public Vector3f getCurrentRay() {
        return currentRay;
    }

    public void update() {
        this.viewMatrix = Camera.getInstance().getViewMatrix();

        this.currentRay = calculateMouseRay();
        this.intersectionPoint = null;

        getHoveredCell();
    }

    public void updateIntersectionOnClick() {
        this.gameObject = null;

        final Map<GameObject, Vector3f> gameObjectIntersections = new HashMap<>();
        // Distance intersection <-> camera
        Scene.getInstance().getGameObjects().forEach(gameObject -> {
            Entry<GameObject, Vector3f> intersection = getIntersectionWithGameObject(gameObject);
            if (intersection != null)
                gameObjectIntersections.put(intersection.getKey(), intersection.getValue());
        });

        calculateIntersectionPoint(gameObjectIntersections);
    }

    private void calculateIntersectionPoint(Map<GameObject, Vector3f> gameObjectIntersections) {
        Entry<GameObject, Vector3f> gameObjectDoubleEntry = gameObjectIntersections.entrySet().stream()
                .filter(Objects::nonNull)
                .min(Comparator.comparingDouble(o -> o.getValue().distance(Camera.getInstance().getPosition())))
                .orElse(null);
        if (gameObjectDoubleEntry != null) {
            this.gameObject = gameObjectDoubleEntry.getKey();
//            this.intersectionPoint = gameObjectDoubleEntry.getValue().sub(new Vector3f(.5f, 0, .5f));
        }
    }

    private Entry<GameObject, Vector3f> getIntersectionWithGameObject(GameObject gameObject) {
        if (gameObject == null)
            return null;
        BoundingBoxComponent boundingBoxComponent = gameObject.getComponent(BoundingBoxComponent.class);
        if (boundingBoxComponent == null)
            return null;
        PositionComponent positionComponent = gameObject.getComponent(PositionComponent.class);
        if (positionComponent == null)
            return null;
        Vector3f position = positionComponent.getPosition();
        OffsetComponent offsetComponent = gameObject.getComponent(OffsetComponent.class);
        if (offsetComponent != null)
            position.add(offsetComponent.getOffset());

        BoundingBox boundingBox = boundingBoxComponent.getBoundingBox();
        Vector3f finalPosition = position;
        Vector3f intersectionPoint = boundingBox.getPlanes().stream()
                .map(plane3D -> {
                    plane3D = plane3D.add(finalPosition);
                    return intersectionWithPlane(plane3D, false);
                }).filter(Objects::nonNull).findFirst().orElse(null);
        if (intersectionPoint == null)
            return null;

        // Distance intersection <-> camera
        return new SimpleEntry<>(gameObject, intersectionPoint);
    }

    public Vector3f intersectionWithPlane(Plane3D plane, boolean print) {
        Vector3f origin = Camera.getInstance().getPosition();

        Vector3f normalPlane = plane.getNormal();
        float dot_dn = Vector3f.dot(this.currentRay, normalPlane);
        if (dot_dn == 0)
            return null;

        Vector3f pointPlane = plane.getPointA();
        float t = -(Vector3f.dot(Vector3f.sub(origin, pointPlane, null), normalPlane)) / dot_dn;

        Vector3f P = Vector3f.add((Vector3f) this.currentRay.scale(t), origin, null);
        P.format();

        if (plane.isPointOnPlane(P)) {
            if (print) {
                System.out.println("t : " + t);
                System.out.println("distance : " + pointPlane.distance(new Vector3f(0, 0, 0)));
                System.out.println("plane : " + plane);
                System.out.println("P : " + P);
            }
            return P;
        }

        return null;
    }

    private Vector3f calculateMouseRay() {
        Vector2f mousePos = MouseUtils.getCursorPos();
        float mouseX = mousePos.getX() * Display.getWindow().getWidth() / 2f + Display.getWindow().getWidth() / 2f;
        float mouseY = mousePos.getY() * Display.getWindow().getHeight() / 2f + Display.getWindow().getHeight() / 2f;
        Vector2f normalizedCoords = getNormalisedDeviceCoordinates(mouseX, mouseY);
        Vector4f clipCoords = new Vector4f(normalizedCoords.getX(), normalizedCoords.getY(), -1.0f, 1.0f);
        Vector4f eyeCoords = toEyeCoords(clipCoords);

        return toWorldCoords(eyeCoords);
    }

    private Vector3f toWorldCoords(Vector4f eyeCoords) {
        Matrix4f invertedView = Matrix4f.invert(this.viewMatrix, null);
        Vector4f rayWorld = Matrix4f.transform(invertedView, eyeCoords, null);
        Vector3f mouseRay = new Vector3f(rayWorld.getX(), rayWorld.getY(), rayWorld.getZ());
        mouseRay.normalise();

        return mouseRay;
    }

    private Vector4f toEyeCoords(Vector4f clipCoords) {
        Matrix4f invertedProjection = Matrix4f.invert(MasterRenderer.getInstance().getProjectionMatrix(), null);
        //TODO: Attribute?
        Vector4f eyeCoords = Matrix4f.transform(invertedProjection, clipCoords, null);

        return new Vector4f(eyeCoords.getX(), eyeCoords.getY(), -1f, 0f);
    }

    private Vector2f getNormalisedDeviceCoordinates(float mouseX, float mouseY) {
        float x = (2.0f * mouseX) / Display.getWindow().getWidth() - 1f;
        float y = (2.0f * mouseY) / Display.getWindow().getHeight() - 1f;

        return new Vector2f(x, y);
    }

    public GameObject getGameObject() {
        return this.gameObject;
    }

    public void getHoveredCell() {
        Terrain terrain = Scene.getInstance().getTerrain();
        if (terrain == null)
            return;
        this.intersectionPoint = null;
        float slopeSpeed = 1.0f;
        Vector3f currPos = new Vector3f(Camera.getInstance().getPosition());

        HeightMapComponent heightMapComponent = terrain.getComponent(HeightMapComponent.class);
        while (Scene.getInstance().isPositionOnTerrain((int) currPos.getX(), (int) currPos.getZ())) {
            int minX = (int) Math.floor(currPos.getX());
            int minZ = (int) Math.floor(currPos.getZ());
            int maxX = minX + 1;
            int maxZ = minZ + 1;

            Vector3f p1 = new Vector3f(minX, heightMapComponent.getHeight(minX, minZ), minZ);
            Vector3f p2 = new Vector3f(maxX, heightMapComponent.getHeight(maxX, minZ), minZ);
            Vector3f p3 = new Vector3f(minX, heightMapComponent.getHeight(minX, maxZ), maxZ);
            Vector3f p4 = new Vector3f(maxX, heightMapComponent.getHeight(maxX, maxZ), maxZ);

            float height = currPos.getY();

            if (p1.getY() >= height || p2.getY() >= height || p3.getY() >= height || p4.getY() >= height) {
                if (Scene.getInstance().isPositionOnTerrain(p1.getX(), p1.getZ())) {
                    TerrainPosition terrainPosition = new TerrainPosition((int) Math.floor(p1.getX()), p1.getY(),
                            (int) Math.floor(p1.getZ()));
                    this.intersectionPoint = terrainPosition.toVector3f();
                    return;
                }
            }
            Vector3f.add(currPos, this.currentRay, currPos);
        }
    }

    private boolean isPosOnTerrain(Vector3f pos) {
        return pos.getX() >= 0 && pos.getZ() >= 0 && pos.getX() < Game.TERRAIN_WIDTH - 1
                && pos.getZ() < Game.TERRAIN_DEPTH - 1;
    }
}