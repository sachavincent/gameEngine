package util;


import entities.Camera;
import inputs.MouseUtils;
import renderEngine.DisplayManager;
import renderEngine.MasterRenderer;
import terrains.Terrain;
import util.math.Maths;
import util.math.Matrix4f;
import util.math.Vector2f;
import util.math.Vector3f;
import util.math.Vector4f;

public class MousePicker {

    private static final int   RECURSION_COUNT = 200;
    private static final float RAY_RANGE       = 600;

    private Vector3f currentRay = new Vector3f();

    private Matrix4f projectionMatrix;
    private Matrix4f viewMatrix;
    private Camera   camera;

    private Terrain  terrain;
    private Vector3f currentTerrainPoint;

    private static MousePicker instance;


    private MousePicker() {
        this.camera = Camera.getInstance();
        this.projectionMatrix = MasterRenderer.getInstance().getProjectionMatrix();
        this.viewMatrix = Maths.createViewMatrix(camera);
        this.terrain = Terrain.getInstance();
    }

    public static MousePicker getInstance() {
        return instance == null ? (instance = new MousePicker()) : instance;
    }

    public Vector3f getCurrentTerrainPoint() {
        return currentTerrainPoint;
    }

    public Vector3f getCurrentRay() {
        return currentRay;
    }

    public void update() {
        viewMatrix = Maths.createViewMatrix(camera);

        currentRay = calculateMouseRay();
        currentRay = (Vector3f) currentRay.normalise();


        currentTerrainPoint = intersectionWithPlane(new Vector3f(0, 0, 0), Terrain.SIZE, 0, Terrain.SIZE,
                new Vector3f(0, 1, 0), false);
        if (currentTerrainPoint != null)
            currentTerrainPoint.y = terrain.getHeightOfTerrain(currentTerrainPoint.x, currentTerrainPoint.z);
        //TODO: Temp parce que le terrain est plat

//        if (intersectionInRange(0, RAY_RANGE, currentRay)) {
//            currentTerrainPoint = binarySearch(0, 0, RAY_RANGE, currentRay);
//            currentTerrainPoint.y = terrain.getHeightOfTerrain(currentTerrainPoint.x, currentTerrainPoint.z);
//            System.out.println(currentTerrainPoint);
//        } else
//            currentTerrainPoint = null;
    }

    public Vector3f intersectionWithPlane(Vector3f pointPlane, float width, float height, float depth,
            Vector3f normalPlane, boolean print) {
        Vector3f origin = camera.getPosition();

        normalPlane = (Vector3f) normalPlane.normalise();
        float dot_dn = Vector3f.dot(currentRay, normalPlane);
        if (dot_dn == 0)
            return null;

        float t = -(Vector3f.dot(Vector3f.sub(origin, pointPlane, null), normalPlane)) / dot_dn;

        Vector3f P = Vector3f.add((Vector3f) currentRay.scale(t), origin, null);

        P.x = Maths.roundFloat(P.x, 3);
        P.y = Maths.roundFloat(P.y, 3);
        P.z = Maths.roundFloat(P.z, 3);
        pointPlane.x = Maths.roundFloat(pointPlane.x, 3);
        pointPlane.y = Maths.roundFloat(pointPlane.y, 3);
        pointPlane.z = Maths.roundFloat(pointPlane.z, 3);
        if (print) {
            System.out.println("t : " + t);
            System.out.println("distance : " + pointPlane.distance(new Vector3f(0, 0, 0)));
            System.out.println("x : " + pointPlane.x);
            System.out.println("y : " + pointPlane.y);
            System.out.println("z : " + pointPlane.z);
            System.out.println("width : " + width);
            System.out.println("height : " + height);
            System.out.println("depth : " + depth);
            System.out.println("P : " + P);
        }

        if (Maths.isPointIn3DBounds(P, pointPlane.x, pointPlane.y, pointPlane.z, width, height, depth))
            return P;
        else
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
        Matrix4f invertedProjection = Matrix4f.invert(projectionMatrix, null);
        Vector4f eyeCoords = Matrix4f.transform(invertedProjection, clipCoords, null);

        return new Vector4f(eyeCoords.x, eyeCoords.y, -1f, 0f);
    }

    private Vector2f getNormalisedDeviceCoordinates(float mouseX, float mouseY) {
        float x = (2.0f * mouseX) / DisplayManager.WIDTH - 1f;
        float y = (2.0f * mouseY) / DisplayManager.HEIGHT - 1f;

        return new Vector2f(x, y);
    }

//    private Vector3f getPointOnRay(Vector3f ray, float distance) {
//        Vector3f camPos = camera.getPosition();
//        Vector3f start = new Vector3f(camPos.x, camPos.y, camPos.z);
//        Vector3f scaledRay = new Vector3f(ray.x * distance, ray.y * distance, ray.z * distance);
//
//        return Vector3f.add(start, scaledRay, null);
//    }

//    private Vector3f binarySearch(int count, float start, float finish, Vector3f ray) {
//        float half = start + ((finish - start) / 2f);
//        if (count >= RECURSION_COUNT)
//            return getPointOnRay(ray, half);
//
//        if (intersectionInRange(start, half, ray)) {
//            return binarySearch(count + 1, start, half, ray);
//        } else {
//            return binarySearch(count + 1, half, finish, ray);
//        }
//    }

//    private boolean intersectionInRange(float start, float finish, Vector3f ray) {
//        Vector3f startPoint = getPointOnRay(ray, start);
//        Vector3f endPoint = getPointOnRay(ray, finish);
//
//        return !isUnderGround(startPoint) && isUnderGround(endPoint);
//    }

//    private boolean isUnderGround(Vector3f testPoint) {
//        float height = 0;
//        if (terrain != null)
//            height = terrain.getHeightOfTerrain(testPoint.getX(), testPoint.getZ());
//
//        return testPoint.y < height;
//    }

}