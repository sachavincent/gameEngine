package util;

import entities.Camera;
import inputs.MouseUtils;
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
import terrains.TerrainPosition;
import util.math.*;

import java.util.AbstractMap.SimpleEntry;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.IntStream;

public class MousePicker {

    private Vector3f currentRay = new Vector3f();

    private Matrix4f viewMatrix;

    private Vector3f intersectionPoint;

    private static MousePicker instance;

    private GameObject gameObject;

    private final List<Vector2f> TMMP = new ArrayList<>();

    private MousePicker() {
        this.viewMatrix = Camera.getInstance().getViewMatrix();

        IntStream.range(0, 50).forEach(xB -> {
            IntStream.range(0, 50).forEach(yB -> {
                TMMP.add(new Vector2f(xB, yB));
            });
        });
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
        this.gameObject = null;
        this.intersectionPoint = null;
//
//        Map<GameObject, Vector3f> map = new HashMap<>();
//        Entry<GameObject, Vector3f> intersectionWithGameObject = getIntersectionWithGameObject(
//                Scene.getInstance().getGameObjectsOfType(Terrain.class, false).stream().findFirst().orElse(null));
//        if (intersectionWithGameObject != null)
//            map.put(intersectionWithGameObject.getKey(), intersectionWithGameObject.getValue());
//        calculateIntersectionPoint(map);
//        Plane3D terrainPlane = new Plane3D(new Vector3f(0, 0, 0), new Vector3f(0, 0, Terrain.SIZE),
//                new Vector3f(Terrain.SIZE, 0, Terrain.SIZE), new Vector3f(Terrain.SIZE, 0, 0));
//        System.out.println("TerrainPoint: " + intersectionWithPlane(terrainPlane, false));
//        if (currentTerrainPoint != null)
//            currentTerrainPoint.y = terrain.getHeightOfTerrain(currentTerrainPoint.x, currentTerrainPoint.z);
////        TODO: Temp parce que le terrain est plat
//
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

    TerrainPosition tmp;

    public TerrainPosition getHoveredCell() {
        if (!MouseUtils.rightClickPressed)
            return tmp;
//        if (this.needRayUpdate)
//            update();

        tmp = null;
        float slopeSpeed = 1.0f;
        Vector3f currPos = new Vector3f(Camera.getInstance().getPosition());
        while (currPos.x >= 0 && currPos.z >= 0 &&
                currPos.x < Terrain.SIZE && currPos.z < Terrain.SIZE) {
            int minX = (int) Math.floor(currPos.x);
            int minZ = (int) Math.floor(currPos.z);
            int maxX = minX + 1;
            int maxZ = minZ + 1;

            Vector3f p1 = new Vector3f(minX, Terrain.heights[minX][minZ], minZ);
            Vector3f p2 = new Vector3f(maxX, Terrain.heights[maxX][minZ], minZ);
            Vector3f p3 = new Vector3f(minX, Terrain.heights[minX][maxZ], maxZ);
            Vector3f p4 = new Vector3f(maxX, Terrain.heights[maxX][maxZ], maxZ);

            float height = currPos.y;

            if (p1.y >= height || p2.y >= height || p3.y >= height || p4.y >= height)
                return tmp = p1.toTerrainPosition();

            Vector3f tmp = new Vector3f(this.currentRay.x * slopeSpeed,
                    this.currentRay.y * slopeSpeed, this.currentRay.z * slopeSpeed);
            currPos = Vector3f.add(currPos, tmp, null);
        }
        return null;
    }

    public void TBD() {
        List<Vector3f> crossedLines = new ArrayList<>();
        Vector3f cameraPosition = new Vector3f(Camera.getInstance().getPosition());

        while (cameraPosition.x < 0 || cameraPosition.z < 0 ||
                cameraPosition.x >= (Terrain.SIZE - 1) || cameraPosition.z >= (Terrain.SIZE - 1)) {
            // Camera is out of the terrain
            cameraPosition.translate(this.currentRay.x, this.currentRay.y, this.currentRay.z);
        }
        while (!(cameraPosition.x < 0 || cameraPosition.z < 0 ||
                cameraPosition.x >= (Terrain.SIZE - 1) || cameraPosition.z >= (Terrain.SIZE - 1))) {

            int minX = (int) Math.floor(cameraPosition.x);
            int minZ = (int) Math.floor(cameraPosition.z);
//            int maxX = (int) Math.ceil(cameraPosition.x);
//            int maxZ = (int) Math.ceil(cameraPosition.z);
            int maxX = minX + 1;
            int maxZ = minZ + 1;
            double topLeftHeight = Terrain.heights[maxX][minZ];
            double topRightHeight = Terrain.heights[maxX][maxZ];
            double bottomLeftHeight = Terrain.heights[minX][minZ];
            double bottomRightHeight = Terrain.heights[minX][maxZ];

            float height = cameraPosition.y;
            double sign1 = Math.signum(topLeftHeight - height);
            double sign2 = Math.signum(topRightHeight - height);
            double sign3 = Math.signum(bottomLeftHeight - height);
            double sign4 = Math.signum(bottomRightHeight - height);
            Vector2f cam = new Vector2f(cameraPosition.x, cameraPosition.z);
            Vector3f tmp = new Vector3f(this.currentRay.x, this.currentRay.y, this.currentRay.z);
            cameraPosition = Vector3f.add(cameraPosition, tmp, null);
//            if (sign1 != sign2 || sign3 != sign4 || sign1 != sign3
//                    || sign2 != sign4 || sign1 != sign4 || sign2 != sign3)
//                continue;
//            if (sign1 == sign2 && sign1 == sign3 && sign1 == sign4)
//                continue;
            if (topLeftHeight < height && bottomLeftHeight < height &&
                    topRightHeight < height && bottomRightHeight < height)
                continue;
            Vector3f p1 = new Vector3f(minX, bottomLeftHeight, minZ);
            Vector3f p2 = new Vector3f(maxX, topLeftHeight, minZ);
            Vector3f p3 = new Vector3f(minX, bottomRightHeight, maxZ);
            Vector3f p4 = new Vector3f(maxX, topRightHeight, maxZ);
//            float height1 = Maths.barryCentric(p1, p2, p3, cam);
//            float height2 = Maths.barryCentric(p2, p3, p4, cam);
            float eps = 0.01f;
//            if (Math.abs(height - height1) < eps) {
            if (isPointInTriangle(cam, new Vector2f(p1.x, p1.z), new Vector2f(p2.x, p2.z), new Vector2f(p3.x, p3.z))) {
//                crossedLines.add(p1);
//                crossedLines.add(p2);
//                crossedLines.add(p3);
//                break;
//            } else if (Math.abs(height - height2) < eps) {
            } else if (isPointInTriangle(cam, new Vector2f(p2.x, p2.z), new Vector2f(p3.x, p3.z), new Vector2f(p4.x, p4.z))) {
//                crossedLines.add(p2);
//                crossedLines.add(p3);
//                crossedLines.add(p4);
//                break;
            }
            crossedLines.add(p1);
            crossedLines.add(p2);
            crossedLines.add(p3);
            crossedLines.add(p4);
            System.out.println(this.currentRay + "=>" + new Vector3f(cam.x, height, cam.y));
            System.out.println("\t" + p1 + " vs " + Terrain.heights[(int) p1.x][(int) p1.z]);
            System.out.println("\t" + p2 + " vs " + Terrain.heights[(int) p2.x][(int) p2.z]);
            System.out.println("\t" + p3 + " vs " + Terrain.heights[(int) p3.x][(int) p3.z]);
            System.out.println("\t" + p4 + " vs " + Terrain.heights[(int) p4.x][(int) p4.z]);

            this.intersectionPoint = p1;
            break;
        }
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
