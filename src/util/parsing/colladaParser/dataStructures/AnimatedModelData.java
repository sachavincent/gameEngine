package util.parsing.colladaParser.dataStructures;

import java.util.List;
import renderEngine.shaders.structs.Material;
import renderEngine.structures.IndexData;

public class AnimatedModelData {

    private final SkeletonData   joints;
    private final IndexData      meshData;
    private final       List<Material> materials;

    public AnimatedModelData(IndexData meshData, SkeletonData joints, List<Material> materials) {
        this.joints = joints;
        this.meshData = meshData;
        this.materials = materials;
    }

    public List<Material> getMaterials() {
        return this.materials;
    }

    public SkeletonData getJointsData() {
        return this.joints;
    }

    public IndexData getMeshData() {
        return this.meshData;
    }
}
