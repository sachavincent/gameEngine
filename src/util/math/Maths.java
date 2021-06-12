package util.math;

import entities.Camera;
import java.text.DecimalFormat;
import terrains.TerrainPosition;

public class Maths {

    public static float clamp(float value, float min, float max) {
        return Math.max(min, Math.min(max, value));
    }
    public static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }

    public static double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }

    /**
     * https://stackoverflow.com/questions/5294955/how-to-scale-down-a-range-of-numbers-with-a-known-min-and-max-value
     */
    public static double scale(final double valueIn, final double baseMin, final double baseMax, final double limitMin,
            final double limitMax) {
        return ((limitMax - limitMin) * (valueIn - baseMin) / (baseMax - baseMin)) + limitMin;
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

    public static Matrix4f createViewMatrix() {
        Matrix4f matrix = new Matrix4f();
        matrix.setIdentity();

        Camera camera = Camera.getInstance();
        Matrix4f.rotate((float) Math.toRadians(camera.getPitch()), new Vector3f(1, 0, 0), matrix, matrix);
        Matrix4f.rotate((float) Math.toRadians(camera.getYaw()), new Vector3f(0, 1, 0), matrix, matrix);
//        Matrix4f.rotate((float) Math.toRadians(camera.getRoll()), new Vector3f(0, 0, 1), matrix, matrix);

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

    public static boolean closestRectangle(
            Vector3f[] cornersRec1, Vector3f[] cornersRec2, Vector3f point) {
        float closestDistanceRec1 = Float.MAX_VALUE;

        for (Vector3f corner : cornersRec1)
            closestDistanceRec1 = Float.min((float) point.distance(corner), closestDistanceRec1);

        float closestDistanceRec2 = Float.MAX_VALUE;

        for (Vector3f corner : cornersRec2)
            closestDistanceRec2 = Float.min((float) point.distance(corner), closestDistanceRec2);


        return closestDistanceRec1 < closestDistanceRec2;
    }

    public static <T> T[] shiftArray(T[] array) {
        if (array.length == 0)
            return array;

        T lastValue = array[array.length - 1];
        T[] resArray = array.clone();
        System.arraycopy(array, 0, resArray, 1, array.length - 1);
        resArray[0] = lastValue;

        return resArray;
    }

    public static int manhattanDistance(TerrainPosition startPos, TerrainPosition endPos) {
        if (startPos == null || endPos == null)
            return Integer.MAX_VALUE;

        return Math.abs(endPos.getX() - startPos.getX()) + Math.abs(endPos.getZ() - startPos.getZ());
    }

    public static float fma(float a, float b, float c) {
        return a * b + c;
    }

    public static double fma(double a, double b, double c) {
        return a * b + c;
    }

    public static float invsqrt(float r) {
        return 1.0f / (float) Math.sqrt(r);
    }

    public static double invsqrt(double r) {
        return 1.0 / Math.sqrt(r);
    }
}
