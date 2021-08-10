package util.parsing.colladaParser.dataStructures;

import renderEngine.MeshData;

public class AnimatedModelData {

    private final SkeletonData joints;
    private final MeshData meshData;

    public AnimatedModelData(MeshData meshData, SkeletonData joints) {
        this.joints = joints;
        this.meshData = meshData;
    }

    public SkeletonData getJointsData() {
        return this.joints;
    }

    public MeshData getMeshData() {
        return this.meshData;
    }
}
