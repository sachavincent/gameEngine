package scene.components;

import entities.Camera.Direction;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;
import terrain.TerrainPosition;
import util.math.Maths;

public class OffsetsComponent extends Component {

    // EAST NORTH WEST SOUTH
    protected int[] offsets;

    public OffsetsComponent(int zNegativeOffset, int xPositiveOffset, int zPositiveOffset, int xNegativeOffset) {
        this(new int[]{zNegativeOffset, xPositiveOffset, zPositiveOffset, xNegativeOffset});
    }

    public OffsetsComponent(int[] offsets) {
        this.offsets = offsets;
    }

    public OffsetsComponent() {
        this(new int[]{0, 0, 0, 0});
    }

    public final int getzPositiveOffset() {
        return this.offsets[2];
    }

    public final int getxPositiveOffset() {
        return this.offsets[1];
    }

    public final int getzNegativeOffset() {
        return this.offsets[0];
    }

    public final int getxNegativeOffset() {
        return this.offsets[3];
    }

    /**
     * Returns the localized offset positions in given Direction
     *
     * @param direction direction in which the GameObject is rotated
     * @return localized offset positions
     */
    public TerrainPosition[] getLocalOffsetPositions(Direction direction) {
        int size = (this.offsets[0] + this.offsets[2])
                * (this.offsets[1] + this.offsets[3]);
        TerrainPosition[] positions = new TerrainPosition[size];
        int[] offsets = getLocalOffsets(direction);
        int offset0 = offsets[0];
        int offset1 = offsets[1];
        int offset2 = offsets[2];
        int offset3 = offsets[3];
        AtomicInteger i = new AtomicInteger();
        IntStream.range(-offset3, offset1).forEach(x -> {
            IntStream.range(-offset0, offset2).forEach(z -> {
                positions[i.getAndIncrement()] = new TerrainPosition(x, z);
            });
        });

        return positions;
    }

    /**
     * Returns the localized offset positions applied to the GameObject position
     *
     * @param direction direction in which the GameObject is rotated
     * @return offset positions
     */
    public TerrainPosition[] getOffsetPositions(Direction direction, int x, int z) {
        TerrainPosition[] offsetPositions = getLocalOffsetPositions(direction);
        for (int i = 0; i < offsetPositions.length; i++) {
            TerrainPosition offsetPosition = offsetPositions[i];
            offsetPositions[i] = new TerrainPosition(offsetPosition.getX() + x, offsetPosition.getZ() + z);
        }

        return offsetPositions;
    }


    /**
     * Returns the localized offset values in given Direction
     *
     * @param direction direction in which the GameObject is rotated
     * @return localized offset valus
     */
    public int[] getLocalOffsets(Direction direction) {
        if (direction == null)
            return this.offsets;

        Integer[] offsets = IntStream.of(this.offsets).boxed().toArray(Integer[]::new);
        for (int i = 0; i < direction.ordinal(); i++)
            offsets = Maths.shiftArrayLeft(offsets);

        return Arrays.stream(offsets).mapToInt(value -> value).toArray();
    }
}