package renderEngine.structures;

import java.util.Collection;

public class IndicesAttribute extends AttributeData<Integer> {

    public IndicesAttribute(Integer[] data) {
        super(-1, 1, data, DataType.INT);
    }

    public IndicesAttribute(Collection<Integer> data) {
        super(-1, 1, data, DataType.INT);
    }

    @Override
    public <V extends Vao> void create(V vao) {
        ((IndexBufferVao) vao).createIndexBuffer(this.data);
    }
}