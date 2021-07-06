package renderEngine;

import animation.Joint;
import java.io.File;
import models.AnimatedTexturedModel;
import textures.ModelTexture;
import util.ModelType;
import util.Vao;
import util.colladaParser.colladaLoader.ColladaLoader;
import util.colladaParser.dataStructures.AnimatedModelData;
import util.colladaParser.dataStructures.JointData;
import util.colladaParser.dataStructures.MeshData;
import util.colladaParser.dataStructures.SkeletonData;

public class AnimatedModelLoader {

    /**
     * Creates an AnimatedEntity from the data in an entity file. It loads up
     * the collada model data, stores the extracted data in a VAO, sets up the
     * joint heirarchy, and loads up the entity's texture.
     *
     * @return The animated entity (no animation applied though)
     */
    public static AnimatedTexturedModel loadEntity(String model, String texture, ModelType modelType) {
        File modelFile = new File("res/" + model);
        File textureFile = new File("res/" + texture);
        AnimatedModelData entityData = ColladaLoader.loadColladaModel(modelFile, 3);
        MeshData meshData = entityData.getMeshData();
        Vao modelVao = Vao.createVao(entityData.getMeshData(), modelType);
        SkeletonData skeletonData = entityData.getJointsData();
        Joint headJoint = createJoints(skeletonData.headJoint);
        AnimatedTexturedModel animatedTexturedModel = new AnimatedTexturedModel(modelVao,
                new ModelTexture(textureFile.getName()), headJoint, skeletonData.jointCount);
        animatedTexturedModel.setIndicesLength(meshData.getIndices().length);
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
