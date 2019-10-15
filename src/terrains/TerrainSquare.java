package terrains;

public class TerrainSquare {

    private final static int WIDTH = 1;

    private float x;
    private float z;

    public TerrainSquare(float x, float z) {
        this.x = x;
        this.z = z;
    }

    @Override
    public String toString() {
        return "TerrainSquare{" +
                "x=" + x +
                ", z=" + z +
                '}';
    }
}
