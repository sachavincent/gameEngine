package util;

import entities.Camera;
import inputs.MouseUtils;
import java.util.AbstractMap.SimpleEntry;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import models.BoundingBox;
import renderEngine.DisplayManager;
import renderEngine.MasterRenderer;
import scene.Scene;
import scene.components.BoundingBoxComponent;
import scene.components.OffsetComponent;
import scene.components.PositionComponent;
import scene.components.TerrainComponent;
import scene.gameObjects.GameObject;
import scene.gameObjects.Terrain;
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
        return intersectionPoint != null && gameObject.getComponent(TerrainComponent.class) != null;
    }

    public Vector3f getCurrentRay() {
        return currentRay;
    }

    public void update() {
        this.viewMatrix = Camera.getInstance().getViewMatrix();

        this.currentRay = calculateMouseRay();
        this.currentRay = (Vector3f) this.currentRay.normalise();

        this.gameObject = null;
        this.intersectionPoint = null;

        Map<GameObject, Vector3f> map = new HashMap<>();
        Entry<GameObject, Vector3f> intersectionWithGameObject = getIntersectionWithGameObject(
                Scene.getInstance().getGameObjectsOfType(Terrain.class, false).stream().findFirst().orElse(null));
        if (intersectionWithGameObject != null)
            map.put(intersectionWithGameObject.getKey(), intersectionWithGameObject.getValue());
        calculateIntersectionPoint(map);
//        Plane3D terrainPlane = new Plane3D(new Vector3f(0, 0, 0), new Vector3f(0, 0, Terrain.SIZE),
//                new Vector3f(Terrain.SIZE, 0, Terrain.SIZE), new Vector3f(Terrain.SIZE, 0, 0));
//        System.out.println("TerrainPoint: " + intersectionWithPlane(terrainPlane, false));
//        if (currentTerrainPoint != null)
//            currentTerrainPoint.y = terrain.getHeightOfTerrain(currentTerrainPoint.x, currentTerrainPoint.z);
        //TODO: Temp parce que le terrain est plat

//        if (intersectionInRange(0, RAY_RANGE, currentRay)) {
//            currentTerrainPoint = binarySearch(0, 0, RAY_RANGE, currentRay);
//            currentTerrainPoint.y = terrain.getHeightOfTerrain(currentTerrainPoint.x, currentTerrainPoint.z);
//            System.out.println(currentTerrainPoint);
//        } else
//            currentTerrainPoint = null;
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
}