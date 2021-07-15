package renderEngine;

import animation.Joint;
import java.io.File;
import models.AnimatedModel;
import textures.ModelTexture;
import util.Vao;
import util.parsing.ModelType;
import util.parsing.colladaParser.colladaLoader.ColladaLoader;
import util.parsing.colladaParser.dataStructures.AnimatedModelData;
import util.parsing.colladaParser.dataStructures.JointData;
import util.parsing.colladaParser.dataStructures.MeshData;
import util.parsing.colladaParser.dataStructures.SkeletonData;

public class AnimatedModelLoader {

    /**
     * Creates an AnimatedEntity from the data in an entity file. It loads up
     * the collada model data, stores the extracted data in a VAO, sets up the
     * joint heirarchy, and loads up the entity's texture.
     *
     * @return The animated entity (no animation applied though)
     */
    public static AnimatedModel loadEntity(File modelFile, File textureFile, ModelType modelType) {
        AnimatedModelData entityData = ColladaLoader.loadColladaModel(modelFile, 3);
        MeshData meshData = entityData.getMeshData();
        Vao modelVao = Vao.createVao(entityData.getMeshData(), modelType);
        SkeletonData skeletonData = entityData.getJointsData();
        Joint headJoint = createJoints(skeletonData.headJoint);
        AnimatedModel animatedTexturedModel = new AnimatedModel(modelVao, ModelTexture.createTexture(textureFile),
                headJoint, skeletonData.jointCount);
//        animatedTexturedModel.setIndicesLength(meshData.getMaterials().stream().findFirst()
//                .get().length);//TODO: Definitely wrong if multiple materials...
        return animatedTexturedModel;
    }


    /**
     * Constructs the joint-hierarchy skeleton from the data extracted from the
     * collada file.
     *
     * @param data - the joints data from the collada file for the head joint.
     * @return The created joint, with all its descendants added.
     */
    private static Joint createJoints(JointData data) {
        Joint joint = new Joint(data.getIndex(), data.getId(), data.getBindLocalTransform());
        for (JointData child : data.getChildren()) {
            joint.addChild(createJoints(child));
        }
        return joint;
    }
}
