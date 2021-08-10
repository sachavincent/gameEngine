package scene.components;

import entities.Camera.Direction;
import terrain.TerrainPosition;
import util.math.Maths;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

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

    public int[] getOffsets(Direction direction) {
        if (direction == null)
            return this.offsets;

        Integer[] offsets = IntStream.of(this.offsets).boxed().toArray(Integer[]::new);
        for (int i = 0; i < direction.ordinal(); i++)
            offsets = Maths.shiftArrayLeft(offsets);

        return Arrays.stream(offsets).mapToInt(value -> value).toArray();
    }

//    public int getzPositiveOffset() {
//        return this.offsets[2];
//    }
//
//    public int getxPositiveOffset() {
//        return this.offsets[1];
//    }
//
//    public int getzNegativeOffset() {
//        return this.offsets[0];
//    }
//
//    public int getxNegativeOffset() {
//        return this.offsets[3];
//    }

    public TerrainPosition[] getOffsetPositions(Direction direction) {
        int size = (this.offsets[0] + this.offsets[2])
                * (this.offsets[1] + this.offsets[3]);
        TerrainPosition[] positions = new TerrainPosition[size];
        int[] offsets = getOffsets(direction);
        int offset0 = offsets[0];
        int offset1 = offsets[1];
        int offset2 = offsets[2];
        int offset3 = offsets[3];
        AtomicInteger i = new AtomicInteger();
        IntStream.range(-offset3, offset1).forEach(x -> {
            IntStream.range(-offset0, offset2).forEach(z -> {
                positions[i.getAndIncrement()] = new TerrainPosition(x, 0, z);
            });
        });

        return positions;
    }
}
