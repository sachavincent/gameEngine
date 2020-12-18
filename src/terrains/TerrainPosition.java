package terrains;

import java.util.Objects;

public class TerrainPosition implements Comparable<TerrainPosition> {

    private int x;
    //    private final int y;
    private int z;

    public TerrainPosition(TerrainPosition terrainPosition) {
        this(terrainPosition.x, terrainPosition.z);
    }

    public TerrainPosition(int x,/* int y, */int z) {
        this.x = x;
//        this.y = y;
        this.z = z;
    }

    public int getX() {
        return this.x;
    }

//    public int getY() {
//        return this.y;
//    }

    public int getZ() {
        return this.z;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setZ(int z) {
        this.z = z;
    }

    public TerrainPosition add(TerrainPosition terrainPosition) {
        return new TerrainPosition(this.x + terrainPosition.x, this.z + terrainPosition.z);
    }

    public TerrainPosition sub(TerrainPosition terrainPosition) {
        return new TerrainPosition(this.x - terrainPosition.x, this.z - terrainPosition.z);
    }

    public static TerrainPosition[] toVectorArray(int[] roadPositions) {
        if (roadPositions.length % 2 != 0)
            return null;

        TerrainPosition[] roads = new TerrainPosition[roadPositions.length / 2];
        for (int i = 0; i < roads.length; i++) {
            roads[i] = new TerrainPosition(roadPositions[i * 2], roadPositions[i * 2 + 1]);
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
        return x == that.x && z == that.z;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, z);
    }

    @Override
    public int compareTo(TerrainPosition o) {
        int i = Integer.compare(x, o.x);
        if (i != 0)
            return i;

        return Integer.compare(z, o.z);
    }
}
