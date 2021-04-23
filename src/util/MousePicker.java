package util;


import entities.Camera;
import inputs.MouseUtils;
import java.util.Objects;
import models.BoundingBox;
import renderEngine.DisplayManager;
import renderEngine.MasterRenderer;
import scene.*;
import scene.components.BoundingBoxComponent;
import scene.components.PositionComponent;
import scene.components.TerrainComponent;
import scene.gameObjects.GameObject;
import util.math.Maths;
import util.math.Matrix4f;
import util.math.Plane3D;
import util.math.Vector2f;
import util.math.Vector3f;
import util.math.Vector4f;

public class MousePicker {

    private Vector3f currentRay = new Vector3f();

    private       Matrix4f viewMatrix;
    private final Camera   camera;

    //    private final Terrain  terrain;
    private Vector3f intersectionPoint;

    private static MousePicker instance;

    private GameObject gameObject;

    private MousePicker() {
        this.camera = Camera.getInstance();
        this.viewMatrix = Maths.createViewMatrix();
//        this.terrain = Terrain.getInstance();
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

    public Vector3f update() {
        viewMatrix = Maths.createViewMatrix();

        currentRay = calculateMouseRay();
        currentRay = (Vector3f) currentRay.normalise();

        this.gameObject = Scene.getInstance().getGameObjects().stream().filter(gameObject -> {
            BoundingBoxComponent boundingBoxComponent = gameObject.getComponent(BoundingBoxComponent.class);
            if (boundingBoxComponent == null)
                return false;
            PositionComponent positionComponent = gameObject.getComponent(PositionComponent.class);
            if (positionComponent == null)
                return false;
            BoundingBox boundingBox = boundingBoxComponent.getBoundingBox();
            this.intersectionPoint = boundingBox.getPlanes().stream()
                    .map(plane3D -> {
                        Vector3f position = positionComponent.getPosition();
                        plane3D = plane3D.add(position);
                        return intersectionWithPlane(plane3D, false);
                    }).filter(Objects::nonNull).findFirst().orElse(null);

            return this.intersectionPoint != null;
        }).findFirst().orElse(null);

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

        return this.intersectionPoint;
    }

    public Vector3f intersectionWithPlane(Plane3D plane, boolean print) {
        Vector3f origin = camera.getPosition();

        Vector3f normalPlane = plane.getNormal();
        float dot_dn = Vector3f.dot(currentRay, normalPlane);
        if (dot_dn == 0)
            return null;

        Vector3f pointPlane = plane.getPointA();
        float t = -(Vector3f.dot(Vector3f.sub(origin, pointPlane, null), normalPlane)) / dot_dn;

        Vector3f P = Vector3f.add((Vector3f) currentRay.scale(t), origin, null);
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
        Matrix4f invertedView = Matrix4f.invert(viewMatrix, null);
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