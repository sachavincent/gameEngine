package renderEngine;

import animation.Joint;
import models.AnimatedModel;
import renderEngine.shaders.structs.Material;
import util.parsing.ModelType;
import util.parsing.colladaParser.colladaLoader.ColladaLoader;
import util.parsing.colladaParser.dataStructures.AnimatedModelData;
import util.parsing.colladaParser.dataStructures.JointData;
import util.parsing.colladaParser.dataStructures.SkeletonData;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class AnimatedModelLoader {

    /**
     * Creates an AnimatedModel from the data in an entity file. It loads up
     * the collada model data, stores the extracted data in a VAO, sets up the
     * joint heirarchy, and loads up the entity's texture.
     *
     * @return The animated model (no animation applied though)
     */
    public static AnimatedModel loadAnimatedModel(File colladaFile, ModelType modelType) {
        AnimatedModelData modelData = ColladaLoader.loadColladaModel(colladaFile, 3);
        Vao modelVao = Vao.createVao(modelData.getMeshData(), modelType);
        SkeletonData skeletonData = modelData.getJointsData();
        Joint headJoint = createJoints(skeletonData.headJoint);
        List<Material> materials = new ArrayList<>(modelData.getMeshData().getIndicesList().keySet());
        return new AnimatedModel(modelVao, headJoint, skeletonData.jointCount, materials);
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
