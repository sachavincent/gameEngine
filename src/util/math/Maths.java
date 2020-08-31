package util.math;

import entities.Camera;
import java.text.DecimalFormat;
import util.MousePicker;

public class Maths {

    public static float clamp(float value, float min, float max) {
        return Math.min(max, Math.min(max, value));
    }

    public static float barryCentric(Vector3f p1, Vector3f p2, Vector3f p3, Vector2f pos) {
        float det = (p2.z - p3.z) * (p1.x - p3.x) + (p3.x - p2.x) * (p1.z - p3.z);
        float l1 = ((p2.z - p3.z) * (pos.x - p3.x) + (p3.x - p2.x) * (pos.y - p3.z)) / det;
        float l2 = ((p3.z - p1.z) * (pos.x - p3.x) + (p1.x - p3.x) * (pos.y - p3.z)) / det;
        float l3 = 1.0f - l1 - l2;

        return l1 * p1.y + l2 * p2.y + l3 * p3.y;
    }

    public static Matrix4f createTransformationMatrix(Vector3f translation, float rx, float ry, float rz, float scale) {
        if (translation == null)
            return null;

        Matrix4f matrix = new Matrix4f();
        matrix.setIdentity();

        Matrix4f.translate(translation, matrix, matrix);

        Matrix4f.rotate((float) Math.toRadians(rx), new Vector3f(1, 0, 0), matrix, matrix);
        Matrix4f.rotate((float) Math.toRadians(ry), new Vector3f(0, 1, 0), matrix, matrix);
        Matrix4f.rotate((float) Math.toRadians(rz), new Vector3f(0, 0, 1), matrix, matrix);

        Matrix4f.scale(new Vector3f(scale, scale, scale), matrix, matrix);

        return matrix;
    }

    public static Matrix4f createTransformationMatrix(Vector2f translation, Vector2f scale) {
        Matrix4f matrix = new Matrix4f();
        matrix.setIdentity();
        Matrix4f.translate(translation, matrix, matrix);
        Matrix4f.scale(new Vector3f(scale.x, scale.y, 1f), matrix, matrix);
        return matrix;
    }

    public static Matrix4f createViewMatrix(Camera camera) {
        Matrix4f matrix = new Matrix4f();
        matrix.setIdentity();

        Matrix4f.rotate((float) Math.toRadians(camera.getPitch()), new Vector3f(1, 0, 0), matrix, matrix);
        Matrix4f.rotate((float) Math.toRadians(camera.getYaw()), new Vector3f(0, 1, 0), matrix, matrix);
        Matrix4f.rotate((float) Math.toRadians(camera.getRoll()), new Vector3f(0, 0, 1), matrix, matrix);

        Vector3f cameraPos = camera.getPosition();
        Vector3f negativeCameraPos = new Vector3f(-cameraPos.x, -cameraPos.y, -cameraPos.z);

        Matrix4f.translate(negativeCameraPos, matrix, matrix);

        return matrix;
    }

    public static boolean isPointIn2DBounds(Vector2f point, float x, float y, float width, float height) {
        return (x - width) <= point.x && point.x <= (x + width) &&
                (y - height) <= point.y && point.y <= (y + height);
    }

    public static boolean isPointIn3DBounds(Vector3f point, float x, float y, float z, float width, float height,
            float depth) { // x y z = left corner !!important
        return (point.x >= x && point.x <= (x + width)) &&
                (point.y >= y && point.y <= (y + height)) &&
                (point.z >= z && point.z <= (z + depth));
    }

    public static float roundFloat(float value, int nbDecimals) {
        if (Float.isNaN(value))
            return -1;

        if (nbDecimals <= 0)
            return value;

        StringBuilder pattern = new StringBuilder("###.");
        for (int i = 0; i < nbDecimals; i++)
            pattern.append("#");

        try {
            DecimalFormat df = new DecimalFormat(pattern.toString());

            return Float.parseFloat(df.format(value).replace(",", "."));
        } catch (NumberFormatException | NullPointerException e) {
            e.printStackTrace();
        }

        return value;
    }

    public static boolean closestRectangle(Vector3f[] cornersRec1, Vector3f[] cornersRec2, Vector3f point) {
        float closestDistanceRec1 = Float.MAX_VALUE;

        for (Vector3f corner : cornersRec1)
            closestDistanceRec1 = Float.min((float) point.distance(corner), closestDistanceRec1);

        float closestDistanceRec2 = Float.MAX_VALUE;

        for (Vector3f corner : cornersRec2)
            closestDistanceRec2 = Float.min((float) point.distance(corner), closestDistanceRec2);


        return closestDistanceRec1 < closestDistanceRec2;
    }

    public static Vector3f temp(Vector3f[] rec1, Vector3f[] rec2, Vector3f point, MousePicker picker) {
        boolean closestRectangle = closestRectangle(rec1, rec2, point);

        float x, y, z, height, depth;
        Vector3f ll, ul, lr;

        if (closestRectangle) { // rec1 closest
            ll = rec1[0];
            ul = rec1[1];
            lr = rec1[2];
        } else { //rec2 closest
            ll = rec2[0];
            ul = rec2[1];
            lr = rec2[2];
        }

        x = ll.x;
        y = ll.y;
        z = ll.z;
        height = ul.y - ll.y;
        depth = lr.z != ll.z ? lr.z - ll.z : lr.x - ll.x;

        Vector3f normalPlane = new Vector3f(ll.x == lr.x ? 1 : 0, ll.y == ul.y ? 1 : 0, ll.z == lr.z ? 1 : 0);
        System.out.println(normalPlane);
        System.out.println(ll.x == lr.x ? 0 : depth);
        System.out.println(ll.y == ul.y ? 0 : height);
        System.out.println(ll.z == lr.z ? 0 : depth);
        return picker.intersectionWithPlane(
                new Vector3f(x, y, z), ll.x == lr.x ? 0 : depth, ll.y == ul.y ? 0 : height, ll.z == lr.z ? 0 : depth,
                normalPlane, false);
    }

    public static boolean[] shiftArray(boolean[] array) {
        if (array.length == 0)
            return array;

        boolean lastValue = array[array.length - 1];
        boolean[] resArray = new boolean[array.length];
        System.arraycopy(array, 0, resArray, 1, array.length - 1);
        resArray[0] = lastValue;

        return resArray;
    }
}
