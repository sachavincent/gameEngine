package renderEngine.structures;

import java.util.ArrayList;
import java.util.List;
import renderEngine.shaders.structs.Material;

public class IndexBufferVao extends Vao {

    private final List<IndexVbo> indexVbos;

    public IndexBufferVao() {
        super();
        this.indexVbos = new ArrayList<>();
    }

    public List<IndexVbo> getIndexVbos() {
        return this.indexVbos;
    }

    public void createIndexBuffer(Material material, Integer[] indices) {
        if (indices.length == 0)
            return;

        MaterialIndexVbo indexVbo = new MaterialIndexVbo(material);
        indexVbo.bind();
        indexVbo.storeData(indices);
        indexVbo.unbind();
        this.indexVbos.add(indexVbo);
    }

    public void createIndexBuffer(Integer[] indices) {
        if (indices.length == 0)
            return;

        IndexVbo indexVbo = new IndexVbo();
        indexVbo.bind();
        indexVbo.storeData(indices);
        indexVbo.unbind();
        this.indexVbos.add(indexVbo);
    }

    @Override
    public void delete() {
        super.delete();

        this.indexVbos.forEach(Vbo::delete);
    }
}
