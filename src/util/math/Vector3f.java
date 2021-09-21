package util.math;

import engineTester.Rome;
import java.io.Serializable;
import java.nio.FloatBuffer;
import java.util.Locale;
import java.util.Objects;
import scene.components.HeightMapComponent;
import scene.gameObjects.Terrain;
import terrain.TerrainPosition;

public class Vector3f extends Vector implements Serializable, ReadableVector3f, WritableVector3f {

    private static final long serialVersionUID = 1L;

    private float x;
    private float y;
    private float z;

    public Vector3f() {
    }

    public Vector3f(ReadableVector3f src) {
        this.set(src);
    }

    public Vector3f(Float x, Float y, Float z) {
        this.set(x, y, z);
    }

    public Vector3f(double x, double y, double z) {
        this.set((float) x, (float) y, (float) z);
    }

    public void set(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void set(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vector3f set(ReadableVector3f src) {
        this.x = src.getX();
        this.y = src.getY();
        this.z = src.getZ();
        return this;
    }

    public float lengthSquared() {
        return this.x * this.x + this.y * this.y + this.z * this.z;
    }

    public Vector3f translate(float x, float y, float z) {
        this.x += x;
        this.y += y;
        this.z += z;
        return this;
    }

    public static Vector3f translate(float x, float y, float z, Vector3f dest) {
        dest.x += x;
        dest.y += y;
        dest.z += z;
        return dest;
    }

    public static Vector3f add(Vector3f left, Vector3f right, Vector3f dest) {
        if (dest == null) {
            return new Vector3f(left.x + right.x, left.y + right.y, left.z + right.z);
        } else {
            dest.set(left.x + right.x, left.y + right.y, left.z + right.z);
            return dest;
        }
    }

    public static Vector3f sub(Vector3f left, Vector3f right, Vector3f dest) {
        if (dest == null) {
            return new Vector3f(left.x - right.x, left.y - right.y, left.z - right.z);
        } else {
            dest.set(left.x - right.x, left.y - right.y, left.z - right.z);
            return dest;
        }
    }

    public static Vector3f cross(Vector3f left, Vector3f right, Vector3f dest) {
        if (dest == null) {
            dest = new Vector3f();
        }

        dest.set(left.y * right.z - left.z * right.y, right.x * left.z - right.z * left.x,
                left.x * right.y - left.y * right.x);
        return dest;
    }

    public static Vector3f cross(Vector3f left, Vector3f right) {
        return cross(left, right, null);
    }

    public Vector negate() {
        this.x = -this.x;
        this.y = -this.y;
        this.z = -this.z;

        return this;
    }


    public Vector3f normalize() {
        float l = this.length();
        set(this.x / l, this.y / l, this.z / l);

        return this;
    }

    public static float dot(Vector3f left, Vector3f right) {
        return left.x * right.x + left.y * right.y + left.z * right.z;
    }

    public static float angle(Vector3f a, Vector3f b) {
        float dls = dot(a, b) / (a.length() * b.length());
        if (dls < -1.0F) {
            dls = -1.0F;
        } else if (dls > 1.0F) {
            dls = 1.0F;
        }

        return (float) Math.acos(dls);
    }

    public Vector load(FloatBuffer buf) {
        this.x = buf.get();
        this.y = buf.get();
        this.z = buf.get();
        return this;
    }

    public Vector scale(float scale) {
        this.x *= scale;
        this.y *= scale;
        this.z *= scale;
        return this;
    }
    
    public Vector3f add(Vector3f vec) {
        this.x += vec.x;
        this.y += vec.y;
        this.z += vec.z;

        return this;
    }

    public Vector3f sub(Vector3f vec) {
        this.x -= vec.x;
        this.y -= vec.y;
        this.z -= vec.z;

        return this;
    }

    public Vector store(FloatBuffer buf) {
        buf.put(this.x);
        buf.put(this.y);
        buf.put(this.z);
        return this;
    }

    public String toString() {
        return "Vector3f[" + this.x + ", " + this.y + ", " + this.z + ']';
    }

    public final float getX() {
        return this.x;
    }

    public final float getY() {
        return this.y;
    }

    public final void setX(float x) {
        this.x = x;
    }

    public final void setY(float y) {
        this.y = y;
    }

    public void setZ(float z) {
        this.z = z;
    }

    public float getZ() {
        return this.z;
    }

    public double distanceSquared(Vector3f v) {
        double dx = x - v.x;
        double dy = y - v.y;
        double dz = z - v.z;
        return dx * dx + dy * dy + dz * dz;
    }
    public double distance(Vector3f v) {
        return Math.sqrt(distanceSquared(v));
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (obj == null) {
            return false;
        } else if (this.getClass() != obj.getClass()) {
            return false;
        } else {
            Vector3f other = (Vector3f) obj;
            return this.x == other.x && this.y == other.y && this.z == other.z;
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.x, this.y, this.z);
    }

    public void format() {
        this.x = Float.parseFloat(String.format(Locale.ROOT, "%.4f", this.x));
        this.y = Float.parseFloat(String.format(Locale.ROOT, "%.4f", this.y));
        this.z = Float.parseFloat(String.format(Locale.ROOT, "%.4f", this.z));
    }

    public boolean isTerrainPosition() {
        Terrain terrain = Rome.getGame().getScene().getTerrain();
        HeightMapComponent heightMapComponent = terrain.getComponent(HeightMapComponent.class);
        if (!Rome.getGame().getScene().isPositionOnTerrain(this.x, this.z))
            return false;

        TerrainPosition pos = toTerrainPosition();
        return this.y == heightMapComponent.getHeight(pos.getX(), pos.getZ());
    }

    public TerrainPosition toTerrainPosition() {
        return new TerrainPosition((int) Math.rint(this.x), this.y, (int) Math.rint(this.z));
    }
}