package renderEngine.structures;

import renderEngine.shaders.structs.Material;

public class MaterialIndexVbo extends IndexVbo {

    private final Material material;

    public MaterialIndexVbo(Material material) {
        super();
        this.material = material;
    }

    public Material getMaterial() {
        return this.material;
    }
}