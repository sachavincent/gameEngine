package renderEngine.structures;

import java.util.Collection;
import renderEngine.shaders.structs.Material;

public class MaterialIndicesAttribute extends IndicesAttribute {

    private final Material material;

    public MaterialIndicesAttribute(Material material, Integer[] data) {
        super(data);
        this.material = material;
    }

    public MaterialIndicesAttribute(Material material, Collection<Integer> data) {
        super(data);
        this.material = material;
    }

    @Override
    public <V extends Vao> void create(V vao) {
        ((IndexBufferVao) vao).createIndexBuffer(this.material, this.data);
    }
}