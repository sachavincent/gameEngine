package scene.components;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;
import terrains.TerrainPosition;

public class OffsetsComponent implements Component {

    // WEST NORTH EAST SOUTH
    protected int[] offsets = new int[4];

    public OffsetsComponent(int zPositiveOffset, int xPositiveOffset, int zNegativeOffset, int xNegativeOffset) {
        this.offsets[0] = zPositiveOffset;
        this.offsets[1] = xPositiveOffset;
        this.offsets[2] = zNegativeOffset;
        this.offsets[3] = xNegativeOffset;
    }

    public OffsetsComponent(int[] offsets) {
        this.offsets = offsets;
    }

    public OffsetsComponent() {
        this.offsets = new int[]{0, 0, 0, 0};
    }

    public int[] getOffsets() {
        return this.offsets;
    }

    public int getzPositiveOffset() {
        return this.offsets[0];
    }

    public int getxPositiveOffset() {
        return this.offsets[1];
    }

    public int getzNegativeOffset() {
        return this.offsets[2];
    }

    public int getxNegativeOffset() {
        return this.offsets[3];
    }

    public TerrainPosition[] getOffsetPositions() {
        int size = (getxNegativeOffset() + getzPositiveOffset() + 1)
                * (getxNegativeOffset() + getxPositiveOffset() + 1);
        TerrainPosition[] positions = new TerrainPosition[size];

        AtomicInteger i = new AtomicInteger();
        IntStream.range(-getxNegativeOffset(), getxPositiveOffset() + 1).forEach(x -> {
            IntStream.range(-getzNegativeOffset(), getzPositiveOffset() + 1).forEach(z -> {
                positions[i.getAndIncrement()] = new TerrainPosition(x, z);
            });
        });

        return positions;
    }
}