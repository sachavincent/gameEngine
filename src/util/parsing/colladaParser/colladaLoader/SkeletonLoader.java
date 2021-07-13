package util.parsing.colladaParser.colladaLoader;

import java.nio.FloatBuffer;
import java.util.List;
import org.lwjgl.BufferUtils;
import util.math.Matrix4f;
import util.math.Vector3f;
import util.parsing.colladaParser.dataStructures.JointData;
import util.parsing.colladaParser.dataStructures.SkeletonData;
import util.parsing.colladaParser.xmlParser.XmlNode;

public class SkeletonLoader {

    private XmlNode armatureData;

    private List<String> boneOrder;

    private int jointCount = 0;

    private static final Matrix4f CORRECTION = new Matrix4f()
            .rotate((float) Math.toRadians(-90), new Vector3f(1, 0, 0));

    public SkeletonLoader(XmlNode visualSceneNode, List<String> boneOrder) {
        this.armatureData = visualSceneNode.getChild("visual_scene").getChildWithAttribute("node", "id", "Armature");
        this.boneOrder = boneOrder;
    }

    public SkeletonData extractBoneData() {
        XmlNode headNode = armatureData.getChild("node");
        JointData headJoint = loadJointData(headNode, true);
        return new SkeletonData(jointCount, headJoint);
    }

    private JointData loadJointData(XmlNode jointNode, boolean isRoot) {
        JointData joint = extractMainJointData(jointNode, isRoot);
        for (XmlNode childNode : jointNode.getChildren("node")) {
            joint.addChild(loadJointData(childNode, false));
        }
        return joint;
    }

    private JointData extractMainJointData(XmlNode jointNode, boolean isRoot) {
        String sid = jointNode.getAttribute("sid");
        String id = jointNode.getAttribute("id");
        if (!boneOrder.contains(sid))
            throw new IllegalStateException("Error in collada file");

        int index = boneOrder.indexOf(sid);
        String[] matrixData = jointNode.getChild("matrix").getData().split(" ");
        Matrix4f matrix = new Matrix4f();
        matrix.load(convertData(matrixData));
        matrix.transpose();
        if (isRoot) {
            //because in Blender z is up, but in our game y is up.
            Matrix4f.mul(CORRECTION, matrix, matrix);
        }
        jointCount++;
        return new JointData(index, id, matrix);
    }

    private FloatBuffer convertData(String[] rawData) {
        float[] matrixData = new float[16];
        for (int i = 0; i < matrixData.length; i++) {
            matrixData[i] = Float.parseFloat(rawData[i]);
        }
        FloatBuffer buffer = BufferUtils.createFloatBuffer(16);
        buffer.put(matrixData);
        buffer.flip();
        return buffer;
    }

}
