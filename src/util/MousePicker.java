package util;

import engineTester.Game;
import entities.Camera;
import inputs.MouseUtils;
import models.BoundingBox;
import renderEngine.DisplayManager;
import renderEngine.MasterRenderer;
import scene.Scene;
import scene.components.BoundingBoxComponent;
import scene.components.HeightMapComponent;
import scene.components.OffsetComponent;
import scene.components.PositionComponent;
import scene.gameObjects.GameObject;
import scene.gameObjects.Terrain;
import terrain.TerrainPosition;
import util.math.*;

import java.util.AbstractMap.SimpleEntry;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

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
        return getHoveredCell() != null;
    }

    public Vector3f getCurrentRay() {
        return currentRay;
    }

    public void update() {
        this.viewMatrix = Camera.getInstance().getViewMatrix();

        this.currentRay = calculateMouseRay();
        this.gameObject = null;
        this.intersectionPoint = null;
//
//        Map<GameObject, Vector3f> map = new HashMap<>();
//        Entry<GameObject, Vector3f> intersectionWithGameObject = getIntersectionWithGameObject(
//                Scene.getInstance().getGameObjectsOfType(Terrain.class, false).stream().findFirst().orElse(null));
//        if (intersectionWithGameObject != null)
//            map.put(intersectionWithGameObject.getKey(), intersectionWithGameObject.getValue());
//        calculateIntersectionPoint(map);
    }

    public void updateIntersectionOnClick() {
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
            this.intersectionPoint = gameObjectDoubleEntry.getValue().sub(new Vector3f(.5f, 0, .5f));
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
            position = position.add(offsetComponent.getOffset());

        BoundingBox boundingBox = boundingBoxComponent.getBoundingBox();
        Vector3f finalPosition = position;
        Vector3f intersectionPoint = boundingBox.getPlanes().stream()
                .map(plane3D -> {
                    plane3D = plane3D.add(finalPosition);
//                    return intersectionWithPlane(plane3D, false);
                    return new Vector3f();
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
        float mouseX = mousePos.x * DisplayManager.WIDTH / 2f + DisplayManager.WIDTH / 2f;
        float mouseY = mousePos.y * DisplayManager.HEIGHT / 2f + DisplayManager.HEIGHT / 2f;
        Vector2f normalizedCoords = getNormalisedDeviceCoordinates(mouseX, mouseY);
        Vector4f clipCoords = new Vector4f(normalizedCoords.x, normalizedCoords.y, -1.0f, 1.0f);
        Vector4f eyeCoords = toEyeCoords(clipCoords);

        return toWorldCoords(eyeCoords);
    }

    private Vector3f toWorldCoords(Vector4f eyeCoords) {
        Matrix4f invertedView = Matrix4f.invert(this.viewMatrix, null);
        Vector4f rayWorld = Matrix4f.transform(invertedView, eyeCoords, null);
        Vector3f mouseRay = new Vector3f(rayWorld.x, rayWorld.y, rayWorld.z);
        mouseRay.normalise();

        return mouseRay;
    }

    private Vector4f toEyeCoords(Vector4f clipCoords) {
        Matrix4f invertedProjection = Matrix4f.invert(MasterRenderer.getInstance().getProjectionMatrix(), null);
        //TODO: Attribute?
        Vector4f eyeCoords = Matrix4f.transform(invertedProjection, clipCoords, null);

        return new Vector4f(eyeCoords.x, eyeCoords.y, -1f, 0f);
    }

    private Vector2f getNormalisedDeviceCoordinates(float mouseX, float mouseY) {
        float x = (2.0f * mouseX) / DisplayManager.WIDTH - 1f;
        float y = (2.0f * mouseY) / DisplayManager.HEIGHT - 1f;

        return new Vector2f(x, y);
    }

    public GameObject getGameObject() {
        return this.gameObject;
    }

    public TerrainPosition getHoveredCell() {
        if (!MouseUtils.rightClickPressed)
            return this.intersectionPoint == null ? null : this.intersectionPoint.toTerrainPosition();
//        if (this.needRayUpdate)
//            update();

        this.intersectionPoint = null;
        float slopeSpeed = 1.0f;
        Vector3f currPos = new Vector3f(Camera.getInstance().getPosition());
        // First, check if mouseRay intersects with Terrain:
//        if (!terrainPlane.doesLineIntersect(this.currentRay))
//            return null;//TODO:Use full BB (minHeight to maxHeight Box
        while (!isPosOnTerrain(currPos)) {//Dangerous for now if not clicked on terrain@see TODO-Above
            // Camera not above the Terrain
            Vector3f tmp = new Vector3f(this.currentRay.x * slopeSpeed,
                    this.currentRay.y * slopeSpeed, this.currentRay.z * slopeSpeed);
            currPos = Vector3f.add(currPos, tmp, null);
        }
        Terrain terrain = Scene.getInstance().getTerrain();
        HeightMapComponent heightMapComponent = terrain.getComponent(HeightMapComponent.class);
        while (isPosOnTerrain(currPos)) {
            int minX = (int) Math.floor(currPos.x);
            int minZ = (int) Math.floor(currPos.z);
            int maxX = minX + 1;
            int maxZ = minZ + 1;

            Vector3f p1 = new Vector3f(minX, heightMapComponent.getHeight(minX, minZ), minZ);
            Vector3f p2 = new Vector3f(maxX, heightMapComponent.getHeight(maxX, minZ), minZ);
            Vector3f p3 = new Vector3f(minX, heightMapComponent.getHeight(minX, maxZ), maxZ);
            Vector3f p4 = new Vector3f(maxX, heightMapComponent.getHeight(maxX, maxZ), maxZ);

            float height = currPos.y;

            if (p1.y >= height || p2.y >= height || p3.y >= height || p4.y >= height) {
                TerrainPosition terrainPosition = new TerrainPosition((int) Math.floor(p1.x), p1.y,
                        (int) Math.floor(p1.z));
                this.intersectionPoint = terrainPosition.toVector3f();
                return terrainPosition;
            }

            Vector3f tmp = new Vector3f(this.currentRay.x * slopeSpeed,
                    this.currentRay.y * slopeSpeed, this.currentRay.z * slopeSpeed);
            currPos = Vector3f.add(currPos, tmp, null);
        }
        return null;
    }

    private boolean isPosOnTerrain(Vector3f pos) {
        return pos.x >= 0 && pos.z >= 0 && pos.x < Game.TERRAIN_WIDTH - 1
                && pos.z < Game.TERRAIN_DEPTH - 1;
    }

    float sign(Vector2f p1, Vector2f p2, Vector2f p3) {
        return (p1.x - p3.x) * (p2.y - p3.y) - (p2.x - p3.x) * (p1.y - p3.y);
    }

    boolean isPointInTriangle(Vector2f pt, Vector2f v1, Vector2f v2, Vector2f v3) {
        float d1, d2, d3;
        boolean has_neg, has_pos;

        d1 = sign(pt, v1, v2);
        d2 = sign(pt, v2, v3);
        d3 = sign(pt, v3, v1);

        has_neg = (d1 < 0) || (d2 < 0) || (d3 < 0);
        has_pos = (d1 > 0) || (d2 > 0) || (d3 > 0);

        return !(has_neg && has_pos);
    }
}
