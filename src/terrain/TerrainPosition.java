package terrain;

import java.util.Objects;
import scene.Scene;
import scene.components.HeightMapComponent;
import util.math.Vector3f;

public class TerrainPosition implements Comparable<TerrainPosition> {

    private int   x;
    private float y;
    private int   z;

    public TerrainPosition(TerrainPosition terrainPosition) {
        this(terrainPosition.x, terrainPosition.y, terrainPosition.z);
    }

    public TerrainPosition(int x, float y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;

//        if (!Scene.getInstance().isPositionOnTerrain(x, z))
//            throw new IllegalArgumentException("TerrainPosition " + this + " does not fit on Terrain");
    }

    public TerrainPosition(int x, int z) {
        this(x, Scene.getInstance().getTerrain().getComponent(HeightMapComponent.class).getHeight(x, z), z);
    }

    public TerrainPosition(Vector3f vector) {
        this((int) vector.getX(), vector.getY(), (int) vector.getZ());
    }

    public int getX() {
        return this.x;
    }

    public float getY() {
        return this.y;
    }

    public int getZ() {
        return this.z;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(float y) {
        this.y = y;
    }

    public void setZ(int z) {
        this.z = z;
    }

    public TerrainPosition add(TerrainPosition terrainPosition) {
        return new TerrainPosition(this.x + terrainPosition.x, this.y + terrainPosition.y, this.z + terrainPosition.z);
    }

    public TerrainPosition add(int x, int z) {
        return new TerrainPosition(this.x + x, this.y, this.z + z);
    }

    public TerrainPosition sub(TerrainPosition terrainPosition) {
        return new TerrainPosition(this.x - terrainPosition.x, this.y - terrainPosition.y, this.z - terrainPosition.z);
    }

    public TerrainPosition sub(int x, int z) {
        return new TerrainPosition(this.x - x, this.y, this.z - z);
    }

    public static TerrainPosition[] toPositionArray(int[] roadPositions) {
        if (roadPositions.length % 2 != 0)
            return null;

        TerrainPosition[] roads = new TerrainPosition[roadPositions.length / 2];
        for (int i = 0; i < roads.length; i++) {
            int x = roadPositions[i * 2];
            int z = roadPositions[i * 2 + 1];
            roads[i] = new TerrainPosition(x, 0, z);
        }

        return roads;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        TerrainPosition that = (TerrainPosition) o;
        return this.x == that.x && this.z == that.z;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.x, this.y, this.z);
    }

    @Override
    public int compareTo(TerrainPosition o) {
        int i = Integer.compare(this.x, o.x);
        if (i != 0)
            return i;
        i = Float.compare(this.y, o.y);
        if (i != 0)
            return i;

        return Integer.compare(this.z, o.z);
    }

    @Override
    public String toString() {
        return "TerrainPosition{" + "x=" + x + ", y=" + y + ", z=" + z + '}';
    }

    public Vector3f toVector3f() {
        return new Vector3f(this.x, this.y, this.z);
    }
}